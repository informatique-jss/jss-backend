package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_incident_order", columnList = "id_customer_order"),
		@Index(name = "idx_incident_employee", columnList = "id_employee") })
public class IncidentReport implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "incident_report_sequence", sequenceName = "incident_report_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incident_report_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_incident_report_status")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private IncidentReportStatus incidentReportStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_incident_responsibility")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private IncidentResponsibility incidentResponsibility;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_incident_type")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private IncidentType incidentType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_incident_cause")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private IncidentCause incidentCause;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Employee assignedTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private CustomerOrder customerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Provision provision;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee_initiator")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Employee initiatedBy;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDate startDate;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDate endDate;

	@Column(length = 200)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String title;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String description;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String customerImpact;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String jssImpact;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String detectionDescription;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String internalCommunicationDescription;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String externalCommunicationDescription;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String analysis;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String remedialActions;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String preventiveActionsProposal;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Float costEstimation;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String managementComments;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private String preventiveActions;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public IncidentReportStatus getIncidentReportStatus() {
		return incidentReportStatus;
	}

	public void setIncidentReportStatus(IncidentReportStatus incidentReportStatus) {
		this.incidentReportStatus = incidentReportStatus;
	}

	public IncidentResponsibility getIncidentResponsibility() {
		return incidentResponsibility;
	}

	public void setIncidentResponsibility(IncidentResponsibility incidentResponsibility) {
		this.incidentResponsibility = incidentResponsibility;
	}

	public Employee getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Employee assignedTo) {
		this.assignedTo = assignedTo;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCustomerImpact() {
		return customerImpact;
	}

	public void setCustomerImpact(String customerImpact) {
		this.customerImpact = customerImpact;
	}

	public String getJssImpact() {
		return jssImpact;
	}

	public void setJssImpact(String jssImpact) {
		this.jssImpact = jssImpact;
	}

	public String getDetectionDescription() {
		return detectionDescription;
	}

	public void setDetectionDescription(String detectionDescription) {
		this.detectionDescription = detectionDescription;
	}

	public String getInternalCommunicationDescription() {
		return internalCommunicationDescription;
	}

	public void setInternalCommunicationDescription(String internalCommunicationDescription) {
		this.internalCommunicationDescription = internalCommunicationDescription;
	}

	public String getExternalCommunicationDescription() {
		return externalCommunicationDescription;
	}

	public void setExternalCommunicationDescription(String externalCommunicationDescription) {
		this.externalCommunicationDescription = externalCommunicationDescription;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public String getRemedialActions() {
		return remedialActions;
	}

	public void setRemedialActions(String remedialActions) {
		this.remedialActions = remedialActions;
	}

	public String getPreventiveActionsProposal() {
		return preventiveActionsProposal;
	}

	public void setPreventiveActionsProposal(String preventiveActionsProposal) {
		this.preventiveActionsProposal = preventiveActionsProposal;
	}

	public Float getCostEstimation() {
		return costEstimation;
	}

	public void setCostEstimation(Float costEstimation) {
		this.costEstimation = costEstimation;
	}

	public String getManagementComments() {
		return managementComments;
	}

	public void setManagementComments(String managementComments) {
		this.managementComments = managementComments;
	}

	public String getPreventiveActions() {
		return preventiveActions;
	}

	public void setPreventiveActions(String preventiveActions) {
		this.preventiveActions = preventiveActions;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Employee getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(Employee initiatedBy) {
		this.initiatedBy = initiatedBy;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Provision getProvision() {
		return provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public IncidentCause getIncidentCause() {
		return incidentCause;
	}

	public void setIncidentCause(IncidentCause incidentCause) {
		this.incidentCause = incidentCause;
	}

	public IncidentType getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(IncidentType incidentType) {
		this.incidentType = incidentType;
	}

}
