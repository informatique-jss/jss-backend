package com.jss.osiris.modules.invoicing.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
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
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoiceStatus;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearch;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearch;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentAssociate;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.model.RefundSearch;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;
import com.jss.osiris.modules.invoicing.service.AzureInvoiceService;
import com.jss.osiris.modules.invoicing.service.AzureReceiptInvoiceService;
import com.jss.osiris.modules.invoicing.service.AzureReceiptService;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.InvoiceStatusService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.invoicing.service.RefundService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Rff;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.BillingLabelTypeService;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.RffService;
import com.jss.osiris.modules.tiers.service.TiersService;

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
    AffaireService affaireService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ConfrereService confrereService;

    @Value("${invoicing.payment.limit.refund.euros}")
    private Integer paymentLimitRefundInEuros;

    @Autowired
    AzureInvoiceService azureInvoiceService;

    @Autowired
    AzureReceiptInvoiceService azureReceiptInvoiceService;

    @Autowired
    AzureReceiptService azureReceiptService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    BillingLabelTypeService billingLabelTypeService;

    @Autowired
    RffService rffService;

    @Autowired
    AccountingRecordGenerationService accountingRecordGenerationService;

    @GetMapping(inputEntryPoint + "/rff/create")
    public ResponseEntity<Invoice> generateInvoiceForRff(@RequestParam Integer idRff)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (idRff == null)
            throw new OsirisValidationException("idRff");

        Rff rff = rffService.getRff(idRff);
        if (rff == null)
            throw new OsirisValidationException("Rff");

        if (rff.getRffTotal() == null || rff.getRffTotal() <= 0f)
            throw new OsirisValidationException("Rff");

        if (rff.getIsCancelled() == true || rff.getIsSent() == false
                || rff.getInvoices() != null && rff.getInvoices().size() > 0)
            throw new OsirisValidationException("Rff");

        return new ResponseEntity<Invoice>(rffService.generateInvoiceForRff(rff), HttpStatus.OK);
    }

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
                azureInvoiceService.generateInvoiceFromAzureInvoice(azureInvoice, provision),
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

    @GetMapping(inputEntryPoint + "/payment")
    public ResponseEntity<Payment> getPaymentById(@RequestParam Integer id) throws OsirisValidationException {
        if (id == null)
            throw new OsirisValidationException("Id");

        return new ResponseEntity<Payment>(paymentService.getPayment(id), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment/waiting")
    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    public ResponseEntity<Payment> movePaymentToWaitingAccount(@RequestParam Integer paymentId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment != null)
            payment = paymentService.movePaymentToWaitingAccount(payment);
        return new ResponseEntity<Payment>(payment, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment/cut")
    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    public ResponseEntity<Payment> cutPayment(@RequestParam Integer paymentId, @RequestParam Float amount)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        validationHelper.validateFloat(amount, true, "amount");
        Payment payment = paymentService.getPayment(paymentId);
        if (payment != null) {
            if (payment.getIsCancelled() || payment.getIsExternallyAssociated())
                throw new OsirisValidationException("cancelled");
            payment = paymentService.cutPayment(payment, amount);
        }
        return new ResponseEntity<Payment>(payment, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payments/search")
    public ResponseEntity<List<PaymentSearchResult>> getPayments(@RequestBody PaymentSearch paymentSearch)
            throws OsirisValidationException {
        if (paymentSearch == null)
            throw new OsirisValidationException("paymentSearch");

        return new ResponseEntity<List<PaymentSearchResult>>(paymentService.searchPayments(paymentSearch),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payment/comment")
    public ResponseEntity<Payment> addOrUpdatePaymentComment(@RequestBody String comment,
            @RequestParam Integer idPayment)
            throws OsirisValidationException, OsirisException {

        Payment payment = paymentService.getPayment(idPayment);

        if (payment == null)
            throw new OsirisValidationException("idPayment");

        payment.setComment(comment);
        return new ResponseEntity<>(paymentService.addOrUpdatePayment(payment), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/refunds/search")
    public ResponseEntity<List<RefundSearchResult>> getRefunds(@RequestBody RefundSearch refundSearch)
            throws OsirisValidationException {
        if (refundSearch == null)
            throw new OsirisValidationException("refundSearch");

        return new ResponseEntity<List<RefundSearchResult>>(refundService.searchRefunds(refundSearch),
                HttpStatus.OK);
    }

    @GetMapping("/refund/label-update")
    public ResponseEntity<Refund> modifyRefundLabel(@RequestParam Integer refundId, @RequestParam String refundLabel)
            throws OsirisException {
        if (refundId == null)
            throw new OsirisValidationException("refundId");
        if (refundLabel != null)
            validationHelper.validateString(refundLabel, null, 250, "refundLabel");

        Refund refund = refundService.getRefund(refundId);
        if (refund == null)
            throw new OsirisValidationException("refund");
        if (refundLabel != null && refundLabel.trim().length() > 0)
            refund.setLabel(refundLabel);

        return new ResponseEntity<Refund>(refundService.addOrUpdateRefund(refund), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/refund/payment")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Boolean> refundPayment(@RequestParam Integer paymentId,
            @RequestParam Integer tiersId, @RequestParam Integer affaireId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        Payment payment = paymentService.getPayment(paymentId);
        Tiers tiers = tiersService.getTiers(tiersId);
        Affaire affaire = affaireService.getAffaire(affaireId);

        if (payment == null)
            throw new OsirisValidationException("payment");

        if (tiers == null)
            throw new OsirisValidationException("tiers");

        paymentService.refundPayment(payment, tiers, affaire);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment/account")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Boolean> putPaymentInAccount(@RequestParam Integer paymentId,
            @RequestParam Integer accountingAccountId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        Payment payment = paymentService.getPayment(paymentId);

        if (payment == null)
            throw new OsirisValidationException("payment");

        if (payment.getIsCancelled() == true || payment.getBankTransfert() != null || payment.getCustomerOrder() != null
                || payment.getInvoice() != null
                || payment.getRefund() != null || payment.getCompetentAuthority() != null
                || payment.getAccountingAccount() != null
                || payment.getIsExternallyAssociated() != null && payment.getIsExternallyAssociated())
            throw new OsirisValidationException("Paiement déjà associé");

        AccountingAccount account = accountingAccountService.getAccountingAccount(accountingAccountId);
        if (account == null)
            throw new OsirisValidationException("account");

        if ((account.getIsAllowedToPutIntoAccount() == null || !account.getIsAllowedToPutIntoAccount())
                && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ADMINISTRATEUR_GROUP))
            throw new OsirisValidationException("Action non autorisée pour ce paiement");
        paymentService.putPaymentInAccount(payment, account);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/refunds/export")
    public ResponseEntity<byte[]> downloadRefunds(@RequestBody RefundSearch refundSearch)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
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
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
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
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
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
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

        if (paymentAssociate == null)
            throw new OsirisValidationException("paymentAssociate");

        // Check referentials

        paymentAssociate.setPayment(
                (Payment) validationHelper.validateReferential(paymentAssociate.getPayment(), true, "Payment"));

        if (paymentAssociate.getPayment().getIsCancelled())
            throw new OsirisValidationException("Payment already cancelled !");

        // Must have same number of association, except when customer payment and refund
        // ITiers / Affaire set
        if (paymentAssociate.getByPassAmount() == null || paymentAssociate.getByPassAmount()
                .size() != (paymentAssociate.getInvoices() == null ? 0 : paymentAssociate.getInvoices().size())
                        + (paymentAssociate.getCustomerOrders() == null ? 0
                                : paymentAssociate.getCustomerOrders().size()))
            if (paymentAssociate.getPayment().getPaymentAmount() < 0 || paymentAssociate.getAffaireRefund() == null
                    && paymentAssociate.getTiersRefund() == null)
                throw new OsirisValidationException("wrong associate number");

        paymentAssociate.setAffaireRefund(
                (Affaire) validationHelper.validateReferential(paymentAssociate.getAffaireRefund(), false, "Affaire"));
        paymentAssociate.setTiersRefund(
                (Tiers) validationHelper.validateReferential(paymentAssociate.getTiersRefund(), false, "Tiers"));

        Responsable responsableOrder = null;
        if (paymentAssociate.getPayment().getPaymentAmount() > 0 && (paymentAssociate.getInvoices() == null
                || paymentAssociate.getInvoices().get(0).getIsCreditNote() == false)) {
            if (paymentAssociate.getResponsableOrder() == null
                    && paymentAssociate.getResponsableOrder().getId() == null)
                throw new OsirisValidationException("no responsable order set");

            responsableOrder = responsableService.getResponsable(paymentAssociate.getResponsableOrder().getId());

            if (responsableOrder == null)
                throw new OsirisValidationException("no responsable order set");
        }

        if (paymentAssociate.getCustomerOrders() != null) {
            if (paymentAssociate.getCustomerOrders().size() == 0)
                paymentAssociate.setCustomerOrders(null);
            else
                for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                    customerOrder = (CustomerOrder) validationHelper.validateReferential(customerOrder, true,
                            "customerOrder");
                }
        }

        // Check invoices status

        if (paymentAssociate.getInvoices() != null) {
            if (paymentAssociate.getInvoices().size() == 0)
                paymentAssociate.setInvoices(null);
            else
                for (Invoice invoice : paymentAssociate.getInvoices()) {
                    invoice = (Invoice) validationHelper.validateReferential(invoice, true, "invoice");
                    if (!invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                            && !invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusReceived().getId())
                            && !invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusCreditNoteReceived().getId()))
                        throw new OsirisValidationException("invoice not send nor received nor credit note received");

                    if (invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCreditNoteEmited().getId()))
                        throw new OsirisValidationException("can't associate payment to customer credit note");

                    if (invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusCreditNoteReceived().getId()))
                        if (Math.round(invoice.getTotalPrice() * 100f) != Math
                                .round(paymentAssociate.getPayment().getPaymentAmount() * 100f))
                            throw new OsirisValidationException("Wrong payment amount");

                    if (invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusReceived().getId())) {
                        if (-Math.round(invoice.getTotalPrice() * 100f) != Math
                                .round(paymentAssociate.getPayment().getPaymentAmount() * 100f))
                            throw new OsirisValidationException("Wrong payment amount");
                    }
                }
        }

        // Check all money used

        Float totalAmount = 0f;
        if (paymentAssociate.getByPassAmount() != null)
            for (Float amount : paymentAssociate.getByPassAmount()) {
                totalAmount += amount;
            }
        totalAmount = Math.round(totalAmount * 100f) / 100f;

        // If incoming, appoint or refund needed, if outgoing, must match
        if (totalAmount != 0) {
            if (totalAmount != Math.round(paymentAssociate.getPayment().getPaymentAmount())) {
                if (paymentAssociate.getPayment().getPaymentAmount() >= 0) {
                    if (paymentAssociate.getPayment().getPaymentAmount() > totalAmount
                            && (Math.abs(totalAmount)
                                    - paymentAssociate.getPayment().getPaymentAmount()) > paymentLimitRefundInEuros)
                        if (paymentAssociate.getTiersRefund() == null && paymentAssociate.getAffaireRefund() == null)
                            throw new OsirisValidationException("not all payment used and no refund tiers set");
                } else if (totalAmount != Math.round(paymentAssociate.getPayment().getPaymentAmount() * 100f) / 100f)
                    throw new OsirisValidationException("not all payment used");
            }
        }

        // Check same customer order for incoming payment
        if (responsableOrder != null) {
            Tiers commonCustomerOrder = responsableOrder.getTiers();
            if (paymentAssociate.getPayment().getPaymentAmount() >= 0
                    && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ADMINISTRATEUR_GROUP)) {
                if (paymentAssociate.getInvoices() != null) {
                    for (Invoice invoice : paymentAssociate.getInvoices()) {
                        if (invoice.getResponsable() != null
                                && !invoice.getResponsable().getTiers().getId().equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                    }
                }

                if (paymentAssociate.getCustomerOrders() != null) {
                    for (CustomerOrder customerOrder : paymentAssociate.getCustomerOrders()) {
                        if (customerOrder.getResponsable() != null
                                && !customerOrder.getResponsable().getTiers().getId()
                                        .equals(commonCustomerOrder.getId()))
                            throw new OsirisValidationException("not same customer order chosed");
                    }
                }
            }
        }

        // Check customer status
        if (paymentAssociate.getCustomerOrders() != null) {
            for (CustomerOrder order : paymentAssociate.getCustomerOrders()) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrder(order.getId());
                if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)
                        || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED))
                    throw new OsirisValidationException(
                            "Impossible de put payment on customer order billed or abandonned");
            }

        }

        paymentService.manualMatchPaymentInvoicesAndCustomerOrders(
                paymentAssociate.getPayment(),
                paymentAssociate.getInvoices(), paymentAssociate.getCustomerOrders(),
                paymentAssociate.getAffaireRefund(), paymentAssociate.getTiersRefund(), responsableOrder,
                paymentAssociate.getByPassAmount());

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
        cashPayment.setPaymentAmount(Math.abs(cashPayment.getPaymentAmount()));
        validationHelper.validateString(cashPayment.getLabel(), true, 250, "paymentType");
        validationHelper.validateDateTimeMax(cashPayment.getPaymentDate(), true, LocalDateTime.now(), "paymentType");
        cashPayment.setIsDeposit(false);
        cashPayment.setIsAppoint(false);

        Float remainingToPay = invoiceService.getRemainingAmountToPayForInvoice(invoice);
        if (remainingToPay == null || remainingToPay < cashPayment.getPaymentAmount())
            throw new OsirisValidationException("paymentAmount");

        this.paymentService.addCashPaymentForCustomerInvoice(cashPayment, invoice);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE)
    @PostMapping(inputEntryPoint + "/payment/cash/add/customer-order")
    public ResponseEntity<Boolean> addCashPaymentForCustomerOrder(@RequestBody Payment cashPayment,
            @RequestParam Integer idCustomerOrder)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);

        if (customerOrder == null || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED)
                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
            throw new OsirisValidationException("customerOrder");

        if (cashPayment == null)
            throw new OsirisValidationException("payment");

        cashPayment.setPaymentType(constantService.getPaymentTypeEspeces());
        cashPayment.setPaymentAmount(Math.abs(cashPayment.getPaymentAmount()));
        validationHelper.validateString(cashPayment.getLabel(), true, 250, "paymentLabel");
        validationHelper.validateDateTimeMax(cashPayment.getPaymentDate(), true, LocalDateTime.now(), "paymentDate");
        cashPayment.setIsDeposit(true);
        cashPayment.setIsAppoint(false);

        this.paymentService.addCashPaymentForCustomerOrder(cashPayment, customerOrder);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    @PostMapping(inputEntryPoint + "/payment/check/add")
    public ResponseEntity<Boolean> addCheckPayment(@RequestBody Payment checkPayment)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (checkPayment == null)
            throw new OsirisValidationException("payment");

        checkPayment.setPaymentType(constantService.getPaymentTypeCheques());
        checkPayment.setPaymentAmount(Math.abs(checkPayment.getPaymentAmount()));
        validationHelper.validateString(checkPayment.getLabel(), true, 250, "paymentType");
        validationHelper.validateDateTimeMax(checkPayment.getPaymentDate(), true, LocalDateTime.now(), "paymentType");
        checkPayment.setIsDeposit(false);
        checkPayment.setIsAppoint(false);

        this.paymentService.addInboundCheckPayment(checkPayment);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payment/provision/add")
    public ResponseEntity<Payment> addProvisionPayment(@RequestBody Payment provisonPayment,
            @RequestParam Integer idProvision)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (provisonPayment == null)
            throw new OsirisValidationException("payment");

        validationHelper.validateReferential(provisonPayment.getPaymentType(), true, "provisionType");

        if (!provisonPayment.getPaymentType().getId().equals(constantService.getPaymentTypeCheques().getId())
                && !provisonPayment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            throw new OsirisClientMessageException("Type de paiement non autorisé");

        provisonPayment.setPaymentAmount(-Math.abs(provisonPayment.getPaymentAmount()));
        validationHelper.validateString(provisonPayment.getLabel(), true, 250, "paymentType");
        validationHelper.validateDateTimeMax(provisonPayment.getPaymentDate(), true, LocalDateTime.now(),
                "paymentType");
        provisonPayment.setIsDeposit(false);
        provisonPayment.setIsAppoint(false);

        if (provisonPayment.getPaymentType().getId().equals(constantService.getPaymentTypeCheques().getId())) {
            validationHelper.validateString(provisonPayment.getCheckNumber(), true, 50, "checkNumber");
        }

        if (idProvision == null)
            throw new OsirisValidationException("idProvision");

        Provision provision = provisionService.getProvision(idProvision);
        if (provision == null)
            throw new OsirisValidationException("provision");

        Payment newPayment = this.paymentService.addOutboundPaymentForProvision(provisonPayment, provision);

        return new ResponseEntity<Payment>(newPayment, HttpStatus.OK);
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
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (invoice.getId() != null && invoice.getCustomerOrder() != null)
            throw new OsirisValidationException("Id");
        validateInvoice(invoice);
        return new ResponseEntity<Invoice>(invoiceService.addOrUpdateInvoiceFromUser(invoice), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/invoice/cancel")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE)
    public ResponseEntity<Invoice> cancelInvoice(@RequestParam Integer idInvoice)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (idInvoice == null)
            throw new OsirisValidationException("Id");

        Invoice invoice = invoiceService.getInvoice(idInvoice);
        if (invoice == null)
            throw new OsirisValidationException("Invoice");

        if (invoice.getCustomerOrder() != null)
            throw new OsirisClientMessageException(
                    "Pour annuler cette facture, repassez la commande correspondante à A facturer");

        if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId())
                || !invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId())
                || !invoice.getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())
                || !invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId())
                        && !invoice.getIsCreditNote() && invoice.getIsCreditNote()
                        && invoice.getProvider() != null && !invoice.getInvoiceStatus().getId()
                                .equals(constantService.getInvoiceStatusReceived().getId()))
            return new ResponseEntity<Invoice>(invoiceService.cancelInvoice(invoice),
                    HttpStatus.OK);
        else
            throw new OsirisClientMessageException("Annulation interdite pour ce statut");
    }

    @PostMapping(inputEntryPoint + "/invoice/credit-note")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Invoice> generateProviderInvoiceCreditNote(@RequestBody Invoice newInvoice,
            @RequestParam Integer idOriginInvoiceForCreditNote)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (newInvoice.getId() != null && newInvoice.getCustomerOrder() != null)
            throw new OsirisValidationException("Id");

        if (idOriginInvoiceForCreditNote == null)
            throw new OsirisValidationException("idOriginInvoiceForCreditNote");

        Invoice originInvoice = invoiceService.getInvoice(idOriginInvoiceForCreditNote);

        if (originInvoice == null)
            throw new OsirisValidationException("originInvoice");

        if (originInvoice.getProvider() != null
                && !newInvoice.getProvider().getId().equals(originInvoice.getProvider().getId()))
            throw new OsirisValidationException("Must be same provider");

        if (newInvoice.getIsCreditNote() == null || newInvoice.getIsCreditNote() == false)
            throw new OsirisValidationException("isProviderCreditNote");
        validateInvoice(newInvoice);
        return new ResponseEntity<Invoice>(
                invoiceService.generateProviderInvoiceCreditNote(newInvoice, idOriginInvoiceForCreditNote),
                HttpStatus.OK);
    }

    private void validateInvoice(Invoice invoice)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (invoice.getIsCreditNote() == null)
            invoice.setIsCreditNote(false);

        int doFound = 0;

        if (invoice.getResponsable() != null) {
            validationHelper.validateReferential(invoice.getResponsable(), true, "Responsable");
            doFound++;
        }

        if (invoice.getProvider() != null) {
            validationHelper.validateReferential(invoice.getProvider(), true, "Provider");
            doFound++;
        }

        if (invoice.getManualAccountingDocumentDate() != null
                && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP)) {
            LocalDate limitDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            LocalDate limitDateAdding = LocalDate.of(LocalDate.now().getYear(), 1, 31);
            if (invoice.getManualAccountingDocumentDate().isBefore(limitDate)
                    && LocalDate.now().isAfter(limitDateAdding)) {
                throw new OsirisClientMessageException("Impossible de saisir une facture sur l'exercice précédent");
            }
        }

        if (doFound != 1)
            throw new OsirisValidationException("Too many customer order");

        BillingLabelType billingLabelAffaire = constantService.getBillingLabelTypeCodeAffaire();

        if (invoice.getProvider() == null && invoice.getIsCreditNote() == false) {
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
                validationHelper.validateReferential(invoiceItem.getVat(), true, "vat of invoice item");
                if (invoiceItem.getId() != null)
                    validationHelper.validateReferential(invoiceItem, true, "invoiceItem");
                if (invoiceItem.getDiscountAmount() == null)
                    invoiceItem.setDiscountAmount(0f);
                if (invoiceItem.getPreTaxPrice() == null)
                    invoiceItem.setPreTaxPrice(0f);
                if (invoiceItem.getVatPrice() == null)
                    invoiceItem.setVatPrice(0f);
                if (invoiceItem.getPreTaxPriceReinvoiced() == null)
                    invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());

            }
        }

        if (invoice.getCustomerOrderForInboundInvoice() != null) {
            if (invoice.getProvider() == null && !invoice.getIsCreditNote())
                throw new OsirisValidationException("customerOrderForInboundInvoice");
            validationHelper.validateReferential(invoice.getCustomerOrderForInboundInvoice(), false,
                    "customerOrderForInboundInvoice");
        }

        // Handle automatic bank transfert
        validationHelper.validateReferential(invoice.getManualPaymentType(), invoice.getProvider() != null,
                "PaymentType");
        if (invoice.getProvider() != null
                && invoice.getManualPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId())) {
            validationHelper.validateIban(invoice.getProvider().getIban(), true, "IBAN");
            validationHelper.validateBic(invoice.getProvider().getBic(), true, "BIC");
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
                customerOrder.getResponsable()), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/paper/label/compute")
    public ResponseEntity<InvoiceLabelResult> computePaperLabelForCustomerOrder(
            @RequestBody CustomerOrder customerOrder) throws OsirisException, OsirisClientMessageException {
        return new ResponseEntity<InvoiceLabelResult>(mailComputeHelper.computePaperLabelResult(customerOrder),
                HttpStatus.OK);
    }
}
