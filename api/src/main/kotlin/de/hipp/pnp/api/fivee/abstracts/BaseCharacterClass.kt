package de.hipp.pnp.api.fivee.abstracts

abstract class BaseCharacterClass {
    open var name: String = ""
    open var level: Int? = 1

    fun increaseLevel(amount: Int) {
        this.level = (this.level?.plus(amount)) ?: amount
    }
}
