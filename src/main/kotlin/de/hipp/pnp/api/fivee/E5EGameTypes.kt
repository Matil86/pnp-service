package de.hipp.pnp.api.fivee

enum class E5EGameTypes(@JvmField val value: Int) {
    GENEFUNK(0);

    companion object {
        @JvmOverloads
        fun fromValue(value: Int?, defaultValue: E5EGameTypes? = null): E5EGameTypes? {
            for (i in entries.toTypedArray().indices) {
                if (value == entries[i].value) {
                    return entries[i]
                }
            }
            return defaultValue
        }
    }
}
