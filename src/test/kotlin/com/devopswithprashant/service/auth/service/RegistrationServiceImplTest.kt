package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.common.exception.AuthErrorCode
import com.devopswithprashant.service.auth.common.exception.BusinessException
import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.dto.response.RegisterResponse
import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.entity.UserCredential
import com.devopswithprashant.service.auth.mapper.UserCredentialMapper
import com.devopswithprashant.service.auth.repository.RoleRepository
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import com.devopswithprashant.service.auth.service.impl.RegistrationServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class RegistrationServiceImplTest {

    @Mock
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var mapper: UserCredentialMapper

    @InjectMocks
    private lateinit var registrationService: RegistrationServiceImpl

    @Test
    fun `register creates user when request is valid`() {
        val request = RegisterRequest("alice", "alice@example.com", "Password123!")
        val encodedPassword = "encoded"
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = encodedPassword)
        val role = Role(name = RoleType.ROLE_USER)

        `when`(userCredentialRepository.existsByUsername("alice")).thenReturn(false)
        `when`(userCredentialRepository.existsByEmail("alice@example.com")).thenReturn(false)
        `when`(passwordEncoder.encode("Password123!")).thenReturn(encodedPassword)
        `when`(mapper.toEntity(request, encodedPassword)).thenReturn(user)
        `when`(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(role)
        `when`(userCredentialRepository.save(any<UserCredential>())).thenReturn(user)
        `when`(mapper.toRegisterResponse(user)).thenReturn(RegisterResponse(user.id, "alice", "alice@example.com", false))

        val response = registrationService.register(request)

        assertEquals("alice", response.username)
        assertEquals("alice@example.com", response.email)
    }

    @Test
    fun `register throws when username already exists`() {
        val request = RegisterRequest("alice", "alice@example.com", "Password123!")

        `when`(userCredentialRepository.existsByUsername("alice")).thenReturn(true)

        val exception = assertThrows(BusinessException::class.java) {
            registrationService.register(request)
        }

        assertEquals(AuthErrorCode.USERNAME_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `register throws when email already exists`() {
        val request = RegisterRequest("alice", "alice@example.com", "Password123!")

        `when`(userCredentialRepository.existsByUsername("alice")).thenReturn(false)
        `when`(userCredentialRepository.existsByEmail("alice@example.com")).thenReturn(true)

        val exception = assertThrows(BusinessException::class.java) {
            registrationService.register(request)
        }

        assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS, exception.errorCode)
    }
}
