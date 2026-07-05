package com.devopswithprashant.service.auth.dto.response

import java.util.UUID

data class CurrentUserResponse(

    val id: UUID,

    val username: String,

    val email: String,

    val roles: Set<String>

)