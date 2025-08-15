package de.hipp.pnp.security

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["de.hipp.*"])
open class SecurityServiceApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SecurityServiceApplication::class.java, *args)
        }
    }
}
