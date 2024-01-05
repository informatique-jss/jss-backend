package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Repreneur implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description_personne")
    DescriptionPersonne descriptionPersonne;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description_entrepreneur")
    DescriptionEntrepreneur descriptionEntrepreneur;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_domicile")
    AdresseDomicile adresseDomicile;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contact")
    Contact contact;

    private LocalDate dateEffet;

    private Boolean is29Or30PTriggered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DescriptionPersonne getDescriptionPersonne() {
        return descriptionPersonne;
    }

    public void setDescriptionPersonne(DescriptionPersonne descriptionPersonne) {
        this.descriptionPersonne = descriptionPersonne;
    }

    public DescriptionEntrepreneur getDescriptionEntrepreneur() {
        return descriptionEntrepreneur;
    }

    public void setDescriptionEntrepreneur(DescriptionEntrepreneur descriptionEntrepreneur) {
        this.descriptionEntrepreneur = descriptionEntrepreneur;
    }

    public AdresseDomicile getAdresseDomicile() {
        return adresseDomicile;
    }

    public void setAdresseDomicile(AdresseDomicile adresseDomicile) {
        this.adresseDomicile = adresseDomicile;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public Boolean getIs29Or30PTriggered() {
        return is29Or30PTriggered;
    }

    public void setIs29Or30PTriggered(Boolean is29Or30PTriggered) {
        this.is29Or30PTriggered = is29Or30PTriggered;
    }

}
