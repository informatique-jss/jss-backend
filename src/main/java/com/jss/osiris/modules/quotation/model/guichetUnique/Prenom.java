package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = {
        @Index(name = "idx_prenom_ancien_exploitant", columnList = "id_ancien_exploitant"),
        @Index(name = "idx_prenom_description_entrepreneur", columnList = "id_description_entrepreneur"),
        @Index(name = "idx_prenom_locataire_gerant_mandataire", columnList = "id_locataire_gerant_mandataire"),
        @Index(name = "idx_prenom_preneur_bail", columnList = "id_preneur_bail"),
        @Index(name = "idx_prenom_description_personne", columnList = "id_description_personne") })
public class Prenom implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    public Prenom(String prenom) {
        this.prenom = prenom;
    }

    public Prenom() {
    }

    @Column(length = 255)
    private String prenom;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_preneur_bail")
    @JsonIgnoreProperties(value = { "prenoms" }, allowSetters = true)
    PreneurBail preneurBail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_locataire_gerant_mandataire")
    @JsonIgnoreProperties(value = { "prenoms" }, allowSetters = true)
    LocataireGerantMandataire locataireGerantMandataire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "prenom" }, allowSetters = true)
    @JoinColumn(name = "id_ancien_exploitant")
    private AncienExploitant ancienExploitant;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description_entrepreneur")
    @JsonIgnoreProperties(value = { "prenom" }, allowSetters = true)
    private DescriptionEntrepreneur descriptionEntrepreneur;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description_personne")
    @JsonIgnoreProperties(value = { "prenom" }, allowSetters = true)
    private DescriptionPersonne descriptionPersonne;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocataireGerantMandataire getLocataireGerantMandataire() {
        return locataireGerantMandataire;
    }

    public void setLocataireGerantMandataire(LocataireGerantMandataire locataireGerantMandataire) {
        this.locataireGerantMandataire = locataireGerantMandataire;
    }

    public AncienExploitant getAncienExploitant() {
        return ancienExploitant;
    }

    public void setAncienExploitant(AncienExploitant ancienExploitant) {
        this.ancienExploitant = ancienExploitant;
    }

    public DescriptionEntrepreneur getDescriptionEntrepreneur() {
        return descriptionEntrepreneur;
    }

    public void setDescriptionEntrepreneur(DescriptionEntrepreneur descriptionEntrepreneur) {
        this.descriptionEntrepreneur = descriptionEntrepreneur;
    }

    public DescriptionPersonne getDescriptionPersonne() {
        return descriptionPersonne;
    }

    public void setDescriptionPersonne(DescriptionPersonne descriptionPersonne) {
        this.descriptionPersonne = descriptionPersonne;
    }

    public PreneurBail getPreneurBail() {
        return preneurBail;
    }

    public void setPreneurBail(PreneurBail preneurBail) {
        this.preneurBail = preneurBail;
    }

}
