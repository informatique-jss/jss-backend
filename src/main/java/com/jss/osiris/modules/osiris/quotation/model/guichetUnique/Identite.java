package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class Identite implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    Entreprise entreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description")
    Description description;

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

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

}
