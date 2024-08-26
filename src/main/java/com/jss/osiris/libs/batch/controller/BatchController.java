package com.jss.osiris.libs.batch.controller;

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

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.model.BatchCategory;
import com.jss.osiris.libs.batch.model.BatchSearch;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.model.BatchStatus;
import com.jss.osiris.libs.batch.model.IBatchStatistics;
import com.jss.osiris.libs.batch.model.IBatchTimeStatistics;
import com.jss.osiris.libs.batch.service.BatchCategoryService;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.batch.service.BatchSettingsService;
import com.jss.osiris.libs.batch.service.BatchStatusService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
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
	BatchStatusService batchStatusService;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	BatchCategoryService batchCategoryService;

	@GetMapping(inputEntryPoint + "/batch-categories")
	public ResponseEntity<List<BatchCategory>> getBatchCategories() {
		return new ResponseEntity<List<BatchCategory>>(batchCategoryService.getBatchCategories(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/batch-status")
	public ResponseEntity<List<BatchStatus>> getBatchStatus() {
		return new ResponseEntity<List<BatchStatus>>(batchStatusService.getBatchStatus(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/statistics")
	public ResponseEntity<List<IBatchStatistics>> getBatchStatistics() {
		return new ResponseEntity<List<IBatchStatistics>>(batchService.getBatchStatistics(), HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@GetMapping(inputEntryPoint + "/statistics/time")
	public ResponseEntity<List<IBatchTimeStatistics>> getBatchTimeStatistics(@RequestParam Integer batchSettingsId)
			throws OsirisValidationException {
		BatchSettings batchSettings = batchSettingsService.getBatchSettings(batchSettingsId);

		if (batchSettings == null)
			throw new OsirisValidationException("batchSettingsId");

		return new ResponseEntity<List<IBatchTimeStatistics>>(batchService.getTimeStatisticsOfBatch(batchSettings),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/settings")
	public ResponseEntity<List<BatchSettings>> getBatchSettings() {
		return new ResponseEntity<List<BatchSettings>>(batchSettingsService.getAllBatchSettings(), HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@PostMapping(inputEntryPoint + "/batch/search")
	public ResponseEntity<List<Batch>> searchBatchs(@RequestBody BatchSearch batchSearch)
			throws OsirisValidationException, OsirisException, OsirisClientMessageException {
		return new ResponseEntity<List<Batch>>(batchService.searchBatchs(batchSearch),
				HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@PostMapping(inputEntryPoint + "/batch/status")
	public ResponseEntity<Batch> addOrUpdateBatchStatus(@RequestBody Batch batch)
			throws OsirisValidationException, OsirisException, OsirisClientMessageException {
		BatchStatus status = batch.getBatchStatus();
		batch = (Batch) validationHelper.validateReferential(batch, true, "batch");
		batch.setBatchStatus(status);
		return new ResponseEntity<Batch>(batchService.addOrUpdateBatch(batch),
				HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@PostMapping(inputEntryPoint + "/batch/new")
	public ResponseEntity<Batch> declareNewBatch(@RequestParam Integer batchSettingsId,
			@RequestParam(name = "entityId", required = false) Integer entityId)
			throws OsirisValidationException, OsirisException, OsirisClientMessageException {
		BatchSettings batchSettings = batchSettingsService.getBatchSettings(batchSettingsId);
		if (batchSettings == null)
			throw new OsirisValidationException("batchSettingsId");
		return new ResponseEntity<Batch>(batchService.declareNewBatch(batchSettings.getCode(), entityId),
				HttpStatus.OK);
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

		return new ResponseEntity<BatchSettings>(batchSettingsService.addOrUpdateBatchSettings(batchSetting),
				HttpStatus.OK);
	}
}