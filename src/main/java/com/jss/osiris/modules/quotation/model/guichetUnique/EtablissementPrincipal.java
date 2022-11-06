package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
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
public class EtablissementPrincipal implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_description_etablissement", nullable = false)
    DescriptionEtablissement descriptionEtablissement;

    @ManyToOne
    @JoinColumn(name = "id_domiciliataire", nullable = false)
    Entreprise domiciliataire;

    @ManyToOne
    @JoinColumn(name = "id_adresse_domiciliataire", nullable = false)
    AdresseDomicile adresseDomiciliataire;

    @ManyToOne
    @JoinColumn(name = "id_effectif_salarie", nullable = false)
    EffectifSalarie effectifSalarie;

    @ManyToOne
    @JoinColumn(name = "id_adresse", nullable = false)
    AdresseDomicile adresse;

    @OneToMany(mappedBy = "etablissementPrincipal")
    @JsonIgnoreProperties(value = { "etablissementPrincipal" }, allowSetters = true)
    List<Activite> activites;

    @OneToMany(mappedBy = "etablissementPrincipal")
    @JsonIgnoreProperties(value = { "etablissementPrincipal" }, allowSetters = true)
    List<NomsDeDomaine> nomsDeDomaine;

    @ManyToOne
    @JoinColumn(name = "id_detail_cessation_etablissement", nullable = false)
    DetailCessationEtablissement detailCessationEtablissement;

    @Column(nullable = false)
    private LocalDate dateEffetOuvertureEtablissement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DescriptionEtablissement getDescriptionEtablissement() {
        return descriptionEtablissement;
    }

    public void setDescriptionEtablissement(DescriptionEtablissement descriptionEtablissement) {
        this.descriptionEtablissement = descriptionEtablissement;
    }

    public Entreprise getDomiciliataire() {
        return domiciliataire;
    }

    public void setDomiciliataire(Entreprise domiciliataire) {
        this.domiciliataire = domiciliataire;
    }

    public AdresseDomicile getAdresseDomiciliataire() {
        return adresseDomiciliataire;
    }

    public void setAdresseDomiciliataire(AdresseDomicile adresseDomiciliataire) {
        this.adresseDomiciliataire = adresseDomiciliataire;
    }

    public EffectifSalarie getEffectifSalarie() {
        return effectifSalarie;
    }

    public void setEffectifSalarie(EffectifSalarie effectifSalarie) {
        this.effectifSalarie = effectifSalarie;
    }

    public AdresseDomicile getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDomicile adresse) {
        this.adresse = adresse;
    }

    public DetailCessationEtablissement getDetailCessationEtablissement() {
        return detailCessationEtablissement;
    }

    public void setDetailCessationEtablissement(DetailCessationEtablissement detailCessationEtablissement) {
        this.detailCessationEtablissement = detailCessationEtablissement;
    }

    public LocalDate getDateEffetOuvertureEtablissement() {
        return dateEffetOuvertureEtablissement;
    }

    public void setDateEffetOuvertureEtablissement(LocalDate dateEffetOuvertureEtablissement) {
        this.dateEffetOuvertureEtablissement = dateEffetOuvertureEtablissement;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    public List<NomsDeDomaine> getNomsDeDomaine() {
        return nomsDeDomaine;
    }

    public void setNomsDeDomaine(List<NomsDeDomaine> nomsDeDomaine) {
        this.nomsDeDomaine = nomsDeDomaine;
    }

}
