package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties
public class LocataireGerantMandataire implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
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
