package de.hipp.pnp.base.converter.persistent

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Tests for ListConverter ensuring bidirectional JSON conversion works correctly
 */
class ListConverterTest : StringSpec({

    val converter = ListConverter()

    "Convert List to JSON string" {
        val list = listOf("Goku", "Vegeta", "Gohan")

        val json = converter.convertToDatabaseColumn(list)
        json.shouldNotBeNull()
    }

    "Convert JSON string back to List" {
        val json = """["Iron Man","Captain America","Thor"]"""

        val list = converter.convertToEntityAttribute(json)
        list.shouldNotBeNull()
        list shouldHaveSize 3
        list shouldContain "Iron Man"
    }

    "Round trip conversion: List -> JSON -> List" {
        val original = listOf("Spider-Man", "Black Widow", "Hawkeye")

        val json = converter.convertToDatabaseColumn(original)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 3
        restored shouldContain "Spider-Man"
    }

    "Convert empty list" {
        val emptyList = emptyList<Any>()

        val json = converter.convertToDatabaseColumn(emptyList)
        json shouldBe "[]"

        val restored = converter.convertToEntityAttribute(json)
        restored.shouldNotBeNull()
        restored shouldHaveSize 0
    }

    "Convert null to null string" {
        val json = converter.convertToDatabaseColumn(null)
        json shouldBe "null"
    }

    "Convert empty string to null" {
        val list = converter.convertToEntityAttribute("")
        list.shouldBeNull()
    }

    "Convert blank string to null" {
        val list = converter.convertToEntityAttribute("   ")
        list.shouldBeNull()
    }

    "Convert null string to null" {
        val list = converter.convertToEntityAttribute(null)
        list.shouldBeNull()
    }

    "List with Japanese characters (ひらがな and カタカナ)" {
        val list = listOf("孫悟空", "ナルト", "ルフィ")

        val json = converter.convertToDatabaseColumn(list)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 3
        restored shouldContain "孫悟空"
    }

    "List with emoji characters" {
        val list = listOf("🎲", "⚔️", "🛡️", "🧙")

        val json = converter.convertToDatabaseColumn(list)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 4
        restored shouldContain "🎲"
    }

    "List with mixed types" {
        val list = listOf("Gandalf", 100, true)

        val json = converter.convertToDatabaseColumn(list)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 3
    }

    "List with special characters and whitespace" {
        val list = listOf("Neo ", " Trinity", "Morpheus\n", "\tCypher")

        val json = converter.convertToDatabaseColumn(list)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 4
    }

    "Large list conversion" {
        val largeList = (1..100).map { "Item$it" }

        val json = converter.convertToDatabaseColumn(largeList)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 100
    }

    "List with nested structures" {
        val list = listOf(
            mapOf("name" to "Batman"),
            mapOf("name" to "Superman")
        )

        val json = converter.convertToDatabaseColumn(list)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 2
    }

    "Single item list" {
        val list = listOf("Wonder Woman")

        val json = converter.convertToDatabaseColumn(list)
        val restored = converter.convertToEntityAttribute(json)

        restored.shouldNotBeNull()
        restored shouldHaveSize 1
        restored shouldContain "Wonder Woman"
    }
})
