package de.hipp.pnp.ui.clients

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create


abstract class BaseHttpService {
    private val logger = KotlinLogging.logger {}
    inline fun <reified clientClass : Any> getClient(): clientClass {
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl("http://localhost:8080")
            .build()
            .create<clientClass>()
    }

    fun getOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(getHttpLoggingInterceptor()).build()

    private fun getHttpLoggingInterceptor(): Interceptor {
        val loggerAdapter = HttpLoggingInterceptor.Logger { logger.info { it } }
        return HttpLoggingInterceptor(loggerAdapter).setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
