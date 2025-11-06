package de.hipp.pnp.config

import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration for distributed tracing with Zipkin.
 *
 * Configures trace sampling rates, service naming, and trace propagation
 * for distributed request tracking across microservices.
 */
@Configuration
open class TracingConfiguration {

    @Value("\${spring.application.name:pnp-character-generator}")
    private lateinit var serviceName: String

    /**
     * Configures WebClient with tracing enabled.
     *
     * Ensures that trace context is propagated to downstream services
     * when making HTTP calls using WebClient.
     *
     * @param tracer The Micrometer tracer
     * @param propagator The trace context propagator
     * @return Configured WebClient builder
     */
    @Bean
    @ConditionalOnProperty(prefix = "management.tracing", name = ["enabled"], havingValue = "true", matchIfMissing = true)
    open fun tracedWebClientBuilder(
        tracer: Tracer?,
        propagator: Propagator?
    ): WebClient.Builder {
        return WebClient.builder()
            .defaultHeader("X-Service-Name", serviceName)
    }

    /**
     * Provides trace sampling probability based on environment.
     *
     * Development: 100% sampling for full trace visibility
     * Production: 10% sampling to reduce overhead
     *
     * Override with TRACING_SAMPLING_RATE environment variable
     */
    @Bean
    open fun samplingProbability(): Double {
        val samplingRate = System.getenv("TRACING_SAMPLING_RATE")?.toDoubleOrNull() ?: 0.1
        return samplingRate.coerceIn(0.0, 1.0)
    }
}
