package de.hipp.pnp.api.fivee.interfaces

import de.hipp.pnp.api.locale.BookLocale

interface FiveEDataProducer {
    fun getAllLanguageKeys(): MutableMap<String, BookLocale>?

    fun getLanguageKeysByGameTypeAndLanguage(gameType: Int, locale: String?): MutableMap<String, BookLocale>?
}
