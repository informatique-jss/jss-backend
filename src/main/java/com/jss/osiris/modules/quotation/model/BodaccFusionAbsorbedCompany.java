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
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;

@Entity
public class BodaccFusionAbsorbedCompany implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "bodacc_sequence", sequenceName = "bodacc_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
	private Integer id;

	@Column(length = 60)
	private String absorbedCompanyDenomination;

	@Column(length = 9)
	private String absorbedCompanySiren;

	@Column(length = 100)
	private String absorbedCompanyAddress;

	private Integer absorbedCompanyShareCapital;

	@ManyToOne
	@JoinColumn(name = "id_legal_form_absorbed_company")
	private LegalForm absorbedCompanyLegalForm;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate absorbedCompanyRcsDeclarationDate;

	@ManyToOne
	@JoinColumn(name = "id_absorbed_company_rcs_competent_authority")
	private CompetentAuthority absorbedCompanyRcsCompetentAuthority;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public LocalDate getAbsorbedCompanyRcsDeclarationDate() {
		return absorbedCompanyRcsDeclarationDate;
	}

	public void setAbsorbedCompanyRcsDeclarationDate(LocalDate absorbedCompanyRcsDeclarationDate) {
		this.absorbedCompanyRcsDeclarationDate = absorbedCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getAbsorbedCompanyRcsCompetentAuthority() {
		return absorbedCompanyRcsCompetentAuthority;
	}

	public void setAbsorbedCompanyRcsCompetentAuthority(CompetentAuthority absorbedCompanyRcsCompetentAuthority) {
		this.absorbedCompanyRcsCompetentAuthority = absorbedCompanyRcsCompetentAuthority;
	}

}
