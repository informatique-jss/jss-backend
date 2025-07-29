package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Cacheable(false)
@DoNotAudit
@Table(indexes = {
        @Index(name = "idx_residence_principal_insaisissabilite", columnList = "id_insaisissabilite") })
public class ResidencePrincipale implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insaisissabilite")
    @JsonIgnoreProperties(value = { "residencesSecondaires" }, allowSetters = true)
    Insaisissabilite insaisissabilite;

    private Boolean residenceInsaisissable;

    @Column(length = 255)
    private String nomResidence;

    private LocalDate dateEffet;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse")
    AdresseDomicile adresse;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_publication")
    Publication publication;

    private Boolean is28PTriggered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getResidenceInsaisissable() {
        return residenceInsaisissable;
    }

    public void setResidenceInsaisissable(Boolean residenceInsaisissable) {
        this.residenceInsaisissable = residenceInsaisissable;
    }

    public String getNomResidence() {
        return nomResidence;
    }

    public void setNomResidence(String nomResidence) {
        this.nomResidence = nomResidence;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public AdresseDomicile getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDomicile adresse) {
        this.adresse = adresse;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Boolean getIs28PTriggered() {
        return is28PTriggered;
    }

    public void setIs28PTriggered(Boolean is28pTriggered) {
        is28PTriggered = is28pTriggered;
    }

    public Insaisissabilite getInsaisissabilite() {
        return insaisissabilite;
    }

    public void setInsaisissabilite(Insaisissabilite insaisissabilite) {
        this.insaisissabilite = insaisissabilite;
    }

}
