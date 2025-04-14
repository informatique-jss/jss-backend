package com.jss.osiris.modules.osiris.quotation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Civility;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowup;

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
public class Affaire implements IId, IAttachment {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_civility")
	@JsonView(JacksonViews.MyJssView.class)
	private Civility civility;

	@Column(length = 150)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String denomination;

	@Column(length = 150)
	@JsonView(JacksonViews.MyJssView.class)
	private String acronym;

	@Column(nullable = false)
	@JsonView(JacksonViews.MyJssView.class)
	private Boolean isIndividual;

	@Column(length = 50)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String firstname;

	@Column(length = 50)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String lastname;

	@Column(length = 9)
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private String siren;

	@Column(length = 14)
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private String siret;

	@Column(length = 10)
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private String rna;

	@Column(length = 10)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	private String postalCode;

	@Column(length = 20)
	@JsonView(JacksonViews.MyJssView.class)
	private String cedexComplement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_city")
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private Country country;

	@Column(length = 100, nullable = false)
	@IndexedField
	@JsonView({ JacksonViews.MyJssView.class, JacksonViews.OsirisDetailedView.class })
	private String address;

	@ManyToMany
	@JoinTable(name = "asso_affaire_mail", joinColumns = @JoinColumn(name = "id_affaire"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	@JsonView(JacksonViews.MyJssView.class)
	private List<Mail> mails;

	@ManyToMany
	@JoinTable(name = "asso_affaire_phone", joinColumns = @JoinColumn(name = "id_affaire"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	@JsonView(JacksonViews.MyJssView.class)
	private List<Phone> phones;

	@Column(length = 60)
	private String externalReference;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
	private BigDecimal shareCapital;

	@Column(length = 40, name = "payment_iban")
	@JsonView(JacksonViews.MyJssView.class)
	private String paymentIban;

	@Column(length = 40, name = "payment_bic")
	@JsonView(JacksonViews.MyJssView.class)
	private String paymentBic;

	private Boolean isUnregistered;

	@Column(length = 20)
	private String intercommunityVat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forme_juridique")
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private FormeJuridique legalForm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forme_exercice")
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private FormeExerciceActivitePrincipal mainActivity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_competent_authority")
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	@JsonIgnoreProperties(value = { "attachments", "departments", "cities", "regions" }, allowSetters = true)
	private CompetentAuthority competentAuthority;

	@OneToMany(mappedBy = "affaire")
	@JsonIgnoreProperties(value = { "affaire" }, allowSetters = true)
	private List<TiersFollowup> tiersFollowups;

	@OneToMany(mappedBy = "affaire", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "affaire" }, allowSetters = true)
	private List<Attachment> attachments;

	@JsonView(JacksonViews.MyJssView.class)
	private Integer employeeNumber;

	private LocalDate lastRneUpdate;

	private Boolean isMainOffice;

	@Column(columnDefinition = "TEXT")
	@JsonView(JacksonViews.MyJssView.class)
	private String apeCodes;

	public String getPaymentIban() {
		return paymentIban;
	}

	public void setPaymentIban(String paymentIban) {
		this.paymentIban = paymentIban;
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

	public BigDecimal getShareCapital() {
		return shareCapital;
	}

	public void setShareCapital(BigDecimal shareCapital) {
		this.shareCapital = shareCapital;
	}

	public Boolean getIsUnregistered() {
		return isUnregistered;
	}

	public void setIsUnregistered(Boolean isUnregistered) {
		this.isUnregistered = isUnregistered;
	}

	public String getCedexComplement() {
		return cedexComplement;
	}

	public void setCedexComplement(String cedexComplement) {
		this.cedexComplement = cedexComplement;
	}

	public String getPaymentBic() {
		return paymentBic;
	}

	public void setPaymentBic(String paymentBic) {
		this.paymentBic = paymentBic;
	}

	public String getIntercommunityVat() {
		return intercommunityVat;
	}

	public void setIntercommunityVat(String intercommunityVat) {
		this.intercommunityVat = intercommunityVat;
	}

	public FormeJuridique getLegalForm() {
		return legalForm;
	}

	public void setLegalForm(FormeJuridique legalForm) {
		this.legalForm = legalForm;
	}

	public FormeExerciceActivitePrincipal getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(FormeExerciceActivitePrincipal mainActivity) {
		this.mainActivity = mainActivity;
	}

	public CompetentAuthority getCompetentAuthority() {
		return competentAuthority;
	}

	public void setCompetentAuthority(CompetentAuthority competentAuthority) {
		this.competentAuthority = competentAuthority;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public List<TiersFollowup> getTiersFollowups() {
		return tiersFollowups;
	}

	public void setTiersFollowups(List<TiersFollowup> tiersFollowups) {
		this.tiersFollowups = tiersFollowups;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Integer getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(Integer employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getApeCodes() {
		return apeCodes;
	}

	public void setApeCodes(String apeCodes) {
		this.apeCodes = apeCodes;
	}

	public LocalDate getLastRneUpdate() {
		return lastRneUpdate;
	}

	public void setLastRneUpdate(LocalDate lastRneUpdate) {
		this.lastRneUpdate = lastRneUpdate;
	}

	public Boolean getIsMainOffice() {
		return isMainOffice;
	}

	public void setIsMainOffice(Boolean isMainOffice) {
		this.isMainOffice = isMainOffice;
	}

}
