package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication(scanBasePackages = ["de.hipp.*"])
open class GeneFunkServiceApplication {

    fun main(args: Array<String>) {
        SpringApplication.run(GeneFunkServiceApplication::class.java, *args)
    }
}

