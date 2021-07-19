package de.hip.pnp.genefunk;

import javax.persistence.Id;

enum GeneFunkGenomeType {
    UNKNOWN(-1),
    Engineered(0),
    MUTT(1),
    OPTIMIZED(2),
    TRANSHUMAN(3);
    @Id
    int value;

    GeneFunkGenomeType(int value) {
        this.value = value;
    }

    public static GeneFunkGenomeType valueOf(int value) {
        GeneFunkGenomeType[] values = GeneFunkGenomeType.values();
        for (GeneFunkGenomeType type : values) {
            if (type.getValue() == value) return type;
        }
        return GeneFunkGenomeType.UNKNOWN;
    }

    public int getValue() {
        return this.value;
    }
}
