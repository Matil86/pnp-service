package de.hipp.pnp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    public SecurityConfiguration() {

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .oauth2Login(o -> o
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userAuthoritiesMapper(this.userAuthoritiesMapper()))
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                        })
                )
                .authorizeHttpRequests((auth) ->
                        auth
                                .requestMatchers("/", "/index.html", "/login/**", "/oauth2/**", "/logout").permitAll()
                                .requestMatchers("/resource/**")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers("/user/**")
                                .hasAuthority("USER")
                                .requestMatchers("/admin/**")
                                .hasAuthority("ADMIN")
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

    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> attributes = oidcUserAuthority.getAttributes();
                    String userRole = "ADMIN";
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

                    switch (userRole) {
                        case "ANNONYMOUS":
                            break;
                        case "USER":
                            log.info("User found: {}", attributes.get("sub"));
                            mappedAuthorities.add(new SimpleGrantedAuthority(userRole));
                            break;
                        case "ADMIN":
                            log.info("Admin found: {}", attributes.get("sub"));
                            mappedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
                            mappedAuthorities.add(new SimpleGrantedAuthority("USER"));
                            break;
                        default:
                            log.error("Unknown role found {}", userRole);
                    }
                } else {
                    log.error("Unknown authority: {}", authority);
                }
            });

            return mappedAuthorities;
        };
    }
}