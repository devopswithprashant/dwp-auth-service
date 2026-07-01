package com.devopswithprashant.service.auth.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(

    @field:NotBlank(message = "Username or email is required.")
    @field:Size(
        min = 3,
        max = 255,
        message = "Username or email must be between 3 and 255 characters."
    )
    val identifier: String,

    @field:NotBlank(message = "Password is required.")
    @field:Size(
        min = 8,
        max = 100,
        message = "Password must be between 8 and 100 characters."
    )
    val password: String
)