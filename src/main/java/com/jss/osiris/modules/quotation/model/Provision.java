package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Provision implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_quotation")
	@JsonBackReference("quotation")
	private Quotation quotation;

	@ManyToOne
	@JoinColumn(name = "id_provision_type")
	private ProvisionType provisionType;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type")
	private ProvisionFamilyType provisionFamilyType;

	@OneToOne
	@JoinColumn(name = "id_affaire")
	private Affaire affaire;

	@OneToOne(targetEntity = Domiciliation.class, mappedBy = "provision", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("provision")
	private Domiciliation domiciliation;

	@OneToOne(targetEntity = Shal.class, mappedBy = "provision", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("provision")
	private Shal shal;

	@OneToOne(targetEntity = Bodacc.class, mappedBy = "provision", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("provision")
	private Bodacc bodacc;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public Affaire getAffaire() {
		return affaire;
	}

	public void setAffaire(Affaire affaire) {
		this.affaire = affaire;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public ProvisionType getProvisionType() {
		return provisionType;
	}

	public void setProvisionType(ProvisionType provisionType) {
		this.provisionType = provisionType;
	}

	public ProvisionFamilyType getProvisionFamilyType() {
		return provisionFamilyType;
	}

	public void setProvisionFamilyType(ProvisionFamilyType provisionFamilyType) {
		this.provisionFamilyType = provisionFamilyType;
	}

	public Domiciliation getDomiciliation() {
		return domiciliation;
	}

	public void setDomiciliation(Domiciliation domiciliation) {
		this.domiciliation = domiciliation;
	}

	public Shal getShal() {
		return shal;
	}

	public void setShal(Shal shal) {
		this.shal = shal;
	}

	public Bodacc getBodacc() {
		return bodacc;
	}

	public void setBodacc(Bodacc bodacc) {
		this.bodacc = bodacc;
	}

}
