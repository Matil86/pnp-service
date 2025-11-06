package de.hipp.pnp.api.dto

import org.springframework.stereotype.Component

@Component
data class LanguageRequest(var locale: String? = null, var gameType: Int = 0)
