package de.hipp.pnp.security

enum class Role(private val value: String) {
    USER("USER"),
    ADMIN("ADMIN");


    override fun toString(): String {
        return this.value
    }
}
