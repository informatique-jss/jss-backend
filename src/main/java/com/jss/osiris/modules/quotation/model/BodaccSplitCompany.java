package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class BodaccSplitCompany implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "bodacc_sequence", sequenceName = "bodacc_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
	private Integer id;

	@Column(length = 60)
	private String splitCompanyDenomination;

	@Column(length = 9)
	private String splitCompanySiren;

	@Column(length = 100)
	private String splitCompanyAddress;

	private Integer splitCompanyShareCapital;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_legal_form_split_company")
	private LegalForm splitCompanyLegalForm;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate splitCompanyRcsDeclarationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_split_company_rcs_competent_authority")
	private CompetentAuthority splitCompanyRcsCompetentAuthority;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public LocalDate getSplitCompanyRcsDeclarationDate() {
		return splitCompanyRcsDeclarationDate;
	}

	public void setSplitCompanyRcsDeclarationDate(LocalDate splitCompanyRcsDeclarationDate) {
		this.splitCompanyRcsDeclarationDate = splitCompanyRcsDeclarationDate;
	}

	public CompetentAuthority getSplitCompanyRcsCompetentAuthority() {
		return splitCompanyRcsCompetentAuthority;
	}

	public void setSplitCompanyRcsCompetentAuthority(CompetentAuthority splitCompanyRcsCompetentAuthority) {
		this.splitCompanyRcsCompetentAuthority = splitCompanyRcsCompetentAuthority;
	}

}
