package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;

@Entity
public class BodaccSplit implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 60, nullable = false)
	private String beneficiaryCompanyDenomination;

	@Column(length = 9, nullable = false)
	private String beneficiaryCompanySiren;

	@Column(length = 100, nullable = false)
	private String beneficiaryCompanyAddress;

	private Integer beneficiaryCompanyShareCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_beneficiary_company")
	private LegalForm beneficiaryCompanyLegalForm;

	@Column(nullable = false)
	private Date beneficiaryCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_beneficiary_company_rcs_competent_authority")
	private CompetentAuthority beneficiaryCompanyRcsCompetentAuthority;

	@Column(length = 60, nullable = false)
	private String splitCompanyDenomination;

	@Column(length = 9, nullable = false)
	private String splitCompanySiren;

	@Column(length = 100, nullable = false)
	private String splitCompanyAddress;

	private Integer splitCompanyShareCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_split_company")
	private LegalForm splitCompanyLegalForm;

	@Column(nullable = false)
	private Date splitCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_split_company_rcs_competent_authority")
	private CompetentAuthority splitCompanyRcsCompetentAuthority;

	@Column(nullable = false)
	private Integer assets;

	@Column(nullable = false)
	private Integer liabilities;

	@Column(nullable = false)
	private Integer splitBonus;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String exchangeRatioReport;

	@Column(nullable = false)
	private Date splitProjectDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBeneficiaryCompanyDenomination() {
		return beneficiaryCompanyDenomination;
	}

	public void setBeneficiaryCompanyDenomination(String beneficiaryCompanyDenomination) {
		this.beneficiaryCompanyDenomination = beneficiaryCompanyDenomination;
	}

	public String getBeneficiaryCompanySiren() {
		return beneficiaryCompanySiren;
	}

	public void setBeneficiaryCompanySiren(String beneficiaryCompanySiren) {
		this.beneficiaryCompanySiren = beneficiaryCompanySiren;
	}

	public String getBeneficiaryCompanyAddress() {
		return beneficiaryCompanyAddress;
	}

	public void setBeneficiaryCompanyAddress(String beneficiaryCompanyAddress) {
		this.beneficiaryCompanyAddress = beneficiaryCompanyAddress;
	}

	public Integer getBeneficiaryCompanyShareCapital() {
		return beneficiaryCompanyShareCapital;
	}

	public void setBeneficiaryCompanyShareCapital(Integer beneficiaryCompanyShareCapital) {
		this.beneficiaryCompanyShareCapital = beneficiaryCompanyShareCapital;
	}

	public LegalForm getBeneficiaryCompanyLegalForm() {
		return beneficiaryCompanyLegalForm;
	}

	public void setBeneficiaryCompanyLegalForm(LegalForm beneficiaryCompanyLegalForm) {
		this.beneficiaryCompanyLegalForm = beneficiaryCompanyLegalForm;
	}

	public Date getBeneficiaryCompanyRcsDeclarationDate() {
		return beneficiaryCompanyRcsDeclarationDate;
	}

	public void setBeneficiaryCompanyRcsDeclarationDate(Date beneficiaryCompanyRcsDeclarationDate) {
		this.beneficiaryCompanyRcsDeclarationDate = beneficiaryCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getBeneficiaryCompanyRcsCompetentAuthority() {
		return beneficiaryCompanyRcsCompetentAuthority;
	}

	public void setBeneficiaryCompanyRcsCompetentAuthority(CompetentAuthority beneficiaryCompanyRcsCompetentAuthority) {
		this.beneficiaryCompanyRcsCompetentAuthority = beneficiaryCompanyRcsCompetentAuthority;
	}

	public String getSplitCompanyDenomination() {
		return splitCompanyDenomination;
	}

	public void setSplitCompanyDenomination(String splitCompanyDenomination) {
		this.splitCompanyDenomination = splitCompanyDenomination;
	}

	public String getSplitCompanySiren() {
		return splitCompanySiren;
	}

	public void setSplitCompanySiren(String splitCompanySiren) {
		this.splitCompanySiren = splitCompanySiren;
	}

	public String getSplitCompanyAddress() {
		return splitCompanyAddress;
	}

	public void setSplitCompanyAddress(String splitCompanyAddress) {
		this.splitCompanyAddress = splitCompanyAddress;
	}

	public Integer getSplitCompanyShareCapital() {
		return splitCompanyShareCapital;
	}

	public void setSplitCompanyShareCapital(Integer splitCompanyShareCapital) {
		this.splitCompanyShareCapital = splitCompanyShareCapital;
	}

	public LegalForm getSplitCompanyLegalForm() {
		return splitCompanyLegalForm;
	}

	public void setSplitCompanyLegalForm(LegalForm splitCompanyLegalForm) {
		this.splitCompanyLegalForm = splitCompanyLegalForm;
	}

	public Date getSplitCompanyRcsDeclarationDate() {
		return splitCompanyRcsDeclarationDate;
	}

	public void setSplitCompanyRcsDeclarationDate(Date splitCompanyRcsDeclarationDate) {
		this.splitCompanyRcsDeclarationDate = splitCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getSplitCompanyRcsCompetentAuthority() {
		return splitCompanyRcsCompetentAuthority;
	}

	public void setSplitCompanyRcsCompetentAuthority(CompetentAuthority splitCompanyRcsCompetentAuthority) {
		this.splitCompanyRcsCompetentAuthority = splitCompanyRcsCompetentAuthority;
	}

	public Integer getAssets() {
		return assets;
	}

	public void setAssets(Integer assets) {
		this.assets = assets;
	}

	public Integer getLiabilities() {
		return liabilities;
	}

	public void setLiabilities(Integer liabilities) {
		this.liabilities = liabilities;
	}

	public Integer getSplitBonus() {
		return splitBonus;
	}

	public void setSplitBonus(Integer splitBonus) {
		this.splitBonus = splitBonus;
	}

	public String getExchangeRatioReport() {
		return exchangeRatioReport;
	}

	public void setExchangeRatioReport(String exchangeRatioReport) {
		this.exchangeRatioReport = exchangeRatioReport;
	}

	public Date getSplitProjectDate() {
		return splitProjectDate;
	}

	public void setSplitProjectDate(Date splitProjectDate) {
		this.splitProjectDate = splitProjectDate;
	}

}
