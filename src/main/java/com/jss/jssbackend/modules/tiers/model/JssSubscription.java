package com.jss.jssbackend.modules.tiers.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class JssSubscription implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_jss_subscription_type")
	private JssSubscriptionType jssSubscriptionType;

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

	public JssSubscriptionType getJssSubscriptionType() {
		return jssSubscriptionType;
	}

	public void setJssSubscriptionType(JssSubscriptionType jssSubscriptionType) {
		this.jssSubscriptionType = jssSubscriptionType;
	}

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

}
