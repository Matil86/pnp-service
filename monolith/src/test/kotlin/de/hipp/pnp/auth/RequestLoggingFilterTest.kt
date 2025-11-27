package de.hipp.pnp.auth

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Tests for RequestLoggingFilter in auth package.
 *
 * Verifies that the filter correctly logs requests and authentication status.
 */
class RequestLoggingFilterTest :
    StringSpec({

        afterEach {
            SecurityContextHolder.clearContext()
        }

        "doFilterInternal - Goku makes authenticated request" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "127.0.0.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("goku@dbz.com", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Spider-Man makes unauthenticated request" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/health"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "192.168.1.100"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            SecurityContextHolder.clearContext()

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Tony Stark with query string parameters" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/generate"
            every { request.queryString } returns "gameType=0"
            every { request.remoteAddr } returns "10.0.0.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("tony@stark.com", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Batman with multiple query parameters" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/locale"
            every { request.queryString } returns "gameType=0&language=en_US"
            every { request.remoteAddr } returns "172.16.0.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            SecurityContextHolder.clearContext()

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Naruto POST request creates character" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "POST"
            every { request.requestURI } returns "/api/characters"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "192.168.1.50"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 201
            every { filterChain.doFilter(request, response) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("naruto@konoha.com", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Vegeta DELETE request with authentication" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "DELETE"
            every { request.requestURI } returns "/api/characters/42"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "10.0.0.42"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 204
            every { filterChain.doFilter(request, response) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("vegeta@saiyan.com", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Pikachu (ピカチュウ) request with 404 status" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/999"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "203.0.113.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 404
            every { filterChain.doFilter(request, response) } returns Unit

            SecurityContextHolder.clearContext()

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Deadpool request causes 500 error" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters/generate"
            every { request.queryString } returns "gameType=999"
            every { request.remoteAddr } returns "198.51.100.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 500
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Wonder Woman unauthorized request (401)" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/admin/users"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "192.0.2.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 401
            every { filterChain.doFilter(request, response) } returns Unit

            SecurityContextHolder.clearContext()

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Frodo forbidden request (403)" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "POST"
            every { request.requestURI } returns "/admin/settings"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "10.1.1.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 403
            every { filterChain.doFilter(request, response) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("frodo@shire.com", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Neo with IPv6 address" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/characters"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Gandalf with long query string" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            val longQueryString = "param1=value1&param2=value2&param3=value3&param4=value4"

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/search"
            every { request.queryString } returns longQueryString
            every { request.remoteAddr } returns "192.168.1.1"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Hulk PUT request with authentication" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "PUT"
            every { request.requestURI } returns "/api/characters/123"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "10.0.0.123"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            val auth = UsernamePasswordAuthenticationToken("hulk@avengers.com", null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Loki with special characters in query string" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "GET"
            every { request.requestURI } returns "/api/search"
            every { request.queryString } returns "name=Loki%20Odinson&realm=Asgard"
            every { request.remoteAddr } returns "192.168.1.66"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }

        "doFilterInternal - Thor OPTIONS request (CORS preflight)" {
            val filter = RequestLoggingFilter()
            val request = mockk<HttpServletRequest>(relaxed = true)
            val response = mockk<HttpServletResponse>()
            val filterChain = mockk<FilterChain>()

            every { request.method } returns "OPTIONS"
            every { request.requestURI } returns "/api/characters"
            every { request.queryString } returns null
            every { request.remoteAddr } returns "192.168.1.200"
            every { request.getAttribute(any()) } returns null
            every { request.setAttribute(any(), any()) } returns Unit
            every { response.status } returns 200
            every { filterChain.doFilter(request, response) } returns Unit

            filter.doFilter(request, response, filterChain)

            verify { filterChain.doFilter(request, response) }
        }
    })
