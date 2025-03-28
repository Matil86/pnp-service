package de.hipp.pnp.ui.clients

import retrofit2.Call
import retrofit2.http.GET

interface GoogleLoginClient {
    @GET("oauth2/authorization/google")
    fun loginWithGoogle(): Call<String>
}
