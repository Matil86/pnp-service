package de.hipp.pnp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityService securityService;

    public SecurityConfiguration(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .oauth2Login(o -> o
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userAuthoritiesMapper(securityService.userAuthoritiesMapper()))
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                        })
                )
                .authorizeHttpRequests((auth) ->
                        auth
                                .requestMatchers("/", "/index.html", "/login/**", "/oauth2/**", "/logout").permitAll()
                                .requestMatchers("/resource/**")
                                .hasAnyAuthority(Arrays.toString(Role.values()))
                                .requestMatchers("/user/**")
                                .hasAuthority(Role.USER.toString())
                                .requestMatchers("/admin/**")
                                .hasAuthority(Role.ADMIN.toString())
                                .anyRequest().authenticated()
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll())
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(false);
    }


}