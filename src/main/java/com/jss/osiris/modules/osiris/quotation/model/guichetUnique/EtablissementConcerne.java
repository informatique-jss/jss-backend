package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

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
@DoNotAudit
@Table(indexes = {
        @Index(name = "idx_etablissement_concerne_lien_entreprise", columnList = "id_lien_entreprise"),
        @Index(name = "idx_etablissement_concerne_pouvoir", columnList = "id_pouvoir") })
public class EtablissementConcerne implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
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
