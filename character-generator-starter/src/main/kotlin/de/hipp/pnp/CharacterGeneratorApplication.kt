package de.hipp.pnp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class CharacterGeneratorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
        }
    }
}
