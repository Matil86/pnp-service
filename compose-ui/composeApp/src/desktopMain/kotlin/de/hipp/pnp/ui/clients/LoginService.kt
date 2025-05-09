package de.hipp.pnp.ui.clients


class LoginService : BaseHttpService(), LoginServiceInterface {
    private val client: GoogleLoginClient = getClient<GoogleLoginClient>()
    override fun login(onSuccess: () -> Unit, onError: (Error) -> Unit) {

        val response = client.login().execute()
        if (response.isSuccessful) {
            println("Login successful")
            onSuccess()
        } else {
            onError(Error("Login failed"))
        }
    }

    override fun logout(onSuccess: () -> Unit, onError: (Error) -> Unit) {
        println("Login successful")
        val response = client.logout().execute()
        if (response.isSuccessful) {
            onSuccess()
        } else {
            onError(Error("Login failed"))
        }
    }


}
