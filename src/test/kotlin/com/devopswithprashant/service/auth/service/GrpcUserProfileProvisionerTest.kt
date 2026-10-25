package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.config.ProfileServiceProperties
import com.devopswithprashant.service.auth.service.impl.GrpcUserProfileProvisioner
import com.devopswithprashant.service.auth.service.impl.ProfileProvisioningResponseMismatchException
import com.devopswithprashant.profile.v1.CreateDefaultProfileRequest
import com.devopswithprashant.profile.v1.CreateDefaultProfileResponse
import com.devopswithprashant.profile.v1.ProfileServiceGrpc
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class GrpcUserProfileProvisionerTest {

    @Mock
    private lateinit var stub: ProfileServiceGrpc.ProfileServiceBlockingStub

    private val properties = ProfileServiceProperties()
    private lateinit var provisioner: GrpcUserProfileProvisioner

    @BeforeEach
    fun setUp() {
        provisioner = GrpcUserProfileProvisioner(stub, properties)
        whenever(stub.withDeadlineAfter(properties.deadline.toMillis(), TimeUnit.MILLISECONDS))
            .thenReturn(stub)
    }

    @Test
    fun `sends canonical auth uuid with finite deadline and verifies response uuid`() {
        val userId = UUID.randomUUID()
        whenever(stub.createDefaultProfile(any<CreateDefaultProfileRequest>()))
            .thenReturn(
                CreateDefaultProfileResponse.newBuilder()
                    .setUserId(userId.toString())
                    .build()
            )

        assertEquals(userId, provisioner.createDefaultProfile(userId))

        verify(stub).createDefaultProfile(
            argThat { getUserId() == userId.toString() }
        )
        verify(stub).withDeadlineAfter(properties.deadline.toMillis(), TimeUnit.MILLISECONDS)
    }

    @Test
    fun `rejects response containing a different uuid`() {
        val userId = UUID.randomUUID()
        whenever(stub.createDefaultProfile(any<CreateDefaultProfileRequest>()))
            .thenReturn(
                CreateDefaultProfileResponse.newBuilder()
                    .setUserId(UUID.randomUUID().toString())
                    .build()
            )

        assertThrows(ProfileProvisioningResponseMismatchException::class.java) {
            provisioner.createDefaultProfile(userId)
        }
    }
}
