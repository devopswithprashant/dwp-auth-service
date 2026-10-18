package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.config.UserProfileProperties
import io.grpc.Status
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserProfileProvisioningWorkerTest {

    @Mock
    private lateinit var provisioningService: UserProfileProvisioningService

    @Mock
    private lateinit var provisioner: UserProfileProvisioner

    private val properties = UserProfileProperties(
        retryInitialBackoff = Duration.ofSeconds(2),
        retryMaxBackoff = Duration.ofSeconds(30),
        retryMaxAttempts = 3
    )

    private lateinit var worker: UserProfileProvisioningWorker

    @BeforeEach
    fun setUp() {
        worker = UserProfileProvisioningWorker(provisioningService, provisioner, properties)
    }

    @Test
    fun `successful provisioning marks job complete`() {
        val userId = UUID.randomUUID()
        whenever(provisioningService.claimNext(any(), eq(properties.leaseDuration)))
            .thenReturn(ProfileProvisioningClaim(userId, 1))
        whenever(provisioner.createDefaultProfile(userId)).thenReturn(userId)

        worker.processNext()

        verify(provisioningService).markProvisioned(userId)
    }

    @Test
    fun `unavailable service schedules bounded retry with same user id`() {
        val userId = UUID.randomUUID()
        whenever(provisioningService.claimNext(any(), eq(properties.leaseDuration)))
            .thenReturn(ProfileProvisioningClaim(userId, 1))
        whenever(provisioner.createDefaultProfile(userId))
            .thenThrow(Status.UNAVAILABLE.asRuntimeException())

        worker.processNext()

        verify(provisioningService).scheduleRetry(
            eq(userId),
            any<Instant>(),
            eq(Status.Code.UNAVAILABLE.name)
        )
        verify(provisioner).createDefaultProfile(userId)
    }

    @Test
    fun `unavailable service marks job failed when retry limit is reached`() {
        val userId = UUID.randomUUID()
        whenever(provisioningService.claimNext(any(), eq(properties.leaseDuration)))
            .thenReturn(ProfileProvisioningClaim(userId, properties.retryMaxAttempts))
        whenever(provisioner.createDefaultProfile(userId))
            .thenThrow(Status.UNAVAILABLE.asRuntimeException())

        worker.processNext()

        verify(provisioningService).markFailed(userId, Status.Code.UNAVAILABLE.name)
    }

    @Test
    fun `different response user id is marked failed`() {
        val userId = UUID.randomUUID()
        whenever(provisioningService.claimNext(any(), eq(properties.leaseDuration)))
            .thenReturn(ProfileProvisioningClaim(userId, 1))
        whenever(provisioner.createDefaultProfile(userId)).thenReturn(UUID.randomUUID())

        worker.processNext()

        verify(provisioningService).markFailed(userId, "RESPONSE_USER_ID_MISMATCH")
    }
}
