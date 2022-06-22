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
public class BodaccFusion implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 60, nullable = false)
	private String mergingCompanyDenomination;

	@Column(length = 9, nullable = false)
	private String mergingCompanySiren;

	@Column(length = 100, nullable = false)
	private String mergingCompanyAddress;

	private Integer mergingCompanyShareCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_merging_company")
	private LegalForm mergingCompanyLegalForm;

	@Column(nullable = false)
	private Date mergingCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_merging_company_rcs_competent_authority")
	private CompetentAuthority mergingCompanyRcsCompetentAuthority;

	@Column(length = 60, nullable = false)
	private String absorbedCompanyDenomination;

	@Column(length = 9, nullable = false)
	private String absorbedCompanySiren;

	@Column(length = 100, nullable = false)
	private String absorbedCompanyAddress;

	private Integer absorbedCompanyShareCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_absorbed_company")
	private LegalForm absorbedCompanyLegalForm;

	@Column(nullable = false)
	private Date absorbedCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_absorbed_company_rcs_competent_authority")
	private CompetentAuthority absorbedCompanyRcsCompetentAuthority;

	@Column(nullable = false)
	private Integer assets;

	@Column(nullable = false)
	private Integer liabilities;

	@Column(nullable = false)
	private Integer mergerBonus;

	@Column(columnDefinition = "TEXT", nullable = false)
	private Date exchangeRatioReport;

	@Column(nullable = false)
	private Date mergingProjectDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMergingCompanyDenomination() {
		return mergingCompanyDenomination;
	}

	public void setMergingCompanyDenomination(String mergingCompanyDenomination) {
		this.mergingCompanyDenomination = mergingCompanyDenomination;
	}

	public String getMergingCompanySiren() {
		return mergingCompanySiren;
	}

	public void setMergingCompanySiren(String mergingCompanySiren) {
		this.mergingCompanySiren = mergingCompanySiren;
	}

	public String getMergingCompanyAddress() {
		return mergingCompanyAddress;
	}

	public void setMergingCompanyAddress(String mergingCompanyAddress) {
		this.mergingCompanyAddress = mergingCompanyAddress;
	}

	public Integer getMergingCompanyShareCapital() {
		return mergingCompanyShareCapital;
	}

	public void setMergingCompanyShareCapital(Integer mergingCompanyShareCapital) {
		this.mergingCompanyShareCapital = mergingCompanyShareCapital;
	}

	public LegalForm getMergingCompanyLegalForm() {
		return mergingCompanyLegalForm;
	}

	public void setMergingCompanyLegalForm(LegalForm mergingCompanyLegalForm) {
		this.mergingCompanyLegalForm = mergingCompanyLegalForm;
	}

	public Date getMergingCompanyRcsDeclarationDate() {
		return mergingCompanyRcsDeclarationDate;
	}

	public void setMergingCompanyRcsDeclarationDate(Date mergingCompanyRcsDeclarationDate) {
		this.mergingCompanyRcsDeclarationDate = mergingCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getMergingCompanyRcsCompetentAuthority() {
		return mergingCompanyRcsCompetentAuthority;
	}

	public void setMergingCompanyRcsCompetentAuthority(CompetentAuthority mergingCompanyRcsCompetentAuthority) {
		this.mergingCompanyRcsCompetentAuthority = mergingCompanyRcsCompetentAuthority;
	}

	public String getAbsorbedCompanyDenomination() {
		return absorbedCompanyDenomination;
	}

	public void setAbsorbedCompanyDenomination(String absorbedCompanyDenomination) {
		this.absorbedCompanyDenomination = absorbedCompanyDenomination;
	}

	public String getAbsorbedCompanySiren() {
		return absorbedCompanySiren;
	}

	public void setAbsorbedCompanySiren(String absorbedCompanySiren) {
		this.absorbedCompanySiren = absorbedCompanySiren;
	}

	public String getAbsorbedCompanyAddress() {
		return absorbedCompanyAddress;
	}

	public void setAbsorbedCompanyAddress(String absorbedCompanyAddress) {
		this.absorbedCompanyAddress = absorbedCompanyAddress;
	}

	public Integer getAbsorbedCompanyShareCapital() {
		return absorbedCompanyShareCapital;
	}

	public void setAbsorbedCompanyShareCapital(Integer absorbedCompanyShareCapital) {
		this.absorbedCompanyShareCapital = absorbedCompanyShareCapital;
	}

	public LegalForm getAbsorbedCompanyLegalForm() {
		return absorbedCompanyLegalForm;
	}

	public void setAbsorbedCompanyLegalForm(LegalForm absorbedCompanyLegalForm) {
		this.absorbedCompanyLegalForm = absorbedCompanyLegalForm;
	}

	public Date getAbsorbedCompanyRcsDeclarationDate() {
		return absorbedCompanyRcsDeclarationDate;
	}

	public void setAbsorbedCompanyRcsDeclarationDate(Date absorbedCompanyRcsDeclarationDate) {
		this.absorbedCompanyRcsDeclarationDate = absorbedCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getAbsorbedCompanyRcsCompetentAuthority() {
		return absorbedCompanyRcsCompetentAuthority;
	}

	public void setAbsorbedCompanyRcsCompetentAuthority(CompetentAuthority absorbedCompanyRcsCompetentAuthority) {
		this.absorbedCompanyRcsCompetentAuthority = absorbedCompanyRcsCompetentAuthority;
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

	public Integer getMergerBonus() {
		return mergerBonus;
	}

	public void setMergerBonus(Integer mergerBonus) {
		this.mergerBonus = mergerBonus;
	}

	public Date getExchangeRatioReport() {
		return exchangeRatioReport;
	}

	public void setExchangeRatioReport(Date exchangeRatioReport) {
		this.exchangeRatioReport = exchangeRatioReport;
	}

	public Date getMergingProjectDate() {
		return mergingProjectDate;
	}

	public void setMergingProjectDate(Date mergingProjectDate) {
		this.mergingProjectDate = mergingProjectDate;
	}

}
