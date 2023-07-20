package com.jss.osiris.modules.quotation.model.guichetUnique;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class ComptesAnnuels {
    @Id
    @SequenceGenerator(name = "guichet_unique_comptes_annuels_sequence", sequenceName = "guichet_unique_comptes_annuels_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_comptes_annuels_sequence")
    private Integer id;

    private boolean interventionCAC;
    private boolean comptesApprouves;
    private boolean comptesConsolides;
    private boolean petiteEntreprise;
    private boolean associeUniquePresident;
    private boolean activiteCreditOuAssurance;
    private Double montantCAAnneePrecedente;
    private Double montantTotalBilanAnneePrecedente;
    private boolean activiteProspection;
    private boolean activiteSansConfidentialite;
    private Integer nbSalarie;
    private String dateCloture;
    private String dateDebutExerciceComptable;
    private String dateFinExerciceComptable;
    private boolean dispenseDepotAnnexes;
    private boolean declarationAffectationPatrimoine;
    private boolean depotSimplifie;
    private boolean nouveauDepot;
    private String niveauConfidentialite;
    private boolean depotModeExpert;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compte_bilan")
    private CompteBilan compteBilan;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compte_resultat")
    private CompteResultat compteResultat;

    public boolean isInterventionCAC() {
        return interventionCAC;
    }

    public void setInterventionCAC(boolean interventionCAC) {
        this.interventionCAC = interventionCAC;
    }

    public boolean isComptesApprouves() {
        return comptesApprouves;
    }

    public void setComptesApprouves(boolean comptesApprouves) {
        this.comptesApprouves = comptesApprouves;
    }

    public boolean isComptesConsolides() {
        return comptesConsolides;
    }

    public void setComptesConsolides(boolean comptesConsolides) {
        this.comptesConsolides = comptesConsolides;
    }

    public boolean isPetiteEntreprise() {
        return petiteEntreprise;
    }

    public void setPetiteEntreprise(boolean petiteEntreprise) {
        this.petiteEntreprise = petiteEntreprise;
    }

    public boolean isAssocieUniquePresident() {
        return associeUniquePresident;
    }

    public void setAssocieUniquePresident(boolean associeUniquePresident) {
        this.associeUniquePresident = associeUniquePresident;
    }

    public boolean isActiviteCreditOuAssurance() {
        return activiteCreditOuAssurance;
    }

    public void setActiviteCreditOuAssurance(boolean activiteCreditOuAssurance) {
        this.activiteCreditOuAssurance = activiteCreditOuAssurance;
    }

    public boolean isActiviteProspection() {
        return activiteProspection;
    }

    public void setActiviteProspection(boolean activiteProspection) {
        this.activiteProspection = activiteProspection;
    }

    public boolean isActiviteSansConfidentialite() {
        return activiteSansConfidentialite;
    }

    public void setActiviteSansConfidentialite(boolean activiteSansConfidentialite) {
        this.activiteSansConfidentialite = activiteSansConfidentialite;
    }

    public Integer getNbSalarie() {
        return nbSalarie;
    }

    public void setNbSalarie(Integer nbSalarie) {
        this.nbSalarie = nbSalarie;
    }

    public boolean isDispenseDepotAnnexes() {
        return dispenseDepotAnnexes;
    }

    public void setDispenseDepotAnnexes(boolean dispenseDepotAnnexes) {
        this.dispenseDepotAnnexes = dispenseDepotAnnexes;
    }

    public boolean isDeclarationAffectationPatrimoine() {
        return declarationAffectationPatrimoine;
    }

    public void setDeclarationAffectationPatrimoine(boolean declarationAffectationPatrimoine) {
        this.declarationAffectationPatrimoine = declarationAffectationPatrimoine;
    }

    public boolean isDepotSimplifie() {
        return depotSimplifie;
    }

    public void setDepotSimplifie(boolean depotSimplifie) {
        this.depotSimplifie = depotSimplifie;
    }

    public boolean isNouveauDepot() {
        return nouveauDepot;
    }

    public void setNouveauDepot(boolean nouveauDepot) {
        this.nouveauDepot = nouveauDepot;
    }

    public String getNiveauConfidentialite() {
        return niveauConfidentialite;
    }

    public void setNiveauConfidentialite(String niveauConfidentialite) {
        this.niveauConfidentialite = niveauConfidentialite;
    }

    public boolean isDepotModeExpert() {
        return depotModeExpert;
    }

    public void setDepotModeExpert(boolean depotModeExpert) {
        this.depotModeExpert = depotModeExpert;
    }

    public CompteBilan getCompteBilan() {
        return compteBilan;
    }

    public void setCompteBilan(CompteBilan compteBilan) {
        this.compteBilan = compteBilan;
    }

    public CompteResultat getCompteResultat() {
        return compteResultat;
    }

    public void setCompteResultat(CompteResultat compteResultat) {
        this.compteResultat = compteResultat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(String dateCloture) {
        this.dateCloture = dateCloture;
    }

    public String getDateDebutExerciceComptable() {
        return dateDebutExerciceComptable;
    }

    public void setDateDebutExerciceComptable(String dateDebutExerciceComptable) {
        this.dateDebutExerciceComptable = dateDebutExerciceComptable;
    }

    public String getDateFinExerciceComptable() {
        return dateFinExerciceComptable;
    }

    public void setDateFinExerciceComptable(String dateFinExerciceComptable) {
        this.dateFinExerciceComptable = dateFinExerciceComptable;
    }

    public Double getMontantCAAnneePrecedente() {
        return montantCAAnneePrecedente;
    }

    public void setMontantCAAnneePrecedente(Double montantCAAnneePrecedente) {
        this.montantCAAnneePrecedente = montantCAAnneePrecedente;
    }

    public Double getMontantTotalBilanAnneePrecedente() {
        return montantTotalBilanAnneePrecedente;
    }

    public void setMontantTotalBilanAnneePrecedente(Double montantTotalBilanAnneePrecedente) {
        this.montantTotalBilanAnneePrecedente = montantTotalBilanAnneePrecedente;
    }

}