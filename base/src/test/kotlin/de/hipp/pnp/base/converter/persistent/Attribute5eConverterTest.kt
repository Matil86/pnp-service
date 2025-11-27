package de.hipp.pnp.base.converter.persistent

import de.hipp.pnp.base.fivee.Attribute5e
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Tests for Attribute5eConverter ensuring bidirectional JSON conversion works correctly
 */
class Attribute5eConverterTest :
    StringSpec({

        val converter = Attribute5eConverter()

        "Convert Attribute5e to JSON string" {
            val attr = Attribute5e(15)
            attr.modifyValue(3)

            val json = converter.convertToDatabaseColumn(attr)
            json.shouldNotBeNull()
            json shouldBe """{"value":18,"max":20,"modifier":4}"""
        }

        "Convert JSON string back to Attribute5e" {
            val json = """{"value":18,"max":20,"modifier":4}"""

            val attr = converter.convertToEntityAttribute(json)
            attr.shouldNotBeNull()
            attr.value shouldBe 18
            attr.max shouldBe 20
            attr.modifier shouldBe 4
        }

        "Round trip conversion: Attribute5e -> JSON -> Attribute5e" {
            val original = Attribute5e(14)
            original.modifyValue(4)

            val json = converter.convertToDatabaseColumn(original)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored.value shouldBe original.value
            restored.max shouldBe original.max
            restored.modifier shouldBe original.modifier
        }

        "Convert null to null string" {
            val json = converter.convertToDatabaseColumn(null)
            json shouldBe "null"
        }

        "Convert empty string to null" {
            val attr = converter.convertToEntityAttribute("")
            attr.shouldBeNull()
        }

        "Convert blank string to null" {
            val attr = converter.convertToEntityAttribute("   ")
            attr.shouldBeNull()
        }

        "Convert null string to null" {
            val attr = converter.convertToEntityAttribute(null)
            attr.shouldBeNull()
        }

        "Goku's strength attribute conversion" {
            val gokuStr = Attribute5e(18)
            gokuStr.modifyValue(2) // Saiyan bonus

            val json = converter.convertToDatabaseColumn(gokuStr)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored.value shouldBe 20
            restored.modifier shouldBe 5
        }

        "Tony Stark's intelligence with custom max" {
            val tonyInt = Attribute5e(19)
            tonyInt.max = 25
            tonyInt.modifyValue(5)

            val json = converter.convertToDatabaseColumn(tonyInt)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored.value shouldBe 24
            restored.max shouldBe 25
            restored.modifier shouldBe 7
        }

        "Low attribute value conversion" {
            val lowAttr = Attribute5e(3)
            lowAttr.modifyValue(0)

            val json = converter.convertToDatabaseColumn(lowAttr)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored.value shouldBe 3
            restored.modifier shouldBe -4
        }

        "Maximum value 20 conversion" {
            val maxAttr = Attribute5e(20)
            maxAttr.modifyValue(0)

            val json = converter.convertToDatabaseColumn(maxAttr)
            val restored = converter.convertToEntityAttribute(json)

            restored.shouldNotBeNull()
            restored.value shouldBe 20
            restored.modifier shouldBe 5
        }
    })
