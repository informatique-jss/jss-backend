package com.jss.osiris.modules.reporting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.reporting.model.IQuotationReporting;
import com.jss.osiris.modules.reporting.model.UserReporting;
import com.jss.osiris.modules.reporting.service.QuotationReportingService;
import com.jss.osiris.modules.reporting.service.UserReportingService;

@RestController
public class ReportingController {

	private static final String inputEntryPoint = "/reporting";

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	QuotationReportingService quotationReportingService;

	@Autowired
	EmployeeService employeeService;

	@GetMapping(inputEntryPoint + "/quotation")
	public ResponseEntity<List<IQuotationReporting>> getQuotationReporting()
			throws OsirisValidationException, OsirisException {

		return new ResponseEntity<List<IQuotationReporting>>(quotationReportingService.getQuotationReporting(0),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/tiers")
	public ResponseEntity<List<IQuotationReporting>> getQuotationReportingForTiers(@RequestParam Integer tiersId)
			throws OsirisValidationException, OsirisException {

		return new ResponseEntity<List<IQuotationReporting>>(quotationReportingService.getQuotationReporting(tiersId),
				HttpStatus.OK);
	}

	@Autowired
	UserReportingService userReportingService;

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

}
