package de.hipp.pnp.aspect

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

/**
 * Aspect for monitoring method execution performance.
 *
 * Automatically logs methods that exceed the configured slow threshold,
 * helping identify performance bottlenecks without manual instrumentation.
 *
 * This aspect intercepts all Spring Service and RestController methods
 * to measure execution time and log warnings for slow operations.
 */
@Aspect
@Component
class PerformanceLoggingAspect {

    @Value("\${performance.slow-threshold-ms:1000}")
    private var slowThresholdMs: Long = 1000

    /**
     * Monitors all service methods for performance.
     *
     * Logs a warning if execution time exceeds the slow threshold.
     */
    @Around("@within(org.springframework.stereotype.Service)")
    fun monitorServicePerformance(joinPoint: ProceedingJoinPoint): Any? {
        return monitorExecution(joinPoint, "Service")
    }

    /**
     * Monitors all REST controller methods for performance.
     *
     * Logs a warning if execution time exceeds the slow threshold.
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun monitorControllerPerformance(joinPoint: ProceedingJoinPoint): Any? {
        return monitorExecution(joinPoint, "Controller")
    }

    /**
     * Core monitoring logic for method execution.
     *
     * @param joinPoint The method being monitored
     * @param componentType The type of component (Service, Controller, etc.)
     * @return The result of the method execution
     */
    private fun monitorExecution(joinPoint: ProceedingJoinPoint, componentType: String): Any? {
        val className = joinPoint.signature.declaringType.simpleName
        val methodName = joinPoint.signature.name
        val startTime = System.currentTimeMillis()

        return try {
            // Execute the method
            val result = joinPoint.proceed()

            // Calculate duration
            val duration = System.currentTimeMillis() - startTime

            // Log if slow
            if (duration > slowThresholdMs) {
                MDC.put("duration_ms", duration.toString())
                MDC.put("threshold_ms", slowThresholdMs.toString())

                logger.warn {
                    "SLOW $componentType METHOD: $className.$methodName took ${duration}ms " +
                    "(threshold: ${slowThresholdMs}ms)"
                }

                MDC.remove("duration_ms")
                MDC.remove("threshold_ms")
            } else {
                logger.debug {
                    "$componentType method executed: $className.$methodName took ${duration}ms"
                }
            }

            result
        } catch (e: Throwable) {
            val duration = System.currentTimeMillis() - startTime

            logger.error(e) {
                "$componentType method failed: $className.$methodName failed after ${duration}ms - ${e.message}"
            }

            throw e
        }
    }
}
