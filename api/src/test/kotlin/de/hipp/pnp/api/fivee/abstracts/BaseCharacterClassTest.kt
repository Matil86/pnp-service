package de.hipp.pnp.api.fivee.abstracts

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BaseCharacterClassTest :
    FunSpec({

        // Concrete implementation for testing
        class TestCharacterClass : BaseCharacterClass() {
            override var name: String = ""
            override var level: Int? = 1
        }

        context("Construction and Default Values") {
            test("should create character class with default values") {
                val characterClass = TestCharacterClass()

                characterClass.name shouldBe ""
                characterClass.level shouldBe 1
            }

            test("should allow setting name") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Wizard"

                characterClass.name shouldBe "Wizard"
            }

            test("should allow setting level") {
                val characterClass = TestCharacterClass()
                characterClass.level = 10

                characterClass.level shouldBe 10
            }

            test("should handle Frodo's Rogue class") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Rogue"
                characterClass.level = 3

                characterClass.name shouldBe "Rogue"
                characterClass.level shouldBe 3
            }
        }

        context("increaseLevel with Non-Null Initial Level") {
            test("should increase level by amount") {
                val characterClass = TestCharacterClass()
                characterClass.level = 5

                characterClass.increaseLevel(3)

                characterClass.level shouldBe 8
            }

            test("should increase level by 1") {
                val characterClass = TestCharacterClass()
                characterClass.level = 1

                characterClass.increaseLevel(1)

                characterClass.level shouldBe 2
            }

            test("should increase level by large amount") {
                val characterClass = TestCharacterClass()
                characterClass.level = 1

                characterClass.increaseLevel(10)

                characterClass.level shouldBe 11
            }

            test("should handle Gandalf leveling from 19 to 20") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Wizard"
                characterClass.level = 19

                characterClass.increaseLevel(1)

                characterClass.level shouldBe 20
            }

            test("should handle Aragorn gaining 5 levels") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Ranger"
                characterClass.level = 10

                characterClass.increaseLevel(5)

                characterClass.level shouldBe 15
            }
        }

        context("increaseLevel with Null Initial Level") {
            test("should set level to amount when level is null") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(5)

                characterClass.level shouldBe 5
            }

            test("should set level to 1 when null and amount is 1") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(1)

                characterClass.level shouldBe 1
            }

            test("should set level to 10 when null and amount is 10") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(10)

                characterClass.level shouldBe 10
            }

            test("should handle Neo starting from null level") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Hacker"
                characterClass.level = null

                characterClass.increaseLevel(99)

                characterClass.level shouldBe 99
            }
        }

        context("increaseLevel with Zero Amount") {
            test("should not change level when increase by 0") {
                val characterClass = TestCharacterClass()
                characterClass.level = 5

                characterClass.increaseLevel(0)

                characterClass.level shouldBe 5
            }

            test("should set to 0 when level is null and amount is 0") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(0)

                characterClass.level shouldBe 0
            }
        }

        context("increaseLevel with Negative Amount") {
            test("should decrease level when amount is negative") {
                val characterClass = TestCharacterClass()
                characterClass.level = 10

                characterClass.increaseLevel(-3)

                characterClass.level shouldBe 7
            }

            test("should allow level to go negative") {
                val characterClass = TestCharacterClass()
                characterClass.level = 2

                characterClass.increaseLevel(-5)

                characterClass.level shouldBe -3
            }

            test("should set negative level when null") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(-5)

                characterClass.level shouldBe -5
            }

            test("should handle Trinity losing levels") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Operator"
                characterClass.level = 20

                characterClass.increaseLevel(-5)

                characterClass.level shouldBe 15
            }
        }

        context("increaseLevel Multiple Times") {
            test("should handle multiple consecutive increases") {
                val characterClass = TestCharacterClass()
                characterClass.level = 1

                characterClass.increaseLevel(2)
                characterClass.increaseLevel(3)
                characterClass.increaseLevel(4)

                characterClass.level shouldBe 10
            }

            test("should handle increases from null then non-null") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(5) // null -> 5
                characterClass.increaseLevel(3) // 5 -> 8

                characterClass.level shouldBe 8
            }

            test("should handle mixed positive and negative changes") {
                val characterClass = TestCharacterClass()
                characterClass.level = 10

                characterClass.increaseLevel(5) // 10 -> 15
                characterClass.increaseLevel(-3) // 15 -> 12
                characterClass.increaseLevel(2) // 12 -> 14

                characterClass.level shouldBe 14
            }

            test("should handle Frodo's leveling journey") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Rogue"
                characterClass.level = 1

                characterClass.increaseLevel(1) // 1 -> 2
                characterClass.increaseLevel(1) // 2 -> 3
                characterClass.increaseLevel(1) // 3 -> 4

                characterClass.level shouldBe 4
            }
        }

        context("Edge Cases and Boundary Values") {
            test("should handle very large increase") {
                val characterClass = TestCharacterClass()
                characterClass.level = 1

                characterClass.increaseLevel(1000000)

                characterClass.level shouldBe 1000001
            }

            test("should handle very large negative increase") {
                val characterClass = TestCharacterClass()
                characterClass.level = 1000000

                characterClass.increaseLevel(-999999)

                characterClass.level shouldBe 1
            }

            test("should handle maximum int value approach") {
                val characterClass = TestCharacterClass()
                characterClass.level = Int.MAX_VALUE - 10

                characterClass.increaseLevel(5)

                characterClass.level shouldBe Int.MAX_VALUE - 5
            }

            test("should handle minimum int value approach") {
                val characterClass = TestCharacterClass()
                characterClass.level = Int.MIN_VALUE + 10

                characterClass.increaseLevel(-5)

                characterClass.level shouldBe Int.MIN_VALUE + 5
            }

            test("should handle starting from level 0") {
                val characterClass = TestCharacterClass()
                characterClass.level = 0

                characterClass.increaseLevel(5)

                characterClass.level shouldBe 5
            }
        }

        context("Name Property") {
            test("should handle empty string name") {
                val characterClass = TestCharacterClass()
                characterClass.name = ""

                characterClass.name shouldBe ""
            }

            test("should handle unicode name") {
                val characterClass = TestCharacterClass()
                characterClass.name = "武士"

                characterClass.name shouldBe "武士"
            }

            test("should handle emoji in name") {
                val characterClass = TestCharacterClass()
                characterClass.name = "⚔️ Warrior"

                characterClass.name shouldBe "⚔️ Warrior"
            }

            test("should handle very long name") {
                val characterClass = TestCharacterClass()
                characterClass.name = "A".repeat(1000)

                characterClass.name shouldBe "A".repeat(1000)
            }

            test("should handle special characters in name") {
                val characterClass = TestCharacterClass()
                characterClass.name = "Wizard-Sorcerer/Warlock"

                characterClass.name shouldBe "Wizard-Sorcerer/Warlock"
            }
        }

        context("Level State Transitions") {
            test("should transition from null to zero to positive") {
                val characterClass = TestCharacterClass()
                characterClass.level = null

                characterClass.increaseLevel(0) // null -> 0
                characterClass.level shouldBe 0

                characterClass.increaseLevel(5) // 0 -> 5
                characterClass.level shouldBe 5
            }

            test("should transition through zero") {
                val characterClass = TestCharacterClass()
                characterClass.level = 3

                characterClass.increaseLevel(-3) // 3 -> 0
                characterClass.level shouldBe 0

                characterClass.increaseLevel(-2) // 0 -> -2
                characterClass.level shouldBe -2
            }

            test("should maintain level consistency after operations") {
                val characterClass = TestCharacterClass()
                characterClass.level = 10

                val originalLevel = characterClass.level
                characterClass.increaseLevel(5)
                characterClass.increaseLevel(-5)

                characterClass.level shouldBe originalLevel
            }
        }

        context("Inheritance Behavior") {
            test("should support subclass with additional logic") {
                class SpecialCharacterClass : BaseCharacterClass() {
                    override var name: String = ""
                    override var level: Int? = 1
                    var maxLevel: Int = 20

                    fun canLevelUp(): Boolean = (level ?: 0) < maxLevel
                }

                val characterClass = SpecialCharacterClass()
                characterClass.level = 19
                characterClass.maxLevel = 20

                characterClass.canLevelUp() shouldBe true
                characterClass.increaseLevel(1)
                characterClass.canLevelUp() shouldBe false
            }
        }

        context("Idempotency and Consistency") {
            test("should produce same result for same operations") {
                val class1 = TestCharacterClass()
                val class2 = TestCharacterClass()

                class1.level = 5
                class2.level = 5

                class1.increaseLevel(3)
                class2.increaseLevel(3)

                class1.level shouldBe class2.level
            }

            test("should handle order of operations") {
                val characterClass = TestCharacterClass()
                characterClass.level = 10

                characterClass.increaseLevel(5)
                characterClass.increaseLevel(3)

                characterClass.level shouldBe 18

                // Same operations in one call
                val characterClass2 = TestCharacterClass()
                characterClass2.level = 10
                characterClass2.increaseLevel(8)

                characterClass2.level shouldBe 18
            }
        }
    })
