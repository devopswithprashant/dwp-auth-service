package com.devopswithprashant.service.auth.repository

import com.devopswithprashant.service.auth.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserCredentialRepository : JpaRepository<UserCredential, UUID> {

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByUsername(username: String): UserCredential?

    fun findByEmail(email: String): UserCredential?

    @Query(
        """
        SELECT uc
        FROM UserCredential uc
        WHERE uc.username = :identifier
           OR uc.email = :identifier
        """
    )
    fun findByIdentifier(
        @Param("identifier")
        identifier: String
    ): UserCredential?
}