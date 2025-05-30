package de.hipp.pnp.security.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `test userExists returns true when user exists`() {
        // Given
        val externalId = "ext-123"
        val user = User(externalIdentifer = externalId)
        `when`(userRepository.getUserByExternalIdentifer(externalId)).thenReturn(user)

        // When
        val result = userService.userExists(externalId)

        // Then
        assertTrue(result)
        verify(userRepository).getUserByExternalIdentifer(externalId)
    }

    @Test
    fun `test userExists returns false when user does not exist`() {
        // Given
        val externalId = "ext-123"
        `when`(userRepository.getUserByExternalIdentifer(externalId)).thenReturn(null)

        // When
        val result = userService.userExists(externalId)

        // Then
        assertFalse(result)
        verify(userRepository).getUserByExternalIdentifer(externalId)
    }

    @Test
    fun `test getRole returns user role when user exists`() {
        // Given
        val externalId = "ext-123"
        val role = "ADMIN"
        val user = User(externalIdentifer = externalId, role = role)
        `when`(userRepository.getUserByExternalIdentifer(externalId)).thenReturn(user)

        // When
        val result = userService.getRole(externalId)

        // Then
        assertEquals(role, result)
        verify(userRepository).getUserByExternalIdentifer(externalId)
    }

    @Test
    fun `test getRole returns ANNONYMOUS when user does not exist`() {
        // Given
        val externalId = "ext-123"
        `when`(userRepository.getUserByExternalIdentifer(externalId)).thenReturn(null)

        // When
        val result = userService.getRole(externalId)

        // Then
        assertEquals("ANNONYMOUS", result)
        verify(userRepository).getUserByExternalIdentifer(externalId)
    }

    @Test
    fun `test getUserByExternalId returns user when user exists`() {
        // Given
        val externalId = "ext-123"
        val user = User(externalIdentifer = externalId)
        `when`(userRepository.getUserByExternalIdentifer(externalId)).thenReturn(user)

        // When
        val result = userService.getUserByExternalId(externalId)

        // Then
        assertEquals(user, result)
        verify(userRepository).getUserByExternalIdentifer(externalId)
    }

    @Test
    fun `test getUserByExternalId returns null when user does not exist`() {
        // Given
        val externalId = "ext-123"
        `when`(userRepository.getUserByExternalIdentifer(externalId)).thenReturn(null)

        // When
        val result = userService.getUserByExternalId(externalId)

        // Then
        assertNull(result)
        verify(userRepository).getUserByExternalIdentifer(externalId)
    }

    @Test
    fun `test saveUser calls repository save method and returns saved user`() {
        // Given
        val user = User(userId = "test-id", vorname = "John", nachname = "Doe")
        `when`(userRepository.save(user)).thenReturn(user)

        // When
        val result = userService.saveUser(user)

        // Then
        assertEquals(user, result)
        verify(userRepository).save(user)
    }
}