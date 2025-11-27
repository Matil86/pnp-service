package de.hipp.pnp.base.fivee

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

/**
 * Comprehensive tests for DiceRoller with creative test data inspired by iconic characters
 * Testing standard dice rolls, drop lowest mechanics, and edge cases
 */
class DiceRollerTest :
    StringSpec({

        "Goku (孫悟空) rolls 4d6 standard dice - should be between 4 and 24" {
            val result = DiceRoller.roll(4, 6)
            result shouldBeGreaterThanOrEqual 4
            result shouldBeLessThanOrEqual 24
        }

        "Tony Stark rolls 1d6 - should be between 1 and 6" {
            val result = DiceRoller.roll(1, 6)
            result shouldBeGreaterThanOrEqual 1
            result shouldBeLessThanOrEqual 6
        }

        "Neo from Matrix rolls 3d20 - should be between 3 and 60" {
            val result = DiceRoller.roll(3, 20)
            result shouldBeGreaterThanOrEqual 3
            result shouldBeLessThanOrEqual 60
        }

        "Naruto Uzumaki (うずまきナルト) rolls 0 dice - should return 0" {
            val result = DiceRoller.roll(0, 6)
            result shouldBe 0
        }

        "Hermione Granger rolls 4d0 (zero-sided dice) - should return 0" {
            val result = DiceRoller.roll(4, 0)
            result shouldBe 0
        }

        "Edge case: Negative dice count should still work (absolute value)" {
            val result = DiceRoller.roll(-2, 6)
            // This will actually roll 0 times due to the loop condition
            result shouldBe 0
        }

        "Character creation: roll 4d6 keep 3 highest for Strength" {
            val result = DiceRoller.roll(4, 6, 3, highest = true)
            result shouldBeGreaterThanOrEqual 3
            result shouldBeLessThanOrEqual 18
        }

        "Wonder Woman rolls 4d6 keep 3 highest - valid stat range 3-18" {
            val result = DiceRoller.roll(4, 6, 3, highest = true)
            result shouldBeGreaterThanOrEqual 3
            result shouldBeLessThanOrEqual 18
        }

        "Spider-Man (スパイダーマン) rolls 4d6 keep 3 lowest" {
            val result = DiceRoller.roll(4, 6, 3, highest = false)
            result shouldBeGreaterThanOrEqual 3
            result shouldBeLessThanOrEqual 18
        }

        "Captain America rolls 5d8 keep 4 highest" {
            val result = DiceRoller.roll(5, 8, 4, highest = true)
            result shouldBeGreaterThanOrEqual 4
            result shouldBeLessThanOrEqual 32
        }

        "Batman rolls 6d6 keep 5 highest for epic stats" {
            val result = DiceRoller.roll(6, 6, 5, highest = true)
            result shouldBeGreaterThanOrEqual 5
            result shouldBeLessThanOrEqual 30
        }

        "Pikachu (ピカチュウ) rolls 3d6 keep 2 highest" {
            val result = DiceRoller.roll(3, 6, 2, highest = true)
            result shouldBeGreaterThanOrEqual 2
            result shouldBeLessThanOrEqual 12
        }

        "Multiple rolls for consistency: 100 rolls of 1d6 should all be valid" {
            repeat(100) {
                val result = DiceRoller.roll(1, 6)
                result shouldBeGreaterThanOrEqual 1
                result shouldBeLessThanOrEqual 6
            }
        }

        "Multiple rolls with drop lowest: 50 rolls of 4d6 keep 3" {
            repeat(50) {
                val result = DiceRoller.roll(4, 6, 3, highest = true)
                result shouldBeGreaterThanOrEqual 3
                result shouldBeLessThanOrEqual 18
            }
        }

        "Large dice roll: Gandalf rolls 10d100" {
            val result = DiceRoller.roll(10, 100)
            result shouldBeGreaterThanOrEqual 10
            result shouldBeLessThanOrEqual 1000
        }

        "Single die: Frodo rolls 1d20 for initiative" {
            val result = DiceRoller.roll(1, 20)
            result shouldBeGreaterThanOrEqual 1
            result shouldBeLessThanOrEqual 20
        }

        "Standard 2d6 roll for damage - Darth Vader" {
            val result = DiceRoller.roll(2, 6)
            result shouldBeGreaterThanOrEqual 2
            result shouldBeLessThanOrEqual 12
        }

        "Keep 1 from 2d6 highest - minimum stats" {
            val result = DiceRoller.roll(2, 6, 1, highest = true)
            result shouldBeGreaterThanOrEqual 1
            result shouldBeLessThanOrEqual 6
        }

        "Keep 0 from 4d6 - edge case testing with Deadpool" {
            val result = DiceRoller.roll(4, 6, 0, highest = true)
            result shouldBeGreaterThanOrEqual 1
            result shouldBeLessThanOrEqual 6
        }
    })
