package de.hipp.pnp.api.fivee.abstracts

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SpeciesTest :
    FunSpec({

        // Concrete implementation for testing
        class TestSpecies : Species() {
            override var name: String? = null
            override var description: String? = null
        }

        context("Construction and Default Values") {
            test("should create species with default values") {
                val species = TestSpecies()

                species.name shouldBe null
                species.description shouldBe null
                species.attributes shouldNotBe null
                species.attributes.shouldBeEmpty()
            }

            test("should allow setting name") {
                val species = TestSpecies()
                species.name = "Hobbit"

                species.name shouldBe "Hobbit"
            }

            test("should allow setting description") {
                val species = TestSpecies()
                species.description = "A small folk who live in the Shire"

                species.description shouldBe "A small folk who live in the Shire"
            }

            test("should initialize with empty attributes map") {
                val species = TestSpecies()

                species.attributes.size shouldBe 0
            }
        }

        context("addAttributeChange Basic Operations") {
            test("should add attribute with value") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 2)

                species.attributes.keys shouldContain "strength"
                species.attributes["strength"] shouldBe 2
            }

            test("should add multiple different attributes") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 2)
                species.addAttributeChange("dexterity", -1)
                species.addAttributeChange("wisdom", 1)

                species.attributes shouldHaveSize 3
                species.attributes["strength"] shouldBe 2
                species.attributes["dexterity"] shouldBe -1
                species.attributes["wisdom"] shouldBe 1
            }

            test("should handle Hobbit attributes") {
                val species = TestSpecies()
                species.name = "Hobbit"

                species.addAttributeChange("dexterity", 2)
                species.addAttributeChange("constitution", 1)

                species.attributes["dexterity"] shouldBe 2
                species.attributes["constitution"] shouldBe 1
            }

            test("should handle Elf attributes") {
                val species = TestSpecies()
                species.name = "Elf"

                species.addAttributeChange("intelligence", 2)
                species.addAttributeChange("charisma", 1)

                species.attributes.shouldHaveSize(2)
            }
        }

        context("addAttributeChange with Null Values") {
            test("should accept null key") {
                val species = TestSpecies()

                species.addAttributeChange(null, 5)

                species.attributes.keys shouldContain null
                species.attributes[null] shouldBe 5
            }

            test("should accept null value") {
                val species = TestSpecies()

                species.addAttributeChange("strength", null)

                species.attributes.keys shouldContain "strength"
                species.attributes["strength"] shouldBe null
            }

            test("should accept both null key and null value") {
                val species = TestSpecies()

                species.addAttributeChange(null, null)

                species.attributes.keys shouldContain null
                species.attributes[null] shouldBe null
            }

            test("should handle multiple null keys") {
                val species = TestSpecies()

                species.addAttributeChange(null, 5)
                species.addAttributeChange(null, 10)

                species.attributes[null] shouldBe 10 // Should overwrite
            }
        }

        context("addAttributeChange with Empty and Blank Strings") {
            test("should accept empty string as key") {
                val species = TestSpecies()

                species.addAttributeChange("", 5)

                species.attributes.keys shouldContain ""
                species.attributes[""] shouldBe 5
            }

            test("should accept blank string as key") {
                val species = TestSpecies()

                species.addAttributeChange("   ", 5)

                species.attributes.keys shouldContain "   "
                species.attributes["   "] shouldBe 5
            }

            test("should treat empty and blank strings as different keys") {
                val species = TestSpecies()

                species.addAttributeChange("", 1)
                species.addAttributeChange(" ", 2)

                species.attributes shouldHaveSize 2
                species.attributes[""] shouldBe 1
                species.attributes[" "] shouldBe 2
            }
        }

        context("addAttributeChange with Special Characters") {
            test("should handle unicode key") {
                val species = TestSpecies()

                species.addAttributeChange("力量", 3)

                species.attributes["力量"] shouldBe 3
            }

            test("should handle emoji key") {
                val species = TestSpecies()

                species.addAttributeChange("💪", 5)

                species.attributes["💪"] shouldBe 5
            }

            test("should handle key with spaces") {
                val species = TestSpecies()

                species.addAttributeChange("max health", 10)

                species.attributes["max health"] shouldBe 10
            }

            test("should handle key with special characters") {
                val species = TestSpecies()

                species.addAttributeChange("strength+bonus", 2)
                species.addAttributeChange("dex-modifier", -1)

                species.attributes["strength+bonus"] shouldBe 2
                species.attributes["dex-modifier"] shouldBe -1
            }

            test("should handle very long key") {
                val species = TestSpecies()
                val longKey = "attribute-" + "x".repeat(1000)

                species.addAttributeChange(longKey, 1)

                species.attributes[longKey] shouldBe 1
            }
        }

        context("addAttributeChange Overwriting Values") {
            test("should overwrite existing attribute") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 2)
                species.addAttributeChange("strength", 5)

                species.attributes["strength"] shouldBe 5
                species.attributes shouldHaveSize 1
            }

            test("should overwrite with null value") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 5)
                species.addAttributeChange("strength", null)

                species.attributes["strength"] shouldBe null
            }

            test("should overwrite null with value") {
                val species = TestSpecies()

                species.addAttributeChange("strength", null)
                species.addAttributeChange("strength", 5)

                species.attributes["strength"] shouldBe 5
            }

            test("should handle Neo's power level evolution") {
                val species = TestSpecies()
                species.name = "The One"

                species.addAttributeChange("power", 50)
                species.addAttributeChange("power", 99)
                species.addAttributeChange("power", 100)

                species.attributes["power"] shouldBe 100
            }
        }

        context("addAttributeChange with Different Value Types") {
            test("should handle zero value") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 0)

                species.attributes["strength"] shouldBe 0
            }

            test("should handle negative values") {
                val species = TestSpecies()

                species.addAttributeChange("strength", -5)
                species.addAttributeChange("dexterity", -10)

                species.attributes["strength"] shouldBe -5
                species.attributes["dexterity"] shouldBe -10
            }

            test("should handle positive values") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 5)
                species.addAttributeChange("dexterity", 10)

                species.attributes["strength"] shouldBe 5
                species.attributes["dexterity"] shouldBe 10
            }

            test("should handle very large values") {
                val species = TestSpecies()

                species.addAttributeChange("godmode", Int.MAX_VALUE)

                species.attributes["godmode"] shouldBe Int.MAX_VALUE
            }

            test("should handle very small values") {
                val species = TestSpecies()

                species.addAttributeChange("cursed", Int.MIN_VALUE)

                species.attributes["cursed"] shouldBe Int.MIN_VALUE
            }
        }

        context("Attributes Map Manipulation") {
            test("should allow direct map access") {
                val species = TestSpecies()
                species.addAttributeChange("strength", 2)

                val attrs = species.attributes

                attrs.keys shouldContain "strength"
                attrs["strength"] shouldBe 2
            }

            test("should allow direct map modification") {
                val species = TestSpecies()

                species.attributes["custom"] = 999

                species.attributes["custom"] shouldBe 999
            }

            test("should maintain map reference") {
                val species = TestSpecies()
                val originalMap = species.attributes

                species.addAttributeChange("strength", 5)

                originalMap shouldBe species.attributes
                originalMap["strength"] shouldBe 5
            }

            test("should support map iteration") {
                val species = TestSpecies()
                species.addAttributeChange("strength", 2)
                species.addAttributeChange("dexterity", 3)

                var count = 0
                for ((key, value) in species.attributes) {
                    count++
                }

                count shouldBe 2
            }
        }

        context("Species with Complex Scenarios") {
            test("should handle Frodo's Hobbit species") {
                val species = TestSpecies()
                species.name = "Hobbit"
                species.description = "Halflings of the Shire"

                species.addAttributeChange("dexterity", 2)
                species.addAttributeChange("constitution", 1)
                species.addAttributeChange("strength", -1)

                species.name shouldBe "Hobbit"
                species.attributes shouldHaveSize 3
                species.attributes["dexterity"] shouldBe 2
                species.attributes["constitution"] shouldBe 1
                species.attributes["strength"] shouldBe -1
            }

            test("should handle Gandalf's Istari species") {
                val species = TestSpecies()
                species.name = "Istari"
                species.description = "Wizards sent from Valinor"

                species.addAttributeChange("wisdom", 5)
                species.addAttributeChange("intelligence", 5)
                species.addAttributeChange("charisma", 3)

                species.attributes shouldHaveSize 3
            }

            test("should handle Aragorn's Human species") {
                val species = TestSpecies()
                species.name = "Human"
                species.description = "Versatile and adaptable"

                species.addAttributeChange("strength", 1)
                species.addAttributeChange("dexterity", 1)

                species.attributes shouldHaveSize 2
            }

            test("should handle Trinity's Operator species") {
                val species = TestSpecies()
                species.name = "Operator"

                species.addAttributeChange("hacking", 95)
                species.addAttributeChange("combat", 90)

                species.attributes["hacking"] shouldBe 95
                species.attributes["combat"] shouldBe 90
            }
        }

        context("Name and Description Properties") {
            test("should handle null name") {
                val species = TestSpecies()
                species.name = null

                species.name shouldBe null
            }

            test("should handle empty name") {
                val species = TestSpecies()
                species.name = ""

                species.name shouldBe ""
            }

            test("should handle unicode name") {
                val species = TestSpecies()
                species.name = "精霊"

                species.name shouldBe "精霊"
            }

            test("should handle emoji in name") {
                val species = TestSpecies()
                species.name = "🧝 Elf"

                species.name shouldBe "🧝 Elf"
            }

            test("should handle very long name") {
                val species = TestSpecies()
                species.name = "A".repeat(1000)

                species.name shouldBe "A".repeat(1000)
            }

            test("should handle null description") {
                val species = TestSpecies()
                species.description = null

                species.description shouldBe null
            }

            test("should handle empty description") {
                val species = TestSpecies()
                species.description = ""

                species.description shouldBe ""
            }

            test("should handle unicode description") {
                val species = TestSpecies()
                species.description = "これは説明です"

                species.description shouldBe "これは説明です"
            }

            test("should handle emoji in description") {
                val species = TestSpecies()
                species.description = "A powerful species ⚡"

                species.description shouldBe "A powerful species ⚡"
            }

            test("should handle very long description") {
                val species = TestSpecies()
                species.description = "X".repeat(5000)

                species.description?.length shouldBe 5000
            }
        }

        context("Inheritance Behavior") {
            test("should support subclass with additional properties") {
                class ExtendedSpecies : Species() {
                    override var name: String? = null
                    override var description: String? = null
                    var rarity: String = "common"
                }

                val species = ExtendedSpecies()
                species.name = "Dragon"
                species.rarity = "legendary"
                species.addAttributeChange("strength", 10)

                species.name shouldBe "Dragon"
                species.rarity shouldBe "legendary"
                species.attributes["strength"] shouldBe 10
            }
        }

        context("Edge Cases") {
            test("should handle rapid successive attribute changes") {
                val species = TestSpecies()

                for (i in 1..100) {
                    species.addAttributeChange("counter", i)
                }

                species.attributes["counter"] shouldBe 100
            }

            test("should handle many different attributes") {
                val species = TestSpecies()

                for (i in 1..100) {
                    species.addAttributeChange("attr_$i", i)
                }

                species.attributes shouldHaveSize 100
            }

            test("should maintain attribute integrity after multiple operations") {
                val species = TestSpecies()

                species.addAttributeChange("strength", 5)
                species.addAttributeChange("dexterity", 3)
                species.addAttributeChange("strength", 10)

                species.attributes shouldHaveSize 2
                species.attributes["strength"] shouldBe 10
                species.attributes["dexterity"] shouldBe 3
            }
        }
    })
