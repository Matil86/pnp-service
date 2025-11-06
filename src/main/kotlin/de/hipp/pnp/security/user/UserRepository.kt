package de.hipp.pnp.security.user

import com.google.cloud.firestore.Filter
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val firestore: Firestore) {
    fun getUserByExternalIdentifer(externalId: String?): User? {
        val firestoreUser = runBlocking {
            firestore.collection("users")
                .where(Filter.equalTo(
                    "externalIdentifier",
                    externalId)
                )
                .limit(1)
                .get()
        }
        return firestoreUser
            .get()
            .documents
            .firstOrNull()?.toObject(User::class.java)
    }

     fun save(user: User): User? {
        firestore.collection("users").document(user.userId).set(user)
        return user
    }
}
