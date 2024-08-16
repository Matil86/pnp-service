package de.hipp.pnp.security.oauth

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("login/oauth2/code")
class AuthorizationEndpoint {
    @GetMapping("/google")
    fun google(@AuthenticationPrincipal principal: OAuth2User): String {
        return "Hello World! $principal"
    }
}
