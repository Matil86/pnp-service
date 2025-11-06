package de.hipp.pnp.api.fivee.abstracts


abstract class BaseCharacterClass {
    open var name: String = ""
    open var level: Int? = 1

    fun increaseLevel(amount: Int) {
        this.level = (if (this.level == null) amount else this.level!! + amount)
    }
}
