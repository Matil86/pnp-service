package de.hipp.pnp.genefunk

import com.google.cloud.firestore.Filter
import com.google.cloud.firestore.Firestore
import de.hipp.pnp.base.fivee.Feature5e
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class GeneFunkFeatureRepository(
    private val firestore: Firestore,
) {
    private val collectionName = "genefunk_features"

    fun findAll(): MutableList<Feature5e> =
        runBlocking {
            firestore
                .collection(collectionName)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(Feature5e::class.java) }
                .toMutableList()
        }

    fun findByLabel(label: String?): Feature5e? =
        runBlocking {
            if (label == null) return@runBlocking null
            firestore
                .collection(collectionName)
                .where(Filter.equalTo("label", label))
                .limit(1)
                .get()
                .get()
                .documents
                .firstOrNull()
                ?.toObject(Feature5e::class.java)
        }

    fun save(feature: Feature5e): Feature5e =
        runBlocking {
            // Use label as document ID since it's likely unique
            val docId = feature.label.replace(" ", "_").lowercase()
            firestore
                .collection(collectionName)
                .document(docId)
                .set(feature)
                .get()
            feature
        }
}
