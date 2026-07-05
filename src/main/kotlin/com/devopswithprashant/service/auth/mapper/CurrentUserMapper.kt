package com.devopswithprashant.service.auth.mapper

import com.devopswithprashant.service.auth.dto.response.CurrentUserResponse
import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetails
import org.springframework.stereotype.Component

@Component
class CurrentUserMapper {

    fun toResponse(

        user: AuthenticatedUserDetails

    ): CurrentUserResponse {

        return CurrentUserResponse(

            id = user.id,

            username = user.username,

            email = user.email,

            roles = user.authorities
                .map { it.authority }
                .toSet()

        )
    }
}