package com.jss.jssbackend.modules.tiers.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.jssbackend.modules.miscellaneous.model.IId;

@Entity
public class JssSubscription implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private Boolean isPaperSubscription;

	@Column(nullable = false)
	private Boolean isWebSubscription;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@JsonBackReference("responsable")
	private Responsable responsable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public Boolean getIsPaperSubscription() {
		return isPaperSubscription;
	}

	public void setIsPaperSubscription(Boolean isPaperSubscription) {
		this.isPaperSubscription = isPaperSubscription;
	}

	public Boolean getIsWebSubscription() {
		return isWebSubscription;
	}

	public void setIsWebSubscription(Boolean isWebSubscription) {
		this.isWebSubscription = isWebSubscription;
	}

}
