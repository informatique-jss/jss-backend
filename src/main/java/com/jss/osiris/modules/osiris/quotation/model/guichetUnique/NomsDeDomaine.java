package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.NatureDomaine;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutDomaine;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

public class NomsDeDomaine implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etablissement_principal")
    @JsonIgnoreProperties(value = { "nomsDeDomaine" }, allowSetters = true)
    EtablissementPrincipal etablissementPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nature_domaine")
    NatureDomaine natureDomaine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_domaine")
    StatutDomaine statutDomaine;

    @Column(length = 255)
    private String nomDomaine;

    private LocalDate dateEffet;

    private Boolean is55PMTriggered;

    private Boolean is14MTriggered;

    private LocalDate dateEffet14M;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autres_etablissement")
    @JsonIgnoreProperties(value = { "nomsDeDomaine" }, allowSetters = true)
    AutresEtablissement autresEtablissement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NatureDomaine getNatureDomaine() {
        return natureDomaine;
    }

    public void setNatureDomaine(NatureDomaine natureDomaine) {
        this.natureDomaine = natureDomaine;
    }

    public StatutDomaine getStatutDomaine() {
        return statutDomaine;
    }

    public void setStatutDomaine(StatutDomaine statutDomaine) {
        this.statutDomaine = statutDomaine;
    }

    public String getNomDomaine() {
        return nomDomaine;
    }

    public void setNomDomaine(String nomDomaine) {
        this.nomDomaine = nomDomaine;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public Boolean getIs55PMTriggered() {
        return is55PMTriggered;
    }

    public void setIs55PMTriggered(Boolean is55pmTriggered) {
        is55PMTriggered = is55pmTriggered;
    }

    public Boolean getIs14MTriggered() {
        return is14MTriggered;
    }

    public void setIs14MTriggered(Boolean is14mTriggered) {
        is14MTriggered = is14mTriggered;
    }

    public LocalDate getDateEffet14M() {
        return dateEffet14M;
    }

    public void setDateEffet14M(LocalDate dateEffet14M) {
        this.dateEffet14M = dateEffet14M;
    }

    public AutresEtablissement getAutresEtablissement() {
        return autresEtablissement;
    }

    public void setAutresEtablissement(AutresEtablissement autresEtablissement) {
        this.autresEtablissement = autresEtablissement;
    }

    public EtablissementPrincipal getEtablissementPrincipal() {
        return etablissementPrincipal;
    }

    public void setEtablissementPrincipal(EtablissementPrincipal etablissementPrincipal) {
        this.etablissementPrincipal = etablissementPrincipal;
    }

}
