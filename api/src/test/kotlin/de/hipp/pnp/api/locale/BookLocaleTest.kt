package de.hipp.pnp.api.locale

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class BookLocaleTest :
    FunSpec({
        val mapper = jacksonObjectMapper()

        context("Construction and Default Values") {
            test("should create BookLocale with default empty maps") {
                val bookLocale = BookLocale()

                bookLocale.backgrounds shouldBe emptyMap()
                bookLocale.classes shouldBe emptyMap()
                bookLocale.features shouldBe emptyMap()
            }

            test("should create BookLocale with provided backgrounds") {
                val backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar"))
                val bookLocale = BookLocale(backgrounds = backgrounds)

                bookLocale.backgrounds shouldBe backgrounds
                bookLocale.classes shouldBe emptyMap()
                bookLocale.features shouldBe emptyMap()
            }

            test("should create BookLocale with provided classes") {
                val classes = mapOf("wizard" to LabelDesc("Wizard", "Magic user"))
                val bookLocale = BookLocale(classes = classes)

                bookLocale.backgrounds shouldBe emptyMap()
                bookLocale.classes shouldBe classes
                bookLocale.features shouldBe emptyMap()
            }

            test("should create BookLocale with provided features") {
                val features = mapOf("rage" to LabelDesc("Rage", "Fury"))
                val bookLocale = BookLocale(features = features)

                bookLocale.backgrounds shouldBe emptyMap()
                bookLocale.classes shouldBe emptyMap()
                bookLocale.features shouldBe features
            }

            test("should create BookLocale with all properties") {
                val backgrounds = mapOf("outlander" to LabelDesc("Outlander", "Wilderness dweller"))
                val classes = mapOf("ranger" to LabelDesc("Ranger", "Forest guardian"))
                val features = mapOf("tracking" to LabelDesc("Tracking", "Follow trails"))

                val bookLocale =
                    BookLocale(
                        backgrounds = backgrounds,
                        classes = classes,
                        features = features,
                    )

                bookLocale.backgrounds shouldBe backgrounds
                bookLocale.classes shouldBe classes
                bookLocale.features shouldBe features
            }
        }

        context("Backgrounds Map") {
            test("should handle single background") {
                val bookLocale =
                    BookLocale(
                        backgrounds = mapOf("hobbit" to LabelDesc("Hobbit", "Frodo's heritage")),
                    )

                bookLocale.backgrounds.shouldContainKey("hobbit")
                bookLocale.backgrounds["hobbit"]?.label shouldBe "Hobbit"
            }

            test("should handle multiple backgrounds") {
                val bookLocale =
                    BookLocale(
                        backgrounds =
                            mapOf(
                                "sage" to LabelDesc("Sage", "Scholar"),
                                "soldier" to LabelDesc("Soldier", "Warrior"),
                                "noble" to LabelDesc("Noble", "Aristocrat"),
                            ),
                    )

                bookLocale.backgrounds.shouldHaveSize(3)
                bookLocale.backgrounds.shouldContainKey("sage")
                bookLocale.backgrounds.shouldContainKey("soldier")
                bookLocale.backgrounds.shouldContainKey("noble")
            }

            test("should handle empty backgrounds map") {
                val bookLocale = BookLocale(backgrounds = emptyMap())

                bookLocale.backgrounds.shouldHaveSize(0)
            }

            test("should handle unicode keys in backgrounds") {
                val bookLocale =
                    BookLocale(
                        backgrounds = mapOf("武士" to LabelDesc("Samurai", "Japanese warrior")),
                    )

                bookLocale.backgrounds.shouldContainKey("武士")
            }

            test("should handle emoji keys in backgrounds") {
                val bookLocale =
                    BookLocale(
                        backgrounds = mapOf("🧙" to LabelDesc("Wizard", "Gandalf's class")),
                    )

                bookLocale.backgrounds.shouldContainKey("🧙")
            }
        }

        context("Classes Map") {
            test("should handle single class") {
                val bookLocale =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
                    )

                bookLocale.classes.shouldContainKey("wizard")
                bookLocale.classes["wizard"]?.description shouldBe "Master of magic"
            }

            test("should handle multiple classes") {
                val bookLocale =
                    BookLocale(
                        classes =
                            mapOf(
                                "wizard" to LabelDesc("Wizard", "Gandalf"),
                                "rogue" to LabelDesc("Rogue", "Frodo"),
                                "ranger" to LabelDesc("Ranger", "Aragorn"),
                            ),
                    )

                bookLocale.classes.shouldHaveSize(3)
            }

            test("should handle Neo's hacker class") {
                val bookLocale =
                    BookLocale(
                        classes = mapOf("hacker" to LabelDesc("Hacker", "The One")),
                    )

                bookLocale.classes["hacker"]?.label shouldBe "Hacker"
            }

            test("should handle Trinity's operator class") {
                val bookLocale =
                    BookLocale(
                        classes = mapOf("operator" to LabelDesc("Operator", "Trinity's role")),
                    )

                bookLocale.classes["operator"]?.description shouldBe "Trinity's role"
            }

            test("should handle empty classes map") {
                val bookLocale = BookLocale(classes = emptyMap())

                bookLocale.classes.shouldHaveSize(0)
            }
        }

        context("Features Map") {
            test("should handle single feature") {
                val bookLocale =
                    BookLocale(
                        features = mapOf("sneakattack" to LabelDesc("Sneak Attack", "Rogue ability")),
                    )

                bookLocale.features.shouldContainKey("sneakattack")
            }

            test("should handle multiple features") {
                val bookLocale =
                    BookLocale(
                        features =
                            mapOf(
                                "rage" to LabelDesc("Rage", "Barbarian fury"),
                                "spellcasting" to LabelDesc("Spellcasting", "Cast spells"),
                                "sneakattack" to LabelDesc("Sneak Attack", "Extra damage"),
                            ),
                    )

                bookLocale.features.shouldHaveSize(3)
            }

            test("should handle empty features map") {
                val bookLocale = BookLocale(features = emptyMap())

                bookLocale.features.shouldHaveSize(0)
            }
        }

        context("Data Class Behavior") {
            test("should implement equals correctly") {
                val bookLocale1 =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )
                val bookLocale2 =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )

                bookLocale1 shouldBe bookLocale2
            }

            test("should implement equals for different values") {
                val bookLocale1 =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )
                val bookLocale2 =
                    BookLocale(
                        classes = mapOf("rogue" to LabelDesc("Rogue", "Stealth")),
                    )

                bookLocale1 shouldNotBe bookLocale2
            }

            test("should implement hashCode correctly") {
                val bookLocale1 =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )
                val bookLocale2 =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )

                bookLocale1.hashCode() shouldBe bookLocale2.hashCode()
            }

            test("should support copy with modifications") {
                val original =
                    BookLocale(
                        backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar")),
                    )
                val copy =
                    original.copy(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )

                copy.backgrounds shouldBe original.backgrounds
                copy.classes shouldBe mapOf("wizard" to LabelDesc("Wizard", "Magic"))
            }

            test("should generate meaningful toString") {
                val bookLocale =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                    )
                val toString = bookLocale.toString()

                toString.shouldContain("BookLocale")
                toString.shouldContain("classes")
            }
        }

        context("JSON Serialization") {
            test("should serialize to JSON") {
                val bookLocale =
                    BookLocale(
                        backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar")),
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                        features = mapOf("rage" to LabelDesc("Rage", "Fury")),
                    )

                val json = mapper.writeValueAsString(bookLocale)

                json.shouldContain("backgrounds")
                json.shouldContain("classes")
                json.shouldContain("features")
                json.shouldContain("sage")
                json.shouldContain("wizard")
                json.shouldContain("rage")
            }

            test("should serialize empty BookLocale") {
                val bookLocale = BookLocale()

                val json = mapper.writeValueAsString(bookLocale)

                json.shouldContain("backgrounds")
                json.shouldContain("classes")
                json.shouldContain("features")
            }

            test("should serialize Frodo's hobbit lore") {
                val bookLocale =
                    BookLocale(
                        backgrounds = mapOf("shire" to LabelDesc("Shire Folk", "Hobbits of the Shire")),
                        classes = mapOf("rogue" to LabelDesc("Rogue", "Sneaky halfling")),
                    )

                val json = mapper.writeValueAsString(bookLocale)

                json.shouldContain("shire")
                json.shouldContain("Hobbits of the Shire")
            }

            test("should serialize unicode content") {
                val bookLocale =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("魔法使い", "Japanese wizard")),
                    )

                val json = mapper.writeValueAsString(bookLocale)

                json.shouldContain("魔法使い")
            }

            test("should serialize emoji content") {
                val bookLocale =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("🧙‍♂️ Wizard", "Magic user")),
                    )

                val json = mapper.writeValueAsString(bookLocale)

                json.shouldContain("🧙‍♂️")
            }
        }

        context("JSON Deserialization") {
            test("should deserialize from JSON") {
                val json =
                    """
                    {
                        "backgrounds": {"sage": {"label": "Sage", "description": "Scholar"}},
                        "classes": {"wizard": {"label": "Wizard", "description": "Magic"}},
                        "features": {"rage": {"label": "Rage", "description": "Fury"}}
                    }
                    """.trimIndent()

                val bookLocale = mapper.readValue<BookLocale>(json)

                bookLocale.backgrounds.shouldContainKey("sage")
                bookLocale.classes.shouldContainKey("wizard")
                bookLocale.features.shouldContainKey("rage")
            }

            test("should deserialize empty maps") {
                val json = """{"backgrounds": {}, "classes": {}, "features": {}}"""

                val bookLocale = mapper.readValue<BookLocale>(json)

                bookLocale.backgrounds.shouldHaveSize(0)
                bookLocale.classes.shouldHaveSize(0)
                bookLocale.features.shouldHaveSize(0)
            }

            test("should deserialize with missing fields") {
                val json = """{"classes": {"wizard": {"label": "Wizard", "description": "Magic"}}}"""

                val bookLocale = mapper.readValue<BookLocale>(json)

                bookLocale.classes.shouldContainKey("wizard")
                bookLocale.backgrounds.shouldHaveSize(0)
            }

            test("should deserialize Gandalf's lore") {
                val json =
                    """
                    {
                        "classes": {"wizard": {"label": "Wizard", "description": "Master of magic"}},
                        "features": {"fireworks": {"label": "Fireworks", "description": "Gandalf's specialty"}}
                    }
                    """.trimIndent()

                val bookLocale = mapper.readValue<BookLocale>(json)

                bookLocale.classes["wizard"]?.description shouldBe "Master of magic"
                bookLocale.features["fireworks"]?.description shouldBe "Gandalf's specialty"
            }

            test("should deserialize unicode") {
                val json = """{"classes": {"武士": {"label": "Samurai", "description": "Warrior"}}}"""

                val bookLocale = mapper.readValue<BookLocale>(json)

                bookLocale.classes.shouldContainKey("武士")
            }

            test("should deserialize emoji") {
                val json = """{"classes": {"🧙": {"label": "Wizard", "description": "Magic"}}}"""

                val bookLocale = mapper.readValue<BookLocale>(json)

                bookLocale.classes.shouldContainKey("🧙")
            }
        }

        context("Round-trip Serialization") {
            test("should maintain data through serialize-deserialize cycle") {
                val original =
                    BookLocale(
                        backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar")),
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")),
                        features = mapOf("rage" to LabelDesc("Rage", "Fury")),
                    )

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<BookLocale>(json)

                deserialized shouldBe original
            }

            test("should handle unicode in round-trip") {
                val original =
                    BookLocale(
                        classes = mapOf("武士" to LabelDesc("Samurai", "武士の道")),
                    )

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<BookLocale>(json)

                deserialized shouldBe original
            }

            test("should handle emoji in round-trip") {
                val original =
                    BookLocale(
                        classes = mapOf("🧙" to LabelDesc("🧙‍♂️ Wizard", "Magic user ⚡")),
                    )

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<BookLocale>(json)

                deserialized shouldBe original
            }
        }

        context("Edge Cases") {
            test("should handle large maps") {
                val largeMap = (1..100).associate { "key_$it" to LabelDesc("Label $it", "Desc $it") }
                val bookLocale =
                    BookLocale(
                        backgrounds = largeMap,
                        classes = largeMap,
                        features = largeMap,
                    )

                bookLocale.backgrounds.shouldHaveSize(100)
                bookLocale.classes.shouldHaveSize(100)
                bookLocale.features.shouldHaveSize(100)
            }

            test("should handle very long keys") {
                val longKey = "key_" + "x".repeat(1000)
                val bookLocale =
                    BookLocale(
                        classes = mapOf(longKey to LabelDesc("Label", "Description")),
                    )

                bookLocale.classes.shouldContainKey(longKey)
            }

            test("should handle special characters in keys") {
                val bookLocale =
                    BookLocale(
                        classes =
                            mapOf(
                                "class-name" to LabelDesc("Class", "Description"),
                                "class_name" to LabelDesc("Class2", "Description2"),
                                "class.name" to LabelDesc("Class3", "Description3"),
                            ),
                    )

                bookLocale.classes.shouldHaveSize(3)
            }

            test("should handle Aragorn's extensive lore") {
                val bookLocale =
                    BookLocale(
                        backgrounds =
                            mapOf(
                                "noble" to LabelDesc("Noble", "King of Gondor"),
                                "ranger" to LabelDesc("Ranger", "Strider"),
                            ),
                        classes =
                            mapOf(
                                "ranger" to LabelDesc("Ranger", "Master tracker"),
                                "fighter" to LabelDesc("Fighter", "Skilled warrior"),
                            ),
                        features =
                            mapOf(
                                "leadership" to LabelDesc("Leadership", "Lead armies"),
                                "healing" to LabelDesc("Healing", "Hands of the king"),
                            ),
                    )

                bookLocale.backgrounds.shouldHaveSize(2)
                bookLocale.classes.shouldHaveSize(2)
                bookLocale.features.shouldHaveSize(2)
            }
        }

        context("Mutability") {
            test("should allow modification of backgrounds") {
                val bookLocale = BookLocale()
                bookLocale.backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar"))

                bookLocale.backgrounds.shouldContainKey("sage")
            }

            test("should allow modification of classes") {
                val bookLocale = BookLocale()
                bookLocale.classes = mapOf("wizard" to LabelDesc("Wizard", "Magic"))

                bookLocale.classes.shouldContainKey("wizard")
            }

            test("should allow modification of features") {
                val bookLocale = BookLocale()
                bookLocale.features = mapOf("rage" to LabelDesc("Rage", "Fury"))

                bookLocale.features.shouldContainKey("rage")
            }
        }
    })
