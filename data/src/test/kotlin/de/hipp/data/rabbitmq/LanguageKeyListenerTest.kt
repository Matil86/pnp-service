package de.hipp.data.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.data.config.LocalizationProperties
import de.hipp.pnp.api.dto.LanguageRequest
import de.hipp.pnp.api.locale.SystemLocale
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory

class LanguageKeyListenerTest :
    FunSpec({
        lateinit var mapper: ObjectMapper
        lateinit var connectionFactory: ConnectionFactory
        lateinit var connection: Connection
        lateinit var channel: Channel
        lateinit var localizationProperties: LocalizationProperties

        beforeEach {
            mapper = ObjectMapper()
            connectionFactory = mockk(relaxed = true)
            connection = mockk(relaxed = true)
            channel = mockk(relaxed = true)
            localizationProperties = LocalizationProperties()

            every { connectionFactory.createConnection() } returns connection
            every { connection.createChannel(true) } returns channel
            every { channel.queueDeclare(any(), any(), any(), any(), any()) } returns mockk()
        }

        context("Initialization and queue declaration") {
            test("should create LanguageKeyListener and declare queues") {
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                listener.shouldNotBe(null)
                verify(exactly = 1) { connectionFactory.createConnection() }
                verify(exactly = 1) { connection.createChannel(true) }
                verify(exactly = 2) { channel.queueDeclare(any(), any(), any(), any(), any()) }
            }

            test("should declare both language key queues") {
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                listener.shouldNotBe(null)
                // Verify both queues are declared
                verify(exactly = 2) {
                    channel.queueDeclare(
                        any(),
                        false, // not durable
                        false, // not exclusive
                        true, // auto-delete
                        null,
                    )
                }
            }

            test("should handle initialization with empty localization properties") {
                localizationProperties.systems = emptyMap()

                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                listener.shouldNotBe(null)
            }
        }

        context("getAllLanguageKeys - basic functionality") {
            test("should return empty language keys when no systems configured") {
                localizationProperties.systems = emptyMap()
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("action")
                result.shouldContain("finished")
                result.shouldContain("payload")
            }

            test("should return language keys for single system") {
                val systemLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to systemLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
                result.shouldContain("payload")
                result.shouldContain("books")
            }

            test("should return language keys for multiple systems") {
                val genefunkLocale = SystemLocale()
                val dnd5eLocale = SystemLocale()
                localizationProperties.systems =
                    mapOf(
                        "genefunk" to genefunkLocale,
                        "dnd5e" to dnd5eLocale,
                    )
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
                result.shouldContain("payload")
                // Verify we have an array with 2 elements in the parsed JSON
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 2
            }

            test("should return all system values without keys") {
                val systems =
                    mapOf(
                        "game1" to SystemLocale(),
                        "game2" to SystemLocale(),
                        "game3" to SystemLocale(),
                    )
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                // Parse JSON to verify structure
                val parsed = mapper.readTree(result)
                parsed.has("payload") shouldBe true
                parsed.get("payload").isArray shouldBe true
            }
        }

        context("getAllLanguageKeys - string input handling") {
            test("should handle system names with unicode characters") {
                val systems =
                    mapOf(
                        "遊戲系統" to SystemLocale(),
                        "게임" to SystemLocale(),
                        "игра" to SystemLocale(),
                    )
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 3
            }

            test("should handle system names with emojis") {
                val systems =
                    mapOf(
                        "genefunk🧬" to SystemLocale(),
                        "fantasy🐉" to SystemLocale(),
                        "scifi🚀" to SystemLocale(),
                    )
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 3
            }

            test("should handle system names with special characters") {
                val systems =
                    mapOf(
                        "D&D 5e" to SystemLocale(),
                        "Star Wars (FFG)" to SystemLocale(),
                        "Cyberpunk-2077" to SystemLocale(),
                    )
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 3
            }

            test("should handle very long system names") {
                val longName = "extremely_long_system_name_for_testing_purposes_that_goes_on_and_on".repeat(5)
                val systems = mapOf(longName to SystemLocale())
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
            }
        }

        context("getAllLanguageKeysByGameTypeAndLanguage - basic functionality") {
            test("should return language keys for valid game type") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0) // GENEFUNK = 0
                val messageJson = mapper.writeValueAsString(request)

                val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
            }

            test("should throw exception when game type not found in localization properties") {
                val dnd5eLocale = SystemLocale()
                localizationProperties.systems = mapOf("dnd5e" to dnd5eLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0) // GENEFUNK not in properties
                val messageJson = mapper.writeValueAsString(request)

                shouldThrow<IllegalArgumentException> {
                    listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                }
            }

            test("should handle message with null payload") {
                localizationProperties.systems = mapOf("genefunk" to SystemLocale())
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val messageJson =
                    """
                    {
                        "action": "getLanguageKeys",
                        "payload": null
                    }
                    """.trimIndent()

                shouldThrow<Exception> {
                    listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                }
            }

            test("should convert game type to lowercase for lookup") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)

                // Should find "genefunk" (lowercase) in the map
                result.shouldContain("finished")
            }
        }

        context("getAllLanguageKeysByGameTypeAndLanguage - string input handling") {
            test("should handle locale with underscore format") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)

                result.shouldNotBeEmpty()
            }

            test("should handle locale with hyphen format") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en-US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)

                result.shouldNotBeEmpty()
            }

            test("should handle various locales") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val locales = listOf("de_DE", "fr_FR", "ja_JP", "ko_KR", "zh_CN", "es_ES")

                locales.forEach { locale ->
                    val request = DefaultMessage<LanguageRequest>()
                    request.payload = LanguageRequest(locale = locale, gameType = 0)
                    val messageJson = mapper.writeValueAsString(request)

                    val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                    result.shouldNotBeEmpty()
                }
            }

            test("should handle message with unicode in JSON") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                // Create message manually with unicode
                val messageJson =
                    """
                    {
                        "action": "getLanguageKeys",
                        "payload": {
                            "locale": "ja_JP",
                            "gameType": 0
                        }
                    }
                    """.trimIndent()

                val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)

                result.shouldNotBeEmpty()
            }
        }

        context("getAllLanguageKeysByGameTypeAndLanguage - error handling") {
            test("should throw IllegalArgumentException with descriptive message when game not found") {
                localizationProperties.systems = mapOf("dnd5e" to SystemLocale())
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                val exception =
                    shouldThrow<IllegalArgumentException> {
                        listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                    }

                exception.message.shouldNotBe(null)
                exception.message shouldContain "genefunk"
                exception.message shouldContain "not found"
            }

            test("should handle empty systems map gracefully") {
                localizationProperties.systems = emptyMap()
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                shouldThrow<IllegalArgumentException> {
                    listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                }
            }

            test("should handle malformed JSON gracefully") {
                localizationProperties.systems = mapOf("genefunk" to SystemLocale())
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val malformedJson = "{ invalid json }"

                shouldThrow<Exception> {
                    listener.getAllLanguageKeysByGameTypeAndLanguage(malformedJson)
                }
            }

            test("should handle empty JSON string") {
                localizationProperties.systems = mapOf("genefunk" to SystemLocale())
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val emptyJson = ""

                shouldThrow<Exception> {
                    listener.getAllLanguageKeysByGameTypeAndLanguage(emptyJson)
                }
            }
        }

        context("JSON serialization") {
            test("should serialize getAllLanguageKeys response to valid JSON") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                val parsed = mapper.readTree(result)
                parsed.shouldNotBe(null)
                parsed.has("action") shouldBe true
                parsed.has("payload") shouldBe true
                parsed.get("action").asText() shouldBe "finished"
                parsed.get("payload").isArray shouldBe true
            }

            test("should serialize getAllLanguageKeysByGameTypeAndLanguage response to valid JSON") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                val result = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)

                val parsed = mapper.readTree(result)
                parsed.shouldNotBe(null)
                parsed.has("action") shouldBe true
                parsed.has("payload") shouldBe true
                parsed.get("action").asText() shouldBe "finished"
            }

            test("should use pretty printer for getAllLanguageKeys output") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                // Pretty printed JSON should contain newlines
                result.shouldContain("\n")
            }

            test("should serialize complex SystemLocale objects correctly") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                // Verify it's valid JSON
                val parsed = mapper.readTree(result)
                parsed.shouldNotBe(null)
            }
        }

        context("Edge cases") {
            test("should handle large number of systems") {
                val largeSystems =
                    (1..1000).associate {
                        "game$it" to SystemLocale()
                    }
                localizationProperties.systems = largeSystems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 1000
            }

            test("should handle system names with mixed case") {
                val systems =
                    mapOf(
                        "GeneFunk" to SystemLocale(),
                        "GENEFUNK" to SystemLocale(),
                        "genefunk" to SystemLocale(),
                    )
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 3
            }

            test("should handle repeated calls to getAllLanguageKeys") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                // Call multiple times
                val result1 = listener.getAllLanguageKeys()
                val result2 = listener.getAllLanguageKeys()
                val result3 = listener.getAllLanguageKeys()

                // Should return consistent results
                result1 shouldBe result2
                result2 shouldBe result3
            }

            test("should reject message with unrecognized fields in payload") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val messageJson =
                    """
                    {
                        "action": "getLanguageKeys",
                        "payload": {
                            "locale": "en_US",
                            "gameType": 0,
                            "extraField": "unrecognized field"
                        }
                    }
                    """.trimIndent()

                // Jackson by default rejects unrecognized properties
                shouldThrow<Exception> {
                    listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                }
            }
        }

        context("Real-world scenarios") {
            test("should handle typical RPG system requests") {
                val systems =
                    mapOf(
                        "genefunk" to SystemLocale(),
                        "dnd5e" to SystemLocale(),
                        "pathfinder2e" to SystemLocale(),
                        "shadowrun" to SystemLocale(),
                    )
                localizationProperties.systems = systems
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                val result = listener.getAllLanguageKeys()

                result.shouldNotBeEmpty()
                val parsed = mapper.readTree(result)
                parsed.get("payload").size() shouldBe 4
            }

            test("should handle request flow: getAllLanguageKeys then getByGameType") {
                val genefunkLocale = SystemLocale()
                localizationProperties.systems = mapOf("genefunk" to genefunkLocale)
                val listener = LanguageKeyListener(mapper, connectionFactory, localizationProperties)

                // First get all keys
                val allKeys = listener.getAllLanguageKeys()
                allKeys.shouldContain("finished")

                // Then get specific game type
                val request = DefaultMessage<LanguageRequest>()
                request.payload = LanguageRequest(locale = "en_US", gameType = 0)
                val messageJson = mapper.writeValueAsString(request)

                val specificKeys = listener.getAllLanguageKeysByGameTypeAndLanguage(messageJson)
                specificKeys.shouldContain("finished")
            }
        }
    })
