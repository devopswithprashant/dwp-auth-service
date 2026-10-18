package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.entity.UserProfileProvisioning
import com.devopswithprashant.service.auth.repository.UserProfileProvisioningRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class UserProfileProvisioningService(
    private val repository: UserProfileProvisioningRepository
) {

    @Transactional
    fun enqueue(userId: UUID) {
        repository.save(UserProfileProvisioning(userId = userId))
    }

    @Transactional
    fun claimNext(now: Instant, leaseDuration: java.time.Duration): ProfileProvisioningClaim? {
        val job = repository.findNextClaimable(now) ?: return null
        job.claim(now.plus(leaseDuration))
        return ProfileProvisioningClaim(job.userId, job.attemptCount)
    }

    @Transactional
    fun markProvisioned(userId: UUID) {
        findJob(userId).markProvisioned()
    }

    @Transactional
    fun scheduleRetry(userId: UUID, nextAttemptAt: Instant, statusCode: String) {
        findJob(userId).scheduleRetry(nextAttemptAt, statusCode)
    }

    @Transactional
    fun markFailed(userId: UUID, statusCode: String) {
        findJob(userId).markFailed(statusCode)
    }

    private fun findJob(userId: UUID): UserProfileProvisioning =
        repository.findById(userId).orElseThrow {
            IllegalStateException("Profile provisioning record not found for userId=$userId")
        }
}
