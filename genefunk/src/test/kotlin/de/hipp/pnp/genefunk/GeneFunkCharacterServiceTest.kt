package de.hipp.pnp.genefunk

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.entity.CharacterCreation
import de.hipp.pnp.base.entity.GeneFunkClass
import de.hipp.pnp.base.entity.Skills
import de.hipp.pnp.base.fivee.Attribute5e
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

/**
 * Tests for GeneFunkCharacterService with creative test data inspired by iconic characters
 */
class GeneFunkCharacterServiceTest : StringSpec({

    fun createMockService(): GeneFunkCharacterService {
        val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()
        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)

        // Mock genome
        val mockGenome = GeneFunkGenome().apply {
            name = "Transhuman"
            description = "Enhanced human"
            attributes = mutableMapOf(
                "strength" to 2,
                "dexterity" to 1,
                "intelligence" to 1
            )
        }

        // Mock class
        val mockClass = GeneFunkClass(
            label = "Soldier",
            description = "Combat specialist",
            characterCreation = CharacterCreation(
                savingThrows = listOf("strength", "constitution"),
                startingEquipment = listOf("Assault Rifle", "¥1000"),
                skills = Skills(choose = 2, from = listOf("Athletics", "Perception", "Stealth"))
            )
        )

        every { genomeService.allGenomes() } returns mutableListOf(mockGenome)
        every { classService.getAllClasses() } returns mutableMapOf("Soldier" to mockClass)
        every { repository.saveAndFlush(any<GeneFunkCharacter>()) } answers { firstArg() }

        return GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)
    }

    "Generate character - Goku (孫悟空) gets valid name from list" {
        val service = createMockService()

        val character = service.generate()

        character.shouldNotBeNull()
        character.firstName.shouldNotBeNull()
        character.lastName.shouldNotBeNull()
    }

    "Generate character - Tony Stark has valid strength attribute" {
        val service = createMockService()

        val character = service.generate()

        character.strength.shouldNotBeNull()
        character.strength!!.value shouldBeGreaterThanOrEqual 3
        character.strength!!.value shouldBeLessThanOrEqual 20
    }

    "Generate character - Neo has all six attributes" {
        val service = createMockService()

        val character = service.generate()

        character.strength.shouldNotBeNull()
        character.dexterity.shouldNotBeNull()
        character.constitution.shouldNotBeNull()
        character.intelligence.shouldNotBeNull()
        character.wisdom.shouldNotBeNull()
        character.charisma.shouldNotBeNull()
    }

    "Generate character - Spider-Man starts at level 1" {
        val service = createMockService()

        val character = service.generate()

        character.level shouldBe 1
    }

    "Generate character - Wonder Woman has valid genome" {
        val service = createMockService()

        val character = service.generate()

        character.genome.shouldNotBeNull()
        character.genome!!.name shouldBe "Transhuman"
    }

    "Generate character - Captain America has character class" {
        val service = createMockService()

        val character = service.generate()

        character.characterClasses.shouldNotBeNull()
        character.characterClasses!!.shouldNotBeEmpty()
    }

    "Generate character with existing character - Naruto (ナルト)" {
        val service = createMockService()
        val existingChar = GeneFunkCharacter().apply {
            firstName = "Naruto"
            lastName = "Uzumaki"
        }

        val character = service.generate(existingChar, "user123")

        character.firstName shouldBe "Naruto"
        character.lastName shouldBe "Uzumaki"
        character.userId shouldBe "user123"
    }

    "Generate character - Batman receives starting equipment" {
        val service = createMockService()

        val character = service.generate()

        character.inventory.shouldNotBeEmpty()
    }

    "Generate character - Hermione has proficient skills" {
        val service = createMockService()

        val character = service.generate()

        character.proficientSkills.shouldNotBeEmpty()
    }

    "Generate character - Pikachu (ピカチュウ) receives starting money" {
        val service = createMockService()

        val character = service.generate()

        // Starting equipment includes ¥1000
        character.money shouldBe 1000
    }

    "Add money with currency symbol - parse ¥1,000" {
        val character = GeneFunkCharacter()
        character.addMoney("¥1,000")

        character.money shouldBe 1000
    }

    "Add money with various formats - ¥10.000" {
        val character = GeneFunkCharacter()
        character.addMoney("¥10.000")

        character.money shouldBe 10000
    }

    "Add money with colon separator - ¥5:500" {
        val character = GeneFunkCharacter()
        character.addMoney("¥5:500")

        character.money shouldBe 5500
    }

    "Add class - Gandalf gains Soldier class" {
        val character = GeneFunkCharacter()
        val classEntity = GeneFunkClassEntity().apply {
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

    "Apply base values - Vegeta gets attribute bonuses" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(15)
            dexterity = Attribute5e(14)
            constitution = Attribute5e(13)
            intelligence = Attribute5e(12)
            wisdom = Attribute5e(11)
            charisma = Attribute5e(10)
        }

        val attributeChanges: MutableMap<String?, Int?> = mutableMapOf(
            "strength" to 2,
            "dexterity" to 1
        )

        character.applyBaseValues(attributeChanges)

        character.strength!!.value shouldBe 17
        character.dexterity!!.value shouldBe 15
    }

    "Set max values - Hulk has increased strength max" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(18)
            dexterity = Attribute5e(14)
            constitution = Attribute5e(16)
            intelligence = Attribute5e(10)
            wisdom = Attribute5e(10)
            charisma = Attribute5e(8)
        }

        val attributeChanges: MutableMap<String?, Int?> = mutableMapOf(
            "strength_max" to 25,
            "constitution_max" to 22
        )

        character.setMaxValues(attributeChanges)

        character.strength!!.max shouldBe 25
        character.constitution!!.max shouldBe 22
    }

    "Get all characters - admin role returns all characters" {
        val repository = mockk<GeneFunkCharacterRepository>()
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()

        val char1 = GeneFunkCharacter().apply { firstName = "Goku" }
        val char2 = GeneFunkCharacter().apply { firstName = "Vegeta" }

        every { userInfoProducer.getCustomerInfoFor("admin123") } returns Customer(
            userId = "1",
            vorname = "Admin",
            nachname = "User",
            name = "Admin User",
            externalIdentifer = "admin123",
            mail = "admin@test.com",
            role = "admin"
        )
        every { repository.findAll() } returns mutableListOf(char1, char2)

        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
        val service = GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)
        val characters = service.getAllCharacters("admin123")

        characters.shouldNotBeNull()
        characters!!.size shouldBe 2
    }

    "Get all characters - regular user returns only their characters" {
        val repository = mockk<GeneFunkCharacterRepository>()
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()

        val char1 = GeneFunkCharacter().apply {
            firstName = "Tony"
            userId = "user123"
        }

        every { userInfoProducer.getCustomerInfoFor("user123") } returns Customer(
            userId = "1",
            vorname = "Tony",
            nachname = "Stark",
            name = "Tony Stark",
            externalIdentifer = "user123",
            mail = "tony@stark.com",
            role = "user"
        )
        every { repository.findByUserId("user123") } returns mutableListOf(char1)

        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
        val service = GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)
        val characters = service.getAllCharacters("user123")

        characters.shouldNotBeNull()
        characters!!.size shouldBe 1
        characters[0]!!.firstName shouldBe "Tony"
    }

    "Delete character - admin can delete any character" {
        val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()

        every { userInfoProducer.getCustomerInfoFor("admin123") } returns Customer(
            userId = "1",
            vorname = "Admin",
            nachname = "User",
            name = "Admin User",
            externalIdentifer = "admin123",
            mail = "admin@test.com",
            role = "admin"
        )
        every { repository.deleteById(1) } returns Unit

        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
        val service = GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)
        service.delete("1", "admin123")

        verify { repository.deleteById(1) }
    }

    "Delete character - user can only delete their own character" {
        val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()

        val character = GeneFunkCharacter().apply {
            id = 1
            firstName = "Spider-Man"
            userId = "user123"
        }

        every { userInfoProducer.getCustomerInfoFor("user123") } returns Customer(
            userId = "1",
            vorname = "Peter",
            nachname = "Parker",
            name = "Peter Parker",
            externalIdentifer = "user123",
            mail = "peter@parker.com",
            role = "user"
        )
        every { repository.findById(1) } returns (Optional.ofNullable(character) as Optional<GeneFunkCharacter?>)
        every { repository.deleteById(1) } returns Unit

        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
        val service = GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)
        service.delete("1", "user123")

        verify { repository.deleteById(1) }
    }

    "Delete character - user cannot delete another user's character" {
        val repository = mockk<GeneFunkCharacterRepository>(relaxed = true)
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()

        val character = GeneFunkCharacter().apply {
            id = 1
            firstName = "Batman"
            userId = "bruce123"
        }

        every { userInfoProducer.getCustomerInfoFor("user456") } returns Customer(
            userId = "2",
            vorname = "Clark",
            nachname = "Kent",
            name = "Clark Kent",
            externalIdentifer = "user456",
            mail = "clark@kent.com",
            role = "user"
        )
        every { repository.findById(1) } returns (Optional.ofNullable(character) as Optional<GeneFunkCharacter?>)

        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
        val service = GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)

        shouldThrow<IllegalArgumentException> {
            service.delete("1", "user456")
        }
    }

    "Delete character - non-existent character throws exception" {
        val repository = mockk<GeneFunkCharacterRepository>()
        val genomeService = mockk<GeneFunkGenomeService>()
        val classService = mockk<GeneFunkClassService>()
        val userInfoProducer = mockk<UserInfoProducer>()

        every { userInfoProducer.getCustomerInfoFor("user123") } returns Customer(
            userId = "1",
            vorname = "Test",
            nachname = "User",
            name = "Test User",
            externalIdentifer = "user123",
            mail = "test@user.com",
            role = "user"
        )
        every { repository.findById(999) } returns (Optional.empty() as Optional<GeneFunkCharacter?>)

        val characterNamesProperties = mockk<CharacterNamesProperties>(relaxed = true)
        val meterRegistry = mockk<io.micrometer.core.instrument.MeterRegistry>(relaxed = true)
        val service = GeneFunkCharacterService(repository, genomeService, classService, userInfoProducer, characterNamesProperties, meterRegistry)

        shouldThrow<IllegalArgumentException> {
            service.delete("999", "user123")
        }
    }
})
