package de.hipp.pnp.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.genefunk.GeneFunkCharacter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.amqp.rabbit.core.RabbitTemplate

/**
 * Tests for CharacterProducer.
 *
 * Verifies character generation, retrieval, and deletion through RabbitMQ.
 */
class CharacterProducerTest :
    StringSpec({

        "generate - Goku generates GeneFunk character (gameType 0)" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Goku"
                    lastName = "Son"
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(0)

            result shouldContain "Goku"
            result shouldContain "Son"
        }

        "generate - Spider-Man generates character with gameType 1" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Peter"
                    lastName = "Parker"
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(1)

            result shouldContain "Peter"
            result shouldContain "Parker"
        }

        "generate - Tony Stark verifies JSON serialization" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Tony"
                    lastName = "Stark"
                }

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns character

            val result = producer.generate(0)

            result shouldNotBe null
            result.isNotEmpty() shouldBe true
        }

        "allCharacters - Batman retrieves all characters" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character1 =
                GeneFunkCharacter().apply {
                    firstName = "Bruce"
                    lastName = "Wayne"
                }
            val character2 =
                GeneFunkCharacter().apply {
                    firstName = "Dick"
                    lastName = "Grayson"
                }

            val characterList = mutableListOf(character1, character2)
            // Mock returns a DefaultMessage JSON string with the character list as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(characterList)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val characters = producer.allCharacters()

            characters.size shouldBe 2
        }

        "allCharacters - Wonder Woman retrieves empty list" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val emptyList = mutableListOf<GeneFunkCharacter>()
            // Mock returns a DefaultMessage JSON string with empty list as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(emptyList)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val characters = producer.allCharacters()

            characters.size shouldBe 0
        }

        "deleteCharacter - Naruto (ナルト) deletes character by ID" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns Unit

            producer.deleteCharacter(42)

            verify { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "deleteCharacter - Vegeta deletes character with ID 1" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns Unit

            producer.deleteCharacter(1)

            verify { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "deleteCharacter - Deadpool deletes non-existent character (999)" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns Unit

            producer.deleteCharacter(999)

            verify { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "generate - Hulk generates character with Japanese name (孫悟空)" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "孫"
                    lastName = "悟空"
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(0)

            result shouldContain "孫"
            result shouldContain "悟空"
        }

        "generate - Pikachu (ピカチュウ) generates character with katakana name" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "ピカチュウ"
                    lastName = ""
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(0)

            result shouldContain "ピカチュウ"
        }

        "allCharacters - Gandalf retrieves single character" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Gandalf"
                    lastName = "the Grey"
                }

            val characterList = mutableListOf(character)
            // Mock returns a DefaultMessage JSON string with the character list as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(characterList)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val characters = producer.allCharacters()

            characters.size shouldBe 1
            characters[0] shouldNotBe null
        }

        "generate - Frodo with emoji in character data 🎲⚔️" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Frodo"
                    lastName = "Baggins 🎲"
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(0)

            result shouldContain "🎲"
        }

        "generate - Neo generates character for Matrix game" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Neo"
                    lastName = "Anderson"
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(0)

            result shouldNotBe null
        }

        "allCharacters - Loki retrieves large list of characters" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val characters =
                mutableListOf<GeneFunkCharacter>().apply {
                    repeat(100) { i ->
                        add(
                            GeneFunkCharacter().apply {
                                firstName = "Character$i"
                                lastName = "Test"
                            },
                        )
                    }
                }

            // Mock returns a DefaultMessage JSON string with the character list as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(characters)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.allCharacters()

            result.size shouldBe 100
        }

        "deleteCharacter - Thor deletes character with large ID" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns Unit

            producer.deleteCharacter(999999)

            verify { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "generate - Captain America verifies routing key is used" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character = GeneFunkCharacter()

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns character

            producer.generate(0)

            verify { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "generate - Black Widow generates character with empty name" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = ""
                    lastName = ""
                }

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns character

            val result = producer.generate(0)

            result shouldNotBe null
        }

        "generate - Thanos with complex character data" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            val character =
                GeneFunkCharacter().apply {
                    firstName = "Thanos"
                    lastName = "of Titan"
                }

            // Mock returns a DefaultMessage JSON string with the character as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(character)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.generate(0)

            result shouldContain "Thanos"
            result shouldContain "Titan"
        }

        "deleteCharacter - multiple deletions in sequence" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = CharacterProducer(rabbitTemplate, mapper)

            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns Unit

            producer.deleteCharacter(1)
            producer.deleteCharacter(2)
            producer.deleteCharacter(3)

            verify(exactly = 3) { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }
    })
