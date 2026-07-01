package com.devopswithprashant.service.auth.common.api

import java.time.Instant

data class ApiErrorResponse(

    val success: Boolean = false,

    val code: String,

    val message: String,

    val timestamp: Instant = Instant.now()
)