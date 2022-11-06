package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleConjoint;

@Entity
public class Entrepreneur implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_role_conjoint", nullable = false)
    RoleConjoint roleConjoint;

    @ManyToOne
    @JoinColumn(name = "id_conjoint", nullable = false)
    Conjoint conjoint;

    @ManyToOne
    @JoinColumn(name = "id_capacite_juridique", nullable = false)
    CapaciteJuridique capaciteJuridique;

    @ManyToOne
    @JoinColumn(name = "id_volet_social", nullable = false)
    VoletSocial voletSocial;

    @ManyToOne
    @JoinColumn(name = "id_regime_micro_social", nullable = false)
    RegimeMicroSocial regimeMicroSocial;

    @ManyToOne
    @JoinColumn(name = "id_description_personne", nullable = false)
    DescriptionPersonne descriptionPersonne;

    @ManyToOne
    @JoinColumn(name = "id_description_entrepreneur", nullable = false)
    DescriptionEntrepreneur descriptionEntrepreneur;

    @ManyToOne
    @JoinColumn(name = "id_adresse_domicile", nullable = false)
    AdresseDomicile adresseDomicile;

    @ManyToOne
    @JoinColumn(name = "id_contact", nullable = false)
    Contact contact;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false)
    private Boolean is29Or30PTriggered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleConjoint getRoleConjoint() {
        return roleConjoint;
    }

    public void setRoleConjoint(RoleConjoint roleConjoint) {
        this.roleConjoint = roleConjoint;
    }

    public Conjoint getConjoint() {
        return conjoint;
    }

    public void setConjoint(Conjoint conjoint) {
        this.conjoint = conjoint;
    }

    public CapaciteJuridique getCapaciteJuridique() {
        return capaciteJuridique;
    }

    public void setCapaciteJuridique(CapaciteJuridique capaciteJuridique) {
        this.capaciteJuridique = capaciteJuridique;
    }

    public VoletSocial getVoletSocial() {
        return voletSocial;
    }

    public void setVoletSocial(VoletSocial voletSocial) {
        this.voletSocial = voletSocial;
    }

    public RegimeMicroSocial getRegimeMicroSocial() {
        return regimeMicroSocial;
    }

    public void setRegimeMicroSocial(RegimeMicroSocial regimeMicroSocial) {
        this.regimeMicroSocial = regimeMicroSocial;
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
