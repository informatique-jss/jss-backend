package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Bodacc implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_provision")
	@JsonBackReference("provision")
	private Provision provision;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type")
	private BodaccPublicationType bodaccPublicationType;

	@ManyToOne
	@JoinColumn(name = "id_transfert_funds_type")
	private TransfertFundsType transfertFundsType;

	@Column(nullable = false)
	private Boolean isDoublePublication;

	@ManyToOne
	@JoinColumn(name = "id_department")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority")
	private CompetentAuthority competentAuthority;

	public Integer getId() {
		return id;
	}

	public Department getDepartment() {
		return department;
	}

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getIsDoublePublication() {
		return isDoublePublication;
	}

	public void setIsDoublePublication(Boolean isDoublePublication) {
		this.isDoublePublication = isDoublePublication;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public BodaccPublicationType getBodaccPublicationType() {
		return bodaccPublicationType;
	}

	public TransfertFundsType getTransfertFundsType() {
		return transfertFundsType;
	}

	public void setTransfertFundsType(TransfertFundsType transfertFundsType) {
		this.transfertFundsType = transfertFundsType;
	}

	public void setBodaccPublicationType(BodaccPublicationType bodaccPublicationType) {
		this.bodaccPublicationType = bodaccPublicationType;
	}

}
