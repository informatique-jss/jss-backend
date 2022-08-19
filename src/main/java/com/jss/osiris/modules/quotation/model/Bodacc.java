package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;

@Entity
public class Bodacc implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonBackReference("provision")
	private Provision provision;

	@ManyToOne
	@JoinColumn(name = "id_payment_type")
	private PaymentType paymentType;

	@ManyToOne
	@JoinColumn(name = "id_bodacc_publication_type")
	private BodaccPublicationType bodaccPublicationType;

	@ManyToOne
	@JoinColumn(name = "id_transfert_funds_type")
	private TransfertFundsType transfertFundsType;

	@OneToOne(targetEntity = BodaccSale.class, cascade = CascadeType.ALL)
	private BodaccSale bodaccSale;

	@OneToOne(targetEntity = BodaccFusion.class, cascade = CascadeType.ALL)
	private BodaccFusion bodaccFusion;

	@OneToOne(targetEntity = BodaccSplit.class, cascade = CascadeType.ALL)
	private BodaccSplit bodaccSplit;

	@OneToMany(targetEntity = Attachment.class, mappedBy = "bodacc", cascade = CascadeType.ALL)
	@JsonManagedReference("bodacc")
	private List<Attachment> attachments;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate dateOfPublication;

	public Integer getId() {
		return id;
	}

	public BodaccFusion getBodaccFusion() {
		return bodaccFusion;
	}

	public LocalDate getDateOfPublication() {
		return dateOfPublication;
	}

	public BodaccSplit getBodaccSplit() {
		return bodaccSplit;
	}

	public void setBodaccSplit(BodaccSplit bodaccSplit) {
		this.bodaccSplit = bodaccSplit;
	}

	public void setDateOfPublication(LocalDate dateOfPublication) {
		this.dateOfPublication = dateOfPublication;
	}

	public void setBodaccFusion(BodaccFusion bodaccFusion) {
		this.bodaccFusion = bodaccFusion;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public BodaccPublicationType getBodaccPublicationType() {
		return bodaccPublicationType;
	}

	public TransfertFundsType getTransfertFundsType() {
		return transfertFundsType;
	}

	public void setTransfertFundsType(TransfertFundsType transfertFundsType) {
		this.transfertFundsType = transfertFundsType;
	}

	public void setBodaccPublicationType(BodaccPublicationType bodaccPublicationType) {
		this.bodaccPublicationType = bodaccPublicationType;
	}

	public BodaccSale getBodaccSale() {
		return bodaccSale;
	}

	public void setBodaccSale(BodaccSale bodaccSale) {
		this.bodaccSale = bodaccSale;
	}

}
