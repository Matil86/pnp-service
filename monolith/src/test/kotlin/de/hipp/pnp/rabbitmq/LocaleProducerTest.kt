package de.hipp.pnp.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.locale.BookLocale
import de.hipp.pnp.api.locale.LabelDesc
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.amqp.rabbit.core.RabbitTemplate

/**
 * Tests for LocaleProducer.
 *
 * Verifies locale and language key retrieval through RabbitMQ.
 */
class LocaleProducerTest :
    StringSpec({

        "getAllLanguageKeys - Goku retrieves all language keys" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "character.name" to BookLocale(features = mapOf("name" to LabelDesc(label = "Character Name"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getAllLanguageKeys()

            result shouldNotBe null
            result?.size shouldBe 1
        }

        "getAllLanguageKeys - Spider-Man retrieves empty map" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val emptyMap = mutableMapOf<String, BookLocale>()
            // Mock returns a DefaultMessage JSON string with empty map as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(emptyMap)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getAllLanguageKeys()

            result shouldNotBe null
            result?.isEmpty() shouldBe true
        }

        "getLanguageKeysByGameTypeAndLanguage - Tony Stark retrieves English locale" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "skill.tech" to BookLocale(features = mapOf("tech" to LabelDesc(label = "Technology"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

            result shouldNotBe null
            result!! shouldContainKey "skill.tech"
        }

        "getLanguageKeysByGameTypeAndLanguage - Batman retrieves German locale" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "skill.stealth" to BookLocale(features = mapOf("stealth" to LabelDesc(label = "Heimlichkeit"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "de_DE")

            result shouldNotBe null
            result!! shouldContainKey "skill.stealth"
        }

        "getLanguageKeysByGameTypeAndLanguage - Wonder Woman with gameType 1" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "power.strength" to BookLocale(features = mapOf("strength" to LabelDesc(label = "Strength"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(1, "en_US")

            result shouldNotBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - Naruto (ナルト) retrieves Japanese locale" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "jutsu.name" to BookLocale(features = mapOf("name" to LabelDesc(label = "術名"))),
                    "ninja.way" to BookLocale(features = mapOf("way" to LabelDesc(label = "忍道"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "ja_JP")

            result shouldNotBe null
            result?.size shouldBe 2
        }

        "getLanguageKeysByGameTypeAndLanguage - Vegeta with null locale returns data" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "power.level" to BookLocale(features = mapOf("level" to LabelDesc(label = "Power Level"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, null)

            result shouldNotBe null
        }

        "getAllLanguageKeys - Deadpool verifies routing key usage" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val emptyMap = mutableMapOf<String, BookLocale>()
            // Mock returns a DefaultMessage JSON string with empty map as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(emptyMap)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            producer.getAllLanguageKeys()

            verify { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "getLanguageKeysByGameTypeAndLanguage - Hulk with emoji in locale data 🎲" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "dice.roll" to BookLocale(features = mapOf("roll" to LabelDesc(label = "🎲 Roll Dice"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

            result shouldNotBe null
            result!! shouldContainKey "dice.roll"
        }

        "getLanguageKeysByGameTypeAndLanguage - Pikachu (ピカチュウ) with katakana" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "pokemon.attack" to BookLocale(features = mapOf("attack" to LabelDesc(label = "でんきショック"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "ja_JP")

            result shouldNotBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - Gandalf with multiple locales" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "spell.fireball" to BookLocale(features = mapOf("name" to LabelDesc(label = "Fireball"))),
                    "spell.lightning" to BookLocale(features = mapOf("name" to LabelDesc(label = "Lightning Bolt"))),
                    "spell.shield" to BookLocale(features = mapOf("name" to LabelDesc(label = "Shield"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

            result?.size shouldBe 3
        }

        "getAllLanguageKeys - Frodo returns null when no data" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            // Mock returns a DefaultMessage JSON string with null payload
            val responseJson = """{"payload":null,"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getAllLanguageKeys()

            result shouldBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - Neo with French locale" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "matrix.reality" to BookLocale(features = mapOf("reality" to LabelDesc(label = "Réalité"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "fr_FR")

            result shouldNotBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - Loki with special characters" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "trickery" to BookLocale(features = mapOf("trick" to LabelDesc(label = "Trickery & Deception"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

            result shouldNotBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - Thor with Spanish locale" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "thunder.strike" to BookLocale(features = mapOf("strike" to LabelDesc(label = "Rayo"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "es_ES")

            result shouldNotBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - Captain America with gameType 5" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf(
                    "shield.throw" to BookLocale(features = mapOf("throw" to LabelDesc(label = "Shield Throw"))),
                )

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(5, "en_US")

            result shouldNotBe null
        }

        "getAllLanguageKeys - Black Widow multiple calls" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData = mutableMapOf<String, BookLocale>()
            // Mock returns a DefaultMessage JSON string with empty map as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            producer.getAllLanguageKeys()
            producer.getAllLanguageKeys()

            verify(exactly = 2) { rabbitTemplate.convertSendAndReceive(any<String>()) }
        }

        "getLanguageKeysByGameTypeAndLanguage - Thanos with large locale data" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData =
                mutableMapOf<String, BookLocale>().apply {
                    repeat(100) { i ->
                        put("key.$i", BookLocale(features = mapOf("label$i" to LabelDesc(label = "Label $i"))))
                    }
                }

            // Mock returns a DefaultMessage JSON string with the locale data as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

            result?.size shouldBe 100
        }

        "getLanguageKeysByGameTypeAndLanguage - returns null when RabbitMQ returns null" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            // Mock returns a DefaultMessage JSON string with null payload
            val responseJson = """{"payload":null,"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

            result shouldBe null
        }

        "getLanguageKeysByGameTypeAndLanguage - handles empty locale string" {
            val rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
            val mapper = ObjectMapper()
            val producer = LocaleProducer(rabbitTemplate, mapper)

            val localeData = mutableMapOf<String, BookLocale>()
            // Mock returns a DefaultMessage JSON string with empty map as payload
            val responseJson = """{"payload":${mapper.writeValueAsString(localeData)},"uuid":"test-uuid"}"""
            every { rabbitTemplate.convertSendAndReceive(any<String>()) } returns responseJson

            val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "")

            result shouldNotBe null
        }
    })
