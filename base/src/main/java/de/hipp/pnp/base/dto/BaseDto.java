package de.hipp.pnp.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseDto {

    @JsonIgnore
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected BaseDto() {
    }

    @JsonIgnore
    @Override
    public String toString() {
        try {
            return this.objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
