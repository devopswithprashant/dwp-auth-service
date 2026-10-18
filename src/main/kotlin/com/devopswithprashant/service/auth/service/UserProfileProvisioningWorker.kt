package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.config.UserProfileProperties
import com.devopswithprashant.service.auth.service.impl.ProfileProvisioningResponseMismatchException
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Component
class UserProfileProvisioningWorker(
    private val provisioningService: UserProfileProvisioningService,
    private val provisioner: UserProfileProvisioner,
    private val properties: UserProfileProperties
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelayString = "\${user-profile.poll-interval:1s}")
    fun processNext() {
        val now = Instant.now()
        val claim = provisioningService.claimNext(now, properties.leaseDuration) ?: return

        if (claim.attemptCount > properties.retryMaxAttempts) {
            provisioningService.markFailed(claim.userId, ATTEMPT_LIMIT_EXCEEDED)
            logger.error("Profile provisioning attempt limit exceeded for userId={}", claim.userId)
            return
        }

        try {
            val provisionedId = provisioner.createDefaultProfile(claim.userId)
            if (provisionedId != claim.userId) {
                provisioningService.markFailed(claim.userId, RESPONSE_USER_ID_MISMATCH)
                logger.error("Profile provisioning response userId mismatch for userId={}", claim.userId)
                return
            }

            provisioningService.markProvisioned(claim.userId)
            logger.info("User profile provisioned for userId={}", claim.userId)
        } catch (exception: StatusRuntimeException) {
            handleGrpcFailure(claim, exception.status.code)
        } catch (exception: ProfileProvisioningResponseMismatchException) {
            provisioningService.markFailed(claim.userId, RESPONSE_USER_ID_MISMATCH)
            logger.error("Profile provisioning response userId mismatch for userId={}", claim.userId)
        }
    }

    private fun handleGrpcFailure(claim: ProfileProvisioningClaim, code: Status.Code) {
        val statusCode = code.name
        if (code in RETRYABLE_CODES && claim.attemptCount < properties.retryMaxAttempts) {
            val retryAt = Instant.now().plus(backoffFor(claim.attemptCount))
            provisioningService.scheduleRetry(claim.userId, retryAt, statusCode)
            logger.warn(
                "Profile provisioning will retry for userId={} status={} attempt={} retryAt={}",
                claim.userId,
                statusCode,
                claim.attemptCount,
                retryAt
            )
            return
        }

        provisioningService.markFailed(claim.userId, statusCode)
        logger.error(
            "Profile provisioning failed permanently for userId={} status={} attempt={}",
            claim.userId,
            statusCode,
            claim.attemptCount
        )
    }

    private fun backoffFor(attemptCount: Int): Duration {
        var delay = properties.retryInitialBackoff
        repeat((attemptCount - 1).coerceAtLeast(0)) {
            delay = if (delay >= properties.retryMaxBackoff.minus(delay)) {
                properties.retryMaxBackoff
            } else {
                delay.multipliedBy(2)
            }
        }
        return minOf(delay, properties.retryMaxBackoff)
    }

    companion object {
        private const val RESPONSE_USER_ID_MISMATCH = "RESPONSE_USER_ID_MISMATCH"
        private const val ATTEMPT_LIMIT_EXCEEDED = "ATTEMPT_LIMIT_EXCEEDED"
        private val RETRYABLE_CODES = setOf(Status.Code.UNAVAILABLE, Status.Code.DEADLINE_EXCEEDED)
    }
}
