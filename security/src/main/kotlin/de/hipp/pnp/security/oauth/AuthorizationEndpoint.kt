package de.hipp.pnp.security.oauth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login/oauth2/code")
public class AuthorizationEndpoint {

    @GetMapping("/google")
    public String google(@AuthenticationPrincipal OAuth2User principal) {
        return "Hello World! " + principal.toString();
    }
}
