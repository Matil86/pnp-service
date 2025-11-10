package de.hipp.pnp.base.converter.persistent

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Tests for HashMapConverter ensuring bidirectional JSON conversion works correctly
 */
class HashMapConverterTest :
    StringSpec({

        val converter = HashMapConverter()

        "Convert HashMap to JSON string" {
            val map =
                mapOf(
                    "strength" to 18,
                    "dexterity" to 14,
                    "intelligence" to 16,
                )

            val json = converter.convertToDatabaseColumn(map)
            json.shouldNotBeNull()
        }

        "Convert JSON string back to HashMap" {
            val json = """{"name":"Goku","power":9000,"level":99}"""

            val map = converter.convertToEntityAttribute(json)
            map.shouldNotBeNull()
            map shouldContainKey "name"
            map shouldContainKey "power"
            map shouldContainKey "level"
        }

        "Round trip conversion: Map -> JSON -> Map" {
            val original =
                mapOf(
                    "character" to "Tony Stark",
                    "alias" to "Iron Man",
                    "suit" to "Mark 85",
                )

            val json = converter.convertToDatabaseColumn(original)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored shouldContainKey "character"
            restored shouldContainKey "alias"
            restored shouldContainKey "suit"
        }

        "Convert empty map" {
            val emptyMap = emptyMap<String, Any>()

            val json = converter.convertToDatabaseColumn(emptyMap)
            json shouldBe "{}"

            val restored = converter.convertToEntityAttribute(json)
            restored.shouldNotBeNull()
            restored.size shouldBe 0
        }

        "Convert null to null string" {
            val json = converter.convertToDatabaseColumn(null)
            json shouldBe "null"
        }

        "Convert empty string to null" {
            val map = converter.convertToEntityAttribute("")
            map.shouldBeNull()
        }

        "Convert blank string to null" {
            val map = converter.convertToEntityAttribute("   ")
            map.shouldBeNull()
        }

        "Convert null string to null" {
            val map = converter.convertToEntityAttribute(null)
            map.shouldBeNull()
        }

        "Map with nested values" {
            val complexMap =
                mapOf(
                    "hero" to "Spider-Man",
                    "stats" to mapOf("agility" to 20, "strength" to 15),
                )

            val json = converter.convertToDatabaseColumn(complexMap)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored shouldContainKey "hero"
            restored shouldContainKey "stats"
        }

        "Map with special characters in Japanese (ひらがな)" {
            val map =
                mapOf(
                    "name" to "ナルト",
                    "village" to "木ノ葉隠れの里",
                    "technique" to "影分身の術",
                )

            val json = converter.convertToDatabaseColumn(map)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored shouldContainKey "name"
        }

        "Map with emoji values" {
            val map =
                mapOf(
                    "dice" to "🎲",
                    "sword" to "⚔️",
                    "shield" to "🛡️",
                )

            val json = converter.convertToDatabaseColumn(map)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored shouldContainKey "dice"
        }

        "Large map conversion" {
            val largeMap = (1..100).associate { "key$it" to "value$it" }

            val json = converter.convertToDatabaseColumn(largeMap)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored.size shouldBe 100
        }
    })
