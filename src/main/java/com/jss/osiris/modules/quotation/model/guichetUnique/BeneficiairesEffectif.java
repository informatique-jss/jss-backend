package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.CascadeType;
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
        @Index(name = "idx_beneficiaires_effectif_personne_morale", columnList = "id_personne_morale") })
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
