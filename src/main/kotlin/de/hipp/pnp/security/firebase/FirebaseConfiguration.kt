package de.hipp.pnp.security.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream

@Configuration
class FirebaseConfiguration {

    @Bean
    fun firebaseApp(): FirebaseApp {
        val credentials = getCredentials()
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        return FirebaseApp.initializeApp(options)
    }

    private fun getCredentials(): GoogleCredentials {
        // Try environment variable first (Cloud Run preferred method)
        val credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON")
        if (!credentialsJson.isNullOrEmpty()) {
            return GoogleCredentials.fromStream(credentialsJson.byteInputStream())
        }

        // Try standard GOOGLE_APPLICATION_CREDENTIALS environment variable
        val credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
        if (!credentialsPath.isNullOrEmpty() && File(credentialsPath).exists()) {
            return GoogleCredentials.fromStream(FileInputStream(credentialsPath))
        }

        // Try default service account in Cloud Run as last resort
        try {
            return GoogleCredentials.getApplicationDefault()
        } catch (e: Exception) {
            throw RuntimeException("No valid credentials found. Please set GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable or ensure service account is properly configured.", e)
        }
    }

    @Bean
    fun fireStore(): Firestore =
        FirestoreOptions.getDefaultInstance().toBuilder()
            .build()
            .service
}