package com.devopswithprashant.service.auth.security.jwt

import com.devopswithprashant.service.auth.config.JwtProperties
import com.devopswithprashant.service.auth.dto.response.TokenResponse
import com.devopswithprashant.service.auth.service.TokenService
import com.devopswithprashant.service.auth.model.AuthenticatedUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtTokenService(

    private val jwtProperties: JwtProperties

) : TokenService {

    private val signingKey: SecretKey =
        Keys.hmacShaKeyFor(
            jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        )

    /**
     * Generate JWT access token.
     */
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
                    authenticatedUser.roles
                )

                .signWith(signingKey)

                .compact()

        return TokenResponse(
            accessToken = jwt,
            expiresInSeconds = jwtProperties.accessTokenValidity
        )
    }

    /**
     * Validate JWT signature and expiration.
     */
    override fun validateToken(
        token: String
    ): Boolean {

        return try {

            parseClaims(token)

            true

        } catch (ex: Exception) {

            false

        }
    }

    /**
     * Extract subject (User UUID).
     */
    override fun extractSubject(
        token: String
    ): String {

        return parseClaims(token)
            .subject
    }

    /**
     * Extract preferred username.
     */
    fun extractUsername(
        token: String
    ): String {

        return parseClaims(token)
            .get(
                "preferred_username",
                String::class.java
            )
    }

    /**
     * Extract all JWT claims.
     */
    fun extractClaims(
        token: String
    ): Claims {

        return parseClaims(token)
    }

    /**
     * Returns true if token has expired.
     */
    fun isExpired(
        token: String
    ): Boolean {

        return parseClaims(token)
            .expiration
            .before(Date())
    }

    /**
     * Parse and validate JWT.
     */
    private fun parseClaims(
        token: String
    ): Claims {

        return Jwts.parser()

            .verifyWith(signingKey)

            .build()

            .parseSignedClaims(token)

            .payload
    }
}