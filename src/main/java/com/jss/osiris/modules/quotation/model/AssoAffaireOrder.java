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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "id_customer_order", "id_affaire", "id_quotation" }) })
public class AssoAffaireOrder implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_affaire_order_sequence", sequenceName = "asso_affaire_order_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_affaire_order_sequence")
	@IndexedField
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_affaire")
	@IndexedField
	private Affaire affaire;

	@ManyToOne
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "assoAffaireOrders", "invoices", "deposits", "payments",
			"accountingRecords" }, allowSetters = true)
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = "id_quotation")
	@JsonIgnoreProperties(value = { "assoAffaireOrders" }, allowSetters = true)
	private Quotation quotation;

	@ManyToOne
	@JoinColumn(name = "id_employee")
	private Employee assignedTo;

	@OneToMany(targetEntity = Provision.class, mappedBy = "assoAffaireOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "assoAffaireOrder" }, allowSetters = true)
	@IndexedField
	private List<Provision> provisions;

	public Affaire getAffaire() {
		return affaire;
	}

	public void setAffaire(Affaire affaire) {
		this.affaire = affaire;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Employee getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Employee assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public List<Provision> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<Provision> provisions) {
		this.provisions = provisions;
	}

}
