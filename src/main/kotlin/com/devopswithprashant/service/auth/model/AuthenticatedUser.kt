package com.devopswithprashant.service.auth.model

import com.devopswithprashant.service.auth.entity.RoleType
import java.util.UUID

data class AuthenticatedUser(

    val id: UUID,

    val username: String,

    val email: String,

    val roles: Set<RoleType>
)