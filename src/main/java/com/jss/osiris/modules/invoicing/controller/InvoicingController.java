package com.jss.osiris.modules.invoicing.controller;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.InvoiceStatusService;

@RestController
public class InvoicingController {

    private static final String inputEntryPoint = "/invoicing";

    private static final Logger logger = LoggerFactory.getLogger(InvoicingController.class);

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @GetMapping(inputEntryPoint + "/invoice")
    public ResponseEntity<Invoice> getTiersById(@RequestParam Integer id) {
        Invoice invoice = null;
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            invoice = invoiceService.getInvoice(id);
            if (invoice == null)
                invoice = new Invoice();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching client types", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Invoice>(invoice, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice-status-list")
    public ResponseEntity<List<InvoiceStatus>> getInvoiceStatus() {
        List<InvoiceStatus> invoiceStatus = null;
        try {
            invoiceStatus = invoiceStatusService.getInvoiceStatus();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching invoiceStatus", e);
            return new ResponseEntity<List<InvoiceStatus>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching invoiceStatus", e);
            return new ResponseEntity<List<InvoiceStatus>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<InvoiceStatus>>(invoiceStatus, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice-status")
    public ResponseEntity<InvoiceStatus> addOrUpdateInvoiceStatus(
            @RequestBody InvoiceStatus invoiceStatus) {
        InvoiceStatus outInvoiceStatus;
        try {
            if (invoiceStatus.getId() != null)
                validationHelper.validateReferential(invoiceStatus, true);
            validationHelper.validateString(invoiceStatus.getCode(), true);
            validationHelper.validateString(invoiceStatus.getLabel(), true);

            outInvoiceStatus = invoiceStatusService
                    .addOrUpdateInvoiceStatus(invoiceStatus);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching invoiceStatus", e);
            return new ResponseEntity<InvoiceStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching invoiceStatus", e);
            return new ResponseEntity<InvoiceStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<InvoiceStatus>(outInvoiceStatus, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice")
    public ResponseEntity<Invoice> addOrUpdateInvoice(
            @RequestBody Invoice invoice) {
        Invoice outInvoice;
        try {
            if (invoice.getId() != null)
                validationHelper.validateReferential(invoice, true);
            validationHelper.validateDateTime(invoice.getCreatedDate(), true);

            outInvoice = invoiceService
                    .addOrUpdateInvoice(invoice);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching invoice", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching invoice", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Invoice>(outInvoice, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice/customer-order")
    public ResponseEntity<Invoice> getInvoiceForCustomerOrder(@RequestParam Integer customerOrderId) {
        Invoice invoice = null;
        try {
            invoice = invoiceService.getInvoiceForCustomerOrder(customerOrderId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching regie", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching regie", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Invoice>(invoice, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice/search")
    public ResponseEntity<List<Invoice>> searchInvoices(@RequestBody InvoiceSearch invoiceSearch) {
        List<Invoice> invoices;
        try {
            if (invoiceSearch == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (invoiceSearch.getInvoiceStatus() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (invoiceSearch.getStartDate() == null || invoiceSearch.getEndDate() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Duration duration = Duration.between(invoiceSearch.getStartDate(),
                    invoiceSearch.getEndDate());

            if (duration.toDays() > 366)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            invoices = invoiceService.searchInvoices(invoiceSearch);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<Invoice>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<Invoice>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Invoice>>(invoices, HttpStatus.OK);
    }

}