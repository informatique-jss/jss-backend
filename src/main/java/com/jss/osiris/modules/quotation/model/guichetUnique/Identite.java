package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    Entreprise entreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrepreneur")
    Entrepreneur entrepreneur;

    @OneToMany(mappedBy = "identite")
    @JsonIgnoreProperties(value = { "identite" }, allowSetters = true)
    List<Eirl> eirl;

    private Boolean contratDAppuiDeclare;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrat_d_appui")
    ContratDAppui contratDAppui;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insaisissabilite")
    Insaisissabilite insaisissabilite;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_correspondance")
    AdresseDomicile adresseCorrespondance;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contact_correspondance")
    Contact contactCorrespondance;

    @Column(length = 255)
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
