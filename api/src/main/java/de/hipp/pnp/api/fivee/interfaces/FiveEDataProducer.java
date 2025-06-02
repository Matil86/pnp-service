package de.hipp.pnp.api.fivee.interfaces;

import de.hipp.pnp.api.locale.BookLocale;

import java.util.Map;

public interface FiveEDataProducer {

    Map<String, BookLocale> getAllLanguageKeys();

    Map<String, BookLocale> getLanguageKeysByGameTypeAndLanguage(int gameType, String locale);
}
