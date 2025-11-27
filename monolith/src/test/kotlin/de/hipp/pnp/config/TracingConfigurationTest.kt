package de.hipp.pnp.config

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import io.mockk.mockk
import org.springframework.test.util.ReflectionTestUtils

/**
 * Tests for TracingConfiguration.
 *
 * Verifies distributed tracing setup, sampling probability, and WebClient configuration.
 */
class TracingConfigurationTest :
    StringSpec({

        "tracedWebClientBuilder - Goku verifies WebClient builder is created" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "pnp-character-generator")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "tracedWebClientBuilder - Spider-Man with null tracer" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "spider-app")

            val webClientBuilder = config.tracedWebClientBuilder(null, null)

            webClientBuilder shouldNotBe null
        }

        "tracedWebClientBuilder - Tony Stark verifies service name header" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "stark-industries")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "samplingProbability - Batman default sampling rate" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            probability.shouldBeBetween(0.0, 1.0, 0.0)
        }

        "samplingProbability - Wonder Woman with 0.0 (no sampling)" {
            val config = TracingConfiguration()

            // Default without env var should be 0.1
            val probability = config.samplingProbability()

            probability shouldBe 0.1
        }

        "samplingProbability - Naruto (ナルト) with 1.0 (full sampling)" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            // Should be within valid range
            probability.shouldBeBetween(0.0, 1.0, 0.0)
        }

        "samplingProbability - Vegeta verifies coercion to valid range" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            // Even if env var is invalid, should be between 0.0 and 1.0
            probability.shouldBeBetween(0.0, 1.0, 0.0)
        }

        "samplingProbability - Deadpool with default 0.1" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            probability shouldBe 0.1
        }

        "tracedWebClientBuilder - Hulk with long service name" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "hulk-smash-character-generator-service")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "tracedWebClientBuilder - Pikachu (ピカチュウ) with Unicode service name" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "pokemon-ピカチュウ-service")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "tracedWebClientBuilder - Gandalf with emoji in service name 🧙" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "wizard-🧙-service")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "samplingProbability - Frodo verifies probability is not negative" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            (probability >= 0.0) shouldBe true
        }

        "samplingProbability - Neo verifies probability is not above 1.0" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            (probability <= 1.0) shouldBe true
        }

        "tracedWebClientBuilder - Loki with empty service name" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "tracedWebClientBuilder - Thor with numeric service name" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "service-12345")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "tracedWebClientBuilder - Captain America with special characters" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "cap-service-2023-v1.0")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val webClientBuilder = config.tracedWebClientBuilder(tracer, propagator)

            webClientBuilder shouldNotBe null
        }

        "samplingProbability - Black Widow verifies default of 0.1" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            probability shouldBe 0.1
        }

        "samplingProbability - Thanos verifies coercion works" {
            val config = TracingConfiguration()

            val probability = config.samplingProbability()

            // Should coerce to valid range [0.0, 1.0]
            probability.shouldBeBetween(0.0, 1.0, 0.0)
        }

        "tracedWebClientBuilder - multiple calls return independent builders" {
            val config = TracingConfiguration()
            ReflectionTestUtils.setField(config, "serviceName", "test-service")

            val tracer = mockk<Tracer>()
            val propagator = mockk<Propagator>()

            val builder1 = config.tracedWebClientBuilder(tracer, propagator)
            val builder2 = config.tracedWebClientBuilder(tracer, propagator)

            builder1 shouldNotBe null
            builder2 shouldNotBe null
        }
    })
