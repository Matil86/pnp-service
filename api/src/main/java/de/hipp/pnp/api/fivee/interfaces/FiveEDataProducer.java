package de.hipp.pnp.api.fivee.interfaces;

import de.hipp.pnp.api.fivee.LanguageValue;

import java.util.Map;

public interface FiveEDataProducer {

    // <Language, <LanguageKey, LanguageEntry>>
    public Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> getAllLanguageKeys();

    // <LanguageKey, LanguageEntry>
    public Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> getLanguageKeysByGameTypeAndLanguage(int gameType, String locale);
}
