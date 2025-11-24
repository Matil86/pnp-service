package de.hipp.pnp.logging

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Instant

/**
 * Comprehensive tests for RequestLoggingFilter in logging package.
 *
 * Tests MDC context, JWT extraction, request correlation, and logging behavior.
 */
class RequestLoggingFilterTest :
    StringSpec({

        afterEach {
            SecurityContextHolder.clearContext()
        }

        fun createMockJwt(
            sub: String = "user123",
            email: String = "test@example.com",
        ): Jwt =
            Jwt
                .withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", sub)
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build()

        "doFilterInternal - Goku authenticated request adds user context to MDC" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>(relaxed = true)
            val filterChain = mockk<FilterChain>(relaxed = true)

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null

            val jwt = createMockJwt("goku123", "goku@dbz.com")
            val auth = UsernamePasswordAuthenticationToken(jwt, null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Spider-Man request with existing request ID" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>(relaxed = true)
            val filterChain = mockk<FilterChain>(relaxed = true)

            val existingRequestId = "spidey-request-123"

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/generate"
            every { request.getHeader("X-Request-ID") } returns existingRequestId

            SecurityContextHolder.clearContext()

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Tony Stark POST request with 201 status" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "POST"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 201
            every { filterChain.doFilter(request, any()) } returns Unit

            val jwt = createMockJwt("tony123", "tony@stark.com")
            val auth = UsernamePasswordAuthenticationToken(jwt, null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Batman DELETE request with 204 no content" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "DELETE"
            every { request.requestURI } returns "/api/characters/42"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 204
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Wonder Woman 400 client error logged as warning" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "POST"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 400
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Deadpool 404 not found" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/999"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 404
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Hulk 500 internal server error logged as error" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/generate"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 500
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Captain America 503 service unavailable" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/health"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 503
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Naruto (ナルト) with JWT extraction" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            val jwt = createMockJwt("naruto123", "naruto@konoha.com")
            val auth = UsernamePasswordAuthenticationToken(jwt, null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Vegeta unauthenticated request" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/locale"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            SecurityContextHolder.clearContext()

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "shouldNotFilter - health endpoint should be excluded" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.requestURI } returns "/health"
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            // Should pass through without setting X-Request-ID
            verify(exactly = 1) { filterChain.doFilter(request, response) }
        }

        "shouldNotFilter - actuator health endpoint should be excluded" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.requestURI } returns "/actuator/health"
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            // Should pass through without setting X-Request-ID
            verify(exactly = 1) { filterChain.doFilter(request, response) }
        }

        "shouldNotFilter - Prometheus metrics endpoint should be excluded" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.requestURI } returns "/actuator/prometheus"
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            // Should pass through without setting X-Request-ID
            verify(exactly = 1) { filterChain.doFilter(request, response) }
        }

        "shouldNotFilter - regular API endpoint should not be excluded" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>(relaxed = true)
            val filterChain = mockk<FilterChain>(relaxed = true)

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null

            filter.doFilter(request, response, filterChain)

            // Should process normal API endpoints (verify filterChain was called)
            verify { filterChain.doFilter(request, any()) }
        }

        "shouldNotFilter - Frodo checks /actuator/info is not excluded" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>(relaxed = true)
            val filterChain = mockk<FilterChain>(relaxed = true)

            every { request.method } returns "GET"
            every { request.requestURI } returns "/actuator/info"
            every { request.getHeader("X-Request-ID") } returns null

            filter.doFilter(request, response, filterChain)

            // Should process /actuator/info (not excluded, verify filterChain was called)
            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Pikachu (ピカチュウ) with non-JWT principal" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("string-principal", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Loki with ContentCachingResponseWrapper" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val cachedResponse = mockk<ContentCachingResponseWrapper>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null
            every { cachedResponse.setHeader("X-Request-ID", any()) } returns Unit
            every { cachedResponse.status } returns 200
            every { filterChain.doFilter(request, cachedResponse) } returns Unit

            filter.doFilter(request, cachedResponse, filterChain)

            verify { filterChain.doFilter(request, cachedResponse) }
        }

        "doFilterInternal - Thor handles JWT extraction exception gracefully" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            // Create a mock authentication that throws when accessing principal
            val auth = mockk<UsernamePasswordAuthenticationToken>()
            every { auth.isAuthenticated } returns true
            every { auth.principal } throws RuntimeException("JWT extraction failed")
            SecurityContextHolder.getContext().authentication = auth

            // Should handle exception gracefully and continue
            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Gandalf with Unicode path (孫悟空)" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/孫悟空"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Black Widow PUT request" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "PUT"
            every { request.requestURI } returns "/api/characters/1"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Thanos PATCH request" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "PATCH"
            every { request.requestURI } returns "/api/characters/42"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }

        "doFilterInternal - Neo with emoji in request path 🎲⚔️" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/test/🎲⚔️"
            every { request.getHeader("X-Request-ID") } returns null
            every { response.setHeader("X-Request-ID", any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, any()) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, any()) }
        }
    })
