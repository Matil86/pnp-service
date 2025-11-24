package de.hipp.pnp.api.rabbitMq

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldContain as shouldContainString

class DefaultMessageTest :
    FunSpec({
        val mapper = jacksonObjectMapper()

        data class TestPayload(
            val name: String,
            val value: Int,
        )

        context("Primary Constructor") {
            test("should create message with all parameters") {
                val header = MessageHeader()
                val payload = TestPayload("test", 42)
                val message =
                    DefaultMessage(
                        action = "CREATE",
                        payload = payload,
                        detailMessage = "Creating test",
                        uuid = "uuid-123",
                        header = header,
                    )

                message.action shouldBe "CREATE"
                message.payload shouldBe payload
                message.detailMessage shouldBe "Creating test"
                message.uuid shouldBe "uuid-123"
                message.header shouldBe header
            }

            test("should create message for Frodo's quest") {
                val payload = TestPayload("Ring Quest", 1)
                val message =
                    DefaultMessage(
                        action = "START_QUEST",
                        payload = payload,
                        detailMessage = "Frodo begins journey to Mordor",
                        uuid = "frodo-123",
                    )

                message.action shouldBe "START_QUEST"
                message.detailMessage shouldContainString "Frodo"
            }

            test("should create message for Gandalf's spell") {
                val payload = TestPayload("Fireball", 99)
                val message =
                    DefaultMessage(
                        action = "CAST_SPELL",
                        payload = payload,
                        detailMessage = "You shall not pass!",
                        uuid = "gandalf-456",
                    )

                message.detailMessage shouldContainString "You shall not pass"
            }
        }

        context("No-arg Constructor") {
            test("should create message with no-arg constructor") {
                val message = DefaultMessage<String>()

                message.action shouldBe ""
                message.payload shouldBe null
                message.detailMessage shouldBe ""
                message.uuid shouldBe ""
                message.header shouldNotBe null
            }

            test("should allow setting properties after no-arg construction") {
                val message = DefaultMessage<TestPayload>()
                message.action = "UPDATE"
                message.payload = TestPayload("test", 10)
                message.detailMessage = "Updated"
                message.uuid = "uuid-789"

                message.action shouldBe "UPDATE"
                message.payload?.name shouldBe "test"
            }
        }

        context("Generic Type Parameter") {
            test("should work with String payload") {
                val message =
                    DefaultMessage(
                        action = "MESSAGE",
                        payload = "Hello World",
                    )

                message.payload shouldBe "Hello World"
            }

            test("should work with Int payload") {
                val message =
                    DefaultMessage(
                        action = "COUNT",
                        payload = 42,
                    )

                message.payload shouldBe 42
            }

            test("should work with custom data class payload") {
                val payload = TestPayload("Neo", 99)
                val message =
                    DefaultMessage(
                        action = "HACK",
                        payload = payload,
                    )

                message.payload.name shouldBe "Neo"
                message.payload.value shouldBe 99
            }

            test("should work with List payload") {
                val payload = listOf("Frodo", "Sam", "Gandalf")
                val message =
                    DefaultMessage(
                        action = "FELLOWSHIP",
                        payload = payload,
                    )

                message.payload.size shouldBe 3
                message.payload.shouldContain("Frodo")
            }

            test("should work with Map payload") {
                val payload = mapOf("power" to 100, "wisdom" to 99)
                val message =
                    DefaultMessage(
                        action = "STATS",
                        payload = payload,
                    )

                message.payload["power"] shouldBe 100
            }

            test("should work with null payload") {
                val message =
                    DefaultMessage<String?>(
                        action = "EMPTY",
                        payload = null,
                    )

                message.payload shouldBe null
            }
        }

        context("Action Property") {
            test("should handle empty action") {
                val message = DefaultMessage(action = "", payload = "test")

                message.action shouldBe ""
            }

            test("should handle uppercase action") {
                val message = DefaultMessage(action = "CREATE", payload = "test")

                message.action shouldBe "CREATE"
            }

            test("should handle lowercase action") {
                val message = DefaultMessage(action = "create", payload = "test")

                message.action shouldBe "create"
            }

            test("should handle action with spaces") {
                val message = DefaultMessage(action = "CREATE USER", payload = "test")

                message.action shouldBe "CREATE USER"
            }

            test("should handle unicode action") {
                val message = DefaultMessage(action = "作成", payload = "test")

                message.action shouldBe "作成"
            }

            test("should handle emoji action") {
                val message = DefaultMessage(action = "⚡ POWER", payload = "test")

                message.action shouldBe "⚡ POWER"
            }

            test("should handle very long action") {
                val longAction = "ACTION_" + "X".repeat(1000)
                val message = DefaultMessage(action = longAction, payload = "test")

                message.action.length shouldBe longAction.length
            }
        }

        context("DetailMessage Property") {
            test("should handle empty detail message") {
                val message = DefaultMessage(action = "TEST", payload = "test", detailMessage = "")

                message.detailMessage shouldBe ""
            }

            test("should handle long detail message") {
                val longDetail = "Detail " + "X".repeat(5000)
                val message = DefaultMessage(action = "TEST", payload = "test", detailMessage = longDetail)

                message.detailMessage.length shouldBe longDetail.length
            }

            test("should handle unicode detail message") {
                val message =
                    DefaultMessage(
                        action = "TEST",
                        payload = "test",
                        detailMessage = "これは詳細メッセージです",
                    )

                message.detailMessage shouldBe "これは詳細メッセージです"
            }

            test("should handle emoji detail message") {
                val message =
                    DefaultMessage(
                        action = "TEST",
                        payload = "test",
                        detailMessage = "Quest completed! 🎉",
                    )

                message.detailMessage shouldContainString "🎉"
            }

            test("should handle multiline detail message") {
                val message =
                    DefaultMessage(
                        action = "TEST",
                        payload = "test",
                        detailMessage = "Line 1\nLine 2\nLine 3",
                    )

                message.detailMessage shouldContainString "\n"
            }

            test("should handle Aragorn's message") {
                val message =
                    DefaultMessage(
                        action = "CLAIM_THRONE",
                        payload = "Gondor",
                        detailMessage = "The return of the king",
                    )

                message.detailMessage shouldBe "The return of the king"
            }
        }

        context("UUID Property") {
            test("should handle empty UUID") {
                val message = DefaultMessage(action = "TEST", payload = "test", uuid = "")

                message.uuid shouldBe ""
            }

            test("should handle standard UUID format") {
                val uuid = "550e8400-e29b-41d4-a716-446655440000"
                val message = DefaultMessage(action = "TEST", payload = "test", uuid = uuid)

                message.uuid shouldBe uuid
            }

            test("should handle custom UUID format") {
                val message = DefaultMessage(action = "TEST", payload = "test", uuid = "custom-id-123")

                message.uuid shouldBe "custom-id-123"
            }

            test("should handle very long UUID") {
                val longUuid = "uuid-" + "x".repeat(1000)
                val message = DefaultMessage(action = "TEST", payload = "test", uuid = longUuid)

                message.uuid shouldBe longUuid
            }

            test("should handle unicode UUID") {
                val message = DefaultMessage(action = "TEST", payload = "test", uuid = "id-孫悟空")

                message.uuid shouldBe "id-孫悟空"
            }
        }

        context("MessageHeader Property") {
            test("should have default MessageHeader") {
                val message = DefaultMessage(action = "TEST", payload = "test")

                message.header shouldNotBe null
                message.header.externalId shouldBe ""
                message.header.roles shouldBe emptyArray()
            }

            test("should accept custom MessageHeader") {
                val header = MessageHeader()
                header.externalId = "ext-123"
                header.roles = arrayOf("admin", "user")

                val message =
                    DefaultMessage(
                        action = "TEST",
                        payload = "test",
                        header = header,
                    )

                message.header.externalId shouldBe "ext-123"
                message.header.roles.size shouldBe 2
            }

            test("should handle Neo's admin header") {
                val header = MessageHeader()
                header.externalId = "neo-001"
                header.roles = arrayOf("admin", "the_one")

                val message =
                    DefaultMessage(
                        action = "REBOOT_MATRIX",
                        payload = "System restart",
                        header = header,
                    )

                message.header.roles.shouldContain("the_one")
            }
        }

        context("toString JSON Serialization") {
            test("should serialize to JSON string") {
                val message =
                    DefaultMessage(
                        action = "CREATE",
                        payload = TestPayload("test", 42),
                        detailMessage = "Creating test",
                        uuid = "uuid-123",
                    )

                val json = message.toString()

                json.shouldContainString("\"action\":\"CREATE\"")
                json.shouldContainString("\"detailMessage\":\"Creating test\"")
                json.shouldContainString("\"uuid\":\"uuid-123\"")
            }

            test("should serialize payload to JSON") {
                val message =
                    DefaultMessage(
                        action = "TEST",
                        payload = TestPayload("Frodo", 3),
                    )

                val json = message.toString()

                json.shouldContainString("payload")
                json.shouldContainString("Frodo")
            }

            test("should serialize header to JSON") {
                val header = MessageHeader()
                header.externalId = "ext-123"

                val message =
                    DefaultMessage(
                        action = "TEST",
                        payload = "test",
                        header = header,
                    )

                val json = message.toString()

                json.shouldContainString("header")
                json.shouldContainString("ext-123")
            }

            test("should exclude toString from JSON due to JsonIgnore") {
                val message = DefaultMessage(action = "TEST", payload = "test")

                val json = message.toString()

                json.shouldNotContain("toString")
            }

            test("should handle null payload in toString") {
                val message =
                    DefaultMessage<String?>(
                        action = "TEST",
                        payload = null,
                    )

                val json = message.toString()

                json shouldNotBe null
            }

            test("should serialize Gandalf's spell message") {
                val message =
                    DefaultMessage(
                        action = "CAST_SPELL",
                        payload = TestPayload("Fireball", 99),
                        detailMessage = "Explosive magic",
                        uuid = "spell-123",
                    )

                val json = message.toString()

                json.shouldContainString("CAST_SPELL")
                json.shouldContainString("Fireball")
            }

            test("should serialize unicode content") {
                val message =
                    DefaultMessage(
                        action = "メッセージ",
                        payload = "孫悟空",
                    )

                val json = message.toString()

                json.shouldContainString("メッセージ")
                json.shouldContainString("孫悟空")
            }

            test("should serialize emoji content") {
                val message =
                    DefaultMessage(
                        action = "🎮 GAME",
                        payload = "⚔️ Battle",
                    )

                val json = message.toString()

                json.shouldContainString("🎮")
                json.shouldContainString("⚔️")
            }

            test("should serialize Trinity's operator message") {
                val message =
                    DefaultMessage(
                        action = "HACK_SYSTEM",
                        payload = TestPayload("Security Override", 95),
                        detailMessage = "Trinity accessing mainframe",
                    )

                val json = message.toString()

                json.shouldContainString("HACK_SYSTEM")
                json.shouldContainString("Trinity")
            }
        }

        context("JSON Deserialization") {
            test("should deserialize from JSON") {
                val json =
                    """
                    {
                        "action": "CREATE",
                        "payload": {"name": "test", "value": 42},
                        "detailMessage": "Creating test",
                        "uuid": "uuid-123",
                        "header": {"externalId": "", "roles": []}
                    }
                    """.trimIndent()

                val message = mapper.readValue<DefaultMessage<TestPayload>>(json)

                message.action shouldBe "CREATE"
                message.payload.name shouldBe "test"
                message.payload.value shouldBe 42
                message.detailMessage shouldBe "Creating test"
                message.uuid shouldBe "uuid-123"
            }

            test("should deserialize with string payload") {
                val json =
                    """
                    {
                        "action": "MESSAGE",
                        "payload": "Hello World",
                        "detailMessage": "",
                        "uuid": "",
                        "header": {"externalId": "", "roles": []}
                    }
                    """.trimIndent()

                val message = mapper.readValue<DefaultMessage<String>>(json)

                message.payload shouldBe "Hello World"
            }

            test("should deserialize Aragorn's message") {
                val json =
                    """
                    {
                        "action": "CLAIM_THRONE",
                        "payload": "Gondor",
                        "detailMessage": "The king has returned",
                        "uuid": "aragorn-123",
                        "header": {"externalId": "elessar", "roles": ["king"]}
                    }
                    """.trimIndent()

                val message = mapper.readValue<DefaultMessage<String>>(json)

                message.action shouldBe "CLAIM_THRONE"
                message.payload shouldBe "Gondor"
                message.header.externalId shouldBe "elessar"
            }
        }

        context("Round-trip Serialization") {
            test("should maintain data through serialize-deserialize cycle") {
                val original =
                    DefaultMessage(
                        action = "CREATE",
                        payload = TestPayload("test", 42),
                        detailMessage = "Creating test",
                        uuid = "uuid-123",
                    )

                val json = original.toString()
                val deserialized = mapper.readValue<DefaultMessage<TestPayload>>(json)

                deserialized.action shouldBe original.action
                deserialized.payload.name shouldBe original.payload.name
                deserialized.payload.value shouldBe original.payload.value
                deserialized.detailMessage shouldBe original.detailMessage
                deserialized.uuid shouldBe original.uuid
            }

            test("should handle unicode in round-trip") {
                val original =
                    DefaultMessage(
                        action = "テスト",
                        payload = "日本語",
                    )

                val json = original.toString()
                val deserialized = mapper.readValue<DefaultMessage<String>>(json)

                deserialized.action shouldBe "テスト"
                deserialized.payload shouldBe "日本語"
            }

            test("should handle emoji in round-trip") {
                val original =
                    DefaultMessage(
                        action = "🎮",
                        payload = "⚔️",
                    )

                val json = original.toString()
                val deserialized = mapper.readValue<DefaultMessage<String>>(json)

                deserialized.action shouldBe "🎮"
                deserialized.payload shouldBe "⚔️"
            }
        }

        context("Edge Cases") {
            test("should handle very large payload") {
                val largePayload = "X".repeat(10000)
                val message = DefaultMessage(action = "TEST", payload = largePayload)

                message.payload.length shouldBe 10000
            }

            test("should handle complex nested payload") {
                data class NestedPayload(
                    val level1: Map<String, List<TestPayload>>,
                )

                val payload =
                    NestedPayload(
                        level1 =
                            mapOf(
                                "group1" to listOf(TestPayload("a", 1), TestPayload("b", 2)),
                            ),
                    )

                val message = DefaultMessage(action = "COMPLEX", payload = payload)

                message.payload.level1["group1"]?.size shouldBe 2
            }

            test("should handle Neo's Matrix reboot sequence") {
                val payload =
                    mapOf(
                        "user" to "Neo",
                        "action" to "reboot",
                        "target" to "Matrix",
                        "power_level" to 100,
                    )

                val message =
                    DefaultMessage(
                        action = "SYSTEM_REBOOT",
                        payload = payload,
                        detailMessage = "The One is rebooting the system",
                        uuid = "neo-reboot-001",
                    )

                message.payload["user"] shouldBe "Neo"
                message.payload["power_level"] shouldBe 100
            }
        }

        context("Mutability") {
            test("should allow modification of action") {
                val message = DefaultMessage(action = "CREATE", payload = "test")
                message.action = "UPDATE"

                message.action shouldBe "UPDATE"
            }

            test("should allow modification of payload") {
                val message = DefaultMessage(action = "TEST", payload = TestPayload("old", 1))
                message.payload = TestPayload("new", 2)

                message.payload.name shouldBe "new"
            }

            test("should allow modification of detailMessage") {
                val message = DefaultMessage(action = "TEST", payload = "test", detailMessage = "old")
                message.detailMessage = "new"

                message.detailMessage shouldBe "new"
            }

            test("should allow modification of uuid") {
                val message = DefaultMessage(action = "TEST", payload = "test", uuid = "old")
                message.uuid = "new"

                message.uuid shouldBe "new"
            }

            test("should allow modification of header") {
                val message = DefaultMessage(action = "TEST", payload = "test")
                val newHeader = MessageHeader()
                newHeader.externalId = "new-id"

                message.header = newHeader

                message.header.externalId shouldBe "new-id"
            }
        }
    })
