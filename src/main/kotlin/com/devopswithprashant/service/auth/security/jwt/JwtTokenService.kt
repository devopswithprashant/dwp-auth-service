package com.devopswithprashant.service.auth.security.jwt

import com.devopswithprashant.service.auth.config.JwtProperties
import com.devopswithprashant.service.auth.dto.response.TokenResponse
import com.devopswithprashant.service.auth.model.AuthenticatedUser
import com.devopswithprashant.service.auth.service.TokenService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

@Service
class JwtTokenService(

    private val jwtProperties: JwtProperties

) : TokenService {

    private val signingKey =
        Keys.hmacShaKeyFor(
            jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        )

    override fun generateAccessToken(
        authenticatedUser: AuthenticatedUser
    ): TokenResponse {

        val now = Instant.now()

        val expiry =
            now.plusSeconds(jwtProperties.accessTokenValidity)

        val jwt =
            Jwts.builder()

                .subject(authenticatedUser.id.toString())

                .issuer(jwtProperties.issuer)

                .audience()
                .add(jwtProperties.audience)
                .and()

                .issuedAt(Date.from(now))

                .expiration(Date.from(expiry))

                .claim(
                    "preferred_username",
                    authenticatedUser.username
                )

                .claim(
                    "email",
                    authenticatedUser.email
                )

                .claim(
                    "roles",
                    authenticatedUser.roles.map { it.name }
                )

                .signWith(signingKey)

                .compact()

        return TokenResponse(

            accessToken = jwt,

            refreshToken = null,

            expiresInSeconds =
                jwtProperties.accessTokenValidity
        )
    }
}