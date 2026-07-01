package com.devopswithprashant.service.auth.entity

import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Role(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "name",
        nullable = false,
        unique = true,
        length = 50
    )
    val name: RoleType

)