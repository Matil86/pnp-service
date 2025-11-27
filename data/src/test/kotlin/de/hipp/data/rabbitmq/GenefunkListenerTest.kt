package de.hipp.data.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.data.config.GameConfiguration
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.entity.CharacterSpeciesEntity
import de.hipp.pnp.base.entity.GameBooks
import de.hipp.pnp.base.entity.GeneFunkClass
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

class GenefunkListenerTest :
    FunSpec({
        lateinit var mapper: ObjectMapper
        lateinit var connectionFactory: ConnectionFactory
        lateinit var connection: Connection
        lateinit var channel: Channel
        lateinit var gameConfiguration: GameConfiguration

        beforeEach {
            mapper = ObjectMapper()
            connectionFactory = mockk(relaxed = true)
            connection = mockk(relaxed = true)
            channel = mockk(relaxed = true)
            gameConfiguration = GameConfiguration()

            every { connectionFactory.createConnection() } returns connection
            every { connection.createChannel(true) } returns channel
            every { channel.queueDeclare(any(), any(), any(), any(), any()) } returns mockk()
        }

        context("Initialization and queue declaration") {
            test("should create GenefunkListener and declare queues") {
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                listener.shouldNotBe(null)
                verify(exactly = 1) { connectionFactory.createConnection() }
                verify(exactly = 1) { connection.createChannel(true) }
                verify(exactly = 1) {
                    channel.queueDeclare(
                        RoutingKeys.GET_GENEFUNK_CLASSES,
                        false,
                        false,
                        true,
                        null,
                    )
                }
                verify(exactly = 1) {
                    channel.queueDeclare(
                        RoutingKeys.GET_GENEFUNK_SPECIES,
                        false,
                        false,
                        true,
                        null,
                    )
                }
            }

            test("should handle queue declaration with empty configuration") {
                gameConfiguration.books = emptyList()

                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                listener.shouldNotBe(null)
                verify(exactly = 2) { channel.queueDeclare(any(), any(), any(), any(), any()) }
            }

            test("should declare queues with correct parameters") {
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                listener.shouldNotBe(null)
                // Verify non-durable, non-exclusive, auto-delete queues
                verify {
                    channel.queueDeclare(
                        RoutingKeys.GET_GENEFUNK_CLASSES,
                        false, // not durable
                        false, // not exclusive
                        true, // auto-delete
                        null, // no arguments
                    )
                }
            }
        }

        context("getGenefunkClasses - basic functionality") {
            test("should return empty classes when no books configured") {
                gameConfiguration.books = emptyList()
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("action")
                result.shouldContain("finished")
                result.shouldContain("payload")
            }

            test("should return classes from single book") {
                val classMap =
                    mapOf(
                        "Engineer" to GeneFunkClass(label = "Engineer", description = "Tech expert"),
                        "Samurai" to GeneFunkClass(label = "Samurai", description = "Warrior"),
                    )
                val book = GameBooks(name = "Core Rulebook", classes = classMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("Engineer")
                result.shouldContain("Samurai")
                result.shouldContain("finished")
            }

            test("should return classes from multiple books") {
                val book1 =
                    GameBooks(
                        name = "Book 1",
                        classes =
                            mapOf(
                                "Engineer" to GeneFunkClass(label = "Engineer", description = "Tech"),
                            ),
                    )
                val book2 =
                    GameBooks(
                        name = "Book 2",
                        classes =
                            mapOf(
                                "Samurai" to GeneFunkClass(label = "Samurai", description = "Warrior"),
                            ),
                    )
                gameConfiguration.books = listOf(book1, book2)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("Engineer")
                result.shouldContain("Samurai")
            }

            test("should handle duplicate classes across books using distinct") {
                val sharedClass =
                    mapOf(
                        "Engineer" to GeneFunkClass(label = "Engineer", description = "Tech"),
                    )
                val book1 = GameBooks(name = "Book 1", classes = sharedClass)
                val book2 = GameBooks(name = "Book 2", classes = sharedClass)
                gameConfiguration.books = listOf(book1, book2)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                // Should only contain Engineer once due to distinct()
                result.shouldContain("Engineer")
            }
        }

        context("getGenefunkClasses - string input handling") {
            test("should handle class names with unicode characters") {
                val classMap =
                    mapOf(
                        "忍者" to GeneFunkClass(label = "忍者", description = "Ninja in Japanese"),
                        "侍" to GeneFunkClass(label = "侍", description = "Samurai in Japanese"),
                    )
                val book = GameBooks(name = "Japanese Edition", classes = classMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("忍者")
                result.shouldContain("侍")
            }

            test("should handle class names with emojis") {
                val classMap =
                    mapOf(
                        "Hacker🤓" to GeneFunkClass(label = "Hacker🤓", description = "Cyber expert"),
                        "Warrior⚔️" to GeneFunkClass(label = "Warrior⚔️", description = "Fighter"),
                    )
                val book = GameBooks(name = "Fun Edition", classes = classMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("Hacker🤓")
                result.shouldContain("Warrior⚔️")
            }

            test("should handle class names with special characters") {
                val classMap =
                    mapOf(
                        "D&D Wizard" to GeneFunkClass(label = "D&D Wizard", description = "Magic user"),
                        "Street Sam' (Modified)" to GeneFunkClass(label = "Street Sam' (Modified)", description = "Modified"),
                    )
                val book = GameBooks(name = "Special Edition", classes = classMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("D&D Wizard")
                result.shouldContain("Street Sam' (Modified)")
            }

            test("should handle very long class names") {
                val longName = "This is an incredibly long class name that someone created for testing purposes".repeat(5)
                val classMap =
                    mapOf(
                        longName to GeneFunkClass(label = longName, description = "Long name class"),
                    )
                val book = GameBooks(name = "Verbose Edition", classes = classMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain(longName)
            }
        }

        context("getGenefunkGenomes - basic functionality") {
            test("should return empty species when no books configured") {
                gameConfiguration.books = emptyList()
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("action")
                result.shouldContain("finished")
                result.shouldContain("payload")
            }

            test("should return species from single book") {
                val species1 =
                    CharacterSpeciesEntity(
                        name = "Human",
                        description = "Standard human",
                    )
                val species2 =
                    CharacterSpeciesEntity(
                        name = "Elf",
                        description = "Elegant elf",
                    )
                val book = GameBooks(name = "Core Rulebook", species = listOf(species1, species2))
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("Human")
                result.shouldContain("Elf")
                result.shouldContain("finished")
            }

            test("should return species from multiple books and flatten") {
                val human = CharacterSpeciesEntity(name = "Human")
                val elf = CharacterSpeciesEntity(name = "Elf")
                val dwarf = CharacterSpeciesEntity(name = "Dwarf")

                val book1 = GameBooks(name = "Book 1", species = listOf(human, elf))
                val book2 = GameBooks(name = "Book 2", species = listOf(dwarf))
                gameConfiguration.books = listOf(book1, book2)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("Human")
                result.shouldContain("Elf")
                result.shouldContain("Dwarf")
            }

            test("should handle duplicate species across books using distinct") {
                val human = CharacterSpeciesEntity(name = "Human")
                val book1 = GameBooks(name = "Book 1", species = listOf(human))
                val book2 = GameBooks(name = "Book 2", species = listOf(human))
                gameConfiguration.books = listOf(book1, book2)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("Human")
            }
        }

        context("getGenefunkGenomes - string input handling") {
            test("should handle species names with unicode characters") {
                val species1 =
                    CharacterSpeciesEntity(
                        name = "人間",
                        description = "Human in Japanese",
                    )
                val species2 =
                    CharacterSpeciesEntity(
                        name = "エルフ",
                        description = "Elf in Japanese",
                    )
                val book = GameBooks(name = "Japanese Edition", species = listOf(species1, species2))
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("人間")
                result.shouldContain("エルフ")
            }

            test("should handle species names with emojis") {
                val species1 =
                    CharacterSpeciesEntity(
                        name = "Dragon🐉",
                        description = "Fire breather",
                    )
                val species2 =
                    CharacterSpeciesEntity(
                        name = "Robot🤖",
                        description = "Mechanical being",
                    )
                val book = GameBooks(name = "Fun Edition", species = listOf(species1, species2))
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("Dragon🐉")
                result.shouldContain("Robot🤖")
            }

            test("should handle species names with special characters") {
                val species1 =
                    CharacterSpeciesEntity(
                        name = "Half-Elf (Modified)",
                        description = "Mixed heritage",
                    )
                val species2 =
                    CharacterSpeciesEntity(
                        name = "Dwarf 'Stonecutter'",
                        description = "Rock dweller",
                    )
                val book = GameBooks(name = "Special Edition", species = listOf(species1, species2))
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("Half-Elf (Modified)")
                result.shouldContain("Dwarf 'Stonecutter'")
            }

            test("should handle empty species list") {
                val book = GameBooks(name = "Empty Book", species = emptyList())
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
            }
        }

        context("JSON serialization") {
            test("should serialize classes response to valid JSON") {
                val classMap =
                    mapOf(
                        "Engineer" to GeneFunkClass(label = "Engineer", description = "Tech expert"),
                    )
                val book = GameBooks(name = "JSON Test Book", classes = classMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                // Should be valid JSON
                val parsed = mapper.readTree(result)
                parsed.shouldNotBe(null)
                parsed.has("action") shouldBe true
                parsed.has("payload") shouldBe true
                parsed.get("action").asText() shouldBe "finished"
            }

            test("should serialize species response to valid JSON") {
                val species = CharacterSpeciesEntity(name = "Human")
                val book = GameBooks(name = "JSON Test Book", species = listOf(species))
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                // Should be valid JSON
                val parsed = mapper.readTree(result)
                parsed.shouldNotBe(null)
                parsed.has("action") shouldBe true
                parsed.has("payload") shouldBe true
                parsed.get("action").asText() shouldBe "finished"
            }

            test("should use pretty printer for JSON output") {
                gameConfiguration.books = emptyList()
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                // Pretty printed JSON should contain newlines
                result.shouldContain("\n")
            }
        }

        context("Edge cases") {
            test("should handle large number of classes") {
                val largeClassMap =
                    (1..1000).associate {
                        "Class$it" to GeneFunkClass(label = "Class$it", description = "Description $it")
                    }
                val book = GameBooks(name = "Large Book", classes = largeClassMap)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("Class1")
                result.shouldContain("Class1000")
            }

            test("should handle large number of species") {
                val largeSpeciesList =
                    (1..1000).map {
                        CharacterSpeciesEntity(
                            name = "Species$it",
                            description = "Description $it",
                        )
                    }
                val book = GameBooks(name = "Large Book", species = largeSpeciesList)
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkGenomes()

                result.shouldNotBeEmpty()
                result.shouldContain("Species1")
                result.shouldContain("Species1000")
            }

            test("should handle books with empty classes map") {
                val book = GameBooks(name = "Empty Classes Book", classes = emptyMap())
                gameConfiguration.books = listOf(book)
                val listener = GenefunkListener(mapper, connectionFactory, gameConfiguration)

                val result = listener.getGenefunkClasses()

                result.shouldNotBeEmpty()
                result.shouldContain("finished")
            }
        }
    })
