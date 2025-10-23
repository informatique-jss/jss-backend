package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.DeviseCapital;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.NatureDesActivite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.NatureGerance;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OrigineFusionScission;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDeStatuts;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Description implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @Column(length = 8000)
    private String objet;

    @Column(length = 255)
    private String sigle;

    private Integer duree;

    private String dateClotureExerciceSocial;

    private LocalDate datePremiereCloture;

    private Boolean ess;

    private Boolean societeMission;

    private Boolean capitalVariable;

    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    private BigDecimal montantCapital;

    private Integer capitalMinimum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devise_capital")
    DeviseCapital deviseCapital;

    @Column(length = 255)
    private String statutLegalParticulier;

    private LocalDate dateAgrementGAEC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_de_statuts")
    TypeDeStatuts typeDeStatuts;

    private Boolean indicateurOrigineFusionScission;

    private Boolean indicateurEtablissementsEtrangers;

    private Boolean indicateurAssocieUnique;

    private Boolean depotDemandeAcre;

    private Boolean indicateurAssocieUniqueDirigeant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nature_gerance")
    NatureGerance natureGerance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nature_des_activite")
    NatureDesActivite natureDesActivite;

    private Boolean operationEntrainantUneAugmentationDeCapital;

    private Boolean formeCooperative;

    @Column(length = 255)
    private String numeroAgrementGAEC;

    private Boolean capitalDisponible;

    private Boolean prorogationDuree;

    private Boolean continuationAvecActifNetInferieurMoitieCapital;

    private Boolean reconstitutionCapitauxPropres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_origine_fusion_scission")
    OrigineFusionScission origineFusionScission;

    private LocalDate dateEffet;

    private Boolean is12MTriggered;

    private Boolean is10MTriggered;

    private Boolean is16MTriggered;

    private Boolean is18MTriggered;

    private Boolean is15MTriggered;

    private Boolean is17MTriggered;

    private Boolean is19MTriggered;

    private LocalDate dateEffet12M;

    private LocalDate dateEffet10M;

    private LocalDate dateEffet16M;

    private LocalDate dateEffet18M;

    private LocalDate dateEffet15M;

    private LocalDate dateEffet17M;

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

    public String getDateClotureExerciceSocial() {
        return dateClotureExerciceSocial;
    }

    public void setDateClotureExerciceSocial(String dateClotureExerciceSocial) {
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

    public BigDecimal getMontantCapital() {
        return montantCapital;
    }

    public void setMontantCapital(BigDecimal montantCapital) {
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
