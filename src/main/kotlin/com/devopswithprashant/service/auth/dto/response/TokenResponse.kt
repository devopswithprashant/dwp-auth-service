package com.devopswithprashant.service.auth.dto.response

data class TokenResponse(

    val accessToken: String,

    val refreshToken: String?,

    val tokenType: String,

    val expiresInSeconds: Long
)