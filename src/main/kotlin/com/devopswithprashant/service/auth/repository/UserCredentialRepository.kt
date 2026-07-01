package com.devopswithprashant.service.auth.repository

import com.devopswithprashant.auth.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserCredentialRepository : JpaRepository<UserCredential, UUID> {

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByUsername(username: String): UserCredential?

    fun findByEmail(email: String): UserCredential?

    fun findByUsernameOrEmail(
        username: String,
        email: String
    ): UserCredential?
}