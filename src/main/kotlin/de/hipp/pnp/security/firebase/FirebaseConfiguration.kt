package de.hipp.pnp.security.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

@Configuration
class FirebaseConfiguration {

    private val log = KotlinLogging.logger {}

    @Bean
    fun firebaseApp(): FirebaseApp {
        val credentials = getCredentials()
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        return FirebaseApp.initializeApp(options)
    }

    private fun getCredentials(): GoogleCredentials {
        val credentialsSteam = getCredentialsStream() ?: return useGoogleDefaultAuth()
        var credentials = GoogleCredentials.fromStream(credentialsSteam)
        val serviceAccount = credentials as ServiceAccountCredentials
        log.info { "Using Firebase Credentials Service Account: ${serviceAccount.clientEmail.substringBefore('@')}" }
        return credentials

    }

    private fun useGoogleDefaultAuth(): GoogleCredentials {
        try {
            log.info { "Using Google Default Firebase Auth" }
            return GoogleCredentials.getApplicationDefault()
        } catch (e: Exception) {
            throw RuntimeException(
                "No valid credentials found. Please set GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable or ensure service account is properly configured.",
                e
            )
        }
    }

    private fun getCredentialsStream(): InputStream? {
        val credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON")
        if (!credentialsJson.isNullOrEmpty()) return credentialsJson.byteInputStream()
        val credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
        if (File(credentialsPath).exists()) return FileInputStream(credentialsPath)
        return null
    }

    @Bean
    fun fireStore(): Firestore =
        FirestoreOptions.getDefaultInstance().toBuilder()
            .build()
            .service
}