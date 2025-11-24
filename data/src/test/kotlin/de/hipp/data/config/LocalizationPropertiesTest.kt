package de.hipp.data.config

import de.hipp.pnp.api.locale.SystemLocale
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class LocalizationPropertiesTest :
    FunSpec({

        context("Construction with default values") {
            test("should create LocalizationProperties with empty systems map by default") {
                val properties = LocalizationProperties()

                properties.systems.isEmpty() shouldBe true
            }

            test("should create LocalizationProperties with provided systems map") {
                val systemsMap =
                    mapOf(
                        "genefunk" to SystemLocale(),
                        "dnd5e" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 2
                properties.systems.keys.shouldContain("genefunk")
                properties.systems.keys.shouldContain("dnd5e")
            }

            test("should handle single system in map") {
                val systemsMap = mapOf("shadowrun" to SystemLocale())
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 1
                properties.systems.keys.shouldContain("shadowrun")
            }
        }

        context("String input handling - system names") {
            test("should handle unicode system names") {
                val systemsMap =
                    mapOf(
                        "龍與地下城" to SystemLocale(),
                        "ダンジョンズ＆ドラゴンズ" to SystemLocale(),
                        "Подземелья и драконы" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 3
                properties.systems.keys.shouldContain("龍與地下城")
                properties.systems.keys.shouldContain("ダンジョンズ＆ドラゴンズ")
                properties.systems.keys.shouldContain("Подземелья и драконы")
            }

            test("should handle system names with emojis") {
                val systemsMap =
                    mapOf(
                        "genefunk🧬" to SystemLocale(),
                        "dnd🐉" to SystemLocale(),
                        "cyberpunk🤖" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 3
                properties.systems.keys.shouldContain("genefunk🧬")
                properties.systems.keys.shouldContain("dnd🐉")
            }

            test("should handle system names with special characters") {
                val systemsMap =
                    mapOf(
                        "D&D 5e" to SystemLocale(),
                        "Shadowrun: 6th World" to SystemLocale(),
                        "Star Wars (FFG)" to SystemLocale(),
                        "Call of Cthulhu - 7th Ed." to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 4
                properties.systems.keys.shouldContain("D&D 5e")
                properties.systems.keys.shouldContain("Shadowrun: 6th World")
            }

            test("should handle very long system names") {
                val longName =
                    "This is an extraordinarily long system name that someone might " +
                        "create for testing purposes or perhaps as a joke but we should handle it anyway " +
                        "because you never know what users will input".repeat(3)
                val systemsMap = mapOf(longName to SystemLocale())
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 1
                properties.systems.keys.shouldContain(longName)
            }

            test("should handle empty string as system name") {
                val systemsMap = mapOf("" to SystemLocale())
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 1
                properties.systems.keys.shouldContain("")
            }

            test("should handle system names with whitespace variations") {
                val systemsMap =
                    mapOf(
                        "  genefunk  " to SystemLocale(),
                        "\tgenefunk\t" to SystemLocale(),
                        "\ngenefunk\n" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 3
            }
        }

        context("Data class behavior") {
            test("should support copy with modified systems") {
                val original =
                    LocalizationProperties(
                        systems = mapOf("genefunk" to SystemLocale()),
                    )

                val copied =
                    original.copy(
                        systems =
                            mapOf(
                                "genefunk" to SystemLocale(),
                                "dnd5e" to SystemLocale(),
                            ),
                    )

                original.systems.size shouldBe 1
                copied.systems.size shouldBe 2
            }

            test("should support equality comparison") {
                val locale1 = SystemLocale()
                val properties1 = LocalizationProperties(systems = mapOf("game1" to locale1))
                val properties2 = LocalizationProperties(systems = mapOf("game1" to locale1))

                properties1 shouldBe properties2
            }

            test("should have different hash codes for different content") {
                val properties1 = LocalizationProperties(systems = mapOf("game1" to SystemLocale()))
                val properties2 = LocalizationProperties(systems = mapOf("game2" to SystemLocale()))

                properties1.hashCode() shouldNotBe properties2.hashCode()
            }

            test("should generate meaningful toString representation") {
                val properties =
                    LocalizationProperties(
                        systems = mapOf("genefunk" to SystemLocale()),
                    )

                val stringRepresentation = properties.toString()
                stringRepresentation.shouldNotBe(null)
                stringRepresentation.shouldNotBe("")
            }
        }

        context("Systems map manipulation") {
            test("should handle replacing systems map entirely") {
                val properties =
                    LocalizationProperties(
                        systems = mapOf("game1" to SystemLocale()),
                    )

                properties.systems =
                    mapOf(
                        "game2" to SystemLocale(),
                        "game3" to SystemLocale(),
                    )

                properties.systems.size shouldBe 2
                properties.systems.keys.shouldContain("game2")
                properties.systems.keys.shouldContain("game3")
            }

            test("should handle setting systems to empty map") {
                val properties =
                    LocalizationProperties(
                        systems =
                            mapOf(
                                "game1" to SystemLocale(),
                                "game2" to SystemLocale(),
                            ),
                    )

                properties.systems = emptyMap()

                properties.systems.isEmpty() shouldBe true
            }

            test("should handle large systems map") {
                val largeSystems = (1..1000).associate { "game$it" to SystemLocale() }
                val properties = LocalizationProperties(systems = largeSystems)

                properties.systems.size shouldBe 1000
                properties.systems.keys.shouldContain("game1")
                properties.systems.keys.shouldContain("game500")
                properties.systems.keys.shouldContain("game1000")
            }
        }

        context("Edge cases") {
            test("should handle creating multiple instances with same data") {
                val systemsMap = mapOf("genefunk" to SystemLocale())
                val properties1 =
                    LocalizationProperties(systems = systemsMap)
                val properties2 = LocalizationProperties(systems = systemsMap)

                properties1 shouldBe properties2
            }

            test("should handle system names with only special characters") {
                val systemsMap =
                    mapOf(
                        "!@#$%^&*()" to SystemLocale(),
                        "【】《》" to SystemLocale(),
                        "°º¤ø,¸¸,ø¤º°" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 3
            }

            test("should maintain immutability semantics through copy") {
                val original = LocalizationProperties(systems = mapOf("game1" to SystemLocale()))
                val modified = original.copy()

                modified.systems = mapOf("game2" to SystemLocale())

                original.systems.size shouldBe 1
                modified.systems.size shouldBe 1
                original.systems.keys.shouldContain("game1")
                modified.systems.keys.shouldContain("game2")
            }
        }

        context("Real-world scenario testing") {
            test("should handle common RPG system names") {
                val systemsMap =
                    mapOf(
                        "dnd5e" to SystemLocale(),
                        "pathfinder2e" to SystemLocale(),
                        "callofcthulhu" to SystemLocale(),
                        "shadowrun6e" to SystemLocale(),
                        "starwars-ffg" to SystemLocale(),
                        "fate-core" to SystemLocale(),
                        "savage-worlds" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 7
                properties.systems.keys.shouldContain("dnd5e")
                properties.systems.keys.shouldContain("pathfinder2e")
                properties.systems.keys.shouldContain("callofcthulhu")
            }

            test("should handle kebab-case, snake_case, and camelCase system names") {
                val systemsMap =
                    mapOf(
                        "gene-funk-2090" to SystemLocale(),
                        "gene_funk_2090" to SystemLocale(),
                        "geneFunk2090" to SystemLocale(),
                    )
                val properties = LocalizationProperties(systems = systemsMap)

                properties.systems.size shouldBe 3
            }
        }
    })
