package com.devopswithprashant.service.auth.common.api

import java.time.Instant

data class ApiSuccessResponse<T>(

    val success: Boolean = true,

    val data: T,

    val timestamp: Instant = Instant.now()
)