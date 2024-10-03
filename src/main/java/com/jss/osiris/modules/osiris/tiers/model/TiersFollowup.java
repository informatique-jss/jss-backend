package com.jss.osiris.modules.osiris.tiers.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.miscellaneous.model.Gift;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_tiers_followup_tiers", columnList = "id_tiers"),
		@Index(name = "idx_tiers_followup_invoice", columnList = "id_invoice"),
		@Index(name = "idx_tiers_followup_confrere", columnList = "id_confrere"),
		@Index(name = "idx_tiers_followup_responsable", columnList = "id_responsable") })
public class TiersFollowup implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "tiers_followup_sequence", sequenceName = "tiers_followup_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tiers_followup_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	@JsonIgnore
	private Tiers tiers;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	@JsonIgnore
	private Responsable responsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	@JsonIgnore
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_affaire")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	@JsonIgnore
	private Affaire affaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers_followup_type")
	private TiersFollowupType tiersFollowupType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_gift")
	private Gift gift;

	private Integer giftNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commercial")
	private Employee salesEmployee;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate followupDate;

	@Column(columnDefinition = "TEXT")
	private String observations;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public TiersFollowupType getTiersFollowupType() {
		return tiersFollowupType;
	}

	public void setTiersFollowupType(TiersFollowupType tiersFollowupType) {
		this.tiersFollowupType = tiersFollowupType;
	}

	public Gift getGift() {
		return gift;
	}

	public void setGift(Gift gift) {
		this.gift = gift;
	}

	public Employee getSalesEmployee() {
		return salesEmployee;
	}

	public void setSalesEmployee(Employee salesEmployee) {
		this.salesEmployee = salesEmployee;
	}

	public LocalDate getFollowupDate() {
		return followupDate;
	}

	public void setFollowupDate(LocalDate followupDate) {
		this.followupDate = followupDate;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Integer getGiftNumber() {
		return giftNumber;
	}

	public void setGiftNumber(Integer giftNumber) {
		this.giftNumber = giftNumber;
	}

	public Affaire getAffaire() {
		return affaire;
	}

	public void setAffaire(Affaire affaire) {
		this.affaire = affaire;
	}

}
