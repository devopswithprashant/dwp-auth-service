package com.devopswithprashant.service.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(

    var secret: String = "",

    var issuer: String = "",

    var audience: String = "",

    var accessTokenValidity: Long = 3600
)