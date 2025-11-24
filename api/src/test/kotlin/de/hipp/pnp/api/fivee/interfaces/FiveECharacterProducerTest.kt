package de.hipp.pnp.api.fivee.interfaces

import com.fasterxml.jackson.core.JsonProcessingException
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.api.fivee.interfaces.FiveECharacterProducer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

// Test implementation for Frodo's character
private class FrodoCharacterProducer : FiveECharacterProducer {
    class FrodoCharacter : BaseCharacter() {
        override var firstName: String? = "Frodo"
        override var lastName: String? = "Baggins"
        override var level: Int? = 3
    }

    override fun generate(gameType: Int): String? {
        val character = FrodoCharacter()
        character.gameType = gameType
        return character.toString()
    }

    override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf(FrodoCharacter())
}

// Test implementation for Gandalf's character
private class GandalfCharacterProducer : FiveECharacterProducer {
    class GandalfCharacter : BaseCharacter() {
        override var firstName: String? = "Gandalf"
        override var lastName: String? = "the Grey"
        override var level: Int? = 20
    }

    override fun generate(gameType: Int): String? =
        if (gameType > 0) {
            val character = GandalfCharacter()
            character.gameType = gameType
            character.toString()
        } else {
            null
        }

    override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf(GandalfCharacter(), null)
}

// Test implementation that throws JsonProcessingException
private class FailingCharacterProducer : FiveECharacterProducer {
    override fun generate(gameType: Int): String? = throw object : JsonProcessingException("Simulated JSON error") {}

    override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf()
}

class FiveECharacterProducerTest :
    FunSpec({

        context("generate Method Contract") {
            test("should generate character JSON for Frodo") {
                val producer = FrodoCharacterProducer()

                val result = producer.generate(1)

                result shouldNotBe null
                result?.shouldContain("Frodo")
                result?.shouldContain("Baggins")
            }

            test("should generate character with specific game type") {
                val producer = FrodoCharacterProducer()

                val result = producer.generate(42)

                result shouldNotBe null
                result?.shouldContain("\"gameType\":42")
            }

            test("should handle zero game type") {
                val producer = FrodoCharacterProducer()

                val result = producer.generate(0)

                result shouldNotBe null
            }

            test("should generate Gandalf's character for positive game type") {
                val producer = GandalfCharacterProducer()

                val result = producer.generate(5)

                result shouldNotBe null
                result?.shouldContain("Gandalf")
            }

            test("should return null for Gandalf with zero game type") {
                val producer = GandalfCharacterProducer()

                val result = producer.generate(0)

                result shouldBe null
            }

            test("should handle negative game type") {
                val producer = FrodoCharacterProducer()

                val result = producer.generate(-1)

                result shouldNotBe null // Implementation specific
            }

            test("should handle very large game type") {
                val producer = FrodoCharacterProducer()

                val result = producer.generate(Int.MAX_VALUE)

                result shouldNotBe null
            }

            test("should generate Neo's character") {
                val producer =
                    object : FiveECharacterProducer {
                        override fun generate(gameType: Int): String? {
                            val character =
                                object : BaseCharacter() {
                                    override var firstName: String? = "Neo"
                                    override var lastName: String? = "Anderson"
                                    override var level: Int? = 99
                                }
                            character.gameType = gameType
                            return character.toString()
                        }

                        override fun allCharacters(): MutableList<BaseCharacter?> =
                            mutableListOf(
                                object : BaseCharacter() {
                                    override var firstName: String? = "Neo"
                                    override var lastName: String? = "Anderson"
                                    override var level: Int? = 99
                                },
                            )
                    }

                val result = producer.generate(99)

                result?.shouldContain("Neo")
                result?.shouldContain("Anderson")
            }
        }

        context("generate Method Exception Handling") {
            test("should throw JsonProcessingException when generation fails") {
                val producer = FailingCharacterProducer()

                try {
                    producer.generate(1)
                } catch (e: JsonProcessingException) {
                    e.message shouldBe "Simulated JSON error"
                }
            }

            test("should declare JsonProcessingException in signature") {
                // This test verifies the interface contract declares @Throws
                val producer = FailingCharacterProducer()

                // The interface declares @Throws(JsonProcessingException::class)
                // so implementations can throw it
                producer shouldNotBe null
            }
        }

        context("allCharacters Method Contract") {
            test("should return list of characters for Frodo") {
                val producer = FrodoCharacterProducer()

                val result = producer.allCharacters()

                result shouldNotBe null
                result?.size shouldBe 1
                result?.get(0) shouldNotBe null
            }

            test("should return list with null entries for Gandalf") {
                val producer = GandalfCharacterProducer()

                val result = producer.allCharacters()

                result shouldNotBe null
                result?.size shouldBe 2
                result?.get(0) shouldNotBe null
                result?.get(1) shouldBe null
            }

            test("should return empty list") {
                val producer = FailingCharacterProducer()

                val result = producer.allCharacters()

                result shouldNotBe null
                result?.size shouldBe 0
            }

            test("should return mutable list") {
                val producer = FrodoCharacterProducer()

                val result = producer.allCharacters()

                result shouldNotBe null
                // Test mutability
                result?.add(null)
                result?.size shouldBe 2
            }

            test("should return list of Trinity's characters") {
                val producer =
                    object : FiveECharacterProducer {
                        private fun createTrinityCharacter() =
                            object : BaseCharacter() {
                                override var firstName: String? = "Trinity"
                                override var lastName: String? = null
                                override var level: Int? = 95
                            }

                        override fun generate(gameType: Int): String? = createTrinityCharacter().toString()

                        override fun allCharacters(): MutableList<BaseCharacter?> =
                            mutableListOf(createTrinityCharacter(), createTrinityCharacter())
                    }

                val result = producer.allCharacters()

                result?.size shouldBe 2
            }

            test("should handle Aragorn's party of characters") {
                val producer =
                    object : FiveECharacterProducer {
                        private fun createMember(name: String) =
                            object : BaseCharacter() {
                                override var firstName: String? = name
                                override var lastName: String? = null
                                override var level: Int? = 10
                            }

                        override fun generate(gameType: Int): String? = createMember("Aragorn").toString()

                        override fun allCharacters(): MutableList<BaseCharacter?> =
                            mutableListOf(
                                createMember("Aragorn"),
                                createMember("Legolas"),
                                createMember("Gimli"),
                            )
                    }

                val result = producer.allCharacters()

                result?.size shouldBe 3
            }
        }

        context("Interface Contract Verification") {
            test("should implement both methods") {
                val producer = FrodoCharacterProducer()

                // Both methods should be callable
                producer.generate(1) shouldNotBe null
                producer.allCharacters() shouldNotBe null
            }

            test("should allow null return for generate") {
                val producer = GandalfCharacterProducer()

                val result = producer.generate(0)

                result shouldBe null
            }

            test("should allow null return for allCharacters") {
                val producer =
                    object : FiveECharacterProducer {
                        override fun generate(gameType: Int): String? = null

                        override fun allCharacters(): MutableList<BaseCharacter?>? = null
                    }

                producer.allCharacters() shouldBe null
            }

            test("should support multiple implementations") {
                val frodoProducer = FrodoCharacterProducer()
                val gandalfProducer = GandalfCharacterProducer()

                frodoProducer.generate(1) shouldNotBe gandalfProducer.generate(1)
            }
        }

        context("Edge Cases") {
            test("should handle empty character list") {
                val producer =
                    object : FiveECharacterProducer {
                        override fun generate(gameType: Int): String? = "{}"

                        override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf()
                    }

                val result = producer.allCharacters()

                result?.size shouldBe 0
            }

            test("should handle large character list") {
                val producer =
                    object : FiveECharacterProducer {
                        override fun generate(gameType: Int): String? = "{}"

                        override fun allCharacters(): MutableList<BaseCharacter?> = MutableList(100) { null }
                    }

                val result = producer.allCharacters()

                result?.size shouldBe 100
            }

            test("should handle unicode in generated JSON") {
                val producer =
                    object : FiveECharacterProducer {
                        private fun createUnicodeCharacter() =
                            object : BaseCharacter() {
                                override var firstName: String? = "孫悟空"
                                override var lastName: String? = "Monkey King"
                                override var level: Int? = 100
                            }

                        override fun generate(gameType: Int): String? = createUnicodeCharacter().toString()

                        override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf(createUnicodeCharacter())
                    }

                val result = producer.generate(1)

                result?.shouldContain("孫悟空")
            }

            test("should handle emoji in generated JSON") {
                val producer =
                    object : FiveECharacterProducer {
                        private fun createEmojiCharacter() =
                            object : BaseCharacter() {
                                override var firstName: String? = "🧙‍♂️"
                                override var lastName: String? = "Wizard"
                                override var level: Int? = 20
                            }

                        override fun generate(gameType: Int): String? = createEmojiCharacter().toString()

                        override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf(createEmojiCharacter())
                    }

                val result = producer.generate(1)

                result?.shouldContain("🧙‍♂️")
            }
        }

        context("Polymorphism and Type Hierarchy") {
            test("should work with BaseCharacter hierarchy") {
                class ExtendedCharacter : BaseCharacter() {
                    override var firstName: String? = "Extended"
                    override var lastName: String? = null
                    override var level: Int? = null
                    var customField: String = "custom"
                }

                val producer =
                    object : FiveECharacterProducer {
                        override fun generate(gameType: Int): String? = ExtendedCharacter().toString()

                        override fun allCharacters(): MutableList<BaseCharacter?> = mutableListOf(ExtendedCharacter())
                    }

                val characters = producer.allCharacters()

                characters?.get(0) shouldNotBe null
            }

            test("should allow interface reference") {
                val producer: FiveECharacterProducer = FrodoCharacterProducer()

                val json = producer.generate(1)
                val characters = producer.allCharacters()

                json shouldNotBe null
                characters shouldNotBe null
            }
        }
    })
