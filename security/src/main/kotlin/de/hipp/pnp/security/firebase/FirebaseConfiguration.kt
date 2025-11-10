package de.hipp.pnp.security.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.IOException

/**
 * Firebase configuration for secure credential management.
 *
 * Supports multiple authentication methods (in order of precedence):
 * 1. FIREBASE_CREDENTIALS environment variable (JSON string)
 * 2. GOOGLE_APPLICATION_CREDENTIALS environment variable (file path)
 * 3. Application Default Credentials (for GCP environments)
 * 4. Fallback to local file for development (not recommended for production)
 */
@Configuration
class FirebaseConfiguration {
    private val log = KotlinLogging.logger {}

    @Bean
    fun firebaseApp(): FirebaseApp {
        // Check if Firebase is already initialized
        if (FirebaseApp.getApps().isNotEmpty()) {
            log.info { "Firebase already initialized, using existing instance" }
            return FirebaseApp.getInstance()
        }

        val credentials = loadCredentials()

        val options =
            FirebaseOptions
                .builder()
                .setCredentials(credentials)
                .build()

        log.info { "Initializing Firebase with secure credentials" }
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun fireStore(): Firestore {
        val credentials = loadCredentials()

        return FirestoreOptions
            .getDefaultInstance()
            .toBuilder()
            .setCredentials(credentials)
            .build()
            .service
    }

    /**
     * Loads Firebase credentials from various sources with security best practices.
     *
     * Priority order:
     * 1. FIREBASE_CREDENTIALS env var (JSON string) - recommended for containers/cloud
     * 2. GOOGLE_APPLICATION_CREDENTIALS env var (file path) - standard Google convention
     * 3. Application Default Credentials - for GCP environments
     * 4. Local file fallback - for development only
     */
    private fun loadCredentials(): GoogleCredentials {
        // Method 1: Load from FIREBASE_CREDENTIALS environment variable (JSON string)
        val firebaseCredsJson = System.getenv("FIREBASE_CREDENTIALS")
        if (!firebaseCredsJson.isNullOrBlank()) {
            try {
                log.info { "Loading Firebase credentials from FIREBASE_CREDENTIALS environment variable" }
                return GoogleCredentials.fromStream(ByteArrayInputStream(firebaseCredsJson.toByteArray()))
            } catch (e: IOException) {
                log.error(e) { "Failed to load credentials from FIREBASE_CREDENTIALS environment variable" }
                throw IllegalStateException("Invalid Firebase credentials in FIREBASE_CREDENTIALS", e)
            }
        }

        // Method 2: Load from GOOGLE_APPLICATION_CREDENTIALS file path
        val credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
        if (!credentialsPath.isNullOrBlank()) {
            try {
                log.info { "Loading Firebase credentials from GOOGLE_APPLICATION_CREDENTIALS: $credentialsPath" }
                return GoogleCredentials.fromStream(FileInputStream(credentialsPath))
            } catch (e: IOException) {
                log.error(e) { "Failed to load credentials from file: $credentialsPath" }
                throw IllegalStateException("Failed to load Firebase credentials from $credentialsPath", e)
            }
        }

        // Method 3: Try Application Default Credentials (works in GCP environments)
        try {
            log.info { "Attempting to use Application Default Credentials" }
            return GoogleCredentials.getApplicationDefault()
        } catch (e: IOException) {
            log.warn { "Application Default Credentials not available, falling back to local file" }
        }

        // Method 4: Fallback to local development file (NOT RECOMMENDED for production)
        val localFilePath = "character-generator-service-private.json"
        try {
            log.warn { "SECURITY WARNING: Using local credentials file for development. DO NOT USE IN PRODUCTION!" }
            log.warn { "Please set FIREBASE_CREDENTIALS or GOOGLE_APPLICATION_CREDENTIALS environment variable" }
            return GoogleCredentials.fromStream(FileInputStream(localFilePath))
        } catch (e: IOException) {
            log.error(e) { "Failed to load Firebase credentials from all sources" }
            throw IllegalStateException(
                "Firebase credentials not found. Please set FIREBASE_CREDENTIALS or GOOGLE_APPLICATION_CREDENTIALS environment variable",
                e,
            )
        }
    }
}
