package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
		@Index(name = "idx_customer_order_comment_provision", columnList = "id_provision"),
		@Index(name = "idx_customer_order_comment_quotation", columnList = "id_quotation"),
		@Index(name = "idx_customer_order_comment_customer_order", columnList = "id_customer_order"),
})
public class CustomerOrderComment implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "customer_order_comment_sequence", sequenceName = "customer_order_comment_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_order_comment_sequence")
	private Integer id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	private Employee employee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Provision provision;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_quotation")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Quotation quotation;

	@ManyToMany
	@JoinTable(name = "asso_customer_order_comment_active_directory_group", joinColumns = @JoinColumn(name = "id_customer_order_comment"), inverseJoinColumns = @JoinColumn(name = "id_active_directory_group"))
	private List<ActiveDirectoryGroup> activeDirectoryGroups;

	@Column(nullable = false)
	private LocalDateTime createdDateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public List<ActiveDirectoryGroup> getActiveDirectoryGroups() {
		return activeDirectoryGroups;
	}

	public void setActiveDirectoryGroups(List<ActiveDirectoryGroup> activeDirectoryGroups) {
		this.activeDirectoryGroups = activeDirectoryGroups;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(LocalDateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
