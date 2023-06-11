package com.jss.osiris.modules.invoicing.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.modules.invoicing.model.Appoint;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoiceStatus;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearch;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.DebourSearch;
import com.jss.osiris.modules.invoicing.model.DebourSearchResult;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearch;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.InfogreffeInvoice;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.OwncloudGreffeInvoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentAssociate;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.invoicing.model.RefundSearch;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;
import com.jss.osiris.modules.invoicing.service.AppointService;
import com.jss.osiris.modules.invoicing.service.AzureInvoiceService;
import com.jss.osiris.modules.invoicing.service.AzureReceiptInvoiceService;
import com.jss.osiris.modules.invoicing.service.AzureReceiptService;
import com.jss.osiris.modules.invoicing.service.DepositService;
import com.jss.osiris.modules.invoicing.service.InfogreffeInvoiceService;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.InvoiceStatusService;
import com.jss.osiris.modules.invoicing.service.OwncloudGreffeDelegateImpl;
import com.jss.osiris.modules.invoicing.service.OwncloudGreffeInvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.invoicing.service.PaymentWayService;
import com.jss.osiris.modules.invoicing.service.RefundService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DebourService;
import com.jss.osiris.modules.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.ITiers;

@RestController
public class InvoicingController {

    private static final String inputEntryPoint = "/invoicing";

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    DepositService depositService;

    @Autowired
    PaymentWayService paymentWayService;

    @Autowired
    ConstantService constantService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    DocumentService documentService;

    @Autowired
    RefundService refundService;

    @Autowired
    MailComputeHelper mailComputeHelper;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    DirectDebitTransfertService directDebitTransfertService;

    @Autowired
    OwncloudGreffeInvoiceService owncloudGreffeInvoiceService;

    @Autowired
    DebourService debourService;

    @Autowired
    OwncloudGreffeDelegateImpl owncloudGreffeDelegateImpl;

    @Autowired
    ProvisionService provisionService;

    @Value("${invoicing.payment.limit.refund.euros}")
    private Integer payementLimitRefundInEuros;

    @Autowired
    InfogreffeInvoiceService infogreffeInvoiceService;

    @Autowired
    AppointService appointService;

    @Autowired
    AzureInvoiceService azureInvoiceService;

    @Autowired
    AzureReceiptInvoiceService azureReceiptInvoiceService;

    @Autowired
    AzureReceiptService azureReceiptService;

    @PostMapping(inputEntryPoint + "/azure-receipt/invoice")
    public ResponseEntity<AzureReceiptInvoice> updateAzureReceiptInvoice(
            @RequestBody AzureReceiptInvoice azureReceiptInvoice)
            throws OsirisValidationException, OsirisException {
        AzureReceiptInvoice currentInvoice = (AzureReceiptInvoice) validationHelper
                .validateReferential(azureReceiptInvoice, true, "azureReceipt");

        azureReceiptInvoice.setAzureReceipt(currentInvoice.getAzureReceipt());
        return new ResponseEntity<AzureReceiptInvoice>(
                azureReceiptInvoiceService.addOrUpdateAzureReceiptInvoice(azureReceiptInvoice), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/azure-receipt")
    public ResponseEntity<AzureReceipt> getAzureReceipt(@RequestParam Integer idAzureReceipt)
            throws OsirisValidationException, OsirisException {
        return new ResponseEntity<AzureReceipt>(
                azureReceiptService.getAzureReceiptFromUser(idAzureReceipt), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/azure-receipt/invoice/status")
    public ResponseEntity<AzureReceiptInvoiceStatus> getAzureReceiptInvoiceStatus(
            @RequestParam Integer idAzureReceiptInvoice)
            throws OsirisValidationException, OsirisException {
        AzureReceiptInvoice invoice = azureReceiptInvoiceService.getAzureReceiptInvoice(idAzureReceiptInvoice);

        if (invoice == null)
            throw new OsirisValidationException("idAzureReceiptInvoice");

        return new ResponseEntity<AzureReceiptInvoiceStatus>(
                azureReceiptInvoiceService.getAzureReceiptInvoiceStatus(invoice), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/azure-receipt/invoice/reconciliated")
    public ResponseEntity<AzureReceiptInvoice> markAsReconciliated(@RequestParam Integer idAzureReceiptInvoice,
            @RequestParam boolean isReconciliated) throws OsirisValidationException {
        AzureReceiptInvoice invoice = azureReceiptInvoiceService.getAzureReceiptInvoice(idAzureReceiptInvoice);

        if (invoice == null)
            throw new OsirisValidationException("idAzureReceiptInvoice");
        return new ResponseEntity<AzureReceiptInvoice>(
                azureReceiptInvoiceService.markAsReconciliated(invoice, isReconciliated),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/azure-invoices")
    public ResponseEntity<List<AzureInvoice>> getAzureInvoices(@RequestParam Boolean displayOnlyToCheck) {
        return new ResponseEntity<List<AzureInvoice>>(azureInvoiceService.getAzureInvoices(displayOnlyToCheck),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/azure-invoices/search")
    public ResponseEntity<List<AzureInvoice>> searchAzureInvoices(@RequestParam String invoiceId) {
        return new ResponseEntity<List<AzureInvoice>>(azureInvoiceService.searchAzureInvoicesByInvoiceId(invoiceId),
                HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    @GetMapping(inputEntryPoint + "/azure-invoice/create")
    public ResponseEntity<Invoice> createInvoiceFromAzureInvoice(@RequestParam Integer provisionId,
            @RequestParam Integer azureInvoiceId)
            throws OsirisValidationException, OsirisClientMessageException, OsirisException {
        Provision provision = provisionService.getProvision(provisionId);
        if (provision == null)
            throw new OsirisValidationException("provisionId");

        AzureInvoice azureInvoice = azureInvoiceService.getAzureInvoice(azureInvoiceId);
        if (azureInvoice == null)
            throw new OsirisValidationException("azureInvoice");

        return new ResponseEntity<Invoice>(
                azureInvoiceService.generateDeboursAndInvoiceFromInvoiceFromUser(azureInvoice, provision),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/azure-invoice")
    public ResponseEntity<AzureInvoice> getAzureInvoice(@RequestParam Integer idInvoice) {
        return new ResponseEntity<AzureInvoice>(azureInvoiceService.getAzureInvoice(idInvoice),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/azure-invoice")
    public ResponseEntity<AzureInvoice> updateAzureInvoice(@RequestBody AzureInvoice azureInvoice)
            throws OsirisException, OsirisValidationException {
        if (azureInvoice.getIsDisabled() == false) {
            validationHelper.validateReferential(azureInvoice.getCompetentAuthority(), true, "competentAuthority");
            validationHelper.validateDate(azureInvoice.getInvoiceDate(), true, "InvoiceDate");
            validationHelper.validateString(azureInvoice.getInvoiceId(), true, "InvoiceId");
            azureInvoice.setToCheck(false);
        }
        return new ResponseEntity<AzureInvoice>(azureInvoiceService.addOrUpdateAzureInvoice(azureInvoice),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/appoints")
    public ResponseEntity<List<Appoint>> getAppoints(@RequestParam String searchLabel) {
        return new ResponseEntity<List<Appoint>>(appointService.getAppoints(searchLabel), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/appoint/refund")
    public ResponseEntity<Boolean> refundAppint(@RequestParam String idAppoint)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (idAppoint == null)
            throw new OsirisValidationException("idAppoint");

        Appoint appoint = appointService.getAppoint(Integer.parseInt(idAppoint));

        if (appoint == null)
            throw new OsirisValidationException("appoint");

        return new ResponseEntity<Boolean>(refundService.generateRefundForAppoint(appoint.getId()),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/infogreffe-invoices")
    public ResponseEntity<List<InfogreffeInvoice>> getInfogreffeInvoices() {
        return new ResponseEntity<List<InfogreffeInvoice>>(infogreffeInvoiceService.getInfogreffeInvoices(),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/infogreffe-invoices/search")
    public ResponseEntity<List<InfogreffeInvoice>> getInfogreffeInvoicesByCustomerReference(
            @RequestParam String customerReference) {
        return new ResponseEntity<List<InfogreffeInvoice>>(
                infogreffeInvoiceService.getInfogreffeInvoicesByCustomerReference(customerReference),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/infogreffe-invoice/import")
    public ResponseEntity<Boolean> importInfogreffeInvoices(@RequestBody String csv)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        return new ResponseEntity<Boolean>(infogreffeInvoiceService.importInfogreffeInvoices(csv),
                HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    @GetMapping(inputEntryPoint + "/infogreffe-invoice/create")
    public ResponseEntity<Invoice> createInvoiceFromInfogreffeInvoice(
            @RequestParam Integer provisionId, @RequestParam Integer infogreffeInvoiceId)
            throws OsirisValidationException, OsirisClientMessageException, OsirisException {
        Provision provision = provisionService.getProvision(provisionId);
        if (provision == null)
            throw new OsirisValidationException("provisionId");

        InfogreffeInvoice greffeInvoice = infogreffeInvoiceService.getInfogreffeInvoice(infogreffeInvoiceId);
        if (greffeInvoice == null)
            throw new OsirisValidationException("infogreffeInvoiceId");

        return new ResponseEntity<Invoice>(
                infogreffeInvoiceService.generateInvoiceFromProvisionAndGreffeInvoice(greffeInvoice, provision),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment-ways")
    public ResponseEntity<List<PaymentWay>> getPaymentWays() {
        return new ResponseEntity<List<PaymentWay>>(paymentWayService.getPaymentWays(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/payment-way")
    public ResponseEntity<PaymentWay> addOrUpdatePaymentWay(
            @RequestBody PaymentWay paymentWays) throws OsirisValidationException, OsirisException {
        if (paymentWays.getId() != null)
            validationHelper.validateReferential(paymentWays, true, "paymentWays");
        validationHelper.validateString(paymentWays.getCode(), true, "code");
        validationHelper.validateString(paymentWays.getLabel(), true, "label");

        return new ResponseEntity<PaymentWay>(paymentWayService.addOrUpdatePaymentWay(paymentWays), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment")
    public ResponseEntity<Payment> getPaymentById(@RequestParam Integer id) throws OsirisValidationException {
        if (id == null)
            throw new OsirisValidationException("Id");

        return new ResponseEntity<Payment>(paymentService.getPayment(id), HttpStatus.OK);
    }

    // TODO remove !
    @GetMapping(inputEntryPoint + "/payment/add")
    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    public ResponseEntity<Payment> addPayment(@RequestParam Float amount, @RequestParam Integer paymentWayId,
            @RequestParam String label)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        Payment payment = new Payment();
        payment.setIsExternallyAssociated(false);
        payment.setIsCancelled(false);
        payment.setLabel(label);
        payment.setPaymentAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentType(constantService.getPaymentTypeVirement());
        payment.setPaymentWay(paymentWayService.getPaymentWay(paymentWayId));
        paymentService.addOrUpdatePayment(payment);
        paymentService.payementGrab();
        return new ResponseEntity<Payment>(payment, HttpStatus.OK);
    }

    // TODO remove !
    @GetMapping(inputEntryPoint + "/payment/automatch")
    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    public ResponseEntity<Payment> automatchPayment()
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        paymentService.payementGrab();
        return new ResponseEntity<Payment>(new Payment(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payments/search")
    public ResponseEntity<List<PaymentSearchResult>> getPayments(@RequestBody PaymentSearch paymentSearch)
            throws OsirisValidationException {
        if (paymentSearch == null)
            throw new OsirisValidationException("paymentSearch");

        if (paymentSearch.getPaymentWays() == null)
            throw new OsirisValidationException("paymentWays");

        return new ResponseEntity<List<PaymentSearchResult>>(paymentService.searchPayments(paymentSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/debours/search")
    public ResponseEntity<List<DebourSearchResult>> getDebours(@RequestBody DebourSearch debourSearch)
            throws OsirisValidationException, OsirisException {
        if (debourSearch == null)
            throw new OsirisValidationException("debourSearch");

        validationHelper.validateReferential(debourSearch.getCompetentAuthority(), false, "competentAuthority");

        return new ResponseEntity<List<DebourSearchResult>>(debourService.searchDebours(debourSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/refunds/search")
    public ResponseEntity<List<RefundSearchResult>> getRefunds(@RequestBody RefundSearch refundSearch)
            throws OsirisValidationException {
        if (refundSearch == null)
            throw new OsirisValidationException("refundSearch");

        return new ResponseEntity<List<RefundSearchResult>>(refundService.searchRefunds(refundSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/refunds/export")
    public ResponseEntity<byte[]> downloadRefunds(@RequestBody RefundSearch refundSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (refundSearch == null)
            throw new OsirisValidationException("refundSearch");

        if (refundSearch.getStartDate() == null || refundSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        File refunds = refundService.getRefundExport(refundSearch);

        if (refunds != null) {
            try {
                data = Files.readAllBytes(refunds.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + refunds.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Remboursements - "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmm")) + ".xml");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/xml");

            refunds.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/transfert/search")
    public ResponseEntity<List<BankTransfertSearchResult>> getTransferts(
            @RequestBody BankTransfertSearch transfertSearch)
            throws OsirisValidationException {
        if (transfertSearch == null)
            throw new OsirisValidationException("transfertSearch");

        return new ResponseEntity<List<BankTransfertSearchResult>>(
                bankTransfertService.searchBankTransfert(transfertSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/transfert/export")
    public ResponseEntity<byte[]> downloadTransferts(@RequestBody BankTransfertSearch transfertSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (transfertSearch == null)
            throw new OsirisValidationException("refundSearch");

        if (transfertSearch.getStartDate() == null || transfertSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        File refunds = bankTransfertService.getBankTransfertExport(transfertSearch);

        if (refunds != null) {
            try {
                data = Files.readAllBytes(refunds.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + refunds.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Virements - "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmm")) + ".xml");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/xml");

            refunds.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/direct/transfert/search")
    public ResponseEntity<List<DirectDebitTransfertSearchResult>> getTransferts(
            @RequestBody DirectDebitTransfertSearch transfertSearch)
            throws OsirisValidationException {
        if (transfertSearch == null)
            throw new OsirisValidationException("transfertSearch");

        return new ResponseEntity<List<DirectDebitTransfertSearchResult>>(
                directDebitTransfertService.searchDirectDebitTransfert(transfertSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/direct/transfert/export")
    public ResponseEntity<byte[]> downloadTransferts(@RequestBody DirectDebitTransfertSearch transfertSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (transfertSearch == null)
            throw new OsirisValidationException("refundSearch");

        if (transfertSearch.getStartDate() == null || transfertSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        File refunds = directDebitTransfertService.getDirectDebitTransfertExport(transfertSearch);

        if (refunds != null) {
            try {
                data = Files.readAllBytes(refunds.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + refunds.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Prélèvements - "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmm")) + ".xml");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/xml");

            refunds.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payments/associate")
    public ResponseEntity<Boolean> associatePaymentAndInvoiceAndCustomerOrder(
            @RequestBody PaymentAssociate paymentAssociate)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {

        if (paymentAssociate == null)
            throw new OsirisValidationException("paymentAssociate");

        paymentAssociate
                .setPayment(
                        (Payment) validationHelper.validateReferential(paymentAssociate.getPayment(), true, "Payment"));

        if (paymentAssociate.getPayment().getIsCancelled())
            throw new OsirisValidationException("Payment already cancelled !");

        if (paymentAssociate.getInvoices() != null) {
            if (paymentAssociate.getInvoices().size() == 0)
                paymentAssociate.setInvoices(null);

            for (Invoice invoice : paymentAssociate.getInvoices()) {
                invoice = (Invoice) validationHelper.validateReferential(invoice, true, "invoice");
                if (paymentAssociate.getPayment().getIsCancelled() != null
                        && paymentAssociate.getPayment().getIsCancelled() == true)
                    throw new OsirisValidationException("payment cancelled");
                if (paymentAssociate.getPayment().getInvoice() != null
                        && invoice.getId().equals(paymentAssociate.getPayment().getInvoice().getId()))
                    throw new OsirisValidationException("payment already associate");

                if (!invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                        && !invoice.getInvoiceStatus().getId()
                                .equals(constantService.getInvoiceStatusReceived().getId())
                        && !invoice.getInvoiceStatus().getId()
                                .equals(constantService.getInvoiceStatusCreditNoteReceived().getId()))
                    throw new OsirisValidationException("invoice not send or received");

                if (invoice.getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())) {
                    if (Math.round(invoice.getTotalPrice() * 100f) != Math
                            .round(paymentAssociate.getPayment().getPaymentAmount() * 100f))
                        throw new OsirisValidationException("Wrong payment amount");
                }
            }
        }

        if (paymentAssociate.getCustomerOrders() != null) {
            if (paymentAssociate.getCustomerOrders().size() == 0)
                paymentAssociate.setCustomerOrders(null);

            for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                customerOrder = (CustomerOrder) validationHelper.validateReferential(customerOrder, true,
                        "customerOrder");
            }
        }

        paymentAssociate
                .setAffaire((Affaire) validationHelper.validateReferential(paymentAssociate.getAffaire(), false,
                        "Affaire"));

        if (paymentAssociate.getByPassAmount() == null || paymentAssociate.getByPassAmount()
                .size() != (paymentAssociate.getInvoices() == null ? 0 : paymentAssociate.getInvoices().size())
                        + (paymentAssociate.getCustomerOrders() == null ? 0
                                : paymentAssociate.getCustomerOrders().size()))
            throw new OsirisValidationException("wrong associate number");

        Float totalAmount = 0f;
        for (Float amount : paymentAssociate.getByPassAmount()) {
            totalAmount += amount;
        }
        totalAmount = Math.round(totalAmount * 100f) / 100f;

        if (paymentAssociate.getPayment().getPaymentWay().getId()
                .equals(constantService.getPaymentWayInbound().getId())) {
            if (paymentAssociate.getTiersRefund() == null && paymentAssociate.getConfrereRefund() == null
                    && paymentAssociate.getPayment().getPaymentAmount() > totalAmount
                    && Math.abs(paymentAssociate.getPayment().getPaymentAmount()
                            - totalAmount) > payementLimitRefundInEuros)
                throw new OsirisValidationException("TiersRefund or ConfrereRefund");
            validationHelper.validateReferential(paymentAssociate.getTiersRefund(), false, "TiersRefund");
            validationHelper.validateReferential(paymentAssociate.getConfrereRefund(), false, "ConfrereRefund");
        }

        if (paymentAssociate.getPayment().getPaymentAmount() < totalAmount)
            throw new OsirisValidationException("not all payment used");

        ITiers commonCustomerOrder = null;
        if (paymentAssociate.getPayment().getPaymentWay().getId()
                .equals(constantService.getPaymentWayInbound().getId())) {
            commonCustomerOrder = null;

            if (paymentAssociate.getInvoices() != null) {
                if (paymentAssociate.getInvoices().get(0).getResponsable() != null)
                    commonCustomerOrder = paymentAssociate.getInvoices().get(0).getResponsable().getTiers();
                if (paymentAssociate.getInvoices().get(0).getConfrere() != null)
                    commonCustomerOrder = paymentAssociate.getInvoices().get(0).getConfrere();
                if (paymentAssociate.getInvoices().get(0).getTiers() != null)
                    commonCustomerOrder = paymentAssociate.getInvoices().get(0).getTiers();

                if (commonCustomerOrder != null) {
                    for (Invoice invoice : paymentAssociate.getInvoices()) {
                        if (invoice.getResponsable() != null
                                && !invoice.getResponsable().getTiers().getId().equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                        if (invoice.getConfrere() != null
                                && !invoice.getConfrere().getId().equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                        if (invoice.getTiers() != null
                                && !invoice.getTiers().getId().equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                    }
                }
            }

            if (paymentAssociate.getCustomerOrders() != null) {
                if (commonCustomerOrder == null) {
                    if (paymentAssociate.getCustomerOrders().get(0).getResponsable() != null)
                        commonCustomerOrder = paymentAssociate.getCustomerOrders().get(0).getResponsable().getTiers();
                    if (paymentAssociate.getCustomerOrders().get(0).getConfrere() != null)
                        commonCustomerOrder = paymentAssociate.getCustomerOrders().get(0).getConfrere();
                    if (paymentAssociate.getCustomerOrders().get(0).getTiers() != null)
                        commonCustomerOrder = paymentAssociate.getCustomerOrders().get(0).getTiers();
                }

                if (commonCustomerOrder != null) {
                    for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                        if (customerOrder.getResponsable() != null
                                && !customerOrder.getResponsable().getTiers().getId()
                                        .equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                        if (customerOrder.getConfrere() != null
                                && !customerOrder.getConfrere().getId().equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                        if (customerOrder.getTiers() != null
                                && !customerOrder.getTiers().getId().equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                    }
                }
            }
        } else {
            // All invoice same provider / AC
            if (paymentAssociate.getInvoices() != null && paymentAssociate.getInvoices().size() > 1) {
                Integer providerId = paymentAssociate.getInvoices().get(0).getProvider() != null
                        ? paymentAssociate.getInvoices().get(0).getProvider().getId()
                        : paymentAssociate.getInvoices().get(0).getCompetentAuthority().getId();
                for (Invoice invoice : paymentAssociate.getInvoices()) {
                    if (invoice.getProvider() != null
                            && !providerId.equals(invoice.getProvider().getId()))
                        throw new OsirisValidationException("not same customer order chosed");
                    if (invoice.getCompetentAuthority() != null
                            && !providerId.equals(invoice.getCompetentAuthority().getId()))
                        throw new OsirisValidationException("not same customer order chosed");
                }
            }
        }

        paymentService.manualMatchPaymentInvoicesAndCustomerOrders(
                paymentAssociate.getPayment(),
                paymentAssociate.getInvoices(), paymentAssociate.getCustomerOrders(),
                paymentAssociate.getAffaire(),
                commonCustomerOrder, paymentAssociate.getByPassAmount());

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE)
    @PostMapping(inputEntryPoint + "/payment/cash/add/invoice")
    public ResponseEntity<Boolean> addCashPaymentForInvoice(@RequestBody Payment cashPayment,
            @RequestParam Integer idInvoice)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        Invoice invoice = invoiceService.getInvoice(idInvoice);

        if (invoice == null)
            throw new OsirisValidationException("invoice");

        if (cashPayment == null)
            throw new OsirisValidationException("payment");

        cashPayment.setPaymentType(constantService.getPaymentTypeEspeces());
        cashPayment.setPaymentWay(constantService.getPaymentWayInbound());
        validationHelper.validateString(cashPayment.getLabel(), true, 250, "paymentType");
        validationHelper.validateDateTimeMax(cashPayment.getPaymentDate(), true, LocalDateTime.now(), "paymentType");

        Float remainingToPay = invoiceService.getRemainingAmountToPayForInvoice(invoice);
        if (remainingToPay == null || remainingToPay < cashPayment.getPaymentAmount())
            throw new OsirisValidationException("paymentAmount");

        this.paymentService.addCashPaymentForInvoice(cashPayment, invoice);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE)
    @PostMapping(inputEntryPoint + "/payment/cash/add/customer-order")
    public ResponseEntity<Boolean> addCashPaymentForCustomerOrder(@RequestBody Payment cashPayment,
            @RequestParam Integer idCustomerOrder)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);

        if (customerOrder == null || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
            throw new OsirisValidationException("customerOrder");

        if (cashPayment == null)
            throw new OsirisValidationException("payment");

        cashPayment.setPaymentType(constantService.getPaymentTypeEspeces());
        cashPayment.setPaymentWay(constantService.getPaymentWayInbound());
        validationHelper.validateString(cashPayment.getLabel(), true, 250, "paymentLabel");
        validationHelper.validateDateTimeMax(cashPayment.getPaymentDate(), true, LocalDateTime.now(), "paymentDate");

        this.paymentService.addCashPaymentForCustomerOrder(cashPayment, customerOrder);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    @PostMapping(inputEntryPoint + "/payment/check/add")
    public ResponseEntity<Boolean> addCheckPayment(@RequestBody Payment checkPayment)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (checkPayment == null)
            throw new OsirisValidationException("payment");

        checkPayment.setPaymentType(constantService.getPaymentTypeCheques());
        checkPayment.setPaymentWay(constantService.getPaymentWayInbound());
        validationHelper.validateString(checkPayment.getLabel(), true, 250, "paymentType");
        validationHelper.validateDateTimeMax(checkPayment.getPaymentDate(), true, LocalDateTime.now(), "paymentType");

        this.paymentService.addCheckPayment(checkPayment);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/deposits/associate")
    public ResponseEntity<Boolean> associateDepositsAndInvoiceAndCustomerOrder(
            @RequestBody PaymentAssociate paymentAssociate)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {

        if (paymentAssociate == null)
            throw new OsirisValidationException("paymentAssociate");

        paymentAssociate.setPayment(null);

        paymentAssociate
                .setDeposit(
                        (Deposit) validationHelper.validateReferential(paymentAssociate.getDeposit(), true, "Deposit"));

        if (paymentAssociate.getDeposit().getIsCancelled())
            throw new OsirisValidationException("Deposit already cancelled !");

        if (paymentAssociate.getInvoices() != null) {
            if (paymentAssociate.getInvoices().size() == 0)
                paymentAssociate.setInvoices(null);

            for (Invoice invoice : paymentAssociate.getInvoices()) {
                invoice = (Invoice) validationHelper.validateReferential(invoice, true, "invoice");
                if (paymentAssociate.getDeposit().getInvoice() != null
                        && invoice.getId().equals(paymentAssociate.getDeposit().getInvoice().getId()))
                    throw new OsirisValidationException("payment already associate");

                if (!invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()))
                    throw new OsirisValidationException("invoice not send");
            }
        }

        if (paymentAssociate.getCustomerOrders() != null) {
            if (paymentAssociate.getCustomerOrders().size() == 0)
                paymentAssociate.setCustomerOrders(null);

            for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                customerOrder = (CustomerOrder) validationHelper.validateReferential(customerOrder, true,
                        "customerOrder");
            }
        }

        paymentAssociate
                .setAffaire((Affaire) validationHelper.validateReferential(paymentAssociate.getAffaire(), false,
                        "Affaire"));

        if (paymentAssociate.getByPassAmount() == null || paymentAssociate.getByPassAmount()
                .size() != (paymentAssociate.getInvoices() == null ? 0 : paymentAssociate.getInvoices().size())
                        + (paymentAssociate.getCustomerOrders() == null ? 0
                                : paymentAssociate.getCustomerOrders().size()))
            throw new OsirisValidationException("wrong associate number");

        Float totalAmount = 0f;
        for (Float amount : paymentAssociate.getByPassAmount()) {
            totalAmount += amount;
        }
        totalAmount = Math.round(totalAmount * 100f) / 100f;

        validationHelper.validateReferential(paymentAssociate.getTiersRefund(), false, "TiersRefund");
        validationHelper.validateReferential(paymentAssociate.getConfrereRefund(), false, "ConfrereRefund");

        if (paymentAssociate.getDeposit().getDepositAmount() < totalAmount)
            throw new OsirisValidationException("not all payment used");

        if (paymentAssociate.getDeposit().getDepositAmount() > totalAmount
                && paymentAssociate.getTiersRefund() == null && paymentAssociate.getAffaire() == null
                && Math.abs(paymentAssociate.getDeposit().getDepositAmount()) > payementLimitRefundInEuros)
            throw new OsirisValidationException("no refund tiers set");

        ITiers commonCustomerOrder;
        commonCustomerOrder = paymentAssociate.getTiersRefund() != null ? paymentAssociate.getTiersRefund()
                : paymentAssociate.getConfrereRefund();

        if (paymentAssociate.getInvoices() != null && Math
                .round(paymentAssociate.getDeposit().getDepositAmount() * 100f) == Math.round(totalAmount * 100f))
            commonCustomerOrder = invoiceHelper.getCustomerOrder(paymentAssociate.getInvoices().get(0));

        if (paymentAssociate.getInvoices() != null) {
            for (Invoice invoice : paymentAssociate.getInvoices())
                if (!invoiceHelper.getCustomerOrder(invoice).getId().equals(commonCustomerOrder.getId()))
                    throw new OsirisValidationException("not same customer order chosed");
        }

        if (paymentAssociate.getCustomerOrders() != null) {
            for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                if (customerOrder.getResponsable() != null
                        && !customerOrder.getResponsable().getTiers().getId().equals(commonCustomerOrder.getId()))
                    throw new OsirisValidationException("not same customer order chosed");
                if (customerOrder.getConfrere() != null
                        && !customerOrder.getConfrere().getId().equals(commonCustomerOrder.getId()))
                    throw new OsirisValidationException("not same customer order chosed");
                if (customerOrder.getTiers() != null
                        && !customerOrder.getTiers().getId().equals(commonCustomerOrder.getId()))
                    throw new OsirisValidationException("not same customer order chosed");
            }
        }

        depositService.manualMatchDepositInvoicesAndCustomerOrders(
                paymentAssociate.getDeposit(),
                paymentAssociate.getInvoices(), paymentAssociate.getCustomerOrders(),
                paymentAssociate.getAffaire(),
                commonCustomerOrder, paymentAssociate.getByPassAmount());

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payments/associate/externally")
    public ResponseEntity<Boolean> setExternallyAssociated(@RequestBody Payment payment)
            throws OsirisValidationException, OsirisException {
        Payment paymentOut = (Payment) validationHelper.validateReferential(payment, true, "payment");

        if (paymentOut.getInvoice() != null)
            throw new OsirisValidationException("Invoice");

        paymentService.setExternallyAssociated(paymentOut);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payments/unassociate/externally")
    public ResponseEntity<Boolean> unsetExternallyAssociated(@RequestBody Payment payment)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        Payment paymentOut = (Payment) validationHelper.validateReferential(payment, true, "payment");

        if (paymentOut.getInvoice() != null)
            throw new OsirisValidationException("Invoice");

        paymentService.unsetExternallyAssociated(paymentOut);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payments/advise")
    public ResponseEntity<List<Payment>> getPaymentAdvised(@RequestParam Integer invoiceId)
            throws OsirisValidationException {
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        if (invoice == null)
            throw new OsirisValidationException("Invoice");

        return new ResponseEntity<List<Payment>>(paymentService.getAdvisedPaymentForInvoice(invoice), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payments/advise/order")
    public ResponseEntity<List<Payment>> getPaymentAdvisedForCustomerOrder(@RequestParam Integer customerOrderId)
            throws OsirisValidationException {
        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
        if (customerOrder == null)
            throw new OsirisValidationException("customerOrder");

        return new ResponseEntity<List<Payment>>(paymentService.getAdvisedPaymentForCustomerOrder(customerOrder),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice")
    public ResponseEntity<Invoice> getInvoiceById(@RequestParam Integer id) throws OsirisValidationException {
        if (id == null)
            throw new OsirisValidationException("Id");

        return new ResponseEntity<Invoice>(invoiceService.getInvoice(id), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Invoice> addOrUpdateInvoice(@RequestBody Invoice invoice)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (invoice.getId() != null && invoice.getCustomerOrder() != null)
            throw new OsirisValidationException("Id");
        validateInvoice(invoice);
        return new ResponseEntity<Invoice>(invoiceService.addOrUpdateInvoiceFromUser(invoice), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice/cancel")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE)
    public ResponseEntity<Invoice> cancelInvoice(@RequestParam Integer idInvoice)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (idInvoice == null)
            throw new OsirisValidationException("Id");

        Invoice invoice = invoiceService.getInvoice(idInvoice);
        if (invoice == null)
            throw new OsirisValidationException("Invoice");

        return new ResponseEntity<Invoice>(invoiceService.cancelInvoice(invoice),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice/credit-note")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Invoice> generateInvoiceCreditNote(@RequestBody Invoice newInvoice,
            @RequestParam Integer idOriginInvoiceForCreditNote)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (newInvoice.getId() != null && newInvoice.getCustomerOrder() != null)
            throw new OsirisValidationException("Id");

        if (idOriginInvoiceForCreditNote == null)
            throw new OsirisValidationException("idOriginInvoiceForCreditNote");

        Invoice originInvoice = invoiceService.getInvoice(idOriginInvoiceForCreditNote);

        if (originInvoice == null)
            throw new OsirisValidationException("originInvoice");

        if (newInvoice.getIsProviderCreditNote() == null || newInvoice.getIsProviderCreditNote() == false)
            throw new OsirisValidationException("isProviderCreditNote");
        validateInvoice(newInvoice);
        return new ResponseEntity<Invoice>(
                invoiceService.generateInvoiceCreditNote(newInvoice, idOriginInvoiceForCreditNote),
                HttpStatus.OK);
    }

    private void validateInvoice(Invoice invoice)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (invoice.getIsInvoiceFromProvider() == null)
            invoice.setIsInvoiceFromProvider(false);

        if (invoice.getIsProviderCreditNote() == null)
            invoice.setIsProviderCreditNote(false);

        int doFound = 0;

        if (invoice.getTiers() != null) {
            validationHelper.validateReferential(invoice.getTiers(), true, "Tiers");
            doFound++;
        }

        if (invoice.getResponsable() != null) {
            validationHelper.validateReferential(invoice.getResponsable(), true, "Responsable");
            doFound++;
        }

        if (invoice.getConfrere() != null) {
            validationHelper.validateReferential(invoice.getConfrere(), true, "Confrere");
            doFound++;
        }

        if (invoice.getProvider() != null) {
            validationHelper.validateReferential(invoice.getProvider(), true, "Provider");
            doFound++;
        }

        if (invoice.getCompetentAuthority() != null) {
            validationHelper.validateReferential(invoice.getCompetentAuthority(), true, "CompetentAuthority");
            doFound++;
        }

        if (doFound != 1)
            throw new OsirisValidationException("Too many customer order");

        if (!invoice.getIsInvoiceFromProvider() && !invoice.getIsProviderCreditNote() && invoice.getProvider() != null)
            throw new OsirisValidationException("Provider not allowed");

        BillingLabelType billingLabelAffaire = constantService.getBillingLabelTypeCodeAffaire();

        if (invoice.getIsInvoiceFromProvider() == false && invoice.getIsProviderCreditNote() == false) {
            validationHelper.validateReferential(invoice.getBillingLabelType(), true, "BillingLabelType");
            validationHelper.validateString(invoice.getBillingLabelAddress(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 160,
                    "BillingLabelAddress");
            validationHelper.validateString(invoice.getBillingLabel(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 100, "BillingLabel");
            validationHelper.validateString(invoice.getBillingLabelPostalCode(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 10,
                    "BillingLabelPostalCode");
            validationHelper.validateString(invoice.getCedexComplement(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), 20,
                    "BillingLabelPostalCode");
            validationHelper.validateReferential(invoice.getBillingLabelCity(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), "BillingLabelCity");
            validationHelper.validateReferential(invoice.getBillingLabelCountry(),
                    invoice.getBillingLabelType().getId().equals(billingLabelAffaire.getId()), "BillingLabelCountry");
            validationHelper.validateString(invoice.getBillingLabelPostalCode(), false, 40, "BillingLabelPostalCode");
        }
        validationHelper.validateReferential(invoice.getInvoiceStatus(), false, "InvoiceStatus");
        validationHelper.validateDate(invoice.getDueDate(), false, "DueDate");
        validationHelper.validateDate(invoice.getManualAccountingDocumentDate(), false, "AccountingDocumentDate");
        validationHelper.validateString(invoice.getManualAccountingDocumentNumber(), false, 150,
                "ManualAccountingDocumentNumber");
        validationHelper.validateString(invoice.getBillingLabelIntercommunityVat(), false, 20, "intercommunityVat");

        if (invoice.getInvoiceItems() == null) {
            throw new OsirisValidationException("InvoiceItems");
        } else {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getId() != null)
                    validationHelper.validateReferential(invoiceItem, true, "invoiceItem");
                if (invoiceItem.getDiscountAmount() == null)
                    invoiceItem.setDiscountAmount(0f);
                if (invoiceItem.getPreTaxPrice() == null)
                    invoiceItem.setPreTaxPrice(0f);
                if (invoiceItem.getVatPrice() == null)
                    invoiceItem.setVatPrice(0f);

            }
        }

        if (invoice.getCustomerOrderForInboundInvoice() != null) {
            if (!invoice.getIsInvoiceFromProvider() && !invoice.getIsProviderCreditNote())
                throw new OsirisValidationException("customerOrderForInboundInvoice");
            validationHelper.validateReferential(invoice.getCustomerOrderForInboundInvoice(), false,
                    "customerOrderForInboundInvoice");
        }

        // Handle automatic bank transfert
        validationHelper.validateReferential(invoice.getManualPaymentType(), invoice.getIsInvoiceFromProvider(),
                "PaymentType");
        if (invoice.getIsInvoiceFromProvider()
                && invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())
                && (invoice.getCompetentAuthority() == null || invoice.getCustomerOrderForInboundInvoice() == null)) {
            validationHelper.validateIban(invoiceHelper.getIbanOfOrderingCustomer(invoice), true, "IBAN");
            validationHelper.validateBic(invoiceHelper.getBicOfOrderingCustomer(invoice), true, "BIC");
        }
    }

    @GetMapping(inputEntryPoint + "/invoice-status-list")
    public ResponseEntity<List<InvoiceStatus>> getInvoiceStatus() {
        return new ResponseEntity<List<InvoiceStatus>>(invoiceStatusService.getInvoiceStatus(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/invoice-status")
    public ResponseEntity<InvoiceStatus> addOrUpdateInvoiceStatus(
            @RequestBody InvoiceStatus invoiceStatus) throws OsirisValidationException, OsirisException {
        if (invoiceStatus.getId() != null)
            validationHelper.validateReferential(invoiceStatus, true, "invoiceStatus");
        validationHelper.validateString(invoiceStatus.getCode(), true, "code");
        validationHelper.validateString(invoiceStatus.getLabel(), true, "label");

        return new ResponseEntity<InvoiceStatus>(invoiceStatusService.addOrUpdateInvoiceStatus(invoiceStatus),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice/customer-order")
    public ResponseEntity<List<InvoiceSearchResult>> getInvoiceForCustomerOrder(@RequestParam Integer customerOrderId)
            throws OsirisException {
        return new ResponseEntity<List<InvoiceSearchResult>>(invoiceService.getInvoiceForCustomerOrder(customerOrderId),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice/customer-order/provider")
    public ResponseEntity<List<InvoiceSearchResult>> getProviderInvoiceForCustomerOrder(
            @RequestParam Integer customerOrderId) throws OsirisException {
        return new ResponseEntity<List<InvoiceSearchResult>>(
                invoiceService.getProviderInvoiceForCustomerOrder(customerOrderId),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice/search")
    public ResponseEntity<List<InvoiceSearchResult>> searchInvoices(@RequestBody InvoiceSearch invoiceSearch)
            throws OsirisValidationException, OsirisException {
        if (invoiceSearch == null)
            throw new OsirisValidationException("invoiceSearch");

        return new ResponseEntity<List<InvoiceSearchResult>>(invoiceService.searchInvoices(invoiceSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/invoice/label/compute")
    public ResponseEntity<InvoiceLabelResult> computeInvoiceLabelForCustomerOrder(
            @RequestBody CustomerOrder customerOrder) throws OsirisException {
        return new ResponseEntity<InvoiceLabelResult>(invoiceHelper.computeInvoiceLabelResult(
                documentService.getBillingDocument(customerOrder.getDocuments()), customerOrder,
                quotationService.getCustomerOrderOfQuotation(customerOrder)), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/paper/label/compute")
    public ResponseEntity<InvoiceLabelResult> computePaperLabelForCustomerOrder(
            @RequestBody CustomerOrder customerOrder) throws OsirisException, OsirisClientMessageException {
        return new ResponseEntity<InvoiceLabelResult>(mailComputeHelper.computePaperLabelResult(customerOrder),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/greffe/invoice/search/order")
    public ResponseEntity<List<OwncloudGreffeInvoice>> getCorrespondingGreffeInvoiceForCustomerOrder(
            @RequestParam Integer customerOrderId)
            throws OsirisValidationException {
        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
        if (customerOrder == null)
            throw new OsirisValidationException("customerOrderId");

        return new ResponseEntity<List<OwncloudGreffeInvoice>>(
                owncloudGreffeInvoiceService.getCorrespondingGreffeInvoiceForCustomerOrder(customerOrder),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/greffe/invoice/search/numero")
    public ResponseEntity<List<OwncloudGreffeInvoice>> getOwncloudGreffeInvoiceByNumero(
            @RequestParam String numero)
            throws OsirisValidationException {
        return new ResponseEntity<List<OwncloudGreffeInvoice>>(
                owncloudGreffeInvoiceService.getOwncloudGreffeInvoiceByNumero(numero),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/greffe/invoice/create")
    public ResponseEntity<Invoice> createInvoiceFromGreffeInvoice(
            @RequestParam Integer provisionId, @RequestParam Integer owncloudGreffeInvoiceId)
            throws OsirisValidationException, OsirisClientMessageException, OsirisException {
        Provision provision = provisionService.getProvision(provisionId);
        if (provision == null)
            throw new OsirisValidationException("provisionId");

        OwncloudGreffeInvoice greffeInvoice = owncloudGreffeInvoiceService
                .getOwncloudGreffeInvoice(owncloudGreffeInvoiceId);
        if (greffeInvoice == null)
            throw new OsirisValidationException("owncloudGreffeInvoiceId");

        return new ResponseEntity<Invoice>(
                owncloudGreffeDelegateImpl.generateInvoiceFromProvisionAndGreffeInvoice(greffeInvoice, provision),
                HttpStatus.OK);
    }
}
