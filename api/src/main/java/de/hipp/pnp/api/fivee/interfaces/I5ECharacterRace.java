package de.hipp.pnp.api.fivee.interfaces;

public interface I5ECharacterRace {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    void addAttributeChange(String key, Integer value);
}
