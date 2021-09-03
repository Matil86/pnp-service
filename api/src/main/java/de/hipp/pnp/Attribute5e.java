package de.hipp.pnp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attribute5e {
    @Id
    @GeneratedValue
    private Integer id;

    @JsonIgnore
    Integer baseValue = 0;

    Integer value = 0;
    Integer max = 20;
    Integer modifier = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


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
