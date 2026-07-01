package com.devopswithprashant.service.auth.common.exception

import org.springframework.http.HttpStatus

enum class AuthErrorCode(

    val code: String,

    val message: String,

    val httpStatus: HttpStatus

) {

    USERNAME_ALREADY_EXISTS(
        "AUTH-001",
        "Username already exists.",
        HttpStatus.CONFLICT
    ),

    EMAIL_ALREADY_EXISTS(
        "AUTH-002",
        "Email already exists.",
        HttpStatus.CONFLICT
    ),

    INVALID_CREDENTIALS(
        "AUTH-003",
        "Invalid username/email or password.",
        HttpStatus.UNAUTHORIZED
    ),

    ACCOUNT_LOCKED(
        "AUTH-004",
        "Account is locked.",
        HttpStatus.FORBIDDEN
    ),

    EMAIL_NOT_VERIFIED(
        "AUTH-005",
        "Email address is not verified.",
        HttpStatus.FORBIDDEN
    ),

    ROLE_NOT_FOUND(
        "AUTH-006",
        "Default role is not configured.",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INTERNAL_SERVER_ERROR(
        "AUTH-999",
        "Unexpected server error.",
        HttpStatus.INTERNAL_SERVER_ERROR
    )
}