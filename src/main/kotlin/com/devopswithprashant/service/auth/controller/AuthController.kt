package com.devopswithprashant.service.auth.controller

import com.devopswithprashant.service.auth.common.api.ApiSuccessResponse
import com.devopswithprashant.service.auth.dto.request.LoginRequest
import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.LoginResponse
import com.devopswithprashant.service.auth.dto.response.RegisterResponse
import com.devopswithprashant.service.auth.service.AuthenticationService
import com.devopswithprashant.service.auth.service.RegistrationService
import com.devopswithprashant.service.auth.service.TokenService
import com.devopswithprashant.service.auth.dto.response.CurrentUserResponse
import com.devopswithprashant.service.auth.mapper.CurrentUserMapper
import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(

    private val registrationService: RegistrationService,
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService,
    private val currentUserMapper: CurrentUserMapper

) {

    @PostMapping("/register")
    fun register(
        @Valid
        @RequestBody
        request: RegisterRequest
    ): ResponseEntity<ApiSuccessResponse<RegisterResponse>> {

        val response = registrationService.register(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ApiSuccessResponse(
                    data = response
                )
            )
    }

    @PostMapping("/login")
    fun login(
        @Valid
        @RequestBody
        request: LoginRequest
    ): ResponseEntity<ApiSuccessResponse<LoginResponse>> {

        val authenticatedUser =
            authenticationService.authenticate(
                request.identifier,
                request.password
            )

        val token =
            tokenService.generateAccessToken(authenticatedUser)

        val response =
            LoginResponse(
                token = token
            )

        return ResponseEntity.ok(
            ApiSuccessResponse(
                data = response
            )
        )
    }


    @GetMapping("/me")
    fun currentUser(

        @AuthenticationPrincipal
        principal: AuthenticatedUserDetails

    ): ResponseEntity<ApiSuccessResponse<CurrentUserResponse>> {

        return ResponseEntity.ok(

            ApiSuccessResponse(

                data = currentUserMapper.toResponse(

                    principal

                )

            )

        )
    }
}