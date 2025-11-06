package de.hipp.pnp.security.user

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@JsonSerialize
@Table(name = "Customer")
open class User(
    @Id
    open var userId: String,
    open var vorname: String? = null,
    open var nachname: String? = null,
    open var name: String? = null,
    open var externalIdentifier: String? = null,
    open var mail: String? = null,
    open var role: String? = null
) {
    constructor() : this(
        userId = UUID.randomUUID().toString(),
        vorname = null,
        nachname = null,
        name = null,
        externalIdentifier = null,
        mail = null,
        role = null
    )
}
