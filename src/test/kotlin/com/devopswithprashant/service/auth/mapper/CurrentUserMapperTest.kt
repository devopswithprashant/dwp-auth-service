package com.devopswithprashant.service.auth.mapper

import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.entity.UserCredential
import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CurrentUserMapperTest {

    private val mapper = CurrentUserMapper()

    @Test
    fun `toResponse maps authenticated user details to current user response`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "hash")
        user.assignRole(Role(name = RoleType.ROLE_USER))
        val details = AuthenticatedUserDetails(user)

        val response = mapper.toResponse(details)

        assertEquals(user.id, response.id)
        assertEquals("alice", response.username)
        assertEquals("alice@example.com", response.email)
        assertEquals(setOf("ROLE_USER"), response.roles)
    }
}
