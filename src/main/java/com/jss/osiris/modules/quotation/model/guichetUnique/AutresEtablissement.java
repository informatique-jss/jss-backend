package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = {
        @Index(name = "idx_autres_etablissement_exploitation", columnList = "id_exploitation"),
        @Index(name = "idx_autres_etablissement_personne_morale", columnList = "id_personne_morale"),
        @Index(name = "idx_autres_etablissement_personne_physique", columnList = "id_personne_physique") })
public class AutresEtablissement implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_description_etablissement")
    DescriptionEtablissement descriptionEtablissement;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne_physique")
    @JsonIgnoreProperties(value = { "autresEtablissements" }, allowSetters = true)
    PersonnePhysique personnePhysique;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne_morale")
    @JsonIgnoreProperties(value = { "autresEtablissements" }, allowSetters = true)
    PersonneMorale personneMorale;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exploitation")
    @JsonIgnoreProperties(value = { "autresEtablissements" }, allowSetters = true)
    Exploitation exploitation;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "autresEtablissement")
    @JsonIgnoreProperties(value = { "autresEtablissement" }, allowSetters = true)
    List<Activite> activites;

    @OneToMany(mappedBy = "autresEtablissement")
    @JsonIgnoreProperties(value = { "autresEtablissement" }, allowSetters = true)
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

    public List<NomsDeDomaine> getNomsDeDomaine() {
        return nomsDeDomaine;
    }

    public void setNomsDeDomaine(List<NomsDeDomaine> nomsDeDomaine) {
        this.nomsDeDomaine = nomsDeDomaine;
    }

    public Exploitation getExploitation() {
        return exploitation;
    }

    public void setExploitation(Exploitation exploitation) {
        this.exploitation = exploitation;
    }

    public PersonneMorale getPersonneMorale() {
        return personneMorale;
    }

    public void setPersonneMorale(PersonneMorale personneMorale) {
        this.personneMorale = personneMorale;
    }

    public PersonnePhysique getPersonnePhysique() {
        return personnePhysique;
    }

    public void setPersonnePhysique(PersonnePhysique personnePhysique) {
        this.personnePhysique = personnePhysique;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

}
