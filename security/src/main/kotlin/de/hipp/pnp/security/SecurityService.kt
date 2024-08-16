package de.hipp.pnp.security

import de.hipp.pnp.security.user.UserService
import org.springframework.stereotype.Service

@Service
class SecurityService(private val userService: UserService)
