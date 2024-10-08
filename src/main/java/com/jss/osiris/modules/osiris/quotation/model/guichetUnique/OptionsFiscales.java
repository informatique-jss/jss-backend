package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.ConditionVersementTVA;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.DeviseCapital;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OptionParticuliereRegimeBenefi;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.PeriodiciteEtOptionsParticulie;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.PeriodiciteVersement;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RegimeImpositionBenefices;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RegimeImpositionBenefices2;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RegimeImpositionTVA;

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
public class OptionsFiscales implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @Column()
    private Boolean indicateurMembreExploitation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_regime_imposition_benefices")
    RegimeImpositionBenefices regimeImpositionBenefices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_option_particuliere_regime_benefi")
    OptionParticuliereRegimeBenefi optionParticuliereRegimeBenefice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_regime_imposition_tva")
    RegimeImpositionTVA regimeImpositionTVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodicite_et_options_particulie")
    PeriodiciteEtOptionsParticulie periodiciteEtOptionsParticulieresTVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condition_versement_tva")
    ConditionVersementTVA conditionVersementTVA;

    private String dateClotureExerciceComptable;

    @Column()
    private Boolean optionVersementLiberatoire;

    @Column(length = 255)
    private String lieuImposition;

    @Column()
    private LocalDate dateEnregistrementStatuts;

    @Column()
    private LocalDate dateEffetFiscalite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_regime_imposition_benefices2")
    RegimeImpositionBenefices2 regimeImpositionBenefices2;

    @OneToMany(mappedBy = "optionsFiscales")
    @JsonIgnoreProperties(value = { "optionsFiscales" }, allowSetters = true)
    List<Immeuble> immeubles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_option_particuliere_regime_benefi2")
    OptionParticuliereRegimeBenefi optionParticuliereRegimeBenefice2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_regime_imposition_tva2")
    RegimeImpositionTVA regimeImpositionTVA2;

    @Column()
    private LocalDate dateEffet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodicite_et_options_particulieres_tva2")
    PeriodiciteEtOptionsParticulie periodiciteEtOptionsParticulieresTVA2;

    @Column()
    private Boolean taxeTroisPourcent;

    @Column()
    private Boolean detentionParticipationSocieteFrancaise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodicite_versement")
    PeriodiciteVersement periodiciteVersement;

    @Column()
    private Integer chiffreAffairePrevisionnelVente;

    @Column()
    private Integer chiffreAffairePrevisionnelService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devise_capital")
    DeviseCapital deviseChiffreAffaire;

    @Column(length = 255)
    private String numeroTVAIntra;

    @Column()
    private Boolean redevableTVA;

    @Column()
    private Boolean redevablePAS;

    @Column(length = 255)
    private String numeroTVAFrance;

    @Column()
    private Boolean clienteleIdentifieesTVA;

    @Column()
    private Boolean clienteleParticuliers;

    @Column()
    private Boolean clienteleAutre;

    @Column()
    private Boolean ali;

    @Column()
    private Boolean aic;

    @Column()
    private Boolean lic;

    @Column()
    private Boolean indicateurAutreOptionFiscale;

    @Column(length = 255)
    private String autreOptionFiscale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condition_versement_tva2")
    ConditionVersementTVA conditionVersementTVA2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIndicateurMembreExploitation() {
        return indicateurMembreExploitation;
    }

    public void setIndicateurMembreExploitation(Boolean indicateurMembreExploitation) {
        this.indicateurMembreExploitation = indicateurMembreExploitation;
    }

    public RegimeImpositionBenefices getRegimeImpositionBenefices() {
        return regimeImpositionBenefices;
    }

    public void setRegimeImpositionBenefices(RegimeImpositionBenefices regimeImpositionBenefices) {
        this.regimeImpositionBenefices = regimeImpositionBenefices;
    }

    public OptionParticuliereRegimeBenefi getOptionParticuliereRegimeBenefice() {
        return optionParticuliereRegimeBenefice;
    }

    public void setOptionParticuliereRegimeBenefice(OptionParticuliereRegimeBenefi optionParticuliereRegimeBenefice) {
        this.optionParticuliereRegimeBenefice = optionParticuliereRegimeBenefice;
    }

    public RegimeImpositionTVA getRegimeImpositionTVA() {
        return regimeImpositionTVA;
    }

    public void setRegimeImpositionTVA(RegimeImpositionTVA regimeImpositionTVA) {
        this.regimeImpositionTVA = regimeImpositionTVA;
    }

    public PeriodiciteEtOptionsParticulie getPeriodiciteEtOptionsParticulieresTVA() {
        return periodiciteEtOptionsParticulieresTVA;
    }

    public void setPeriodiciteEtOptionsParticulieresTVA(
            PeriodiciteEtOptionsParticulie periodiciteEtOptionsParticulieresTVA) {
        this.periodiciteEtOptionsParticulieresTVA = periodiciteEtOptionsParticulieresTVA;
    }

    public ConditionVersementTVA getConditionVersementTVA() {
        return conditionVersementTVA;
    }

    public void setConditionVersementTVA(ConditionVersementTVA conditionVersementTVA) {
        this.conditionVersementTVA = conditionVersementTVA;
    }

    public Boolean getOptionVersementLiberatoire() {
        return optionVersementLiberatoire;
    }

    public void setOptionVersementLiberatoire(Boolean optionVersementLiberatoire) {
        this.optionVersementLiberatoire = optionVersementLiberatoire;
    }

    public String getLieuImposition() {
        return lieuImposition;
    }

    public void setLieuImposition(String lieuImposition) {
        this.lieuImposition = lieuImposition;
    }

    public LocalDate getDateEnregistrementStatuts() {
        return dateEnregistrementStatuts;
    }

    public void setDateEnregistrementStatuts(LocalDate dateEnregistrementStatuts) {
        this.dateEnregistrementStatuts = dateEnregistrementStatuts;
    }

    public LocalDate getDateEffetFiscalite() {
        return dateEffetFiscalite;
    }

    public void setDateEffetFiscalite(LocalDate dateEffetFiscalite) {
        this.dateEffetFiscalite = dateEffetFiscalite;
    }

    public RegimeImpositionBenefices2 getRegimeImpositionBenefices2() {
        return regimeImpositionBenefices2;
    }

    public void setRegimeImpositionBenefices2(RegimeImpositionBenefices2 regimeImpositionBenefices2) {
        this.regimeImpositionBenefices2 = regimeImpositionBenefices2;
    }

    public OptionParticuliereRegimeBenefi getOptionParticuliereRegimeBenefice2() {
        return optionParticuliereRegimeBenefice2;
    }

    public void setOptionParticuliereRegimeBenefice2(OptionParticuliereRegimeBenefi optionParticuliereRegimeBenefice2) {
        this.optionParticuliereRegimeBenefice2 = optionParticuliereRegimeBenefice2;
    }

    public RegimeImpositionTVA getRegimeImpositionTVA2() {
        return regimeImpositionTVA2;
    }

    public void setRegimeImpositionTVA2(RegimeImpositionTVA regimeImpositionTVA2) {
        this.regimeImpositionTVA2 = regimeImpositionTVA2;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public PeriodiciteEtOptionsParticulie getPeriodiciteEtOptionsParticulieresTVA2() {
        return periodiciteEtOptionsParticulieresTVA2;
    }

    public void setPeriodiciteEtOptionsParticulieresTVA2(
            PeriodiciteEtOptionsParticulie periodiciteEtOptionsParticulieresTVA2) {
        this.periodiciteEtOptionsParticulieresTVA2 = periodiciteEtOptionsParticulieresTVA2;
    }

    public Boolean getTaxeTroisPourcent() {
        return taxeTroisPourcent;
    }

    public void setTaxeTroisPourcent(Boolean taxeTroisPourcent) {
        this.taxeTroisPourcent = taxeTroisPourcent;
    }

    public Boolean getDetentionParticipationSocieteFrancaise() {
        return detentionParticipationSocieteFrancaise;
    }

    public void setDetentionParticipationSocieteFrancaise(Boolean detentionParticipationSocieteFrancaise) {
        this.detentionParticipationSocieteFrancaise = detentionParticipationSocieteFrancaise;
    }

    public PeriodiciteVersement getPeriodiciteVersement() {
        return periodiciteVersement;
    }

    public void setPeriodiciteVersement(PeriodiciteVersement periodiciteVersement) {
        this.periodiciteVersement = periodiciteVersement;
    }

    public Integer getChiffreAffairePrevisionnelVente() {
        return chiffreAffairePrevisionnelVente;
    }

    public void setChiffreAffairePrevisionnelVente(Integer chiffreAffairePrevisionnelVente) {
        this.chiffreAffairePrevisionnelVente = chiffreAffairePrevisionnelVente;
    }

    public Integer getChiffreAffairePrevisionnelService() {
        return chiffreAffairePrevisionnelService;
    }

    public void setChiffreAffairePrevisionnelService(Integer chiffreAffairePrevisionnelService) {
        this.chiffreAffairePrevisionnelService = chiffreAffairePrevisionnelService;
    }

    public DeviseCapital getDeviseChiffreAffaire() {
        return deviseChiffreAffaire;
    }

    public void setDeviseChiffreAffaire(DeviseCapital deviseChiffreAffaire) {
        this.deviseChiffreAffaire = deviseChiffreAffaire;
    }

    public String getNumeroTVAIntra() {
        return numeroTVAIntra;
    }

    public void setNumeroTVAIntra(String numeroTVAIntra) {
        this.numeroTVAIntra = numeroTVAIntra;
    }

    public Boolean getRedevableTVA() {
        return redevableTVA;
    }

    public void setRedevableTVA(Boolean redevableTVA) {
        this.redevableTVA = redevableTVA;
    }

    public Boolean getRedevablePAS() {
        return redevablePAS;
    }

    public void setRedevablePAS(Boolean redevablePAS) {
        this.redevablePAS = redevablePAS;
    }

    public String getNumeroTVAFrance() {
        return numeroTVAFrance;
    }

    public void setNumeroTVAFrance(String numeroTVAFrance) {
        this.numeroTVAFrance = numeroTVAFrance;
    }

    public Boolean getClienteleIdentifieesTVA() {
        return clienteleIdentifieesTVA;
    }

    public void setClienteleIdentifieesTVA(Boolean clienteleIdentifieesTVA) {
        this.clienteleIdentifieesTVA = clienteleIdentifieesTVA;
    }

    public Boolean getClienteleParticuliers() {
        return clienteleParticuliers;
    }

    public void setClienteleParticuliers(Boolean clienteleParticuliers) {
        this.clienteleParticuliers = clienteleParticuliers;
    }

    public Boolean getClienteleAutre() {
        return clienteleAutre;
    }

    public void setClienteleAutre(Boolean clienteleAutre) {
        this.clienteleAutre = clienteleAutre;
    }

    public Boolean getAli() {
        return ali;
    }

    public void setAli(Boolean ali) {
        this.ali = ali;
    }

    public Boolean getAic() {
        return aic;
    }

    public void setAic(Boolean aic) {
        this.aic = aic;
    }

    public Boolean getLic() {
        return lic;
    }

    public void setLic(Boolean lic) {
        this.lic = lic;
    }

    public Boolean getIndicateurAutreOptionFiscale() {
        return indicateurAutreOptionFiscale;
    }

    public void setIndicateurAutreOptionFiscale(Boolean indicateurAutreOptionFiscale) {
        this.indicateurAutreOptionFiscale = indicateurAutreOptionFiscale;
    }

    public String getAutreOptionFiscale() {
        return autreOptionFiscale;
    }

    public void setAutreOptionFiscale(String autreOptionFiscale) {
        this.autreOptionFiscale = autreOptionFiscale;
    }

    public ConditionVersementTVA getConditionVersementTVA2() {
        return conditionVersementTVA2;
    }

    public void setConditionVersementTVA2(ConditionVersementTVA conditionVersementTVA2) {
        this.conditionVersementTVA2 = conditionVersementTVA2;
    }

    public List<Immeuble> getImmeubles() {
        return immeubles;
    }

    public void setImmeubles(List<Immeuble> immeubles) {
        this.immeubles = immeubles;
    }

    public String getDateClotureExerciceComptable() {
        return dateClotureExerciceComptable;
    }

    public void setDateClotureExerciceComptable(String dateClotureExerciceComptable) {
        this.dateClotureExerciceComptable = dateClotureExerciceComptable;
    }

}
