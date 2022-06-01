package com.jss.jssbackend.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jss.jssbackend.modules.quotation.model.CustomerOrder;
import com.jss.jssbackend.modules.quotation.model.Quotation;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "pk_attachment", columnList = "id", unique = true),
		@Index(name = "idx_tiers_attachment", columnList = "id_tiers"),
		@Index(name = "idx_responsable_attachment", columnList = "id_responsable") })
public class Attachment implements Serializable {

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
	@JoinColumn(name = "id_quotation")
	@JsonBackReference("quotation")
	private Quotation quotation;

	@ManyToOne
	@JoinColumn(name = "id_customer_order")
	@JsonBackReference("customerOrder")
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type")
	private AttachmentType attachmentType;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_uploaded_file")
	private UploadedFile uploadedFile;

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

	public AttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

}
