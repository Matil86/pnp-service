package de.hipp.pnp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class CharacterGeneratorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
        }

        @Bean
        fun commandLineRunner(ctx: ApplicationContext): CommandLineRunner =
            CommandLineRunner { _: Array<String?>? ->
                logger.info { "Inspecting beans provided by Spring Boot (non-Spring beans):" }
                val beanNames: List<String?> =
                    ctx.getBeanDefinitionNames().filter { bean -> !bean.toString().contains("spring") }
                beanNames.forEach { beanName ->
                    logger.debug { "Bean: $beanName" }
                }
                logger.info { "Total non-Spring beans registered: ${beanNames.size}" }
            }
    }
}
