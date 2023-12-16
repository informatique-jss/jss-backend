package com.jss.osiris.libs.batch.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.model.IBatchStatistics;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.batch.service.BatchSettingsService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.service.BillingLabelTypeService;

@RestController
public class BatchController {

	private static final String inputEntryPoint = "/batch";

	@Autowired
	BillingLabelTypeService billingLabelTypeService;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	BatchSettingsService batchSettingsService;

	@Autowired
	BatchService batchService;

	@Autowired
	ValidationHelper validationHelper;

	@GetMapping(inputEntryPoint + "/statistics")
	public ResponseEntity<List<IBatchStatistics>> getBatchStatistics() {
		return new ResponseEntity<List<IBatchStatistics>>(batchService.getBatchStatistics(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/settings")
	public ResponseEntity<List<BatchSettings>> getBatchSettings() {
		return new ResponseEntity<List<BatchSettings>>(batchSettingsService.getAllBatchSettings(), HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@PostMapping(inputEntryPoint + "/settings")
	public ResponseEntity<BatchSettings> addOrUpdateBatchSetting(
			@RequestBody BatchSettings batchSetting)
			throws OsirisValidationException, OsirisException, OsirisClientMessageException {
		if (batchSetting.getId() != null)
			validationHelper.validateReferential(batchSetting, true, "batchSetting");
		validationHelper.validateString(batchSetting.getCode(), true, 250, "Code");
		validationHelper.validateString(batchSetting.getLabel(), true, 250, "Label");
		validationHelper.validateInteger(batchSetting.getFixedRate(), true, "FixedRate");
		validationHelper.validateInteger(batchSetting.getMaxAddedNumberPerIteration(), true,
				"MaxAddedNumberPerIteration");
		validationHelper.validateInteger(batchSetting.getQueueSize(), true, "QueueSize");

		return new ResponseEntity<BatchSettings>(batchSettingsService.addOrUpdBatchSettings(batchSetting),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/invoice/reminder")
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public ResponseEntity<Boolean> sendRemindersForInvoices(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam Integer billingLabelTypeId)
			throws OsirisException, OsirisClientMessageException, OsirisValidationException {
		BillingLabelType billingLabelType = billingLabelTypeService.getBillingLabelType(billingLabelTypeId);
		invoiceService.sendRemindersForInvoices(startDate, endDate, billingLabelType);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}