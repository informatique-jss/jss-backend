package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class RubriqueInfogreffe {
    @Id
    @SequenceGenerator(name = "rubrique_montant_formalite_infogreffe_sequence", sequenceName = "rubrique_montant_formalite_infogreffe_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rubrique_montant_formalite_infogreffe_sequence")
    private Integer id;
    private String code;

    @Column(scale = 2)
    @JsonProperty("montantHT")
    private Double montantHt;

    @Column(scale = 2)
    @JsonProperty("montantTva")
    private Double montantTva;

    @Column(scale = 2)
    @JsonProperty("montantTtc")
    private Double montantTtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_montant_formalite_infogreffe")
    @JsonIgnoreProperties(value = { "rubriques" }, allowSetters = true)
    private MontantFormalite montantInfogreffe;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MontantFormalite getMontantInfogreffe() {
        return montantInfogreffe;
    }

    public void setMontantInfogreffe(MontantFormalite montantInfogreffe) {
        this.montantInfogreffe = montantInfogreffe;
    }

    public Double getMontantHt() {
        return montantHt;
    }

    public void setMontantHt(Double montantHt) {
        this.montantHt = montantHt;
    }

    public Double getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(Double montantTva) {
        this.montantTva = montantTva;
    }

    public Double getMontantTtc() {
        return montantTtc;
    }

    public void setMontantTtc(Double montantTtc) {
        this.montantTtc = montantTtc;
    }

}
