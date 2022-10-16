package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;

@Entity
public class Affaire implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_civility")
	private Civility civility;

	@Column(length = 60)
	@IndexedField
	private String denomination;

	@Column(nullable = false)
	private Boolean isIndividual;

	@Column(length = 20)
	@IndexedField
	private String firstname;

	@Column(length = 20)
	@IndexedField
	private String lastname;

	@ManyToOne
	@JoinColumn(name = "id_legal_form")
	private LegalForm legalForm;

	@Column(length = 9)
	@IndexedField
	private String siren;

	@Column(length = 14)
	@IndexedField
	private String siret;

	@Column(length = 10)
	@IndexedField
	private String rna;

	@Column(length = 10, nullable = false)
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

	@ManyToMany
	@JoinTable(name = "asso_affaire_mail", joinColumns = @JoinColumn(name = "id_affaire"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_affaire_phone", joinColumns = @JoinColumn(name = "id_affaire"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 60)
	private String externalReference;

	@Column(columnDefinition = "TEXT")
	private String observations;

	private Float shareCapital;

	@Column(length = 40, name = "payment_iban")
	private String paymentIban;

	@Column(length = 40, name = "payment_bic")
	private String paymentBic;

	public String getPaymentIban() {
		return paymentIban;
	}

	public void setPaymentIban(String paymentIban) {
		this.paymentIban = paymentIban;
	}

	public String getPaymentBic() {
		return paymentBic;
	}

	public void setPaymentBic(String paymentBic) {
		this.paymentBic = paymentBic;
	}

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

	public Civility getCivility() {
		return civility;
	}

	public void setCivility(Civility civility) {
		this.civility = civility;
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
