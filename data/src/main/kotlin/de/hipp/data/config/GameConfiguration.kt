package de.hipp.data.config

import de.hipp.pnp.base.entity.GameBooks
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "genefunk")
open class GameConfiguration {

    var books: List<GameBooks> = emptyList()
    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        log.info { "GameConfiguration initialized" }
        log.info { books }
    }
}