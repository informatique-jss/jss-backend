package com.jss.osiris.modules.invoicing.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentAssociate;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.InvoiceStatusService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.invoicing.service.PaymentWayService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.ITiers;

@RestController
public class InvoicingController {

    private static final String inputEntryPoint = "/invoicing";

    private static final Logger logger = LoggerFactory.getLogger(InvoicingController.class);

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentWayService paymentWayService;

    @Value("${invoicing.invoice.status.send.code}")
    private String invoiceStatusSendCode;

    @Autowired
    ConstantService constantService;

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

    @PostMapping(inputEntryPoint + "/payments/associate")
    public ResponseEntity<Boolean> associatePaymentAndInvoiceAndCustomerOrder(
            @RequestBody PaymentAssociate paymentAssociate) {

        try {
            if (paymentAssociate == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            paymentAssociate
                    .setPayment((Payment) validationHelper.validateReferential(paymentAssociate.getPayment(), true));

            if (paymentAssociate.getInvoices() != null) {
                if (paymentAssociate.getInvoices().size() == 0)
                    paymentAssociate.setInvoices(null);

                for (Invoice invoice : paymentAssociate.getInvoices()) {
                    invoice = (Invoice) validationHelper.validateReferential(invoice, true);
                    if (paymentAssociate.getPayment().getInvoice() != null
                            && invoice.getId().equals(paymentAssociate.getPayment().getInvoice().getId()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

                    if (!invoice.getInvoiceStatus().getCode().equals(invoiceStatusSendCode))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            if (paymentAssociate.getCustomerOrders() != null) {
                if (paymentAssociate.getCustomerOrders().size() == 0)
                    paymentAssociate.setCustomerOrders(null);

                for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                    customerOrder = (CustomerOrder) validationHelper.validateReferential(customerOrder, true);
                    if (paymentAssociate.getPayment().getCustomerOrder() != null
                            && customerOrder.getId().equals(paymentAssociate.getPayment().getCustomerOrder().getId()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            paymentAssociate
                    .setAffaire((Affaire) validationHelper.validateReferential(paymentAssociate.getAffaire(), false));

            if (paymentAssociate.getByPassAmount() == null || paymentAssociate.getByPassAmount()
                    .size() != (paymentAssociate.getInvoices() == null ? 0 : paymentAssociate.getInvoices().size())
                            + (paymentAssociate.getCustomerOrders() == null ? 0
                                    : paymentAssociate.getCustomerOrders().size()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Float totalAmount = 0f;
            for (Float amount : paymentAssociate.getByPassAmount()) {
                totalAmount += amount;
            }
            totalAmount = Math.round(totalAmount * 100f) / 100f;

            // Mandatory because we need customer order to get customer accounting account
            if (paymentAssociate.getTiersRefund() == null && paymentAssociate.getConfrereRefund() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            validationHelper.validateReferential(paymentAssociate.getTiersRefund(), false);
            validationHelper.validateReferential(paymentAssociate.getConfrereRefund(), false);

            if (paymentAssociate.getPayment().getPaymentAmount() < totalAmount)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (paymentAssociate.getPayment().getPaymentAmount() > totalAmount
                    && paymentAssociate.getTiersRefund() == null && paymentAssociate.getAffaire() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            ITiers commonCustomerOrder = paymentAssociate.getTiersRefund() != null ? paymentAssociate.getTiersRefund()
                    : paymentAssociate.getConfrereRefund();
            if (paymentAssociate.getInvoices() != null) {
                for (Invoice invoice : paymentAssociate.getInvoices())
                    if (!invoiceHelper.getCustomerOrder(invoice).getId().equals(commonCustomerOrder.getId()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (paymentAssociate.getCustomerOrders() != null) {
                for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                    if (customerOrder.getResponsable() != null
                            && !customerOrder.getResponsable().getTiers().getId().equals(commonCustomerOrder.getId()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (customerOrder.getConfrere() != null
                            && !customerOrder.getConfrere().getId().equals(commonCustomerOrder.getId()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (!customerOrder.getTiers().getId().equals(commonCustomerOrder.getId()))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            paymentService.manualMatchPaymentInvoicesAndGeneratePaymentAccountingRecords(
                    paymentAssociate.getPayment(),
                    paymentAssociate.getInvoices(), paymentAssociate.getCustomerOrders(),
                    paymentAssociate.getAffaire(),
                    commonCustomerOrder, paymentAssociate.getByPassAmount());
        } catch (ResponseStatusException e) {
            return new ResponseEntity<Boolean>(e.getStatus());
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching payment", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching payment", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
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
    public ResponseEntity<Invoice> getInvoiceById(@RequestParam Integer id) {
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

    @PostMapping(inputEntryPoint + "/invoice")
    public ResponseEntity<Invoice> addOrUpdateInvoice(@RequestBody Invoice invoice) {
        Invoice outInvoice;
        try {
            if (invoice.getId() != null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (invoice.getCustomerOrder() != null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            int doFound = 0;

            if (invoice.getTiers() != null) {
                validationHelper.validateReferential(invoice.getTiers(), true);
                doFound++;
            }

            if (invoice.getResponsable() != null) {
                validationHelper.validateReferential(invoice.getResponsable(), true);
                doFound++;
            }

            if (invoice.getConfrere() != null) {
                validationHelper.validateReferential(invoice.getConfrere(), true);
                doFound++;
            }

            if (invoice.getProvider() != null) {
                validationHelper.validateReferential(invoice.getProvider(), true);
                doFound++;
            }

            if (doFound != 1)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            BillingLabelType billingLabelAffaire = constantService.getBillingLabelTypeCodeAffaire();

            validationHelper.validateReferential(invoice.getBillingLabelType(), true);
            validationHelper.validateString(invoice.getBillingLabelAddress(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 160);
            validationHelper.validateString(invoice.getBillingLabel(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 40);
            validationHelper.validateString(invoice.getBillingLabelPostalCode(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 10);
            validationHelper.validateReferential(invoice.getBillingLabelCity(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()));
            validationHelper.validateReferential(invoice.getBillingLabelCountry(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()));
            validationHelper.validateString(invoice.getBillingLabelPostalCode(), false, 40);
            validationHelper.validateReferential(invoice.getInvoiceStatus(), false);
            validationHelper.validateDate(invoice.getDueDate(), false);

            if (invoice.getInvoiceItems() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                    if (invoiceItem.getId() != null)
                        validationHelper.validateReferential(invoiceItem, true);
                    if (invoiceItem.getDiscountAmount() == null)
                        invoiceItem.setDiscountAmount(0f);
                    if (invoiceItem.getPreTaxPrice() == null)
                        invoiceItem.setPreTaxPrice(0f);
                    if (invoiceItem.getVatPrice() == null)
                        invoiceItem.setVatPrice(0f);

                }
            }

            outInvoice = invoiceService.addOrUpdateInvoiceFromUser(invoice);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching invoiceStatus", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching invoiceStatus", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Invoice>(outInvoice, HttpStatus.OK);
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

    @GetMapping(inputEntryPoint + "/invoice/customer-order")
    public ResponseEntity<Invoice> getInvoiceForCustomerOrder(@RequestParam Integer customerOrderId) {
        Invoice invoice = null;
        try {
            invoice = invoiceService.getInvoiceForCustomerOrder(customerOrderId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching customer-order", e);
            return new ResponseEntity<Invoice>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching customer-order", e);
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