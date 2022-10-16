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
public class BodaccFusionMergingCompany implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
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
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate mergingCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_merging_company_rcs_competent_authority")
	private CompetentAuthority mergingCompanyRcsCompetentAuthority;

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

	public LocalDate getMergingCompanyRcsDeclarationDate() {
		return mergingCompanyRcsDeclarationDate;
	}

	public void setMergingCompanyRcsDeclarationDate(LocalDate mergingCompanyRcsDeclarationDate) {
		this.mergingCompanyRcsDeclarationDate = mergingCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getMergingCompanyRcsCompetentAuthority() {
		return mergingCompanyRcsCompetentAuthority;
	}

	public void setMergingCompanyRcsCompetentAuthority(CompetentAuthority mergingCompanyRcsCompetentAuthority) {
		this.mergingCompanyRcsCompetentAuthority = mergingCompanyRcsCompetentAuthority;
	}

}
