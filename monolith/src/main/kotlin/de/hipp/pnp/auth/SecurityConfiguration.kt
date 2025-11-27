package de.hipp.pnp.auth

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity
open class SecurityConfiguration(
    @param:Autowired private var userInfoProducer: UserInfoProducer,
) {
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // Add request logging filter
        http.addFilterAfter(
            RequestLoggingFilter(),
            org.springframework.security.web.authentication.AuthenticationFilter::class.java,
        )

        http {
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = jwtAuthenticationConverter()
                }
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.IF_REQUIRED
            }
            formLogin {
                disable()
            }
            httpBasic {
                disable()
            }
            authorizeHttpRequests {
                authorize("/health", permitAll)
                authorize("/error/**", permitAll)
                authorize("/resource/**", hasAnyAuthority("ADMIN", "USER"))
                authorize("/user/**", hasAnyAuthority("USER"))
                authorize("/admin/**", hasAnyAuthority("ADMIN"))
            }
            logout {
                logoutSuccessUrl = "/"
            }
            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
            cors {
                configurationSource = corsConfigurationSource()
            }
            headers {
                frameOptions { }
                xssProtection { }
                contentTypeOptions { }
                referrerPolicy {
                    policy = org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN
                }
                contentSecurityPolicy {
                    policyDirectives =
                        "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'; frame-ancestors 'none'"
                }
                httpStrictTransportSecurity {
                    includeSubDomains = true
                    maxAgeInSeconds = 31536000
                }
                permissionsPolicy {
                    policy = "geolocation=(), microphone=(), camera=(), payment=(), usb=(), magnetometer=(), gyroscope=(), accelerometer=()"
                }
            }
        }
        return http.build()
    }

    @Bean
    open fun customer(): Customer? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = auth.principal as? Jwt ?: return null
        val userId = jwt.claims["sub"] as? String ?: return null
        return userInfoProducer.getCustomerInfoFor(userId)
    }

    @Bean
    open fun webSecurityCustomizer(): WebSecurityCustomizer = WebSecurityCustomizer { web: WebSecurity -> web.debug(false) }

    @Bean
    open fun corsConfiguration(): CorsConfiguration {
        val corsConfiguration = CorsConfiguration()

        // Get allowed origins from environment variable or use defaults
        val desktopAppUrl = System.getenv("DESKTOP_APP_URL")
        val allowedOrigins = mutableListOf("http://localhost:3000", "http://localhost:8080")

        // Add desktop app URL if provided
        if (!desktopAppUrl.isNullOrBlank()) {
            allowedOrigins.add(desktopAppUrl)
            logger.info { "Added desktop app URL to CORS allowed origins: $desktopAppUrl" }
        }

        corsConfiguration.allowedOrigins = allowedOrigins
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.allowCredentials = true
        return corsConfiguration
    }

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration())
        return source
    }

    @Bean
    open fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        val jwtConverter = JwtAuthenticationConverter()
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

        // Extract roles from the JWT token
        jwtConverter.setJwtGrantedAuthoritiesConverter { jwt ->

            val authorities = grantedAuthoritiesConverter.convert(jwt)
            val userId = jwt.claims["sub"] as? String
            val email = jwt.claims["email"] as? String

            // Add to MDC for structured logging
            userId?.let { MDC.put("user_id", it) }
            email?.let { MDC.put("email", it) }

            logger.debug { "Converting JWT to authorities for userId=$userId, email=$email" }

            // Look up the user by email and add appropriate authorities
            if (userId != null) {
                try {
                    val customer = userInfoProducer.getCustomerInfoFor(userId)
                    logger.debug { "Retrieved customer info: userId=$userId, role=${customer.role}" }

                    if (customer.role != null) {
                        val userRole = customer.role ?: "USER"
                        MDC.put("action", "assign_role")
                        MDC.put("result", userRole)

                        when (userRole) {
                            "ADMIN" -> {
                                authorities?.add(SimpleGrantedAuthority("ADMIN"))
                                authorities?.add(SimpleGrantedAuthority("USER"))
                                logger.info { "Assigned ADMIN role to user: userId=$userId, email=$email" }
                            }

                            "USER" -> {
                                authorities?.add(SimpleGrantedAuthority("USER"))
                                logger.debug { "Assigned USER role to user: userId=$userId, email=$email" }
                            }
                        }

                        MDC.remove("action")
                        MDC.remove("result")
                    } else {
                        // Create a new user if not found - with security validations
                        val email = jwt.claims["email"] as? String ?: ""
                        val givenName = jwt.claims["given_name"] as? String ?: ""
                        val familyName = jwt.claims["family_name"] as? String ?: ""
                        val name = jwt.claims["name"] as? String ?: ""
                        val sub = jwt.claims["sub"] as? String ?: ""

                        MDC.put("action", "create_user")

                        // SECURITY: Validate email format
                        if (email.isBlank() || !isValidEmail(email)) {
                            MDC.put("result", "validation_failed")
                            logger.error {
                                "SECURITY: Attempted user creation with invalid email: email=$email, subject=$sub"
                            }
                            MDC.remove("action")
                            MDC.remove("result")
                            throw IllegalArgumentException("Invalid email address provided in JWT")
                        }

                        // SECURITY: Validate string lengths to prevent abuse
                        if (givenName.length > 100 || familyName.length > 100 || name.length > 200) {
                            MDC.put("result", "validation_failed")
                            logger.error {
                                "SECURITY: Attempted user creation with excessively long name fields: email=$email"
                            }
                            MDC.remove("action")
                            MDC.remove("result")
                            throw IllegalArgumentException("Name fields exceed maximum length")
                        }

                        val newCustomer =
                            Customer(
                                UUID.randomUUID().toString(),
                                givenName,
                                familyName,
                                name,
                                sub,
                                email,
                                "USER",
                            )

                        // SECURITY: Log new user creation for audit trail
                        logger.info {
                            "SECURITY: Creating new user account - email=$email, subject=$sub, " +
                                "givenName=$givenName, familyName=$familyName"
                        }

                        try {
                            userInfoProducer.saveNewUser(newCustomer)
                            MDC.put("result", "success")
                            logger.info { "SECURITY: Successfully created new user account: email=$email, userId=$sub" }
                        } catch (e: Exception) {
                            MDC.put("result", "failure")
                            logger.error(e) { "SECURITY: Failed to create user account: email=$email, error=${e.message}" }
                            MDC.remove("action")
                            MDC.remove("result")
                            throw IllegalStateException("Failed to create user account", e)
                        }

                        authorities?.add(SimpleGrantedAuthority("USER"))
                        MDC.remove("action")
                        MDC.remove("result")
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error processing JWT authentication for userId=$userId" }
                    throw e
                }
            }

            logger.debug { "Assigned authorities: $authorities for userId=$userId" }

            // Clean up MDC
            MDC.remove("user_id")
            MDC.remove("email")

            authorities
        }

        return jwtConverter
    }

    /**
     * Validates email address format.
     *
     * @param email Email address to validate
     * @return true if email is valid, false otherwise
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Checks if email domain is in the allowed list.
     * Currently allows all domains. Override this method to restrict user registration to specific domains.
     *
     * To enable domain restrictions:
     * 1. Remove the @Suppress annotation
     * 2. Call this method in the JWT authentication converter before creating users
     * 3. Add allowed domains to the logic below
     *
     * @param email Email address to check
     * @return true if domain is allowed, false otherwise
     */
    @Suppress("unused")
    private fun isAllowedDomain(email: String): Boolean {
        // Currently allows all domains for open registration
        // To restrict, uncomment and customize:
        // val allowedDomains = listOf("@yourcompany.com", "@partner.com")
        // return allowedDomains.any { email.endsWith(it) }
        return true
    }
}
