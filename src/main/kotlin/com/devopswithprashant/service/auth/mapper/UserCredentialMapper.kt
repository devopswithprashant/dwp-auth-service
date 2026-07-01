package com.devopswithprashant.service.auth.mapper

import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.RegisterResponse
import com.devopswithprashant.service.auth.entity.UserCredential
import org.springframework.stereotype.Component

@Component
class UserCredentialMapper {

    fun toEntity(
        request: RegisterRequest,
        encodedPassword: String
    ): UserCredential {

        return UserCredential(
            username = request.username,
            email = request.email,
            passwordHash = encodedPassword
        )
    }

    fun toRegisterResponse(
        user: UserCredential
    ): RegisterResponse {

        return RegisterResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            emailVerified = user.emailVerified
        )
    }
}