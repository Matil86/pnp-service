package de.hipp.pnp.security.user

import org.springframework.stereotype.Service

@Service
class UserService(private var userRepository: UserRepository) {
    suspend fun userExists(sub: String?): Boolean {
        return this.getUserByExternalId(sub) != null
    }

    suspend fun getRole(sub: String?): String {
        var role = "ANNONYMOUS"
        val user = this.getUserByExternalId(sub)
        if (user != null) {
            role = user.role.toString()
        }
        return role
    }

     fun getUserByExternalId(externalUserId: String?): User? = userRepository.getUserByExternalIdentifer(externalUserId)

     fun saveUser(user: User): User? =
         userRepository.save(user)
}
