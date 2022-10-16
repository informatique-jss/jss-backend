package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;

@Entity
public class BodaccSplitBeneficiary implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
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
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate beneficiaryCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_beneficiary_company_rcs_competent_authority")
	private CompetentAuthority beneficiaryCompanyRcsCompetentAuthority;

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

	public LocalDate getBeneficiaryCompanyRcsDeclarationDate() {
		return beneficiaryCompanyRcsDeclarationDate;
	}

	public void setBeneficiaryCompanyRcsDeclarationDate(LocalDate beneficiaryCompanyRcsDeclarationDate) {
		this.beneficiaryCompanyRcsDeclarationDate = beneficiaryCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getBeneficiaryCompanyRcsCompetentAuthority() {
		return beneficiaryCompanyRcsCompetentAuthority;
	}

	public void setBeneficiaryCompanyRcsCompetentAuthority(CompetentAuthority beneficiaryCompanyRcsCompetentAuthority) {
		this.beneficiaryCompanyRcsCompetentAuthority = beneficiaryCompanyRcsCompetentAuthority;
	}

}
