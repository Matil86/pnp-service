package de.hipp.pnp.base.fivee

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import kotlin.math.min

/**
 * Represents a 5th Edition attribute with value, maximum, and modifier.
 *
 * @property baseValue The base value of the attribute (default: 0)
 * @property value The current value of the attribute
 * @property max The maximum value the attribute can reach (default: 20)
 * @property modifier The calculated modifier based on the attribute value
 */
data class Attribute5e(
    @JsonIgnore var baseValue: Int = 0,
) : Serializable {
    var value: Int = baseValue

    @JvmField
    var max: Int = 20
    var modifier: Int = 0

    /**
     * Modifies the attribute value and recalculates the modifier.
     * The value is capped at the maximum allowed value.
     *
     * @param value The amount to add to the current value
     */
    fun modifyValue(value: Int) {
        this.value = min(max, this.value + value)
        this.modifier = (this.value - 10).floorDiv(2)
    }
}
