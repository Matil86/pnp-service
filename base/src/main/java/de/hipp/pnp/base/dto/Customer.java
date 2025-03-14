package de.hipp.pnp.base.dto;

import org.springframework.stereotype.Component;

@Component
public class Customer extends BaseDto {
    String userId;
    String vorname;
    String nachname;
    String name;
    String externalIdentifer;
    String mail;
    String role;

    public Customer(String userId, String vorname, String nachname, String name, String externalIdentifer, String mail, String role) {
        this.userId = userId;
        this.vorname = vorname;
        this.nachname = nachname;
        this.name = name;
        this.externalIdentifer = externalIdentifer;
        this.mail = mail;
        this.role = role;
    }

    public Customer() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalIdentifer() {
        return externalIdentifer;
    }

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }
}
