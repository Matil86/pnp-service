package de.hipp.pnp.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Attribute5e {


    @JsonIgnore
    Integer baseValue = 0;
    Integer value = 0;
    Integer max = 20;
    Integer modifier = 0;
    private Long id;

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

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
