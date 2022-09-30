package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.Affaire;
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

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_confrere")
	private Confrere confrere;

	@ManyToOne
	@JoinColumn(name = "id_affaire")
	private Affaire affaire;

	@ManyToOne
	@JoinColumn(name = "id_payment")
	private Payment payment;

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

	public Affaire getAffaire() {
		return affaire;
	}

	public void setAffaire(Affaire affaire) {
		this.affaire = affaire;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

}
