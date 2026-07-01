package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.RegisterResponse

interface RegistrationService {

    fun register(
        request: RegisterRequest
    ): RegisterResponse
}