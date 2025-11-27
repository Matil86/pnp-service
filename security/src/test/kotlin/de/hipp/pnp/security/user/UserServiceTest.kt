package de.hipp.pnp.security.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

/**
 * Comprehensive test suite for UserService.
 *
 * Coverage includes:
 * - User existence checks with various inputs
 * - Role retrieval with edge cases
 * - External ID lookups
 * - User persistence operations
 * - String input validation (empty, null, whitespace, unicode, special chars)
 * - Security scenarios (SQL injection, XSS attempts)
 */
class UserServiceTest :
    FunSpec({

        lateinit var userRepository: UserRepository
        lateinit var userService: UserService

        beforeTest {
            userRepository = mockk()
            userService = UserService(userRepository)
        }

        context("userExists - User Existence Checks") {
            test("should return true when user exists with valid external ID") {
                runTest {
                    val externalId = "auth0|123456789"
                    val user =
                        User(
                            userId = "user-123",
                            externalIdentifier = externalId,
                            mail = "test@example.com",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val result = userService.userExists(externalId)

                    result shouldBe true
                    coVerify(exactly = 1) { userRepository.getUserByExternalIdentifier(externalId) }
                }
            }

            test("should return false when user does not exist") {
                runTest {
                    val externalId = "nonexistent-user"

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns null

                    val result = userService.userExists(externalId)

                    result shouldBe false
                }
            }

            test("should return false when external ID is null") {
                runTest {
                    coEvery { userRepository.getUserByExternalIdentifier(null) } returns null

                    val result = userService.userExists(null)

                    result shouldBe false
                }
            }

            test("should return false when external ID is empty string") {
                runTest {
                    coEvery { userRepository.getUserByExternalIdentifier("") } returns null

                    val result = userService.userExists("")

                    result shouldBe false
                }
            }

            test("should handle external ID with only whitespace") {
                runTest {
                    val whitespaceId = "   "
                    coEvery { userRepository.getUserByExternalIdentifier(whitespaceId) } returns null

                    val result = userService.userExists(whitespaceId)

                    result shouldBe false
                }
            }

            test("should handle external ID with hiragana characters") {
                runTest {
                    val hiraganaId = "ひらがな123"
                    coEvery { userRepository.getUserByExternalIdentifier(hiraganaId) } returns null

                    val result = userService.userExists(hiraganaId)

                    result shouldBe false
                }
            }

            test("should handle external ID with katakana characters") {
                runTest {
                    val katakanaId = "カタカナ456"
                    coEvery { userRepository.getUserByExternalIdentifier(katakanaId) } returns null

                    val result = userService.userExists(katakanaId)

                    result shouldBe false
                }
            }

            test("should handle external ID with emoji") {
                runTest {
                    val emojiId = "user😊123"
                    coEvery { userRepository.getUserByExternalIdentifier(emojiId) } returns null

                    val result = userService.userExists(emojiId)

                    result shouldBe false
                }
            }

            test("should handle external ID with SQL injection attempt") {
                runTest {
                    val sqlInjectionId = "'; DROP TABLE users; --"
                    coEvery { userRepository.getUserByExternalIdentifier(sqlInjectionId) } returns null

                    val result = userService.userExists(sqlInjectionId)

                    result shouldBe false
                }
            }

            test("should handle external ID with XSS attempt") {
                runTest {
                    val xssId = "<script>alert('XSS')</script>"
                    coEvery { userRepository.getUserByExternalIdentifier(xssId) } returns null

                    val result = userService.userExists(xssId)

                    result shouldBe false
                }
            }
        }

        context("getRole - Role Retrieval") {
            test("should return user role when user exists") {
                runTest {
                    val externalId = "auth0|123456789"
                    val user =
                        User(
                            userId = "user-123",
                            externalIdentifier = externalId,
                            role = "USER",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val role = userService.getRole(externalId)

                    role shouldBe "USER"
                }
            }

            test("should return ADMIN role when user is admin") {
                runTest {
                    val externalId = "auth0|admin123"
                    val user =
                        User(
                            userId = "admin-123",
                            externalIdentifier = externalId,
                            role = "ADMIN",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val role = userService.getRole(externalId)

                    role shouldBe "ADMIN"
                }
            }

            test("should return ANONYMOUS when user does not exist") {
                runTest {
                    val externalId = "nonexistent-user"

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns null

                    val role = userService.getRole(externalId)

                    role shouldBe "ANONYMOUS"
                }
            }

            test("should return ANONYMOUS when external ID is null") {
                runTest {
                    coEvery { userRepository.getUserByExternalIdentifier(null) } returns null

                    val role = userService.getRole(null)

                    role shouldBe "ANONYMOUS"
                }
            }

            test("should return ANONYMOUS when external ID is empty") {
                runTest {
                    coEvery { userRepository.getUserByExternalIdentifier("") } returns null

                    val role = userService.getRole("")

                    role shouldBe "ANONYMOUS"
                }
            }

            test("should handle user with null role") {
                runTest {
                    val externalId = "auth0|norole"
                    val user =
                        User(
                            userId = "user-123",
                            externalIdentifier = externalId,
                            role = null,
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val role = userService.getRole(externalId)

                    role shouldBe "null"
                }
            }

            test("should handle user with empty role") {
                runTest {
                    val externalId = "auth0|emptyrole"
                    val user =
                        User(
                            userId = "user-123",
                            externalIdentifier = externalId,
                            role = "",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val role = userService.getRole(externalId)

                    role shouldBe ""
                }
            }

            test("should handle user with unicode characters in role") {
                runTest {
                    val externalId = "auth0|unicode"
                    val user =
                        User(
                            userId = "user-123",
                            externalIdentifier = externalId,
                            role = "ユーザー",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val role = userService.getRole(externalId)

                    role shouldBe "ユーザー"
                }
            }
        }

        context("getUserByExternalId - User Retrieval") {
            test("should return user when found by external ID") {
                runTest {
                    val externalId = "auth0|123456789"
                    val expectedUser =
                        User(
                            userId = "user-123",
                            vorname = "John",
                            nachname = "Doe",
                            name = "John Doe",
                            externalIdentifier = externalId,
                            mail = "john.doe@example.com",
                            role = "USER",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns expectedUser

                    val result = userService.getUserByExternalId(externalId)

                    result shouldNotBe null
                    result?.userId shouldBe "user-123"
                    result?.vorname shouldBe "John"
                    result?.nachname shouldBe "Doe"
                    result?.mail shouldBe "john.doe@example.com"
                }
            }

            test("should return null when user not found") {
                runTest {
                    val externalId = "nonexistent-user"

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns null

                    val result = userService.getUserByExternalId(externalId)

                    result shouldBe null
                }
            }

            test("should return null when external ID is null") {
                runTest {
                    coEvery { userRepository.getUserByExternalIdentifier(null) } returns null

                    val result = userService.getUserByExternalId(null)

                    result shouldBe null
                }
            }

            test("should handle user with hiragana name") {
                runTest {
                    val externalId = "auth0|japanese"
                    val user =
                        User(
                            userId = "user-jp",
                            vorname = "たろう",
                            nachname = "やまだ",
                            name = "やまだたろう",
                            externalIdentifier = externalId,
                            mail = "yamada@example.jp",
                            role = "USER",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val result = userService.getUserByExternalId(externalId)

                    result shouldNotBe null
                    result?.vorname shouldBe "たろう"
                    result?.nachname shouldBe "やまだ"
                }
            }

            test("should handle user with katakana name") {
                runTest {
                    val externalId = "auth0|katakana"
                    val user =
                        User(
                            userId = "user-kt",
                            name = "タロウヤマダ",
                            externalIdentifier = externalId,
                            mail = "katakana@example.jp",
                            role = "USER",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val result = userService.getUserByExternalId(externalId)

                    result shouldNotBe null
                    result?.name shouldBe "タロウヤマダ"
                }
            }

            test("should handle user with emoji in name") {
                runTest {
                    val externalId = "auth0|emoji"
                    val user =
                        User(
                            userId = "user-emoji",
                            name = "Cool User 😎",
                            externalIdentifier = externalId,
                            mail = "cool@example.com",
                            role = "USER",
                        )

                    coEvery { userRepository.getUserByExternalIdentifier(externalId) } returns user

                    val result = userService.getUserByExternalId(externalId)

                    result shouldNotBe null
                    result?.name shouldBe "Cool User 😎"
                }
            }
        }

        context("saveUser - User Persistence") {
            test("should save user successfully") {
                runTest {
                    val user =
                        User(
                            userId = "new-user-123",
                            vorname = "Jane",
                            nachname = "Smith",
                            name = "Jane Smith",
                            externalIdentifier = "auth0|987654321",
                            mail = "jane.smith@example.com",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.userId shouldBe "new-user-123"
                    coVerify(exactly = 1) { userRepository.save(user) }
                }
            }

            test("should save user with minimal data") {
                runTest {
                    val user = User(userId = "minimal-user")

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.userId shouldBe "minimal-user"
                }
            }

            test("should save user with null optional fields") {
                runTest {
                    val user =
                        User(
                            userId = "user-null-fields",
                            vorname = null,
                            nachname = null,
                            name = null,
                            externalIdentifier = "auth0|nullfields",
                            mail = null,
                            role = null,
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.userId shouldBe "user-null-fields"
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
                            externalIdentifier = "auth0|empty",
                            mail = "",
                            role = "",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

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
                            externalIdentifier = "auth0|hiragana",
                            mail = "sakura@example.jp",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

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
                            externalIdentifier = "auth0|katakana2",
                            mail = "katakana2@example.jp",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.name shouldBe "サクラハルノ"
                }
            }

            test("should save user with emoji in email") {
                runTest {
                    val user =
                        User(
                            userId = "user-emoji-mail",
                            name = "Test User",
                            externalIdentifier = "auth0|emoji",
                            mail = "test😀@example.com",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.mail shouldBe "test😀@example.com"
                }
            }

            test("should handle long email addresses") {
                runTest {
                    val longEmail = "a".repeat(240) + "@example.com"
                    val user =
                        User(
                            userId = "user-long-email",
                            mail = longEmail,
                            externalIdentifier = "auth0|long",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.mail shouldBe longEmail
                }
            }

            test("should handle special characters in name") {
                runTest {
                    val user =
                        User(
                            userId = "user-special",
                            name = "O'Connor-Smith (Jr.)",
                            externalIdentifier = "auth0|special",
                            mail = "oconnor@example.com",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.name shouldContain "O'Connor"
                }
            }

            test("should preserve whitespace in names") {
                runTest {
                    val user =
                        User(
                            userId = "user-whitespace",
                            vorname = "  John  ",
                            nachname = "  Doe  ",
                            name = "  John Doe  ",
                            externalIdentifier = "auth0|whitespace",
                            mail = "john@example.com",
                            role = "USER",
                        )

                    coEvery { userRepository.save(user) } returns user

                    val result = userService.saveUser(user)

                    result shouldNotBe null
                    result?.vorname shouldBe "  John  "
                }
            }
        }
    })
