package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

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
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private Integer id;

	@Column(nullable = false, columnDefinition = "TEXT")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private Employee employee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private Responsable currentCustomer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonView({ JacksonViews.OsirisListView.class })
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
	@JsonView({ JacksonViews.OsirisListView.class })
	private List<ActiveDirectoryGroup> activeDirectoryGroups;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private LocalDateTime createdDateTime;

	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private Boolean isRead;

	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private Boolean isToDisplayToCustomer;

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

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Responsable getCurrentCustomer() {
		return currentCustomer;
	}

	public void setCurrentCustomer(Responsable currentCustomer) {
		this.currentCustomer = currentCustomer;
	}

	public Boolean getIsToDisplayToCustomer() {
		return isToDisplayToCustomer;
	}

	public void setIsToDisplayToCustomer(Boolean isToDisplayToCustomer) {
		this.isToDisplayToCustomer = isToDisplayToCustomer;
	}
}
