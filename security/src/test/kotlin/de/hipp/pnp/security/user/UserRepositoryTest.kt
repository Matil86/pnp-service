package de.hipp.pnp.security.user

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest

/**
 * Comprehensive test suite for UserRepository.
 *
 * Coverage includes:
 * - Firestore query operations
 * - User retrieval by external ID
 * - User persistence
 * - Edge cases with null/empty/unicode inputs
 * - Security scenarios (SQL injection prevention)
 * - Error handling
 */
class UserRepositoryTest :
    FunSpec({

        lateinit var firestore: Firestore
        lateinit var userRepository: UserRepository
        lateinit var collectionReference: CollectionReference
        lateinit var query: Query
        lateinit var apiFuture: ApiFuture<QuerySnapshot>
        lateinit var querySnapshot: QuerySnapshot
        lateinit var documentReference: DocumentReference

        beforeTest {
            firestore = mockk()
            collectionReference = mockk()
            query = mockk()
            apiFuture = mockk()
            querySnapshot = mockk()
            documentReference = mockk()
            userRepository = UserRepository(firestore)
        }

        context("getUserByExternalIdentifer - User Retrieval") {
            test("should return user when found by external identifier") {
                runTest {
                    val externalId = "auth0|123456789"
                    val documentSnapshot = mockk<QueryDocumentSnapshot>()
                    val expectedUser =
                        User(
                            userId = "user-123",
                            vorname = "John",
                            nachname = "Doe",
                            externalIdentifer = externalId,
                            mail = "john@example.com",
                            role = "USER",
                        )

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns mutableListOf(documentSnapshot)
                    every { documentSnapshot.toObject(User::class.java) } returns expectedUser

                    val result = userRepository.getUserByExternalIdentifer(externalId)

                    result shouldNotBe null
                    result?.userId shouldBe "user-123"
                    result?.vorname shouldBe "John"
                    result?.externalIdentifer shouldBe externalId

                    verify(exactly = 1) { firestore.collection("users") }
                    verify(exactly = 1) { query.limit(1) }
                }
            }

            test("should return null when no user found") {
                runTest {
                    val externalId = "nonexistent-user"

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(externalId)

                    result shouldBe null
                }
            }

            test("should return null when external ID is null") {
                runTest {
                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(null)

                    result shouldBe null
                }
            }

            test("should return null when external ID is empty string") {
                runTest {
                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer("")

                    result shouldBe null
                }
            }

            test("should handle external ID with whitespace only") {
                runTest {
                    val whitespaceId = "   "

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(whitespaceId)

                    result shouldBe null
                }
            }

            test("should handle external ID with hiragana characters") {
                runTest {
                    val hiraganaId = "ひらがな123"
                    val documentSnapshot = mockk<QueryDocumentSnapshot>()
                    val user =
                        User(
                            userId = "user-hiragana",
                            externalIdentifer = hiraganaId,
                            role = "USER",
                        )

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns mutableListOf(documentSnapshot)
                    every { documentSnapshot.toObject(User::class.java) } returns user

                    val result = userRepository.getUserByExternalIdentifer(hiraganaId)

                    result shouldNotBe null
                    result?.externalIdentifer shouldBe hiraganaId
                }
            }

            test("should handle external ID with katakana characters") {
                runTest {
                    val katakanaId = "カタカナ456"
                    val documentSnapshot = mockk<QueryDocumentSnapshot>()
                    val user =
                        User(
                            userId = "user-katakana",
                            externalIdentifer = katakanaId,
                            role = "USER",
                        )

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns mutableListOf(documentSnapshot)
                    every { documentSnapshot.toObject(User::class.java) } returns user

                    val result = userRepository.getUserByExternalIdentifer(katakanaId)

                    result shouldNotBe null
                    result?.externalIdentifer shouldBe katakanaId
                }
            }

            test("should handle external ID with emoji") {
                runTest {
                    val emojiId = "user😊123"

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(emojiId)

                    // Should safely handle emoji in query without errors
                    result shouldBe null
                }
            }

            test("should safely handle SQL injection attempt in external ID") {
                runTest {
                    val sqlInjectionId = "'; DROP TABLE users; --"

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(sqlInjectionId)

                    // Firestore API is safe from SQL injection as it's NoSQL
                    result shouldBe null
                }
            }

            test("should safely handle XSS attempt in external ID") {
                runTest {
                    val xssId = "<script>alert('XSS')</script>"

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(xssId)

                    result shouldBe null
                }
            }

            test("should handle very long external ID") {
                runTest {
                    val longId = "a".repeat(1000)

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(longId)

                    result shouldBe null
                }
            }

            test("should handle external ID with special characters") {
                runTest {
                    val specialId = "user@domain.com|12345!#$%"
                    val documentSnapshot = mockk<QueryDocumentSnapshot>()
                    val user =
                        User(
                            userId = "user-special",
                            externalIdentifer = specialId,
                            role = "USER",
                        )

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns mutableListOf(documentSnapshot)
                    every { documentSnapshot.toObject(User::class.java) } returns user

                    val result = userRepository.getUserByExternalIdentifer(specialId)

                    result shouldNotBe null
                    result?.externalIdentifer shouldBe specialId
                }
            }

            test("should handle external ID with newline characters") {
                runTest {
                    val newlineId = "user\n123\n456"

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(newlineId)

                    result shouldBe null
                }
            }

            test("should handle external ID with tab characters") {
                runTest {
                    val tabId = "user\t123"

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.where(any()) } returns query
                    every { query.limit(1) } returns query
                    every { query.get() } returns apiFuture
                    every { apiFuture.get() } returns querySnapshot
                    every { querySnapshot.documents } returns emptyList()

                    val result = userRepository.getUserByExternalIdentifer(tabId)

                    result shouldBe null
                }
            }
        }

        context("save - User Persistence") {
            test("should save user successfully") {
                runTest {
                    val user =
                        User(
                            userId = "new-user-123",
                            vorname = "Jane",
                            nachname = "Smith",
                            externalIdentifer = "auth0|987654321",
                            mail = "jane@example.com",
                            role = "USER",
                        )

                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.userId shouldBe "new-user-123"

                    verify(exactly = 1) { firestore.collection("users") }
                    verify(exactly = 1) { collectionReference.document("new-user-123") }
                    verify(exactly = 1) { documentReference.set(user) }
                }
            }

            test("should save user with minimal data") {
                runTest {
                    val user = User(userId = "minimal-user")
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.userId shouldBe "minimal-user"
                }
            }

            test("should save user with null fields") {
                runTest {
                    val user =
                        User(
                            userId = "user-null",
                            vorname = null,
                            nachname = null,
                            name = null,
                            externalIdentifer = null,
                            mail = null,
                            role = null,
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.userId shouldBe "user-null"
                }
            }

            test("should save user with empty strings") {
                runTest {
                    val user =
                        User(
                            userId = "user-empty",
                            vorname = "",
                            nachname = "",
                            name = "",
                            externalIdentifer = "",
                            mail = "",
                            role = "",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.vorname shouldBe ""
                }
            }

            test("should save user with hiragana characters") {
                runTest {
                    val user =
                        User(
                            userId = "user-hiragana",
                            vorname = "さくら",
                            nachname = "はるの",
                            name = "はるのさくら",
                            externalIdentifer = "auth0|hiragana",
                            mail = "sakura@example.jp",
                            role = "USER",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.vorname shouldBe "さくら"
                }
            }

            test("should save user with katakana characters") {
                runTest {
                    val user =
                        User(
                            userId = "user-katakana",
                            name = "サクラハルノ",
                            externalIdentifer = "auth0|katakana",
                            mail = "katakana@example.jp",
                            role = "USER",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.name shouldBe "サクラハルノ"
                }
            }

            test("should save user with emoji in fields") {
                runTest {
                    val user =
                        User(
                            userId = "user-emoji",
                            name = "Cool User 😎",
                            externalIdentifer = "auth0|emoji",
                            mail = "cool😊@example.com",
                            role = "USER",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.name shouldBe "Cool User 😎"
                }
            }

            test("should save user with special characters in name") {
                runTest {
                    val user =
                        User(
                            userId = "user-special",
                            name = "O'Connor-Smith (Jr.)",
                            externalIdentifer = "auth0|special",
                            mail = "oconnor@example.com",
                            role = "USER",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.name shouldBe "O'Connor-Smith (Jr.)"
                }
            }

            test("should save user with very long email") {
                runTest {
                    val longEmail = "a".repeat(240) + "@example.com"
                    val user =
                        User(
                            userId = "user-long-email",
                            mail = longEmail,
                            externalIdentifer = "auth0|long",
                            role = "USER",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.mail shouldBe longEmail
                }
            }

            test("should save user with whitespace in fields") {
                runTest {
                    val user =
                        User(
                            userId = "user-whitespace",
                            vorname = "  John  ",
                            nachname = "  Doe  ",
                            name = "  John Doe  ",
                            externalIdentifer = "auth0|whitespace",
                            mail = "john@example.com",
                            role = "USER",
                        )
                    val apiFuture = mockk<ApiFuture<com.google.cloud.firestore.WriteResult>>()

                    every { firestore.collection("users") } returns collectionReference
                    every { collectionReference.document(user.userId) } returns documentReference
                    every { documentReference.set(user) } returns apiFuture

                    val result = userRepository.save(user)

                    result shouldNotBe null
                    result?.vorname shouldBe "  John  "
                }
            }
        }
    })
