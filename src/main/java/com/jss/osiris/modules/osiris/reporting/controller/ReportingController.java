package com.jss.osiris.modules.osiris.reporting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.reporting.model.AssoReportingDashboardWidget;
import com.jss.osiris.modules.osiris.reporting.model.IncidentCause;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;
import com.jss.osiris.modules.osiris.reporting.model.IncidentResponsibility;
import com.jss.osiris.modules.osiris.reporting.model.IncidentType;
import com.jss.osiris.modules.osiris.reporting.model.ReportingDashboard;
import com.jss.osiris.modules.osiris.reporting.model.ReportingUpdateFrequency;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWorkingTable;
import com.jss.osiris.modules.osiris.reporting.model.UserReporting;
import com.jss.osiris.modules.osiris.reporting.service.AnnouncementReportingService;
import com.jss.osiris.modules.osiris.reporting.service.CustomerOrderReportingService;
import com.jss.osiris.modules.osiris.reporting.service.IncidentCauseService;
import com.jss.osiris.modules.osiris.reporting.service.IncidentReportService;
import com.jss.osiris.modules.osiris.reporting.service.IncidentReportStatusService;
import com.jss.osiris.modules.osiris.reporting.service.IncidentResponsibilityService;
import com.jss.osiris.modules.osiris.reporting.service.IncidentTypeService;
import com.jss.osiris.modules.osiris.reporting.service.ProvisionProductionReportingService;
import com.jss.osiris.modules.osiris.reporting.service.ProvisionReportingService;
import com.jss.osiris.modules.osiris.reporting.service.QuotationReportingService;
import com.jss.osiris.modules.osiris.reporting.service.RecoveryReportingService;
import com.jss.osiris.modules.osiris.reporting.service.ReportingDashboardService;
import com.jss.osiris.modules.osiris.reporting.service.ReportingWidgetService;
import com.jss.osiris.modules.osiris.reporting.service.ReportingWorkingTableService;
import com.jss.osiris.modules.osiris.reporting.service.TiersReportingService;
import com.jss.osiris.modules.osiris.reporting.service.TurnoverReportingService;
import com.jss.osiris.modules.osiris.reporting.service.TurnoverVatReportingService;
import com.jss.osiris.modules.osiris.reporting.service.UserReportingService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@RestController
public class ReportingController {

	private static final String inputEntryPoint = "/reporting";

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	QuotationReportingService quotationReportingService;

	@Autowired
	TurnoverReportingService turnoverReportingService;

	@Autowired
	TurnoverVatReportingService turnoverVatReportingService;

	@Autowired
	CustomerOrderReportingService customerOrderReportingService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	UserReportingService userReportingService;

	@Autowired
	ProvisionReportingService provisionReportingService;

	@Autowired
	RecoveryReportingService recoveryReportingService;

	@Autowired
	ProvisionProductionReportingService productionReportingService;

	@Autowired
	AnnouncementReportingService announcementReportingService;

	@Autowired
	TiersReportingService tiersReportingService;

	@Autowired
	ActiveDirectoryHelper activeDirectoryHelper;

	@Autowired
	IncidentReportService incidentReportService;

	@Autowired
	IncidentReportStatusService incidentReportStatusService;

	@Autowired
	IncidentResponsibilityService incidentResponsibilityService;

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	IncidentCauseService incidentCauseService;

	@Autowired
	IncidentTypeService incidentTypeService;

	@Autowired
	TiersService tiersService;

	@Autowired
	ResponsableService responsableService;

	@Autowired
	ReportingWorkingTableService reportingWorkingTableService;

	@Autowired
	ReportingDashboardService reportingDashboardService;

	@Autowired
	ReportingWidgetService reportingWidgetService;

	@GetMapping(inputEntryPoint + "/reporting-widget/payload")
	public ResponseEntity<String> getReportingWidgetPayload(@RequestParam Integer id) throws OsirisValidationException {
		ReportingWidget widget = reportingWidgetService.getReportingWidget(id);

		if (widget == null)
			throw new OsirisValidationException("widget");

		return new ResponseEntity<String>(reportingWidgetService.getReportingWidgetPayload(widget),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reporting-widgets")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<List<ReportingWidget>> getReportingWidgets() {
		return new ResponseEntity<List<ReportingWidget>>(reportingWidgetService.getReportingWidgets(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/reporting-widget")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<ReportingWidget> addOrUpdateReportingWidget(
			@RequestBody ReportingWidget reportingWidgets) throws OsirisValidationException, OsirisException {
		if (reportingWidgets.getId() != null)
			validationHelper.validateReferential(reportingWidgets, true, "reportingWidgets");
		validationHelper.validateString(reportingWidgets.getLabel(), true, "label");
		validationHelper.validateString(reportingWidgets.getLabelSqlText(), true, "labelSqlText");
		validationHelper.validateString(reportingWidgets.getLabelType(), true, "labelType");

		return new ResponseEntity<ReportingWidget>(reportingWidgetService.addOrUpdateReportingWidget(reportingWidgets),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reporting-dashboards/current")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<ReportingDashboard>> getReportingDashboardsForCurrentUser() {
		return new ResponseEntity<List<ReportingDashboard>>(
				reportingDashboardService.getReportingDashboardsForCurrentUser(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reporting-dashboards/id")
	@JsonView(JacksonViews.OsirisDetailedView.class)
	public ResponseEntity<ReportingDashboard> getReportingDashboardById(@RequestParam Integer id) {
		return new ResponseEntity<ReportingDashboard>(reportingDashboardService.getReportingDashboardById(id),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reporting-dashboards")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<List<ReportingDashboard>> getReportingDashboards() {
		return new ResponseEntity<List<ReportingDashboard>>(reportingDashboardService.getReportingDashboards(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/reporting-dashboard")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<ReportingDashboard> addOrUpdateReportingDashboard(
			@RequestBody ReportingDashboard reportingDashboards) throws OsirisValidationException, OsirisException {
		if (reportingDashboards.getId() != null)
			validationHelper.validateReferential(reportingDashboards, true, "reportingDashboards");
		validationHelper.validateString(reportingDashboards.getLabel(), true, "label");
		validationHelper.validateInteger(reportingDashboards.getDashboardOrder(), true, "order");

		if (reportingDashboards.getActiveDirectoryGroups() != null)
			for (ActiveDirectoryGroup adGroup : reportingDashboards.getActiveDirectoryGroups())
				validationHelper.validateReferential(adGroup, true, "adGroup");

		if (reportingDashboards.getAssoReportingDashboardWidgets() != null)
			for (AssoReportingDashboardWidget asso : reportingDashboards.getAssoReportingDashboardWidgets())
				validationHelper.validateReferential(asso.getReportingWidget(), true, "assoWidget");

		return new ResponseEntity<ReportingDashboard>(
				reportingDashboardService.addOrUpdateReportingDashboard(reportingDashboards), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reporting-update-frequency")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<List<ReportingUpdateFrequency>> getReportingUpdateFrequencies() {
		return new ResponseEntity<List<ReportingUpdateFrequency>>(ReportingUpdateFrequency.getAllReporingFrequency(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/reporting-working-tables")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<List<ReportingWorkingTable>> getReportingWorkingTables() {
		return new ResponseEntity<List<ReportingWorkingTable>>(reportingWorkingTableService.getReportingWorkingTables(),
				HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/reporting-working-table")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<ReportingWorkingTable> addOrUpdateReportingWorkingTable(
			@RequestBody ReportingWorkingTable reportingWorkingTables)
			throws OsirisValidationException, OsirisException {
		if (reportingWorkingTables.getId() != null)
			validationHelper.validateReferential(reportingWorkingTables, true, "reportingWorkingTables");
		validationHelper.validateString(reportingWorkingTables.getSqlText(), true, "code");
		validationHelper.validateString(reportingWorkingTables.getLabel(), true, "label");
		validationHelper.validateString(reportingWorkingTables.getViewName(), true, "viewName");
		validationHelper.validateString(reportingWorkingTables.getReportingUpdateFrequency(), true,
				"reportingUpdateFrequency");

		return new ResponseEntity<ReportingWorkingTable>(
				reportingWorkingTableService.addOrUpdateReportingWorkingTable(reportingWorkingTables), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-types")
	public ResponseEntity<List<IncidentType>> getIncidentTypes() {
		return new ResponseEntity<List<IncidentType>>(incidentTypeService.getIncidentTypes(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/incident-type")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<IncidentType> addOrUpdateIncidentType(
			@RequestBody IncidentType incidentTypes) throws OsirisValidationException, OsirisException {
		if (incidentTypes.getId() != null)
			validationHelper.validateReferential(incidentTypes, true, "incidentTypes");
		validationHelper.validateString(incidentTypes.getCode(), true, "code");
		validationHelper.validateString(incidentTypes.getLabel(), true, "label");

		return new ResponseEntity<IncidentType>(incidentTypeService.addOrUpdateIncidentType(incidentTypes),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-causes")
	public ResponseEntity<List<IncidentCause>> getIncidentCauses() {
		return new ResponseEntity<List<IncidentCause>>(incidentCauseService.getIncidentCauses(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/incident-cause")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<IncidentCause> addOrUpdateIncidentCause(
			@RequestBody IncidentCause incidentCauses) throws OsirisValidationException, OsirisException {
		if (incidentCauses.getId() != null)
			validationHelper.validateReferential(incidentCauses, true, "incidentCauses");
		validationHelper.validateString(incidentCauses.getCode(), true, "code");
		validationHelper.validateString(incidentCauses.getLabel(), true, "label");

		return new ResponseEntity<IncidentCause>(incidentCauseService.addOrUpdateIncidentCause(incidentCauses),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-responsibilities")
	public ResponseEntity<List<IncidentResponsibility>> getIncidentResponsibilities() {
		return new ResponseEntity<List<IncidentResponsibility>>(
				incidentResponsibilityService.getIncidentResponsibilities(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/incident-responsibility")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<IncidentResponsibility> addOrUpdateIncidentResponsibility(
			@RequestBody IncidentResponsibility incidentResponsibilities)
			throws OsirisValidationException, OsirisException {
		if (incidentResponsibilities.getId() != null)
			validationHelper.validateReferential(incidentResponsibilities, true, "incidentResponsibilities");
		validationHelper.validateString(incidentResponsibilities.getCode(), true, "code");
		validationHelper.validateString(incidentResponsibilities.getLabel(), true, "label");

		return new ResponseEntity<IncidentResponsibility>(
				incidentResponsibilityService.addOrUpdateIncidentResponsibility(incidentResponsibilities),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getQuotationReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				quotationReportingService.getQuotationReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/turnover")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getTurnoverReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				turnoverReportingService.getTurnoverReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/turnover-vat")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getTurnoverVatReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				turnoverVatReportingService.getTurnoverVatReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getAnnouncementReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				announcementReportingService.getAnnouncementReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tiers")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getTiersReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(tiersReportingService.getTiersReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/customer-order")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getCustomerOrderReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				customerOrderReportingService.getCustomerOrderReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/provision")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getProvisionReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				provisionReportingService.getProvisionReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/provision-production")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getProvisionProductionReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				productionReportingService.getProvisionProductionReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/recovery")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getRecoveryReporting(
			@RequestParam ArrayList<String> columns)
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<ArrayList<HashMap<String, String>>>(
				recoveryReportingService.getRecoveryReporting(columns),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user-reportings")
	public ResponseEntity<List<UserReporting>> getUserReportings(@RequestParam Integer employeeId)
			throws OsirisValidationException {
		Employee employee = employeeService.getEmployee(employeeId);

		if (employee == null)
			throw new OsirisValidationException("employeeId");

		return new ResponseEntity<List<UserReporting>>(userReportingService.getUserReportings(employee), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user-reporting")
	public ResponseEntity<UserReporting> getUserReporting(@RequestParam Integer id)
			throws OsirisValidationException {
		return new ResponseEntity<UserReporting>(userReportingService.getUserReporting(id), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/user-reporting")
	public ResponseEntity<UserReporting> addOrUpdateUserReporting(
			@RequestBody UserReporting userReportings) throws OsirisValidationException, OsirisException {
		if (userReportings.getId() != null)
			validationHelper.validateReferential(userReportings, true, "userReportings");
		validationHelper.validateString(userReportings.getDataset(), true, "Dataset");
		validationHelper.validateString(userReportings.getSettings(), true, "Settings");
		validationHelper.validateString(userReportings.getName(), true, "Name");
		validationHelper.validateReferential(userReportings.getEmployee(), true, "Employee");

		return new ResponseEntity<UserReporting>(userReportingService.addOrUpdateUserReporting(userReportings),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user-reporting/copy")
	public ResponseEntity<UserReporting> getUserReporting(@RequestParam Integer userReportingId,
			@RequestParam Integer employeeId)
			throws OsirisValidationException {
		Employee employee = employeeService.getEmployee(employeeId);
		if (employee == null)
			throw new OsirisValidationException("employeeId");

		UserReporting userReporting = userReportingService.getUserReporting(userReportingId);
		if (userReporting == null)
			throw new OsirisValidationException("userReportingId");

		userReportingService.copyUserReportingToUser(userReporting, employee);

		return new ResponseEntity<UserReporting>(userReporting, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user-reporting/delete")
	public ResponseEntity<UserReporting> deleteUserReporting(@RequestParam Integer userReportingId)
			throws OsirisValidationException {
		UserReporting userReporting = userReportingService.getUserReporting(userReportingId);
		if (userReporting == null)
			throw new OsirisValidationException("userReportingId");

		if (employeeService.getCurrentEmployee() != null
				&& !userReporting.getEmployee().getId().equals(employeeService.getCurrentEmployee().getId())
				&& activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ADMINISTRATEUR_GROUP))
			throw new OsirisValidationException("forbidden");

		userReportingService.deleteReporting(userReporting);

		return new ResponseEntity<UserReporting>(userReporting, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/user-reporting/fake")
	public ResponseEntity<ArrayList<HashMap<String, String>>> getFakeData(@RequestParam String dataset)
			throws OsirisValidationException {

		if (dataset.equals(UserReporting.REPORTING_DATASET_TURNOVER_AMOUNT))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(turnoverReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_ANNOUNCEMENT))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(announcementReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_CUSTOMER_ORDER))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(customerOrderReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_PROVISION))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(provisionReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_PROVISION_PRODUCTION))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(productionReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_QUOTATION))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(quotationReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_RECOVERY))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(recoveryReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_TIERS))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(tiersReportingService.getFakeData(),
					HttpStatus.OK);
		if (dataset.equals(UserReporting.REPORTING_DATASET_TURNOVER_VAT_AMOUNT))
			return new ResponseEntity<ArrayList<HashMap<String, String>>>(turnoverVatReportingService.getFakeData(),
					HttpStatus.OK);

		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/incident-report")
	public ResponseEntity<IncidentReport> addOrUpdateIncidentReport(
			@RequestBody IncidentReport incidentReports) throws OsirisValidationException, OsirisException {
		if (incidentReports.getId() != null)
			validationHelper.validateReferential(incidentReports, true, "incidentReports");
		validationHelper.validateReferential(incidentReports.getIncidentReportStatus(), false, "incidentReportStatus");
		validationHelper.validateReferential(incidentReports.getIncidentResponsibility(), false,
				"incidentResponsibility");
		validationHelper.validateReferential(incidentReports.getIncidentType(), false, "incidentType");
		validationHelper.validateReferential(incidentReports.getIncidentCause(), false, "incidentCause");
		validationHelper.validateReferential(incidentReports.getAssignedTo(), false, "assignedTo");
		validationHelper.validateReferential(incidentReports.getInitiatedBy(), false, "initiatedBy");
		validationHelper.validateString(incidentReports.getTitle(), true, 200, "title");
		validationHelper.validateDate(incidentReports.getStartDate(), false, "startDate");
		validationHelper.validateDate(incidentReports.getEndDate(), false, "endDate");
		incidentReports.setCustomerOrder((CustomerOrder) validationHelper
				.validateReferential(incidentReports.getCustomerOrder(), true, "customerOrder"));
		incidentReports.setProvision(
				(Provision) validationHelper.validateReferential(incidentReports.getProvision(), true, "provision"));

		return new ResponseEntity<IncidentReport>(incidentReportService.addOrUpdateIncidentReport(incidentReports),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-report-status-list")
	public ResponseEntity<List<IncidentReportStatus>> getIncidentReportStatusList() {
		return new ResponseEntity<List<IncidentReportStatus>>(incidentReportStatusService.getIncidentReportStatusList(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-report/tiers")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<IncidentReport>> getIncidentReportsForTiers(
			@RequestParam Integer idTiers)
			throws OsirisValidationException {
		Tiers tiers = tiersService.getTiers(idTiers);
		if (tiers == null)
			throw new OsirisValidationException("tiers");
		return new ResponseEntity<List<IncidentReport>>(
				incidentReportService.getIncidentReportByTiers(tiers),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-report/responsable")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<IncidentReport>> getIncidentReportsForReponsable(
			@RequestParam Integer idResponsable)
			throws OsirisValidationException {
		Responsable responsable = responsableService.getResponsable(idResponsable);
		if (responsable == null)
			throw new OsirisValidationException("responsable");
		return new ResponseEntity<List<IncidentReport>>(
				incidentReportService.getIncidentReportByResponsable(responsable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-report/order")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<IncidentReport>> getIncidentReportsForCustomerOrder(
			@RequestParam Integer idCustomerOrder)
			throws OsirisValidationException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null)
			throw new OsirisValidationException("indicator");
		return new ResponseEntity<List<IncidentReport>>(
				incidentReportService.getIncidentReportsForCustomerOrder(customerOrder),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/incident-report/search")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<IncidentReport>> searchIncidentReport(
			@RequestParam(required = false) List<Integer> employeeIds,
			@RequestParam(required = false) List<Integer> statusIds)
			throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

		List<Employee> employees = new ArrayList<Employee>();
		if (employeeIds != null)
			for (Integer id : employeeIds) {
				Employee employee = employeeService.getEmployee(id);
				if (employee == null)
					throw new OsirisValidationException("employeeIds");
				else
					employees.add(employee);
			}

		List<IncidentReportStatus> status = new ArrayList<IncidentReportStatus>();
		if (statusIds != null)
			for (Integer id : statusIds) {
				IncidentReportStatus statu = incidentReportStatusService.getIncidentReportStatus(id);
				if (statu == null)
					throw new OsirisValidationException("statusIds");
				else
					status.add(statu);
			}

		return new ResponseEntity<List<IncidentReport>>(
				incidentReportService.searchIncidentReport(employees, status),
				HttpStatus.OK);
	}

}
