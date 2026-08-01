package com.devopswithprashant.service.auth.service.impl

import com.devopswithprashant.service.auth.common.exception.BusinessException
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.common.exception.AuthErrorCode
import com.devopswithprashant.service.auth.model.AuthenticatedUser
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.service.AuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthenticationServiceImpl(

    private val userCredentialRepository: UserCredentialRepository,

    private val passwordEncoder: PasswordEncoder

) : AuthenticationService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun authenticate(

        identifier: String,

        password: String

    ): AuthenticatedUser {

        logger.info("Authentication attempt for identifier={}", identifier)

        val user =
            userCredentialRepository.findByIdentifierWithRoles(identifier)
                ?: run {
                    logger.warn("Authentication failed: no matching user found for identifier={}", identifier)
                    throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
                }

        logger.debug("User record found for identifier={}, userId={}, enabled={}, accountNonLocked={}", identifier, user.id, user.enabled, user.accountNonLocked)

        if (!user.enabled) {
            logger.warn("Authentication failed: account disabled for userId={}", user.id)
            throw BusinessException(
                AuthErrorCode.ACCOUNT_DISABLED
            )
        }

        if (!user.accountNonLocked) {
            logger.warn("Authentication failed: account locked for userId={}", user.id)
            throw BusinessException(
                AuthErrorCode.ACCOUNT_LOCKED
            )
        }

        /*
        * TODO:
        * Re-enable this check once the Email Verification
        * feature is implemented.
        *
        * For the initial beta release (v0.X), users are allowed
        * to log in even if their email address has not yet
        * been verified.
        */

        // if (!user.emailVerified) {
        //
        //     throw BusinessException(
        //         AuthErrorCode.EMAIL_NOT_VERIFIED
        //     )
        //
        // }

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            logger.warn("Authentication failed: invalid password for userId={}", user.id)
            throw BusinessException(
                AuthErrorCode.INVALID_CREDENTIALS
            )
        }

        logger.info("Authentication succeeded for userId={}, username={}", user.id, user.username)

        return AuthenticatedUser(
            id = user.id,
            username = user.username,
            email = user.email,
            roles = user.getRoleTypes()
        )
    }
}