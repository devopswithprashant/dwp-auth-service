package com.devopswithprashant.service.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.charset.StandardCharsets

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(

    var secret: String = "",

    var issuer: String = "",

    var audience: String = "",

    var accessTokenValidity: Long = 3600
) {
    init {
        require(secret.toByteArray(StandardCharsets.UTF_8).size >= 48) {
            "jwt.secret must contain at least 48 UTF-8 bytes for HS384"
        }
        require(accessTokenValidity > 0) { "jwt.access-token-validity must be positive" }
    }
}