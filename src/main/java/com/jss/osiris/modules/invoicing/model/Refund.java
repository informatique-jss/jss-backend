package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
public class Refund implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	private Float refundAmount;

	private LocalDateTime refundDateTime;

	private Tiers tiers;

	private Confrere confrere;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Float getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Float refundAmount) {
		this.refundAmount = refundAmount;
	}

	public LocalDateTime getRefundDateTime() {
		return refundDateTime;
	}

	public void setRefundDateTime(LocalDateTime refundDateTime) {
		this.refundDateTime = refundDateTime;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public Confrere getConfrere() {
		return confrere;
	}

	public void setConfrere(Confrere confrere) {
		this.confrere = confrere;
	}

}
