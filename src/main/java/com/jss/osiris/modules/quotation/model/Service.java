package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = { @Index(name = "idx_service_asso_affaire_order", columnList = "id_asso_affaire_order") })
public class Service implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "service_sequence", sequenceName = "service_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_sequence")
	@IndexedField
	private Integer id;

	@OneToMany(targetEntity = Provision.class, mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	@IndexedField
	private List<Provision> provisions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_asso_affaire_order")
	@JsonIgnoreProperties(value = { "services" }, allowSetters = true)
	private AssoAffaireOrder assoAffaireOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type")
	private ServiceType serviceType;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	private List<AssoServiceDocument> assoServiceDocuments;

	@OneToMany(targetEntity = MissingAttachmentQuery.class, mappedBy = "service", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "service" }, allowSetters = true)
	private List<MissingAttachmentQuery> missingAttachmentQueries;

	public List<AssoServiceDocument> getAssoServiceDocuments() {
		return assoServiceDocuments;
	}

	public void setAssoServiceDocuments(List<AssoServiceDocument> assoServiceDocuments) {
		this.assoServiceDocuments = assoServiceDocuments;
	}

	private String customLabel;

	@Column(columnDefinition = "TEXT")
	private String customerComment;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Provision> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<Provision> provisions) {
		this.provisions = provisions;
	}

	public AssoAffaireOrder getAssoAffaireOrder() {
		return assoAffaireOrder;
	}

	public void setAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder) {
		this.assoAffaireOrder = assoAffaireOrder;
	}

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public String getCustomerComment() {
		return customerComment;
	}

	public void setCustomerComment(String customerComment) {
		this.customerComment = customerComment;
	}

	public List<MissingAttachmentQuery> getMissingAttachmentQueries() {
		return missingAttachmentQueries;
	}

	public void setMissingAttachmentQueries(List<MissingAttachmentQuery> missingAttachmentQueries) {
		this.missingAttachmentQueries = missingAttachmentQueries;
	}

}
