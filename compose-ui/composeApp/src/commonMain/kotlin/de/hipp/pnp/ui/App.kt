package de.hipp.pnp.ui

import androidx.compose.runtime.Composable
import de.hipp.pnp.ui.clients.LoginServiceInterface
import de.hipp.pnp.ui.mainview.MainViewModel
import de.hipp.pnp.ui.mainview.mainView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(loginService: LoginServiceInterface) {
    mainView(loginService = loginService, mainViewModel = MainViewModel())
}