package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class AdresseEntreprise implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_caracteristiques", nullable = false)
    Caracteristiques caracteristiques;

    @ManyToOne
    @JoinColumn(name = "id_adresse", nullable = false)
    AdresseDomicile adresse;

    @ManyToOne
    @JoinColumn(name = "id_entreprise_domiciliataire", nullable = false)
    Entreprise entrepriseDomiciliataire;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Caracteristiques getCaracteristiques() {
        return caracteristiques;
    }

    public void setCaracteristiques(Caracteristiques caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public AdresseDomicile getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDomicile adresse) {
        this.adresse = adresse;
    }

    public Entreprise getEntrepriseDomiciliataire() {
        return entrepriseDomiciliataire;
    }

    public void setEntrepriseDomiciliataire(Entreprise entrepriseDomiciliataire) {
        this.entrepriseDomiciliataire = entrepriseDomiciliataire;
    }

}
