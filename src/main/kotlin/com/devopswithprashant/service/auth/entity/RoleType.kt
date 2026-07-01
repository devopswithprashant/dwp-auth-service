package com.devopswithprashant.service.auth.entity

/**
 * Defines all application roles.
 *
 * These values are persisted in the database and are also
 * used by Spring Security during authorization.
 */
enum class RoleType {
    
    ROLE_USER,
    ROLE_ADMIN
}