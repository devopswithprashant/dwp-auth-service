package com.devopswithprashant.service.auth.dto.response

data class TokenResponse(

    val accessToken: String,

    val refreshToken: String? = null,

    val tokenType: String = "Bearer",

    val expiresInSeconds: Long
)