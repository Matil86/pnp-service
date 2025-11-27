package de.hipp.pnp.security.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import java.util.UUID

/**
 * Comprehensive test suite for User entity.
 *
 * Coverage includes:
 * - User creation with various constructors
 * - Field validation
 * - String input edge cases (empty, null, whitespace, unicode)
 * - Security scenarios
 * - UUID generation
 * - Entity behavior
 */
class UserTest :
    FunSpec({

        context("User Construction - Primary Constructor") {
            test("should create user with all fields") {
                val user =
                    User(
                        userId = "user-123",
                        vorname = "John",
                        nachname = "Doe",
                        name = "John Doe",
                        externalIdentifier = "auth0|123456",
                        mail = "john@example.com",
                        role = "USER",
                    )

                user.userId shouldBe "user-123"
                user.vorname shouldBe "John"
                user.nachname shouldBe "Doe"
                user.name shouldBe "John Doe"
                user.externalIdentifier shouldBe "auth0|123456"
                user.mail shouldBe "john@example.com"
                user.role shouldBe "USER"
            }

            test("should create user with minimal required field") {
                val user = User(userId = "user-minimal")

                user.userId shouldBe "user-minimal"
                user.vorname shouldBe null
                user.nachname shouldBe null
                user.name shouldBe null
                user.externalIdentifier shouldBe null
                user.mail shouldBe null
                user.role shouldBe null
            }

            test("should create user with null optional fields") {
                val user =
                    User(
                        userId = "user-null",
                        vorname = null,
                        nachname = null,
                        name = null,
                        externalIdentifier = null,
                        mail = null,
                        role = null,
                    )

                user.userId shouldBe "user-null"
                user.vorname shouldBe null
                user.nachname shouldBe null
            }

            test("should create user with empty strings") {
                val user =
                    User(
                        userId = "user-empty",
                        vorname = "",
                        nachname = "",
                        name = "",
                        externalIdentifier = "",
                        mail = "",
                        role = "",
                    )

                user.userId shouldBe "user-empty"
                user.vorname shouldBe ""
                user.nachname shouldBe ""
                user.name shouldBe ""
            }

            test("should create ADMIN user") {
                val user =
                    User(
                        userId = "admin-123",
                        name = "Admin User",
                        mail = "admin@example.com",
                        role = "ADMIN",
                    )

                user.role shouldBe "ADMIN"
            }
        }

        context("User Construction - No-Arg Constructor") {
            test("should create user with generated UUID") {
                val user = User()

                user.userId.shouldNotBeEmpty()
                user.vorname shouldBe null
                user.nachname shouldBe null
                user.name shouldBe null
                user.externalIdentifier shouldBe null
                user.mail shouldBe null
                user.role shouldBe null
            }

            test("should generate unique UUIDs for different users") {
                val user1 = User()
                val user2 = User()

                user1.userId shouldNotBe user2.userId
            }

            test("should generate valid UUID format") {
                val user = User()

                // Should be able to parse as UUID without exception
                val uuid = UUID.fromString(user.userId)
                uuid shouldNotBe null
            }
        }

        context("User Fields - String Input Validation") {
            test("should handle hiragana in vorname") {
                val user =
                    User(
                        userId = "user-hiragana",
                        vorname = "さくら",
                        nachname = "はるの",
                    )

                user.vorname shouldBe "さくら"
                user.nachname shouldBe "はるの"
            }

            test("should handle katakana in name") {
                val user =
                    User(
                        userId = "user-katakana",
                        name = "サクラハルノ",
                    )

                user.name shouldBe "サクラハルノ"
            }

            test("should handle mixed Japanese characters") {
                val user =
                    User(
                        userId = "user-mixed",
                        vorname = "さくら",
                        nachname = "春野",
                        name = "春野さくら",
                    )

                user.vorname shouldBe "さくら"
                user.nachname shouldBe "春野"
                user.name shouldBe "春野さくら"
            }

            test("should handle emoji in name") {
                val user =
                    User(
                        userId = "user-emoji",
                        name = "Cool User 😎🎉",
                    )

                user.name shouldContain "😎"
                user.name shouldContain "🎉"
            }

            test("should handle emoji in email") {
                val user =
                    User(
                        userId = "user-emoji-mail",
                        mail = "test😊@example.com",
                    )

                user.mail shouldContain "😊"
            }

            test("should handle whitespace in fields") {
                val user =
                    User(
                        userId = "user-whitespace",
                        vorname = "  John  ",
                        nachname = "  Doe  ",
                        name = "  John Doe  ",
                        mail = "  john@example.com  ",
                    )

                user.vorname shouldBe "  John  "
                user.nachname shouldBe "  Doe  "
                user.name shouldBe "  John Doe  "
                user.mail shouldBe "  john@example.com  "
            }

            test("should handle only whitespace") {
                val user =
                    User(
                        userId = "user-only-ws",
                        vorname = "   ",
                        nachname = "\t\n",
                        name = "    ",
                    )

                user.vorname shouldBe "   "
                user.nachname shouldBe "\t\n"
                user.name shouldBe "    "
            }

            test("should handle special characters in name") {
                val user =
                    User(
                        userId = "user-special",
                        vorname = "O'Connor",
                        nachname = "Smith-Jones",
                        name = "O'Connor Smith-Jones (Jr.)",
                    )

                user.vorname shouldBe "O'Connor"
                user.nachname shouldBe "Smith-Jones"
                user.name shouldContain "(Jr.)"
            }

            test("should handle SQL injection attempt in name") {
                val user =
                    User(
                        userId = "user-sql",
                        name = "'; DROP TABLE users; --",
                    )

                user.name shouldBe "'; DROP TABLE users; --"
            }

            test("should handle XSS attempt in name") {
                val user =
                    User(
                        userId = "user-xss",
                        name = "<script>alert('XSS')</script>",
                    )

                user.name shouldBe "<script>alert('XSS')</script>"
            }

            test("should handle very long strings") {
                val longString = "a".repeat(1000)
                val user =
                    User(
                        userId = "user-long",
                        name = longString,
                    )

                user.name shouldBe longString
            }

            test("should handle newlines in name") {
                val user =
                    User(
                        userId = "user-newline",
                        name = "First Line\nSecond Line\nThird Line",
                    )

                user.name shouldContain "\n"
            }

            test("should handle tabs in name") {
                val user =
                    User(
                        userId = "user-tab",
                        name = "Column1\tColumn2\tColumn3",
                    )

                user.name shouldContain "\t"
            }

            test("should handle unicode combining characters") {
                val user =
                    User(
                        userId = "user-combining",
                        name = "José María", // e with acute, i with acute
                    )

                user.name shouldBe "José María"
            }

            test("should handle right-to-left text") {
                val user =
                    User(
                        userId = "user-rtl",
                        name = "مرحبا", // Arabic for "hello"
                    )

                user.name shouldBe "مرحبا"
            }

            test("should handle Chinese characters") {
                val user =
                    User(
                        userId = "user-chinese",
                        name = "李明",
                    )

                user.name shouldBe "李明"
            }

            test("should handle Korean characters") {
                val user =
                    User(
                        userId = "user-korean",
                        name = "김철수",
                    )

                user.name shouldBe "김철수"
            }
        }

        context("User Fields - Email Edge Cases") {
            test("should handle standard email format") {
                val user =
                    User(
                        userId = "user-email",
                        mail = "user@example.com",
                    )

                user.mail shouldBe "user@example.com"
            }

            test("should handle email with subdomain") {
                val user =
                    User(
                        userId = "user-subdomain",
                        mail = "user@mail.example.com",
                    )

                user.mail shouldBe "user@mail.example.com"
            }

            test("should handle email with plus addressing") {
                val user =
                    User(
                        userId = "user-plus",
                        mail = "user+tag@example.com",
                    )

                user.mail shouldBe "user+tag@example.com"
            }

            test("should handle email with dots") {
                val user =
                    User(
                        userId = "user-dots",
                        mail = "first.last@example.com",
                    )

                user.mail shouldBe "first.last@example.com"
            }

            test("should handle very long email") {
                val longEmail = "a".repeat(240) + "@example.com"
                val user =
                    User(
                        userId = "user-long-email",
                        mail = longEmail,
                    )

                user.mail shouldBe longEmail
            }

            test("should handle international email") {
                val user =
                    User(
                        userId = "user-intl-email",
                        mail = "用户@例え.jp",
                    )

                user.mail shouldBe "用户@例え.jp"
            }
        }

        context("User Fields - Role Validation") {
            test("should accept USER role") {
                val user =
                    User(
                        userId = "user-role",
                        role = "USER",
                    )

                user.role shouldBe "USER"
            }

            test("should accept ADMIN role") {
                val user =
                    User(
                        userId = "admin-role",
                        role = "ADMIN",
                    )

                user.role shouldBe "ADMIN"
            }

            test("should accept ANONYMOUS role") {
                val user =
                    User(
                        userId = "anon-role",
                        role = "ANONYMOUS",
                    )

                user.role shouldBe "ANONYMOUS"
            }

            test("should allow custom role string") {
                val user =
                    User(
                        userId = "custom-role",
                        role = "CUSTOM_ROLE",
                    )

                user.role shouldBe "CUSTOM_ROLE"
            }

            test("should allow empty role") {
                val user =
                    User(
                        userId = "empty-role",
                        role = "",
                    )

                user.role shouldBe ""
            }

            test("should allow null role") {
                val user =
                    User(
                        userId = "null-role",
                        role = null,
                    )

                user.role shouldBe null
            }
        }

        context("User Fields - External Identifier") {
            test("should handle Auth0 format") {
                val user =
                    User(
                        userId = "user-auth0",
                        externalIdentifier = "auth0|123456789",
                    )

                user.externalIdentifier shouldBe "auth0|123456789"
            }

            test("should handle Google OAuth format") {
                val user =
                    User(
                        userId = "user-google",
                        externalIdentifier = "google-oauth2|123456789",
                    )

                user.externalIdentifier shouldBe "google-oauth2|123456789"
            }

            test("should handle Firebase format") {
                val user =
                    User(
                        userId = "user-firebase",
                        externalIdentifier = "firebase|abc123def456",
                    )

                user.externalIdentifier shouldBe "firebase|abc123def456"
            }

            test("should handle empty external identifier") {
                val user =
                    User(
                        userId = "user-empty-ext",
                        externalIdentifier = "",
                    )

                user.externalIdentifier shouldBe ""
            }

            test("should handle null external identifier") {
                val user =
                    User(
                        userId = "user-null-ext",
                        externalIdentifier = null,
                    )

                user.externalIdentifier shouldBe null
            }
        }

        context("User Entity - Mutability") {
            test("should allow updating vorname") {
                val user = User(userId = "user-update")
                user.vorname = "Updated"

                user.vorname shouldBe "Updated"
            }

            test("should allow updating nachname") {
                val user = User(userId = "user-update")
                user.nachname = "Updated"

                user.nachname shouldBe "Updated"
            }

            test("should allow updating name") {
                val user = User(userId = "user-update")
                user.name = "Updated Name"

                user.name shouldBe "Updated Name"
            }

            test("should allow updating mail") {
                val user = User(userId = "user-update")
                user.mail = "updated@example.com"

                user.mail shouldBe "updated@example.com"
            }

            test("should allow updating role") {
                val user = User(userId = "user-update", role = "USER")
                user.role = "ADMIN"

                user.role shouldBe "ADMIN"
            }

            test("should allow updating external identifier") {
                val user = User(userId = "user-update")
                user.externalIdentifier = "auth0|updated"

                user.externalIdentifier shouldBe "auth0|updated"
            }

            test("should allow updating userId") {
                val user = User(userId = "old-id")
                user.userId = "new-id"

                user.userId shouldBe "new-id"
            }
        }
    })
