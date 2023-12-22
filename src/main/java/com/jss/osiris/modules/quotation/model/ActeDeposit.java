package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class ActeDeposit implements Serializable, IId {
	@Id
	@SequenceGenerator(name = "acte_deposit_sequence", sequenceName = "acte_deposit_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acte_deposit_sequence")
	private Integer id;

	private Boolean isReductionCapitalSocial;
	private Boolean isTransformation;
	private Boolean isFusionScission;
	private Boolean isFusionTransfrontaliere;
	private Boolean isApportPartiel;
	private Boolean isCessation;
	private Boolean isSocialPartsCession;
	private Boolean isSocialPartsDonation;
	private Boolean isMandateExtension;
	private Boolean isFusion;
	private Boolean isScission;
	private Boolean isAnnualAccountRefusal;
	private Boolean isBylawsAmendments;
	private Boolean isOrder;
	private Boolean isLiquidatorReport;
	private Boolean isContratAppui;
	private Boolean isInformationConjoint;
	private Boolean isAffectationPatrimoine;
	private Boolean isDelegationPower;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getIsReductionCapitalSocial() {
		return isReductionCapitalSocial;
	}

	public void setIsReductionCapitalSocial(Boolean isReductionCapitalSocial) {
		this.isReductionCapitalSocial = isReductionCapitalSocial;
	}

	public Boolean getIsTransformation() {
		return isTransformation;
	}

	public void setIsTransformation(Boolean isTransformation) {
		this.isTransformation = isTransformation;
	}

	public Boolean getIsFusionScission() {
		return isFusionScission;
	}

	public void setIsFusionScission(Boolean isFusionScission) {
		this.isFusionScission = isFusionScission;
	}

	public Boolean getIsFusionTransfrontaliere() {
		return isFusionTransfrontaliere;
	}

	public void setIsFusionTransfrontaliere(Boolean isFusionTransfrontaliere) {
		this.isFusionTransfrontaliere = isFusionTransfrontaliere;
	}

	public Boolean getIsApportPartiel() {
		return isApportPartiel;
	}

	public void setIsApportPartiel(Boolean isApportPartiel) {
		this.isApportPartiel = isApportPartiel;
	}

	public Boolean getIsCessation() {
		return isCessation;
	}

	public void setIsCessation(Boolean isCessation) {
		this.isCessation = isCessation;
	}

	public Boolean getIsSocialPartsCession() {
		return isSocialPartsCession;
	}

	public void setIsSocialPartsCession(Boolean isSocialPartsCession) {
		this.isSocialPartsCession = isSocialPartsCession;
	}

	public Boolean getIsSocialPartsDonation() {
		return isSocialPartsDonation;
	}

	public void setIsSocialPartsDonation(Boolean isSocialPartsDonation) {
		this.isSocialPartsDonation = isSocialPartsDonation;
	}

	public Boolean getIsMandateExtension() {
		return isMandateExtension;
	}

	public void setIsMandateExtension(Boolean isMandateExtension) {
		this.isMandateExtension = isMandateExtension;
	}

	public Boolean getIsFusion() {
		return isFusion;
	}

	public void setIsFusion(Boolean isFusion) {
		this.isFusion = isFusion;
	}

	public Boolean getIsScission() {
		return isScission;
	}

	public void setIsScission(Boolean isScission) {
		this.isScission = isScission;
	}

	public Boolean getIsAnnualAccountRefusal() {
		return isAnnualAccountRefusal;
	}

	public void setIsAnnualAccountRefusal(Boolean isAnnualAccountRefusal) {
		this.isAnnualAccountRefusal = isAnnualAccountRefusal;
	}

	public Boolean getIsBylawsAmendments() {
		return isBylawsAmendments;
	}

	public void setIsBylawsAmendments(Boolean isBylawsAmendments) {
		this.isBylawsAmendments = isBylawsAmendments;
	}

	public Boolean getIsOrder() {
		return isOrder;
	}

	public void setIsOrder(Boolean isOrder) {
		this.isOrder = isOrder;
	}

	public Boolean getIsLiquidatorReport() {
		return isLiquidatorReport;
	}

	public void setIsLiquidatorReport(Boolean isLiquidatorReport) {
		this.isLiquidatorReport = isLiquidatorReport;
	}

	public Boolean getIsContratAppui() {
		return isContratAppui;
	}

	public void setIsContratAppui(Boolean isContratAppui) {
		this.isContratAppui = isContratAppui;
	}

	public Boolean getIsInformationConjoint() {
		return isInformationConjoint;
	}

	public void setIsInformationConjoint(Boolean isInformationConjoint) {
		this.isInformationConjoint = isInformationConjoint;
	}

	public Boolean getIsAffectationPatrimoine() {
		return isAffectationPatrimoine;
	}

	public void setIsAffectationPatrimoine(Boolean isAffectationPatrimoine) {
		this.isAffectationPatrimoine = isAffectationPatrimoine;
	}

	public Boolean getIsDelegationPower() {
		return isDelegationPower;
	}

	public void setIsDelegationPower(Boolean isDelegationPower) {
		this.isDelegationPower = isDelegationPower;
	}
}
