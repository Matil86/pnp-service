package de.hipp.pnp.security.user

import org.springframework.stereotype.Service

@Service
open class UserService(private var userRepository: UserRepository) {
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

    fun getUserByExternalId(externalUserId: String?): User? {
        val user = userRepository.getUserByExternalIdentifer(externalUserId)
        return user
    }

    open fun saveUser(user: User): User? {
        return userRepository.saveAndFlush(user)
    }
}
