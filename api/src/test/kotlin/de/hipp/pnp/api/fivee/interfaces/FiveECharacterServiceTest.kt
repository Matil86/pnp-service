package de.hipp.pnp.api.fivee.interfaces

import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

// Test character implementations
private class FrodoCharacter : BaseCharacter() {
    override var firstName: String? = "Frodo"
    override var lastName: String? = "Baggins"
    override var level: Int? = 3
}

private class GandalfCharacter : BaseCharacter() {
    override var firstName: String? = "Gandalf"
    override var lastName: String? = "the Grey"
    override var level: Int? = 20
}

private class NeoCharacter : BaseCharacter() {
    override var firstName: String? = "Neo"
    override var lastName: String? = "Anderson"
    override var level: Int? = 99
}

// Test service implementations
private class FrodoCharacterService : FiveECharacterService<FrodoCharacter> {
    override fun getAllCharacters(userId: String?): MutableList<FrodoCharacter?> =
        if (userId == "frodo") {
            mutableListOf(FrodoCharacter())
        } else {
            mutableListOf()
        }

    override fun generate(): FrodoCharacter = FrodoCharacter()
}

private class GandalfCharacterService : FiveECharacterService<GandalfCharacter> {
    override fun getAllCharacters(userId: String?): MutableList<GandalfCharacter?> = mutableListOf(GandalfCharacter(), null)

    override fun generate(): GandalfCharacter = GandalfCharacter()
}

private class NullableCharacterService : FiveECharacterService<BaseCharacter> {
    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?>? = null

    override fun generate(): BaseCharacter? = null
}

class FiveECharacterServiceTest :
    FunSpec({

        context("getAllCharacters Method Contract") {
            test("should get all characters for Frodo user") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("frodo")

                result shouldNotBe null
                result?.size shouldBe 1
                result?.get(0)?.firstName shouldBe "Frodo"
            }

            test("should return empty list for unknown user") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("unknown")

                result shouldNotBe null
                result?.size shouldBe 0
            }

            test("should handle null userId") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters(null)

                result shouldNotBe null
            }

            test("should get Gandalf's characters with null entries") {
                val service = GandalfCharacterService()

                val result = service.getAllCharacters("gandalf")

                result shouldNotBe null
                result?.size shouldBe 2
                result?.get(0) shouldNotBe null
                result?.get(1) shouldBe null
            }

            test("should handle empty string userId") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("")

                result shouldNotBe null
            }

            test("should handle blank string userId") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("   ")

                result shouldNotBe null
            }

            test("should handle unicode userId") {
                class UnicodeService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> =
                        if (userId == "孫悟空") {
                            mutableListOf(BaseCharacter())
                        } else {
                            mutableListOf()
                        }

                    override fun generate(): BaseCharacter = BaseCharacter()
                }

                val service = UnicodeService()
                val result = service.getAllCharacters("孫悟空")

                result.size shouldBe 1
            }

            test("should handle emoji userId") {
                class EmojiService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> =
                        if (userId == "🧙‍♂️") {
                            mutableListOf(BaseCharacter())
                        } else {
                            mutableListOf()
                        }

                    override fun generate(): BaseCharacter = BaseCharacter()
                }

                val service = EmojiService()
                val result = service.getAllCharacters("🧙‍♂️")

                result.size shouldBe 1
            }

            test("should handle very long userId") {
                val service = FrodoCharacterService()
                val longUserId = "user_" + "x".repeat(1000)

                val result = service.getAllCharacters(longUserId)

                result shouldNotBe null
            }

            test("should return null from getAllCharacters") {
                val service = NullableCharacterService()

                val result = service.getAllCharacters("test")

                result shouldBe null
            }
        }

        context("generate Method Contract") {
            test("should generate Frodo character") {
                val service = FrodoCharacterService()

                val result = service.generate()

                result shouldNotBe null
                result?.firstName shouldBe "Frodo"
                result?.lastName shouldBe "Baggins"
                result?.level shouldBe 3
            }

            test("should generate Gandalf character") {
                val service = GandalfCharacterService()

                val result = service.generate()

                result shouldNotBe null
                result?.firstName shouldBe "Gandalf"
                result?.lastName shouldBe "the Grey"
                result?.level shouldBe 20
            }

            test("should generate Neo character") {
                class NeoService : FiveECharacterService<NeoCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<NeoCharacter?> = mutableListOf()

                    override fun generate(): NeoCharacter = NeoCharacter()
                }

                val service = NeoService()
                val result = service.generate()

                result?.firstName shouldBe "Neo"
                result?.level shouldBe 99
            }

            test("should generate Trinity character") {
                class TrinityCharacter : BaseCharacter() {
                    override var firstName: String? = "Trinity"
                    override var lastName: String? = null
                    override var level: Int? = 95
                }

                class TrinityService : FiveECharacterService<TrinityCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<TrinityCharacter?> = mutableListOf()

                    override fun generate(): TrinityCharacter = TrinityCharacter()
                }

                val service = TrinityService()
                val result = service.generate()

                result?.firstName shouldBe "Trinity"
            }

            test("should return null from generate") {
                val service = NullableCharacterService()

                val result = service.generate()

                result shouldBe null
            }

            test("should generate multiple independent characters") {
                val service = FrodoCharacterService()

                val char1 = service.generate()
                val char2 = service.generate()

                char1 shouldNotBe null
                char2 shouldNotBe null
                // They should be different instances
                char1 shouldNotBe char2
            }
        }

        context("Generic Type Parameter") {
            test("should work with FrodoCharacter type") {
                val service: FiveECharacterService<FrodoCharacter> = FrodoCharacterService()

                val character = service.generate()

                character shouldNotBe null
            }

            test("should work with GandalfCharacter type") {
                val service: FiveECharacterService<GandalfCharacter> = GandalfCharacterService()

                val character = service.generate()

                character shouldNotBe null
            }

            test("should work with BaseCharacter type") {
                val service: FiveECharacterService<BaseCharacter> =
                    object : FiveECharacterService<BaseCharacter> {
                        override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> = mutableListOf()

                        override fun generate(): BaseCharacter = BaseCharacter()
                    }

                val character = service.generate()

                character shouldNotBe null
            }

            test("should support nullable generic type") {
                val service: FiveECharacterService<BaseCharacter?> =
                    object : FiveECharacterService<BaseCharacter?> {
                        override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?>? = mutableListOf()

                        override fun generate(): BaseCharacter? = null
                    }

                val character = service.generate()

                character shouldBe null
            }
        }

        context("Mutable List Operations") {
            test("should return mutable list that can be modified") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("frodo")

                result?.add(FrodoCharacter())
                result?.size shouldBe 2
            }

            test("should allow adding null to mutable list") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("frodo")

                result?.add(null)
                result?.last() shouldBe null
            }

            test("should allow clearing mutable list") {
                val service = FrodoCharacterService()

                val result = service.getAllCharacters("frodo")

                result?.clear()
                result?.size shouldBe 0
            }

            test("should allow removing from mutable list") {
                val service = GandalfCharacterService()

                val result = service.getAllCharacters("test")

                result?.removeAt(0)
                result?.size shouldBe 1
            }
        }

        context("User ID Filtering") {
            test("should filter by exact user ID match") {
                class FilteringService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> =
                        when (userId) {
                            "aragorn" -> mutableListOf(BaseCharacter(), BaseCharacter())
                            "legolas" -> mutableListOf(BaseCharacter())
                            else -> mutableListOf()
                        }

                    override fun generate(): BaseCharacter = BaseCharacter()
                }

                val service = FilteringService()

                service.getAllCharacters("aragorn")?.size shouldBe 2
                service.getAllCharacters("legolas")?.size shouldBe 1
                service.getAllCharacters("gimli")?.size shouldBe 0
            }

            test("should handle case-sensitive user IDs") {
                class CaseSensitiveService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> =
                        if (userId == "Frodo") {
                            mutableListOf(BaseCharacter())
                        } else {
                            mutableListOf()
                        }

                    override fun generate(): BaseCharacter = BaseCharacter()
                }

                val service = CaseSensitiveService()

                service.getAllCharacters("Frodo")?.size shouldBe 1
                service.getAllCharacters("frodo")?.size shouldBe 0
            }
        }

        context("Edge Cases") {
            test("should handle large character lists") {
                class LargeListService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> = MutableList(1000) { BaseCharacter() }

                    override fun generate(): BaseCharacter = BaseCharacter()
                }

                val service = LargeListService()
                val result = service.getAllCharacters("test")

                result.size shouldBe 1000
            }

            test("should handle empty character list") {
                class EmptyListService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> = mutableListOf()

                    override fun generate(): BaseCharacter = BaseCharacter()
                }

                val service = EmptyListService()
                val result = service.getAllCharacters("test")

                result.size shouldBe 0
            }

            test("should handle list with all null entries") {
                class AllNullService : FiveECharacterService<BaseCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> = MutableList(5) { null }

                    override fun generate(): BaseCharacter? = null
                }

                val service = AllNullService()
                val result = service.getAllCharacters("test")

                result.size shouldBe 5
                result.all { it == null } shouldBe true
            }
        }

        context("Interface Contract Verification") {
            test("should implement both methods") {
                val service = FrodoCharacterService()

                // Both methods should be callable
                service.generate() shouldNotBe null
                service.getAllCharacters("test") shouldNotBe null
            }

            test("should allow multiple implementations") {
                val frodoService = FrodoCharacterService()
                val gandalfService = GandalfCharacterService()

                frodoService.generate()?.firstName shouldBe "Frodo"
                gandalfService.generate()?.firstName shouldBe "Gandalf"
            }

            test("should support polymorphism") {
                val service: FiveECharacterService<BaseCharacter> =
                    object : FiveECharacterService<BaseCharacter> {
                        override fun getAllCharacters(userId: String?): MutableList<BaseCharacter?> =
                            mutableListOf(FrodoCharacter(), GandalfCharacter())

                        override fun generate(): BaseCharacter = NeoCharacter()
                    }

                val characters = service.getAllCharacters("test")
                val generated = service.generate()

                characters?.size shouldBe 2
                generated shouldNotBe null
            }
        }

        context("Subclass Hierarchy") {
            test("should work with extended character classes") {
                class ExtendedCharacter : BaseCharacter() {
                    override var firstName: String? = null
                    override var lastName: String? = null
                    override var level: Int? = null
                    var customProperty: String = "custom"
                }

                class ExtendedService : FiveECharacterService<ExtendedCharacter> {
                    override fun getAllCharacters(userId: String?): MutableList<ExtendedCharacter?> = mutableListOf(ExtendedCharacter())

                    override fun generate(): ExtendedCharacter = ExtendedCharacter()
                }

                val service = ExtendedService()
                val character = service.generate()

                character.customProperty shouldBe "custom"
            }
        }
    })
