package com.jss.osiris.modules.osiris.quotation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Confrere implements IId {

	@Id
	@SequenceGenerator(name = "confrere_sequence", sequenceName = "confrere_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confrere_sequence")
	private Integer id;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(length = 100)
	@IndexedField
	private String label;

	@ManyToMany
	@JoinTable(name = "asso_confrere_department", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany
	@JoinTable(name = "asso_confrere_mail", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_confrere_phone", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@ManyToMany
	@JoinTable(name = "asso_confrere_special_offer", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	private Integer reinvoicing;

	@ManyToMany
	@JoinTable(name = "asso_confrere_publication_day", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_weekday"))
	private List<WeekDay> weekDays;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_journal_type")
	private JournalType journalType;

	@Column(length = 200)
	private String lastShipmentForPublication;

	@Column(length = 20)
	private String publicationCertificateDocumentGrade;

	@Column(length = 20)
	private String billingGrade;

	@Column(length = 20)
	private String paperGrade;

	@Column(length = 20)
	private String boardGrade;

	private Float shippingCosts;

	private Float administrativeFees;

	private Integer numberOfPrint;

	private Float paperPrice;

	private Integer discountRate;

	@Column(length = 60)
	private String mailRecipient;

	@Column(length = 100)
	private String address;

	@Column(length = 10)
	private String postalCode;

	@Column(length = 20)
	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	private Country country;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@OneToMany(mappedBy = "confrere", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "confrere" }, allowSetters = true)
	private List<Document> documents;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provider")
	private Provider provider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	private Responsable responsable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
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

	public List<SpecialOffer> getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(List<SpecialOffer> specialOffers) {
		this.specialOffers = specialOffers;
	}

	public Integer getReinvoicing() {
		return reinvoicing;
	}

	public void setReinvoicing(Integer reinvoicing) {
		this.reinvoicing = reinvoicing;
	}

	public List<WeekDay> getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(List<WeekDay> weekDays) {
		this.weekDays = weekDays;
	}

	public JournalType getJournalType() {
		return journalType;
	}

	public void setJournalType(JournalType journalType) {
		this.journalType = journalType;
	}

	public String getLastShipmentForPublication() {
		return lastShipmentForPublication;
	}

	public void setLastShipmentForPublication(String lastShipmentForPublication) {
		this.lastShipmentForPublication = lastShipmentForPublication;
	}

	public String getPublicationCertificateDocumentGrade() {
		return publicationCertificateDocumentGrade;
	}

	public void setPublicationCertificateDocumentGrade(String publicationCertificateDocumentGrade) {
		this.publicationCertificateDocumentGrade = publicationCertificateDocumentGrade;
	}

	public String getBillingGrade() {
		return billingGrade;
	}

	public void setBillingGrade(String billingGrade) {
		this.billingGrade = billingGrade;
	}

	public String getPaperGrade() {
		return paperGrade;
	}

	public void setPaperGrade(String paperGrade) {
		this.paperGrade = paperGrade;
	}

	public String getBoardGrade() {
		return boardGrade;
	}

	public void setBoardGrade(String boardGrade) {
		this.boardGrade = boardGrade;
	}

	public Float getShippingCosts() {
		return shippingCosts;
	}

	public void setShippingCosts(Float shippingCosts) {
		this.shippingCosts = shippingCosts;
	}

	public Float getAdministrativeFees() {
		return administrativeFees;
	}

	public void setAdministrativeFees(Float administrativeFees) {
		this.administrativeFees = administrativeFees;
	}

	public Integer getNumberOfPrint() {
		return numberOfPrint;
	}

	public void setNumberOfPrint(Integer numberOfPrint) {
		this.numberOfPrint = numberOfPrint;
	}

	public Float getPaperPrice() {
		return paperPrice;
	}

	public void setPaperPrice(Float paperPrice) {
		this.paperPrice = paperPrice;
	}

	public Integer getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Integer discountRate) {
		this.discountRate = discountRate;
	}

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
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

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
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

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

}
