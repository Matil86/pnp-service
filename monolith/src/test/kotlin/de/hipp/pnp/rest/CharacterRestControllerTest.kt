package de.hipp.pnp.rest

import de.hipp.pnp.genefunk.GeneFunkCharacter
import de.hipp.pnp.rabbitmq.CharacterProducer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * Tests for CharacterRestController with creative test data
 */
class CharacterRestControllerTest :
    StringSpec({

        fun createMockController(): CharacterRestController {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val goku =
                GeneFunkCharacter().apply {
                    firstName = "Goku"
                    lastName = "Son"
                }

            val tonyStark =
                GeneFunkCharacter().apply {
                    firstName = "Tony"
                    lastName = "Stark"
                }

            every { characterProducer.allCharacters() } returns mutableListOf(goku, tonyStark)
            every { characterProducer.generate(any()) } returns """{"firstName":"Neo","lastName":"Anderson"}"""
            every { characterProducer.deleteCharacter(any()) } returns Unit

            return CharacterRestController(characterProducer, meterRegistry)
        }

        "GET /characters - Goku (孫悟空) and Tony Stark should be returned" {
            val controller = createMockController()

            val characters = controller.allCharacters()

            characters.size shouldBe 2
            (characters[0] as GeneFunkCharacter).firstName shouldBe "Goku"
            (characters[1] as GeneFunkCharacter).firstName shouldBe "Tony"
        }

        "GET /characters/generate - should generate Neo from Matrix" {
            val controller = createMockController()

            val result = controller.generateCharacter(0)

            result shouldContain "Neo"
            result shouldContain "Anderson"
        }

        "GET /characters/generate with gameType 0 - default GeneFunk character" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)
            every { characterProducer.generate(0) } returns """{"firstName":"Spider-Man"}"""

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val result = controller.generateCharacter(0)

            result shouldContain "Spider-Man"
            verify { characterProducer.generate(0) }
        }

        "GET /characters/generate with gameType 1 - different game system" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)
            every { characterProducer.generate(1) } returns """{"firstName":"Wonder Woman"}"""

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val result = controller.generateCharacter(1)

            result shouldContain "Wonder Woman"
            verify { characterProducer.generate(1) }
        }

        "DELETE /characters/1 - Batman should be deleted" {
            val characterProducer = mockk<CharacterProducer>(relaxed = true)
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val controller = CharacterRestController(characterProducer, meterRegistry)
            controller.deleteCharacter(1)

            verify { characterProducer.deleteCharacter(1) }
        }

        "DELETE /characters/999 - Delete non-existent character Gandalf" {
            val characterProducer = mockk<CharacterProducer>(relaxed = true)
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val controller = CharacterRestController(characterProducer, meterRegistry)
            controller.deleteCharacter(999)

            verify { characterProducer.deleteCharacter(999) }
        }

        "GET /characters - Empty list when no characters exist" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)
            every { characterProducer.allCharacters() } returns mutableListOf()

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val characters = controller.allCharacters()

            characters.size shouldBe 0
        }

        "GET /characters/generate - Japanese character name (ナルト)" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)
            every { characterProducer.generate(0) } returns """{"firstName":"ナルト","lastName":"うずまき"}"""

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val result = controller.generateCharacter(0)

            result shouldContain "ナルト"
            result shouldContain "うずまき"
        }

        "GET /characters/generate - Character with emoji in description 🎲⚔️" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)
            every { characterProducer.generate(0) } returns """{"firstName":"Deadpool","description":"🎲⚔️"}"""

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val result = controller.generateCharacter(0)

            result shouldContain "Deadpool"
            result shouldContain "🎲⚔️"
        }

        "DELETE /characters/42 - Delete Pikachu (ピカチュウ)" {
            val characterProducer = mockk<CharacterProducer>(relaxed = true)
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val controller = CharacterRestController(characterProducer, meterRegistry)
            controller.deleteCharacter(42)

            verify { characterProducer.deleteCharacter(42) }
        }

        "GET /characters - Multiple characters including Vegeta and Piccolo" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val vegeta =
                GeneFunkCharacter().apply {
                    firstName = "Vegeta"
                    lastName = "Prince"
                }

            val piccolo =
                GeneFunkCharacter().apply {
                    firstName = "Piccolo"
                    lastName = "Namekian"
                }

            every { characterProducer.allCharacters() } returns mutableListOf(vegeta, piccolo)

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val characters = controller.allCharacters()

            characters.size shouldBe 2
            (characters[0] as GeneFunkCharacter).firstName shouldBe "Vegeta"
            (characters[1] as GeneFunkCharacter).firstName shouldBe "Piccolo"
        }

        "GET /characters/generate - Captain America with shield" {
            val characterProducer = mockk<CharacterProducer>()
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)
            every { characterProducer.generate(0) } returns """{"firstName":"Steve","lastName":"Rogers","equipment":["Shield"]}"""

            val controller = CharacterRestController(characterProducer, meterRegistry)
            val result = controller.generateCharacter(0)

            result shouldContain "Steve"
            result shouldContain "Rogers"
            result shouldContain "Shield"
        }

        "DELETE /characters/0 - Edge case: delete character with ID 0" {
            val characterProducer = mockk<CharacterProducer>(relaxed = true)
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val controller = CharacterRestController(characterProducer, meterRegistry)
            controller.deleteCharacter(0)

            verify { characterProducer.deleteCharacter(0) }
        }

        "DELETE /characters/-1 - Edge case: delete character with negative ID" {
            val characterProducer = mockk<CharacterProducer>(relaxed = true)
            val meterRegistry = mockk<MeterRegistry>(relaxed = true)

            val controller = CharacterRestController(characterProducer, meterRegistry)
            controller.deleteCharacter(-1)

            verify { characterProducer.deleteCharacter(-1) }
        }
    })
