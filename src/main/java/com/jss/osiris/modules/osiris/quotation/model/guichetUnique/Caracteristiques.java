package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties
public class Caracteristiques implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    private Boolean ambulant;

    private Boolean domiciliataire;

    private Boolean indicateurDomicileEntrepreneur;

    private Boolean indicateurAdresseEtablissement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getAmbulant() {
        return ambulant;
    }

    public void setAmbulant(Boolean ambulant) {
        this.ambulant = ambulant;
    }

    public Boolean getDomiciliataire() {
        return domiciliataire;
    }

    public void setDomiciliataire(Boolean domiciliataire) {
        this.domiciliataire = domiciliataire;
    }

    public Boolean getIndicateurDomicileEntrepreneur() {
        return indicateurDomicileEntrepreneur;
    }

    public void setIndicateurDomicileEntrepreneur(Boolean indicateurDomicileEntrepreneur) {
        this.indicateurDomicileEntrepreneur = indicateurDomicileEntrepreneur;
    }

    public Boolean getIndicateurAdresseEtablissement() {
        return indicateurAdresseEtablissement;
    }

    public void setIndicateurAdresseEtablissement(Boolean indicateurAdresseEtablissement) {
        this.indicateurAdresseEtablissement = indicateurAdresseEtablissement;
    }

}
