package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class CompetentAuthority implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@ManyToOne
	@JoinColumn(name = "id_competent_authority_type")
	private CompetentAuthorityType competentAuthorityType;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_department", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_payment_type", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_payment_type"))
	private List<PaymentType> paymentTypes;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_mail", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_competent_authority_phone", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 40)
	private String iban;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<PaymentType> getPaymentTypes() {
		return paymentTypes;
	}

	public void setPaymentTypes(List<PaymentType> paymentTypes) {
		this.paymentTypes = paymentTypes;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public CompetentAuthorityType getCompetentAuthorityType() {
		return competentAuthorityType;
	}

	public void setCompetentAuthorityType(CompetentAuthorityType competentAuthorityType) {
		this.competentAuthorityType = competentAuthorityType;
	}

}
