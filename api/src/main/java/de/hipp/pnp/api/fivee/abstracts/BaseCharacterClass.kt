package de.hipp.pnp.api.fivee.abstracts


abstract class BaseCharacterClass {
    open var name: String? = null
    open var level: Int? = null

    fun increaseLevel(amount: Int) {
        this.level = (if (this.level == null) amount else this.level!! + amount)
    }
}
