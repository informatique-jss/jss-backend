package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class LocataireGerantMandataire implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(length = 255)
    private String nom;

    @Column(length = 255)
    private String nomUsage;

    @OneToMany(mappedBy = "locataireGerantMandataire")
    @JsonIgnoreProperties(value = { "locataireGerantMandataire" }, allowSetters = true)
    List<Prenom> prenoms;

    @Column(length = 255)
    private String denomination;

    @Column(length = 255)
    private String siren;

    @Column(length = 255)
    private String lieuRegistre;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNomUsage() {
        return nomUsage;
    }

    public void setNomUsage(String nomUsage) {
        this.nomUsage = nomUsage;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getLieuRegistre() {
        return lieuRegistre;
    }

    public void setLieuRegistre(String lieuRegistre) {
        this.lieuRegistre = lieuRegistre;
    }

    public List<Prenom> getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(List<Prenom> prenoms) {
        this.prenoms = prenoms;
    }

}
