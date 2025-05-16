package com.jss.osiris.modules.osiris.reporting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorGroup;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;
import com.jss.osiris.modules.osiris.reporting.service.IndicatorGroupService;
import com.jss.osiris.modules.osiris.reporting.service.IndicatorService;
import com.jss.osiris.modules.osiris.reporting.service.IndicatorValueService;

@RestController
public class IndicatorController {

	private static final String inputEntryPoint = "/indicator";

	@Autowired
	IndicatorService indicatorService;

	@Autowired
	IndicatorValueService indicatorValueService;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	IndicatorGroupService indicatorGroupService;

	@Autowired
	EmployeeService employeeService;

	@GetMapping(inputEntryPoint + "/indicator-groups")
	public ResponseEntity<List<IndicatorGroup>> getIndicatorGroups() {
		return new ResponseEntity<List<IndicatorGroup>>(indicatorGroupService.getIndicatorGroups(), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/indicator-group")
	public ResponseEntity<IndicatorGroup> addOrUpdateIndicatorGroup(
			@RequestBody IndicatorGroup indicatorGroups) throws OsirisValidationException, OsirisException {
		if (indicatorGroups.getId() != null)
			validationHelper.validateReferential(indicatorGroups, true, "indicatorGroups");
		validationHelper.validateString(indicatorGroups.getCode(), true, "code");
		validationHelper.validateString(indicatorGroups.getLabel(), true, "label");

		return new ResponseEntity<IndicatorGroup>(indicatorGroupService.addOrUpdateIndicatorGroup(indicatorGroups),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/indicators")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<Indicator>> getIndicators() {
		return new ResponseEntity<List<Indicator>>(indicatorService.getIndicators(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/indicator")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<Indicator> getIndicator(@RequestParam Integer id) {
		return new ResponseEntity<Indicator>(indicatorService.getIndicator(id), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/indicator")
	public ResponseEntity<Indicator> addOrUpdateIndicator(
			@RequestBody Indicator indicators) throws OsirisValidationException, OsirisException {
		if (indicators.getId() != null)
			validationHelper.validateReferential(indicators, true, "indicators");
		validationHelper.validateString(indicators.getCode(), true, "code");
		validationHelper.validateString(indicators.getLabel(), true, "label");
		validationHelper.validateReferential(indicators.getIndicatorGroup(), true, "indicatorGroup");

		return new ResponseEntity<Indicator>(indicatorService.addOrUpdateIndicator(indicators), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/indicator/values")
	@JsonView(JacksonViews.OsirisListView.class)
	public ResponseEntity<List<IndicatorValue>> getIndicatorValuesForIndicator(@RequestParam Integer idindicator)
			throws OsirisValidationException {

		Indicator indicator = indicatorService.getIndicator(idindicator);
		if (indicator == null)
			throw new OsirisValidationException("indicator");
		return new ResponseEntity<List<IndicatorValue>>(indicatorValueService.getIndicatorValuesForIndicator(indicator),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.OsirisDetailedView.class)
	@GetMapping(inputEntryPoint + "/indicator/values/employee")
	public ResponseEntity<List<IndicatorValue>> getIndicatorValuesForEmployee(@RequestParam Integer idEmployee)
			throws OsirisValidationException {

		Employee employee = employeeService.getEmployee(idEmployee);
		if (employee == null)
			throw new OsirisValidationException("employee");
		return new ResponseEntity<List<IndicatorValue>>(
				indicatorValueService.getLatestIndicatorValuesForEmployee(employee),
				HttpStatus.OK);
	}
}
