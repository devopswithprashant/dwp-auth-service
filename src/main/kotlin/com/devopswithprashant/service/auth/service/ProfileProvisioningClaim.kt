package com.devopswithprashant.service.auth.service

import java.util.UUID

data class ProfileProvisioningClaim(
    val userId: UUID,
    val attemptCount: Int
)
