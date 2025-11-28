package de.hipp.pnp.genefunk

import com.google.cloud.firestore.Filter
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class GeneFunkClassRepository(
    private val firestore: Firestore,
) {
    private val collectionName = "genefunk_classes"

    fun findAll(): MutableList<GeneFunkClassEntity> =
        runBlocking {
            firestore
                .collection(collectionName)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(GeneFunkClassEntity::class.java) }
                .toMutableList()
        }

    fun findByName(name: String?): GeneFunkClassEntity? =
        runBlocking {
            if (name == null) return@runBlocking null
            firestore
                .collection(collectionName)
                .where(Filter.equalTo("name", name))
                .limit(1)
                .get()
                .get()
                .documents
                .firstOrNull()
                ?.toObject(GeneFunkClassEntity::class.java)
        }

    fun save(classEntity: GeneFunkClassEntity): GeneFunkClassEntity =
        runBlocking {
            val docId = classEntity.id.toString()
            firestore
                .collection(collectionName)
                .document(docId)
                .set(classEntity)
                .get()
            classEntity
        }
}
