package com.devopswithprashant.service.auth.repository

import com.devopswithprashant.auth.entity.Role
import com.devopswithprashant.auth.entity.RoleType
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: RoleType): Role?
}