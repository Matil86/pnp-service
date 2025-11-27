package de.hipp.pnp.api.fivee

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class E5EGameTypesTest :
    FunSpec({

        context("Enum Values") {
            test("should have GENEFUNK enum value") {
                val genefunk = E5EGameTypes.GENEFUNK

                genefunk shouldNotBe null
                genefunk.value shouldBe 0
            }

            test("should have exactly one enum value") {
                val entries = E5EGameTypes.entries

                entries.size shouldBe 1
                entries shouldContain E5EGameTypes.GENEFUNK
            }

            test("should provide entries through entries property") {
                val entries = E5EGameTypes.entries

                entries shouldNotBe null
                entries.isNotEmpty() shouldBe true
            }
        }

        context("Enum Value Field") {
            test("should have correct value for GENEFUNK") {
                E5EGameTypes.GENEFUNK.value shouldBe 0
            }

            test("should allow direct access to value field") {
                val value = E5EGameTypes.GENEFUNK.value
                value shouldBe 0
            }
        }

        context("Companion Object fromValue") {
            test("should return GENEFUNK for value 0") {
                val result = E5EGameTypes.fromValue(0)

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should return null for unknown value") {
                val result = E5EGameTypes.fromValue(999)

                result shouldBe null
            }

            test("should return null for negative value") {
                val result = E5EGameTypes.fromValue(-1)

                result shouldBe null
            }

            test("should return null for null value with no default") {
                val result = E5EGameTypes.fromValue(null)

                result shouldBe null
            }

            test("should return default value for null input") {
                val result = E5EGameTypes.fromValue(null, E5EGameTypes.GENEFUNK)

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should return default value for unknown input") {
                val result = E5EGameTypes.fromValue(42, E5EGameTypes.GENEFUNK)

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should prioritize match over default") {
                val result = E5EGameTypes.fromValue(0, null)

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should handle Frodo's GENEFUNK campaign") {
                val result = E5EGameTypes.fromValue(0)

                result shouldBe E5EGameTypes.GENEFUNK
                result?.value shouldBe 0
            }

            test("should handle value 1 with GENEFUNK default for Gandalf") {
                val result = E5EGameTypes.fromValue(1, E5EGameTypes.GENEFUNK)

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should return null for very large value") {
                val result = E5EGameTypes.fromValue(Int.MAX_VALUE)

                result shouldBe null
            }

            test("should return null for very small value") {
                val result = E5EGameTypes.fromValue(Int.MIN_VALUE)

                result shouldBe null
            }

            test("should handle Neo's unregistered game type with default") {
                val result = E5EGameTypes.fromValue(99, E5EGameTypes.GENEFUNK)

                result shouldBe E5EGameTypes.GENEFUNK
            }
        }

        context("Enum Standard Methods") {
            test("should return correct name") {
                E5EGameTypes.GENEFUNK.name shouldBe "GENEFUNK"
            }

            test("should return correct ordinal") {
                E5EGameTypes.GENEFUNK.ordinal shouldBe 0
            }

            test("should support valueOf") {
                val result = E5EGameTypes.valueOf("GENEFUNK")

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should have consistent toString") {
                val toString = E5EGameTypes.GENEFUNK.toString()

                toString shouldBe "GENEFUNK"
            }
        }

        context("fromValue Optional Default Parameter") {
            test("should work with single parameter (no default)") {
                val result = E5EGameTypes.fromValue(0)

                result shouldBe E5EGameTypes.GENEFUNK
            }

            test("should work with explicit null default") {
                val result = E5EGameTypes.fromValue(999, null)

                result shouldBe null
            }

            test("should work with explicit enum default") {
                val result = E5EGameTypes.fromValue(999, E5EGameTypes.GENEFUNK)

                result shouldBe E5EGameTypes.GENEFUNK
            }
        }

        context("Edge Cases and Boundary Testing") {
            test("should handle zero value correctly") {
                val result = E5EGameTypes.fromValue(0)

                result shouldBe E5EGameTypes.GENEFUNK
                result?.value shouldBe 0
            }

            test("should handle null with null default") {
                val result = E5EGameTypes.fromValue(null, null)

                result shouldBe null
            }

            test("should iterate through all entries") {
                val entries = E5EGameTypes.entries
                var foundGenefunk = false

                for (entry in entries) {
                    if (entry == E5EGameTypes.GENEFUNK) {
                        foundGenefunk = true
                    }
                }

                foundGenefunk shouldBe true
            }

            test("should handle Aragorn's lookup with fallback strategy") {
                val userInput = 5
                val result = E5EGameTypes.fromValue(userInput, E5EGameTypes.GENEFUNK)

                result shouldBe E5EGameTypes.GENEFUNK // fallback to default
            }
        }

        context("Value to Enum Mapping") {
            test("should create consistent mapping for value 0") {
                val result1 = E5EGameTypes.fromValue(0)
                val result2 = E5EGameTypes.fromValue(0)

                result1 shouldBe result2
            }

            test("should maintain referential equality for same enum") {
                val result1 = E5EGameTypes.fromValue(0)
                val result2 = E5EGameTypes.GENEFUNK

                result1 shouldBe result2
            }
        }
    })
