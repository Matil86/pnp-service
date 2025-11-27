package de.hipp.pnp.api.rabbitMq

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class MessageHeaderTest :
    FunSpec({
        val mapper = jacksonObjectMapper()

        context("Construction and Default Values") {
            test("should create MessageHeader with default values") {
                val header = MessageHeader()

                header.externalId shouldBe ""
                header.roles.size shouldBe 0
            }

            test("should have empty externalId by default") {
                val header = MessageHeader()

                header.externalId shouldBe ""
            }

            test("should have empty roles array by default") {
                val header = MessageHeader()

                header.roles shouldBe emptyArray()
            }
        }

        context("ExternalId Property") {
            test("should allow setting externalId") {
                val header = MessageHeader()
                header.externalId = "ext-123"

                header.externalId shouldBe "ext-123"
            }

            test("should handle empty externalId") {
                val header = MessageHeader()
                header.externalId = ""

                header.externalId shouldBe ""
            }

            test("should handle long externalId") {
                val header = MessageHeader()
                val longId = "id-" + "x".repeat(1000)
                header.externalId = longId

                header.externalId shouldBe longId
            }

            test("should handle unicode externalId") {
                val header = MessageHeader()
                header.externalId = "ユーザー123"

                header.externalId shouldBe "ユーザー123"
            }

            test("should handle emoji externalId") {
                val header = MessageHeader()
                header.externalId = "user-🎮-123"

                header.externalId shouldBe "user-🎮-123"
            }

            test("should handle Frodo's externalId") {
                val header = MessageHeader()
                header.externalId = "hobbit-frodo-baggins"

                header.externalId shouldBe "hobbit-frodo-baggins"
            }

            test("should handle Gandalf's externalId") {
                val header = MessageHeader()
                header.externalId = "wizard-gandalf-grey"

                header.externalId shouldBe "wizard-gandalf-grey"
            }

            test("should handle special characters in externalId") {
                val header = MessageHeader()
                header.externalId = "user@domain.com-123"

                header.externalId shouldBe "user@domain.com-123"
            }

            test("should handle UUID format externalId") {
                val header = MessageHeader()
                header.externalId = "550e8400-e29b-41d4-a716-446655440000"

                header.externalId shouldBe "550e8400-e29b-41d4-a716-446655440000"
            }

            test("should handle whitespace in externalId") {
                val header = MessageHeader()
                header.externalId = "  spaces  "

                header.externalId shouldBe "  spaces  "
            }
        }

        context("Roles Array Property") {
            test("should allow setting single role") {
                val header = MessageHeader()
                header.roles = arrayOf("admin")

                header.roles.size shouldBe 1
                header.roles[0] shouldBe "admin"
            }

            test("should allow setting multiple roles") {
                val header = MessageHeader()
                header.roles = arrayOf("admin", "user", "moderator")

                header.roles.size shouldBe 3
                header.roles[0] shouldBe "admin"
                header.roles[1] shouldBe "user"
                header.roles[2] shouldBe "moderator"
            }

            test("should allow empty roles array") {
                val header = MessageHeader()
                header.roles = emptyArray()

                header.roles.size shouldBe 0
            }

            test("should handle Frodo's roles") {
                val header = MessageHeader()
                header.roles = arrayOf("ring_bearer", "hobbit", "adventurer")

                header.roles.size shouldBe 3
                header.roles shouldBe arrayOf("ring_bearer", "hobbit", "adventurer")
            }

            test("should handle Gandalf's roles") {
                val header = MessageHeader()
                header.roles = arrayOf("wizard", "istari", "white_council")

                header.roles.size shouldBe 3
            }

            test("should handle Aragorn's roles") {
                val header = MessageHeader()
                header.roles = arrayOf("ranger", "king", "heir_of_isildur")

                header.roles[0] shouldBe "ranger"
                header.roles[1] shouldBe "king"
            }

            test("should handle Neo's roles") {
                val header = MessageHeader()
                header.roles = arrayOf("admin", "hacker", "the_one")

                header.roles shouldBe arrayOf("admin", "hacker", "the_one")
            }

            test("should handle Trinity's roles") {
                val header = MessageHeader()
                header.roles = arrayOf("operator", "hacker", "pilot")

                header.roles.size shouldBe 3
            }

            test("should handle large number of roles") {
                val header = MessageHeader()
                val manyRoles = (1..100).map { "role_$it" }.toTypedArray()
                header.roles = manyRoles

                header.roles.size shouldBe 100
            }

            test("should handle roles with spaces") {
                val header = MessageHeader()
                header.roles = arrayOf("system admin", "power user")

                header.roles[0] shouldBe "system admin"
            }

            test("should handle roles with special characters") {
                val header = MessageHeader()
                header.roles = arrayOf("role-1", "role_2", "role.3")

                header.roles.size shouldBe 3
            }

            test("should handle unicode roles") {
                val header = MessageHeader()
                header.roles = arrayOf("管理者", "ユーザー")

                header.roles[0] shouldBe "管理者"
            }

            test("should handle emoji roles") {
                val header = MessageHeader()
                header.roles = arrayOf("🎮 gamer", "⚔️ warrior")

                header.roles[0] shouldBe "🎮 gamer"
            }

            test("should handle empty string roles") {
                val header = MessageHeader()
                header.roles = arrayOf("", "admin", "")

                header.roles.size shouldBe 3
                header.roles[0] shouldBe ""
            }

            test("should handle very long role names") {
                val header = MessageHeader()
                val longRole = "role-" + "x".repeat(1000)
                header.roles = arrayOf(longRole)

                header.roles[0].length shouldBe longRole.length
            }
        }

        context("JSON Serialization") {
            test("should serialize to JSON") {
                val header = MessageHeader()
                header.externalId = "ext-123"
                header.roles = arrayOf("admin", "user")

                val json = mapper.writeValueAsString(header)

                json.shouldContain("\"externalId\":\"ext-123\"")
                json.shouldContain("admin")
                json.shouldContain("user")
            }

            test("should serialize empty MessageHeader") {
                val header = MessageHeader()

                val json = mapper.writeValueAsString(header)

                json.shouldContain("\"externalId\":\"\"")
                json.shouldContain("\"roles\"")
            }

            test("should serialize with only externalId") {
                val header = MessageHeader()
                header.externalId = "user-456"

                val json = mapper.writeValueAsString(header)

                json.shouldContain("user-456")
            }

            test("should serialize with only roles") {
                val header = MessageHeader()
                header.roles = arrayOf("admin")

                val json = mapper.writeValueAsString(header)

                json.shouldContain("admin")
            }

            test("should serialize Frodo's header") {
                val header = MessageHeader()
                header.externalId = "frodo-baggins"
                header.roles = arrayOf("ring_bearer", "hobbit")

                val json = mapper.writeValueAsString(header)

                json.shouldContain("frodo-baggins")
                json.shouldContain("ring_bearer")
            }

            test("should serialize Gandalf's header") {
                val header = MessageHeader()
                header.externalId = "gandalf"
                header.roles = arrayOf("wizard", "white_council")

                val json = mapper.writeValueAsString(header)

                json.shouldContain("gandalf")
                json.shouldContain("wizard")
            }

            test("should serialize unicode content") {
                val header = MessageHeader()
                header.externalId = "ユーザー"
                header.roles = arrayOf("管理者")

                val json = mapper.writeValueAsString(header)

                json.shouldContain("ユーザー")
                json.shouldContain("管理者")
            }

            test("should serialize emoji content") {
                val header = MessageHeader()
                header.externalId = "user-🎮"
                header.roles = arrayOf("🎮 gamer")

                val json = mapper.writeValueAsString(header)

                json.shouldContain("🎮")
            }

            test("should serialize large roles array") {
                val header = MessageHeader()
                header.roles = (1..50).map { "role_$it" }.toTypedArray()

                val json = mapper.writeValueAsString(header)

                json shouldNotBe null
            }
        }

        context("JSON Deserialization") {
            test("should deserialize from JSON") {
                val json = """{"externalId":"ext-123","roles":["admin","user"]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe "ext-123"
                header.roles.size shouldBe 2
                header.roles[0] shouldBe "admin"
                header.roles[1] shouldBe "user"
            }

            test("should deserialize empty values") {
                val json = """{"externalId":"","roles":[]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe ""
                header.roles.size shouldBe 0
            }

            test("should deserialize with missing fields using defaults") {
                val json = """{}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe ""
                header.roles.size shouldBe 0
            }

            test("should deserialize Aragorn's header") {
                val json = """{"externalId":"aragorn","roles":["ranger","king"]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe "aragorn"
                header.roles shouldBe arrayOf("ranger", "king")
            }

            test("should deserialize Neo's header") {
                val json = """{"externalId":"neo-001","roles":["admin","the_one"]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe "neo-001"
                header.roles[1] shouldBe "the_one"
            }

            test("should deserialize unicode") {
                val json = """{"externalId":"ユーザー","roles":["管理者"]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe "ユーザー"
                header.roles[0] shouldBe "管理者"
            }

            test("should deserialize emoji") {
                val json = """{"externalId":"user-🎮","roles":["🎮 gamer"]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.externalId shouldBe "user-🎮"
                header.roles[0] shouldBe "🎮 gamer"
            }

            test("should deserialize large roles array") {
                val rolesJson = (1..100).map { "\"role_$it\"" }.joinToString(",")
                val json = """{"externalId":"user","roles":[$rolesJson]}"""

                val header = mapper.readValue<MessageHeader>(json)

                header.roles.size shouldBe 100
            }
        }

        context("Round-trip Serialization") {
            test("should maintain data through serialize-deserialize cycle") {
                val original = MessageHeader()
                original.externalId = "ext-123"
                original.roles = arrayOf("admin", "user")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<MessageHeader>(json)

                deserialized.externalId shouldBe original.externalId
                deserialized.roles shouldBe original.roles
            }

            test("should handle empty header in round-trip") {
                val original = MessageHeader()

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<MessageHeader>(json)

                deserialized.externalId shouldBe ""
                deserialized.roles.size shouldBe 0
            }

            test("should handle unicode in round-trip") {
                val original = MessageHeader()
                original.externalId = "孫悟空"
                original.roles = arrayOf("武士")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<MessageHeader>(json)

                deserialized.externalId shouldBe "孫悟空"
                deserialized.roles[0] shouldBe "武士"
            }

            test("should handle emoji in round-trip") {
                val original = MessageHeader()
                original.externalId = "🧙‍♂️"
                original.roles = arrayOf("⚔️")

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<MessageHeader>(json)

                deserialized.externalId shouldBe "🧙‍♂️"
                deserialized.roles[0] shouldBe "⚔️"
            }
        }

        context("Edge Cases") {
            test("should handle null externalId assignment") {
                val header = MessageHeader()
                // externalId is non-null String, can't assign null directly
                // But we can test the default value
                header.externalId = ""

                header.externalId shouldBe ""
            }

            test("should handle roles array with duplicate values") {
                val header = MessageHeader()
                header.roles = arrayOf("admin", "admin", "user")

                header.roles.size shouldBe 3
                header.roles[0] shouldBe "admin"
                header.roles[1] shouldBe "admin"
            }

            test("should handle very long externalId") {
                val header = MessageHeader()
                val longId = "id-" + "x".repeat(10000)
                header.externalId = longId

                header.externalId.length shouldBe longId.length
            }

            test("should handle whitespace-only externalId") {
                val header = MessageHeader()
                header.externalId = "   "

                header.externalId shouldBe "   "
            }

            test("should handle whitespace-only roles") {
                val header = MessageHeader()
                header.roles = arrayOf("   ", "\t", "\n")

                header.roles.size shouldBe 3
            }

            test("should handle externalId with newlines") {
                val header = MessageHeader()
                header.externalId = "line1\nline2"

                header.externalId shouldContain "\n"
            }

            test("should handle role with newlines") {
                val header = MessageHeader()
                header.roles = arrayOf("role\nwith\nnewlines")

                header.roles[0] shouldContain "\n"
            }
        }

        context("Component Annotation Behavior") {
            test("should be Spring component") {
                val header = MessageHeader()

                // Component annotation means it can be injected by Spring
                // We just verify the object can be instantiated
                header shouldNotBe null
            }

            test("should create multiple instances") {
                val header1 = MessageHeader()
                val header2 = MessageHeader()

                header1 shouldNotBe header2 // Different instances
            }

            test("should allow independent modifications") {
                val header1 = MessageHeader()
                val header2 = MessageHeader()

                header1.externalId = "user1"
                header2.externalId = "user2"

                header1.externalId shouldBe "user1"
                header2.externalId shouldBe "user2"
            }
        }

        context("Real-world Usage Scenarios") {
            test("should model Frodo's message header") {
                val header = MessageHeader()
                header.externalId = "frodo-baggins-shire"
                header.roles = arrayOf("ring_bearer", "hobbit", "fellowship_member")

                header.externalId shouldBe "frodo-baggins-shire"
                header.roles.size shouldBe 3
            }

            test("should model Gandalf's message header") {
                val header = MessageHeader()
                header.externalId = "gandalf-the-white"
                header.roles = arrayOf("wizard", "istari", "white_council", "fellowship_leader")

                header.roles.size shouldBe 4
            }

            test("should model Aragorn's message header") {
                val header = MessageHeader()
                header.externalId = "aragorn-elessar"
                header.roles = arrayOf("ranger", "king", "dunedain", "fellowship_member")

                header.externalId shouldContain "elessar"
            }

            test("should model Neo's admin header") {
                val header = MessageHeader()
                header.externalId = "neo-the-one"
                header.roles = arrayOf("admin", "superuser", "the_one", "anomaly")

                header.roles[2] shouldBe "the_one"
            }

            test("should model Trinity's operator header") {
                val header = MessageHeader()
                header.externalId = "trinity-operator"
                header.roles = arrayOf("operator", "hacker", "pilot", "fighter")

                header.roles.size shouldBe 4
            }

            test("should model system service header") {
                val header = MessageHeader()
                header.externalId = "system-service-001"
                header.roles = arrayOf("system", "automated", "trusted")

                header.externalId shouldContain "system"
            }

            test("should model guest user header") {
                val header = MessageHeader()
                header.externalId = "guest"
                header.roles = arrayOf("guest", "limited")

                header.roles[0] shouldBe "guest"
            }
        }

        context("Array Equality") {
            test("should compare roles arrays correctly") {
                val header1 = MessageHeader()
                val header2 = MessageHeader()

                header1.roles = arrayOf("admin", "user")
                header2.roles = arrayOf("admin", "user")

                // Arrays use reference equality by default
                header1.roles shouldBe arrayOf("admin", "user")
                header2.roles shouldBe arrayOf("admin", "user")
            }

            test("should detect different roles") {
                val header1 = MessageHeader()
                val header2 = MessageHeader()

                header1.roles = arrayOf("admin")
                header2.roles = arrayOf("user")

                header1.roles[0] shouldNotBe header2.roles[0]
            }
        }
    })
