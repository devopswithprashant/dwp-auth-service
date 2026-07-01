package com.devopswithprashant.service.auth.controller

import com.devopswithprashant.service.auth.common.api.ApiSuccessResponse
import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.RegisterResponse
import com.devopswithprashant.service.auth.service.RegistrationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class RegistrationController(

    private val registrationService: RegistrationService

) {

    @PostMapping("/register")
    fun register(

        @Valid
        @RequestBody
        request: RegisterRequest

    ): ResponseEntity<ApiSuccessResponse<RegisterResponse>> {

        val response =
            registrationService.register(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ApiSuccessResponse(
                    data = response
                )
            )
    }
}