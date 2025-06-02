package de.hipp.pnp.api.fivee.abstracts;


public abstract class BaseCharacterClass {

    private String name;
    private Integer level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void increaseLevel(int amount) {
        this.level = (this.level == null ? amount : this.level + amount);
    }
}
