package com.devopswithprashant.service.auth.security.user

import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthenticatedUserDetailsService(

    private val userCredentialRepository: UserCredentialRepository

) : UserDetailsService {

    /**
     * Used by Spring Security when authentication
     * is username based.
     *
     * Loads the user together with roles.
     */
    override fun loadUserByUsername(

        username: String

    ): UserDetails {

        val credential =
            userCredentialRepository.findByIdentifierWithRoles(
                username
            )
                ?: throw UsernameNotFoundException(
                    "User not found."
                )

        return AuthenticatedUserDetails(
            credential
        )
    }

    /**
     * Used by JWT authentication.
     *
     * JWT subject (sub) contains the immutable UUID.
     */
    fun loadUserById(

        id: UUID

    ): AuthenticatedUserDetails {

        val credential =
            userCredentialRepository.findByIdWithRoles(
                id
            )
                ?: throw UsernameNotFoundException(
                    "User not found."
                )

        return AuthenticatedUserDetails(
            credential
        )
    }
}