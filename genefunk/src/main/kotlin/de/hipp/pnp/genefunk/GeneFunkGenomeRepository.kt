package de.hipp.pnp.genefunk

import com.google.cloud.firestore.Filter
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class GeneFunkGenomeRepository(
    private val firestore: Firestore,
) {
    private val collectionName = "genefunk_genomes"

    fun findAll(): MutableList<GeneFunkGenome> =
        runBlocking {
            firestore
                .collection(collectionName)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(GeneFunkGenome::class.java) }
                .toMutableList()
        }

    fun findByName(name: String?): GeneFunkGenome? =
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
                ?.toObject(GeneFunkGenome::class.java)
        }

    fun existsByName(name: String?): Boolean =
        runBlocking {
            if (name == null) return@runBlocking false
            val result =
                firestore
                    .collection(collectionName)
                    .where(Filter.equalTo("name", name))
                    .limit(1)
                    .get()
                    .get()
            !result.isEmpty
        }

    fun saveAndFlush(genome: GeneFunkGenome): GeneFunkGenome =
        runBlocking {
            val docName = genome.name ?: throw IllegalArgumentException("Genome name cannot be null")
            firestore
                .collection(collectionName)
                .document(docName)
                .set(genome)
                .get()
            genome
        }
}
