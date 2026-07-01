package com.devopswithprashant.service.auth.service.impl

import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.RegisterResponse
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.exception.AuthErrorCode
import com.devopswithprashant.service.auth.common.exception.BusinessException
import com.devopswithprashant.service.auth.mapper.UserCredentialMapper
import com.devopswithprashant.service.auth.repository.RoleRepository
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.service.RegistrationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegistrationServiceImpl(

    private val userCredentialRepository: UserCredentialRepository,

    private val roleRepository: RoleRepository,

    private val passwordEncoder: PasswordEncoder,

    private val mapper: UserCredentialMapper

) : RegistrationService {

    override fun register(
        request: RegisterRequest
    ): RegisterResponse {

        if (userCredentialRepository.existsByUsername(request.username)) {
            throw BusinessException(
                AuthErrorCode.USERNAME_ALREADY_EXISTS
            )
        }

        if (userCredentialRepository.existsByEmail(request.email)) {
            throw BusinessException(
                AuthErrorCode.EMAIL_ALREADY_EXISTS
            )
        }

        val encodedPassword =
            passwordEncoder.encode(request.password)

        val user =
            mapper.toEntity(
                request,
                encodedPassword
            )

        val defaultRole =
            roleRepository.findByName(RoleType.ROLE_USER)
                ?: throw BusinessException(
                    AuthErrorCode.ROLE_NOT_FOUND
                )

        user.assignRole(defaultRole)

        val savedUser =
            userCredentialRepository.save(user)

        return mapper.toRegisterResponse(savedUser)
    }
}