package de.hipp.pnp.base.rabbitmq

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Manages the initialization order of RabbitMQ components to ensure listeners 
 * are fully ready before producers can send messages.
 */
@Component
class RabbitMQInitializationManager {
    
    private val log = KotlinLogging.logger {}
    private val initializationLatch = CountDownLatch(1)
    private var isInitialized = false
    
    /**
     * Called when the application is fully started and ready to handle requests.
     * This ensures all @RabbitListener components are initialized before allowing
     * producer operations.
     */
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        log.info { "Application ready - RabbitMQ listeners are now initialized" }
        isInitialized = true
        initializationLatch.countDown()
    }
    
    /**
     * Blocks until RabbitMQ listeners are fully initialized.
     * This should be called by producers before sending messages.
     * 
     * @param timeoutSeconds Maximum time to wait for initialization
     * @return true if initialization completed within timeout, false otherwise
     */
    fun waitForInitialization(timeoutSeconds: Long = 30): Boolean {
        if (isInitialized) {
            return true
        }
        
        log.debug { "Waiting for RabbitMQ listeners initialization..." }
        return try {
            initializationLatch.await(timeoutSeconds, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            log.warn { "Interrupted while waiting for RabbitMQ initialization" }
            false
        }
    }
    
    /**
     * Checks if RabbitMQ listeners are initialized without blocking.
     */
    fun isInitialized(): Boolean = isInitialized
}