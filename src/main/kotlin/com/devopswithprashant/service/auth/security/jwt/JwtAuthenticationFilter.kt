package com.devopswithprashant.service.auth.security.jwt

import com.devopswithprashant.service.auth.security.user.AuthenticatedUserDetailsService
import com.devopswithprashant.service.auth.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class JwtAuthenticationFilter(

    private val tokenService: TokenService,

    private val authenticatedUserDetailsService: AuthenticatedUserDetailsService

) : OncePerRequestFilter() {

    override fun doFilterInternal(

        request: HttpServletRequest,

        response: HttpServletResponse,

        filterChain: FilterChain

    ) {

        val authorizationHeader =

            request.getHeader("Authorization")

        if (

            authorizationHeader.isNullOrBlank() ||

            !authorizationHeader.startsWith("Bearer ")

        ) {

            filterChain.doFilter(request, response)

            return

        }

        val token =

            authorizationHeader.removePrefix("Bearer ").trim()

        if (

            !tokenService.validateToken(token)

        ) {

            filterChain.doFilter(request, response)

            return

        }

        val userId =

            UUID.fromString(
                tokenService.extractSubject(token)
            )

        val userDetails =

            authenticatedUserDetailsService.loadUserById(
                userId
            )

        val authentication =

            UsernamePasswordAuthenticationToken(

                userDetails,
                null,
                userDetails.authorities

            )

        SecurityContextHolder

            .getContext()
            .authentication = authentication

        filterChain.doFilter(

            request,
            response

        )
    }

    override fun shouldNotFilter(
        request: HttpServletRequest
    ): Boolean {
        return request.servletPath == "/api/v1/auth/login" ||
            request.servletPath == "/api/v1/auth/register"
    }
}