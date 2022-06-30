package com.jss.osiris.modules.quotation.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
public class CustomerOrder implements IQuotation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@IndexedField
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@IndexedField
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_responsable")
	@IndexedField
	private Responsable responsable;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_customer_order_special_offer", joinColumns = @JoinColumn(name = "id_customer_order"), inverseJoinColumns = @JoinColumn(name = "id_special_offer"))
	private List<SpecialOffer> specialOffers;

	@IndexedField
	private Date createdDate;

	@ManyToOne
	@JoinColumn(name = "id_status")
	private QuotationStatus status;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@Column(columnDefinition = "TEXT")
	private String description;

	@OneToMany(targetEntity = Attachment.class, mappedBy = "customerOrder", cascade = CascadeType.ALL)
	@JsonManagedReference("customerOrder")
	private List<Attachment> attachments;

	@OneToMany(targetEntity = Document.class, mappedBy = "customerOrder", cascade = CascadeType.ALL)
	@JsonManagedReference("customerOrder")
	private List<Document> documents;

	@ManyToOne
	@JoinColumn(name = "id_label_type")
	private QuotationLabelType labelType;

	@Column(length = 40)
	private String label;

	@ManyToOne
	@JoinColumn(name = "id_record_type")
	private RecordType recordType;

	@OneToMany(targetEntity = Provision.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("customerOrder")
	private List<Provision> provisions;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_customer_order_mail", joinColumns = @JoinColumn(name = "id_customer_order"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_customer_order_phone", joinColumns = @JoinColumn(name = "id_customer_order"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

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

	public List<SpecialOffer> getSpecialOffers() {
		return specialOffers;
	}

	public void setSpecialOffers(List<SpecialOffer> specialOffers) {
		this.specialOffers = specialOffers;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public QuotationStatus getStatus() {
		return status;
	}

	public void setStatus(QuotationStatus status) {
		this.status = status;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public QuotationLabelType getLabelType() {
		return labelType;
	}

	public void setLabelType(QuotationLabelType labelType) {
		this.labelType = labelType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}

	public List<Provision> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<Provision> provisions) {
		this.provisions = provisions;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

}
