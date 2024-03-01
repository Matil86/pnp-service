package de.hipp.pnp.api.fivee;

import java.util.List;

public record BookEntry(String bookName, List<LanguageEntry> languageEntries) {
}
