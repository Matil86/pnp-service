package de.hipp.pnp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.locale.BookLocale
import de.hipp.pnp.rabbitmq.LocaleProducer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * Tests for LocaleRestController with special string inputs including:
 * - Empty strings
 * - Null values
 * - Whitespace
 * - Hiragana (ひらがな)
 * - Katakana (カタカナ)
 * - Emoji (🎲)
 */
class LocaleRestControllerTest : StringSpec({

    val mapper = ObjectMapper()

    fun createMockController(localeData: MutableMap<String, BookLocale> = mutableMapOf()): LocaleRestController {
        val localeProducer = mockk<LocaleProducer>()
        every { localeProducer.getLanguageKeysByGameTypeAndLanguage(any(), any()) } returns localeData
        return LocaleRestController(localeProducer, mapper)
    }

    "GET /locale - default gameType 0 returns English locale" {
        val localeData = mutableMapOf(
            "character.name" to BookLocale().apply { value = "Character Name" },
            "character.class" to BookLocale().apply { value = "Character Class" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result.shouldNotBeEmpty()
        result shouldContain "character.name"
        result shouldContain "Character Name"
    }

    "GET /locale - Goku (孫悟空) checks Japanese locale with hiragana" {
        val localeData = mutableMapOf(
            "character.name" to BookLocale().apply { value = "なまえ" },
            "character.strength" to BookLocale().apply { value = "ちから" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "なまえ"
        result shouldContain "ちから"
    }

    "GET /locale - Naruto (ナルト) checks Japanese locale with katakana" {
        val localeData = mutableMapOf(
            "game.title" to BookLocale().apply { value = "ゲーム" },
            "character.status" to BookLocale().apply { value = "ステータス" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "ゲーム"
        result shouldContain "ステータス"
    }

    "GET /locale - Emoji support 🎲⚔️🛡️" {
        val localeData = mutableMapOf(
            "dice.roll" to BookLocale().apply { value = "🎲 Roll Dice" },
            "weapon.attack" to BookLocale().apply { value = "⚔️ Attack" },
            "armor.defense" to BookLocale().apply { value = "🛡️ Defense" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "🎲"
        result shouldContain "⚔️"
        result shouldContain "🛡️"
    }

    "GET /locale - Empty locale data returns empty JSON object" {
        val controller = createMockController(mutableMapOf())

        val result = controller.getLocale(0)

        result shouldBe "{}"
    }

    "GET /locale - Key with empty string value" {
        val localeData = mutableMapOf(
            "empty.key" to BookLocale().apply { value = "" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "empty.key"
    }

    "GET /locale - Key with whitespace value" {
        val localeData = mutableMapOf(
            "whitespace.key" to BookLocale().apply { value = "   " }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "whitespace.key"
        result shouldContain "   "
    }

    "GET /locale - Mixed languages (English, Japanese, emoji)" {
        val localeData = mutableMapOf(
            "title.en" to BookLocale().apply { value = "GeneFunk 2090" },
            "title.jp" to BookLocale().apply { value = "ジーンファンク 2090" },
            "icon" to BookLocale().apply { value = "🎲🧬" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "GeneFunk 2090"
        result shouldContain "ジーンファンク 2090"
        result shouldContain "🎲🧬"
    }

    "GET /locale - Tony Stark checks technical terms" {
        val localeData = mutableMapOf(
            "tech.ai" to BookLocale().apply { value = "Artificial Intelligence" },
            "tech.nano" to BookLocale().apply { value = "Nanotechnology" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "Artificial Intelligence"
        result shouldContain "Nanotechnology"
    }

    "GET /locale - Spider-Man (スパイダーマン) checks ability names" {
        val localeData = mutableMapOf(
            "ability.web" to BookLocale().apply { value = "ウェブ・シューター" },
            "ability.sense" to BookLocale().apply { value = "スパイダーセンス" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "ウェブ・シューター"
        result shouldContain "スパイダーセンス"
    }

    "GET /locale - Special characters and symbols" {
        val localeData = mutableMapOf(
            "math" to BookLocale().apply { value = "±10%" },
            "currency" to BookLocale().apply { value = "¥1000" },
            "arrows" to BookLocale().apply { value = "→←↑↓" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "±10%"
        result shouldContain "¥1000"
        result shouldContain "→←↑↓"
    }

    "GET /locale - Verify en_US locale is requested by default" {
        val localeProducer = mockk<LocaleProducer>()
        every { localeProducer.getLanguageKeysByGameTypeAndLanguage(0, "en_US") } returns mutableMapOf()

        val controller = LocaleRestController(localeProducer, mapper)
        controller.getLocale(0)

        verify { localeProducer.getLanguageKeysByGameTypeAndLanguage(0, "en_US") }
    }

    "GET /locale - GameType 1 requests different game system" {
        val localeProducer = mockk<LocaleProducer>()
        every { localeProducer.getLanguageKeysByGameTypeAndLanguage(1, "en_US") } returns mutableMapOf()

        val controller = LocaleRestController(localeProducer, mapper)
        controller.getLocale(1)

        verify { localeProducer.getLanguageKeysByGameTypeAndLanguage(1, "en_US") }
    }

    "GET /locale - Batman checks combat terms" {
        val localeData = mutableMapOf(
            "combat.melee" to BookLocale().apply { value = "Melee Attack" },
            "combat.ranged" to BookLocale().apply { value = "Ranged Attack" },
            "combat.critical" to BookLocale().apply { value = "Critical Hit! 💥" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "Melee Attack"
        result shouldContain "Ranged Attack"
        result shouldContain "💥"
    }

    "GET /locale - Pikachu (ピカチュウ) checks Pokemon-style names" {
        val localeData = mutableMapOf(
            "pokemon.pikachu" to BookLocale().apply { value = "ピカチュウ" },
            "pokemon.attack" to BookLocale().apply { value = "でんきショック ⚡" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "ピカチュウ"
        result shouldContain "でんきショック"
        result shouldContain "⚡"
    }

    "GET /locale - Newline and tab characters in value" {
        val localeData = mutableMapOf(
            "multiline" to BookLocale().apply { value = "Line1\nLine2\tTabbed" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "multiline"
    }

    "GET /locale - Very long locale key name" {
        val longKey = "a".repeat(100)
        val localeData = mutableMapOf(
            longKey to BookLocale().apply { value = "Long key test" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain longKey
        result shouldContain "Long key test"
    }

    "GET /locale - Null locale data handled gracefully" {
        val localeProducer = mockk<LocaleProducer>()
        every { localeProducer.getLanguageKeysByGameTypeAndLanguage(any(), any()) } returns null

        val controller = LocaleRestController(localeProducer, mapper)
        val result = controller.getLocale(0)

        result shouldBe "null"
    }

    "GET /locale - Unicode characters beyond BMP (🧙‍♂️)" {
        val localeData = mutableMapOf(
            "wizard" to BookLocale().apply { value = "🧙‍♂️ Gandalf" },
            "elf" to BookLocale().apply { value = "🧝‍♀️ Legolas" }
        )
        val controller = createMockController(localeData)

        val result = controller.getLocale(0)

        result shouldContain "Gandalf"
        result shouldContain "Legolas"
    }
})
