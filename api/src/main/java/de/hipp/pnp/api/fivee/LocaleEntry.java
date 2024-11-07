package de.hipp.pnp.api.fivee;

import java.util.List;

public record LocaleEntry(String locale, List<GameLanguageEntry> games) {
}
