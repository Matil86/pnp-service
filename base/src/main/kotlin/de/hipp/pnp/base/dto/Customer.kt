package de.hipp.pnp.base.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.stereotype.Component

/**
 * Customer data transfer object with security validations.
 *
 * @property userId Internal user identifier
 * @property vorname First name (German: Vorname)
 * @property nachname Last name (German: Nachname)
 * @property name Full display name
 * @property externalIdentifer External identifier (e.g., from OAuth provider)
 * @property mail Email address - must be valid format
 * @property role User role - must be "USER" or "ADMIN"
 */
@Component
data class Customer(
    val userId: String? = null,
    @field:Size(max = 100, message = "First name must not exceed 100 characters")
    val vorname: String? = null,
    @field:Size(max = 100, message = "Last name must not exceed 100 characters")
    val nachname: String? = null,
    @field:Size(max = 200, message = "Name must not exceed 200 characters")
    var name: String? = null,
    @field:Size(max = 255, message = "External identifier must not exceed 255 characters")
    val externalIdentifer: String? = null,
    @field:Email(message = "Email must be a valid email address")
    @field:NotBlank(message = "Email must not be blank")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val mail: String? = null,
    @field:Pattern(
        regexp = "^(USER|ADMIN)$",
        message = "Role must be either 'USER' or 'ADMIN'",
    )
    val role: String? = null,
) : BaseDto()
