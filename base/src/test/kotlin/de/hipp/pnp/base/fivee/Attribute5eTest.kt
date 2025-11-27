package de.hipp.pnp.base.fivee

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Attribute5e class covering modifier calculations and value management
 * D&D 5e attributes range from 1-20 with modifiers calculated as (value - 10) / 2
 */
class Attribute5eTest :
    StringSpec({

        "Create attribute with base value 10 - modifier should be 0" {
            val attr = Attribute5e(10)
            attr.value shouldBe 10
            attr.baseValue shouldBe 10
            attr.modifier shouldBe 0
        }

        "Create attribute with base value 8 - modifier should be -1" {
            val attr = Attribute5e(8)
            attr.modifyValue(0)
            attr.value shouldBe 8
            attr.modifier shouldBe -1
        }

        "Goku's strength: base 15, +3 racial bonus = 18, modifier +4" {
            val attr = Attribute5e(15)
            attr.modifyValue(3)
            attr.value shouldBe 18
            attr.modifier shouldBe 4
        }

        "Tony Stark's intelligence: base 18, +2 enhancement = 20 (max), modifier +5" {
            val attr = Attribute5e(18)
            attr.modifyValue(2)
            attr.value shouldBe 20
            attr.modifier shouldBe 5
        }

        "Attribute should cap at max value (20 by default)" {
            val attr = Attribute5e(18)
            attr.modifyValue(5) // Would be 23, but caps at 20
            attr.value shouldBe 20
            attr.modifier shouldBe 5
        }

        "Hermione's custom max: base 16, max 25, +8 enhancement = 24" {
            val attr = Attribute5e(16)
            attr.max = 25
            attr.modifyValue(8)
            attr.value shouldBe 24
            attr.modifier shouldBe 7
        }

        "Neo's dexterity: base 20, +0 modifier = 20, modifier +5" {
            val attr = Attribute5e(20)
            attr.modifyValue(0)
            attr.value shouldBe 20
            attr.modifier shouldBe 5
        }

        "Low strength: base 3, -0 = 3, modifier -4" {
            val attr = Attribute5e(3)
            attr.modifyValue(0)
            attr.value shouldBe 3
            attr.modifier shouldBe -4
        }

        "Negative modifier: base 6, modifier should be -2" {
            val attr = Attribute5e(6)
            attr.modifyValue(0)
            attr.value shouldBe 6
            attr.modifier shouldBe -2
        }

        "Spider-Man agility: base 14, +4 = 18, modifier +4" {
            val attr = Attribute5e(14)
            attr.modifyValue(4)
            attr.value shouldBe 18
            attr.modifier shouldBe 4
        }

        "Wonder Woman's strength: base 16, +2 = 18, modifier +4" {
            val attr = Attribute5e(16)
            attr.modifyValue(2)
            attr.value shouldBe 18
            attr.modifier shouldBe 4
        }

        "Zero base value with +10 = 10, modifier 0" {
            val attr = Attribute5e(0)
            attr.modifyValue(10)
            attr.value shouldBe 10
            attr.modifier shouldBe 0
        }

        "Maximum value 20: modifier should be +5" {
            val attr = Attribute5e(20)
            attr.modifyValue(0)
            attr.value shouldBe 20
            attr.modifier shouldBe 5
        }

        "Odd value 11: modifier should be +0 (floor division)" {
            val attr = Attribute5e(11)
            attr.modifyValue(0)
            attr.value shouldBe 11
            attr.modifier shouldBe 0
        }

        "Odd value 13: modifier should be +1" {
            val attr = Attribute5e(13)
            attr.modifyValue(0)
            attr.value shouldBe 13
            attr.modifier shouldBe 1
        }

        "Odd value 9: modifier should be -1 (floor division)" {
            val attr = Attribute5e(9)
            attr.modifyValue(0)
            attr.value shouldBe 9
            attr.modifier shouldBe -1
        }

        "Odd value 7: modifier should be -2" {
            val attr = Attribute5e(7)
            attr.modifyValue(0)
            attr.value shouldBe 7
            attr.modifier shouldBe -2
        }

        "Batman's intelligence with penalty: base 18, -2 = 16, modifier +3" {
            val attr = Attribute5e(18)
            attr.modifyValue(-2)
            attr.value shouldBe 16
            attr.modifier shouldBe 3
        }

        "Captain America: base 17, +1 = 18, modifier +4" {
            val attr = Attribute5e(17)
            attr.modifyValue(1)
            attr.value shouldBe 18
            attr.modifier shouldBe 4
        }

        "Gandalf's wisdom: base 19, +1 = 20, modifier +5" {
            val attr = Attribute5e(19)
            attr.modifyValue(1)
            attr.value shouldBe 20
            attr.modifier shouldBe 5
        }

        "Custom max 30: base 20, max 30, +10 = 30, modifier +10" {
            val attr = Attribute5e(20)
            attr.max = 30
            attr.modifyValue(10)
            attr.value shouldBe 30
            attr.modifier shouldBe 10
        }
    })
