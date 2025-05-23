package de.hipp.pnp.auth

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

@Configuration
@EnableWebSecurity
open class SecurityConfiguration(@Autowired private var userInfoProducer: UserInfoProducer) {
    var log: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // Add request logging filter
        http.addFilterAfter(
            RequestLoggingFilter(),
            org.springframework.security.web.authentication.AuthenticationFilter::class.java
        )

        http {
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = jwtAuthenticationConverter()
                }
            }
            sessionManagement {
                // Check if desktop app requires stateless session management
                val sessionPolicy = if (System.getenv("USE_STATELESS_SESSION")?.toBoolean() == true) {
                    log.info("Using STATELESS session policy for desktop app compatibility")
                    SessionCreationPolicy.STATELESS
                } else {
                    log.info("Using IF_REQUIRED session policy")
                    SessionCreationPolicy.IF_REQUIRED
                }
                sessionCreationPolicy = sessionPolicy
            }
            formLogin {
                disable()
            }
            httpBasic {
                disable()
            }
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/index.html", permitAll)
                authorize("/login/**", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize("/logout", permitAll)
                authorize("/api/login/**", permitAll)
                authorize("/test/public", permitAll)
                authorize("/test/auth", hasAnyAuthority("ADMIN", "USER"))
                authorize("/resource/**", hasAnyAuthority("ADMIN", "USER"))
                authorize("/user/**", hasAnyAuthority("USER"))
                authorize("/admin/**", hasAnyAuthority("ADMIN"))
            }
            logout {
                logoutSuccessUrl = "/"
            }
            csrf {
                // Check if CSRF protection should be disabled for API endpoints used by desktop app
                val disableCsrfForApi = System.getenv("DISABLE_CSRF_FOR_API")?.toBoolean() == true

                if (disableCsrfForApi) {
                    log.info("CSRF protection disabled for API endpoints")
                    // Disable CSRF for API endpoints
                    ignoringRequestMatchers("/api/**", "/test/**")
                } else {
                    log.info("CSRF protection enabled for all endpoints")
                }

                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
            cors {
                configurationSource = corsConfigurationSource()
            }
        }
        return http.build()
    }

    @Bean
    open fun customer(): Customer? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = auth.principal as? Jwt ?: return null
        val email = jwt.claims["email"] as? String ?: return null
        return userInfoProducer.getCustomerInfoFor(email)
    }

    @Bean
    open fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(false) }
    }

    @Bean
    open fun corsConfiguration(): CorsConfiguration {
        val corsConfiguration = CorsConfiguration()

        // Get allowed origins from environment variable or use defaults
        val desktopAppUrl = System.getenv("DESKTOP_APP_URL")
        val allowedOrigins = mutableListOf("http://localhost:3000", "http://localhost:8080")

        // Add desktop app URL if provided
        if (!desktopAppUrl.isNullOrBlank()) {
            allowedOrigins.add(desktopAppUrl)
            log.info("Added desktop app URL to CORS allowed origins: $desktopAppUrl")
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

            // Look up the user by email and add appropriate authorities
            if (userId != null) {
                val customer = userInfoProducer.getCustomerInfoFor(userId)
                if (customer != null) {
                    val userRole = customer.role ?: "USER"
                    when (userRole) {
                        "ADMIN" -> {
                            authorities?.add(SimpleGrantedAuthority("ADMIN"))
                            authorities?.add(SimpleGrantedAuthority("USER"))
                        }

                        "USER" -> {
                            authorities?.add(SimpleGrantedAuthority("USER"))
                        }
                    }
                } else {
                    // Create a new user if not found
                    val newCustomer = Customer(
                        UUID.randomUUID().toString(),
                        jwt.claims["given_name"] as? String ?: "",
                        jwt.claims["family_name"] as? String ?: "",
                        jwt.claims["name"] as? String ?: "",
                        jwt.claims["sub"] as? String ?: "",
                        userId,
                        "USER"
                    )
                    userInfoProducer.saveNewUser(newCustomer)
                    authorities?.add(SimpleGrantedAuthority("USER"))
                }
            }

            authorities
        }

        return jwtConverter
    }


}
