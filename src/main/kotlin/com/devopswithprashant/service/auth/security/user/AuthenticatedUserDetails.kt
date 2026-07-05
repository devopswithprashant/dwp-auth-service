package com.devopswithprashant.service.auth.security.user

import com.devopswithprashant.service.auth.entity.UserCredential
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class AuthenticatedUserDetails(

    private val userCredential: UserCredential

) : UserDetails {

    val id: UUID
        get() = userCredential.id

    val email: String
        get() = userCredential.email

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return userCredential.getRoles().map {
            SimpleGrantedAuthority(it.name.name)
        }
    }

    override fun getPassword(): String {
        return userCredential.passwordHash
    }

    /**
     * Spring Security requires a username.
     *
     * We support login using username OR email,
     * but internally username is our principal.
     */
    override fun getUsername(): String {
        return userCredential.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return userCredential.accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return userCredential.enabled
    }
}