package com.devopswithprashant.service.auth.security

import com.devopswithprashant.service.auth.config.JwtProperties
import com.devopswithprashant.service.auth.model.AuthenticatedUser
import com.devopswithprashant.service.auth.security.jwt.JwtTokenService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class JwtTokenServiceTest {

    private val jwtProperties = JwtProperties(
        secret = "0123456789abcdef0123456789abcdef",
        issuer = "auth-service",
        audience = "auth-clients",
        accessTokenValidity = 3600
    )

    private val jwtTokenService = JwtTokenService(jwtProperties)

    @Test
    fun `generateAccessToken creates a token with claims`() {
        val user = AuthenticatedUser(
            id = UUID.randomUUID(),
            username = "alice",
            email = "alice@example.com",
            roles = setOf(com.devopswithprashant.service.auth.entity.RoleType.ROLE_USER)
        )

        val tokenResponse = jwtTokenService.generateAccessToken(user)

        assertEquals(3600L, tokenResponse.expiresInSeconds)
        assertTrue(tokenResponse.accessToken.isNotBlank())
        assertEquals("alice", jwtTokenService.extractUsername(tokenResponse.accessToken))
        assertEquals(user.id.toString(), jwtTokenService.extractSubject(tokenResponse.accessToken))
    }

    @Test
    fun `validateToken returns false for invalid token`() {
        assertFalse(jwtTokenService.validateToken("not-a-valid-token"))
    }
}
