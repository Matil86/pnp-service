package de.hipp.pnp.ui.mainview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import de.hipp.pnp.ui.clients.LoginServiceInterface

@Composable
fun mainView(
    mainViewModel: MainViewModel = MainViewModel(),
    loginService: LoginServiceInterface,
) {
    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {


            val buttonText: String
            val onLoginClick: () -> Unit

            if (mainViewModel.userIsAuthenticated) {
                buttonText = "Logout"
                onLoginClick = {
                    loginService.logout(
                        onSuccess = {
                            mainViewModel.showContent = false
                            mainViewModel.userIsAuthenticated = false
                        },
                        onError = { error -> println("Error: $error") }
                    )
                }
            } else {
                buttonText = "Login"
                onLoginClick = {
                    loginService.login(
                        onSuccess = {
                            mainViewModel.showContent = true
                            mainViewModel.userIsAuthenticated = true
                        },
                        onError = { error -> println("Error: $error") }
                    )
                }
            }

            Button(onClick = onLoginClick) {
                Text(buttonText)
            }
        }
    }
}
