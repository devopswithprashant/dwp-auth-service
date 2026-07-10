package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.common.exception.AuthErrorCode
import com.devopswithprashant.service.auth.common.exception.BusinessException
import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.entity.UserCredential
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.service.impl.AuthenticationServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthenticationServiceImplTest {

    @Mock
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var authenticationService: AuthenticationServiceImpl

    @Test
    fun `authenticate returns authenticated user for valid credentials`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "encoded")
        user.assignRole(Role(name = RoleType.ROLE_USER))

        `when`(userCredentialRepository.findByIdentifierWithRoles("alice")).thenReturn(user)
        `when`(passwordEncoder.matches("Password123!", "encoded")).thenReturn(true)

        val result = authenticationService.authenticate("alice", "Password123!")

        assertEquals("alice", result.username)
        assertEquals("alice@example.com", result.email)
        assertEquals(setOf(RoleType.ROLE_USER), result.roles)
    }

    @Test
    fun `authenticate throws when user not found`() {
        `when`(userCredentialRepository.findByIdentifierWithRoles("missing")).thenReturn(null)

        val exception = assertThrows(BusinessException::class.java) {
            authenticationService.authenticate("missing", "Password123!")
        }

        assertEquals(AuthErrorCode.INVALID_CREDENTIALS, exception.errorCode)
    }

    @Test
    fun `authenticate throws when account is disabled`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "encoded")
        user.disable()

        `when`(userCredentialRepository.findByIdentifierWithRoles("alice")).thenReturn(user)

        val exception = assertThrows(BusinessException::class.java) {
            authenticationService.authenticate("alice", "Password123!")
        }

        assertEquals(AuthErrorCode.ACCOUNT_DISABLED, exception.errorCode)
    }

    @Test
    fun `authenticate throws when account is locked`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "encoded")
        user.lockAccount()

        `when`(userCredentialRepository.findByIdentifierWithRoles("alice")).thenReturn(user)

        val exception = assertThrows(BusinessException::class.java) {
            authenticationService.authenticate("alice", "Password123!")
        }

        assertEquals(AuthErrorCode.ACCOUNT_LOCKED, exception.errorCode)
    }
}
