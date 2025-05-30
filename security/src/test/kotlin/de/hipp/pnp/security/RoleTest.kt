package de.hipp.pnp.security

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class RoleTest {

    @Test
    fun `test toString returns correct value for USER role`() {
        // Given
        val role = Role.USER

        // When
        val result = role.toString()

        // Then
        assertEquals("USER", result)
    }

    @Test
    fun `test toString returns correct value for ADMIN role`() {
        // Given
        val role = Role.ADMIN

        // When
        val result = role.toString()

        // Then
        assertEquals("ADMIN", result)
    }
}