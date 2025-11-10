package de.hipp.data.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(LocalizationProperties::class)
open class LanguageKeyConfiguration
