package com.devopswithprashant.service.auth.entity

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Instant

class BaseEntityTest {

    @Test
    fun `base entity can hold created and updated timestamps`() {
        val entity = object : BaseEntity() {}
        entity.createdAt = Instant.now()
        entity.updatedAt = Instant.now()

        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }
}
