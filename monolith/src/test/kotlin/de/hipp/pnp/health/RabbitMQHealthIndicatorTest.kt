package de.hipp.pnp.health

import com.rabbitmq.client.Channel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.boot.actuate.health.Status
import java.net.InetAddress

/**
 * Tests for RabbitMQHealthIndicator.
 *
 * Verifies RabbitMQ connectivity checks and health status reporting.
 */
class RabbitMQHealthIndicatorTest :
    StringSpec({

        "health - Goku when RabbitMQ connection is open and healthy" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()
            val address = InetAddress.getByName("localhost")

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 2047
            every { delegate.address } returns address
            every { delegate.port } returns 5672

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.UP
            health.details["status"] shouldBe "RabbitMQ connection is open"
            health.details["service"] shouldBe "rabbitmq"
            health.details["maxChannels"] shouldBe 2047
            health.details["host"] shouldBe "localhost"
            health.details["port"] shouldBe 5672
        }

        "health - Spider-Man when RabbitMQ connection is closed" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns false

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "RabbitMQ connection is closed"
            health.details["service"] shouldBe "rabbitmq"
        }

        "health - Tony Stark when connection fails with exception" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception("Connection refused")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["status"] shouldBe "RabbitMQ connection failed"
            health.details["error"] shouldBe "Connection refused"
            health.details["service"] shouldBe "rabbitmq"
        }

        "health - Batman when delegate is null" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns null

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.UP
            health.details["maxChannels"] shouldBe 0
        }

        "health - Wonder Woman when connection timeout occurs" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception("Connection timeout")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Connection timeout"
        }

        "health - Naruto (ナルト) when authentication fails" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception("Authentication failed")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Authentication failed"
        }

        "health - Vegeta when network is unreachable" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception("Network unreachable")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["errorType"] shouldBe "Exception"
        }

        "health - Deadpool when connection is open but slow" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()
            val address = InetAddress.getByName("localhost")

            every { connectionFactory.createConnection() } answers {
                Thread.sleep(100)
                connection
            }
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 1024
            every { delegate.address } returns address
            every { delegate.port } returns 5672

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.UP
        }

        "health - Hulk when IOException occurs" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws java.io.IOException("IO error")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["errorType"] shouldBe "IOException"
        }

        "health - Pikachu (ピカチュウ) with custom port number" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()
            // Mock the address instead of resolving a real DNS name
            val address = mockk<InetAddress>()

            every { address.hostName } returns "rabbitmq.example.com"
            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 512
            every { delegate.address } returns address
            every { delegate.port } returns 5673

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.details["port"] shouldBe 5673
        }

        "health - Gandalf when delegate address is null" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 2047
            every { delegate.address } returns null
            every { delegate.port } returns 5672

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.UP
            health.details["host"] shouldBe "unknown"
        }

        "health - Frodo when connection has zero channels" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()
            val address = InetAddress.getByName("localhost")

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 0
            every { delegate.address } returns address
            every { delegate.port } returns 5672

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.UP
            health.details["maxChannels"] shouldBe 0
        }

        "health - Neo when SSL connection fails" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception("SSL handshake failed")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "SSL handshake failed"
        }

        "health - Loki when connection factory throws NPE" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws NullPointerException("Null connection config")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["errorType"] shouldBe "NullPointerException"
        }

        "health - Thor with maximum channels" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()
            val address = InetAddress.getByName("localhost")

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 65535
            every { delegate.address } returns address
            every { delegate.port } returns 5672

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.UP
            health.details["maxChannels"] shouldBe 65535
        }

        "health - Captain America multiple health checks" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()
            val address = InetAddress.getByName("localhost")

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 2047
            every { delegate.address } returns address
            every { delegate.port } returns 5672

            val indicator = RabbitMQHealthIndicator(connectionFactory)

            val health1 = indicator.health()
            val health2 = indicator.health()

            health1.status shouldBe Status.UP
            health2.status shouldBe Status.UP
        }

        "health - Black Widow when exception has null message" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception(null as String?)

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Unknown error"
        }

        "health - Thanos when connection refused" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws Exception("Connection refused - port 5672")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.status shouldBe Status.DOWN
            health.details["error"] shouldBe "Connection refused - port 5672"
        }

        "health - verifies all required details present when UP" {
            val connectionFactory = mockk<ConnectionFactory>()
            val connection = mockk<Connection>()
            val delegate = mockk<com.rabbitmq.client.Connection>()

            every { connectionFactory.createConnection() } returns connection
            every { connection.isOpen } returns true
            every { connection.delegate } returns delegate
            every { delegate.channelMax } returns 2047

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.details shouldNotBe null
            health.details["status"] shouldNotBe null
            health.details["service"] shouldNotBe null
        }

        "health - verifies error details present when DOWN" {
            val connectionFactory = mockk<ConnectionFactory>()

            every { connectionFactory.createConnection() } throws RuntimeException("Test error")

            val indicator = RabbitMQHealthIndicator(connectionFactory)
            val health = indicator.health()

            health.details["error"] shouldNotBe null
            health.details["errorType"] shouldNotBe null
        }
    })
