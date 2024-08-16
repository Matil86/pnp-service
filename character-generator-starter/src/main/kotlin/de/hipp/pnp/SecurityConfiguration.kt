package de.hipp.pnp

import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

@Configuration
@EnableWebSecurity
open class SecurityConfiguration(var userInfoProducer: UserInfoProducer) {
    var log: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    private fun userAuthoritiesMapper(): GrantedAuthoritiesMapper =
        GrantedAuthoritiesMapper { authorities: Collection<GrantedAuthority> ->
            val mappedAuthorities = emptySet<GrantedAuthority>()

            authorities.forEach { authority ->
                if (authority is OidcUserAuthority) {
                    val attributes = authority.attributes
                    val userRole = "ADMIN"

                    /* if (userService.userExists(String.valueOf(attributes.get("sub")))) {
                            userRole = userService.getRole(String.valueOf(attributes.get("sub")));
                        } else {
                            User createdUser = userService.createUser(attributes);
                            if (createdUser != null) {
                                userRole = Role.USER.toString();
                                User maskedUser = this.maskUserData(createdUser);
                                if (maskedUser != null) {
                                    userService.updateUser(maskedUser);
                                }
                            }
                        }*/
                    when (userRole) {
                        "ANNONYMOUS" -> {}
                        "USER" -> {
                            log.info("User found: {}", attributes["sub"])
                            mappedAuthorities + (SimpleGrantedAuthority(userRole))
                        }

                        "ADMIN" -> {
                            log.info("Admin found: {}", attributes["sub"])
                            mappedAuthorities + SimpleGrantedAuthority("ADMIN")
                            mappedAuthorities + (SimpleGrantedAuthority("USER"))
                        }

                        else -> log.error("Unknown role found {}", userRole)
                    }
                } else {
                    log.error("Unknown authority: {}", authority)
                }
            }

            mappedAuthorities
        }

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


}