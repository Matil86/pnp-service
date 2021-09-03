package de.hipp.pnp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute5e {
    @JsonIgnore
    Integer baseValue = 0;

    Integer value = 0;
    Integer max = 20;
    Integer modifier = 0;

    public Attribute5e(Integer baseValue) {
        this.baseValue = baseValue;
        this.value = baseValue;
    }

    public Attribute5e(Integer baseValue, Integer max) {
        this.baseValue = baseValue;
        this.value = baseValue;
        this.max = max;
    }

    public Integer getModifier() {
        return (value - 10) / 2;
    }

    public void modifyValue(Integer value) {
        this.value = Math.min(max, baseValue + value);
    }
}
