package de.hipp.pnp.api.fivee.interfaces

import de.hipp.pnp.api.locale.BookLocale
import de.hipp.pnp.api.locale.LabelDesc
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

// Test implementation for Frodo's game
private class FrodoDataProducer : FiveEDataProducer {
    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> {
        val hobbits =
            BookLocale(
                classes = mapOf("rogue" to LabelDesc("Rogue", "Sneaky halfling")),
            )
        return mutableMapOf("hobbit_lore" to hobbits)
    }

    override fun getLanguageKeysByGameTypeAndLanguage(
        gameType: Int,
        locale: String?,
    ): MutableMap<String, BookLocale> =
        if (gameType == 0 && locale == "en_US") {
            mutableMapOf("shire" to BookLocale())
        } else {
            mutableMapOf()
        }
}

// Test implementation for Gandalf's game
private class GandalfDataProducer : FiveEDataProducer {
    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> {
        val wizards =
            BookLocale(
                classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
            )
        return mutableMapOf(
            "istari" to wizards,
            "maiar" to BookLocale(),
        )
    }

    override fun getLanguageKeysByGameTypeAndLanguage(
        gameType: Int,
        locale: String?,
    ): MutableMap<String, BookLocale>? {
        return null // Can return null
    }
}

// Test implementation for empty data
private class EmptyDataProducer : FiveEDataProducer {
    override fun getAllLanguageKeys(): MutableMap<String, BookLocale>? = null

    override fun getLanguageKeysByGameTypeAndLanguage(
        gameType: Int,
        locale: String?,
    ): MutableMap<String, BookLocale>? = null
}

class FiveEDataProducerTest :
    FunSpec({

        context("getAllLanguageKeys Method Contract") {
            test("should get all language keys for Frodo") {
                val producer = FrodoDataProducer()

                val result = producer.getAllLanguageKeys()

                result shouldNotBe null
                result.shouldContainKey("hobbit_lore")
                result.shouldHaveSize(1)
            }

            test("should get all language keys for Gandalf") {
                val producer = GandalfDataProducer()

                val result = producer.getAllLanguageKeys()

                result shouldNotBe null
                result.shouldContainKey("istari")
                result.shouldContainKey("maiar")
                result.shouldHaveSize(2)
            }

            test("should return null from getAllLanguageKeys") {
                val producer = EmptyDataProducer()

                val result = producer.getAllLanguageKeys()

                result shouldBe null
            }

            test("should return empty map") {
                class EmptyMapProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = EmptyMapProducer()
                val result = producer.getAllLanguageKeys()

                result shouldNotBe null
                result.shouldHaveSize(0)
            }

            test("should handle Neo's Matrix lore") {
                class NeoDataProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> =
                        mutableMapOf(
                            "matrix" to
                                BookLocale(
                                    classes = mapOf("hacker" to LabelDesc("Hacker", "One who bends reality")),
                                ),
                        )

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = NeoDataProducer()
                val result = producer.getAllLanguageKeys()

                result.shouldContainKey("matrix")
            }

            test("should handle Trinity's data") {
                class TrinityDataProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> =
                        mutableMapOf(
                            "operator" to
                                BookLocale(
                                    classes = mapOf("operative" to LabelDesc("Operative", "Elite operator")),
                                ),
                        )

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = TrinityDataProducer()
                val result = producer.getAllLanguageKeys()

                result.size shouldBe 1
            }
        }

        context("getLanguageKeysByGameTypeAndLanguage Method Contract") {
            test("should get keys for Frodo's game type and locale") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

                result shouldNotBe null
                result.shouldContainKey("shire")
                result.shouldHaveSize(1)
            }

            test("should return empty map for unknown game type") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(999, "en_US")

                result shouldNotBe null
                result.shouldHaveSize(0)
            }

            test("should return empty map for unknown locale") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "xx_XX")

                result shouldNotBe null
                result.shouldHaveSize(0)
            }

            test("should return null for Gandalf's implementation") {
                val producer = GandalfDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(1, "en_US")

                result shouldBe null
            }

            test("should handle null locale") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, null)

                result shouldNotBe null
            }

            test("should handle empty string locale") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "")

                result shouldNotBe null
            }

            test("should handle blank string locale") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "   ")

                result shouldNotBe null
            }

            test("should handle unicode locale") {
                class UnicodeLocaleProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        if (locale == "ja_JP") {
                            mutableMapOf("日本" to BookLocale())
                        } else {
                            mutableMapOf()
                        }
                }

                val producer = UnicodeLocaleProducer()
                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "ja_JP")

                result.shouldContainKey("日本")
            }

            test("should handle emoji in locale") {
                class EmojiLocaleProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        if (locale == "emoji_🌍") {
                            mutableMapOf("world" to BookLocale())
                        } else {
                            mutableMapOf()
                        }
                }

                val producer = EmojiLocaleProducer()
                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "emoji_🌍")

                result.shouldContainKey("world")
            }
        }

        context("Game Type Filtering") {
            test("should filter by positive game type") {
                class FilteringProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        when (gameType) {
                            0 -> mutableMapOf("genefunk" to BookLocale())
                            1 -> mutableMapOf("campaign1" to BookLocale())
                            else -> mutableMapOf()
                        }
                }

                val producer = FilteringProducer()

                producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US").shouldContainKey("genefunk")
                producer.getLanguageKeysByGameTypeAndLanguage(1, "en_US").shouldContainKey("campaign1")
                producer.getLanguageKeysByGameTypeAndLanguage(2, "en_US").shouldHaveSize(0)
            }

            test("should handle negative game type") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(-1, "en_US")

                result shouldNotBe null
            }

            test("should handle zero game type") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

                result shouldNotBe null
            }

            test("should handle very large game type") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(Int.MAX_VALUE, "en_US")

                result shouldNotBe null
            }

            test("should handle Aragorn's campaign (game type 5)") {
                class AragornProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        if (gameType == 5 && locale == "en_GB") {
                            mutableMapOf("gondor" to BookLocale())
                        } else {
                            mutableMapOf()
                        }
                }

                val producer = AragornProducer()
                val result = producer.getLanguageKeysByGameTypeAndLanguage(5, "en_GB")

                result.shouldContainKey("gondor")
            }
        }

        context("Locale Variations") {
            test("should handle en_US locale") {
                class LocaleProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        when (locale) {
                            "en_US" -> mutableMapOf("american" to BookLocale())
                            "en_GB" -> mutableMapOf("british" to BookLocale())
                            "de_DE" -> mutableMapOf("german" to BookLocale())
                            else -> mutableMapOf()
                        }
                }

                val producer = LocaleProducer()

                producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US").shouldContainKey("american")
                producer.getLanguageKeysByGameTypeAndLanguage(0, "en_GB").shouldContainKey("british")
                producer.getLanguageKeysByGameTypeAndLanguage(0, "de_DE").shouldContainKey("german")
            }

            test("should handle hyphen format locale") {
                class HyphenLocaleProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        if (locale == "en-US") {
                            mutableMapOf("data" to BookLocale())
                        } else {
                            mutableMapOf()
                        }
                }

                val producer = HyphenLocaleProducer()
                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en-US")

                result.shouldContainKey("data")
            }

            test("should handle case-sensitive locale") {
                class CaseSensitiveProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> =
                        if (locale == "en_US") {
                            mutableMapOf("correct" to BookLocale())
                        } else {
                            mutableMapOf()
                        }
                }

                val producer = CaseSensitiveProducer()

                producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US").shouldHaveSize(1)
                producer.getLanguageKeysByGameTypeAndLanguage(0, "EN_US").shouldHaveSize(0)
            }
        }

        context("Mutable Map Operations") {
            test("should return mutable map from getAllLanguageKeys") {
                val producer = FrodoDataProducer()

                val result = producer.getAllLanguageKeys()

                result.put("new_key", BookLocale())
                result.shouldContainKey("new_key")
            }

            test("should return mutable map from getLanguageKeysByGameTypeAndLanguage") {
                val producer = FrodoDataProducer()

                val result = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

                result.put("new_key", BookLocale())
                result.shouldContainKey("new_key")
            }

            test("should allow clearing mutable map") {
                val producer = FrodoDataProducer()

                val result = producer.getAllLanguageKeys()

                result.clear()
                result.shouldHaveSize(0)
            }

            test("should allow removing from mutable map") {
                val producer = FrodoDataProducer()

                val result = producer.getAllLanguageKeys()

                result.remove("hobbit_lore")
                result.shouldHaveSize(0)
            }
        }

        context("BookLocale Content") {
            test("should return BookLocale with classes") {
                val producer = FrodoDataProducer()

                val result = producer.getAllLanguageKeys()
                val bookLocale = result.get("hobbit_lore")

                bookLocale shouldNotBe null
                bookLocale?.classes?.shouldContainKey("rogue")
            }

            test("should return BookLocale with backgrounds") {
                class BackgroundProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> =
                        mutableMapOf(
                            "data" to
                                BookLocale(
                                    backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar of lore")),
                                ),
                        )

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = BackgroundProducer()
                val result = producer.getAllLanguageKeys()
                val bookLocale = result["data"]

                bookLocale?.backgrounds?.shouldContainKey("sage")
            }

            test("should return BookLocale with features") {
                class FeatureProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> =
                        mutableMapOf(
                            "data" to
                                BookLocale(
                                    features = mapOf("rage" to LabelDesc("Rage", "Barbarian fury")),
                                ),
                        )

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = FeatureProducer()
                val result = producer.getAllLanguageKeys()
                val bookLocale = result["data"]

                bookLocale?.features?.shouldContainKey("rage")
            }

            test("should return empty BookLocale") {
                val producer = GandalfDataProducer()

                val result = producer.getAllLanguageKeys()
                val bookLocale = result.get("maiar")

                bookLocale shouldNotBe null
                bookLocale?.classes?.size shouldBe 0
            }
        }

        context("Edge Cases") {
            test("should handle large map of language keys") {
                class LargeMapProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> =
                        MutableList(100) { "key_$it" to BookLocale() }.toMap().toMutableMap()

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = LargeMapProducer()
                val result = producer.getAllLanguageKeys()

                result.shouldHaveSize(100)
            }

            test("should handle unicode keys") {
                class UnicodeKeyProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf("日本語" to BookLocale())

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = UnicodeKeyProducer()
                val result = producer.getAllLanguageKeys()

                result.shouldContainKey("日本語")
            }

            test("should handle emoji keys") {
                class EmojiKeyProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> = mutableMapOf("🎲" to BookLocale())

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = EmojiKeyProducer()
                val result = producer.getAllLanguageKeys()

                result.shouldContainKey("🎲")
            }

            test("should handle very long keys") {
                class LongKeyProducer : FiveEDataProducer {
                    override fun getAllLanguageKeys(): MutableMap<String, BookLocale> {
                        val longKey = "key_" + "x".repeat(1000)
                        return mutableMapOf(longKey to BookLocale())
                    }

                    override fun getLanguageKeysByGameTypeAndLanguage(
                        gameType: Int,
                        locale: String?,
                    ): MutableMap<String, BookLocale> = mutableMapOf()
                }

                val producer = LongKeyProducer()
                val result = producer.getAllLanguageKeys()

                result.shouldHaveSize(1)
            }
        }

        context("Interface Contract Verification") {
            test("should implement both methods") {
                val producer = FrodoDataProducer()

                // Both methods should be callable
                producer.getAllLanguageKeys() shouldNotBe null
                producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US") shouldNotBe null
            }

            test("should allow multiple implementations") {
                val frodoProducer = FrodoDataProducer()
                val gandalfProducer = GandalfDataProducer()

                frodoProducer.getAllLanguageKeys().shouldHaveSize(1)
                gandalfProducer.getAllLanguageKeys().shouldHaveSize(2)
            }

            test("should support polymorphism") {
                val producer: FiveEDataProducer = FrodoDataProducer()

                val allKeys = producer.getAllLanguageKeys()
                val filteredKeys = producer.getLanguageKeysByGameTypeAndLanguage(0, "en_US")

                allKeys shouldNotBe null
                filteredKeys shouldNotBe null
            }
        }
    })
