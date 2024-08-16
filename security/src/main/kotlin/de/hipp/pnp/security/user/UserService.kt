package de.hipp.pnp.security.user

import de.hipp.pnp.security.Role
import org.springframework.stereotype.Service

@Service
class UserService(private var userRepository: UserRepository) {
    fun userExists(sub: String?): Boolean {
        return userRepository.getUserByExternalIdentifer(sub) != null
    }

    fun getRole(sub: String?): String {
        var role = "ANNONYMOUS"
        val user = userRepository.getUserByExternalIdentifer(sub)
        if (user != null) {
            role = user.role.toString()
        }
        return role
    }

    fun createUser(attributes: Map<String, Any>): User? {
        return create(attributes, false)
    }

    fun createAdmin(attributes: Map<String, Any>): User? {
        return create(attributes, true)
    }

    private fun create(attributes: Map<String, Any>, isAdmin: Boolean): User? {
        if (attributes.isEmpty()) {
            return null
        }
        if (!attributes.containsKey("email_verified")) {
            return null
        }
        if ("true" != attributes["email_verified"].toString()) {
            return null
        }
        val newUser = User()
        newUser.mail = attributes["email"].toString()
        newUser.nachname = attributes["family_name"].toString()
        newUser.vorname = attributes["given_name"].toString()
        newUser.name = attributes["name"].toString()
        newUser.externalIdentifer = attributes["sub"].toString()
        newUser.role = if (isAdmin) Role.ADMIN.toString() else Role.USER.toString()
        userRepository.save(newUser)
        return newUser
    }

    fun updateUser(maskedUser: User) {
        userRepository.save(maskedUser)
    }

    fun getUserByExternalId(externalUserId: String?): User? {
        val user = userRepository.getUserByExternalIdentifer(externalUserId)
        return user
    }
}
