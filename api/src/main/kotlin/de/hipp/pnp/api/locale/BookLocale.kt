package de.hipp.pnp.api.locale

data class BookLocale(
    var backgrounds: Map<String, LabelDesc> = emptyMap(),
    var classes: Map<String, LabelDesc> = emptyMap(),
    var features: Map<String, LabelDesc> = emptyMap(),
)
