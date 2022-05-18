package com.jss.jssbackend.modules.clients.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jss.jssbackend.modules.miscellaneous.model.Civility;
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
	private TiersType clientType;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TiersType getClientType() {
		return clientType;
	}

	public void setClientType(TiersType clientType) {
		this.clientType = clientType;
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

}
