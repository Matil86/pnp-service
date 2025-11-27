package de.hipp.pnp.api.locale

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class SystemLocaleTest :
    FunSpec({
        val mapper = jacksonObjectMapper()

        context("Construction and Default Values") {
            test("should create SystemLocale with default empty map") {
                val systemLocale = SystemLocale()

                systemLocale.books shouldBe emptyMap()
            }

            test("should create SystemLocale with provided books") {
                val books = mapOf("phb" to BookLocale())
                val systemLocale = SystemLocale(books = books)

                systemLocale.books shouldBe books
            }

            test("should create SystemLocale with single book") {
                val phb =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
                    )
                val systemLocale = SystemLocale(books = mapOf("phb" to phb))

                systemLocale.books.shouldContainKey("phb")
                systemLocale.books["phb"] shouldBe phb
            }

            test("should create SystemLocale with multiple books") {
                val phb = BookLocale(classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")))
                val xanathar = BookLocale(classes = mapOf("artificer" to LabelDesc("Artificer", "Inventor")))

                val systemLocale =
                    SystemLocale(
                        books =
                            mapOf(
                                "phb" to phb,
                                "xanathar" to xanathar,
                            ),
                    )

                systemLocale.books.shouldHaveSize(2)
                systemLocale.books.shouldContainKey("phb")
                systemLocale.books.shouldContainKey("xanathar")
            }
        }

        context("Books Map") {
            test("should handle single book") {
                val systemLocale =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )

                systemLocale.books.shouldContainKey("phb")
                systemLocale.books.shouldHaveSize(1)
            }

            test("should handle multiple books") {
                val systemLocale =
                    SystemLocale(
                        books =
                            mapOf(
                                "phb" to BookLocale(),
                                "dmg" to BookLocale(),
                                "mm" to BookLocale(),
                            ),
                    )

                systemLocale.books.shouldHaveSize(3)
                systemLocale.books.shouldContainKey("phb")
                systemLocale.books.shouldContainKey("dmg")
                systemLocale.books.shouldContainKey("mm")
            }

            test("should handle empty books map") {
                val systemLocale = SystemLocale(books = emptyMap())

                systemLocale.books.shouldHaveSize(0)
            }

            test("should handle Frodo's Shire books") {
                val shireGuide =
                    BookLocale(
                        backgrounds = mapOf("hobbit" to LabelDesc("Hobbit", "Folk of the Shire")),
                    )
                val systemLocale =
                    SystemLocale(
                        books = mapOf("shire_guide" to shireGuide),
                    )

                systemLocale.books["shire_guide"]?.backgrounds?.shouldContainKey("hobbit")
            }

            test("should handle Gandalf's wizard tomes") {
                val wizardTome =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
                        features = mapOf("fireball" to LabelDesc("Fireball", "Explosive spell")),
                    )
                val systemLocale =
                    SystemLocale(
                        books = mapOf("wizard_tome" to wizardTome),
                    )

                systemLocale.books["wizard_tome"]?.classes?.shouldContainKey("wizard")
            }

            test("should handle Aragorn's ranger handbook") {
                val rangerBook =
                    BookLocale(
                        classes = mapOf("ranger" to LabelDesc("Ranger", "Master tracker")),
                        backgrounds = mapOf("outlander" to LabelDesc("Outlander", "Wilderness dweller")),
                    )
                val systemLocale =
                    SystemLocale(
                        books = mapOf("ranger_handbook" to rangerBook),
                    )

                systemLocale.books.shouldContainKey("ranger_handbook")
            }
        }

        context("Data Class Behavior") {
            test("should implement equals correctly") {
                val systemLocale1 =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )
                val systemLocale2 =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )

                systemLocale1 shouldBe systemLocale2
            }

            test("should implement equals for different values") {
                val systemLocale1 =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )
                val systemLocale2 =
                    SystemLocale(
                        books = mapOf("dmg" to BookLocale()),
                    )

                systemLocale1 shouldNotBe systemLocale2
            }

            test("should implement hashCode correctly") {
                val systemLocale1 =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )
                val systemLocale2 =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )

                systemLocale1.hashCode() shouldBe systemLocale2.hashCode()
            }

            test("should support copy with modifications") {
                val original =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )
                val copy =
                    original.copy(
                        books = mapOf("dmg" to BookLocale()),
                    )

                copy.books.shouldContainKey("dmg")
                copy.books.shouldHaveSize(1)
            }

            test("should generate meaningful toString") {
                val systemLocale =
                    SystemLocale(
                        books = mapOf("phb" to BookLocale()),
                    )
                val toString = systemLocale.toString()

                toString.shouldContain("SystemLocale")
                toString.shouldContain("books")
            }
        }

        context("JSON Serialization") {
            test("should serialize to JSON") {
                val phb =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
                    )
                val systemLocale = SystemLocale(books = mapOf("phb" to phb))

                val json = mapper.writeValueAsString(systemLocale)

                json.shouldContain("books")
                json.shouldContain("phb")
                json.shouldContain("wizard")
            }

            test("should serialize empty SystemLocale") {
                val systemLocale = SystemLocale()

                val json = mapper.writeValueAsString(systemLocale)

                json.shouldContain("books")
            }

            test("should serialize multiple books") {
                val phb = BookLocale(classes = mapOf("wizard" to LabelDesc("Wizard", "Magic")))
                val dmg = BookLocale(classes = mapOf("dm" to LabelDesc("DM", "Game master")))

                val systemLocale =
                    SystemLocale(
                        books =
                            mapOf(
                                "phb" to phb,
                                "dmg" to dmg,
                            ),
                    )

                val json = mapper.writeValueAsString(systemLocale)

                json.shouldContain("phb")
                json.shouldContain("dmg")
            }

            test("should serialize Neo's Matrix system") {
                val matrix =
                    BookLocale(
                        classes = mapOf("hacker" to LabelDesc("Hacker", "The One")),
                    )
                val systemLocale = SystemLocale(books = mapOf("matrix_core" to matrix))

                val json = mapper.writeValueAsString(systemLocale)

                json.shouldContain("matrix_core")
                json.shouldContain("hacker")
            }

            test("should serialize unicode keys") {
                val book = BookLocale()
                val systemLocale = SystemLocale(books = mapOf("日本語" to book))

                val json = mapper.writeValueAsString(systemLocale)

                json.shouldContain("日本語")
            }

            test("should serialize emoji keys") {
                val book = BookLocale()
                val systemLocale = SystemLocale(books = mapOf("🎲" to book))

                val json = mapper.writeValueAsString(systemLocale)

                json.shouldContain("🎲")
            }
        }

        context("JSON Deserialization") {
            test("should deserialize from JSON") {
                val json =
                    """
                    {
                        "books": {
                            "phb": {
                                "backgrounds": {},
                                "classes": {"wizard": {"label": "Wizard", "description": "Magic"}},
                                "features": {}
                            }
                        }
                    }
                    """.trimIndent()

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books.shouldContainKey("phb")
                systemLocale.books["phb"]?.classes?.shouldContainKey("wizard")
            }

            test("should deserialize empty books") {
                val json = """{"books": {}}"""

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books.shouldHaveSize(0)
            }

            test("should deserialize with missing books field") {
                val json = """{}"""

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books.shouldHaveSize(0)
            }

            test("should deserialize multiple books") {
                val json =
                    """
                    {
                        "books": {
                            "phb": {"backgrounds": {}, "classes": {}, "features": {}},
                            "dmg": {"backgrounds": {}, "classes": {}, "features": {}}
                        }
                    }
                    """.trimIndent()

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books.shouldHaveSize(2)
                systemLocale.books.shouldContainKey("phb")
                systemLocale.books.shouldContainKey("dmg")
            }

            test("should deserialize Trinity's operator manual") {
                val json =
                    """
                    {
                        "books": {
                            "operator_manual": {
                                "backgrounds": {},
                                "classes": {"operator": {"label": "Operator", "description": "Elite"}},
                                "features": {}
                            }
                        }
                    }
                    """.trimIndent()

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books["operator_manual"]?.classes?.shouldContainKey("operator")
            }

            test("should deserialize unicode") {
                val json = """{"books": {"日本語": {"backgrounds": {}, "classes": {}, "features": {}}}}"""

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books.shouldContainKey("日本語")
            }

            test("should deserialize emoji") {
                val json = """{"books": {"🎲": {"backgrounds": {}, "classes": {}, "features": {}}}}"""

                val systemLocale = mapper.readValue<SystemLocale>(json)

                systemLocale.books.shouldContainKey("🎲")
            }
        }

        context("Round-trip Serialization") {
            test("should maintain data through serialize-deserialize cycle") {
                val phb =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
                    )
                val original = SystemLocale(books = mapOf("phb" to phb))

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<SystemLocale>(json)

                deserialized shouldBe original
            }

            test("should handle empty SystemLocale in round-trip") {
                val original = SystemLocale()

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<SystemLocale>(json)

                deserialized shouldBe original
            }

            test("should handle unicode in round-trip") {
                val book =
                    BookLocale(
                        classes = mapOf("武士" to LabelDesc("Samurai", "Warrior")),
                    )
                val original = SystemLocale(books = mapOf("日本語" to book))

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<SystemLocale>(json)

                deserialized shouldBe original
            }

            test("should handle emoji in round-trip") {
                val book =
                    BookLocale(
                        classes = mapOf("🧙" to LabelDesc("Wizard", "Magic")),
                    )
                val original = SystemLocale(books = mapOf("🎲" to book))

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<SystemLocale>(json)

                deserialized shouldBe original
            }

            test("should handle complex nested structure in round-trip") {
                val phb =
                    BookLocale(
                        backgrounds =
                            mapOf(
                                "sage" to LabelDesc("Sage", "Scholar"),
                                "soldier" to LabelDesc("Soldier", "Warrior"),
                            ),
                        classes =
                            mapOf(
                                "wizard" to LabelDesc("Wizard", "Magic"),
                                "fighter" to LabelDesc("Fighter", "Combat"),
                            ),
                        features =
                            mapOf(
                                "secondwind" to LabelDesc("Second Wind", "Healing"),
                            ),
                    )
                val original = SystemLocale(books = mapOf("phb" to phb))

                val json = mapper.writeValueAsString(original)
                val deserialized = mapper.readValue<SystemLocale>(json)

                deserialized shouldBe original
            }
        }

        context("Nested BookLocale Access") {
            test("should access nested classes") {
                val phb =
                    BookLocale(
                        classes = mapOf("wizard" to LabelDesc("Wizard", "Master of magic")),
                    )
                val systemLocale = SystemLocale(books = mapOf("phb" to phb))

                val wizardClass = systemLocale.books["phb"]?.classes?.get("wizard")

                wizardClass?.label shouldBe "Wizard"
                wizardClass?.description shouldBe "Master of magic"
            }

            test("should access nested backgrounds") {
                val phb =
                    BookLocale(
                        backgrounds = mapOf("sage" to LabelDesc("Sage", "Scholar")),
                    )
                val systemLocale = SystemLocale(books = mapOf("phb" to phb))

                val sageBackground = systemLocale.books["phb"]?.backgrounds?.get("sage")

                sageBackground?.label shouldBe "Sage"
            }

            test("should access nested features") {
                val phb =
                    BookLocale(
                        features = mapOf("rage" to LabelDesc("Rage", "Fury")),
                    )
                val systemLocale = SystemLocale(books = mapOf("phb" to phb))

                val rageFeature = systemLocale.books["phb"]?.features?.get("rage")

                rageFeature?.description shouldBe "Fury"
            }

            test("should handle missing book gracefully") {
                val systemLocale = SystemLocale(books = mapOf("phb" to BookLocale()))

                val missing = systemLocale.books["nonexistent"]

                missing shouldBe null
            }

            test("should handle Frodo's complete character data") {
                val shireBook =
                    BookLocale(
                        backgrounds = mapOf("hobbit" to LabelDesc("Hobbit", "Small folk")),
                        classes = mapOf("rogue" to LabelDesc("Rogue", "Sneaky")),
                        features = mapOf("lucky" to LabelDesc("Lucky", "Halfling luck")),
                    )
                val systemLocale = SystemLocale(books = mapOf("shire" to shireBook))

                systemLocale.books["shire"]?.backgrounds?.shouldContainKey("hobbit")
                systemLocale.books["shire"]?.classes?.shouldContainKey("rogue")
                systemLocale.books["shire"]?.features?.shouldContainKey("lucky")
            }
        }

        context("Edge Cases") {
            test("should handle large number of books") {
                val books = (1..100).associate { "book_$it" to BookLocale() }
                val systemLocale = SystemLocale(books = books)

                systemLocale.books.shouldHaveSize(100)
            }

            test("should handle very long book keys") {
                val longKey = "book_" + "x".repeat(1000)
                val systemLocale = SystemLocale(books = mapOf(longKey to BookLocale()))

                systemLocale.books.shouldContainKey(longKey)
            }

            test("should handle special characters in book keys") {
                val systemLocale =
                    SystemLocale(
                        books =
                            mapOf(
                                "book-name" to BookLocale(),
                                "book_name" to BookLocale(),
                                "book.name" to BookLocale(),
                            ),
                    )

                systemLocale.books.shouldHaveSize(3)
            }

            test("should handle Aragorn's extensive library") {
                val books =
                    mapOf(
                        "gondor_lore" to
                            BookLocale(
                                backgrounds = mapOf("noble" to LabelDesc("Noble", "King")),
                            ),
                        "ranger_guide" to
                            BookLocale(
                                classes = mapOf("ranger" to LabelDesc("Ranger", "Tracker")),
                            ),
                        "dunedain_secrets" to
                            BookLocale(
                                features = mapOf("longevity" to LabelDesc("Longevity", "Extended life")),
                            ),
                    )
                val systemLocale = SystemLocale(books = books)

                systemLocale.books.shouldHaveSize(3)
            }

            test("should handle empty book entries") {
                val systemLocale =
                    SystemLocale(
                        books =
                            mapOf(
                                "empty1" to BookLocale(),
                                "empty2" to BookLocale(),
                                "empty3" to BookLocale(),
                            ),
                    )

                systemLocale.books.shouldHaveSize(3)
                systemLocale.books["empty1"]?.classes?.shouldHaveSize(0)
            }
        }

        context("Mutability") {
            test("should allow modification of books") {
                val systemLocale = SystemLocale()
                systemLocale.books = mapOf("phb" to BookLocale())

                systemLocale.books.shouldContainKey("phb")
            }

            test("should allow replacement of books") {
                val systemLocale = SystemLocale(books = mapOf("phb" to BookLocale()))
                systemLocale.books = mapOf("dmg" to BookLocale())

                systemLocale.books.shouldContainKey("dmg")
                systemLocale.books.shouldHaveSize(1)
            }

            test("should allow setting to empty map") {
                val systemLocale = SystemLocale(books = mapOf("phb" to BookLocale()))
                systemLocale.books = emptyMap()

                systemLocale.books.shouldHaveSize(0)
            }
        }

        context("Real-world D&D System") {
            test("should model complete 5E system") {
                val phb =
                    BookLocale(
                        classes =
                            mapOf(
                                "wizard" to LabelDesc("Wizard", "Gandalf's class"),
                                "rogue" to LabelDesc("Rogue", "Frodo's class"),
                                "ranger" to LabelDesc("Ranger", "Aragorn's class"),
                            ),
                    )
                val dmg =
                    BookLocale(
                        features =
                            mapOf(
                                "magic_items" to LabelDesc("Magic Items", "Legendary treasures"),
                            ),
                    )

                val systemLocale =
                    SystemLocale(
                        books =
                            mapOf(
                                "phb" to phb,
                                "dmg" to dmg,
                            ),
                    )

                systemLocale.books.shouldHaveSize(2)
                systemLocale.books["phb"]?.classes?.shouldHaveSize(3)
            }

            test("should model Neo's Matrix system") {
                val matrixCore =
                    BookLocale(
                        classes =
                            mapOf(
                                "hacker" to LabelDesc("Hacker", "Neo's ability"),
                                "operator" to LabelDesc("Operator", "Trinity's role"),
                            ),
                    )

                val systemLocale =
                    SystemLocale(
                        books = mapOf("matrix_core" to matrixCore),
                    )

                systemLocale.books["matrix_core"]?.classes?.shouldContainKey("hacker")
                systemLocale.books["matrix_core"]?.classes?.shouldContainKey("operator")
            }
        }
    })
