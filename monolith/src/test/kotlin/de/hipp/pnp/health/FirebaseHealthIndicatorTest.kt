package de.hipp.pnp.health

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.springframework.boot.actuate.health.Status

/**
 * Tests for FirebaseHealthIndicator.
 *
 * Verifies Firebase connectivity checks and health status reporting.
 */
class FirebaseHealthIndicatorTest :
    StringSpec({

        afterEach {
            unmockkAll()
        }

        "health - Goku when Firebase is initialized successfully" {
            mockkStatic(FirebaseAuth::class)
            val mockFirebaseAuth = mockk<FirebaseAuth>()
            every { FirebaseAuth.getInstance() } returns mockFirebaseAuth

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.UP
            health.details["status"] shouldBe "Firebase Admin SDK initialized"
            health.details["service"] shouldBe "firebase-auth"
        }

        "health - Spider-Man when Firebase Auth instance is null" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } returns null

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "Firebase Auth instance is null"
            health.details["service"] shouldBe "firebase-auth"
        }

        "health - Tony Stark when Firebase not initialized (IllegalStateException)" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws IllegalStateException("Firebase not initialized")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "Firebase not initialized"
            health.details["error"] shouldBe "Firebase not initialized"
            health.details["service"] shouldBe "firebase-auth"
        }

        "health - Batman when Firebase throws generic exception" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws RuntimeException("Firebase connection failed")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "Firebase health check failed"
            health.details["error"] shouldBe "Firebase connection failed"
            health.details["service"] shouldBe "firebase-auth"
        }

        "health - Wonder Woman verifies all health details present when UP" {
            mockkStatic(FirebaseAuth::class)
            val mockFirebaseAuth = mockk<FirebaseAuth>()
            every { FirebaseAuth.getInstance() } returns mockFirebaseAuth

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldNotBe null
            health.details shouldNotBe null
            health.details.size shouldBe 2
        }

        "health - Naruto (ナルト) when Firebase throws NullPointerException" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws NullPointerException("Null reference")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Null reference"
        }

        "health - Vegeta when Firebase throws security exception" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws SecurityException("Security violation")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "Firebase health check failed"
        }

        "health - Deadpool when Firebase initialized but connection slow" {
            mockkStatic(FirebaseAuth::class)
            val mockFirebaseAuth = mockk<FirebaseAuth>()
            every { FirebaseAuth.getInstance() } answers {
                Thread.sleep(100)
                mockFirebaseAuth
            }

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.UP
        }

        "health - Hulk when Firebase throws IOException (wrapped as exception)" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws Exception("IO error connecting to Firebase")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "IO error connecting to Firebase"
        }

        "health - Pikachu (ピカチュウ) when Firebase initialized multiple checks" {
            mockkStatic(FirebaseAuth::class)
            val mockFirebaseAuth = mockk<FirebaseAuth>()
            every { FirebaseAuth.getInstance() } returns mockFirebaseAuth

            val indicator = FirebaseHealthIndicator()

            val health1 = indicator.health()
            val health2 = indicator.health()

            health1.status shouldBe Status.UP
            health2.status shouldBe Status.UP
        }

        "health - Gandalf when IllegalStateException with empty message" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws IllegalStateException()

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "Firebase not initialized"
        }

        "health - Frodo when exception with null message" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws RuntimeException(null as String?)

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Unknown error"
        }

        "health - Neo when Firebase fails with authentication error" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws IllegalStateException("Authentication credentials invalid")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Authentication credentials invalid"
        }

        "health - Loki when Firebase instance creation fails" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws Exception("Failed to create Firebase instance")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
        }

        "health - Thor when Firebase throws timeout exception" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws Exception("Connection timeout")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Connection timeout"
        }

        "health - Captain America when Firebase successful with specific details" {
            mockkStatic(FirebaseAuth::class)
            val mockFirebaseAuth = mockk<FirebaseAuth>()
            every { FirebaseAuth.getInstance() } returns mockFirebaseAuth

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.details["service"] shouldBe "firebase-auth"
            health.details["status"] shouldBe "Firebase Admin SDK initialized"
        }

        "health - Black Widow when Firebase throws ClassNotFoundException" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws Exception("Firebase class not found")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "Firebase health check failed"
        }

        "health - Thanos when Firebase initialization fails with network error" {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance() } throws Exception("Network unreachable")

            val indicator = FirebaseHealthIndicator()
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Network unreachable"
        }
    })
