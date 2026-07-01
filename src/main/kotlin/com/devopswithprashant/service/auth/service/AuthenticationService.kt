package com.devopswithprashant.service.auth.service

import com.devopswithprashant.service.auth.model.AuthenticatedUser

interface AuthenticationService {

    fun authenticate(

        identifier: String,

        password: String

    ): AuthenticatedUser
}