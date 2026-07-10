package com.devopswithprashant.service.auth.mapper

import com.devopswithprashant.service.auth.dto.request.RegisterRequest
import com.devopswithprashant.service.auth.entity.UserCredential
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class UserCredentialMapperTest {

    private val mapper = UserCredentialMapper()

    @Test
    fun `toEntity maps register request to user credential`() {
        val request = RegisterRequest(
            username = "alice",
            email = "alice@example.com",
            password = "Password123!"
        )

        val entity = mapper.toEntity(request, "encoded-password")

        assertEquals("alice", entity.username)
        assertEquals("alice@example.com", entity.email)
        assertEquals("encoded-password", entity.passwordHash)
        assertFalse(entity.emailVerified)
    }

    @Test
    fun `toRegisterResponse converts entity to response`() {
        val user = UserCredential(
            username = "bob",
            email = "bob@example.com",
            passwordHash = "hash"
        ).apply {
            verifyEmail()
        }

        val response = mapper.toRegisterResponse(user)

        assertEquals(user.id, response.id)
        assertEquals("bob", response.username)
        assertEquals("bob@example.com", response.email)
        assertEquals(true, response.emailVerified)
    }
}
