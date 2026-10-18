package com.devopswithprashant.service.auth.service.impl

import com.devopswithprashant.service.auth.config.UserProfileProperties
import com.devopswithprashant.service.auth.service.UserProfileProvisioner
import com.devopswithprashant.user.v1.CreateDefaultProfileRequest
import com.devopswithprashant.user.v1.UserProfileServiceGrpc
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class GrpcUserProfileProvisioner(
    private val stub: UserProfileServiceGrpc.UserProfileServiceBlockingStub,
    private val properties: UserProfileProperties
) : UserProfileProvisioner {

    override fun createDefaultProfile(userId: UUID): UUID {
        val response = stub
            .withDeadlineAfter(properties.deadline.toMillis(), TimeUnit.MILLISECONDS)
            .createDefaultProfile(
                CreateDefaultProfileRequest.newBuilder()
                    .setUserId(userId.toString())
                    .build()
            )

        val expectedId = userId.toString()
        if (!response.userId.equals(expectedId, ignoreCase = true)) {
            throw ProfileProvisioningResponseMismatchException()
        }

        return userId
    }
}

class ProfileProvisioningResponseMismatchException :
    RuntimeException("User Service returned a different user_id")
