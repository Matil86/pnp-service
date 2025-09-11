package de.hipp.pnp.genefunk

import de.hipp.pnp.base.entity.GeneFunkClass
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class GeneFunkClassService(
    private val genefunkInfoProducer: GenefunkInfoProducer
) {

    var genefuncClasses = mutableMapOf<String, GeneFunkClass>()

    private val log = KotlinLogging.logger {}

    fun reloadAllClasses(): MutableMap<String, GeneFunkClass> {
        val classes: Map<String, GeneFunkClass> = genefunkInfoProducer.getAllClasses()
        if (classes.isNullOrEmpty()) {
            log.info {"No classes found for GeneFunk"}
            return mutableMapOf()
        }

        this.genefuncClasses.putAll(classes)
        return this.genefuncClasses
    }

    fun getAllClasses(): MutableMap<String, GeneFunkClass> = genefuncClasses.isEmpty().let { reloadAllClasses() }

}
