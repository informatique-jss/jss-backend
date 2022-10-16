package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = { @Index(name = "idx_tiers_attachment", columnList = "id_tiers"),
		@Index(name = "idx_domiciliation_attachment", columnList = "id_domiciliation"),
		@Index(name = "idx_customer_order_attachment", columnList = "id_customer_order"),
		@Index(name = "idx_quotation_attachment", columnList = "id_quotation"),
		@Index(name = "idx_responsable_attachment", columnList = "id_responsable") })
public class Attachment implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_sequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Responsable responsable;

	@ManyToOne
	@JoinColumn(name = "id_quotation")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Quotation quotation;

	@ManyToOne
	@JoinColumn(name = "id_domiciliation")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Domiciliation domiciliation;

	@ManyToOne
	@JoinColumn(name = "id_announcement")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Announcement announcement;

	@ManyToOne
	@JoinColumn(name = "id_bodacc")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private Bodacc bodacc;

	@ManyToOne
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "attachments" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type")
	private AttachmentType attachmentType;

	@ManyToOne
	@JoinColumn(name = "id_uploaded_file")
	private UploadedFile uploadedFile;

	@Column(nullable = false)
	private Boolean isDisabled;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Bodacc getBodacc() {
		return bodacc;
	}

	public void setBodacc(Bodacc bodacc) {
		this.bodacc = bodacc;
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

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public AttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
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

	public Domiciliation getDomiciliation() {
		return domiciliation;
	}

	public void setDomiciliation(Domiciliation domiciliation) {
		this.domiciliation = domiciliation;
	}

}
