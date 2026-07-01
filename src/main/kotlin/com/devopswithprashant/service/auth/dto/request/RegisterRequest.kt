package com.devopswithprashant.service.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(

    @field:NotBlank(message = "Username is required.")
    @field:Size(
        min = 3,
        max = 50,
        message = "Username must be between 3 and 50 characters."
    )
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "Username can contain only letters, numbers, dots, underscores and hyphens."
    )
    val username: String,

    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Invalid email address.")
    val email: String,

    @field:NotBlank(message = "Password is required.")
    @field:Size(
        min = 8,
        max = 100,
        message = "Password must be between 8 and 100 characters."
    )
    val password: String
)