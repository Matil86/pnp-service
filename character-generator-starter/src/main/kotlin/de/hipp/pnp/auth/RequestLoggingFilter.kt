package de.hipp.pnp.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter to log all incoming requests and their authentication status.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = SecurityContextHolder.getContext().getAuthentication()
        val isAuthenticated = auth != null && auth.isAuthenticated

        // Get query string if present
        val queryString = if (request.queryString != null) "?${request.queryString}" else ""
        
        // Execute the rest of the filter chain
        filterChain.doFilter(request, response)

        // Log response status after the request is processed
        log.info(
            "Response: {} {}{} - Status: {} - Authenticated: {} - User: {} - Remote IP: {}",
            request.method,
            request.requestURI,
            queryString,
            response.status,
            isAuthenticated,
            if (isAuthenticated) auth?.name else "anonymous",
            request.remoteAddr
        )
    }
}
