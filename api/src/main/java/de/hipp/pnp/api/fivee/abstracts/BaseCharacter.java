package de.hipp.pnp.api.fivee.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

public class BaseCharacter {

    private int gameType;
    private String firstName;
    private String lastName;
    private Integer level;
    protected Set<BaseCharacterClass> characterClasses;

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Set<BaseCharacterClass> getCharacterClasses() {
        return characterClasses;
    }

    public void setCharacterClasses(Set<BaseCharacterClass> characterClasses) {
        this.characterClasses = characterClasses;
    }

    @Override
    @JsonIgnore
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
