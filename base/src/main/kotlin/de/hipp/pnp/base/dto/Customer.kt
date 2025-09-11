package de.hipp.pnp.base.dto

import org.springframework.stereotype.Component

@Component
data class Customer(
    val userId: String? = null,
    val vorname: String? = null,
    val nachname: String? = null,
    var name: String? = null,
    val externalIdentifer: String? = null,
    val mail: String? = null,
    val role: String? = null
) : BaseDto()