package de.hipp.pnp.security.user

import com.google.cloud.firestore.Filter
import com.google.cloud.firestore.Firestore
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val firestore: Firestore,
) {
    suspend fun getUserByExternalIdentifer(externalId: String?): User? {
        val firestoreUser =
            firestore
                .collection("users")
                .where(Filter.equalTo("externalIdentifier", externalId))
                .limit(1)
                .get()
        return firestoreUser
            .get()
            .documents
            .firstOrNull()
            ?.toObject(User::class.java)
    }

    suspend fun save(user: User): User? {
        firestore.collection("users").document(user.userId).set(user)
        return user
    }
}
