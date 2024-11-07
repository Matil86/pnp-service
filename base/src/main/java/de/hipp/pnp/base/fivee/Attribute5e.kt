package de.hipp.pnp.base.fivee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute5e implements Serializable {


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

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public void modifyValue(Integer value) {
        this.value = Math.min(max, baseValue + value);
    }

    public Integer getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Integer baseValue) {
        this.baseValue = baseValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

}
