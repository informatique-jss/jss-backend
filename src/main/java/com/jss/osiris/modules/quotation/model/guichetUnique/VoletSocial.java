package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.JeuneAgriculteur;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureVoletSocial;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OrganismeAssuranceMaladieActue;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SituationVisAVisMsa;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutExerciceActiviteSimultan;

@Entity
public class VoletSocial implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nature_volet_social")
    NatureVoletSocial natureVoletSocial;

    @Column(length = 255)
    private String dateEffetVoletSocial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situation_vis_a_vis_msa")
    SituationVisAVisMsa situationVisAVisMsa;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_activite_anterieure_activite")
    Activite activiteAnterieureActivite;

    private LocalDate activiteAnterieureDateFin;

    @Column(length = 255)
    private String activiteAnterieureCodeGeo;

    @Column(length = 255)
    private String activiteAnterieurePays;

    @Column(length = 255)
    private String activiteAnterieureCommune;

    @Column(length = 255)
    private String activiteAnterieureCodePostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organisme_assurance_maladie_actue")
    OrganismeAssuranceMaladieActue organismeAssuranceMaladieActuelle;

    @Column(length = 255)
    private String autreOrganisme;

    private Boolean demandeAcre;

    private Boolean activiteSimultanee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_exercice_activite_simultan")
    StatutExerciceActiviteSimultan statutExerciceActiviteSimultanee;

    @Column(length = 255)
    private String autreActiviteExercee;

    private Boolean affiliationPamBiologiste;

    private Boolean affiliationPamPharmacien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jeune_agriculteur")
    JeuneAgriculteur jeuneAgriculteur;

    @Column(length = 255)
    private String organismePension;

    @Column(length = 255)
    private String nonSalarieOuConjointBeneficiaireRsaRmi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_choix_organisme_assurance_maladie")
    OrganismeAssuranceMaladieActue choixOrganismeAssuranceMaladie;

    @Column(length = 255)
    private String departementOrganismeAssuranceMaladie;

    private Boolean indicateurRegimeAssuranceMaladie;

    private Boolean declarationMineur;

    private Integer nbMineursDeclares;

    private Boolean activiteNonSalariee;

    private Boolean indicateurActiviteAnterieure;

    @Column(length = 255)
    private String ancienNumeroSiren;

    @OneToMany(mappedBy = "voletSocial")
    @JsonIgnoreProperties(value = { "voletSocial" }, allowSetters = true)
    List<Mineur> mineur;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NatureVoletSocial getNatureVoletSocial() {
        return natureVoletSocial;
    }

    public void setNatureVoletSocial(NatureVoletSocial natureVoletSocial) {
        this.natureVoletSocial = natureVoletSocial;
    }

    public String getDateEffetVoletSocial() {
        return dateEffetVoletSocial;
    }

    public void setDateEffetVoletSocial(String dateEffetVoletSocial) {
        this.dateEffetVoletSocial = dateEffetVoletSocial;
    }

    public SituationVisAVisMsa getSituationVisAVisMsa() {
        return situationVisAVisMsa;
    }

    public void setSituationVisAVisMsa(SituationVisAVisMsa situationVisAVisMsa) {
        this.situationVisAVisMsa = situationVisAVisMsa;
    }

    public Activite getActiviteAnterieureActivite() {
        return activiteAnterieureActivite;
    }

    public void setActiviteAnterieureActivite(Activite activiteAnterieureActivite) {
        this.activiteAnterieureActivite = activiteAnterieureActivite;
    }

    public LocalDate getActiviteAnterieureDateFin() {
        return activiteAnterieureDateFin;
    }

    public void setActiviteAnterieureDateFin(LocalDate activiteAnterieureDateFin) {
        this.activiteAnterieureDateFin = activiteAnterieureDateFin;
    }

    public String getActiviteAnterieureCodeGeo() {
        return activiteAnterieureCodeGeo;
    }

    public void setActiviteAnterieureCodeGeo(String activiteAnterieureCodeGeo) {
        this.activiteAnterieureCodeGeo = activiteAnterieureCodeGeo;
    }

    public String getActiviteAnterieurePays() {
        return activiteAnterieurePays;
    }

    public void setActiviteAnterieurePays(String activiteAnterieurePays) {
        this.activiteAnterieurePays = activiteAnterieurePays;
    }

    public String getActiviteAnterieureCommune() {
        return activiteAnterieureCommune;
    }

    public void setActiviteAnterieureCommune(String activiteAnterieureCommune) {
        this.activiteAnterieureCommune = activiteAnterieureCommune;
    }

    public String getActiviteAnterieureCodePostal() {
        return activiteAnterieureCodePostal;
    }

    public void setActiviteAnterieureCodePostal(String activiteAnterieureCodePostal) {
        this.activiteAnterieureCodePostal = activiteAnterieureCodePostal;
    }

    public OrganismeAssuranceMaladieActue getOrganismeAssuranceMaladieActuelle() {
        return organismeAssuranceMaladieActuelle;
    }

    public void setOrganismeAssuranceMaladieActuelle(OrganismeAssuranceMaladieActue organismeAssuranceMaladieActuelle) {
        this.organismeAssuranceMaladieActuelle = organismeAssuranceMaladieActuelle;
    }

    public String getAutreOrganisme() {
        return autreOrganisme;
    }

    public void setAutreOrganisme(String autreOrganisme) {
        this.autreOrganisme = autreOrganisme;
    }

    public Boolean getDemandeAcre() {
        return demandeAcre;
    }

    public void setDemandeAcre(Boolean demandeAcre) {
        this.demandeAcre = demandeAcre;
    }

    public Boolean getActiviteSimultanee() {
        return activiteSimultanee;
    }

    public void setActiviteSimultanee(Boolean activiteSimultanee) {
        this.activiteSimultanee = activiteSimultanee;
    }

    public StatutExerciceActiviteSimultan getStatutExerciceActiviteSimultanee() {
        return statutExerciceActiviteSimultanee;
    }

    public void setStatutExerciceActiviteSimultanee(StatutExerciceActiviteSimultan statutExerciceActiviteSimultanee) {
        this.statutExerciceActiviteSimultanee = statutExerciceActiviteSimultanee;
    }

    public String getAutreActiviteExercee() {
        return autreActiviteExercee;
    }

    public void setAutreActiviteExercee(String autreActiviteExercee) {
        this.autreActiviteExercee = autreActiviteExercee;
    }

    public Boolean getAffiliationPamBiologiste() {
        return affiliationPamBiologiste;
    }

    public void setAffiliationPamBiologiste(Boolean affiliationPamBiologiste) {
        this.affiliationPamBiologiste = affiliationPamBiologiste;
    }

    public Boolean getAffiliationPamPharmacien() {
        return affiliationPamPharmacien;
    }

    public void setAffiliationPamPharmacien(Boolean affiliationPamPharmacien) {
        this.affiliationPamPharmacien = affiliationPamPharmacien;
    }

    public JeuneAgriculteur getJeuneAgriculteur() {
        return jeuneAgriculteur;
    }

    public void setJeuneAgriculteur(JeuneAgriculteur jeuneAgriculteur) {
        this.jeuneAgriculteur = jeuneAgriculteur;
    }

    public String getOrganismePension() {
        return organismePension;
    }

    public void setOrganismePension(String organismePension) {
        this.organismePension = organismePension;
    }

    public String getNonSalarieOuConjointBeneficiaireRsaRmi() {
        return nonSalarieOuConjointBeneficiaireRsaRmi;
    }

    public void setNonSalarieOuConjointBeneficiaireRsaRmi(String nonSalarieOuConjointBeneficiaireRsaRmi) {
        this.nonSalarieOuConjointBeneficiaireRsaRmi = nonSalarieOuConjointBeneficiaireRsaRmi;
    }

    public OrganismeAssuranceMaladieActue getChoixOrganismeAssuranceMaladie() {
        return choixOrganismeAssuranceMaladie;
    }

    public void setChoixOrganismeAssuranceMaladie(OrganismeAssuranceMaladieActue choixOrganismeAssuranceMaladie) {
        this.choixOrganismeAssuranceMaladie = choixOrganismeAssuranceMaladie;
    }

    public String getDepartementOrganismeAssuranceMaladie() {
        return departementOrganismeAssuranceMaladie;
    }

    public void setDepartementOrganismeAssuranceMaladie(String departementOrganismeAssuranceMaladie) {
        this.departementOrganismeAssuranceMaladie = departementOrganismeAssuranceMaladie;
    }

    public Boolean getIndicateurRegimeAssuranceMaladie() {
        return indicateurRegimeAssuranceMaladie;
    }

    public void setIndicateurRegimeAssuranceMaladie(Boolean indicateurRegimeAssuranceMaladie) {
        this.indicateurRegimeAssuranceMaladie = indicateurRegimeAssuranceMaladie;
    }

    public Boolean getDeclarationMineur() {
        return declarationMineur;
    }

    public void setDeclarationMineur(Boolean declarationMineur) {
        this.declarationMineur = declarationMineur;
    }

    public Integer getNbMineursDeclares() {
        return nbMineursDeclares;
    }

    public void setNbMineursDeclares(Integer nbMineursDeclares) {
        this.nbMineursDeclares = nbMineursDeclares;
    }

    public Boolean getActiviteNonSalariee() {
        return activiteNonSalariee;
    }

    public void setActiviteNonSalariee(Boolean activiteNonSalariee) {
        this.activiteNonSalariee = activiteNonSalariee;
    }

    public Boolean getIndicateurActiviteAnterieure() {
        return indicateurActiviteAnterieure;
    }

    public void setIndicateurActiviteAnterieure(Boolean indicateurActiviteAnterieure) {
        this.indicateurActiviteAnterieure = indicateurActiviteAnterieure;
    }

    public String getAncienNumeroSiren() {
        return ancienNumeroSiren;
    }

    public void setAncienNumeroSiren(String ancienNumeroSiren) {
        this.ancienNumeroSiren = ancienNumeroSiren;
    }

    public List<Mineur> getMineur() {
        return mineur;
    }

    public void setMineur(List<Mineur> mineur) {
        this.mineur = mineur;
    }

}
