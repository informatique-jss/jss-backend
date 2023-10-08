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
import com.jss.osiris.modules.reporting.model.IAnnouncementReporting;
import com.jss.osiris.modules.reporting.model.ICustomerOrderReporting;
import com.jss.osiris.modules.reporting.model.IProvisionReporting;
import com.jss.osiris.modules.reporting.model.IQuotationReporting;
import com.jss.osiris.modules.reporting.model.ITiersReporting;
import com.jss.osiris.modules.reporting.model.ITurnoverReporting;
import com.jss.osiris.modules.reporting.model.UserReporting;
import com.jss.osiris.modules.reporting.service.AnnouncementReportingService;
import com.jss.osiris.modules.reporting.service.CustomerOrderReportingService;
import com.jss.osiris.modules.reporting.service.ProvisionReportingService;
import com.jss.osiris.modules.reporting.service.QuotationReportingService;
import com.jss.osiris.modules.reporting.service.TiersReportingService;
import com.jss.osiris.modules.reporting.service.TurnoverReportingService;
import com.jss.osiris.modules.reporting.service.UserReportingService;

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
	CustomerOrderReportingService customerOrderReportingService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	UserReportingService userReportingService;

	@Autowired
	ProvisionReportingService provisionReportingService;

	@Autowired
	AnnouncementReportingService announcementReportingService;

	@Autowired
	TiersReportingService tiersReportingService;

	@GetMapping(inputEntryPoint + "/quotation")
	public ResponseEntity<List<IQuotationReporting>> getQuotationReporting()
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<List<IQuotationReporting>>(quotationReportingService.getQuotationReporting(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/turnover")
	public ResponseEntity<List<ITurnoverReporting>> getTurnoverReporting()
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<List<ITurnoverReporting>>(turnoverReportingService.getTurnoverReporting(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement")
	public ResponseEntity<List<IAnnouncementReporting>> getAnnouncementReporting()
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<List<IAnnouncementReporting>>(announcementReportingService.getAnnouncementReporting(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tiers")
	public ResponseEntity<List<ITiersReporting>> getTiersReporting()
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<List<ITiersReporting>>(tiersReportingService.getTiersReporting(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/customer-order")
	public ResponseEntity<List<ICustomerOrderReporting>> getCustomerOrderReporting()
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<List<ICustomerOrderReporting>>(
				customerOrderReportingService.getCustomerOrderReporting(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/provision")
	public ResponseEntity<List<IProvisionReporting>> getProvisionReporting()
			throws OsirisValidationException, OsirisException {
		return new ResponseEntity<List<IProvisionReporting>>(
				provisionReportingService.getProvisionReporting(),
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

}
