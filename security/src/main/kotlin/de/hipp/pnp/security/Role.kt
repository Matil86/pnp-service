package de.hipp.pnp.security

enum class Role(
    private val value: String,
) {
    ADMIN("ADMIN"),
    ANONYMOUS("ANONYMOUS"),
    USER("USER"),
    ;

    override fun toString(): String = this.value
}
