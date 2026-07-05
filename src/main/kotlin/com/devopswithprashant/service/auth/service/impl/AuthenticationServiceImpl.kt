package com.devopswithprashant.service.auth.service.impl

import com.devopswithprashant.service.auth.common.exception.BusinessException
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.common.exception.AuthErrorCode
import com.devopswithprashant.service.auth.model.AuthenticatedUser
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.service.AuthenticationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthenticationServiceImpl(

    private val userCredentialRepository: UserCredentialRepository,

    private val passwordEncoder: PasswordEncoder

) : AuthenticationService {

    override fun authenticate(

        identifier: String,

        password: String

    ): AuthenticatedUser {

        val user =
    userCredentialRepository.findByIdentifierWithRoles(identifier)
                ?: throw BusinessException(
                    AuthErrorCode.INVALID_CREDENTIALS
                )

        if (!user.enabled) {
            throw BusinessException(
                AuthErrorCode.ACCOUNT_DISABLED
            )
        }

        if (!user.accountNonLocked) {
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
            throw BusinessException(
                AuthErrorCode.INVALID_CREDENTIALS
            )
        }

        return AuthenticatedUser(
            id = user.id,
            username = user.username,
            email = user.email,
            roles = user.getRoleTypes()
        )
    }
}