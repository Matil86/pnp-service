package de.hipp.pnp.base.dto;

import org.springframework.stereotype.Component;

@Component
public class Customer {
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
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
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

    public void setExternalIdentifer(String externalIdentifer) {
        this.externalIdentifer = externalIdentifer;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
