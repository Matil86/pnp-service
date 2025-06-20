package de.hipp.pnp.genefunk

import de.hipp.pnp.base.entity.GeneFunkClass
import de.hipp.pnp.base.rabbitmq.GenefunkInfoProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class GeneFunkClassService(
    private val genefunkInfoProducer: GenefunkInfoProducer
) {

    var genefuncClasses = mutableMapOf<String, GeneFunkClass>()

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        val classes: Map<String, GeneFunkClass> = genefunkInfoProducer.getAllClasses()
        if (classes.isNullOrEmpty()) {
            println("No classes found for GeneFunk")
            return
        }

        this.genefuncClasses.putAll(classes)
    }

    fun getAllClasses(): MutableMap<String, GeneFunkClass> = genefuncClasses
}
