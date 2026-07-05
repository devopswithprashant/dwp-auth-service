package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.dto.response.TokenResponse
import com.devopswithprashant.service.auth.model.AuthenticatedUser

interface TokenService {

    fun generateAccessToken(
        authenticatedUser: AuthenticatedUser
    ): TokenResponse

    fun validateToken(
        token: String
    ): Boolean

    fun extractSubject(
        token: String
    ): String
    
}