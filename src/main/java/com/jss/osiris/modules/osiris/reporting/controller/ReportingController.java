package com.jss.osiris.modules.osiris.reporting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.UserReporting;
import com.jss.osiris.modules.osiris.reporting.service.AnnouncementReportingService;
import com.jss.osiris.modules.osiris.reporting.service.CustomerOrderReportingService;
import com.jss.osiris.modules.osiris.reporting.service.ProvisionProductionReportingService;
import com.jss.osiris.modules.osiris.reporting.service.ProvisionReportingService;
import com.jss.osiris.modules.osiris.reporting.service.QuotationReportingService;
import com.jss.osiris.modules.osiris.reporting.service.RecoveryReportingService;
import com.jss.osiris.modules.osiris.reporting.service.TiersReportingService;
import com.jss.osiris.modules.osiris.reporting.service.TurnoverReportingService;
import com.jss.osiris.modules.osiris.reporting.service.TurnoverVatReportingService;
import com.jss.osiris.modules.osiris.reporting.service.UserReportingService;

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

		if (!userReporting.getEmployee().getId().equals(employeeService.getCurrentEmployee().getId())
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

}
