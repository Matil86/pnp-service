package de.hipp.pnp

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import java.util.Arrays


@SpringBootApplication
class CharacterGeneratorApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
        }

        @Bean
        fun commandLineRunner(ctx: ApplicationContext): CommandLineRunner {
            return CommandLineRunner { args: Array<String?>? ->
                println("Let's inspect the beans provided by Spring Boot:")
                val beanNames: List<String?> = ctx.getBeanDefinitionNames().filter { bean -> !bean.toString().contains("spring") }
                Arrays.sort(beanNames.toTypedArray())
                for (beanName in beanNames) {
                    println(beanName)
                }
            }
        }
    }
}
