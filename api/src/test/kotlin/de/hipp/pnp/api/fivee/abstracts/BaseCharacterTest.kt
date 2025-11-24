package de.hipp.pnp.api.fivee.abstracts

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class BaseCharacterTest :
    FunSpec({
        val mapper = jacksonObjectMapper()

        // Concrete implementation for testing
        class TestCharacter : BaseCharacter() {
            override var firstName: String? = null
            override var lastName: String? = null
            override var level: Int? = null
        }

        context("Construction and Default Values") {
            test("should create character with default values") {
                val character = TestCharacter()

                character.gameType shouldBe 0
                character.firstName shouldBe null
                character.lastName shouldBe null
                character.level shouldBe null
                character.characterClasses shouldBe null
            }

            test("should allow setting gameType") {
                val character = TestCharacter()
                character.gameType = 42

                character.gameType shouldBe 42
            }

            test("should allow setting firstName") {
                val character = TestCharacter()
                character.firstName = "Frodo"

                character.firstName shouldBe "Frodo"
            }

            test("should allow setting lastName") {
                val character = TestCharacter()
                character.lastName = "Baggins"

                character.lastName shouldBe "Baggins"
            }

            test("should allow setting level") {
                val character = TestCharacter()
                character.level = 10

                character.level shouldBe 10
            }
        }

        context("Character Classes Management") {
            test("should allow setting character classes") {
                val character = TestCharacter()
                val classes = mutableSetOf<BaseCharacterClass?>()

                character.characterClasses = classes

                character.characterClasses shouldBe classes
            }

            test("should handle empty character classes set") {
                val character = TestCharacter()
                character.characterClasses = mutableSetOf()

                character.characterClasses shouldNotBe null
                character.characterClasses?.size shouldBe 0
            }

            test("should handle null character classes") {
                val character = TestCharacter()
                character.characterClasses = null

                character.characterClasses shouldBe null
            }
        }

        context("toString JSON Serialization") {
            test("should serialize to JSON string") {
                val character = TestCharacter()
                character.gameType = 1
                character.firstName = "Frodo"
                character.lastName = "Baggins"
                character.level = 3

                val json = character.toString()

                json shouldNotBe null
                json.shouldContain("\"gameType\":1")
                json.shouldContain("\"firstName\":\"Frodo\"")
                json.shouldContain("\"lastName\":\"Baggins\"")
                json.shouldContain("\"level\":3")
            }

            test("should handle null firstName in JSON") {
                val character = TestCharacter()
                character.gameType = 0
                character.firstName = null

                val json = character.toString()

                json.shouldContain("\"gameType\":0")
            }

            test("should handle null lastName in JSON") {
                val character = TestCharacter()
                character.lastName = null

                val json = character.toString()

                json shouldNotBe null
            }

            test("should handle null level in JSON") {
                val character = TestCharacter()
                character.level = null

                val json = character.toString()

                json shouldNotBe null
            }

            test("should serialize Gandalf the Grey") {
                val character = TestCharacter()
                character.firstName = "Gandalf"
                character.lastName = "the Grey"
                character.level = 20

                val json = character.toString()

                json.shouldContain("\"firstName\":\"Gandalf\"")
                json.shouldContain("\"lastName\":\"the Grey\"")
                json.shouldContain("\"level\":20")
            }

            test("should serialize Aragorn son of Arathorn") {
                val character = TestCharacter()
                character.firstName = "Aragorn"
                character.lastName = "son of Arathorn"
                character.level = 15

                val json = character.toString()

                json.shouldContain("\"firstName\":\"Aragorn\"")
                json.shouldContain("\"lastName\":\"son of Arathorn\"")
            }

            test("should handle unicode characters in name") {
                val character = TestCharacter()
                character.firstName = "孫悟空"
                character.lastName = "Monkey King"

                val json = character.toString()

                json.shouldContain("孫悟空")
                json.shouldContain("Monkey King")
            }

            test("should handle emoji in name") {
                val character = TestCharacter()
                character.firstName = "Neo"
                character.lastName = "⚡"

                val json = character.toString()

                json.shouldContain("Neo")
                json.shouldContain("⚡")
            }

            test("should handle special characters in name") {
                val character = TestCharacter()
                character.firstName = "O'Malley"
                character.lastName = "McTavish-O'Brien"

                val json = character.toString()

                json.shouldContain("O'Malley")
                json.shouldContain("McTavish-O'Brien")
            }

            test("should handle empty string names") {
                val character = TestCharacter()
                character.firstName = ""
                character.lastName = ""

                val json = character.toString()

                json.shouldContain("\"firstName\":\"\"")
                json.shouldContain("\"lastName\":\"\"")
            }

            test("should handle very long names") {
                val character = TestCharacter()
                character.firstName = "A".repeat(1000)

                val json = character.toString()

                json.shouldContain("A".repeat(1000))
            }

            test("should exclude toString from JSON due to JsonIgnore") {
                val character = TestCharacter()
                character.firstName = "Test"

                val json = character.toString()

                json.shouldNotContain("toString")
            }
        }

        context("JSON Deserialization") {
            test("should deserialize from JSON") {
                val json = """{"gameType":1,"firstName":"Frodo","lastName":"Baggins","level":3}"""
                val character = mapper.readValue<TestCharacter>(json)

                character.gameType shouldBe 1
                character.firstName shouldBe "Frodo"
                character.lastName shouldBe "Baggins"
                character.level shouldBe 3
            }

            test("should deserialize with null values") {
                val json = """{"gameType":0}"""
                val character = mapper.readValue<TestCharacter>(json)

                character.gameType shouldBe 0
                character.firstName shouldBe null
                character.lastName shouldBe null
                character.level shouldBe null
            }

            test("should deserialize Neo's character") {
                val json = """{"gameType":99,"firstName":"Neo","lastName":"Anderson","level":99}"""
                val character = mapper.readValue<TestCharacter>(json)

                character.firstName shouldBe "Neo"
                character.lastName shouldBe "Anderson"
                character.level shouldBe 99
            }

            test("should deserialize Trinity's character") {
                val json = """{"firstName":"Trinity","level":95}"""
                val character = mapper.readValue<TestCharacter>(json)

                character.firstName shouldBe "Trinity"
                character.level shouldBe 95
            }
        }

        context("Round-trip Serialization") {
            test("should maintain data through serialize-deserialize cycle") {
                val original = TestCharacter()
                original.gameType = 5
                original.firstName = "Frodo"
                original.lastName = "Baggins"
                original.level = 3

                val json = original.toString()
                val deserialized = mapper.readValue<TestCharacter>(json)

                deserialized.gameType shouldBe original.gameType
                deserialized.firstName shouldBe original.firstName
                deserialized.lastName shouldBe original.lastName
                deserialized.level shouldBe original.level
            }

            test("should handle unicode in round-trip") {
                val original = TestCharacter()
                original.firstName = "孫悟空"

                val json = original.toString()
                val deserialized = mapper.readValue<TestCharacter>(json)

                deserialized.firstName shouldBe "孫悟空"
            }

            test("should handle emoji in round-trip") {
                val original = TestCharacter()
                original.firstName = "🧙‍♂️ Gandalf"

                val json = original.toString()
                val deserialized = mapper.readValue<TestCharacter>(json)

                deserialized.firstName shouldBe "🧙‍♂️ Gandalf"
            }
        }

        context("Edge Cases and Error Handling") {
            test("should handle negative gameType") {
                val character = TestCharacter()
                character.gameType = -1

                character.gameType shouldBe -1
            }

            test("should handle negative level") {
                val character = TestCharacter()
                character.level = -5

                character.level shouldBe -5
            }

            test("should handle zero level") {
                val character = TestCharacter()
                character.level = 0

                character.level shouldBe 0
            }

            test("should handle very large gameType") {
                val character = TestCharacter()
                character.gameType = Int.MAX_VALUE

                character.gameType shouldBe Int.MAX_VALUE
            }

            test("should handle very large level") {
                val character = TestCharacter()
                character.level = Int.MAX_VALUE

                character.level shouldBe Int.MAX_VALUE
            }
        }

        context("Inheritance Behavior") {
            test("should support subclass with additional properties") {
                class ExtendedCharacter : BaseCharacter() {
                    override var firstName: String? = null
                    override var lastName: String? = null
                    override var level: Int? = null
                    var customProperty: String = "custom"
                }

                val character = ExtendedCharacter()
                character.firstName = "Extended"
                character.customProperty = "test"

                character.firstName shouldBe "Extended"
                character.customProperty shouldBe "test"
            }

            test("should call overridden toString") {
                val character = TestCharacter()
                character.firstName = "Test"

                val result = character.toString()

                result shouldNotBe null
                result.shouldContain("firstName")
            }
        }
    })
