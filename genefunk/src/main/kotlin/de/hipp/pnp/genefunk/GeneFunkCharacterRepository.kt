package de.hipp.pnp.genefunk

import com.google.cloud.firestore.Filter
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class GeneFunkCharacterRepository(
    private val firestore: Firestore,
) {
    private val collectionName = "genefunk_characters"

    fun findAll(): MutableList<GeneFunkCharacter> =
        runBlocking {
            firestore
                .collection(collectionName)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(GeneFunkCharacter::class.java) }
                .toMutableList()
        }

    fun findById(id: Int): GeneFunkCharacter? =
        runBlocking {
            firestore
                .collection(collectionName)
                .document(id.toString())
                .get()
                .get()
                .toObject(GeneFunkCharacter::class.java)
        }

    fun findByUserId(userId: String?): MutableList<GeneFunkCharacter> =
        runBlocking {
            firestore
                .collection(collectionName)
                .where(Filter.equalTo("userId", userId))
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(GeneFunkCharacter::class.java) }
                .toMutableList()
        }

    fun saveAndFlush(character: GeneFunkCharacter): GeneFunkCharacter =
        runBlocking {
            // Generate ID if not present
            if (character.id == null) {
                val maxId = findMaxId()
                character.id = maxId + 1
            }
            firestore
                .collection(collectionName)
                .document(character.id.toString())
                .set(character)
                .get()
            character
        }

    fun deleteById(id: Int) {
        runBlocking {
            firestore
                .collection(collectionName)
                .document(id.toString())
                .delete()
                .get()
        }
    }

    private fun findMaxId(): Int =
        runBlocking {
            val documents =
                firestore
                    .collection(collectionName)
                    .get()
                    .get()
                    .documents
            documents.mapNotNull { it.toObject(GeneFunkCharacter::class.java) }.mapNotNull { it.id }.maxOrNull() ?: 0
        }
}
