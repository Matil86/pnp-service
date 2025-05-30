package de.hipp.pnp.security.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `test getUserByExternalIdentifer returns user when user exists`() {
        // Given
        val externalId = "ext-123"
        val user = User(
            vorname = "John",
            nachname = "Doe",
            name = "John Doe",
            externalIdentifer = externalId,
            mail = "john.doe@example.com",
            role = "USER"
        )
        entityManager.persist(user)
        entityManager.flush()

        // When
        val foundUser = userRepository.getUserByExternalIdentifer(externalId)

        // Then
        assertNotNull(foundUser)
        assertEquals(externalId, foundUser?.externalIdentifer)
    }

    @Test
    fun `test getUserByExternalIdentifer returns null when user does not exist`() {
        // Given
        val nonExistentExternalId = "non-existent-id"

        // When
        val foundUser = userRepository.getUserByExternalIdentifer(nonExistentExternalId)

        // Then
        assertNull(foundUser)
    }

    @Test
    fun `test save persists user and returns saved user`() {
        // Given
        val user = User(
            vorname = "Jane",
            nachname = "Doe",
            name = "Jane Doe",
            externalIdentifer = "ext-456",
            mail = "jane.doe@example.com",
            role = "ADMIN"
        )

        // When
        val savedUser = userRepository.save(user)

        // Then
        assertNotNull(savedUser)
        assertNotNull(savedUser.userId)
        
        // Verify the user was actually saved
        val foundUser = entityManager.find(User::class.java, savedUser.userId)
        assertNotNull(foundUser)
        assertEquals(user.vorname, foundUser.vorname)
        assertEquals(user.nachname, foundUser.nachname)
        assertEquals(user.name, foundUser.name)
        assertEquals(user.externalIdentifer, foundUser.externalIdentifer)
        assertEquals(user.mail, foundUser.mail)
        assertEquals(user.role, foundUser.role)
    }
}