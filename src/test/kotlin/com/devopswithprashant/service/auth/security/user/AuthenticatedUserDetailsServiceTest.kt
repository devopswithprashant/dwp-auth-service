package com.devopswithprashant.service.auth.security.user

import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.entity.UserCredential
import com.devopswithprashant.service.auth.repository.UserCredentialRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthenticatedUserDetailsServiceTest {

    @Mock
    private lateinit var userCredentialRepository: UserCredentialRepository

    @InjectMocks
    private lateinit var service: AuthenticatedUserDetailsService

    @Test
    fun `loadUserByUsername returns user details for existing user`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "hash")
        user.assignRole(Role(name = RoleType.ROLE_USER))
        `when`(userCredentialRepository.findByIdentifierWithRoles("alice")).thenReturn(user)

        val details = service.loadUserByUsername("alice")

        assertEquals("alice", details.username)
    }

    @Test
    fun `loadUserById throws when user is not found`() {
        val id = UUID.randomUUID()
        `when`(userCredentialRepository.findByIdWithRoles(id)).thenReturn(null)

        assertThrows(UsernameNotFoundException::class.java) {
            service.loadUserById(id)
        }
    }
}
