package de.hipp.pnp.ui.clients


class LoginService : BaseHttpService(), LoginServiceInterface {
    private val client: GoogleLoginClient = getClient<GoogleLoginClient>()
    override fun login(onSuccess: () -> Unit, onError: (Error) -> Unit) {

        println("Login successful")
        val response = client.loginWithGoogle().execute()
        if (response.isSuccessful) {
            onSuccess()
        } else {
            onError(Error("Login failed"))
        }
    }

    override fun logout(onSuccess: () -> Unit, onError: (Error) -> Unit) {
        println("Login successful")
        val response = client.loginWithGoogle().execute()
        if (response.isSuccessful) {
            onSuccess()
        } else {
            onError(Error("Login failed"))
        }
    }


}
