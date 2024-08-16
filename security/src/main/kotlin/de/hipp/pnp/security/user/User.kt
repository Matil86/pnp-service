package de.hipp.pnp.security.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@JsonSerialize
@Table(name = "Customer")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID userId;
    String vorname;
    String nachname;
    String name;
    String externalIdentifer;
    String mail;
    String role;

}
