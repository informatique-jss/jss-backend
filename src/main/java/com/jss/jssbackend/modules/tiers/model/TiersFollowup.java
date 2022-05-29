package com.jss.jssbackend.modules.tiers.model;

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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.jssbackend.modules.miscellaneous.model.Gift;
import com.jss.jssbackend.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "pk_tiers_followup", columnList = "id", unique = true),
		@Index(name = "idx_tiers_followup_tiers", columnList = "id_tiers"),
		@Index(name = "idx_tiers_followup_responsable", columnList = "id_responsable") })
public class TiersFollowup implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@JsonBackReference("tiers")
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@JsonBackReference("responsable")
	private Responsable responsable;

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
	private Date followupDate;

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

	public Date getFollowupDate() {
		return followupDate;
	}

	public void setFollowupDate(Date followupDate) {
		this.followupDate = followupDate;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

}
