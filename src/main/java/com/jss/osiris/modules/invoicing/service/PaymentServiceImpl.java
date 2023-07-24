package com.jss.osiris.modules.invoicing.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.ofx.OFXParser;
import com.jss.osiris.libs.ofx.OFXStatement;
import com.jss.osiris.libs.ofx.StatementTransaction;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.PaymentSearch;
import com.jss.osiris.modules.invoicing.model.PaymentSearchResult;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.repository.PaymentRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceHelper invoiceHelper;

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

    @Value("${invoicing.payment.limit.refund.euros}")
    private String payementLimitRefundInEuros;

    @Override
    public Payment getPayment(Integer id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent())
            return payment.get();
        return null;
    }

    @Override
    public Payment addOrUpdatePayment(
            Payment payment) {
        if (payment.getIsCancelled() == null)
            payment.setIsCancelled(false);
        if (payment.getIsAppoint() == null)
            payment.setIsAppoint(false);
        if (payment.getIsDeposit() == null)
            payment.setIsDeposit(false);
        return paymentRepository.save(payment);
    }

    @Override
    public List<PaymentSearchResult> searchPayments(PaymentSearch paymentSearch) {
        ArrayList<Integer> paymentWayId = new ArrayList<Integer>();
        if (paymentSearch.getStartDate() == null)
            paymentSearch.setStartDate(LocalDateTime.now().minusYears(100));

        if (paymentSearch.getEndDate() == null)
            paymentSearch.setEndDate(LocalDateTime.now().plusYears(100));

        return paymentRepository.findPayments(paymentWayId,
                paymentSearch.getStartDate().withHour(0).withMinute(0),
                paymentSearch.getEndDate().withHour(23).withMinute(59), paymentSearch.getMinAmount(),
                paymentSearch.getMaxAmount(),
                paymentSearch.getLabel(), paymentSearch.isHideAssociatedPayments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentGrab()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadOfxFile(InputStream file, Integer targetAccountingAccountId)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        OFXStatement operationList = ofxParser.parseOfx(file);

        AccountingAccount accountingAccount = accountingAccountService.getAccountingAccount(targetAccountingAccountId);

        if (accountingAccount == null)
            throw new OsirisValidationException("targetAccountingAccountId");

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
                    payment.setPaymentAmount(Math.abs(transaction.amount().floatValue()));
                    payment.setPaymentDate(transaction.datePosted().atStartOfDay());
                    payment.setPaymentType(constantService.getPaymentTypeVirement());
                    payment.setTargetAccountingAccount(accountingAccount);
                    payment.setIsAppoint(false);
                    payment.setIsDeposit(false);
                    addOrUpdatePayment(payment);
                    accountingRecordService.generateAccountingRecordOnIncomingPaymentCreation(payment);
                }
            }
        automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords();
        return null;
    }

    private void automatchPaymentsInvoicesAndGeneratePaymentAccountingRecords()
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        List<Payment> payments = paymentRepository.findNotAssociatedPayments();

        for (Payment payment : payments) {
            automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(payment);
        }
    }

    @Override
    public void automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(Payment payment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        // Match inbound payment
        if (payment.getPaymentAmount() >= 0) {
            // Get corresponding entities
            List<IndexEntity> correspondingEntities = getCorrespondingEntityForInboudPayment(payment);
            List<Invoice> correspondingInvoices = new ArrayList<Invoice>();
            List<CustomerOrder> correspondingCustomerOrder = new ArrayList<CustomerOrder>();
            List<Quotation> correspondingQuotation = new ArrayList<Quotation>();

            // Get invoices and customer orders and quotations
            Float totalToPay = 0f;
            ArrayList<Integer> foundInvoices = new ArrayList<Integer>();
            if (correspondingEntities != null && correspondingEntities.size() > 0) {
                for (IndexEntity foundEntity : correspondingEntities) {
                    Invoice invoice = getInvoiceForEntity(foundEntity);
                    if (invoice != null
                            && invoice.getInvoiceStatus().getId()
                                    .equals(constantService.getInvoiceStatusSend().getId())
                            && invoice.getProvider() == null && !foundInvoices.contains(invoice.getId())) {
                        foundInvoices.add(invoice.getId());
                        correspondingInvoices.add(invoice);
                        totalToPay += invoiceService.getRemainingAmountToPayForInvoice(invoice);
                    }
                    CustomerOrder customerOrder = getCustomerOrderAtNotBilledStatusForEntity(foundEntity);
                    if (customerOrder != null) {
                        correspondingCustomerOrder.add(customerOrder);
                        totalToPay += customerOrderService.getRemainingAmountToPayForCustomerOrder(customerOrder);
                    }
                    Quotation quotation = getQuotationForEntity(foundEntity);
                    if (quotation != null) {
                        correspondingQuotation.add(quotation);
                        totalToPay += customerOrderService.getTotalForCustomerOrder(quotation);
                    }
                }
            }

            Float remainingMoney = Math.round(payment.getPaymentAmount() * 100f) / 100f;

            // Associate automatically only if we have enough item to put all money
            // For customerOrder and quotation, we can put inlimited money on them
            Float totalItemsAmount = 0f;
            if (correspondingInvoices.size() > 0)
                for (Invoice invoice : correspondingInvoices)
                    totalItemsAmount += invoiceService.getRemainingAmountToPayForInvoice(invoice);

            if (totalItemsAmount < (remainingMoney - Integer.parseInt(payementLimitRefundInEuros))
                    || correspondingCustomerOrder.size() == 0
                    || correspondingQuotation.size() == 0)
                return;

            // Cancel payment, it will be cut afterward
            cancelPayment(payment);

            // Invoices to payed found
            if (correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, null);
            }

            // Quotation waiting customer answer found
            // Transform them to customer order
            if (correspondingQuotation.size() > 0 && remainingMoney > 0) {
                for (Quotation quotation : correspondingQuotation) {
                    if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.VALIDATED_BY_CUSTOMER)
                            && quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0) {
                        boolean found = false;
                        // If already in correspondingCustomerOrder list, do not consider it
                        if (correspondingCustomerOrder.size() > 0)
                            for (CustomerOrder customerOrderFound : correspondingCustomerOrder)
                                if (customerOrderFound.getId().equals(quotation.getCustomerOrders().get(0).getId()))
                                    found = true;
                        if (!found)
                            correspondingCustomerOrder
                                    .add(quotation.getCustomerOrders().get(0));
                    } else if (quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER)) {
                        Quotation validatedQuotation = quotationService.unlockQuotationFromDeposit(quotation);
                        correspondingCustomerOrder.add(validatedQuotation.getCustomerOrders().get(0));
                    }
                }
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder.size() > 0 && remainingMoney > 0) {
                associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder, correspondingInvoices,
                        null, payment.getPaymentAmount());
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
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0), null);
            }

            // TODO : matcher les chèques présaisis
            // TODO : matcher les CB

            // If not found and CB payment, try to match randomly a debour at same day or in
            // 3 days range before
            if (payment.getLabel().contains("FACTURE CARTE")) {
                // TODO : matcher les CB
                /*
                 * List<Debour> debourList =
                 * debourService.findNonAssociatedDeboursForDateAndAmount(
                 * payment.getPaymentDate().toLocalDate(),
                 * payment.getPaymentAmount());
                 * 
                 * if (debourList == null || debourList.size() == 0)
                 * debourList = debourService.findNonAssociatedDeboursForDateAndAmount(
                 * payment.getPaymentDate().toLocalDate().minusDays(1),
                 * payment.getPaymentAmount());
                 * 
                 * if (debourList == null || debourList.size() == 0)
                 * debourList = debourService.findNonAssociatedDeboursForDateAndAmount(
                 * payment.getPaymentDate().toLocalDate().minusDays(2),
                 * payment.getPaymentAmount());
                 * 
                 * if (debourList == null || debourList.size() == 0)
                 * debourList = debourService.findNonAssociatedDeboursForDateAndAmount(
                 * payment.getPaymentDate().toLocalDate().minusDays(3),
                 * payment.getPaymentAmount());
                 * 
                 * if (debourList != null && debourList.size() > 0) {
                 * generateWaitingAccountAccountingRecords = new MutableBoolean(true);
                 * associateOutboundPaymentAndDebour(payment, Arrays.asList(debourList.get(0)));
                 * generateWaitingAccountAccountingRecords = new MutableBoolean(
                 * debourList.get(0).getInvoiceItem() == null);
                 * }
                 */

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualMatchPaymentInvoicesAndCustomerOrders(Payment payment, List<Invoice> correspondingInvoices,
            List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
            List<Float> byPassAmount) throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        payment = getPayment(payment.getId());

        // Cancel payment, it will be cut afterwards
        cancelPayment(payment);

        String refundLabelSuffix = "";
        float remainingMoney = payment.getPaymentAmount();

        if (payment.getPaymentAmount() >= 0) {
            // Invoices to payed found
            if (correspondingInvoices != null && correspondingInvoices.size() > 0) {
                remainingMoney = associateInboundPaymentAndInvoices(payment, correspondingInvoices, byPassAmount);
                refundLabelSuffix = "facture n°" + correspondingInvoices.get(0).getId();
            }

            // Customer order waiting for deposit found
            if (correspondingCustomerOrder != null && correspondingCustomerOrder.size() > 0) {
                remainingMoney = associateInboundPaymentAndCustomerOrders(payment, correspondingCustomerOrder,
                        correspondingInvoices, byPassAmount, remainingMoney);
                refundLabelSuffix = "commande n°" + correspondingCustomerOrder.get(0).getId();
            }

            if (remainingMoney > 0) {
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

                refundService.generateRefund(tiersRefund, affaireRefund, payment, remainingMoney,
                        refundLabelSuffix, customerOrder);
            }
        } else {
            if (correspondingInvoices != null && correspondingInvoices.size() == 1)
                associateOutboundPaymentAndInvoice(payment, correspondingInvoices.get(0), byPassAmount);
        }
    }

    private Float associateInboundPaymentAndCustomerOrders(Payment payment,
            List<CustomerOrder> correspondingCustomerOrder, List<Invoice> correspondingInvoice,
            List<Float> byPassAmount, float remainingMoney)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        Float remainingToPay = 0f;
        int amountIndex = 0;
        if (correspondingInvoice != null)
            amountIndex = correspondingInvoice.size() - 1 + 1;

        for (CustomerOrder customerOrder : correspondingCustomerOrder) {
            remainingToPay += Math
                    .round(customerOrderService.getRemainingAmountToPayForCustomerOrder(customerOrder) * 100f) / 100f;
        }

        // if no by pass, put all on first customer order even if there is too much
        // money
        for (int i = 0; i < correspondingCustomerOrder.size(); i++) {
            Float effectivePayment;
            if (byPassAmount != null) {
                effectivePayment = byPassAmount.get(amountIndex);
                amountIndex++;
            } else {
                effectivePayment = payment.getPaymentAmount();
            }

            // Generate one deposit per customer order
            Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, true);
            associatePaymentAndCustomerOrder(newPayment, correspondingCustomerOrder.get(i));

            remainingToPay -= effectivePayment;

            // Unlocked customer order if necessary
            customerOrderService.unlockCustomerOrderFromDeposit(correspondingCustomerOrder.get(i));
        }
        return Math.round(remainingMoney * 100f) / 100f;
    }

    private Float associateInboundPaymentAndInvoices(Payment payment, List<Invoice> correspondingInvoices,
            List<Float> byPassAmount) throws OsirisException {
        int amountIndex = 0;
        Float remainingMoney = payment.getPaymentAmount();

        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || remainingMoney >= 0f) {
            for (int i = 0; i < correspondingInvoices.size(); i++) {
                if (correspondingInvoices.get(i).getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusSend().getId())) {
                    Float remainingToPayForCurrentInvoice = Math.round(
                            invoiceService.getRemainingAmountToPayForInvoice(correspondingInvoices.get(i)) * 100f)
                            / 100f;
                    Float effectivePayment = 0f;

                    if (byPassAmount != null) {
                        effectivePayment = byPassAmount.get(amountIndex);
                        amountIndex++;
                    } else {
                        effectivePayment = Math.min(remainingToPayForCurrentInvoice, remainingMoney);
                    }

                    Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, false);
                    associatePaymentAndInvoice(newPayment, correspondingInvoices.get(i));

                    remainingMoney -= effectivePayment;

                    remainingToPayForCurrentInvoice = Math
                            .round((remainingToPayForCurrentInvoice - effectivePayment) * 100f) / 100f;

                    // Handle appoint
                    if (Math.abs(remainingToPayForCurrentInvoice) <= Integer.parseInt(payementLimitRefundInEuros)) {
                        Payment appoint = generateNewAppointPayment(-remainingToPayForCurrentInvoice);
                        associatePaymentAndInvoice(appoint, correspondingInvoices.get(i));
                    }
                } else if (correspondingInvoices.get(i).getInvoiceStatus().getId()
                        .equals(constantService.getInvoiceStatusCreditNoteReceived().getId())) {
                    // It's a refund
                    Payment newPayment = generateNewPaymentFromPayment(payment, payment.getPaymentAmount(), false);
                    associatePaymentAndInvoice(newPayment, correspondingInvoices.get(i));
                    remainingMoney = 0f;
                }
            }
        }
        return Math.round(remainingMoney * 100f) / 100f;
    }

    private Float associateOutboundPaymentAndInvoice(Payment payment, Invoice correspondingInvoice,
            List<Float> byPassAmount) throws OsirisException {
        int amountIndex = 0;
        Float remainingMoney = Math.abs(payment.getPaymentAmount());

        // If payment is not over total of remaining to pay on all invoices
        if (byPassAmount != null || remainingMoney > 0f) {
            Float remainingToPayForCurrentInvoice = invoiceService
                    .getRemainingAmountToPayForInvoice(correspondingInvoice);
            Float effectivePayment = 0f;

            if (byPassAmount != null) {
                effectivePayment = byPassAmount.get(amountIndex);
                amountIndex++;
            } else {
                effectivePayment = Math.min(remainingToPayForCurrentInvoice, remainingMoney);
            }

            Payment newPayment = generateNewPaymentFromPayment(payment, effectivePayment, false);
            associatePaymentAndInvoice(newPayment, correspondingInvoice);

            remainingMoney -= remainingToPayForCurrentInvoice;
        }
        return Math.round(remainingMoney * 100f) / 100f;
    }

    private void associateOutboundPaymentAndRefund(Payment payment, Refund refund) throws OsirisException {

        Float refundAmount = Math.round(refund.getRefundAmount() * 100f) / 100f;
        Float paymentAmount = Math.round(payment.getPaymentAmount() * 100f) / 100f;

        if (refundAmount.equals(paymentAmount)) {
            refund.setIsMatched(true);
            refundService.addOrUpdateRefund(refund);
            payment.setRefund(refund);
            addOrUpdatePayment(payment);
        }
    }

    private List<IndexEntity> getCorrespondingEntityForOutboundPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(Refund.class.getSimpleName());

        return getCorrespondingEntityForPayment(payment, entityTypesToSearch);
    }

    private List<IndexEntity> getCorrespondingEntityForInboudPayment(Payment payment) {
        ArrayList<String> entityTypesToSearch = new ArrayList<String>();
        entityTypesToSearch.add(Invoice.class.getSimpleName());
        entityTypesToSearch.add(CustomerOrder.class.getSimpleName());
        entityTypesToSearch.add(Quotation.class.getSimpleName());

        return getCorrespondingEntityForPayment(payment, entityTypesToSearch);
    }

    private List<IndexEntity> getCorrespondingEntityForPayment(Payment payment, ArrayList<String> entityTypesToSearch) {
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

                    Invoice directDebitTransfertInvoice = invoiceService
                            .searchInvoicesByIdDirectDebitTransfert(idToFind);
                    if (directDebitTransfertInvoice != null)
                        tmpEntitiesFound.addAll(searchService.searchForEntitiesById(directDebitTransfertInvoice.getId(),
                                Arrays.asList(Invoice.class.getSimpleName())));
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
                                || customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN)
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
                Float totalRound = Math.round(invoice.getTotalPrice() * 100f) / 100f;
                for (Payment payment : payments) {
                    Float paymentRound = Math.round(payment.getPaymentAmount() * 100f) / 100f;
                    if (paymentRound.equals(totalRound)) {
                        advisedPayments.add(payment);
                    }
                }
            }
        }
        return advisedPayments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Payment> getAdvisedPaymentForCustomerOrder(CustomerOrder customerOrder) {
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
                Float totalRound = customerOrderService.getTotalForCustomerOrder(customerOrder);
                for (Payment payment : payments) {
                    Float paymentRound = Math.round(payment.getPaymentAmount() * 100f) / 100f;
                    if (paymentRound.equals(totalRound)) {
                        advisedPayments.add(payment);
                    }
                }
            }
        }
        return advisedPayments;
    }

    private Payment cancelPayment(Payment payment) throws OsirisException {
        /*
         * Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1,
         * 1000000000);
         * 
         * if (payment.getInvoice() != null &&
         * (payment.getInvoice().getIsInvoiceFromProvider() == null
         * || payment.getInvoice().getIsInvoiceFromProvider() == false))
         * invoiceService.unletterInvoiceEmitted(payment.getInvoice());
         * 
         * if (payment.getAccountingRecords() != null)
         * for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
         * if (accountingRecord.getIsCounterPart() == null ||
         * !accountingRecord.getIsCounterPart())
         * if (!accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().
         * getId()
         * .equals(constantService.getPrincipalAccountingAccountBank().getId())
         * && !accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().
         * getId()
         * .equals(constantService.getPrincipalAccountingAccountCharge().getId()))
         * if (accountingRecord.getAccountingId() == null) {
         * accountingRecordService.deleteAccountingRecord(accountingRecord);
         * } else {
         * accountingRecordService.letterCounterPartRecords(accountingRecord,
         * accountingRecordService.getCounterPart(accountingRecord,
         * operationIdCounterPart, journal));
         * }
         * }
         * payment.setIsCancelled(true);
         * payment.setInvoice(null);
         */
        // TODO
        return addOrUpdatePayment(payment);
    }

    @Override
    public void addCashPaymentForInvoice(Payment cashPayment, Invoice invoice) throws OsirisException {
        addOrUpdatePayment(cashPayment);
        accountingRecordService.generateAccountingRecordOnIncomingPaymentCreation(cashPayment);
        cancelPayment(cashPayment);
        associateInboundPaymentAndInvoices(getPayment(cashPayment.getId()), Arrays.asList(invoice), null);
    }

    @Override
    public void addCheckPayment(Payment checkPayment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        addOrUpdatePayment(checkPayment);
        accountingRecordService.generateAccountingRecordOnIncomingPaymentCreation(checkPayment);
        automatchPaymentInvoicesAndGeneratePaymentAccountingRecords(checkPayment);
    }

    @Override
    public void addCashPaymentForCustomerOrder(Payment cashPayment, CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        addOrUpdatePayment(cashPayment);
        accountingRecordService.generateAccountingRecordOnIncomingPaymentCreation(cashPayment);
        associateInboundPaymentAndCustomerOrders(getPayment(cashPayment.getId()), Arrays.asList(customerOrder),
                new ArrayList<Invoice>(), null, cashPayment.getPaymentAmount());
        cancelPayment(cashPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPayment(Payment payment, Tiers tiers, Affaire affaire)
            throws OsirisException, OsirisClientMessageException {
        tiers = tiersService.getTiers(tiers.getId());
        if (affaire != null)
            affaire = affaireService.getAffaire(affaire.getId());
        payment = getPayment(payment.getId());
        refundService.generateRefund(tiers, affaire, payment, payment.getPaymentAmount(), null, null);
    }

    private Payment generateNewPaymentFromPayment(Payment payment, Float paymentAmount, Boolean isDeposit)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(isDeposit);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(payment);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(payment.getPaymentType());

        return addOrUpdatePayment(newPayment);
    }

    private Payment generateNewAppointPayment(Float paymentAmount) throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(true);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(constantService.getPaymentTypeAccount());
        if (paymentAmount >= 0)
            newPayment.setTargetAccountingAccount(accountingAccountService.getProfitAccountingAccount());
        else
            newPayment.setTargetAccountingAccount(accountingAccountService.getLostAccountingAccount());

        return addOrUpdatePayment(newPayment);
    }

    @Override
    public Payment generateNewAccountPayment(Float paymentAmount, AccountingAccount targetAccountingAccount)
            throws OsirisException {
        Payment newPayment = new Payment();
        newPayment.setIsAppoint(false);
        newPayment.setIsDeposit(false);
        newPayment.setIsCancelled(false);
        newPayment.setIsExternallyAssociated(false);
        newPayment.setOriginPayment(null);
        newPayment.setPaymentAmount(paymentAmount);
        newPayment.setPaymentDate(LocalDateTime.now());
        newPayment.setPaymentType(constantService.getPaymentTypeAccount());
        newPayment.setTargetAccountingAccount(targetAccountingAccount);

        return addOrUpdatePayment(newPayment);
    }

    private void associatePaymentAndCustomerOrder(Payment payment, CustomerOrder customerOrder) {
        // TODO
    }

    private void associatePaymentAndInvoice(Payment payment, Invoice invoice) {
        // TODO
        // Handle outbound and inbound invoice, including provider credit note
        // Handle chekc invoice for lettrage
    }

    @Override
    public void movePaymentFromInvoiceToCustomerOrder(Payment payment, Invoice invoice, CustomerOrder customerOrder) {
        // TODO
    }

    @Override
    public void movePaymentFromCustomerOrderToInvoice(Payment payment, CustomerOrder customerOrder, Invoice invoice) {
        // TODO
    }

    public void generateInvoiceForCentralPayPayment() {
        // TODO : générer facture fournissuer, créer un paiement en compte sur le compte
        // CentralPay, associer le paiement et lettrer
        /*
         * 
         * AccountingAccount accountingAccountBankCentralPay =
         * constantService.getAccountingAccountBankCentralPay();
         * AccountingJournal accountingJournalSales =
         * constantService.getAccountingJournalSales();
         * AccountingJournal accountingJournalPurshases =
         * constantService.getAccountingJournalPurchases();
         * AccountingJournal accountingJournalBank =
         * constantService.getAccountingJournalBank();
         * BillingType billingTypeCentralPayCommission =
         * constantService.getBillingTypeCentralPayFees();
         * 
         * if (accountingAccountBankCentralPay == null)
         * throw new OsirisException(null,
         * "Accounting account for Central Pay not defined in constants");
         * if (accountingJournalSales == null)
         * throw new OsirisException(null,
         * "Accounting journal Sales not defined in constants");
         * if (accountingJournalPurshases == null)
         * throw new OsirisException(null,
         * "Accounting journal Purshases not defined in constants");
         * if (billingTypeCentralPayCommission == null)
         * throw new OsirisException(null,
         * "Billing type central pay fees not defined in constants");
         * if (billingTypeCentralPayCommission.getVat() == null)
         * throw new OsirisException(null,
         * "VAT not defined in billing type central pay fees");
         * if (billingTypeCentralPayCommission.getAccountingAccountCharge() == null)
         * throw new OsirisException(null,
         * "Charge accounting account not defined in billing type central pay fees");
         * if (billingTypeCentralPayCommission.getAccountingAccountCharge() == null)
         * throw new OsirisException(null,
         * "Charge accounting account not defined in billing type central pay fees");
         * if (billingTypeCentralPayCommission.getVat().getRate() == null)
         * throw new OsirisException(null,
         * "Rate not defined in VAT of billing type central pay fees");
         * if (invoice == null && deposit == null)
         * throw new OsirisException(null,
         * "Must provide at least an invoice or a deposit");
         * 
         * String label = "";
         * if (deposit != null)
         * label = "Paiement d'acompte pour la commande n°" + customerOrder.getId();
         * else if (invoice != null)
         * label = "Paiement pour la facture " + invoice.getId();
         * 
         * generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null,
         * null,
         * label, null, payment.getPaymentAmount(),
         * accountingAccountBankCentralPay, null,
         * invoice, customerOrder, accountingJournalBank, payment, deposit, null, null,
         * null);
         * 
         * CentralPayTransaction transaction =
         * centralPayDelegateService.getTransaction(centralPayPaymentRequest);
         * 
         * Float commission = (transaction.getCommission() != null ?
         * transaction.getCommission() : 0f) / 100f;
         * Float preTaxPrice = commission / ((100 +
         * billingTypeCentralPayCommission.getVat().getRate()) / 100f);
         * Float taxPrice = commission - preTaxPrice;
         * 
         * generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null,
         * null,
         * label, commission, null,
         * accountingAccountBankCentralPay, null, invoice, customerOrder,
         * accountingJournalPurshases, payment, deposit,
         * null, null, null);
         * 
         * generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null,
         * null,
         * label, null, preTaxPrice,
         * billingTypeCentralPayCommission.getAccountingAccountCharge(), null, invoice,
         * customerOrder,
         * accountingJournalPurshases, payment, deposit, null, null, null);
         * 
         * generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null,
         * null,
         * label, null, taxPrice,
         * billingTypeCentralPayCommission.getVat().getAccountingAccount(), null,
         * invoice, customerOrder,
         * accountingJournalPurshases, payment, deposit, null, null, null);
         */
    }
}
