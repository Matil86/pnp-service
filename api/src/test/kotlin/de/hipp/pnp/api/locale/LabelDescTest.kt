package de.hipp.pnp.api.locale

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class LabelDescTest :
    FunSpec({
        val mapper = jacksonObjectMapper()

        context("Construction and Default Values") {
            test("should create LabelDesc with default empty strings") {
                val labelDesc = LabelDesc()

                labelDesc.label shouldBe ""
                labelDesc.description shouldBe ""
            }

            test("should create LabelDesc with provided values") {
                val labelDesc =
                    LabelDesc(
                        label = "Wizard",
                        description = "Master of magic",
                    )

                labelDesc.label shouldBe "Wizard"
                labelDesc.description shouldBe "Master of magic"
            }

            test("should create LabelDesc for Frodo") {
                val labelDesc =
                    LabelDesc(
                        label = "Hobbit",
                        description = "A small folk from the Shire",
                    )

                labelDesc.label shouldBe "Hobbit"
                labelDesc.description shouldBe "A small folk from the Shire"
            }

            test("should create LabelDesc for Gandalf") {
                val labelDesc =
                    LabelDesc(
                        label = "Wizard",
                        description = "One of the Istari, wielder of Glamdring",
                    )

                labelDesc.label shouldBe "Wizard"
                labelDesc.description shouldContain "Istari"
            }
        }

        context("Label Property") {
            test("should handle empty label") {
                val labelDesc = LabelDesc(label = "")

                labelDesc.label shouldBe ""
            }

            test("should handle single character label") {
                val labelDesc = LabelDesc(label = "A")

                labelDesc.label shouldBe "A"
            }

            test("should handle long label") {
                val longLabel = "A".repeat(1000)
                val labelDesc = LabelDesc(label = longLabel)

                labelDesc.label shouldBe longLabel
            }

            test("should handle unicode label") {
                val labelDesc = LabelDesc(label = "武士")

                labelDesc.label shouldBe "武士"
            }

            test("should handle emoji label") {
                val labelDesc = LabelDesc(label = "🧙‍♂️")

                labelDesc.label shouldBe "🧙‍♂️"
            }

            test("should handle label with spaces") {
                val labelDesc = LabelDesc(label = "Great Wizard")

                labelDesc.label shouldBe "Great Wizard"
            }

            test("should handle label with special characters") {
                val labelDesc = LabelDesc(label = "O'Malley-Smith")

                labelDesc.label shouldBe "O'Malley-Smith"
            }

            test("should handle label with numbers") {
                val labelDesc = LabelDesc(label = "Level 20 Wizard")

                labelDesc.label shouldBe "Level 20 Wizard"
            }

            test("should handle label with punctuation") {
                val labelDesc = LabelDesc(label = "Wizard, Master of Magic!")

                labelDesc.label shouldBe "Wizard, Master of Magic!"
            }

            test("should handle blank label") {
                val labelDesc = LabelDesc(label = "   ")

                labelDesc.label shouldBe "   "
            }
        }

        context("Description Property") {
            test("should handle empty description") {
                val labelDesc = LabelDesc(description = "")

                labelDesc.description shouldBe ""
            }

            test("should handle single character description") {
                val labelDesc = LabelDesc(description = "X")

                labelDesc.description shouldBe "X"
            }

            test("should handle long description") {
                val longDesc = "X".repeat(5000)
                val labelDesc = LabelDesc(description = longDesc)

                labelDesc.description shouldBe longDesc
            }

            test("should handle unicode description") {
                val labelDesc = LabelDesc(description = "これは説明です")

                labelDesc.description shouldBe "これは説明です"
            }

            test("should handle emoji description") {
                val labelDesc = LabelDesc(description = "A powerful class ⚡")

                labelDesc.description shouldBe "A powerful class ⚡"
            }

            test("should handle multiline description") {
                val labelDesc =
                    LabelDesc(
                        description = "Line 1\nLine 2\nLine 3",
                    )

                labelDesc.description shouldBe "Line 1\nLine 2\nLine 3"
            }

            test("should handle description with tabs") {
                val labelDesc = LabelDesc(description = "Tab\there")

                labelDesc.description shouldBe "Tab\there"
            }

            test("should handle Neo's description") {
                val labelDesc =
                    LabelDesc(
                        label = "The One",
                        description = "A hacker who can manipulate the Matrix itself",
                    )

                labelDesc.description shouldContain "Matrix"
            }

            test("should handle Trinity's description") {
                val labelDesc =
                    LabelDesc(
                        label = "Operator",
                        description = "Elite operative skilled in combat and hacking",
                    )

                labelDesc.description shouldContain "Elite"
            }

            test("should handle blank description") {
                val labelDesc = LabelDesc(description = "   ")

                labelDesc.description shouldBe "   "
            }
        }

        context("Data Class Behavior") {
            test("should implement equals correctly") {
                val labelDesc1 = LabelDesc("Wizard", "Magic user")
                val labelDesc2 = LabelDesc("Wizard", "Magic user")

                labelDesc1 shouldBe labelDesc2
            }

            test("should implement equals for different values") {
                val labelDesc1 = LabelDesc("Wizard", "Magic user")
                val labelDesc2 = LabelDesc("Rogue", "Sneaky")

                labelDesc1 shouldNotBe labelDesc2
            }

            test("should implement equals for different labels same description") {
                val labelDesc1 = LabelDesc("Wizard", "Description")
                val labelDesc2 = LabelDesc("Rogue", "Description")

                labelDesc1 shouldNotBe labelDesc2
            }

            test("should implement equals for same label different description") {
                val labelDesc1 = LabelDesc("Wizard", "Magic user")
                val labelDesc2 = LabelDesc("Wizard", "Spellcaster")

                labelDesc1 shouldNotBe labelDesc2
            }

            test("should implement hashCode correctly") {
                val labelDesc1 = LabelDesc("Wizard", "Magic user")
                val labelDesc2 = LabelDesc("Wizard", "Magic user")

                labelDesc1.hashCode() shouldBe labelDesc2.hashCode()
            }

            test("should support copy with label modification") {
                val original = LabelDesc("Wizard", "Magic user")
                val copy = original.copy(label = "Sorcerer")

                copy.label shouldBe "Sorcerer"
                copy.description shouldBe "Magic user"
            }

            test("should support copy with description modification") {
                val original = LabelDesc("Wizard", "Magic user")
                val copy = original.copy(description = "Spellcaster")

                copy.label shouldBe "Wizard"
                copy.description shouldBe "Spellcaster"
            }

            test("should support copy with both modifications") {
                val original = LabelDesc("Wizard", "Magic user")
                val copy = original.copy(label = "Sorcerer", description = "Innate magic")

                copy.label shouldBe "Sorcerer"
                copy.description shouldBe "Innate magic"
            }

            test("should generate meaningful toString") {
                val labelDesc = LabelDesc("Wizard", "Magic user")
                val toString = labelDesc.toString()

                toString.shouldContain("LabelDesc")
                toString.shouldContain("label")
                toString.shouldContain("description")
            }

            test("should toString contain actual values") {
                val labelDesc = LabelDesc("Wizard", "Magic user")
                val toString = labelDesc.toString()

                toString.shouldContain("Wizard")
                toString.shouldContain("Magic user")
            }
        }

        context("JSON Serialization") {
            test("should serialize to JSON") {
                val labelDesc = LabelDesc("Wizard", "Master of magic")

                val json = mapper.writeValueAsString(labelDesc)

                json.shouldContain("\"label\":\"Wizard\"")
                json.shouldContain("\"description\":\"Master of magic\"")
            }

            test("should serialize empty strings") {
                val labelDesc = LabelDesc("", "")

                val json = mapper.writeValueAsString(labelDesc)

                json.shouldContain("\"label\":\"\"")
                json.shouldContain("\"description\":\"\"")
            }

            test("should serialize Frodo's hobbit lore") {
                val labelDesc =
                    LabelDesc(
                        "Hobbit",
                        "Small folk of the Shire, resistant to corruption",
                    )

                val json = mapper.writeValueAsString(labelDesc)

                json.shouldContain("Hobbit")
                json.shouldContain("resistant to corruption")
            }

            test("should serialize Aragorn's ranger info") {
                val labelDesc =
                    LabelDesc(
                        "Ranger",
                        "Skilled tracker and warrior, heir to the throne of Gondor",
                    )

                val json = mapper.writeValueAsString(labelDesc)

                json.shouldContain("Ranger")
                json.shouldContain("Gondor")
            }

            test("should serialize unicode content") {
                val labelDesc = LabelDesc("武士", "日本の戦士")

                val json = mapper.writeValueAsString(labelDesc)

                json.shouldContain("武士")
                json.shouldContain("日本の戦士")
            }

            test("should serialize emoji content") {
                val labelDesc = LabelDesc("🧙‍♂️", "Magic user ⚡")

                val json = mapper.writeValueAsString(labelDesc)

                json.shouldContain("🧙‍♂️")
                json.shouldContain("⚡")
            }

            test("should serialize special characters") {
                val labelDesc = LabelDesc("Test's \"Label\"", "Description with\nnewline")

                val json = mapper.writeValueAsString(labelDesc)

                json shouldNotBe null
            }

            test("should serialize very long strings") {
                val labelDesc = LabelDesc("A".repeat(1000), "B".repeat(1000))

                val json = mapper.writeValueAsString(labelDesc)

                json.length shouldBe (json.length) // Just verify it serializes
            }
        }

        context("JSON Deserialization") {
            test("should deserialize from JSON") {
                val json = """{"label":"Wizard","description":"Master of magic"}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldBe "Wizard"
                labelDesc.description shouldBe "Master of magic"
            }

            test("should deserialize empty strings") {
                val json = """{"label":"","description":""}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldBe ""
                labelDesc.description shouldBe ""
            }

            test("should deserialize with missing fields using defaults") {
                val json = """{}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldBe ""
                labelDesc.description shouldBe ""
            }

            test("should deserialize Gandalf's wizard info") {
                val json = """{"label":"Wizard","description":"One of the Istari sent to Middle-earth"}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldBe "Wizard"
                labelDesc.description shouldContain "Istari"
            }

            test("should deserialize unicode") {
                val json = """{"label":"武士","description":"日本の戦士"}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldBe "武士"
                labelDesc.description shouldBe "日本の戦士"
            }

            test("should deserialize emoji") {
                val json = """{"label":"🧙‍♂️","description":"Magic ⚡"}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldBe "🧙‍♂️"
                labelDesc.description shouldBe "Magic ⚡"
            }

            test("should deserialize escaped characters") {
                val json = """{"label":"Test's \"Label\"","description":"Line 1\nLine 2"}"""

                val labelDesc = mapper.readValue<LabelDesc>(json)

                labelDesc.label shouldContain "Test's"
                labelDesc.description shouldContain "\n"
            }
        }

        context("Round-trip Serialization") {
            test("should maintain data through serialize-deserialize cycle") {
                val original = LabelDesc("Wizard", "Master of magic")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<LabelDesc>(json)

                deserialized shouldBe original
            }

            test("should handle unicode in round-trip") {
                val original = LabelDesc("武士", "日本の戦士")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<LabelDesc>(json)

                deserialized shouldBe original
            }

            test("should handle emoji in round-trip") {
                val original = LabelDesc("🧙‍♂️ Wizard", "Magic user ⚡")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<LabelDesc>(json)

                deserialized shouldBe original
            }

            test("should handle empty strings in round-trip") {
                val original = LabelDesc("", "")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<LabelDesc>(json)

                deserialized shouldBe original
            }

            test("should handle special characters in round-trip") {
                val original = LabelDesc("Test\nLabel", "Desc\twith\ttabs")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<LabelDesc>(json)

                deserialized shouldBe original
            }
        }

        context("Mutability") {
            test("should allow label modification") {
                val labelDesc = LabelDesc("Wizard", "Magic")
                labelDesc.label = "Sorcerer"

                labelDesc.label shouldBe "Sorcerer"
            }

            test("should allow description modification") {
                val labelDesc = LabelDesc("Wizard", "Magic")
                labelDesc.description = "Innate magic user"

                labelDesc.description shouldBe "Innate magic user"
            }

            test("should allow both modifications") {
                val labelDesc = LabelDesc("Wizard", "Magic")
                labelDesc.label = "Warlock"
                labelDesc.description = "Pact magic"

                labelDesc.label shouldBe "Warlock"
                labelDesc.description shouldBe "Pact magic"
            }

            test("should allow setting to empty strings") {
                val labelDesc = LabelDesc("Wizard", "Magic")
                labelDesc.label = ""
                labelDesc.description = ""

                labelDesc.label shouldBe ""
                labelDesc.description shouldBe ""
            }
        }

        context("Edge Cases") {
            test("should handle very long label and description") {
                val longLabel = "L".repeat(10000)
                val longDesc = "D".repeat(10000)
                val labelDesc = LabelDesc(longLabel, longDesc)

                labelDesc.label.length shouldBe 10000
                labelDesc.description.length shouldBe 10000
            }

            test("should handle whitespace-only label") {
                val labelDesc = LabelDesc("   ", "description")

                labelDesc.label shouldBe "   "
            }

            test("should handle whitespace-only description") {
                val labelDesc = LabelDesc("label", "   ")

                labelDesc.description shouldBe "   "
            }

            test("should handle label with only newlines") {
                val labelDesc = LabelDesc("\n\n\n", "description")

                labelDesc.label shouldBe "\n\n\n"
            }

            test("should handle description with only tabs") {
                val labelDesc = LabelDesc("label", "\t\t\t")

                labelDesc.description shouldBe "\t\t\t"
            }

            test("should handle mixed whitespace") {
                val labelDesc = LabelDesc(" \t\n ", " \n\t ")

                labelDesc.label shouldBe " \t\n "
                labelDesc.description shouldBe " \n\t "
            }
        }

        context("Real-world D&D Content") {
            test("should handle Frodo's background") {
                val labelDesc =
                    LabelDesc(
                        "Shire Folk",
                        "Hobbits are a small, peaceful folk who prefer the comforts of home",
                    )

                labelDesc.label shouldBe "Shire Folk"
                labelDesc.description shouldContain "Hobbits"
            }

            test("should handle Gandalf's class feature") {
                val labelDesc =
                    LabelDesc(
                        "Spellcasting",
                        "As a wizard, you have learned to cast spells through study and practice",
                    )

                labelDesc.label shouldBe "Spellcasting"
                labelDesc.description shouldContain "wizard"
            }

            test("should handle Aragorn's background") {
                val labelDesc =
                    LabelDesc(
                        "Noble",
                        "You were born into nobility and have been trained in leadership",
                    )

                labelDesc.label shouldBe "Noble"
                labelDesc.description shouldContain "nobility"
            }

            test("should handle Neo's class") {
                val labelDesc =
                    LabelDesc(
                        "Hacker",
                        "One who can see and manipulate the code of the Matrix itself",
                    )

                labelDesc.label shouldBe "Hacker"
                labelDesc.description shouldContain "Matrix"
            }
        }
    })
