package de.hipp.pnp.api.fivee;

import java.util.List;

public record GameLanguageEntry(String name, List<BookLanguageEntry> books) {
}
