package de.hipp.pnp.api.rabbitMq;

import org.springframework.stereotype.Component;

@Component
public class MessageHeader {
    String externalId;
    String[] roles;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
