package de.hipp.pnp;

public enum E5EGameTypes {

    DND(0),
    GENEFUNK(1);

    private final int value;

    E5EGameTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static E5EGameTypes fromValue(int value, E5EGameTypes defaultValue){
        for (int i = 0; i < E5EGameTypes.values().length; i++) {
            if(value == E5EGameTypes.values()[i].getValue()){
                return E5EGameTypes.values()[i];
            }
        }
        return defaultValue;
    }
}
