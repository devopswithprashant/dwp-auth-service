package com.devopswithprashant.service.auth.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "user_profile_provisioning")
class UserProfileProvisioning(

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ProfileProvisioningStatus = ProfileProvisioningStatus.PENDING,

    @Column(name = "attempt_count", nullable = false)
    var attemptCount: Int = 0,

    @Column(name = "next_attempt_at", nullable = false)
    var nextAttemptAt: Instant = Instant.now(),

    @Column(name = "lease_expires_at")
    var leaseExpiresAt: Instant? = null,

    @Column(name = "last_status_code", length = 64)
    var lastStatusCode: String? = null
) : BaseEntity() {

    fun claim(leaseExpiresAt: Instant) {
        status = ProfileProvisioningStatus.PROCESSING
        attemptCount += 1
        this.leaseExpiresAt = leaseExpiresAt
    }

    fun markProvisioned() {
        status = ProfileProvisioningStatus.PROVISIONED
        leaseExpiresAt = null
        lastStatusCode = null
    }

    fun scheduleRetry(nextAttemptAt: Instant, statusCode: String) {
        status = ProfileProvisioningStatus.PENDING
        this.nextAttemptAt = nextAttemptAt
        leaseExpiresAt = null
        lastStatusCode = statusCode
    }

    fun markFailed(statusCode: String) {
        status = ProfileProvisioningStatus.FAILED
        leaseExpiresAt = null
        lastStatusCode = statusCode
    }
}
