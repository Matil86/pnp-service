package de.hipp.pnp.api.dto

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.Validation
import jakarta.validation.Validator

class LanguageRequestTest :
    FunSpec({
        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        context("Constructor and Properties") {
            test("should create LanguageRequest with default values") {
                val request = LanguageRequest()

                request.locale shouldBe null
                request.gameType shouldBe 0
            }

            test("should create LanguageRequest with provided values") {
                val request =
                    LanguageRequest(
                        locale = "en_US",
                        gameType = 42,
                    )

                request.locale shouldBe "en_US"
                request.gameType shouldBe 42
            }

            test("should handle Frodo's preferred locale") {
                val request = LanguageRequest(locale = "en_GB", gameType = 5)
                request.locale shouldBe "en_GB"
            }
        }

        context("Locale Pattern Validation") {
            test("should accept valid underscore format en_US") {
                val request = LanguageRequest(locale = "en_US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should accept valid hyphen format en-US") {
                val request = LanguageRequest(locale = "en-US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should accept de_DE for Gandalf's German campaign") {
                val request = LanguageRequest(locale = "de_DE", gameType = 1)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should accept fr-FR") {
                val request = LanguageRequest(locale = "fr-FR", gameType = 2)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should accept es_ES for Aragorn's Spanish locale") {
                val request = LanguageRequest(locale = "es_ES", gameType = 3)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should reject null locale") {
                val request = LanguageRequest(locale = null, gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 0 // null is allowed by validation
            }

            test("should reject empty string locale") {
                val request = LanguageRequest(locale = "", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
                violations.first().message shouldBe "Locale must be in format xx_XX or xx-XX (e.g., en_US, de_DE)"
            }

            test("should reject invalid format EN_US (uppercase language)") {
                val request = LanguageRequest(locale = "EN_US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject invalid format en_us (lowercase country)") {
                val request = LanguageRequest(locale = "en_us", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject invalid format e_US (short language)") {
                val request = LanguageRequest(locale = "e_US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject invalid format en_U (short country)") {
                val request = LanguageRequest(locale = "en_U", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject invalid format eng_USA (long codes)") {
                val request = LanguageRequest(locale = "eng_USA", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject locale with space") {
                val request = LanguageRequest(locale = "en US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject locale with unicode characters") {
                val request = LanguageRequest(locale = "zh_中国", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject locale with emoji") {
                val request = LanguageRequest(locale = "en_🇺🇸", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject locale with numbers") {
                val request = LanguageRequest(locale = "en_12", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject locale with dot separator") {
                val request = LanguageRequest(locale = "en.US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }
        }

        context("Game Type Validation") {
            test("should accept gameType 0 (minimum boundary)") {
                val request = LanguageRequest(locale = "en_US", gameType = 0)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should accept gameType 100 (maximum boundary)") {
                val request = LanguageRequest(locale = "en_US", gameType = 100)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should accept gameType 50 (middle value)") {
                val request = LanguageRequest(locale = "en_US", gameType = 50)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }

            test("should reject negative gameType") {
                val request = LanguageRequest(locale = "en_US", gameType = -1)
                val violations = validator.validate(request)

                violations.size shouldBe 1
                violations.first().message shouldBe "Game type must be non-negative"
            }

            test("should reject gameType exceeding 100") {
                val request = LanguageRequest(locale = "en_US", gameType = 101)
                val violations = validator.validate(request)

                violations.size shouldBe 1
                violations.first().message shouldBe "Game type must not exceed 100"
            }

            test("should reject very large gameType") {
                val request = LanguageRequest(locale = "en_US", gameType = 9999)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }

            test("should reject very negative gameType") {
                val request = LanguageRequest(locale = "en_US", gameType = -100)
                val violations = validator.validate(request)

                violations.size shouldBe 1
            }
        }

        context("Combined Validation") {
            test("should reject multiple violations (invalid locale and gameType)") {
                val request = LanguageRequest(locale = "invalid", gameType = -5)
                val violations = validator.validate(request)

                violations.size shouldBe 2
            }

            test("should accept all valid values for Neo's Matrix campaign") {
                val request = LanguageRequest(locale = "en-US", gameType = 99)
                val violations = validator.validate(request)

                violations.size shouldBe 0
            }
        }

        context("Data Class Behavior") {
            test("should implement equals correctly") {
                val request1 = LanguageRequest(locale = "en_US", gameType = 42)
                val request2 = LanguageRequest(locale = "en_US", gameType = 42)

                request1 shouldBe request2
            }

            test("should implement equals correctly for different values") {
                val request1 = LanguageRequest(locale = "en_US", gameType = 42)
                val request2 = LanguageRequest(locale = "de_DE", gameType = 42)

                request1 shouldNotBe request2
            }

            test("should implement hashCode correctly") {
                val request1 = LanguageRequest(locale = "en_US", gameType = 42)
                val request2 = LanguageRequest(locale = "en_US", gameType = 42)

                request1.hashCode() shouldBe request2.hashCode()
            }

            test("should support copy with modifications") {
                val original = LanguageRequest(locale = "en_US", gameType = 42)
                val copy = original.copy(locale = "de_DE")

                copy.locale shouldBe "de_DE"
                copy.gameType shouldBe 42
            }

            test("should support copy for gameType") {
                val original = LanguageRequest(locale = "en_US", gameType = 42)
                val copy = original.copy(gameType = 100)

                copy.locale shouldBe "en_US"
                copy.gameType shouldBe 100
            }

            test("should generate meaningful toString") {
                val request = LanguageRequest(locale = "en_US", gameType = 42)
                val toString = request.toString()

                toString shouldNotBe null
                toString.contains("locale") shouldBe true
                toString.contains("gameType") shouldBe true
            }
        }

        context("Mutability") {
            test("should allow locale modification") {
                val request = LanguageRequest(locale = "en_US", gameType = 0)
                request.locale = "de_DE"

                request.locale shouldBe "de_DE"
            }

            test("should allow gameType modification") {
                val request = LanguageRequest(locale = "en_US", gameType = 0)
                request.gameType = 50

                request.gameType shouldBe 50
            }

            test("should allow setting locale to null") {
                val request = LanguageRequest(locale = "en_US", gameType = 0)
                request.locale = null

                request.locale shouldBe null
            }
        }
    })
