package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
public class Provision implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provision_sequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_asso_affaire_order")
	@JsonIgnoreProperties(value = { "provisions" }, allowSetters = true)
	private AssoAffaireOrder assoAffaireOrder;

	@ManyToOne
	@JoinColumn(name = "id_provision_type")
	@IndexedField
	private ProvisionType provisionType;

	@ManyToOne
	@JoinColumn(name = "id_provision_family_type")
	@IndexedField
	private ProvisionFamilyType provisionFamilyType;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_domiciliation")
	private Domiciliation domiciliation;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_announcement")
	private Announcement announcement;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_bodacc")
	private Bodacc bodacc;

	@OneToMany(targetEntity = InvoiceItem.class, mappedBy = "provision", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "provision" }, allowSetters = true)
	private List<InvoiceItem> invoiceItems;

	@ManyToOne
	@JoinColumn(name = "id_employee")
	@IndexedField
	private Employee assignedTo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AssoAffaireOrder getAssoAffaireOrder() {
		return assoAffaireOrder;
	}

	public void setAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder) {
		this.assoAffaireOrder = assoAffaireOrder;
	}

	public ProvisionType getProvisionType() {
		return provisionType;
	}

	public void setProvisionType(ProvisionType provisionType) {
		this.provisionType = provisionType;
	}

	public ProvisionFamilyType getProvisionFamilyType() {
		return provisionFamilyType;
	}

	public void setProvisionFamilyType(ProvisionFamilyType provisionFamilyType) {
		this.provisionFamilyType = provisionFamilyType;
	}

	public Domiciliation getDomiciliation() {
		return domiciliation;
	}

	public void setDomiciliation(Domiciliation domiciliation) {
		this.domiciliation = domiciliation;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	public Bodacc getBodacc() {
		return bodacc;
	}

	public void setBodacc(Bodacc bodacc) {
		this.bodacc = bodacc;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Employee getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Employee assignedTo) {
		this.assignedTo = assignedTo;
	}

}
