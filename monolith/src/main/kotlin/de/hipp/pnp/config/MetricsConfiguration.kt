package de.hipp.pnp.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.config.MeterFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Micrometer metrics and Prometheus integration.
 *
 * Provides custom metrics setup with common tags, metric filters, and
 * application-specific metric definitions for character generation tracking.
 */
@Configuration
open class MetricsConfiguration {

    @Value("\${spring.application.name:pnp-character-generator}")
    private lateinit var applicationName: String

    @Value("\${info.app.version:unknown}")
    private lateinit var applicationVersion: String

    /**
     * Configures common tags for all metrics.
     *
     * These tags are added to every metric emitted by the application,
     * enabling filtering and grouping in Prometheus and Grafana.
     *
     * @return MeterRegistryCustomizer with common tags configured
     */
    @Bean
    open fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                listOf(
                    Tag.of("application", applicationName),
                    Tag.of("version", applicationVersion),
                    Tag.of("environment", System.getenv("ENVIRONMENT") ?: "development")
                )
            )
        }
    }

    /**
     * Configures metric filters for fine-tuning metric collection.
     *
     * Applies distribution statistics to specific metrics for percentile
     * calculations (p50, p95, p99) which are crucial for SLO monitoring.
     *
     * @return MeterRegistryCustomizer with meter filters configured
     */
    @Bean
    open fun metricsFilters(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config()
                // Enable histogram data for HTTP server requests to calculate percentiles
                .meterFilter(
                    MeterFilter.maximumAllowableTags(
                        "http.server.requests",
                        "uri",
                        100,
                        MeterFilter.deny()
                    )
                )
        }
    }
}
