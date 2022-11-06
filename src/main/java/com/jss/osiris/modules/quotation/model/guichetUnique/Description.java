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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DeviseCapital;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureDesActivite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureGerance;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OrigineFusionScission;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDeStatuts;

@Entity
public class Description implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String objet;

    @Column(nullable = false, length = 255)
    private String sigle;

    @Column(nullable = false)
    private Integer duree;

    @Column(nullable = false)
    private LocalDate dateClotureExerciceSocial;

    @Column(nullable = false)
    private LocalDate datePremiereCloture;

    @Column(nullable = false)
    private Boolean ess;

    @Column(nullable = false)
    private Boolean societeMission;

    @Column(nullable = false)
    private Boolean capitalVariable;

    @Column(nullable = false)
    private Integer montantCapital;

    @Column(nullable = false)
    private Integer capitalMinimum;

    @ManyToOne
    @JoinColumn(name = "id_devise_capital", nullable = false)
    DeviseCapital deviseCapital;

    @Column(nullable = false, length = 255)
    private String statutLegalParticulier;

    @Column(nullable = false)
    private LocalDate dateAgrementGAEC;

    @ManyToOne
    @JoinColumn(name = "id_type_de_statuts", nullable = false)
    TypeDeStatuts typeDeStatuts;

    @Column(nullable = false)
    private Boolean indicateurOrigineFusionScission;

    @Column(nullable = false)
    private Boolean indicateurEtablissementsEtrangers;

    @Column(nullable = false)
    private Boolean indicateurAssocieUnique;

    @Column(nullable = false)
    private Boolean depotDemandeAcre;

    @Column(nullable = false)
    private Boolean indicateurAssocieUniqueDirigeant;

    @ManyToOne
    @JoinColumn(name = "id_nature_gerance", nullable = false)
    NatureGerance natureGerance;

    @ManyToOne
    @JoinColumn(name = "id_nature_des_activite", nullable = false)
    NatureDesActivite natureDesActivite;

    @Column(nullable = false)
    private Boolean operationEntrainantUneAugmentationDeCapital;

    @Column(nullable = false)
    private Boolean formeCooperative;

    @Column(nullable = false, length = 255)
    private String numeroAgrementGAEC;

    @Column(nullable = false)
    private Boolean capitalDisponible;

    @Column(nullable = false)
    private Boolean prorogationDuree;

    @Column(nullable = false)
    private Boolean continuationAvecActifNetInferieurMoitieCapital;

    @Column(nullable = false)
    private Boolean reconstitutionCapitauxPropres;

    @ManyToOne
    @JoinColumn(name = "id_origine_fusion_scission", nullable = false)
    OrigineFusionScission origineFusionScission;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false)
    private Boolean is12MTriggered;

    @Column(nullable = false)
    private Boolean is10MTriggered;

    @Column(nullable = false)
    private Boolean is16MTriggered;

    @Column(nullable = false)
    private Boolean is18MTriggered;

    @Column(nullable = false)
    private Boolean is15MTriggered;

    @Column(nullable = false)
    private Boolean is17MTriggered;

    @Column(nullable = false)
    private Boolean is19MTriggered;

    @Column(nullable = false)
    private LocalDate dateEffet12M;

    @Column(nullable = false)
    private LocalDate dateEffet10M;

    @Column(nullable = false)
    private LocalDate dateEffet16M;

    @Column(nullable = false)
    private LocalDate dateEffet18M;

    @Column(nullable = false)
    private LocalDate dateEffet15M;

    @Column(nullable = false)
    private LocalDate dateEffet17M;

    @Column(nullable = false)
    private LocalDate dateEffet19M;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public LocalDate getDateClotureExerciceSocial() {
        return dateClotureExerciceSocial;
    }

    public void setDateClotureExerciceSocial(LocalDate dateClotureExerciceSocial) {
        this.dateClotureExerciceSocial = dateClotureExerciceSocial;
    }

    public LocalDate getDatePremiereCloture() {
        return datePremiereCloture;
    }

    public void setDatePremiereCloture(LocalDate datePremiereCloture) {
        this.datePremiereCloture = datePremiereCloture;
    }

    public Boolean getEss() {
        return ess;
    }

    public void setEss(Boolean ess) {
        this.ess = ess;
    }

    public Boolean getSocieteMission() {
        return societeMission;
    }

    public void setSocieteMission(Boolean societeMission) {
        this.societeMission = societeMission;
    }

    public Boolean getCapitalVariable() {
        return capitalVariable;
    }

    public void setCapitalVariable(Boolean capitalVariable) {
        this.capitalVariable = capitalVariable;
    }

    public Integer getMontantCapital() {
        return montantCapital;
    }

    public void setMontantCapital(Integer montantCapital) {
        this.montantCapital = montantCapital;
    }

    public Integer getCapitalMinimum() {
        return capitalMinimum;
    }

    public void setCapitalMinimum(Integer capitalMinimum) {
        this.capitalMinimum = capitalMinimum;
    }

    public DeviseCapital getDeviseCapital() {
        return deviseCapital;
    }

    public void setDeviseCapital(DeviseCapital deviseCapital) {
        this.deviseCapital = deviseCapital;
    }

    public String getStatutLegalParticulier() {
        return statutLegalParticulier;
    }

    public void setStatutLegalParticulier(String statutLegalParticulier) {
        this.statutLegalParticulier = statutLegalParticulier;
    }

    public LocalDate getDateAgrementGAEC() {
        return dateAgrementGAEC;
    }

    public void setDateAgrementGAEC(LocalDate dateAgrementGAEC) {
        this.dateAgrementGAEC = dateAgrementGAEC;
    }

    public TypeDeStatuts getTypeDeStatuts() {
        return typeDeStatuts;
    }

    public void setTypeDeStatuts(TypeDeStatuts typeDeStatuts) {
        this.typeDeStatuts = typeDeStatuts;
    }

    public Boolean getIndicateurOrigineFusionScission() {
        return indicateurOrigineFusionScission;
    }

    public void setIndicateurOrigineFusionScission(Boolean indicateurOrigineFusionScission) {
        this.indicateurOrigineFusionScission = indicateurOrigineFusionScission;
    }

    public Boolean getIndicateurEtablissementsEtrangers() {
        return indicateurEtablissementsEtrangers;
    }

    public void setIndicateurEtablissementsEtrangers(Boolean indicateurEtablissementsEtrangers) {
        this.indicateurEtablissementsEtrangers = indicateurEtablissementsEtrangers;
    }

    public Boolean getIndicateurAssocieUnique() {
        return indicateurAssocieUnique;
    }

    public void setIndicateurAssocieUnique(Boolean indicateurAssocieUnique) {
        this.indicateurAssocieUnique = indicateurAssocieUnique;
    }

    public Boolean getDepotDemandeAcre() {
        return depotDemandeAcre;
    }

    public void setDepotDemandeAcre(Boolean depotDemandeAcre) {
        this.depotDemandeAcre = depotDemandeAcre;
    }

    public Boolean getIndicateurAssocieUniqueDirigeant() {
        return indicateurAssocieUniqueDirigeant;
    }

    public void setIndicateurAssocieUniqueDirigeant(Boolean indicateurAssocieUniqueDirigeant) {
        this.indicateurAssocieUniqueDirigeant = indicateurAssocieUniqueDirigeant;
    }

    public NatureGerance getNatureGerance() {
        return natureGerance;
    }

    public void setNatureGerance(NatureGerance natureGerance) {
        this.natureGerance = natureGerance;
    }

    public NatureDesActivite getNatureDesActivite() {
        return natureDesActivite;
    }

    public void setNatureDesActivite(NatureDesActivite natureDesActivite) {
        this.natureDesActivite = natureDesActivite;
    }

    public Boolean getOperationEntrainantUneAugmentationDeCapital() {
        return operationEntrainantUneAugmentationDeCapital;
    }

    public void setOperationEntrainantUneAugmentationDeCapital(Boolean operationEntrainantUneAugmentationDeCapital) {
        this.operationEntrainantUneAugmentationDeCapital = operationEntrainantUneAugmentationDeCapital;
    }

    public Boolean getFormeCooperative() {
        return formeCooperative;
    }

    public void setFormeCooperative(Boolean formeCooperative) {
        this.formeCooperative = formeCooperative;
    }

    public String getNumeroAgrementGAEC() {
        return numeroAgrementGAEC;
    }

    public void setNumeroAgrementGAEC(String numeroAgrementGAEC) {
        this.numeroAgrementGAEC = numeroAgrementGAEC;
    }

    public Boolean getCapitalDisponible() {
        return capitalDisponible;
    }

    public void setCapitalDisponible(Boolean capitalDisponible) {
        this.capitalDisponible = capitalDisponible;
    }

    public Boolean getProrogationDuree() {
        return prorogationDuree;
    }

    public void setProrogationDuree(Boolean prorogationDuree) {
        this.prorogationDuree = prorogationDuree;
    }

    public Boolean getContinuationAvecActifNetInferieurMoitieCapital() {
        return continuationAvecActifNetInferieurMoitieCapital;
    }

    public void setContinuationAvecActifNetInferieurMoitieCapital(
            Boolean continuationAvecActifNetInferieurMoitieCapital) {
        this.continuationAvecActifNetInferieurMoitieCapital = continuationAvecActifNetInferieurMoitieCapital;
    }

    public Boolean getReconstitutionCapitauxPropres() {
        return reconstitutionCapitauxPropres;
    }

    public void setReconstitutionCapitauxPropres(Boolean reconstitutionCapitauxPropres) {
        this.reconstitutionCapitauxPropres = reconstitutionCapitauxPropres;
    }

    public OrigineFusionScission getOrigineFusionScission() {
        return origineFusionScission;
    }

    public void setOrigineFusionScission(OrigineFusionScission origineFusionScission) {
        this.origineFusionScission = origineFusionScission;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public Boolean getIs12MTriggered() {
        return is12MTriggered;
    }

    public void setIs12MTriggered(Boolean is12mTriggered) {
        is12MTriggered = is12mTriggered;
    }

    public Boolean getIs10MTriggered() {
        return is10MTriggered;
    }

    public void setIs10MTriggered(Boolean is10mTriggered) {
        is10MTriggered = is10mTriggered;
    }

    public Boolean getIs16MTriggered() {
        return is16MTriggered;
    }

    public void setIs16MTriggered(Boolean is16mTriggered) {
        is16MTriggered = is16mTriggered;
    }

    public Boolean getIs18MTriggered() {
        return is18MTriggered;
    }

    public void setIs18MTriggered(Boolean is18mTriggered) {
        is18MTriggered = is18mTriggered;
    }

    public Boolean getIs15MTriggered() {
        return is15MTriggered;
    }

    public void setIs15MTriggered(Boolean is15mTriggered) {
        is15MTriggered = is15mTriggered;
    }

    public Boolean getIs17MTriggered() {
        return is17MTriggered;
    }

    public void setIs17MTriggered(Boolean is17mTriggered) {
        is17MTriggered = is17mTriggered;
    }

    public Boolean getIs19MTriggered() {
        return is19MTriggered;
    }

    public void setIs19MTriggered(Boolean is19mTriggered) {
        is19MTriggered = is19mTriggered;
    }

    public LocalDate getDateEffet12M() {
        return dateEffet12M;
    }

    public void setDateEffet12M(LocalDate dateEffet12M) {
        this.dateEffet12M = dateEffet12M;
    }

    public LocalDate getDateEffet10M() {
        return dateEffet10M;
    }

    public void setDateEffet10M(LocalDate dateEffet10M) {
        this.dateEffet10M = dateEffet10M;
    }

    public LocalDate getDateEffet16M() {
        return dateEffet16M;
    }

    public void setDateEffet16M(LocalDate dateEffet16M) {
        this.dateEffet16M = dateEffet16M;
    }

    public LocalDate getDateEffet18M() {
        return dateEffet18M;
    }

    public void setDateEffet18M(LocalDate dateEffet18M) {
        this.dateEffet18M = dateEffet18M;
    }

    public LocalDate getDateEffet15M() {
        return dateEffet15M;
    }

    public void setDateEffet15M(LocalDate dateEffet15M) {
        this.dateEffet15M = dateEffet15M;
    }

    public LocalDate getDateEffet17M() {
        return dateEffet17M;
    }

    public void setDateEffet17M(LocalDate dateEffet17M) {
        this.dateEffet17M = dateEffet17M;
    }

    public LocalDate getDateEffet19M() {
        return dateEffet19M;
    }

    public void setDateEffet19M(LocalDate dateEffet19M) {
        this.dateEffet19M = dateEffet19M;
    }

}
