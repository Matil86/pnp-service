package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["de.hipp.*"])
open class CharacterGeneratorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
        }
    }
}


