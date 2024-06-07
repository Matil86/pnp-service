package de.hipp.pnp.base.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class Customer {
    String userId;
    String vorname;
    String nachname;
    String name;
    String externalIdentifer;
    String mail;
    String role;
}
