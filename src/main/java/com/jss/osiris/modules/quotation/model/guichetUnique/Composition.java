package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Composition implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "composition")
    @JsonIgnoreProperties(value = { "composition" }, allowSetters = true)
    List<Pouvoir> pouvoirs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
