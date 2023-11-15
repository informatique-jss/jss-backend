package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = {
        @Index(name = "idx_etablissement_concerne_lien_entreprise", columnList = "id_lien_entreprise"),
        @Index(name = "idx_etablissement_concerne_pouvoir", columnList = "id_pouvoir") })
public class EtablissementConcerne implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pouvoir")
    @JsonIgnoreProperties(value = { "etablissementConcerne" }, allowSetters = true)
    Pouvoir pouvoir;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lien_entreprise")
    @JsonIgnoreProperties(value = { "etablissementConcerne" }, allowSetters = true)
    LienEntreprise lienEntreprise;

    @Column(length = 255)
    private String siret;

    private LocalDate dateEffetEtablissementConcerne;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public LocalDate getDateEffetEtablissementConcerne() {
        return dateEffetEtablissementConcerne;
    }

    public void setDateEffetEtablissementConcerne(LocalDate dateEffetEtablissementConcerne) {
        this.dateEffetEtablissementConcerne = dateEffetEtablissementConcerne;
    }

    public LienEntreprise getLienEntreprise() {
        return lienEntreprise;
    }

    public void setLienEntreprise(LienEntreprise lienEntreprise) {
        this.lienEntreprise = lienEntreprise;
    }

    public Pouvoir getPouvoir() {
        return pouvoir;
    }

    public void setPouvoir(Pouvoir pouvoir) {
        this.pouvoir = pouvoir;
    }

}
