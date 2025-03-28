package de.hipp.pnp.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform