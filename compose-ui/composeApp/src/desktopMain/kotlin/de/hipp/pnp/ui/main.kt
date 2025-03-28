package de.hipp.pnp.ui

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.hipp.pnp.ui.clients.LoginService

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Character Generator Client",
    ) {
        App(LoginService())
    }
}