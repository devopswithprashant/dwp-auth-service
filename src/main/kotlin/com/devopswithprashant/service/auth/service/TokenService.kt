package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.model.AuthenticatedUser
import com.devopswithprashant.service.auth.dto.response.TokenResponse

interface TokenService {

    fun generateToken(

        authenticatedUser: AuthenticatedUser

    ): TokenResponse
}