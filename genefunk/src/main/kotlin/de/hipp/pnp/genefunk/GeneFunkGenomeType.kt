package de.hipp.pnp.genefunk

import jakarta.persistence.Id

enum class GeneFunkGenomeType(
    @Id var value: Int,
) {
    UNKNOWN(-1),
    ENGINEERED(0),
    MUTTS(1),
    OPTIMIZED(2),
    TRANSHUMAN(3),
    ;

    companion object {
        fun valueOf(value: Int): GeneFunkGenomeType {
            val values = entries.toTypedArray()
            for (type in values) {
                if (type.value == value) return type
            }
            return UNKNOWN
        }
    }
}
