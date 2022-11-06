package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Building implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_adresse", nullable = false)
    Immeuble adresse;

    @ManyToOne
    @JoinColumn(name = "id_activite")
    @JsonIgnoreProperties(value = { "buildings" }, allowSetters = true)
    Activite activite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Immeuble getAdresse() {
        return adresse;
    }

    public void setAdresse(Immeuble adresse) {
        this.adresse = adresse;
    }

    public Activite getActivite() {
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

}
