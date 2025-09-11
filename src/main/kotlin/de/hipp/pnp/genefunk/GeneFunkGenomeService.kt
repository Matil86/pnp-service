package de.hipp.pnp.genefunk

import de.hipp.pnp.base.entity.CharacterSpeciesEntity
import de.hipp.pnp.genefunk.GenefunkInfoProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class GeneFunkGenomeService(
    val repository: GeneFunkGenomeRepository,
    val genefunkInfoProducer: GenefunkInfoProducer
) {

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        val genomes: List<CharacterSpeciesEntity>? = genefunkInfoProducer.getAllSpecies()
        if (genomes.isNullOrEmpty()) {
            println("No genomes found for GeneFunk")
            return
        }
        val geneFunkGenomes = genomes.map { species ->
            GeneFunkGenome().apply {
                name = species.name
                description = species.description
                attributes = species.attributes.mapValues { entry -> entry.value.toInt() }.toMutableMap()
                features = species.features.map { it }.toMutableSet()
                genomeType = getGenomeType(species.name)
            }
        }
        println("genomes found for GeneFunk: ${geneFunkGenomes.size}")
        save(geneFunkGenomes)
    }

    private fun getGenomeType(name: String): GeneFunkGenomeType {
        when (name.lowercase()) {
            "mutts" -> return GeneFunkGenomeType.MUTTS
            "optimized" -> return GeneFunkGenomeType.OPTIMIZED
            "transhuman" -> return GeneFunkGenomeType.TRANSHUMAN
            else -> return GeneFunkGenomeType.ENGINEERED
        }
    }

    fun save(genomes: List<GeneFunkGenome>) {
        genomes.forEach {
            log.info { "Saving genome: ${it.name}" }
            if (repository.existsByName(it.name)) {
                log.warn { "Genome with name ${it.name} already exists, skipping save." }
                return@forEach
            }
            repository.saveAndFlush(it)
        }
    }

    fun allGenomes(): MutableList<GeneFunkGenome?> = repository.findAll()
}
