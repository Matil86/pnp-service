package de.hipp.pnp.ui.clients

import retrofit2.Call
import retrofit2.http.POST

interface GoogleLoginClient {
    @POST("login")
    fun login(): Call<String>

    @POST("logout")
    fun logout(): Call<String>
}
