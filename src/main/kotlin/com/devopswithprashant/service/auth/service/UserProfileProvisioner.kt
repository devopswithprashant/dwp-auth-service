package com.devopswithprashant.service.auth.service

import java.util.UUID

interface UserProfileProvisioner {
    fun createDefaultProfile(userId: UUID): UUID
}
