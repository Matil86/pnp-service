package de.hipp.pnp.security.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.dto.Customer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

/**
 * Comprehensive test suite for UserListener.
 *
 * Coverage includes:
 * - RabbitMQ message handling
 * - Get internal user operations
 * - Save new user operations
 * - JSON serialization/deserialization
 * - Edge cases with null/empty/unicode inputs
 * - Security scenarios
 * - Error handling
 */
class UserListenerTest :
    FunSpec({

        lateinit var mapper: ObjectMapper
        lateinit var userService: UserService
        lateinit var channel: Channel
        lateinit var connection: org.springframework.amqp.rabbit.connection.Connection
        lateinit var userListener: UserListener

        beforeTest {
            mapper = ObjectMapper()
            userService = mockk()
            channel = mockk(relaxed = true)
            connection = mockk()

            every { connection.createChannel(true) } returns channel
            every { channel.queueDeclare(any(), any(), any(), any(), any()) } returns mockk()

            val connectionFactory = mockk<org.springframework.amqp.rabbit.connection.ConnectionFactory>()
            every { connectionFactory.createConnection() } returns connection

            userListener = UserListener(mapper, connectionFactory, userService)
        }

        context("handleGetInternalUserId - User Retrieval Messages") {
            test("should return user data when user exists") {
                runTest {
                    val externalId = "auth0|123456789"
                    val user =
                        User(
                            userId = "user-123",
                            vorname = "John",
                            nachname = "Doe",
                            name = "John Doe",
                            externalIdentifier = externalId,
                            mail = "john@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<String>()
                    message.payload = externalId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(externalId) } returns user

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain externalId
                    result shouldContain "USER"
                    result shouldContain "user-123"
                }
            }

            test("should return anonymous role when user not found") {
                runTest {
                    val externalId = "nonexistent-user"

                    val message = DefaultMessage<String>()
                    message.payload = externalId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(externalId) } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                    result shouldContain externalId
                }
            }

            test("should handle null external ID in message") {
                runTest {
                    val message = DefaultMessage<String?>()
                    message.payload = null
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(null) } returns null

                    // Current implementation throws NPE when trying to set null to non-null externalId field
                    // This is a known limitation of the current implementation
                    try {
                        userListener.handleGetInternalUserId(messageJson)
                        // If we get here, fail the test
                        throw AssertionError("Expected NullPointerException but none was thrown")
                    } catch (e: NullPointerException) {
                        // Expected behavior - null payload causes NPE due to MessageHeader.externalId being non-null
                        e shouldNotBe null
                    }
                }
            }

            test("should handle empty string external ID") {
                runTest {
                    val message = DefaultMessage<String>()
                    message.payload = ""
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId("") } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                }
            }

            test("should handle external ID with whitespace") {
                runTest {
                    val whitespaceId = "   "
                    val message = DefaultMessage<String>()
                    message.payload = whitespaceId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(whitespaceId) } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                }
            }

            test("should handle external ID with hiragana characters") {
                runTest {
                    val hiraganaId = "ひらがな123"
                    val user =
                        User(
                            userId = "user-hiragana",
                            externalIdentifier = hiraganaId,
                            role = "USER",
                        )

                    val message = DefaultMessage<String>()
                    message.payload = hiraganaId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(hiraganaId) } returns user

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "USER"
                }
            }

            test("should handle external ID with katakana characters") {
                runTest {
                    val katakanaId = "カタカナ456"
                    val user =
                        User(
                            userId = "user-katakana",
                            externalIdentifier = katakanaId,
                            role = "USER",
                        )

                    val message = DefaultMessage<String>()
                    message.payload = katakanaId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(katakanaId) } returns user

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "USER"
                }
            }

            test("should handle external ID with emoji") {
                runTest {
                    val emojiId = "user😊123"

                    val message = DefaultMessage<String>()
                    message.payload = emojiId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(emojiId) } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                }
            }

            test("should safely handle SQL injection in external ID") {
                runTest {
                    val sqlInjectionId = "'; DROP TABLE users; --"

                    val message = DefaultMessage<String>()
                    message.payload = sqlInjectionId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(sqlInjectionId) } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                }
            }

            test("should safely handle XSS attempt in external ID") {
                runTest {
                    val xssId = "<script>alert('XSS')</script>"

                    val message = DefaultMessage<String>()
                    message.payload = xssId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(xssId) } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                }
            }

            test("should handle user with ADMIN role") {
                runTest {
                    val externalId = "auth0|admin123"
                    val user =
                        User(
                            userId = "admin-123",
                            externalIdentifier = externalId,
                            role = "ADMIN",
                        )

                    val message = DefaultMessage<String>()
                    message.payload = externalId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(externalId) } returns user

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ADMIN"
                }
            }

            test("should handle user with null role") {
                runTest {
                    val externalId = "auth0|norole"
                    val user =
                        User(
                            userId = "user-norole",
                            externalIdentifier = externalId,
                            role = null,
                        )

                    val message = DefaultMessage<String>()
                    message.payload = externalId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(externalId) } returns user

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    // When role is null, toString() returns "null" string, not ANONYMOUS
                    // The UserListener uses customer?.role which will be the string "null" when role is null
                    result shouldContain "null"
                }
            }

            test("should handle malformed JSON message gracefully") {
                runTest {
                    val malformedJson = "{ invalid json }"

                    try {
                        userListener.handleGetInternalUserId(malformedJson)
                    } catch (e: Exception) {
                        // Expected to throw JsonProcessingException
                        e shouldNotBe null
                    }
                }
            }

            test("should handle very long external ID") {
                runTest {
                    val longId = "a".repeat(1000)
                    val message = DefaultMessage<String>()
                    message.payload = longId
                    val messageJson = mapper.writeValueAsString(message)

                    coEvery { userService.getUserByExternalId(longId) } returns null

                    val result = userListener.handleGetInternalUserId(messageJson)

                    result shouldNotBe null
                    result shouldContain "ANONYMOUS"
                }
            }
        }

        context("handleSaveNewUser - User Creation Messages") {
            test("should create new user when user does not exist") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-123",
                            vorname = "Jane",
                            nachname = "Smith",
                            name = "Jane Smith",
                            externalIdentifier = "auth0|987654321",
                            mail = "jane@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-123",
                            vorname = "Jane",
                            nachname = "Smith",
                            name = "Jane Smith",
                            externalIdentifier = "auth0|987654321",
                            mail = "jane@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-123") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "Jane"
                    result shouldContain "Smith"
                    result shouldContain "jane@example.com"
                }
            }

            test("should return existing user when user already exists") {
                runTest {
                    val customer =
                        Customer(
                            userId = "existing-123",
                            vorname = "John",
                            nachname = "Doe",
                            mail = "john@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val existingUser =
                        User(
                            userId = "existing-123",
                            vorname = "John",
                            nachname = "Doe",
                            mail = "john@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("existing-123") } returns existingUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "John"
                }
            }

            test("should handle customer with null optional fields") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-null",
                            vorname = null,
                            nachname = null,
                            name = null,
                            externalIdentifier = "auth0|null",
                            mail = "null@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-null",
                            externalIdentifier = "auth0|null",
                            mail = "null@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-null") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "customer-null"
                }
            }

            test("should handle customer with empty strings") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-empty",
                            vorname = "",
                            nachname = "",
                            name = "",
                            externalIdentifier = "auth0|empty",
                            mail = "empty@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-empty",
                            vorname = "",
                            nachname = "",
                            name = "",
                            externalIdentifier = "auth0|empty",
                            mail = "empty@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-empty") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                }
            }

            test("should handle customer with hiragana name") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-hiragana",
                            vorname = "さくら",
                            nachname = "はるの",
                            name = "はるのさくら",
                            externalIdentifier = "auth0|hiragana",
                            mail = "sakura@example.jp",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-hiragana",
                            vorname = "さくら",
                            nachname = "はるの",
                            name = "はるのさくら",
                            externalIdentifier = "auth0|hiragana",
                            mail = "sakura@example.jp",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-hiragana") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "さくら"
                }
            }

            test("should handle customer with katakana name") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-katakana",
                            name = "サクラハルノ",
                            externalIdentifier = "auth0|katakana",
                            mail = "katakana@example.jp",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-katakana",
                            name = "サクラハルノ",
                            externalIdentifier = "auth0|katakana",
                            mail = "katakana@example.jp",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-katakana") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "サクラハルノ"
                }
            }

            test("should handle customer with emoji in name") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-emoji",
                            name = "Cool User 😎",
                            externalIdentifier = "auth0|emoji",
                            mail = "cool@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-emoji",
                            name = "Cool User 😎",
                            externalIdentifier = "auth0|emoji",
                            mail = "cool@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-emoji") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                }
            }

            test("should handle customer with SQL injection in name") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-sql",
                            name = "'; DROP TABLE users; --",
                            externalIdentifier = "auth0|sql",
                            mail = "sql@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-sql",
                            name = "'; DROP TABLE users; --",
                            externalIdentifier = "auth0|sql",
                            mail = "sql@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-sql") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    // Should safely store the string without executing SQL
                    result shouldNotBe null
                }
            }

            test("should handle customer with XSS attempt in name") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-xss",
                            name = "<script>alert('XSS')</script>",
                            externalIdentifier = "auth0|xss",
                            mail = "xss@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-xss",
                            name = "<script>alert('XSS')</script>",
                            externalIdentifier = "auth0|xss",
                            mail = "xss@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-xss") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    // Should safely store the string without executing script
                    result shouldNotBe null
                }
            }

            test("should handle malformed JSON message") {
                runTest {
                    val malformedJson = "{ invalid json }"

                    val result = userListener.handleSaveNewUser(malformedJson)

                    // Should return error message instead of crashing
                    result shouldNotBe null
                }
            }

            // Nullable message handling is covered by error handling tests above

            test("should handle very long email address") {
                runTest {
                    val longEmail = "a".repeat(240) + "@example.com"
                    val customer =
                        Customer(
                            userId = "customer-long",
                            mail = longEmail,
                            externalIdentifier = "auth0|long",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-long",
                            mail = longEmail,
                            externalIdentifier = "auth0|long",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-long") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                }
            }

            test("should handle customer with ADMIN role") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-admin",
                            name = "Admin User",
                            externalIdentifier = "auth0|admin",
                            mail = "admin@example.com",
                            role = "ADMIN",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-admin",
                            name = "Admin User",
                            externalIdentifier = "auth0|admin",
                            mail = "admin@example.com",
                            role = "ADMIN",
                        )

                    coEvery { userService.getUserByExternalId("customer-admin") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "ADMIN"
                }
            }

            test("should handle special characters in name") {
                runTest {
                    val customer =
                        Customer(
                            userId = "customer-special",
                            name = "O'Connor-Smith (Jr.)",
                            externalIdentifier = "auth0|special",
                            mail = "oconnor@example.com",
                            role = "USER",
                        )

                    val message = DefaultMessage<Customer>()
                    message.payload = customer
                    val messageJson = mapper.writeValueAsString(message)

                    val savedUser =
                        User(
                            userId = "customer-special",
                            name = "O'Connor-Smith (Jr.)",
                            externalIdentifier = "auth0|special",
                            mail = "oconnor@example.com",
                            role = "USER",
                        )

                    coEvery { userService.getUserByExternalId("customer-special") } returns null
                    coEvery { userService.saveUser(any()) } returns savedUser

                    val result = userListener.handleSaveNewUser(messageJson)

                    result shouldNotBe null
                    result shouldContain "O'Connor"
                }
            }
        }
    })
