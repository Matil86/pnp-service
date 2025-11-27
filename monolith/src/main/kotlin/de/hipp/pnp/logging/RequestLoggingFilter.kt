package de.hipp.pnp.logging

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.UUID

private val logger = KotlinLogging.logger {}

/**
 * Filter for logging all HTTP requests with structured context.
 *
 * Captures request and response details, adds correlation IDs,
 * and enriches MDC with user context for distributed tracing.
 *
 * This filter:
 * - Generates unique request IDs for request correlation
 * - Captures HTTP method, path, query parameters, status code
 * - Measures request duration
 * - Extracts user context from JWT tokens
 * - Adds structured fields to MDC for log correlation
 * - Logs all requests and responses with timing information
 */
@Component
class RequestLoggingFilter : OncePerRequestFilter() {
    companion object {
        private const val REQUEST_ID_HEADER = "X-Request-ID"
        private val EXCLUDED_PATHS = setOf("/health", "/actuator/health", "/actuator/prometheus")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // Skip logging for health check and metrics endpoints
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val startTime = System.currentTimeMillis()
        val requestId = request.getHeader(REQUEST_ID_HEADER) ?: UUID.randomUUID().toString()

        // Add request ID to MDC for all logs in this request
        MDC.put("request_id", requestId)
        MDC.put("http_method", request.method)
        MDC.put("http_path", request.requestURI)

        // Add request ID to response headers for client correlation
        response.setHeader(REQUEST_ID_HEADER, requestId)

        // Extract user context from JWT if available
        try {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication != null && authentication.isAuthenticated) {
                val jwt = authentication.principal as? Jwt
                if (jwt != null) {
                    val userId = jwt.claims["sub"] as? String
                    val email = jwt.claims["email"] as? String

                    userId?.let { MDC.put("user_id", it) }
                    email?.let { MDC.put("email", it) }
                }
            }
        } catch (e: Exception) {
            // Ignore errors in user context extraction
            logger.debug { "Failed to extract user context from JWT: ${e.message}" }
        }

        // Wrap response to capture status code
        val responseWrapper =
            if (response is ContentCachingResponseWrapper) {
                response
            } else {
                ContentCachingResponseWrapper(response)
            }

        try {
            // Process the request
            filterChain.doFilter(request, responseWrapper)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            val statusCode = responseWrapper.status

            MDC.put("http_status", statusCode.toString())
            MDC.put("duration_ms", duration.toString())

            // Log request completion with appropriate level based on status and duration
            when {
                statusCode >= 500 -> {
                    logger.error {
                        "HTTP request completed with server error: " +
                            "method=${request.method}, path=${request.requestURI}, " +
                            "status=$statusCode, duration=${duration}ms, requestId=$requestId"
                    }
                }
                statusCode >= 400 -> {
                    logger.warn {
                        "HTTP request completed with client error: " +
                            "method=${request.method}, path=${request.requestURI}, " +
                            "status=$statusCode, duration=${duration}ms, requestId=$requestId"
                    }
                }
                duration > 1000 -> {
                    logger.warn {
                        "Slow HTTP request detected: " +
                            "method=${request.method}, path=${request.requestURI}, " +
                            "status=$statusCode, duration=${duration}ms, requestId=$requestId"
                    }
                }
                else -> {
                    logger.info {
                        "HTTP request completed: " +
                            "method=${request.method}, path=${request.requestURI}, " +
                            "status=$statusCode, duration=${duration}ms, requestId=$requestId"
                    }
                }
            }

            // Clear MDC to prevent memory leaks
            MDC.clear()
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return EXCLUDED_PATHS.any { path.startsWith(it) }
    }
}
