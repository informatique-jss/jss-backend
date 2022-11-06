package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;

@Entity
public class PreneurBail implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_type_personne", nullable = false)
    TypePersonne typePersonne;

    @Column(nullable = false, length = 255)
    private String siren;

    @Column(nullable = false, length = 255)
    private String denomination;

    @Column(nullable = false, length = 255)
    private String nom;

    @OneToMany(mappedBy = "preneurBail")
    @JsonIgnoreProperties(value = { "preneurBail" }, allowSetters = true)
    List<Prenom> prenoms;

    @Column(nullable = false, length = 255)
    private String nomUsage;

    @Column(nullable = false)
    private LocalDate dateEffet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypePersonne getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(TypePersonne typePersonne) {
        this.typePersonne = typePersonne;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Prenom> getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(List<Prenom> prenoms) {
        this.prenoms = prenoms;
    }

    public String getNomUsage() {
        return nomUsage;
    }

    public void setNomUsage(String nomUsage) {
        this.nomUsage = nomUsage;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

}
