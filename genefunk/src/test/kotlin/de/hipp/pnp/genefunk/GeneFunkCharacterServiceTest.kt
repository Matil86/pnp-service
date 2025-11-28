package de.hipp.pnp.genefunk

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.entity.CharacterCreation
import de.hipp.pnp.base.entity.GeneFunkClass
import de.hipp.pnp.base.entity.Skills
import de.hipp.pnp.base.fivee.Attribute5e
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.Optional

/**
 * Comprehensive tests for GeneFunkCharacterService with creative test data.
 * Tests cover character generation, validation, business logic, and string input edge cases.
 */
class GeneFunkCharacterServiceTest :
    FunSpec({

        // Setup static mocks once for all tests
        beforeSpec {
            mockkStatic(io.micrometer.core.instrument.Timer::class)
            mockkStatic(io.micrometer.core.instrument.Counter::class)
        }

        afterSpec {
            unmockkAll()
        }

        fun createMockService(): GeneFunkCharacterService {
            val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
            val genomeService = mockk<GeneFunkGenomeService>()
            val classService = mockk<GeneFunkClassService>()
            val userInfoProducer = mockk<UserInfoProducer>()
            val characterNamesProperties =
                CharacterNamesProperties().apply {
                    names = listOf("John", "Jane", "Alex", "Sam", "Taylor", "Jordan", "Morgan", "Casey")
                }
            val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)

            // Mock Timer to properly execute the callable and return its result
            val mockTimer = mockk<io.micrometer.core.instrument.Timer>(relaxed = true)
            val mockTimerBuilder = mockk<io.micrometer.core.instrument.Timer.Builder>(relaxed = true)
            val mockCounterBuilder = mockk<io.micrometer.core.instrument.Counter.Builder>(relaxed = true)
            val mockCounter = mockk<io.micrometer.core.instrument.Counter>(relaxed = true)

            every {
                io.micrometer.core.instrument.Timer
                    .builder(any())
            } returns mockTimerBuilder
            every { mockTimerBuilder.description(any()) } returns mockTimerBuilder
            every { mockTimerBuilder.register(any()) } returns mockTimer
            every { mockTimer.recordCallable<GeneFunkCharacter>(any()) } answers {
                val callable = firstArg<java.util.concurrent.Callable<GeneFunkCharacter>>()
                callable.call()
            }

            every {
                io.micrometer.core.instrument.Counter
                    .builder(any())
            } returns mockCounterBuilder
            every { mockCounterBuilder.description(any()) } returns mockCounterBuilder
            every { mockCounterBuilder.register(any()) } returns mockCounter

            // Mock genome
            val mockGenome =
                GeneFunkGenome().apply {
                    name = "Transhuman"
                    description = "Enhanced human"
                    attributes =
                        mutableMapOf(
                            "strength" to 2,
                            "dexterity" to 1,
                            "intelligence" to 1,
                        )
                }

            // Mock class
            val mockClass =
                GeneFunkClass(
                    label = "Soldier",
                    description = "Combat specialist",
                    characterCreation =
                        CharacterCreation(
                            savingThrows = listOf("strength", "constitution"),
                            startingEquipment = listOf("Assault Rifle", "¥1000"),
                            skills = Skills(choose = 2, from = listOf("Athletics", "Perception", "Stealth")),
                        ),
                )

            every { genomeService.allGenomes() } returns mutableListOf(mockGenome)
            every { classService.getAllClasses() } returns mutableMapOf("Soldier" to mockClass)
            // Mock repository to return the same character passed to it (simulating persist)
            every { repository.saveAndFlush(any<GeneFunkCharacter>()) } answers {
                val char = firstArg<GeneFunkCharacter>()
                // Simulate database ID assignment
                if (char.id == null) {
                    char.id = (1..1000).random()
                }
                char
            }

            return GeneFunkCharacterService(
                repository,
                genomeService,
                classService,
                userInfoProducer,
                characterNamesProperties,
                meterRegistry,
            )
        }

        // ========== Character Generation Tests ==========

        context("Character generation - Basic attributes") {
            test("should generate character with valid name from list - Goku (孫悟空)") {
                val service = createMockService()

                val character = service.generate()

                character.shouldNotBeNull()
                character.firstName.shouldNotBeNull()
                character.lastName.shouldNotBeNull()
            }

            test("should generate character with valid strength attribute - Tony Stark") {
                val service = createMockService()

                val character = service.generate()

                character.strength.shouldNotBeNull()
                character.strength!!.value shouldBeGreaterThanOrEqual 3
                character.strength!!.value shouldBeLessThanOrEqual 20
            }

            test("should generate character with all six attributes - Neo") {
                val service = createMockService()

                val character = service.generate()

                character.strength.shouldNotBeNull()
                character.dexterity.shouldNotBeNull()
                character.constitution.shouldNotBeNull()
                character.intelligence.shouldNotBeNull()
                character.wisdom.shouldNotBeNull()
                character.charisma.shouldNotBeNull()
            }

            test("should generate character starting at level 1 - Spider-Man") {
                val service = createMockService()

                val character = service.generate()

                character.level shouldBe 1
            }

            test("should generate character with valid genome - Wonder Woman") {
                val service = createMockService()

                val character = service.generate()

                character.genome.shouldNotBeNull()
                character.genome!!.name shouldBe "Transhuman"
            }

            test("should generate character with character class - Captain America") {
                val service = createMockService()

                val character = service.generate()

                character.characterClasses.shouldNotBeNull()
                character.characterClasses!!.shouldNotBeEmpty()
            }

            test("should generate character with starting equipment - Batman") {
                val service = createMockService()

                val character = service.generate()

                character.inventory.shouldNotBeEmpty()
            }

            test("should generate character with proficient skills - Hermione") {
                val service = createMockService()

                val character = service.generate()

                character.proficientSkills.shouldNotBeEmpty()
            }

            test("should generate character with starting money - Pikachu (ピカチュウ)") {
                val service = createMockService()

                val character = service.generate()

                // Starting equipment includes ¥1000
                character.money shouldBe 1000
            }
        }

        context("Character generation - With existing character") {
            test("should preserve existing character firstName - Naruto (ナルト)") {
                val service = createMockService()
                val existingChar =
                    GeneFunkCharacter().apply {
                        firstName = "Naruto"
                        lastName = "Uzumaki"
                    }

                val character = service.generate(existingChar, "user123")

                character.firstName shouldBe "Naruto"
                character.lastName shouldBe "Uzumaki"
                character.userId shouldBe "user123"
            }

            test("should preserve existing attributes when provided") {
                val service = createMockService()
                val existingChar =
                    GeneFunkCharacter().apply {
                        firstName = "Vegeta"
                        strength = Attribute5e(18)
                    }

                val character = service.generate(existingChar, "user123")

                character.firstName shouldBe "Vegeta"
                // Strength will be 18 + 2 (from Transhuman genome) = 20
                character.strength!!.value shouldBe 20
            }
        }

        // ========== String Input Edge Cases ==========

        context("Character names - String input edge cases") {
            test("should generate character with random name when firstName is null") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = null
                        lastName = null
                    }

                val result = service.generate(character, "user123")

                result.firstName.shouldNotBeNull()
                result.lastName.shouldNotBeNull()
            }

            test("should handle hiragana in firstName - ひらがな") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "ひらがな"
                        lastName = "テスト"
                    }

                val result = service.generate(character, "user123")

                result.firstName shouldBe "ひらがな"
                result.lastName shouldBe "テスト"
            }

            test("should handle katakana in firstName - カタカナ") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "カタカナ"
                        lastName = "キャラクター"
                    }

                val result = service.generate(character, "user123")

                result.firstName shouldBe "カタカナ"
                result.lastName shouldBe "キャラクター"
            }

            test("should handle kanji in character name - 孫悟空") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "孫"
                        lastName = "悟空"
                    }

                val result = service.generate(character, "user123")

                result.firstName shouldBe "孫"
                result.lastName shouldBe "悟空"
            }

            test("should handle emoji in character name - Goku 🐉") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "Goku"
                        lastName = "Dragon 🐉"
                    }

                val result = service.generate(character, "user123")

                result.firstName shouldBe "Goku"
                result.lastName shouldContain "🐉"
            }

            test("should handle SQL injection attempt in firstName") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "'; DROP TABLE characters; --"
                        lastName = "Hacker"
                    }

                val result = service.generate(character, "user123")

                result.firstName shouldContain "DROP TABLE"
            }

            test("should handle XSS attempt in lastName") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "Test"
                        lastName = "<script>alert('XSS')</script>"
                    }

                val result = service.generate(character, "user123")

                result.lastName shouldContain "<script>"
            }

            test("should handle empty string in firstName") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = ""
                        lastName = ""
                    }

                // Empty strings should be treated as null and replaced with random names
                val result = service.generate(character, "user123")

                result.firstName.shouldNotBeNull()
                result.lastName.shouldNotBeNull()
            }

            test("should handle whitespace-only firstName") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "   "
                        lastName = "\t\n"
                    }

                val result = service.generate(character, "user123")

                // System accepts whitespace strings as-is
                result.firstName shouldBe "   "
                result.lastName shouldBe "\t\n"
            }

            test("should handle very long name - Luffy's full title") {
                val service = createMockService()
                val character =
                    GeneFunkCharacter().apply {
                        firstName = "Monkey D. Luffy"
                        lastName = "Future King of the Pirates"
                    }

                val result = service.generate(character, "user123")

                result.firstName shouldBe "Monkey D. Luffy"
                result.lastName shouldBe "Future King of the Pirates"
            }
        }

        context("External ID - String input edge cases") {
            test("should handle null externalId") {
                val service = createMockService()

                val character = service.generate(GeneFunkCharacter(), null)

                character.userId shouldBe null
            }

            test("should handle empty externalId") {
                val service = createMockService()

                val character = service.generate(GeneFunkCharacter(), "")

                character.userId shouldBe ""
            }

            test("should handle UUID externalId - Tony Stark @ Stark Industries") {
                val service = createMockService()
                val uuid = "550e8400-e29b-41d4-a716-446655440000"

                val character = service.generate(GeneFunkCharacter(), uuid)

                character.userId shouldBe uuid
            }

            test("should handle special characters in externalId") {
                val service = createMockService()
                val specialId = "user-123_@email.com"

                val character = service.generate(GeneFunkCharacter(), specialId)

                character.userId shouldBe specialId
            }
        }

        // ========== Money Management Tests ==========

        context("Money management - Currency parsing") {
            test("should parse money with currency symbol - ¥1,000") {
                val character = GeneFunkCharacter()
                character.addMoney("¥1,000")

                character.money shouldBe 1000
            }

            test("should parse money with various formats - ¥10.000") {
                val character = GeneFunkCharacter()
                character.addMoney("¥10.000")

                character.money shouldBe 10000
            }

            test("should parse money with colon separator - ¥5:500") {
                val character = GeneFunkCharacter()
                character.addMoney("¥5:500")

                character.money shouldBe 5500
            }

            test("should handle money without currency symbol - 999") {
                val character = GeneFunkCharacter()
                character.addMoney("999")

                character.money shouldBe 999
            }

            test("should handle empty string money as 0") {
                val character = GeneFunkCharacter()
                character.addMoney("")

                character.money shouldBe 0
            }

            test("should handle invalid money string as 0") {
                val character = GeneFunkCharacter()
                character.addMoney("not a number")

                character.money shouldBe 0
            }

            test("should handle SQL injection in money string") {
                val character = GeneFunkCharacter()
                character.addMoney("'; DROP TABLE money; --")

                character.money shouldBe 0
            }

            test("should accumulate money from multiple sources") {
                val character = GeneFunkCharacter()
                character.addMoney("¥500")
                character.addMoney("¥300")
                character.addMoney("200")

                character.money shouldBe 1000
            }
        }

        // ========== Class Management Tests ==========

        context("Character class management") {
            test("should add class successfully - Gandalf gains Soldier class") {
                val character = GeneFunkCharacter()
                val classEntity =
                    GeneFunkClassEntity().apply {
                        name = "Soldier"
                        label = "Soldier"
                        description = "Combat specialist"
                        skills = listOf("Athletics", "Perception")
                        startingEquipment = listOf("Rifle", "¥500")
                    }

                character.addClass(classEntity)

                character.characterClasses.shouldNotBeNull()
                character.characterClasses!!.size shouldBe 1
                character.proficientSkills shouldContain "Athletics"
                character.money shouldBe 500
            }

            test("should handle class with no starting equipment") {
                val character = GeneFunkCharacter()
                val classEntity =
                    GeneFunkClassEntity().apply {
                        name = "Scout"
                        label = "Scout"
                        description = "Recon specialist"
                        skills = listOf("Stealth", "Survival")
                        startingEquipment = emptyList()
                    }

                character.addClass(classEntity)

                character.characterClasses.shouldNotBeNull()
                character.money shouldBe 0
            }

            test("should accumulate skills from multiple classes") {
                val character = GeneFunkCharacter()
                val class1 =
                    GeneFunkClassEntity().apply {
                        name = "Fighter"
                        skills = listOf("Athletics", "Intimidation")
                        startingEquipment = emptyList()
                    }
                val class2 =
                    GeneFunkClassEntity().apply {
                        name = "Rogue"
                        skills = listOf("Stealth", "Sleight of Hand")
                        startingEquipment = emptyList()
                    }

                character.addClass(class1)
                character.addClass(class2)

                character.proficientSkills.size shouldBe 4
                character.proficientSkills shouldContain "Athletics"
                character.proficientSkills shouldContain "Stealth"
            }
        }

        // ========== Attribute Management Tests ==========

        context("Attribute management - Apply base values") {
            test("should apply attribute bonuses - Vegeta gets stronger") {
                val character =
                    GeneFunkCharacter().apply {
                        strength = Attribute5e(15)
                        dexterity = Attribute5e(14)
                        constitution = Attribute5e(13)
                        intelligence = Attribute5e(12)
                        wisdom = Attribute5e(11)
                        charisma = Attribute5e(10)
                    }

                val attributeChanges: MutableMap<String?, Int?> =
                    mutableMapOf(
                        "strength" to 2,
                        "dexterity" to 1,
                    )

                character.applyBaseValues(attributeChanges)

                character.strength!!.value shouldBe 17
                character.dexterity!!.value shouldBe 15
            }

            test("should handle zero attribute changes") {
                val character =
                    GeneFunkCharacter().apply {
                        strength = Attribute5e(10)
                        dexterity = Attribute5e(10)
                        constitution = Attribute5e(10)
                        intelligence = Attribute5e(10)
                        wisdom = Attribute5e(10)
                        charisma = Attribute5e(10)
                    }

                val attributeChanges: MutableMap<String?, Int?> =
                    mutableMapOf(
                        "strength" to 0,
                    )

                character.applyBaseValues(attributeChanges)

                character.strength!!.value shouldBe 10
            }

            test("should handle negative attribute changes") {
                val character =
                    GeneFunkCharacter().apply {
                        strength = Attribute5e(15)
                        dexterity = Attribute5e(14)
                        constitution = Attribute5e(13)
                        intelligence = Attribute5e(12)
                        wisdom = Attribute5e(11)
                        charisma = Attribute5e(10)
                    }

                val attributeChanges: MutableMap<String?, Int?> =
                    mutableMapOf(
                        "strength" to -2,
                    )

                character.applyBaseValues(attributeChanges)

                character.strength!!.value shouldBe 13
            }
        }

        context("Attribute management - Set max values") {
            test("should set max values - Hulk has increased strength max") {
                val character =
                    GeneFunkCharacter().apply {
                        strength = Attribute5e(18)
                        dexterity = Attribute5e(14)
                        constitution = Attribute5e(16)
                        intelligence = Attribute5e(10)
                        wisdom = Attribute5e(10)
                        charisma = Attribute5e(8)
                    }

                val attributeChanges: MutableMap<String?, Int?> =
                    mutableMapOf(
                        "strength_max" to 25,
                        "constitution_max" to 22,
                    )

                character.setMaxValues(attributeChanges)

                character.strength!!.max shouldBe 25
                character.constitution!!.max shouldBe 22
            }

            test("should handle empty max value map") {
                val character =
                    GeneFunkCharacter().apply {
                        strength = Attribute5e(10)
                        dexterity = Attribute5e(10)
                        constitution = Attribute5e(10)
                        intelligence = Attribute5e(10)
                        wisdom = Attribute5e(10)
                        charisma = Attribute5e(10)
                    }

                val attributeChanges: MutableMap<String?, Int?> = mutableMapOf()

                character.setMaxValues(attributeChanges)

                // Defaults should remain unchanged
                character.strength!!.max shouldBe 20
            }
        }

        // ========== Authorization Tests ==========

        context("Get all characters - Authorization") {
            test("should return all characters for admin role") {
                val repository = mockk<GeneFunkCharacterRepository>()
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                val char1 = GeneFunkCharacter().apply { firstName = "Goku" }
                val char2 = GeneFunkCharacter().apply { firstName = "Vegeta" }

                every { userInfoProducer.getCustomerInfoFor("admin123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Admin",
                        nachname = "User",
                        name = "Admin User",
                        externalIdentifier = "admin123",
                        mail = "admin@test.com",
                        role = "admin",
                    )
                every { repository.findAll() } returns mutableListOf(char1, char2)

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )
                val characters = service.getAllCharacters("admin123")

                characters.shouldNotBeNull()
                characters.size shouldBe 2
            }

            test("should return only user's characters for regular user") {
                val repository = mockk<GeneFunkCharacterRepository>()
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                val char1 =
                    GeneFunkCharacter().apply {
                        firstName = "Tony"
                        userId = "user123"
                    }

                every { userInfoProducer.getCustomerInfoFor("user123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Tony",
                        nachname = "Stark",
                        name = "Tony Stark",
                        externalIdentifier = "user123",
                        mail = "tony@starkindustries.com",
                        role = "user",
                    )
                every { repository.findByUserId("user123") } returns mutableListOf(char1)

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )
                val characters = service.getAllCharacters("user123")

                characters.shouldNotBeNull()
                characters.size shouldBe 1
                characters[0].firstName shouldBe "Tony"
            }

            test("should handle case-insensitive admin role check") {
                val repository = mockk<GeneFunkCharacterRepository>()
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                every { userInfoProducer.getCustomerInfoFor("admin123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Admin",
                        nachname = "User",
                        name = "Admin User",
                        externalIdentifier = "admin123",
                        mail = "admin@test.com",
                        role = "ADMIN", // Uppercase
                    )
                every { repository.findAll() } returns mutableListOf()

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )
                val characters = service.getAllCharacters("admin123")

                characters.shouldNotBeNull()
            }
        }

        // ========== Delete Character Tests ==========

        context("Delete character - Authorization") {
            test("should allow admin to delete any character") {
                val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                every { userInfoProducer.getCustomerInfoFor("admin123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Admin",
                        nachname = "User",
                        name = "Admin User",
                        externalIdentifier = "admin123",
                        mail = "admin@test.com",
                        role = "admin",
                    )
                every { repository.deleteById(1) } returns Unit

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )
                service.delete("1", "admin123")

                verify { repository.deleteById(1) }
            }

            test("should allow user to delete their own character") {
                val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                val character: GeneFunkCharacter =
                    GeneFunkCharacter().apply {
                        id = 1
                        firstName = "Spider-Man"
                        userId = "user123"
                    }

                every { userInfoProducer.getCustomerInfoFor("user123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Peter",
                        nachname = "Parker",
                        name = "Peter Parker",
                        externalIdentifier = "user123",
                        mail = "peter@parker.com",
                        role = "user",
                    )
                every { repository.findById(1) } returns character
                every { repository.deleteById(1) } returns Unit

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )
                service.delete("1", "user123")

                verify { repository.deleteById(1) }
            }

            test("should prevent user from deleting another user's character") {
                val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                val character: GeneFunkCharacter =
                    GeneFunkCharacter().apply {
                        id = 1
                        firstName = "Batman"
                        userId = "bruce123"
                    }

                every { userInfoProducer.getCustomerInfoFor("user456") } returns
                    Customer(
                        userId = "2",
                        vorname = "Clark",
                        nachname = "Kent",
                        name = "Clark Kent",
                        externalIdentifier = "user456",
                        mail = "clark@kent.com",
                        role = "user",
                    )
                every { repository.findById(1) } returns character

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )

                shouldThrow<IllegalArgumentException> {
                    service.delete("1", "user456")
                }
            }

            test("should throw exception when deleting non-existent character") {
                val repository = mockk<GeneFunkCharacterRepository>()
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                every { userInfoProducer.getCustomerInfoFor("user123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Test",
                        nachname = "User",
                        name = "Test User",
                        externalIdentifier = "user123",
                        mail = "test@user.com",
                        role = "user",
                    )
                every { repository.findById(999) } returns null

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )

                shouldThrow<IllegalArgumentException> {
                    service.delete("999", "user123")
                }
            }
        }

        context("Delete character - String input edge cases") {
            test("should handle numeric string characterId") {
                val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                every { userInfoProducer.getCustomerInfoFor("admin123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Admin",
                        nachname = "User",
                        name = "Admin User",
                        externalIdentifier = "admin123",
                        mail = "admin@test.com",
                        role = "admin",
                    )

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )

                service.delete("42", "admin123")

                verify { repository.deleteById(42) }
            }

            test("should handle SQL injection in characterId") {
                val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
                val genomeService = mockk<GeneFunkGenomeService>()
                val classService = mockk<GeneFunkClassService>()
                val userInfoProducer = mockk<UserInfoProducer>()

                every { userInfoProducer.getCustomerInfoFor("admin123") } returns
                    Customer(
                        userId = "1",
                        vorname = "Admin",
                        nachname = "User",
                        name = "Admin User",
                        externalIdentifier = "admin123",
                        mail = "admin@test.com",
                        role = "admin",
                    )

                val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
                val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
                val service =
                    GeneFunkCharacterService(
                        repository,
                        genomeService,
                        classService,
                        userInfoProducer,
                        characterNamesProperties,
                        meterRegistry,
                    )

                // Should throw NumberFormatException when parsing non-numeric ID
                shouldThrow<NumberFormatException> {
                    service.delete("'; DROP TABLE characters; --", "admin123")
                }
            }
        }
    })
