package com.jss.osiris.modules.invoicing.controller;

import java.time.Duration;
import java.time.LocalDateTime;
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
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.InvoiceStatusService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.invoicing.service.PaymentWayService;

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

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentWayService paymentWayService;

    @GetMapping(inputEntryPoint + "/payment-ways")
    public ResponseEntity<List<PaymentWay>> getPaymentWays() {
        List<PaymentWay> paymentWays = null;
        try {
            paymentWays = paymentWayService.getPaymentWays();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching paymentWay", e);
            return new ResponseEntity<List<PaymentWay>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching paymentWay", e);
            return new ResponseEntity<List<PaymentWay>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<PaymentWay>>(paymentWays, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payment-way")
    public ResponseEntity<PaymentWay> addOrUpdatePaymentWay(
            @RequestBody PaymentWay paymentWays) {
        PaymentWay outPaymentWay;
        try {
            if (paymentWays.getId() != null)
                validationHelper.validateReferential(paymentWays, true);
            validationHelper.validateString(paymentWays.getCode(), true);
            validationHelper.validateString(paymentWays.getLabel(), true);

            outPaymentWay = paymentWayService
                    .addOrUpdatePaymentWay(paymentWays);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching paymentWay", e);
            return new ResponseEntity<PaymentWay>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching paymentWay", e);
            return new ResponseEntity<PaymentWay>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<PaymentWay>(outPaymentWay, HttpStatus.OK);
    }

    // TODO : à retirer avant la MEP, seulement pour test !
    @PostMapping(inputEntryPoint + "/payment")
    public ResponseEntity<Payment> addOrUpdatePayment(@RequestBody Payment payment) {
        Payment outPayment;
        try {
            if (payment.getId() != null)
                validationHelper.validateReferential(payment, true);
            validationHelper.validateString(payment.getLabel(), true);
            validationHelper.validateFloat(payment.getPaymentAmount(), true);

            payment.setPaymentDate(LocalDateTime.now());
            outPayment = paymentService.addOrUpdatePayment(payment);
            paymentService.payementGrab();
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching paymentWay", e);
            return new ResponseEntity<Payment>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching paymentWay", e);
            return new ResponseEntity<Payment>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Payment>(outPayment, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payments/search")
    public ResponseEntity<List<Payment>> getPayments(@RequestBody PaymentSearch paymentSearch) {
        List<Payment> payments;
        if (paymentSearch == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (paymentSearch.getStartDate() == null || paymentSearch.getEndDate() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (paymentSearch.getPaymentWays() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(paymentSearch.getStartDate(), paymentSearch.getEndDate());

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {
            validationHelper.validateFloat(paymentSearch.getMinAmount(), false);
            validationHelper.validateFloat(paymentSearch.getMaxAmount(), false);
            payments = paymentService.searchPayments(paymentSearch);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching payment", e);
            return new ResponseEntity<List<Payment>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching payment", e);
            return new ResponseEntity<List<Payment>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Payment>>(payments, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payments/advise")
    public ResponseEntity<List<Payment>> getPaymentAdvised(@RequestParam Integer invoiceId) {
        List<Payment> payments;

        try {
            Invoice invoice = invoiceService.getInvoice(invoiceId);
            if (invoice == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            payments = paymentService.getAdvisedPaymentForInvoice(invoice);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching payment", e);
            return new ResponseEntity<List<Payment>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching payment", e);
            return new ResponseEntity<List<Payment>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Payment>>(payments, HttpStatus.OK);
    }

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