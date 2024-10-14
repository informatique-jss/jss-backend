package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;

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
public class CompetentAuthority implements IAttachment, IId {

	@Id
	@SequenceGenerator(name = "competent_authority_sequence", sequenceName = "competent_authority_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competent_authority_sequence")
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	private String apiId;

	@Column(nullable = false, length = 200)
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority_type")
	private CompetentAuthorityType competentAuthorityType;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_department", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_mail", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_phone", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	@Column(length = 60)
	private String contact;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_city", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_city"))
	@JsonIgnoreProperties(value = { "department", "country" }, allowSetters = true)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<City> cities;

	@ManyToMany
	@JoinTable(name = "asso_competent_authority_region", joinColumns = @JoinColumn(name = "id_competent_authority"), inverseJoinColumns = @JoinColumn(name = "id_region"))
	private List<Region> regions;

	@Column(length = 60)
	private String mailRecipient;

	@Column(length = 200)
	private String address;

	@Column(length = 10)
	private String postalCode;

	@Column(length = 40)
	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	private Country country;

	@Column(length = 2000)
	private String schedulle;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@OneToMany(mappedBy = "competentAuthority")
	@JsonIgnoreProperties(value = { "invoice", "customerOrder" }, allowSetters = true)
	private List<Attachment> attachments;

	private String inpiReference;

	private String azureCustomReference;

	@Column(length = 20)
	private String intercommunityVat;

	private Boolean isNotToReminder;

	@OneToMany(mappedBy = "competentAuthority", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "competentAuthority" }, allowSetters = true)
	private List<AssoMailCompetentAuthorityServiceFamilyGroup> assoMailCompetentAuthorityServiceFamilyGroups;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provider")
	private Provider provider;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
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

	public CompetentAuthorityType getCompetentAuthorityType() {
		return competentAuthorityType;
	}

	public void setCompetentAuthorityType(CompetentAuthorityType competentAuthorityType) {
		this.competentAuthorityType = competentAuthorityType;
	}

	public String getSchedulle() {
		return schedulle;
	}

	public void setSchedulle(String schedulle) {
		this.schedulle = schedulle;
	}

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getInpiReference() {
		return inpiReference;
	}

	public void setInpiReference(String inpiReference) {
		this.inpiReference = inpiReference;
	}

	public String getAzureCustomReference() {
		return azureCustomReference;
	}

	public void setAzureCustomReference(String azureCustomReference) {
		this.azureCustomReference = azureCustomReference;
	}

	public Boolean getIsNotToReminder() {
		return isNotToReminder;
	}

	public void setIsNotToReminder(Boolean isNotToReminder) {
		this.isNotToReminder = isNotToReminder;
	}

	public List<AssoMailCompetentAuthorityServiceFamilyGroup> getAssoMailCompetentAuthorityServiceFamilyGroups() {
		return assoMailCompetentAuthorityServiceFamilyGroups;
	}

	public void setAssoMailCompetentAuthorityServiceFamilyGroups(
			List<AssoMailCompetentAuthorityServiceFamilyGroup> assoMailCompetentAuthorityServiceFamilyGroups) {
		this.assoMailCompetentAuthorityServiceFamilyGroups = assoMailCompetentAuthorityServiceFamilyGroups;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getIntercommunityVat() {
		return intercommunityVat;
	}

	public void setIntercommunityVat(String intercommunityVat) {
		this.intercommunityVat = intercommunityVat;
	}

}
