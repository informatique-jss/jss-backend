package com.jss.osiris.modules.reporting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.reporting.model.IQuotationReporting;
import com.jss.osiris.modules.reporting.service.QuotationReportingService;

@RestController
public class ReportingController {

	private static final String inputEntryPoint = "/reporting";

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	QuotationReportingService quotationReportingService;

	@PostMapping(inputEntryPoint + "/quotation")
	public ResponseEntity<List<IQuotationReporting>> getQuotationReporting()
			throws OsirisValidationException, OsirisException {

		return new ResponseEntity<List<IQuotationReporting>>(quotationReportingService.getQuotationReporting(),
				HttpStatus.OK);
	}

}
