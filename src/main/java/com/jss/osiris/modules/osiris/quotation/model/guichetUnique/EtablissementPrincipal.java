package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class EtablissementPrincipal implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description_etablissement")
    DescriptionEtablissement descriptionEtablissement;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_domiciliataire")
    Entreprise domiciliataire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_domiciliataire")
    AdresseDomicile adresseDomiciliataire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_effectif_salarie")
    EffectifSalarie effectifSalarie;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse")
    AdresseDomicile adresse;

    @OneToMany(mappedBy = "etablissementPrincipal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "etablissementPrincipal" }, allowSetters = true)
    List<Activite> activites;

    @OneToMany(mappedBy = "etablissementPrincipal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "etablissementPrincipal" }, allowSetters = true)
    List<NomsDeDomaine> nomsDeDomaine;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detail_cessation_etablissement")
    DetailCessationEtablissement detailCessationEtablissement;

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
