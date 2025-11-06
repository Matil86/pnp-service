package de.hipp.pnp.base.fivee

import kotlin.random.Random

/**
 * Utility object for rolling dice with various configurations.
 *
 * Provides methods to roll standard dice and to roll multiple dice while keeping only
 * the highest or lowest results.
 */
object DiceRoller {

    private val random = Random.Default

    /**
     * Rolls a specified number of dice with a given number of sides.
     *
     * @param numberOfDice The number of dice to roll
     * @param diceSides The number of sides on each die
     * @return The sum of all dice rolls
     */
    fun roll(numberOfDice: Int, diceSides: Int): Int {
        // Handle edge cases for invalid dice configurations
        if (diceSides <= 0 || numberOfDice <= 0) {
            return 0
        }

        var returnValue = 0
        val min = 1

        for (i in 0 until numberOfDice) {
            returnValue += random.nextInt(min, diceSides + 1)
        }

        return returnValue
    }

    /**
     * Rolls multiple dice and keeps only a specified number of the highest or lowest results.
     *
     * This method is commonly used in tabletop RPGs for attribute generation (e.g., "4d6 keep 3 highest").
     *
     * @param numberOfDice The total number of dice to roll
     * @param diceSides The number of sides on each die
     * @param keep The number of dice results to keep (must be less than numberOfDice)
     * @param highest If true, keeps the highest results; if false, keeps the lowest results
     * @return The sum of the kept dice results
     */
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

        // If keep is 0 or negative, default to keeping 1 die
        val actualKeep = if (keep <= 0) 1 else keep.coerceAtMost(returnList.size)

        var returnValue = 0

        for (j in 0 until actualKeep) {
            returnValue += returnList[j]
        }

        return returnValue
    }
}