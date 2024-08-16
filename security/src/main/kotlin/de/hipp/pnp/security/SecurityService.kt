package de.hipp.pnp.security;

import de.hipp.pnp.security.user.User;
import de.hipp.pnp.security.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class SecurityService {

    private final UserService userService;

    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> attributes = oidcUserAuthority.getAttributes();
                    String userRole = "ANNONYMOUS";
                    if (userService.userExists(String.valueOf(attributes.get("sub")))) {
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
                    }

                    switch (userRole) {
                        case "ANNONYMOUS":
                            break;
                        case "USER":
                            log.info("User found: {}", attributes.get("sub"));
                            mappedAuthorities.add(new SimpleGrantedAuthority(userRole));
                            break;
                        case "ADMIN":
                            log.info("Admin found: {}", attributes.get("sub"));
                            mappedAuthorities.add(new SimpleGrantedAuthority(Role.ADMIN.toString()));
                            mappedAuthorities.add(new SimpleGrantedAuthority(Role.USER.toString()));
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

    private User maskUserData(User user) {
        if (user == null) {
            log.error("User not found");
            return null;
        }
        if (user.getExternalIdentifer() == null) {
            log.error("External identifer not found");
            return null;
        }
        String maskingKey = this.getMasikingKeyForUser(user.getExternalIdentifer());
        return user;
    }

    private String getMasikingKeyForUser(String externalIdentifer) {
        return null;
    }


}
