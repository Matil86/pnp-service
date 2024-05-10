package de.hipp.pnp.security;

enum Roles {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    Roles(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
