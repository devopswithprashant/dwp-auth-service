package com.devopswithprashant.service.auth.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_credentials")
class UserCredential(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(
        name = "username",
        nullable = false,
        unique = true,
        length = 100
    )
    var username: String,

    @Column(
        name = "email",
        nullable = false,
        unique = true,
        length = 255
    )
    var email: String,

    @Column(
        name = "password_hash",
        nullable = false
    )
    var passwordHash: String,

    @Column(
        name = "enabled",
        nullable = false
    )
    var enabled: Boolean = true,

    @Column(
        name = "email_verified",
        nullable = false
    )
    var emailVerified: Boolean = false,

    @Column(
        name = "account_non_locked",
        nullable = false
    )
    var accountNonLocked: Boolean = true

) : BaseEntity() {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "credential_roles",
        joinColumns = [JoinColumn(name = "credential_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    private val roles: MutableSet<Role> = mutableSetOf()

    fun getRoles(): Set<Role> = roles.toSet()

    fun assignRole(role: Role) {
        roles.add(role)
    }

    fun removeRole(role: Role) {
        roles.remove(role)
    }

    fun changePassword(newPasswordHash: String) {
        passwordHash = newPasswordHash
    }

    fun verifyEmail() {
        emailVerified = true
    }

    fun lockAccount() {
        accountNonLocked = false
    }

    fun unlockAccount() {
        accountNonLocked = true
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }
}