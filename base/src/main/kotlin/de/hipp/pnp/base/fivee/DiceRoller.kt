package de.hipp.pnp.base.fivee

import kotlin.random.Random

object DiceRoller {
    
    private val random = Random.Default

    fun roll(numberOfDice: Int, diceSides: Int): Int {
        var returnValue = 0
        val min = 1

        for (i in 0 until numberOfDice) {
            returnValue += random.nextInt(min, diceSides + 1)
        }

        return returnValue
    }

    fun roll(numberOfDice: Int, diceSides: Int, keep: Int, highest: Boolean): Int {
        val returnList = mutableListOf<Int>()
        for (i in 0 until numberOfDice) {
            returnList.add(roll(1, diceSides))
        }
        
        if (highest) {
            returnList.sort()
        } else {
            returnList.sortDescending()
        }

        var returnValue = 0

        for (j in 0..keep) {
            returnValue += returnList[j]
        }

        return returnValue
    }
}