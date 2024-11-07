package de.hipp.pnp.base.fivee

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import kotlin.math.min

data class Attribute5e(@JsonIgnore var baseValue: Int = 0) : Serializable {

    var value: Int = 0

    @JvmField
    var max: Int = 20
    var modifier: Int = 0

    fun modifyValue(value: Int) {
        this.value = min(max.toDouble(), (baseValue + value).toDouble()).toInt()
    }
}
