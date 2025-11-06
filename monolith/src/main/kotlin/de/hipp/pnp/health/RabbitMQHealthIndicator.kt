package de.hipp.pnp.health

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

/**
 * Custom health indicator for RabbitMQ connectivity.
 *
 * Verifies that RabbitMQ connection is active and healthy.
 * This is critical for asynchronous messaging and event processing.
 */
@Component
class RabbitMQHealthIndicator(
    private val connectionFactory: ConnectionFactory
) : HealthIndicator {

    override fun health(): Health {
        return try {
            // Attempt to create a connection to verify connectivity
            val connection = connectionFactory.createConnection()

            if (connection.isOpen) {
                val delegate = connection.delegate
                val channelCount = delegate?.channelMax ?: 0

                logger.debug { "RabbitMQ health check: OK - Connection is open" }

                val builder = Health.up()
                    .withDetail("status", "RabbitMQ connection is open")
                    .withDetail("service", "rabbitmq")
                    .withDetail("maxChannels", channelCount)

                // Add host and port details if delegate is available
                if (delegate != null) {
                    builder.withDetail("host", delegate.address?.hostName ?: "unknown")
                        .withDetail("port", delegate.port)
                }

                builder.build()
            } else {
                logger.warn { "RabbitMQ health check: Connection is closed" }
                Health.down()
                    .withDetail("status", "RabbitMQ connection is closed")
                    .withDetail("service", "rabbitmq")
                    .build()
            }
        } catch (e: Exception) {
            logger.error(e) { "RabbitMQ health check: Failed - ${e.message}" }
            Health.down()
                .withDetail("status", "RabbitMQ connection failed")
                .withDetail("error", e.message ?: "Unknown error")
                .withDetail("errorType", e.javaClass.simpleName)
                .withDetail("service", "rabbitmq")
                .build()
        }
    }
}
