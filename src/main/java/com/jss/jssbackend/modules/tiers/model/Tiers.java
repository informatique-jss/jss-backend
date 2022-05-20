package com.jss.jssbackend.modules.tiers.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.model.DeliveryService;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "pk_client", columnList = "id", unique = true) })
public class Tiers implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers_type")
	private TiersType tiersType;
	@Column(length = 60)
	private String denomination;
	// TODO : waiting for Xcase experiment to evaluate if we get it dynamically from
	// compatibility or keep it here
	private Date firstBilling;
	private Boolean isIndividual;
	@JoinColumn(name = "id_civility")
	private Civility civility;
	@Column(length = 20)
	private String firstname;
	@Column(length = 20)
	private String lastname;
	@ManyToOne
	@JoinColumn(name = "id_tiers_category")
	private TiersCategory tiersCategory;

	@ManyToOne
	@JoinColumn(name = "id_commercial")
	private Employee salesEmployee;

	@ManyToOne
	@JoinColumn(name = "id_formaliste")
	private Employee formalisteEmployee;

	@ManyToOne
	@JoinColumn(name = "id_insertion")
	private Employee insertionEmployee;

	@Column(columnDefinition = "TEXT")
	private String mailRecipient;

	@ManyToOne
	@JoinColumn(name = "id_language")
	private Language language;

	@ManyToOne
	@JoinColumn(name = "id_delivery_service")
	private DeliveryService deliveryService;

	@Column(length = 60, nullable = false)
	private String address;
	@Column(length = 10)
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne
	@JoinColumn(name = "id_country")
	private Country country;

	@Column(length = 12)
	private String intercom;

	@Column(length = 20)
	private String intercommunityVat;

	@ManyToOne
	@JoinColumn(name = "id_special_offer")
	private SpecialOffer specialOffer;

	private Float rcaFormaliteRate;
	private Float rcaInsertionRate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@Column(columnDefinition = "TEXT")
	private String instructions;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_tiers_mail", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_tiers_phone", joinColumns = @JoinColumn(name = "id_tiers"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TiersType getTiersType() {
		return tiersType;
	}

	public void setTiersType(TiersType tiersType) {
		this.tiersType = tiersType;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public Date getFirstBilling() {
		return firstBilling;
	}

	public void setFirstBilling(Date firstBilling) {
		this.firstBilling = firstBilling;
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

	public TiersCategory getTiersCategory() {
		return tiersCategory;
	}

	public void setTiersCategory(TiersCategory tiersCategory) {
		this.tiersCategory = tiersCategory;
	}

	public Employee getSalesEmployee() {
		return salesEmployee;
	}

	public void setSalesEmployee(Employee salesEmployee) {
		this.salesEmployee = salesEmployee;
	}

	public Employee getFormalisteEmployee() {
		return formalisteEmployee;
	}

	public void setFormalisteEmployee(Employee formalisteEmployee) {
		this.formalisteEmployee = formalisteEmployee;
	}

	public Employee getInsertionEmployee() {
		return insertionEmployee;
	}

	public void setInsertionEmployee(Employee insertionEmployee) {
		this.insertionEmployee = insertionEmployee;
	}

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public DeliveryService getDeliveryService() {
		return deliveryService;
	}

	public void setDeliveryService(DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getIntercom() {
		return intercom;
	}

	public void setIntercom(String intercom) {
		this.intercom = intercom;
	}

	public String getIntercommunityVat() {
		return intercommunityVat;
	}

	public void setIntercommunityVat(String intercommunityVat) {
		this.intercommunityVat = intercommunityVat;
	}

	public SpecialOffer getSpecialOffer() {
		return specialOffer;
	}

	public void setSpecialOffer(SpecialOffer specialOffer) {
		this.specialOffer = specialOffer;
	}

	public Float getRcaFormaliteRate() {
		return rcaFormaliteRate;
	}

	public void setRcaFormaliteRate(Float rcaFormaliteRate) {
		this.rcaFormaliteRate = rcaFormaliteRate;
	}

	public Float getRcaInsertionRate() {
		return rcaInsertionRate;
	}

	public void setRcaInsertionRate(Float rcaInsertionRate) {
		this.rcaInsertionRate = rcaInsertionRate;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

}
