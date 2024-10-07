package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class MontantFormalite {
    @Id
    @SequenceGenerator(name = "montant_formalite_infogreffe_sequence", sequenceName = "montant_formalite_infogreffe_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "montant_formalite_infogreffe_sequence")
    private Integer id;
    @Column(precision = 2)
    private Float montantTotal;
    private String typePaiement;

    @OneToMany(mappedBy = "montantInfogreffe", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "montantInfogreffe" }, allowSetters = true)
    private List<RubriqueInfogreffe> rubriques;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Float montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(String typePaiement) {
        this.typePaiement = typePaiement;
    }

    public List<RubriqueInfogreffe> getRubriques() {
        return rubriques;
    }

    public void setRubriques(List<RubriqueInfogreffe> rubriques) {
        this.rubriques = rubriques;
    }

}
