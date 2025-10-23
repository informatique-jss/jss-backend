package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties
public class BeneficiairesEffectif implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne_morale")
    @JsonIgnoreProperties(value = { "beneficiairesEffectifs" }, allowSetters = true)
    PersonneMorale personneMorale;

    private Boolean indexPouvoir;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_beneficiaire")
    Repreneur beneficiaire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalite")
    Modalite modalite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIndexPouvoir() {
        return indexPouvoir;
    }

    public void setIndexPouvoir(Boolean indexPouvoir) {
        this.indexPouvoir = indexPouvoir;
    }

    public Repreneur getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(Repreneur beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    public Modalite getModalite() {
        return modalite;
    }

    public void setModalite(Modalite modalite) {
        this.modalite = modalite;
    }

    public PersonneMorale getPersonneMorale() {
        return personneMorale;
    }

    public void setPersonneMorale(PersonneMorale personneMorale) {
        this.personneMorale = personneMorale;
    }

}
