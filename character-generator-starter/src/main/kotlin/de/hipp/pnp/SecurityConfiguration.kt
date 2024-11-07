package de.hipp.pnp

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import java.util.UUID

@Configuration
@EnableWebSecurity
open class SecurityConfiguration(@Autowired private var userInfoProducer: UserInfoProducer) {
    var log: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            oauth2Login {
                userInfoEndpoint {
                    userAuthoritiesMapper = userAuthoritiesMapper()
                }
            }
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/index.html", permitAll)
                authorize("/login/**", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize("/logout", permitAll)
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
        }
        return http.build()
    }

    @Bean
    open fun customer(): Customer? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null
        val user = auth.principal as DefaultOidcUser ?: return null
        return userInfoProducer.getCustomerInfoFor(user.name)
    }

    @Bean
    open fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity -> web.debug(false) }
    }

    @Bean
    open fun corsConfiguration(): CorsConfiguration {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("http://localhost:3000")
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        corsConfiguration.allowedHeaders = listOf("*")
        return corsConfiguration
    }

    private fun userAuthoritiesMapper(): GrantedAuthoritiesMapper =
        GrantedAuthoritiesMapper { authorities: Collection<GrantedAuthority> ->
            val mappedAuthorities = mutableSetOf<GrantedAuthority>()

            authorities.forEach { authority ->
                if (authority is OidcUserAuthority) {
                    val attributes = authority.attributes
                    var customer: Customer? = userInfoProducer.getCustomerInfoFor(attributes["sub"].toString())
                    if (customer == null) {
                        customer = Customer(
                            UUID.randomUUID().toString(),
                            attributes["given_name"] as String,
                            attributes["family_name"] as String,
                            attributes["name"] as String,
                            attributes["sub"] as String,
                            attributes["email"] as String,
                            "USER",
                        )
                        customer = userInfoProducer.saveNewUser(customer)
                    }
                    val userRole = customer?.role ?: "ADMIN"
                    when (userRole) {
                        "ANNONYMOUS" -> {}
                        "USER" -> {
                            log.info("User found: {}", attributes["sub"])
                            mappedAuthorities.add(SimpleGrantedAuthority("USER"))
                        }

                        "ADMIN" -> {
                            log.info("Admin found: {}", attributes["sub"])
                            mappedAuthorities.add(SimpleGrantedAuthority("ADMIN"))
                            mappedAuthorities.add(SimpleGrantedAuthority("USER"))
                        }

                        else -> log.error("Unknown role found {}", userRole)
                    }
                } else if (authority is SimpleGrantedAuthority) {
                    log.warn("SimpleGrantedAuthority authority: {}", authority.authority)
                } else {
                    log.error("Unknown authority: {}", authority)
                }
            }

            mappedAuthorities
        }

}