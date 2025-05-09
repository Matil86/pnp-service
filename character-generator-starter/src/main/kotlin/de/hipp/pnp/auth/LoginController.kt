package de.hipp.pnp.auth

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController {
    private val log = KotlinLogging.logger {}
    private val mapper = ObjectMapper()

    @PostMapping
    fun login(request: HttpServletRequest, response: HttpServletResponse): String {
        log.info { "Login requested" }
        val requestString = mapper.writeValueAsString(request)
        log.info { "request: $requestString" }
        val responseString = mapper.writeValueAsString(response)
        log.info { "response: $responseString" }
        return "login"
    }
}