package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class PersonneQualifiee implements Serializable, IId {

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
    Immeuble adresseDomicile;

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

    public Immeuble getAdresseDomicile() {
        return adresseDomicile;
    }

    public void setAdresseDomicile(Immeuble adresseDomicile) {
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
