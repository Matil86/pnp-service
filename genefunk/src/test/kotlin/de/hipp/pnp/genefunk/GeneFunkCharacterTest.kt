package de.hipp.pnp.genefunk

import de.hipp.pnp.base.constants.AttributeConstants
import de.hipp.pnp.base.entity.InventoryItem
import de.hipp.pnp.base.fivee.Attribute5e
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe

/**
 * Tests for GeneFunkCharacter entity with creative test data
 */
class GeneFunkCharacterTest : StringSpec({

    "Initialize character - Goku (孫悟空) applies genome attributes" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(15)
            dexterity = Attribute5e(14)
            constitution = Attribute5e(13)
            intelligence = Attribute5e(12)
            wisdom = Attribute5e(11)
            charisma = Attribute5e(10)
            genome = GeneFunkGenome().apply {
                name = "Saiyan"
                attributes = mutableMapOf(
                    AttributeConstants.STRENGTH to 3,
                    AttributeConstants.CONSTITUTION to 2
                )
            }
        }

        character.initialize()

        character.strength!!.value shouldBe 18
        character.constitution!!.value shouldBe 15
    }

    "Apply base values - Tony Stark enhances intelligence" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(10)
            dexterity = Attribute5e(10)
            constitution = Attribute5e(10)
            intelligence = Attribute5e(18)
            wisdom = Attribute5e(10)
            charisma = Attribute5e(10)
        }

        val changes: MutableMap<String?, Int?> = mutableMapOf(
            AttributeConstants.INTELLIGENCE to 2,
            AttributeConstants.CHARISMA to 1
        )

        character.applyBaseValues(changes)

        character.intelligence!!.value shouldBe 20
        character.charisma!!.value shouldBe 11
    }

    "Set max values - Hulk has increased strength maximum" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(18)
            dexterity = Attribute5e(14)
            constitution = Attribute5e(16)
            intelligence = Attribute5e(10)
            wisdom = Attribute5e(10)
            charisma = Attribute5e(8)
        }

        val maxChanges: MutableMap<String?, Int?> = mutableMapOf(
            AttributeConstants.STRENGTH_MAX to 25,
            AttributeConstants.CONSTITUTION_MAX to 22
        )

        character.setMaxValues(maxChanges)

        character.strength!!.max shouldBe 25
        character.constitution!!.max shouldBe 22
    }

    "Add class - Neo learns Hacker skills" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(10)
            dexterity = Attribute5e(14)
            constitution = Attribute5e(12)
            intelligence = Attribute5e(18)
            wisdom = Attribute5e(15)
            charisma = Attribute5e(13)
        }

        val hackerClass = GeneFunkClassEntity().apply {
            name = "Hacker"
            label = "Hacker"
            description = "Elite computer specialist"
            skills = listOf("Computers", "Electronics", "Investigation")
            startingEquipment = listOf("Laptop", "Hacking Tools", "¥2000")
        }

        character.addClass(hackerClass)

        character.characterClasses.shouldNotBeEmpty()
        character.characterClasses!!.size shouldBe 1
        character.proficientSkills shouldContain "Computers"
        character.proficientSkills shouldContain "Electronics"
        character.money shouldBe 2000
        character.inventory shouldHaveSize 2
    }

    "Add money - parse ¥1,000 format" {
        val character = GeneFunkCharacter()

        character.addMoney("¥1,000")

        character.money shouldBe 1000
    }

    "Add money - parse ¥10.000 format (European style)" {
        val character = GeneFunkCharacter()

        character.addMoney("¥10.000")

        character.money shouldBe 10000
    }

    "Add money - parse ¥5:500 with colon separator" {
        val character = GeneFunkCharacter()

        character.addMoney("¥5:500")

        character.money shouldBe 5500
    }

    "Add money - invalid format returns 0" {
        val character = GeneFunkCharacter()

        character.addMoney("invalid")

        character.money shouldBe 0
    }

    "Add money - empty string returns 0" {
        val character = GeneFunkCharacter()

        character.addMoney("")

        character.money shouldBe 0
    }

    "Add class with duplicate - Spider-Man levels up existing class" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(15)
            dexterity = Attribute5e(20)
            constitution = Attribute5e(14)
            intelligence = Attribute5e(16)
            wisdom = Attribute5e(14)
            charisma = Attribute5e(12)
        }

        val acrobatClass = GeneFunkClassEntity().apply {
            name = "Acrobat"
            label = "Acrobat"
            skills = listOf("Acrobatics", "Athletics")
            startingEquipment = listOf("Grappling Hook")
        }

        character.addClass(acrobatClass)
        character.addClass(acrobatClass)

        // When adding duplicate class, it should level up existing one
        character.characterClasses!!.size shouldBe 1
    }

    "Add class with inventory item stacking - Batman collects batarangs" {
        val character = GeneFunkCharacter()

        val batmanClass = GeneFunkClassEntity().apply {
            name = "Vigilante"
            label = "Vigilante"
            skills = listOf("Stealth", "Investigation")
            startingEquipment = listOf("Batarang", "Grapple Gun", "Batarang")
        }

        character.addClass(batmanClass)

        character.inventory.shouldNotBeEmpty()
        val batarangs = character.inventory.filter { it.name == "Batarang" }
        batarangs shouldHaveSize 2
    }

    "Initialize with null genome - Wonder Woman without enhancements" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(18)
            dexterity = Attribute5e(16)
            constitution = Attribute5e(17)
            intelligence = Attribute5e(14)
            wisdom = Attribute5e(15)
            charisma = Attribute5e(18)
            genome = null
        }

        character.initialize()

        // Should not throw exception with null genome
        character.strength!!.value shouldBe 18
    }

    "Apply base values with missing attributes - handles gracefully" {
        val character = GeneFunkCharacter().apply {
            strength = Attribute5e(10)
            dexterity = Attribute5e(10)
            constitution = Attribute5e(10)
            intelligence = Attribute5e(10)
            wisdom = Attribute5e(10)
            charisma = Attribute5e(10)
        }

        val changes: MutableMap<String?, Int?> = mutableMapOf(
            "unknown_attribute" to 5
        )

        character.applyBaseValues(changes)

        // Should not crash on unknown attributes
        character.strength!!.value shouldBe 10
    }

    "Character with Japanese name - Naruto (ナルト)" {
        val character = GeneFunkCharacter().apply {
            firstName = "ナルト"
            lastName = "うずまき"
            strength = Attribute5e(16)
            dexterity = Attribute5e(14)
            constitution = Attribute5e(15)
            intelligence = Attribute5e(10)
            wisdom = Attribute5e(12)
            charisma = Attribute5e(13)
        }

        character.firstName shouldBe "ナルト"
        character.lastName shouldBe "うずまき"
    }

    "Character with emoji in description - Pikachu ⚡" {
        val character = GeneFunkCharacter().apply {
            firstName = "Pikachu"
            lastName = "⚡"
        }

        character.firstName shouldBe "Pikachu"
        character.lastName shouldBe "⚡"
    }

    "Default level is 1 - new adventurer" {
        val character = GeneFunkCharacter()

        character.level shouldBe 1
    }

    "Default money is 0 - broke adventurer" {
        val character = GeneFunkCharacter()

        character.money shouldBe 0
    }
})
