package de.hipp.pnp.genefunk

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot
import com.google.cloud.firestore.WriteResult
import de.hipp.pnp.base.fivee.Attribute5e
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * Comprehensive test suite for GeneFunkCharacterRepository with Firestore.
 *
 * Coverage includes:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Query operations (findAll, findByUserId)
 * - Edge cases (null values, empty results, special characters)
 * - Error handling
 * - ID generation logic
 */
class GeneFunkCharacterRepositoryTest :
    FunSpec({

        lateinit var firestore: Firestore
        lateinit var repository: GeneFunkCharacterRepository
        lateinit var collection: CollectionReference

        beforeEach {
            firestore = mockk<Firestore>(relaxed = true)
            collection = mockk<CollectionReference>(relaxed = true)
            every { firestore.collection("genefunk_characters") } returns collection
            repository = GeneFunkCharacterRepository(firestore)
        }

        context("findAll") {
            test("should return all characters from Firestore") {
                val char1 = createTestCharacter(1, "Alice", "user1")
                val char2 = createTestCharacter(2, "Bob", "user2")

                val querySnapshot = mockk<QuerySnapshot>()
                val doc1 = mockDocumentSnapshot(char1)
                val doc2 = mockDocumentSnapshot(char2)

                every { querySnapshot.documents } returns listOf(doc1, doc2)
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { collection.get() } returns apiFuture

                val result = repository.findAll()

                result shouldHaveSize 2
                result[0].firstName shouldBe "Alice"
                result[1].firstName shouldBe "Bob"
            }

            test("should return empty list when no characters exist") {
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { collection.get() } returns apiFuture

                val result = repository.findAll()

                result.shouldBeEmpty()
            }

            test("should handle empty documents") {
                val querySnapshot = mockk<QuerySnapshot>()

                every { querySnapshot.documents } returns emptyList()
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { collection.get() } returns apiFuture

                val result = repository.findAll()

                result.shouldBeEmpty()
            }
        }

        context("findById") {
            test("should return character when ID exists") {
                val char = createTestCharacter(1, "Alice", "user1")
                val doc = mockk<DocumentReference>()
                val snapshot = mockk<DocumentSnapshot>()

                every { collection.document("1") } returns doc
                every { snapshot.toObject(GeneFunkCharacter::class.java) } returns char
                val apiFuture = mockk<ApiFuture<DocumentSnapshot>>()
                every { apiFuture.get() } returns snapshot
                every { doc.get() } returns apiFuture

                val result = repository.findById(1)

                result shouldNotBe null
                result?.firstName shouldBe "Alice"
            }

            test("should return null when ID does not exist") {
                val doc = mockk<DocumentReference>()
                val snapshot = mockk<DocumentSnapshot>()

                every { collection.document("999") } returns doc
                every { snapshot.toObject(GeneFunkCharacter::class.java) } returns null
                val apiFuture = mockk<ApiFuture<DocumentSnapshot>>()
                every { apiFuture.get() } returns snapshot
                every { doc.get() } returns apiFuture

                val result = repository.findById(999)

                result shouldBe null
            }

            test("should handle special ID values") {
                val char = createTestCharacter(0, "Zero", "user0")
                val doc = mockk<DocumentReference>()
                val snapshot = mockk<DocumentSnapshot>()

                every { collection.document("0") } returns doc
                every { snapshot.toObject(GeneFunkCharacter::class.java) } returns char
                val apiFuture = mockk<ApiFuture<DocumentSnapshot>>()
                every { apiFuture.get() } returns snapshot
                every { doc.get() } returns apiFuture

                val result = repository.findById(0)

                result shouldNotBe null
            }
        }

        context("findByUserId") {
            test("should return characters for specific user") {
                val char1 = createTestCharacter(1, "Alice", "user1")
                val char2 = createTestCharacter(2, "Bob", "user1")

                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()
                val doc1 = mockDocumentSnapshot(char1)
                val doc2 = mockDocumentSnapshot(char2)

                every { querySnapshot.documents } returns listOf(doc1, doc2)
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId("user1")

                result shouldHaveSize 2
                result.all { it.userId == "user1" } shouldBe true
            }

            test("should return empty list when user has no characters") {
                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()

                every { querySnapshot.documents } returns emptyList()
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId("nonexistent")

                result.shouldBeEmpty()
            }

            test("should handle null userId") {
                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()

                every { querySnapshot.documents } returns emptyList()
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId(null)

                result.shouldBeEmpty()
            }

            test("should handle empty string userId") {
                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()

                every { querySnapshot.documents } returns emptyList()
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId("")

                result.shouldBeEmpty()
            }

            test("should handle whitespace-only userId") {
                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()

                every { querySnapshot.documents } returns emptyList()
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId("   ")

                result.shouldBeEmpty()
            }

            test("should handle userId with special characters") {
                val char = createTestCharacter(1, "Alice", "user@123.com")
                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()
                val doc = mockDocumentSnapshot(char)

                every { querySnapshot.documents } returns listOf(doc)
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId("user@123.com")

                result shouldHaveSize 1
                result[0].userId shouldBe "user@123.com"
            }

            test("should handle userId with Unicode characters") {
                val char = createTestCharacter(1, "Alice", "ユーザー１")
                val query = mockk<Query>()
                val querySnapshot = mockk<QuerySnapshot>()
                val doc = mockDocumentSnapshot(char)

                every { querySnapshot.documents } returns listOf(doc)
                val apiFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { apiFuture.get() } returns querySnapshot
                every { query.get() } returns apiFuture
                every { collection.where(any()) } returns query

                val result = repository.findByUserId("ユーザー１")

                result shouldHaveSize 1
            }
        }

        context("saveAndFlush") {
            test("should save character with existing ID") {
                val char = createTestCharacter(1, "Alice", "user1")
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("1") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result shouldBe char
                result.id shouldBe 1
                verify { doc.set(char) }
            }

            test("should generate ID when character has no ID") {
                val char = createTestCharacter(null, "Alice", "user1")
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document(any()) } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId to return 5
                val existingChar = createTestCharacter(5, "Bob", "user2")
                val querySnapshot = mockk<QuerySnapshot>()
                val existingDoc = mockDocumentSnapshot(existingChar)
                every { querySnapshot.documents } returns listOf(existingDoc)
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result.id shouldBe 6
                verify { doc.set(char) }
            }

            test("should generate ID 1 when no characters exist") {
                val char = createTestCharacter(null, "Alice", "user1")
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document(any()) } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId to return 0
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result.id shouldBe 1
            }

            test("should handle character with null fields") {
                val char = GeneFunkCharacter()
                char.id = 1
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("1") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result shouldNotBe null
            }
        }

        context("deleteById") {
            test("should delete character by ID") {
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("1") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.delete() } returns apiFuture

                repository.deleteById(1)

                verify { doc.delete() }
            }

            test("should handle deletion of non-existent ID") {
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("999") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.delete() } returns apiFuture

                repository.deleteById(999)

                verify { doc.delete() }
            }

            test("should handle zero ID deletion") {
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("0") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.delete() } returns apiFuture

                repository.deleteById(0)

                verify { doc.delete() }
            }
        }

        context("Edge Cases") {
            test("should handle character with very long name") {
                val longName = "A".repeat(1000)
                val char = createTestCharacter(1, longName, "user1")
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("1") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result.firstName shouldBe longName
            }

            test("should handle character with emoji in name") {
                val char = createTestCharacter(1, "Alice 😊", "user1")
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("1") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result.firstName shouldBe "Alice 😊"
            }

            test("should handle character with hiragana in name") {
                val char = createTestCharacter(1, "あいうえお", "user1")
                val doc = mockk<DocumentReference>()
                val writeResult = mockk<WriteResult>()

                every { collection.document("1") } returns doc
                val apiFuture = mockk<ApiFuture<WriteResult>>()
                every { apiFuture.get() } returns writeResult
                every { doc.set(char) } returns apiFuture

                // Mock findMaxId
                val querySnapshot = mockk<QuerySnapshot>()
                every { querySnapshot.documents } returns emptyList()
                val queryFuture = mockk<ApiFuture<QuerySnapshot>>()
                every { queryFuture.get() } returns querySnapshot
                every { collection.get() } returns queryFuture

                val result = repository.saveAndFlush(char)

                result.firstName shouldBe "あいうえお"
            }
        }
    })

private fun createTestCharacter(
    id: Int?,
    firstName: String,
    userId: String,
): GeneFunkCharacter {
    val char = GeneFunkCharacter()
    char.id = id
    char.firstName = firstName
    char.userId = userId
    char.strength = Attribute5e(10)
    char.dexterity = Attribute5e(12)
    char.constitution = Attribute5e(14)
    char.intelligence = Attribute5e(16)
    char.wisdom = Attribute5e(8)
    char.charisma = Attribute5e(11)
    return char
}

private fun mockDocumentSnapshot(char: GeneFunkCharacter): QueryDocumentSnapshot {
    val doc = mockk<QueryDocumentSnapshot>()
    every { doc.toObject(GeneFunkCharacter::class.java) } returns char
    return doc
}
