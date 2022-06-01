package com.jss.jssbackend.modules.quotation.model;

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

import com.jss.jssbackend.libs.search.model.IndexedField;
import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.model.LegalForm;
import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.model.Phone;

@Entity
public class Affaire implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_civility")
	private Civility civility;

	@Column(length = 60)
	private String denomination;

	private Boolean isIndividual;

	@Column(length = 20)
	private String firstname;

	@Column(length = 20)
	private String lastname;

	@ManyToOne
	@JoinColumn(name = "id_legal_form")
	private LegalForm legalForm;

	@Column(length = 9)
	private String siren;

	@Column(length = 14)
	private String siret;

	@Column(length = 10)
	private String rna;

	@Column(length = 10)
	@IndexedField
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "id_city")
	@IndexedField
	private City city;

	@ManyToOne
	@JoinColumn(name = "id_country")
	private Country country;

	@Column(length = 60, nullable = false)
	@IndexedField
	private String address;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_affaire_mail", joinColumns = @JoinColumn(name = "id_affaire"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_affaire_phone", joinColumns = @JoinColumn(name = "id_affaire"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 60)
	private String externalReference;

	@Column(columnDefinition = "TEXT")
	private String observations;

	private Float shareCapital;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public Boolean getIsIndividual() {
		return isIndividual;
	}

	public void setIsIndividual(Boolean isIndividual) {
		this.isIndividual = isIndividual;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
	}

	public LegalForm getLegalForm() {
		return legalForm;
	}

	public void setLegalForm(LegalForm legalForm) {
		this.legalForm = legalForm;
	}

	public String getSiren() {
		return siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}

	public String getSiret() {
		return siret;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

	public String getRna() {
		return rna;
	}

	public void setRna(String rna) {
		this.rna = rna;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public Float getShareCapital() {
		return shareCapital;
	}

	public void setShareCapital(Float shareCapital) {
		this.shareCapital = shareCapital;
	}

}
