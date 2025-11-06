package de.hipp.pnp.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.stereotype.Component

/**
 * Language request DTO with input validation.
 *
 * @property locale Locale code in format xx_XX or xx-XX (e.g., en_US, de_DE)
 * @property gameType Game type identifier (0-100)
 */
@Component
data class LanguageRequest(
    @field:Pattern(
        regexp = "^[a-z]{2}[_-][A-Z]{2}$",
        message = "Locale must be in format xx_XX or xx-XX (e.g., en_US, de_DE)"
    )
    var locale: String? = null,

    @field:Min(0, message = "Game type must be non-negative")
    @field:Max(100, message = "Game type must not exceed 100")
    var gameType: Int = 0
)
