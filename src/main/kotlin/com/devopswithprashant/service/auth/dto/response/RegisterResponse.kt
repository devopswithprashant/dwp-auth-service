package com.devopswithprashant.service.auth.dto.response

import java.util.UUID

data class RegisterResponse(

    val id: UUID,

    val username: String,

    val email: String,

    val emailVerified: Boolean
)