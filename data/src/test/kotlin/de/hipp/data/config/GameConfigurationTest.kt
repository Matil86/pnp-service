package de.hipp.data.config

import de.hipp.pnp.base.entity.GameBooks
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GameConfigurationTest :
    FunSpec({

        context("Construction and initialization") {
            test("should create GameConfiguration with empty books list by default") {
                val config = GameConfiguration()

                config.books.shouldBeEmpty()
            }

            test("should create GameConfiguration with provided books") {
                val config = GameConfiguration()
                val book1 = GameBooks(name = "Core Rulebook")
                val book2 = GameBooks(name = "Expansion")
                config.books = listOf(book1, book2)

                config.books shouldHaveSize 2
                config.books.shouldContain(book1)
                config.books.shouldContain(book2)
            }

            test("should handle single book in list") {
                val config = GameConfiguration()
                val singleBook = GameBooks(name = "Player's Handbook")
                config.books = listOf(singleBook)

                config.books shouldHaveSize 1
                config.books.first() shouldBe singleBook
            }

            test("should handle multiple books with same reference") {
                val config = GameConfiguration()
                val book = GameBooks(name = "Shared Book")
                config.books = listOf(book, book, book)

                config.books shouldHaveSize 3
                config.books.all { it === book } shouldBe true
            }
        }

        context("PostConstruct initialization") {
            test("should successfully call init method") {
                val config = GameConfiguration()
                config.books = emptyList()

                // init() logs but doesn't throw or modify state
                config.init()

                config.books.shouldBeEmpty()
            }

            test("should call init with populated books list") {
                val config = GameConfiguration()
                val book1 = GameBooks(name = "Book 1")
                val book2 = GameBooks(name = "Book 2")
                config.books = listOf(book1, book2)

                config.init()

                config.books shouldHaveSize 2
            }

            test("should handle init with large books list") {
                val config = GameConfiguration()
                val books = (1..100).map { GameBooks(name = "Book $it") }
                config.books = books

                config.init()

                config.books shouldHaveSize 100
            }
        }

        context("Books list manipulation") {
            test("should replace books list completely") {
                val config = GameConfiguration()
                val initialBooks = listOf(GameBooks(name = "Old Book 1"), GameBooks(name = "Old Book 2"))
                config.books = initialBooks

                val newBooks = listOf(GameBooks(name = "New Book"))
                config.books = newBooks

                config.books shouldBe newBooks
                config.books shouldHaveSize 1
            }

            test("should handle setting books to empty after having books") {
                val config = GameConfiguration()
                config.books = listOf(GameBooks(name = "Book 1"), GameBooks(name = "Book 2"), GameBooks(name = "Book 3"))

                config.books = emptyList()

                config.books.shouldBeEmpty()
            }

            test("should maintain books list reference") {
                val config = GameConfiguration()
                val books = listOf(GameBooks(name = "Reference Book 1"), GameBooks(name = "Reference Book 2"))
                config.books = books

                val retrievedBooks = config.books
                retrievedBooks shouldBe books
            }
        }

        context("Edge cases") {
            test("should handle null-like scenarios with empty list") {
                val config = GameConfiguration()
                config.books = emptyList()

                config.books.shouldNotBe(null)
                config.books.shouldBeEmpty()
            }

            test("should handle multiple reinitialization calls") {
                val config = GameConfiguration()
                config.books = listOf(GameBooks(name = "Reinitialized Book"))

                config.init()
                config.init()
                config.init()

                config.books shouldHaveSize 1
            }

            test("should maintain books order") {
                val config = GameConfiguration()
                val book1 = GameBooks(name = "First")
                val book2 = GameBooks(name = "Second")
                val book3 = GameBooks(name = "Third")
                config.books = listOf(book1, book2, book3)

                config.books[0] shouldBe book1
                config.books[1] shouldBe book2
                config.books[2] shouldBe book3
            }
        }
    })
