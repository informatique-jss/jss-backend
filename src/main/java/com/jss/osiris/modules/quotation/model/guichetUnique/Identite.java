package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Identite implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_entreprise", nullable = false)
    Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "id_entrepreneur", nullable = false)
    Entrepreneur entrepreneur;

    @OneToMany(mappedBy = "identite")
    @JsonIgnoreProperties(value = { "identite" }, allowSetters = true)
    List<Eirl> eirl;

    @Column(nullable = false)
    private Boolean contratDAppuiDeclare;

    @ManyToOne
    @JoinColumn(name = "id_contrat_d_appui", nullable = false)
    ContratDAppui contratDAppui;

    @ManyToOne
    @JoinColumn(name = "id_insaisissabilite", nullable = false)
    Insaisissabilite insaisissabilite;

    @ManyToOne
    @JoinColumn(name = "id_adresse_correspondance", nullable = false)
    AdresseDomicile adresseCorrespondance;

    @ManyToOne
    @JoinColumn(name = "id_contact_correspondance", nullable = false)
    Contact contactCorrespondance;

    @Column(nullable = false, length = 255)
    private String nomCorrespondance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Entrepreneur getEntrepreneur() {
        return entrepreneur;
    }

    public void setEntrepreneur(Entrepreneur entrepreneur) {
        this.entrepreneur = entrepreneur;
    }

    public Boolean getContratDAppuiDeclare() {
        return contratDAppuiDeclare;
    }

    public void setContratDAppuiDeclare(Boolean contratDAppuiDeclare) {
        this.contratDAppuiDeclare = contratDAppuiDeclare;
    }

    public ContratDAppui getContratDAppui() {
        return contratDAppui;
    }

    public void setContratDAppui(ContratDAppui contratDAppui) {
        this.contratDAppui = contratDAppui;
    }

    public Insaisissabilite getInsaisissabilite() {
        return insaisissabilite;
    }

    public void setInsaisissabilite(Insaisissabilite insaisissabilite) {
        this.insaisissabilite = insaisissabilite;
    }

    public AdresseDomicile getAdresseCorrespondance() {
        return adresseCorrespondance;
    }

    public void setAdresseCorrespondance(AdresseDomicile adresseCorrespondance) {
        this.adresseCorrespondance = adresseCorrespondance;
    }

    public Contact getContactCorrespondance() {
        return contactCorrespondance;
    }

    public void setContactCorrespondance(Contact contactCorrespondance) {
        this.contactCorrespondance = contactCorrespondance;
    }

    public String getNomCorrespondance() {
        return nomCorrespondance;
    }

    public void setNomCorrespondance(String nomCorrespondance) {
        this.nomCorrespondance = nomCorrespondance;
    }

    public List<Eirl> getEirl() {
        return eirl;
    }

    public void setEirl(List<Eirl> eirl) {
        this.eirl = eirl;
    }

}
