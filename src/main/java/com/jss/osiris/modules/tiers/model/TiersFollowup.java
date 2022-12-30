package com.jss.osiris.modules.tiers.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.Gift;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Confrere;

@Entity
@Table(indexes = { @Index(name = "idx_tiers_followup_tiers", columnList = "id_tiers"),
		@Index(name = "idx_tiers_followup_responsable", columnList = "id_responsable") })
public class TiersFollowup implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "tiers_followup_sequence", sequenceName = "tiers_followup_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tiers_followup_sequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	private Responsable responsable;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	private Confrere confrere;

	@ManyToOne
	@JoinColumn(name = "id_invoice")
	@JsonIgnoreProperties(value = { "tiersFollowups" }, allowSetters = true)
	private Invoice invoice;

	@ManyToOne
	@JoinColumn(name = "id_tiers_followup_type")
	private TiersFollowupType tiersFollowupType;

	@ManyToOne
	@JoinColumn(name = "id_gift")
	private Gift gift;

	@ManyToOne
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

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

}
