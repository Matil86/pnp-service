package de.hipp.pnp.security.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class UserService(
    private var userRepository: UserRepository,
) {
    suspend fun userExists(sub: String?): Boolean = this.getUserByExternalId(sub) != null

    suspend fun getRole(sub: String?): String {
        var role = "ANNONYMOUS"
        val user = this.getUserByExternalId(sub)
        if (user != null) {
            role = user.role.toString()
        }
        return role
    }

    suspend fun getUserByExternalId(externalUserId: String?): User? =
        withContext(Dispatchers.IO) {
            userRepository.getUserByExternalIdentifer(externalUserId)
        }

    suspend fun saveUser(user: User): User? =
        withContext(Dispatchers.IO) {
            userRepository.save(user)
        }
}
