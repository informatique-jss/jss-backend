package com.jss.osiris.modules.myjss.invoicing.controller;

import java.util.ArrayList;
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
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.modules.myjss.quotation.controller.MyJssQuotationValidationHelper;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@RestController
public class MyJssInvoicingController {

	private static final String inputEntryPoint = "/myjss/invoicing";

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	InvoiceHelper invoiceHelper;

	@Autowired
	DocumentService documentService;

	@Autowired
	ResponsableService responsableService;

	@Autowired
	MailComputeHelper mailComputeHelper;

	@Autowired
	QuotationService quotationService;

	@Autowired
	MyJssQuotationValidationHelper myJssQuotationValidationHelper;

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/invoice/label/compute/order")
	public ResponseEntity<InvoiceLabelResult> computeInvoiceLabelForCustomerOrder(@RequestParam Integer customerOrderId)
			throws OsirisException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<InvoiceLabelResult>(new InvoiceLabelResult(), HttpStatus.OK);

		return new ResponseEntity<InvoiceLabelResult>(invoiceHelper.computeInvoiceLabelResult(
				documentService.getBillingDocument(customerOrder.getDocuments()), customerOrder,
				responsableService.getResponsable(customerOrder.getResponsable().getId())), HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/invoice/label/physical/compute/order")
	public ResponseEntity<InvoiceLabelResult> computePhysicalMailComputeResultForBillingForCustomerOrder(
			@RequestParam Integer customerOrderId)
			throws OsirisException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<InvoiceLabelResult>(new InvoiceLabelResult(), HttpStatus.OK);

		return new ResponseEntity<InvoiceLabelResult>(mailComputeHelper.computePaperLabelResult(customerOrder),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/mail/billing/compute/order")
	public ResponseEntity<MailComputeResult> getMailComputeResultForBillingForCustomerOrder(
			@RequestParam Integer customerOrderId)
			throws OsirisException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<MailComputeResult>(new MailComputeResult(), HttpStatus.OK);

		return new ResponseEntity<MailComputeResult>(
				mailComputeHelper.computeMailForCustomerOrderFinalizationAndInvoice(customerOrder),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/mail/digital/compute/order")
	public ResponseEntity<MailComputeResult> getMailComputeResultForDigitalForCustomerOrder(
			@RequestParam Integer customerOrderId)
			throws OsirisException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<MailComputeResult>(new MailComputeResult(), HttpStatus.OK);

		return new ResponseEntity<MailComputeResult>(
				mailComputeHelper.computeMailForGenericDigitalDocument(customerOrder),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/invoice/label/compute/quotation")
	public ResponseEntity<InvoiceLabelResult> computeInvoiceLabelForQuotation(@RequestParam Integer quotationId)
			throws OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<InvoiceLabelResult>(new InvoiceLabelResult(), HttpStatus.OK);

		return new ResponseEntity<InvoiceLabelResult>(invoiceHelper.computeInvoiceLabelResult(
				documentService.getBillingDocument(quotation.getDocuments()), quotation,
				responsableService.getResponsable(quotation.getResponsable().getId())), HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/invoice/label/physical/compute/quotation")
	public ResponseEntity<InvoiceLabelResult> computePhysicalMailComputeResultForBillingForQuotation(
			@RequestParam Integer quotationId)
			throws OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<InvoiceLabelResult>(new InvoiceLabelResult(), HttpStatus.OK);

		return new ResponseEntity<InvoiceLabelResult>(mailComputeHelper.computePaperLabelResult(quotation),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/mail/billing/compute/quotation")
	public ResponseEntity<MailComputeResult> getMailComputeResultForBillingForQuotation(
			@RequestParam Integer quotationId)
			throws OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<MailComputeResult>(new MailComputeResult(), HttpStatus.OK);

		return new ResponseEntity<MailComputeResult>(
				mailComputeHelper.computeMailForCustomerOrderFinalizationAndInvoice(quotation),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/mail/digital/compute/quotation")
	public ResponseEntity<MailComputeResult> getMailComputeResultForDigitalForQuotation(
			@RequestParam Integer quotationId)
			throws OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<MailComputeResult>(new MailComputeResult(), HttpStatus.OK);

		return new ResponseEntity<MailComputeResult>(
				mailComputeHelper.computeMailForGenericDigitalDocument(quotation),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/order/list")
	public ResponseEntity<List<Payment>> getApplicablePaymentsForCustomerOrder(
			@RequestParam Integer customerOrderId) throws OsirisException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<List<Payment>>(new ArrayList<Payment>(), HttpStatus.OK);

		return new ResponseEntity<List<Payment>>(
				customerOrderService.getApplicablePaymentsForCustomerOrder(customerOrder),
				HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/invoice/summary/order")
	public ResponseEntity<InvoicingSummary> getInvoicingSummaryForCustomerOrder(
			@RequestParam Integer customerOrderId) throws OsirisException {
		CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);

		if (customerOrder == null || !myJssQuotationValidationHelper.canSeeQuotation(customerOrder))
			return new ResponseEntity<InvoicingSummary>(new InvoicingSummary(), HttpStatus.OK);

		return new ResponseEntity<InvoicingSummary>(
				customerOrderService.getInvoicingSummaryForIQuotation(customerOrder), HttpStatus.OK);
	}

	@JsonView(JacksonViews.MyJssListView.class)
	@GetMapping(inputEntryPoint + "/invoice/summary/quotation")
	public ResponseEntity<InvoicingSummary> getInvoicingSummaryForQuotation(
			@RequestParam Integer quotationId) throws OsirisException {
		Quotation quotation = quotationService.getQuotation(quotationId);

		if (quotation == null || !myJssQuotationValidationHelper.canSeeQuotation(quotation))
			return new ResponseEntity<InvoicingSummary>(new InvoicingSummary(), HttpStatus.OK);

		return new ResponseEntity<InvoicingSummary>(
				customerOrderService.getInvoicingSummaryForIQuotation(quotation), HttpStatus.OK);
	}
}
