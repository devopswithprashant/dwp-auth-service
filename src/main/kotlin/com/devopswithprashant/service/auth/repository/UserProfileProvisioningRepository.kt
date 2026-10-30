package com.devopswithprashant.service.auth.repository

import com.devopswithprashant.service.auth.entity.UserProfileProvisioning
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface UserProfileProvisioningRepository : JpaRepository<UserProfileProvisioning, UUID> {

    @Query(
        value = """
            SELECT *
            FROM user_profile_provisioning
            WHERE (status = 'PENDING' AND next_attempt_at <= :now)
               OR (status = 'PROCESSING' AND lease_expires_at <= :now)
            ORDER BY next_attempt_at, user_id
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        """,
        nativeQuery = true
    )
    fun findNextClaimable(
        @Param("now") now: Instant
    ): UserProfileProvisioning?
}
