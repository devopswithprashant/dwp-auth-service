package com.devopswithprashant.service.auth.service.impl

import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.RegisterResponse
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.common.exception.AuthErrorCode
import com.devopswithprashant.service.auth.common.exception.BusinessException
import com.devopswithprashant.service.auth.mapper.UserCredentialMapper
import com.devopswithprashant.service.auth.repository.RoleRepository
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.service.RegistrationService
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun register(
        request: RegisterRequest
    ): RegisterResponse {

        logger.info("Registration attempt for username={} email={}", request.username, request.email)

        if (userCredentialRepository.existsByUsername(request.username)) {
            logger.warn("Registration failed: username already exists for username={}", request.username)
            throw BusinessException(
                AuthErrorCode.USERNAME_ALREADY_EXISTS
            )
        }

        if (userCredentialRepository.existsByEmail(request.email)) {
            logger.warn("Registration failed: email already exists for email={}", request.email)
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
                ?: run {
                    logger.error("Registration failed: default role {} is missing", RoleType.ROLE_USER)
                    throw BusinessException(AuthErrorCode.ROLE_NOT_FOUND)
                }

        user.assignRole(defaultRole)

        val savedUser =
            userCredentialRepository.save(user)

        logger.info("Registration succeeded for userId={}, username={}", savedUser.id, savedUser.username)

        return mapper.toRegisterResponse(savedUser)
    }
}