package com.jss.osiris.modules.myjss.tiers.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.myjss.quotation.controller.MyJssQuotationValidationHelper;
import com.jss.osiris.modules.osiris.accounting.model.BillingClosureReceiptValue;
import com.jss.osiris.modules.osiris.accounting.service.BillingClosureReceiptHelper;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@RestController
public class MyJssTiersController {

	private static final String inputEntryPoint = "/myjss/tiers";

	@Autowired
	UserScopeService userScopeService;

	@Autowired
	ResponsableService responsableService;

	@Autowired
	MyJssQuotationValidationHelper myJssQuotationValidationHelper;

	@Autowired
	BillingClosureReceiptHelper billingClosureReceiptHelper;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	@GetMapping(inputEntryPoint + "/billing-closure")
	public ResponseEntity<List<BillingClosureReceiptValue>> getBillingClosureReceiptValueForResponsable(
			@RequestParam Integer responsableId, @RequestParam boolean isOrderingByEventDate)
			throws OsirisException {

		Responsable responsable = responsableService.getResponsable(responsableId);
		if (!myJssQuotationValidationHelper.canSeeResponsable(responsable))
			return new ResponseEntity<List<BillingClosureReceiptValue>>(new ArrayList<BillingClosureReceiptValue>(),
					HttpStatus.OK);

		List<BillingClosureReceiptValue> values = billingClosureReceiptHelper
				.generateBillingClosureValuesForITiers(responsable.getTiers(), responsable, isOrderingByEventDate,
						true, true);

		values.sort(new Comparator<BillingClosureReceiptValue>() {
			@Override
			public int compare(BillingClosureReceiptValue o1, BillingClosureReceiptValue o2) {
				if (o2.getEventDateTime() == null && o1.getEventDateTime() != null)
					return -1;
				if (o2.getEventDateTime() != null && o1.getEventDateTime() == null)
					return 1;
				if (o1.getEventDateTime() == null && o2.getEventDateTime() == null)
					return 0;
				return o1.getEventDateTime().isAfter(o2.getEventDateTime()) ? 1 : -1;
			}
		});

		return new ResponseEntity<List<BillingClosureReceiptValue>>(values, HttpStatus.OK);
	}

}
