package de.hipp.pnp.health

import com.google.firebase.auth.FirebaseAuth
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

/**
 * Custom health indicator for Firebase connectivity and authentication.
 *
 * Verifies that Firebase Admin SDK is properly initialized and accessible.
 * This is critical for JWT token validation and user authentication.
 */
@Component
class FirebaseHealthIndicator : HealthIndicator {
    override fun health(): Health =
        try {
            // Attempt to get Firebase Auth instance
            val firebaseAuth = FirebaseAuth.getInstance()

            if (firebaseAuth != null) {
                // Firebase is initialized and accessible
                logger.debug { "Firebase health check: OK" }
                Health
                    .up()
                    .withDetail("status", "Firebase Admin SDK initialized")
                    .withDetail("service", "firebase-auth")
                    .build()
            } else {
                logger.warn { "Firebase health check: Firebase Auth instance is null" }
                Health
                    .down()
                    .withDetail("status", "Firebase Auth instance is null")
                    .withDetail("service", "firebase-auth")
                    .build()
            }
        } catch (e: IllegalStateException) {
            logger.error(e) { "Firebase health check: Not initialized - ${e.message}" }
            Health
                .down()
                .withDetail("status", "Firebase not initialized")
                .withDetail("error", e.message ?: "Unknown error")
                .withDetail("service", "firebase-auth")
                .build()
        } catch (e: Exception) {
            logger.error(e) { "Firebase health check: Failed - ${e.message}" }
            Health
                .down()
                .withDetail("status", "Firebase health check failed")
                .withDetail("error", e.message ?: "Unknown error")
                .withDetail("service", "firebase-auth")
                .build()
        }
}
