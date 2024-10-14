package com.jss.osiris.modules.myjss.quotation.controller;

import java.util.ArrayList;
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
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;

@RestController
public class MyJssQuotationController {

	private static final String inputEntryPoint = "/myjss/quotation";

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	QuotationService quotationService;

	@Autowired
	AssoAffaireOrderService assoAffaireOrderService;

	@Autowired
	MyJssQuotationValidationHelper myJssQuotationValidationHelper;

	@PostMapping(inputEntryPoint + "/order/search/current")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<CustomerOrder>> searchOrdersForCurrentUser(
			@RequestBody List<String> customerOrderStatus, @RequestParam Integer page, @RequestParam String sortBy)
			throws OsirisClientMessageException {
		if (customerOrderStatus == null || customerOrderStatus.size() == 0)
			return new ResponseEntity<List<CustomerOrder>>(new ArrayList<CustomerOrder>(), HttpStatus.OK);

		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("createdDateDesc") && !sortBy.equals("createdDateAsc")
				&& !sortBy.equals("statusAsc"))
			sortBy = "createdDateDesc";

		return new ResponseEntity<List<CustomerOrder>>(
				customerOrderService.searchOrdersForCurrentUser(customerOrderStatus, page, sortBy), HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/quotation/search/current")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<Quotation>> searchQuotationsForCurrentUser(
			@RequestBody List<String> quotationStatus, @RequestParam Integer page, @RequestParam String sortBy)
			throws OsirisClientMessageException {
		if (quotationStatus == null || quotationStatus.size() == 0)
			return new ResponseEntity<List<Quotation>>(new ArrayList<Quotation>(), HttpStatus.OK);

		if (page == null || page < 0)
			page = 0;

		if (sortBy == null || !sortBy.equals("createdDateDesc") && !sortBy.equals("createdDateAsc")
				&& !sortBy.equals("statusAsc"))
			sortBy = "createdDateDesc";

		return new ResponseEntity<List<Quotation>>(
				quotationService.searchQuotationsForCurrentUser(quotationStatus, page, sortBy), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order/asso")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<AssoAffaireOrder>> getAssoAffaireOrderForCustomerOrder(
			@RequestParam Integer idCustomerOrder)
			throws OsirisClientMessageException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<List<AssoAffaireOrder>>(new ArrayList<AssoAffaireOrder>(), HttpStatus.OK);

		return new ResponseEntity<List<AssoAffaireOrder>>(
				assoAffaireOrderService.getAssoAffaireOrderForCustomerOrder(customerOrder), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/quotation/asso")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<AssoAffaireOrder>> getAssoAffaireOrderForQuotation(
			@RequestParam Integer idQuotation)
			throws OsirisClientMessageException {

		Quotation quotation = quotationService.getQuotation(idQuotation);
		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<List<AssoAffaireOrder>>(new ArrayList<AssoAffaireOrder>(), HttpStatus.OK);

		return new ResponseEntity<List<AssoAffaireOrder>>(
				assoAffaireOrderService.getAssoAffaireOrderForQuotation(quotation), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/order")
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<CustomerOrder> getCustomerOrder(@RequestParam Integer customerOrderId)
			throws OsirisClientMessageException {

		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);

		return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
	}
}
