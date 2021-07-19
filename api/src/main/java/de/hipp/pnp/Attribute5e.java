package de.hipp.pnp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attribute5e {

    @JsonIgnore
    int baseValue = 0;

    int value = 0;
    int max = 20;
    int modifier = 0;


    public Attribute5e(int baseValue) {
        this.baseValue = baseValue;
        this.value = baseValue;
    }

    public Attribute5e(int baseValue, int max) {
        this.baseValue = baseValue;
        this.value = baseValue;
        this.max = max;
    }

    public int getModifier() {
        return (value - 10) / 2;
    }

    public void modifyValue(int value) {
        this.value = Math.min(max, baseValue + value);
    }
}
