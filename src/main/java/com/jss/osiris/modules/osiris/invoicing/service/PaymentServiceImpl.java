package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.PictureHelper;
import com.jss.osiris.libs.QrCodeHelper;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.ofx.OFXParser;
import com.jss.osiris.libs.ofx.OFXStatement;
import com.jss.osiris.libs.ofx.StatementTransaction;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.quotation.controller.model.MyJssImage;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordGenerationService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.OutboundCheckSearch;
import com.jss.osiris.modules.osiris.invoicing.model.OutboundCheckSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.invoicing.repository.PaymentRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.osiris.quotation.model.centralPay.CentralPayTransaction;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.BankTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.CentralPayDelegateService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private BigDecimal zeroValue = new BigDecimal(0);
    private BigDecimal oneHundredValue = new BigDecimal(100);

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingRecordGenerationService accountingRecordGenerationService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    RefundService refundService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    ConstantService constantService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    OFXParser ofxParser;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    TiersService tiersService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    CentralPayDelegateService centralPayDelegateService;

    @Autowired
    VatService vatService;

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Autowired
    DirectDebitTransfertService debitTransfertService;

    @Autowired
    DocumentService documentService;

    @Autowired
    BatchService batchService;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Autowired
    QrCodeHelper qrCodeHelper;

    @Autowired
    AuditService auditService;

    @Value("${payment.cb.entry.point}")
    private String paymentCbEntryPoint;

    @Autowired
    PictureHelper pictureHelper;

    @Override
    public Payment getPayment(Integer id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent())
            return payment.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OutboundCheckSearchResult> searchOutboundChecks(OutboundCheckSearch outboundCheckSearch) {
        if (outboundCheckSearch.getStartDate() == null)
            outboundCheckSearch.setStartDate(LocalDateTime.now().minusYears(100));
        if (outboundCheckSearch.getEndDate() == null)
            outboundCheckSearch.setEndDate(LocalDateTime.now().plusYears(100));
        return paymentRepository.findOutboundChecks(
                outboundCheckSearch.getStartDate().withHour(0).withMinute(0),
                outboundCheckSearch.getEndDate().withHour(23).withMinute(59), outboundCheckSearch.getMinAmount(),
                outboundCheckSearch.getMaxAmount(),
                outboundCheckSearch.getLabel(), outboundCheckSearch.getIsDisplayNonMatchedOutboundChecks());
    }

    @Override
    public Payment addOrUpdatePayment(
            Payment payment) throws OsirisException {
        if (payment.getIsCancelled() == null)
            payment.setIsCancelled(false);
        if (payment.getIsAppoint() == null)
            payment.setIsAppoint(false);
        if (payment.getIsDeposit() == null)
            payment.setIsDeposit(false);
        if (payment.getIsExternallyAssociated() == null)
            payment.setIsExternallyAssociated(false);
        paymentRepository.save(payment);
        batchService.declareNewBatch(Batch.REINDEX_PAYMENT, payment.getId());
        return payment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexPayments() throws OsirisException {
        List<Payment> payments = IterableUtils.toList(paymentRepository.findAll());
        if (payments != null)
            for (Payment payment : payments)
                batchService.declareNewBatch(Batch.REINDEX_PAYMENT, payment.getId());
    }

    @Override
    public void deleteDuplicatePayments() {
        paymentRepository.deleteDuplicatePayments();
    }

    @Override
    public List<PaymentSearchResult> searchPayments(PaymentSearch paymentSearch) {
        if (paymentSearch.getStartDate() == null)
            paymentSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (paymentSearch.getEndDate() == null)
            paymentSearch.setEndDate(LocalDateTime.now().plusYears(100));

        if (paymentSearch.getIdPayment() == null)
            paymentSearch.setIdPayment(0);

        return paymentRepository.findPayments(paymentSearch.getStartDate().withHour(0).withMinute(0),
                paymentSearch.getEndDate().withHour(23).withMinute(59), paymentSearch.getMinAmount(),
                paymentSearch.getMaxAmount(),
                paymentSearch.getLabel(), paymentSearch.isHideAssociatedPayments(),
                paymentSearch.isHideCancelledPayments(), paymentSearch.isHideAppoint(), paymentSearch.isHideNoOfx(),
                paymentSearch.getIdPayment());
    }

    @Override
    public List<Payment> getMatchingOfxPayments(PaymentSearch paymentSearch) {
        if (paymentSearch.getStartDate() == null)
            paymentSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (paymentSearch.getEndDate() == null)
            paymentSearch.setEndDate(LocalDateTime.now().plusYears(100));

        if (paymentSearch.getIdPayment() == null)
            paymentSearch.setIdPayment(0);

        if (paymentSearch.getMinAmount() == null)
            paymentSearch.setMinAmount(Integer.MIN_VALUE + 0f);

        if (paymentSearch.getMaxAmount() == null)
            paymentSearch.setMaxAmount(Integer.MAX_VALUE + 0f);

        if (paymentSearch.getLabel() != null && paymentSearch.getLabel().trim().length() == 0)
            paymentSearch.setLabel(null);

        List<Payment> finalPayments = new ArrayList<Payment>();
        List<Payment> payments = paymentRepository.findPaymentsForOfxMatching(
                paymentSearch.getStartDate().withHour(0).withMinute(0),
                paymentSearch.getEndDate().withHour(23).withMinute(59), LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0), paymentSearch.getMinAmount(),
                paymentSearch.getMaxAmount(),
                paymentSearch.getLabel());

        if (payments != null && payments.size() > 0) {
            for (Payment payment : payments) {

                if (payment.getIsCancelled() != null && payment.getIsCancelled() == false) {
                    payment.setMatchType("En compte d'attente");
                    payment.setMatchAutomation("Automatique");
                }

                else if (payment.getRefund() != null) {
                    payment.setMatchType("Remboursement");
                    payment.setMatchAutomation("Automatique");
                }

                else if (payment.getBankTransfert() != null) {
                    payment.setMatchType("Virement fournisseur");
                    payment.setMatchAutomation("Automatique");
                }

                else if (payment.getDirectDebitTransfert() != null) {
                    payment.setMatchType("Prélèvement client");
                    payment.setMatchAutomation("Automatique");
                }

                else if (payment.getCheckDepositNumber() != null) {
                    payment.setMatchType("Remise de chèque");
                    payment.setMatchAutomation("Automatique");
                }

                else if (payment.getChildrenPayments() == null || payment.getChildrenPayments().size() == 0) {
                    List<Payment> paymentWithLabel = paymentRepository
                            .findByLabelStartsWith("Remboursement du paiement N " + payment.getId());
                    if (paymentWithLabel != null && paymentWithLabel.size() > 0) {
                        payment.setRefund(paymentWithLabel.get(0).getRefund());
                        payment.setMatchType("Remboursement d'un paiement");
                        payment.setMatchAutomation("Automatique");
                    }
                }

                if (payment.getChildrenPayments() == null || payment.getChildrenPayments().size() == 0) {
                    if (payment.getMatchType() == null)
                        payment.setMatchType("Indéterminé");
                    finalPayments.add(payment);
                } else if (payment.getMatchType() == null) {
                    for (Payment childPayment : payment.getChildrenPayments()) {
                        if (childPayment.getAccountingAccount() != null) {
                            payment.setMatchType("Mis en compte");
                            payment.setMatchAutomation("Manuel");
                            payment.setAccountingAccount(childPayment.getAccountingAccount());
                        } else if (childPayment.getInvoice() != null
                                && childPayment.getInvoice().getResponsable() != null) {
                            payment.setMatchType("Associé à une facture client");
                            payment.setInvoice(childPayment.getInvoice());
                        } else if (childPayment.getCheckNumber() != null
                                && childPayment.getPaymentAmount().compareTo(new BigDecimal(0)) < 0) {
                            payment.setMatchType("Chèque fournisseur");
                            payment.setMatchAutomation("Automatique");
                            payment.setCheckNumber(childPayment.getCheckNumber());
                        } else if (childPayment.getInvoice() != null
                                && childPayment.getInvoice().getProvider() != null) {
                            payment.setMatchType("Associé à une facture fournisseur");
                            payment.setInvoice(childPayment.getInvoice());
                        } else if (childPayment.getCustomerOrder() != null) {
                            payment.setMatchType("Associé à une commande");
                            payment.setCustomerOrder(childPayment.getCustomerOrder());
                        } else if (childPayment.getRefund() != null
                                && childPayment.getPaymentAmount().compareTo(new BigDecimal(0)) < 0) {
                            payment.setRefund(childPayment.getRefund());
                            payment.setMatchType("Remboursement d'un trop perçu");
                        }

                        LocalDateTime parentPaymentCreationDate = auditService
                                .getCreationDateTimeForEntity(Payment.class.getSimpleName(), payment.getId());
                        LocalDateTime childPaymentCreationDate = auditService
                                .getCreationDateTimeForEntity(Payment.class.getSimpleName(), childPayment.getId());

                        if (payment.getMatchType() != null && parentPaymentCreationDate != null
                                && childPaymentCreationDate != null && payment.getMatchAutomation() == null) {
                            if (Math.abs(ChronoUnit.MINUTES.between(parentPaymentCreationDate,
                                    childPaymentCreationDate)) > 5)
                                payment.setMatchAutomation("Manuel");
                            else
                                payment.setMatchAutomation("Automatique");
                            break;
                        }
                    }

                    if (payment.getMatchType() == null)
                        payment.setMatchType("Indéterminé");

                    finalPayments.add(payment);
                }
            }
        }

        if (finalPayments != null && finalPayments.size() > 0)
            finalPayments.sort(new Comparator<Payment>() {
                @Override
                public int compare(Payment o1, Payment o2) {
                    if (o1 == null && o2 != null)
                        return -1;
                    else if (o1 != null && o2 == null)
                        return 1;
                    return o1.getPaymentDate().compareTo(o2.getPaymentDate());
                }
            });

        return payments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentGrab()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();

        for (Payment payment : payments) {
            batchService.declareNewBatch(Batch.AUTOMATCH_PAYMENT, payment.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadOfxFile(InputStream file)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        OFXStatement operationList = ofxParser.parseOfx(file);

        if (operationList != null && operationList.getAccountStatements() != null
                && operationList.getAccountStatements().size() > 0
                && operationList.getAccountStatements().get(0) != null
                && operationList.getAccountStatements().get(0).getBankTransactionList() != null
                && operationList.getAccountStatements().get(0).getBankTransactionList().transactions() != null
                && operationList.getAccountStatements().get(0).getBankTransactionList().transactions().size() > 0)
            for (StatementTransaction transaction : operationList.getAccountStatements().get(0).getBankTransactionList()
                    .transactions()) {
                Payment existingTransaction = paymentRepository.findByBankId(transaction.id());
                if (existingTransaction == null) {
                    Payment payment = new Payment();
                    payment.setBankId(transaction.id());
                    payment.setIsExternallyAssociated(false);
                    payment.setLabel(transaction.name() + " " + transaction.memo());
                    payment.setPaymentAmount(transaction.amount());
                    payment.setPaymentDate(transaction.datePosted().atStartOfDay());
                    payment.setPaymentType(constantService.getPaymentTypeVirement());
                    payment.setIsAppoint(false);
                    payment.setIsDeposit(false);
                    payment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
                    payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
                    addOrUpdatePayment(payment);
                    if (transaction.amount().floatValue() > 0)
                        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(payment,
                                false, true);
                    else
                        accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(payment,
                                false, true);
                }
                Payment payment = paymentRepository.findByBankId(transaction.id());
                if (payment != null && (payment.getIsCancelled() == null || payment.getIsCancelled() == false))
                    batchService.declareNewBatch(Batch.AUTOMATCH_PAYMENT, payment.getId());
            }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void automatchPayment(Payment payment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Match inbound payment
        payment = getPayment(payment.getId());
        if (payment.getPaymentAmount().compareTo(zeroValue) >= 0) {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForInboudPayment(payment);
            ArrayList<Invoice> correspondingInvoices = new ArrayList<Invoice>();
            ArrayList<CustomerOrder> correspondingCustomerOrder = new ArrayList<CustomerOrder>();
            ArrayList<Quotation> correspondingQuotation = new ArrayList<Quotation>();

            // Match checks
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                BigDecimal totalAmount = new BigDecimal(0);
                Payment lastPayment = null;
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Payment.class.getSimpleName())) {
                        Payment foundPayment = getPayment(foundEntity.getEntityId());
                        lastPayment = foundPayment;
                        if (foundPayment != null && !payment.getId().equals(foundPayment.getId())) {
                            totalAmount = totalAmount.add(foundPayment.getPaymentAmount());
                        }
                    }
                }

                if (lastPayment != null && totalAmount.setScale(2, RoundingMode.HALF_EVEN)
                        .compareTo(payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN)) == 0) {
                    payment.setCheckDepositNumber(lastPayment.getCheckDepositNumber());
                    addOrUpdatePayment(payment);
                    cancelPayment(payment);
                    return;
                }
            }

            DirectDebitTransfert directDebitFound = null;

            // Get invoices and customer orders and quotations
            ArrayList<Integer> foundInvoices = new ArrayList<Integer>();
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(DirectDebitTransfert.class.getSimpleName())) {
                        DirectDebitTransfert directDebitTransfert = debitTransfertService
                                .getDirectDebitTransfert(foundEntity.getEntityId());
                        if (directDebitTransfert != null && directDebitTransfert.getIsMatched() == false) {
                            directDebitFound = directDebitTransfert;
                            break;
                        }
                    }

                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null
                            && invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusSend().getId())
                            && invoice.getProvider() == null && !foundInvoices.contains(invoice.getId())) {

                        foundInvoices.add(invoice.getId());
                        correspondingInvoices.add(invoice);
                    }
                    CustomerOrder customerOrder = getCustomerOrderAtNotBilledStatusForEntity(foundEntity);
                    if (customerOrder != null) {
                        correspondingCustomerOrder.add(customerOrder);
                    }
                    Quotation quotation = getQuotationForEntity(foundEntity);
                    if (quotation != null) {
                        correspondingQuotation.add(quotation);
                    }
                }
            }

            if (directDebitFound != null) {
                associateInboundPaymentAndDirectDebitTransfert(payment, directDebitFound);
                return;
            }

            BigDecimal remainingMoney = payment.getPaymentAmount().multiply(oneHundredValue).setScale(0)
                    .divide(oneHundredValue);

            // Quotation waiting customer answer found
            // Transform them to customer order
            if (correspondingQuotation.size() > 0 && remainingMoney.compareTo(zeroValue) > 0) {
                for (Quotation quotation : correspondingQuotation) {
                    if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)
                            && quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0) {
                        boolean found = false;
                        // If already in correspondingCustomerOrder list, do not consider it
                        if (correspondingCustomerOrder.size() > 0)
                            for (CustomerOrder customerOrderFound : correspondingCustomerOrder)
                                if (customerOrderFound.getId().equals(quotation.getCustomerOrders().get(0).getId())) {
                                    found = true;
                                }
                        if (!found) {
                            // if customer order billed, use invoice if not already matched
                            if (quotation.getCustomerOrders().get(0).getCustomerOrderStatus().getCode()
                                    .equals(CustomerOrderStatus.BILLED)
                                    && quotation.getCustomerOrders().get(0).getInvoices() != null) {
                                for (Invoice invoice : quotation.getCustomerOrders().get(0).getInvoices()) {
                                    if (invoice.getInvoiceStatus().getId()
                                            .equals(constantService.getInvoiceStatusSend().getId())) {

                                        boolean invoiceFound = false;
                                        if (correspondingInvoices.size() > 0) {
                                            for (Invoice correspondingInvoice : correspondingInvoices) {
                                                if (correspondingInvoice.getId().equals(invoice.getId())) {
                                                    invoiceFound = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!invoiceFound)
                                            correspondingInvoices.add(invoice);
                                    }
                                }
                            } else {
                                correspondingCustomerOrder
                                        .add(quotation.getCustomerOrders().get(0));
                            }
                        }
                    } else if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        Quotation validatedQuotation = quotationService.unlockQuotationFromDeposit(quotation);
                        correspondingCustomerOrder.add(validatedQuotation.getCustomerOrders().get(0));
                    }
                }
            }

            // Associate automatically only if we have enough item to put all money
            BigDecimal totalItemsAmount = new BigDecimal(0);
            if (correspondingInvoices.size() > 0)
                for (Invoice invoice : correspondingInvoices)
                    totalItemsAmount = totalItemsAmount.add(invoiceService.getRemainingAmountToPayForInvoice(invoice));

            if (correspondingInvoices.size() > 0
                    && !totalItemsAmount.multiply(oneHundredValue).setScale(0).equals(remainingMoney
                            .multiply(oneHundredValue).setScale(0)))
                if (correspondingCustomerOrder.size() == 0 && correspondingQuotation.size() == 0)
                    return;

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, null, false);
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder.size() > 0 && remainingMoney.compareTo(zeroValue) > 0) {
                associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder, correspondingInvoices,
                        null, remainingMoney, false);
            }
        } else {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForOutboundPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();

            Refund refundFound = null;
            // Try to match refunds
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Refund.class.getSimpleName())) {
                        Refund refund = refundService.getRefund(foundEntity.getEntityId());
                        if (refund != null && refund.getIsMatched() == false)
                            refundFound = refund;
                    }
                }
            }

            if (refundFound != null) {
                associateOutboundPaymentAndRefund(payment, refundFound);
                return;
            }

            BankTransfert bankTransfertFound = null;
            // Try to match bank transferts
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(BankTransfert.class.getSimpleName())) {
                        BankTransfert bankTransfert = bankTransfertService.getBankTransfert(foundEntity.getEntityId());
                        if (bankTransfert != null
                                && (bankTransfert.getIsMatched() == null || bankTransfert.getIsMatched() == false))
                            bankTransfertFound = bankTransfert;
                    }
                }
            }

            if (bankTransfertFound != null) {
                associateOutboundPaymentAndBankTransfert(payment, bankTransfertFound);
                return;
            }

            // Get invoices
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null && invoice.getInvoiceStatus().getId()
                            .equals(constantService.getInvoiceStatusReceived().getId())) {
                        correspondingInvoices.add(invoice);
                        break;
                    }
                }
            }

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
            }

            // Match checks
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    if (foundEntity.getEntityType().equals(Payment.class.getSimpleName())) {
                        Payment foundPayment = getPayment(foundEntity.getEntityId());
                        if (foundPayment != null && !payment.getId().equals(foundPayment.getId())) {
                            boolean isCancelled = foundPayment.getIsCancelled();
                            // If first payment already associated, check for the child one
                            if (isCancelled && foundPayment.getChildrenPayments() != null
                                    && foundPayment.getChildrenPayments().size() > 0)
                                isCancelled = foundPayment.getChildrenPayments().get(0).getIsCancelled();

                            if (!isCancelled)
                                associateOutboundCheckPayment(payment, foundPayment);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment, List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, Tiers tiersRefund,
            Responsable responsable,
            List<BigDecimal> byPassAmount)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        payment = getPayment(payment.getId());

        BigDecimal remainingMoney = payment.getPaymentAmount();

        if (payment.getPaymentAmount().compareTo(zeroValue) >= 0) {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, byPassAmount,
                        false);
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
                remainingMoney = associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder,
                        correspondingInvoices, byPassAmount, remainingMoney, false);
            }

            if (remainingMoney.compareTo(BigDecimal.valueOf(Integer.parseInt(payementLimitRefundInEuros))) > 0) {
                // Refund
                // Try to find a customerOrder for decorate the refund ...
                CustomerOrder customerOrder = null;
                if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0)
                    customerOrder = correspondingCustomerOrder.get(0);
                else if (correspondingInvoices != null && correspondingInvoices.size() > 0)
                    for (Invoice invoice : correspondingInvoices)
                        if (invoice.getCustomerOrder() != null)
                            customerOrder = invoice.getCustomerOrder();
                        else if (invoice.getCustomerOrderForInboundInvoice() != null)
                            customerOrder = invoice.getCustomerOrderForInboundInvoice();

                refundService.refundPayment(tiersRefund, affaireRefund, responsable.getTiers(), payment,
                        remainingMoney,
                        customerOrder);
            }
        } else {
            if (correspondingInvoices != null && correspondingInvoices.size() == 1)
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0));
        }
    }

    private BigDecimal associateInboundPaymentAndCustomerOrders(Payment payment,
            List<CustomerOrder> correspondingCustomerOrder, List<Invoice> correspondingInvoice,
            List<BigDecimal> byPassAmount, BigDecimal remainingMoney, boolean isMovedFromInvoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        int amountIndex = 0;
        if (correspondingInvoice != null)
            amountIndex = correspondingInvoice.size() - 1 + 1;

        if (remainingMoney.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).compareTo(zeroValue) == 0)
            return zeroValue;

        // if no by pass, put all on last customer order even if there is too much
        // money
        for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
            BigDecimal remainingToPayForCustomerOrder = customerOrderService
                    .getRemainingAmountToPayForCustomerOrder(correspondingCustomerOrder.get(i))
                    .multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue)
                    .max(zeroValue);
            BigDecimal effectivePayment;
            if (byPassAmount != null) {
                effectivePayment = byPassAmount.get(amountIndex);
                amountIndex++;
            } else if (i == correspondingCustomerOrder.size() - 1) { // if last, put all on last customer order
                effectivePayment = remainingMoney;
            } else {
                effectivePayment = remainingToPayForCustomerOrder.min(remainingMoney);
            }

            // Generate one deposit per customer order
            cancelPayment(payment);
            Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, true,
                    correspondingCustomerOrder.get(i).getResponsable().getTiers().getAccountingAccountDeposit());
            newPayment.setCustomerOrder(correspondingCustomerOrder.get(i));
            addOrUpdatePayment(newPayment);

            if (!isMovedFromInvoice) {
                CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                        correspondingCustomerOrder.get(i),
                        "Nouveau paiement n°" + newPayment.getId() + " de " + newPayment.getPaymentAmount()
                                + " € placé sur la commande");

                customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                        constantService.getActiveDirectoryGroupFacturation());
            }
            accountingRecordGenerationService.generateAccountingRecordsForSaleOnCustomerOrderDeposit(
                    correspondingCustomerOrder.get(i), newPayment, false);

            remainingMoney = remainingMoney.subtract(effectivePayment);

            // Unlocked customer order if necessary
            customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i));

            if (remainingMoney.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue)
                    .compareTo(zeroValue) == 0)
                return zeroValue;
        }
        return remainingMoney.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
    }

    private BigDecimal associateInboundPaymentAndInvoices(Payment payment, List<Invoice> correspondingInvoices,
            List<BigDecimal> byPassAmount, boolean isMovedFromCustomerOrder)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        int amountIndex = 0;
        BigDecimal remainingMoney = payment.getPaymentAmount();

        // If payment is not over total of remaining to pay on all invoices
        if (remainingMoney.compareTo(zeroValue) >= 0) {
            for (int i = 0; i < correspondingInvoices.size(); i++) {
                if (correspondingInvoices.get(i).getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusSend().getId())) {
                    BigDecimal remainingToPayForCurrentInvoice = invoiceService
                            .getRemainingAmountToPayForInvoice(correspondingInvoices.get(i)).multiply(oneHundredValue)
                            .setScale(0).divide(oneHundredValue);
                    BigDecimal effectivePayment = new BigDecimal(0);

                    if (remainingToPayForCurrentInvoice.compareTo(zeroValue) < 0)
                        continue;

                    if (byPassAmount != null) {
                        effectivePayment = byPassAmount.get(amountIndex);
                        amountIndex++;
                    } else {
                        effectivePayment = remainingToPayForCurrentInvoice.min(remainingMoney);
                    }

                    // If there is an appoint, use all remaining money, it's handled in
                    // associatePaymentAndInvoice method
                    if (i == correspondingInvoices.size() - 1
                            && remainingToPayForCurrentInvoice.abs().compareTo(zeroValue) > 0
                            && remainingToPayForCurrentInvoice.subtract(remainingMoney).abs()
                                    .compareTo(BigDecimal.valueOf(Integer
                                            .parseInt(payementLimitRefundInEuros))) <= 0)
                        effectivePayment = remainingMoney;

                    cancelPayment(payment);
                    Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, false,
                            correspondingInvoices.get(i).getResponsable().getTiers().getAccountingAccountCustomer());
                    newPayment.setInvoice(correspondingInvoices.get(i));
                    addOrUpdatePayment(newPayment);

                    // Check appoint only for last invoice
                    associatePaymentAndInvoice(newPayment, correspondingInvoices.get(i),
                            i == correspondingInvoices.size() - 1, isMovedFromCustomerOrder);
                    remainingMoney = remainingMoney.subtract(effectivePayment);

                } else if (correspondingInvoices.get(i).getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())) {
                    if (!invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i))
                            .multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).equals(payment
                                    .getPaymentAmount().multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)))
                        throw new OsirisException(null,
                                "Wrong amount to pay on invoice " + correspondingInvoices.get(i).getId()
                                        + " and payment bank id " + payment.getBankId() + " " + payment.getId());
                    // It's a provider refund
                    cancelPayment(payment);
                    Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                            correspondingInvoices.get(i).getProvider().getAccountingAccountProvider());
                    newPayment.setInvoice(correspondingInvoices.get(i));
                    addOrUpdatePayment(newPayment);

                    associatePaymentAndInvoice(newPayment, correspondingInvoices.get(i), false,
                            isMovedFromCustomerOrder);
                    remainingMoney = zeroValue;
                }
            }
        }
        return remainingMoney.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
    }

    private BigDecimal associateOutboundPaymentAndInvoice(Payment payment, Invoice correspondingInvoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        BigDecimal paymentAmount = payment.getPaymentAmount().abs().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
        BigDecimal invoiceAmount = invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoice).abs()
                .multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);

        if (paymentAmount.compareTo(invoiceAmount) <= 0) {
            cancelPayment(payment);
            Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                    payment.getTargetAccountingAccount());
            if (correspondingInvoice.getRff() != null)
                newPayment.setSourceAccountingAccount(
                        correspondingInvoice.getResponsable().getTiers().getAccountingAccountCustomer());
            else
                newPayment
                        .setSourceAccountingAccount(correspondingInvoice.getProvider().getAccountingAccountProvider());

            // If account payment, keep deposit account as target account, except CentralPay
            // which is payed directly with bank account
            if (!payment.getSourceAccountingAccount().getId()
                    .equals(constantService.getAccountingAccountBankCentralPay().getId())) {
                if (newPayment.getPaymentType().getId().equals(constantService.getPaymentTypeAccount().getId()))
                    newPayment.setTargetAccountingAccount(
                            correspondingInvoice.getProvider().getAccountingAccountDeposit());
                else if (!payment.getTargetAccountingAccount().getId()
                        .equals(constantService.getAccountingAccountCaisse().getId()))
                    newPayment.setTargetAccountingAccount(constantService.getAccountingAccountBankJss());
            } else {
                newPayment.setTargetAccountingAccount(constantService.getAccountingAccountBankCentralPay());
            }
            associatePaymentAndInvoice(newPayment, correspondingInvoice, false, false);
        }

        return paymentAmount.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
    }

    private void associateOutboundPaymentAndRefund(Payment payment, Refund refund) throws OsirisException {

        BigDecimal refundAmount = refund.getRefundAmount().multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);
        BigDecimal paymentAmount = payment.getPaymentAmount().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);

        if (refundAmount.compareTo(paymentAmount.negate()) == 0) {
            if (refund.getPayments() != null && refund.getPayments().size() == 1) {
                Payment paymentChild = refund.getPayments().get(0);
                Payment oldParent = paymentChild.getOriginPayment();

                paymentChild.setOriginPayment(payment);
                addOrUpdatePayment(paymentChild);

                payment.setOriginPayment(oldParent);
            }
            refund.setIsMatched(true);
            refundService.addOrUpdateRefund(refund);
            payment.setRefund(refund);
            addOrUpdatePayment(payment);
            cancelPayment(payment);
        }
    }

    private void associateInboundPaymentAndDirectDebitTransfert(Payment payment,
            DirectDebitTransfert directDebitTransfert) throws OsirisException, OsirisValidationException {

        BigDecimal directDebitAmount = directDebitTransfert.getTransfertAmount().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);
        BigDecimal paymentAmount = payment.getPaymentAmount().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);

        if (directDebitAmount.compareTo(paymentAmount) == 0) {
            if (directDebitTransfert.getPayments() != null && directDebitTransfert.getPayments().size() == 1) {
                Payment paymentChild = directDebitTransfert.getPayments().get(0);
                paymentChild.setOriginPayment(payment);
                addOrUpdatePayment(paymentChild);
            }
            payment.setPaymentType(constantService.getPaymentTypePrelevement());
            directDebitTransfert.setIsMatched(true);
            debitTransfertService.addOrUpdateDirectDebitTransfert(directDebitTransfert);
            payment.setDirectDebitTransfert(directDebitTransfert);
            addOrUpdatePayment(payment);
            cancelPayment(payment);
        }
    }

    private void associateOutboundPaymentAndBankTransfert(Payment payment, BankTransfert bankTransfert)
            throws OsirisException, OsirisValidationException {

        BigDecimal bankTransfertAmount = bankTransfert.getTransfertAmount().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);
        BigDecimal paymentAmount = payment.getPaymentAmount().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);

        if (bankTransfertAmount.compareTo(paymentAmount.negate()) == 0) {
            if (bankTransfert.getPayments() != null && bankTransfert.getPayments().size() == 1) {
                Payment paymentChild = bankTransfert.getPayments().get(0);
                paymentChild.setOriginPayment(payment);
                addOrUpdatePayment(paymentChild);
            }
            bankTransfert.setIsMatched(true);
            bankTransfertService.addOrUpdateBankTransfert(bankTransfert);
            payment.setBankTransfert(bankTransfert);
            addOrUpdatePayment(payment);
            cancelPayment(payment);
        }
    }

    private void associateOutboundCheckPayment(Payment inPayment, Payment checkPayment)
            throws OsirisException, OsirisValidationException {
        BigDecimal inAmount = inPayment.getPaymentAmount().multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);
        BigDecimal checkAmount = checkPayment.getPaymentAmount().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN)
                .divide(oneHundredValue);

        if (inAmount.compareTo(checkAmount) == 0) {
            inPayment.setPaymentType(constantService.getPaymentTypeCheques());
            addOrUpdatePayment(inPayment);
            cancelPayment(inPayment);
            checkPayment.setOriginPayment(inPayment);
            addOrUpdatePayment(checkPayment);
        }
    }

    public void cancelAppoint(Payment payment) throws OsirisException, OsirisValidationException {
        cancelPayment(payment);
    }

    @Transactional(rollbackFor = Exception.class)
    public Payment cancelPayment(Payment paymentToCancel) throws OsirisException, OsirisValidationException {
        paymentToCancel = getPayment(paymentToCancel.getId());
        if (paymentToCancel.getIsCancelled())
            return paymentToCancel;
        paymentToCancel.setIsCancelled(true);
        accountingRecordGenerationService.generateAccountingRecordOnPaymentCancellation(paymentToCancel);
        return addOrUpdatePayment(paymentToCancel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCashPaymentForCustomerInvoice(Payment cashPayment, Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        cashPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        cashPayment.setSourceAccountingAccount(constantService.getAccountingAccountCaisse());
        addOrUpdatePayment(cashPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(cashPayment, false, true);
        associateInboundPaymentAndInvoices(getPayment(cashPayment.getId()), Arrays.asList(invoice), null, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInboundCheckPayment(Payment checkPayment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        checkPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        checkPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());

        addOrUpdatePayment(checkPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(checkPayment, false, true);
        batchService.declareNewBatch(Batch.AUTOMATCH_PAYMENT, checkPayment.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment addOutboundPaymentForProvision(Payment payment, Provision provision)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (payment.getId() == null
                && payment.getPaymentType().getId().equals(constantService.getPaymentTypeCheques().getId())
                && payment.getCheckNumber() != null) {
            Payment duplicateCheckPayment = paymentRepository.findByCheckNumber(payment.getCheckNumber());
            if (duplicateCheckPayment != null)
                throw new OsirisValidationException("Numéro de chèque existant");
        }
        payment.setProvision(provision);
        payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        payment.setSourceAccountingAccount(
                payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId())
                        ? constantService.getAccountingAccountCaisse()
                        : constantService.getAccountingAccountBankJss());
        addOrUpdatePayment(payment);
        accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(payment, false, true);
        return payment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        cashPayment.setSourceAccountingAccount(constantService.getAccountingAccountCaisse());
        cashPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(cashPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(cashPayment, false, true);
        associateInboundPaymentAndCustomerOrders(getPayment(cashPayment.getId()), Arrays.asList(customerOrder),
                new ArrayList<Invoice>(), null, cashPayment.getPaymentAmount(), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPayment(Payment payment, Tiers tiers, Affaire affaire)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        tiers = tiersService.getTiers(tiers.getId());
        if (affaire != null)
            affaire = affaireService.getAffaire(affaire.getId());
        payment = getPayment(payment.getId());
        refundService.refundPayment(tiers, affaire, tiers, payment, payment.getPaymentAmount(), null);
    }

    private Payment generateNewPaymentFromPayment(Payment payment, BigDecimal paymentAmount, Boolean isDeposit,
            AccountingAccount targetAccountingAccount)
            throws OsirisException {
        if (!payment.getIsCancelled())
            throw new OsirisException(null, "Payment n°" + payment.getId() + " must be cancelled before clonning !");

        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(isDeposit);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(payment);
        newPayment.setPaymentAmount(paymentAmount);

        newPayment.setPaymentDate(payment.getPaymentDate());
        newPayment.setPaymentType(payment.getPaymentType());
        newPayment.setLabel(payment.getLabel());

        if (targetAccountingAccount == null)
            throw new OsirisException(null, "No target accounting account defined for payment n°" + payment.getId());

        if (newPayment.getPaymentAmount().compareTo(zeroValue) > 0) {
            newPayment.setSourceAccountingAccount(payment.getSourceAccountingAccount());
            newPayment.setTargetAccountingAccount(targetAccountingAccount);
        } else {
            newPayment.setTargetAccountingAccount(payment.getTargetAccountingAccount());
            newPayment.setSourceAccountingAccount(targetAccountingAccount);
        }

        return addOrUpdatePayment(newPayment);
    }

    private Payment generateNewAppointPayment(BigDecimal paymentAmount, Tiers tiersToGiveAppoint, Invoice invoice)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(true);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount.negate());
        newPayment.setLabel("Appoint pour la facture " + invoice.getId());
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(constantService.getPaymentTypeAccount());
        if (newPayment.getPaymentAmount().compareTo(zeroValue) > 0) {
            newPayment.setSourceAccountingAccount(tiersToGiveAppoint.getAccountingAccountCustomer());
            newPayment.setTargetAccountingAccount(constantService.getAccountingAccountProfit());
        } else {
            newPayment.setSourceAccountingAccount(constantService.getAccountingAccountLost());
            newPayment.setTargetAccountingAccount(tiersToGiveAppoint.getAccountingAccountCustomer());
        }

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewRefundPayment(Refund refund, BigDecimal paymentAmount, Tiers tiersToRefund,
            Payment paymentToRefund)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setLabel(refund.getLabel());
        newPayment.setRefund(refund);
        newPayment.setOriginPayment(paymentToRefund);
        newPayment.setPaymentType(constantService.getPaymentTypeVirement());
        newPayment.setTargetAccountingAccount(tiersToRefund.getAccountingAccountCustomer());
        // /!\ Override in
        // AccountingRecordGenerationServiceImpl.generateAccountingRecordsForRefundGeneration
        // to put debit amount in CentralPay account and not JSS Bank account
        newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewBankTransfertPayment(BankTransfert bankTransfert, BigDecimal paymentAmount,
            Provider providerToPay, Responsable responsableToPay)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setLabel(bankTransfert.getLabel());
        newPayment.setBankTransfert(bankTransfert);
        newPayment.setPaymentType(constantService.getPaymentTypeVirement());
        newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
        if (responsableToPay != null)
            newPayment.setTargetAccountingAccount(responsableToPay.getTiers().getAccountingAccountCustomer());
        else
            newPayment.setTargetAccountingAccount(providerToPay.getAccountingAccountProvider());

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewAccountPayment(BigDecimal paymentAmount, AccountingAccount sourceDepositAccountingAccount,
            AccountingAccount targetAccountingAccount,
            String label)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setLabel(label);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(constantService.getPaymentTypeAccount());
        newPayment.setSourceAccountingAccount(sourceDepositAccountingAccount);
        newPayment.setTargetAccountingAccount(targetAccountingAccount);

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewDirectDebitPayment(BigDecimal paymentAmount, String label,
            DirectDebitTransfert directDebitTransfert) throws OsirisException {

        Payment payment = new Payment();
        payment.setIsExternallyAssociated(false);
        payment.setLabel(label);
        payment.setPaymentAmount(paymentAmount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentType(constantService.getPaymentTypePrelevement());
        payment.setIsAppoint(false);
        payment.setIsDeposit(false);
        payment.setDirectDebitTransfert(directDebitTransfert);
        payment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
        payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());

        return addOrUpdatePayment(payment);
    }

    private void associatePaymentAndInvoice(Payment payment, Invoice invoice, boolean checkForAppoint,
            boolean isMovedFromCustomerOrder)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        invoice = invoiceService.getInvoice(invoice.getId());
        payment.setInvoice(invoice);
        if (invoice.getPayments() == null)
            invoice.setPayments(new ArrayList<Payment>());
        invoice.getPayments().add(payment);

        addOrUpdatePayment(payment);

        if (payment.getPaymentAmount().compareTo(zeroValue) >= 0 || payment.getIsAppoint()) {
            if (invoice.getProvider() != null && invoice.getIsCreditNote()) {
                accountingRecordGenerationService.generateAccountingRecordsForProviderInvoiceRefund(invoice, payment,
                        false);
            } else {
                accountingRecordGenerationService.generateAccountingRecordsForSaleOnInvoicePayment(invoice, payment,
                        false);

                if (!isMovedFromCustomerOrder && invoice.getCustomerOrder() != null) {
                    CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                            invoice.getCustomerOrder(),
                            "Nouveau paiement n°" + payment.getId() + " de " + payment.getPaymentAmount()
                                    + " € placé sur la facture n°" + invoice.getId());

                    customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                            constantService.getActiveDirectoryGroupFacturation());
                }
                BigDecimal remainingToPayForCurrentInvoice = invoiceService.getRemainingAmountToPayForInvoice(invoice);
                // Handle appoint
                if (checkForAppoint && remainingToPayForCurrentInvoice.abs().compareTo(zeroValue) > 0
                        && remainingToPayForCurrentInvoice.abs()
                                .compareTo(BigDecimal.valueOf(Integer.parseInt(payementLimitRefundInEuros))) <= 0) {
                    Payment appoint = generateNewAppointPayment(remainingToPayForCurrentInvoice,
                            invoice.getResponsable().getTiers(), invoice);

                    associatePaymentAndInvoice(appoint, invoice, false, isMovedFromCustomerOrder);
                }
            }
        } else {
            accountingRecordGenerationService.generateAccountingRecordsForPurschaseOnInvoicePayment(invoice, payment,
                    false);
        }
    }

    @Override
    public void movePaymentFromInvoiceToCustomerOrder(Payment payment, Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException, OsirisDuplicateException {
        associateInboundPaymentAndCustomerOrders(payment, Arrays.asList(customerOrder), null,
                Arrays.asList(payment.getPaymentAmount()), payment.getPaymentAmount(), true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment movePaymentToWaitingAccount(Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        payment = getPayment(payment.getId());
        cancelPayment(payment);
        Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                accountingAccountService.getWaitingAccountingAccount());
        // If not from bank or null, set it to default bank account
        if (newPayment.getSourceAccountingAccount() == null
                || !newPayment.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                        .equals(constantService.getPrincipalAccountingAccountBank().getId()))
            newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
        newPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(newPayment);
        if (newPayment.getPaymentAmount().compareTo(zeroValue) > 0)
            accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(newPayment,
                    false, false);
        else
            accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(newPayment, false,
                    false);
        return newPayment;
    }

    @Override
    public void movePaymentFromCustomerOrderToInvoice(Payment payment, CustomerOrder customerOrder, Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        associateInboundPaymentAndInvoices(payment, Arrays.asList(invoice), null, true);
    }

    private void generateInvoiceForCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest,
            Payment payment, Invoice targetInvoice, CustomerOrder targetCustomerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        Invoice invoice = new Invoice();

        invoice.setIsCreditNote(false);
        invoice.setProvider(constantService.getProviderCentralPay());
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
        invoice.setCreatedDate(LocalDateTime.now());

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setBillingItem(
                pricingHelper.getAppliableBillingItem(constantService.getBillingTypeCentralPayFees(), null));
        invoiceItem.setDiscountAmount(zeroValue);
        invoiceItem.setIsGifted(false);
        invoiceItem.setIsOverridePrice(false);

        if (targetCustomerOrder == null && targetInvoice == null)
            throw new OsirisException(null,
                    "No target invoice and customer order set for central pay invoice generation");

        String invoiceLabel = "";
        if (targetCustomerOrder != null) {
            invoiceLabel = "Commission CentralPay - Commande n°" + targetCustomerOrder.getId();
            invoiceItem.setLabel(
                    "Commission CentralPay - Paiement d'acompte pour la commande n°" + targetCustomerOrder.getId());
        } else {
            invoiceLabel = "Commission CentralPay - Facture n°" + targetInvoice.getId();
            invoiceItem.setLabel("Commission CentralPay - Paiement pour la facture " + targetInvoice.getId());
        }

        CentralPayTransaction transaction = centralPayDelegateService.getTransaction(centralPayPaymentRequest);
        BigDecimal commission = (transaction.getCommission() != null ? BigDecimal.valueOf(transaction.getCommission())
                : zeroValue)
                .divide(oneHundredValue);

        invoiceItem.setPreTaxPrice(
                commission.divide(new BigDecimal(1.2)).multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN)
                        .divide(oneHundredValue));
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());
        invoice.getInvoiceItems().add(invoiceItem);
        vatService.completeVatOnInvoiceItem(invoiceItem, invoice);

        Invoice centralPayInvoice = invoiceService.addOrUpdateInvoiceFromUser(invoice);
        Payment centralPayPayment = generateNewAccountPayment(
                invoiceItem.getPreTaxPrice().add(invoiceItem.getVatPrice()).negate(),
                constantService.getAccountingAccountBankCentralPay(),
                constantService.getProviderCentralPay().getAccountingAccountProvider(), invoiceLabel);
        accountingRecordGenerationService.generateAccountingRecordOnOutgoingPaymentCreation(centralPayPayment, false,
                true);

        associateOutboundPaymentAndInvoice(centralPayPayment, centralPayInvoice);
    }

    @Override
    public Payment generateDepositOnCustomerOrderForCbPayment(CustomerOrder customerOrder,
            CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
        // Generate payment to materialize CB payment
        Payment payment = generateCentralPayPayment(centralPayPaymentRequest, true, null, customerOrder);
        generateInvoiceForCentralPayPayment(centralPayPaymentRequest, payment, null, customerOrder);
        associateInboundPaymentAndCustomerOrders(payment, Arrays.asList(customerOrder), null, null,
                payment.getPaymentAmount(), false);
        return payment;
    }

    @Override
    public void generatePaymentOnInvoiceForCbPayment(Invoice invoice,
            CentralPayPaymentRequest centralPayPaymentRequest)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        // Generate payment to materialize CB payment
        Payment payment = generateCentralPayPayment(centralPayPaymentRequest, true, invoice, null);
        generateInvoiceForCentralPayPayment(centralPayPaymentRequest, payment, invoice, null);
        associateInboundPaymentAndInvoices(payment, Arrays.asList(invoice), null, false);
    }

    @Override
    public void unassociateInboundPaymentFromInvoice(Payment payment, Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        cancelPayment(payment);
        Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                accountingAccountService.getWaitingAccountingAccount());
        newPayment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(newPayment);
        accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(newPayment, true, false);
    }

    private Payment generateCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest, boolean isForDepostit,
            Invoice invoice, CustomerOrder customerOrder)
            throws OsirisException {
        Payment payment = new Payment();
        payment.setIsExternallyAssociated(false);
        payment.setBankId(centralPayPaymentRequest.getPaymentRequestId());
        payment.setLabel(centralPayPaymentRequest.getDescription());

        // Compute DO label
        CustomerOrder labelledCustomerOrder = customerOrder;
        if (invoice != null && invoice.getCustomerOrder() != null)
            labelledCustomerOrder = invoice.getCustomerOrder();

        if (labelledCustomerOrder != null) {
            InvoiceLabelResult labelResult = invoiceHelper.computeInvoiceLabelResult(
                    documentService.getBillingDocument(labelledCustomerOrder.getDocuments()),
                    labelledCustomerOrder, labelledCustomerOrder.getResponsable());
            if (labelResult != null && labelResult.getBillingLabel() != null)
                payment.setLabel(payment.getLabel() + " - " + labelResult.getBillingLabel());
        }
        payment.setPaymentAmount(centralPayPaymentRequest.getTotalAmount().divide(oneHundredValue));
        payment.setPaymentDate(centralPayPaymentRequest.getCreationDate());
        payment.setPaymentType(constantService.getPaymentTypeCB());
        payment.setIsCancelled(isForDepostit);
        payment.setInvoice(invoice);
        payment.setSourceAccountingAccount(constantService.getAccountingAccountBankCentralPay());
        payment.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
        addOrUpdatePayment(payment);
        return payment;
    }

    private List<IndexEntity> getCorrespondingEntityForOutboundPayment(Payment payment) throws OsirisException {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(Refund.class.getSimpleName());
        entityTypesToSearch.add(BankTransfert.class.getSimpleName());
        entityTypesToSearch.add(Payment.class.getSimpleName());

        return getCorrespondingEntityForPayment(payment, entityTypesToSearch);
    }

    private List<IndexEntity> getCorrespondingEntityForInboudPayment(Payment payment) throws OsirisException {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(CustomerOrder.class.getSimpleName());
        entityTypesToSearch.add(Quotation.class.getSimpleName());
        entityTypesToSearch.add(DirectDebitTransfert.class.getSimpleName());

        return getCorrespondingEntityForPayment(payment, entityTypesToSearch);
    }

    private List<IndexEntity> getCorrespondingEntityForPayment(Payment payment, ArrayList<String> entityTypesToSearch)
            throws OsirisException {
        Pattern p = Pattern.compile("\\d+");
        List<IndexEntity> entitiesFound = new ArrayList<IndexEntity>();

        // Find possible references
        if (payment.getLabel() != null) {
            Matcher m = p.matcher(payment.getLabel());
            while (m.find()) {
                Integer idToFind = null;
                List<IndexEntity> tmpEntitiesFound = null;
                try {
                    idToFind = Integer.parseInt(m.group());
                } catch (NumberFormatException e) {
                }

                if (idToFind != null) {
                    tmpEntitiesFound = searchService.searchForEntitiesById(idToFind, entityTypesToSearch);
                }
                if ((tmpEntitiesFound == null || tmpEntitiesFound.size() == 0)
                        && payment.getPaymentAmount().compareTo(zeroValue) < 0
                        && payment.getLabel().contains("CHEQUE ")) {
                    // Try check on outbound payments
                    tmpEntitiesFound = searchService.searchForEntities("\"checkNumber\":\"" + idToFind + "\"",
                            Payment.class.getSimpleName(), true);
                }
                if ((tmpEntitiesFound == null || tmpEntitiesFound.size() == 0)
                        && payment.getPaymentAmount().compareTo(zeroValue) > 0
                        && payment.getLabel().contains("REMISE CHEQUES ")) {
                    // Try check on inbound payments
                    tmpEntitiesFound = searchService.searchForEntities(
                            "\"checkDepositNumber\":\"" + StringUtils.leftPad(idToFind + "", 8, "0") + "\"",
                            Payment.class.getSimpleName(), true);
                }
                if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                    for (IndexEntity newEntity : tmpEntitiesFound) {
                        boolean found = false;
                        for (IndexEntity entityAlreadyFound : entitiesFound) {
                            if (entityAlreadyFound.getEntityId().equals(newEntity.getEntityId())
                                    && entityAlreadyFound.getEntityType().equals(newEntity.getEntityType())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                            entitiesFound.add(newEntity);
                    }
                }
            }
        }
        return entitiesFound;
    }

    private Invoice getInvoiceForEntity(IndexEntity foundEntity) throws OsirisException {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(Invoice.class.getSimpleName())) {
                Invoice invoice = invoiceService.getInvoice(foundEntity.getEntityId());

                if (invoice.getInvoiceStatus() == null)
                    throw new OsirisException(null,
                            "No invoice status found for entity n°" + foundEntity.getEntityId());

                if (invoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusSend().getId()) || invoice
                        .getInvoiceStatus().getId().equals(constantService.getInvoiceStatusReceived().getId()))
                    return invoice;
            }
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                List<InvoiceSearchResult> invoices = invoiceService
                        .getInvoiceForCustomerOrder(foundEntity.getEntityId());
                if (invoices != null && invoices.size() > 0)
                    for (InvoiceSearchResult invoice : invoices) {
                        Invoice completeInvoice = invoiceService.getInvoice(invoice.getInvoiceId());
                        if (completeInvoice.getInvoiceStatus().getId()
                                .equals(constantService.getInvoiceStatusSend().getId()))
                            return completeInvoice;
                    }
            }
            if (foundEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                Quotation quotation = quotationService.getQuotation(foundEntity.getEntityId());
                if (quotation != null && quotation.getCustomerOrders() != null
                        && quotation.getCustomerOrders().size() > 0) {
                    for (CustomerOrder customerOrder : quotation.getCustomerOrders()) {
                        List<InvoiceSearchResult> invoices = invoiceService
                                .getInvoiceForCustomerOrder(customerOrder.getId());
                        if (invoices != null && invoices.size() > 0)
                            for (InvoiceSearchResult invoice : invoices) {
                                Invoice completeInvoice = invoiceService.getInvoice(invoice.getInvoiceId());
                                if (completeInvoice.getInvoiceStatus().getId()
                                        .equals(constantService.getInvoiceStatusSend().getId()))
                                    return completeInvoice;
                            }
                    }
                }
            }
        }
        return null;
    }

    private CustomerOrder getCustomerOrderAtNotBilledStatusForEntity(IndexEntity foundEntity) {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                CustomerOrder customerOrder = customerOrderService.getCustomerOrder(foundEntity.getEntityId());
                if (customerOrder.getCustomerOrderStatus() != null
                        && (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)
                                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.DRAFT)
                                || customerOrder.getCustomerOrderStatus().getCode()
                                        .equals(CustomerOrderStatus.BEING_PROCESSED)
                                || customerOrder.getCustomerOrderStatus().getCode()
                                        .equals(CustomerOrderStatus.TO_BILLED)))
                    return customerOrder;
            }
        }
        return null;
    }

    private Quotation getQuotationForEntity(IndexEntity foundEntity) {
        if (foundEntity != null && foundEntity.getEntityType() != null) {
            if (foundEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                Quotation quotation = quotationService.getQuotation(foundEntity.getEntityId());
                if (quotation.getQuotationStatus() != null
                        && (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)
                                || quotation.getQuotationStatus().getCode()
                                        .equals(QuotationStatus.VALIDATED_BY_CUSTOMER)))
                    return quotation;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public List<Payment> getAdvisedPaymentForInvoice(Invoice invoice) {
        invoice = invoiceService.getInvoice(invoice.getId());
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();
        List<Payment> advisedPayments = new ArrayList<Payment>();
        if (payments != null && payments.size() > 0) {
            Pattern p = Pattern.compile("\\d+");
            for (Payment payment : payments) {
                if (payment.getLabel() != null) {
                    Matcher m = p.matcher(payment.getLabel());
                    while (m.find()) {
                        if (m.group().equals(invoice.getId().toString()) && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                        else if (invoice.getCustomerOrder() != null
                                && m.group().equals(invoice.getCustomerOrder().getId().toString())
                                && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                        else if (invoice.getCustomerOrder() != null
                                && invoice.getCustomerOrder().getQuotations() != null
                                && invoice.getCustomerOrder().getQuotations().size() > 0 && m.group()
                                        .equals(invoice.getCustomerOrder().getQuotations().get(0).getId().toString())
                                && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                    }
                }
            }
            // If no match by name, attempt by amount
            if (advisedPayments.size() == 0) {
                BigDecimal totalRound = invoice.getTotalPrice().multiply(oneHundredValue)
                        .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
                for (Payment payment : payments) {
                    BigDecimal paymentRound = payment.getPaymentAmount().multiply(oneHundredValue)
                            .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
                    if (invoice.getProvider() != null && paymentRound.compareTo(totalRound.negate()) == 0)
                        advisedPayments.add(payment);
                    else if (invoice.getIsCreditNote() && paymentRound.compareTo(totalRound.negate()) == 0)
                        advisedPayments.add(payment);
                    else if (paymentRound.compareTo(totalRound.negate()) == 0)
                        advisedPayments.add(payment);
                }
            }
        }
        return advisedPayments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder) throws OsirisException {
        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();
        List<Payment> advisedPayments = new ArrayList<Payment>();
        if (payments != null && payments.size() > 0) {
            Pattern p = Pattern.compile("\\d+");
            for (Payment payment : payments) {
                if (payment.getLabel() != null) {
                    Matcher m = p.matcher(payment.getLabel());
                    while (m.find()) {
                        if (m.group().equals(customerOrder.getId().toString()) && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                        else if (customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0
                                && m.group().equals(customerOrder.getQuotations().get(0).getId().toString())
                                && !advisedPayments.contains(payment))
                            advisedPayments.add(payment);
                    }
                }
            }
            // If no match by name, attempt by amount
            if (advisedPayments.size() == 0) {
                BigDecimal totalRound = customerOrderService.getInvoicingSummaryForIQuotation(customerOrder)
                        .getTotalPrice();
                for (Payment payment : payments) {
                    BigDecimal paymentRound = payment.getPaymentAmount().multiply(oneHundredValue)
                            .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue);
                    if (paymentRound.compareTo(totalRound) == 0) {
                        advisedPayments.add(payment);
                    }
                }
            }
        }
        return advisedPayments;
    }

    @Override
    public Payment getOriginalPaymentOfPayment(Payment payment) {
        if (payment.getOriginPayment() == null)
            return payment;

        payment = payment.getOriginPayment();
        return getOriginalPaymentOfPayment(payment);
    }

    @Override
    public void putPaymentInAccount(Payment payment, AccountingAccount accountingAccount)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        cancelPayment(payment);
        Payment newPayment = null;
        if (payment.getPaymentAmount().compareTo(zeroValue) > 0) {
            newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                    accountingAccount);
            // If not from bank or null, set it to default bank account
            if (newPayment.getSourceAccountingAccount() == null
                    || !newPayment.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId()))
                newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment.setTargetAccountingAccount(accountingAccount);
            accountingRecordGenerationService
                    .generateAccountingRecordOnIncomingPaymentOnAccountingAccount(newPayment);
            newPayment.setAccountingAccount(accountingAccount);
        } else {
            newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false,
                    accountingAccount);
            // If not from bank or null, set it to default bank account
            if (newPayment.getSourceAccountingAccount() == null
                    || !newPayment.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId()))
                newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment.setTargetAccountingAccount(accountingAccount);
            accountingRecordGenerationService
                    .generateAccountingRecordOnOutgoingPaymentOnAccountingAccount(newPayment);
            newPayment.setAccountingAccount(accountingAccount);
        }
        addOrUpdatePayment(newPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment cutPayment(Payment payment, BigDecimal amount)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        cancelPayment(payment);
        Payment newPayment1 = null;
        Payment newPayment2 = null;
        if (payment.getPaymentAmount().compareTo(zeroValue) > 0) {
            newPayment1 = generateNewPaymentFromPayment(payment, payment.getPaymentAmount().subtract(amount), false,
                    accountingAccountService.getWaitingAccountingAccount());
            // If not from bank or null, set it to default bank account
            if (newPayment1.getSourceAccountingAccount() == null
                    || !newPayment1.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId()))
                newPayment1.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment1.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment1.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
            accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(newPayment1, true,
                    false);

            newPayment2 = generateNewPaymentFromPayment(payment, amount, false,
                    accountingAccountService.getWaitingAccountingAccount());
            // If not from bank or null, set it to default bank account
            if (newPayment2.getSourceAccountingAccount() == null
                    || !newPayment2.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId()))
                newPayment2.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment2.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment2.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
            accountingRecordGenerationService.generateAccountingRecordOnIncomingPaymentCreation(newPayment2, true,
                    false);
            addOrUpdatePayment(newPayment1);
            addOrUpdatePayment(newPayment2);
        } else {
            newPayment1 = generateNewPaymentFromPayment(payment, payment.getPaymentAmount().add(amount), false,
                    accountingAccountService.getWaitingAccountingAccount());
            // If not from bank or null, set it to default bank account
            if (newPayment1.getSourceAccountingAccount() == null
                    || !newPayment1.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId()))
                newPayment1.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment1.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment1.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
            accountingRecordGenerationService
                    .generateAccountingRecordOnOutgoingPaymentCreation(newPayment1, true, false);

            newPayment2 = generateNewPaymentFromPayment(payment, amount.negate(), false,
                    accountingAccountService.getWaitingAccountingAccount());
            // If not from bank or null, set it to default bank account
            if (newPayment2.getSourceAccountingAccount() == null
                    || !newPayment2.getSourceAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId()))
                newPayment2.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment2.setSourceAccountingAccount(constantService.getAccountingAccountBankJss());
            newPayment2.setTargetAccountingAccount(accountingAccountService.getWaitingAccountingAccount());
            accountingRecordGenerationService
                    .generateAccountingRecordOnOutgoingPaymentCreation(newPayment2, true, false);
            addOrUpdatePayment(newPayment1);
            addOrUpdatePayment(newPayment2);
        }

        return payment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MyJssImage downloadQrCodeForOrderPayment(CustomerOrder customerOrder, String mail) throws OsirisException {
        MyJssImage image = new MyJssImage();
        image.setAddress(
                paymentCbEntryPoint + "/order/deposit?customerOrderId=" + customerOrder.getId() + "&mail=" + mail);
        image.setData(pictureHelper
                .getPictureAsBase64String(qrCodeHelper.getQrCode(image.getAddress(), 150)));
        return image;
    }

}
