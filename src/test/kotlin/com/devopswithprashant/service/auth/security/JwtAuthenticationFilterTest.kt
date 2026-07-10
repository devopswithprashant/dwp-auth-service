package com.devopswithprashant.service.auth.security

import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.entity.UserCredential
import com.devopswithprashant.service.auth.security.jwt.JwtAuthenticationFilter
import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetails
import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetailsService
import com.devopswithprashant.service.auth.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class JwtAuthenticationFilterTest {

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var authenticatedUserDetailsService: AuthenticatedUserDetailsService

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var filterChain: FilterChain

    @InjectMocks
    private lateinit var filter: JwtAuthenticationFilter

    @Test
    fun `filter skips requests without bearer token`() {
        `when`(request.getHeader("Authorization")).thenReturn(null)

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `filter sets authentication for valid token`() {
        val userId = UUID.randomUUID()
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "hash")
        user.assignRole(Role(name = RoleType.ROLE_USER))
        val userDetails = AuthenticatedUserDetails(user)

        `when`(request.getHeader("Authorization")).thenReturn("Bearer valid-token")
        `when`(tokenService.validateToken("valid-token")).thenReturn(true)
        `when`(tokenService.extractSubject("valid-token")).thenReturn(userId.toString())
        `when`(authenticatedUserDetailsService.loadUserById(userId)).thenReturn(userDetails)

        filter.doFilter(request, response, filterChain)

        assertTrue(SecurityContextHolder.getContext().authentication != null)
    }
}
