package de.hipp.pnp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["de.hipp.*"])
open class DataServiceApplication {

    fun main(args: Array<String>) {
        runApplication<DataServiceApplication>(*args)
    }
}


