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
                sessionCreationPolicy = SessionCreationPolicy.IF_REQUIRED
            }
            formLogin {
                disable()
            }
            httpBasic {
                disable()
            }
            authorizeHttpRequests {
                authorize("/", permitAll)
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

            log.debug("User ID from JWT: {}", userId)
            // Look up the user by email and add appropriate authorities
            if (userId != null) {
                val customer = userInfoProducer.getCustomerInfoFor(userId)
                log.debug("Customer from UserInfoProducer: {}", customer)
                if (customer.role != null) {
                    log.debug("Customer role: {}", customer.role)
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
            log.debug("Assigned authorities: {}", authorities)
            authorities
        }

        return jwtConverter
    }


}
