package com.devopswithprashant.service.auth.security

import com.devopswithprashant.service.auth.entity.Role
import com.devopswithprashant.service.auth.entity.RoleType
import com.devopswithprashant.service.auth.entity.UserCredential
import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthenticatedUserDetailsTest {

    @Test
    fun `authenticated user details exposes security properties`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "hash")
        user.assignRole(Role(name = RoleType.ROLE_USER))
        val details = AuthenticatedUserDetails(user)

        assertEquals("alice", details.username)
        assertEquals("hash", details.password)
        assertTrue(details.isEnabled)
        assertTrue(details.isAccountNonExpired)
        assertTrue(details.isCredentialsNonExpired)
        assertTrue(details.isAccountNonLocked)
        assertEquals(setOf("ROLE_USER"), details.authorities.map { it.authority }.toSet())
    }

    @Test
    fun `authenticated user details reflects locked and disabled states`() {
        val user = UserCredential(username = "alice", email = "alice@example.com", passwordHash = "hash")
        user.lockAccount()
        user.disable()
        val details = AuthenticatedUserDetails(user)

        assertFalse(details.isAccountNonLocked)
        assertFalse(details.isEnabled)
    }
}
