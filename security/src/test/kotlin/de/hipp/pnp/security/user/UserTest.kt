package de.hipp.pnp.security.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class UserTest {

    @Test
    fun `test user creation with default values`() {
        // Given
        val user = User()

        // Then
        assertNull(user.userId)
        assertNull(user.vorname)
        assertNull(user.nachname)
        assertNull(user.name)
        assertNull(user.externalIdentifer)
        assertNull(user.mail)
        assertNull(user.role)
    }

    @Test
    fun `test user creation with specific values`() {
        // Given
        val userId = "test-id"
        val vorname = "John"
        val nachname = "Doe"
        val name = "John Doe"
        val externalIdentifier = "ext-123"
        val mail = "john.doe@example.com"
        val role = "USER"

        // When
        val user = User(
            userId = userId,
            vorname = vorname,
            nachname = nachname,
            name = name,
            externalIdentifer = externalIdentifier,
            mail = mail,
            role = role
        )

        // Then
        assertEquals(userId, user.userId)
        assertEquals(vorname, user.vorname)
        assertEquals(nachname, user.nachname)
        assertEquals(name, user.name)
        assertEquals(externalIdentifier, user.externalIdentifer)
        assertEquals(mail, user.mail)
        assertEquals(role, user.role)
    }

    @Test
    fun `test setting user properties`() {
        // Given
        val user = User()
        val userId = "test-id"
        val vorname = "John"
        val nachname = "Doe"
        val name = "John Doe"
        val externalIdentifier = "ext-123"
        val mail = "john.doe@example.com"
        val role = "USER"

        // When
        user.userId = userId
        user.vorname = vorname
        user.nachname = nachname
        user.name = name
        user.externalIdentifer = externalIdentifier
        user.mail = mail
        user.role = role

        // Then
        assertEquals(userId, user.userId)
        assertEquals(vorname, user.vorname)
        assertEquals(nachname, user.nachname)
        assertEquals(name, user.name)
        assertEquals(externalIdentifier, user.externalIdentifer)
        assertEquals(mail, user.mail)
        assertEquals(role, user.role)
    }
}