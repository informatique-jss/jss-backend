package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "id_customer_order", "id_affaire", "id_quotation" }) }, indexes = {
				@Index(name = "idx_asso_affaire_customer_order", columnList = "id_customer_order"),
				@Index(name = "idx_asso_affaire_affaire", columnList = "id_affaire"),
				@Index(name = "idx_asso_affaire_employee", columnList = "id_employee"),
				@Index(name = "idx_asso_affaire_quotation", columnList = "id_quotation") })
public class AssoAffaireOrder implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_affaire_order_sequence", sequenceName = "asso_affaire_order_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_affaire_order_sequence")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_affaire")
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Affaire affaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "assoAffaireOrders", "invoices", "deposits", "payments",
			"accountingRecords" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	@IndexedField
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_quotation")
	@JsonIgnoreProperties(value = { "assoAffaireOrders" }, allowSetters = true)
	private Quotation quotation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	private Employee assignedTo;

	@OneToMany(targetEntity = Service.class, mappedBy = "assoAffaireOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "assoAffaireOrder" }, allowSetters = true)
	@IndexedField
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	@OrderBy("id")
	private List<Service> services;

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

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

}
