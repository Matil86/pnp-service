package de.hipp.pnp.security.user

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@JsonSerialize
@Table(name = "Customer")
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open var userId: UUID? = null,
    open var vorname: String? = null,
    open var nachname: String? = null,
    open var name: String? = null,
    open var externalIdentifer: String? = null,
    open var mail: String? = null,
    open var role: String? = null
)
