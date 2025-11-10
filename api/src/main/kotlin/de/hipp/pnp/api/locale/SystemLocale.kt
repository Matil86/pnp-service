package de.hipp.pnp.api.locale

data class SystemLocale(
    var books: Map<String, BookLocale> = emptyMap(),
)
