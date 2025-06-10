package com.jss.osiris.modules.osiris.accounting.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.SageRecord;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.RefundService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;

@Service
public class AccountingRecordGenerationServiceImpl implements AccountingRecordGenerationService {

    private BigDecimal oneHundredValue = new BigDecimal(100);
    private BigDecimal zeroValue = new BigDecimal(0);

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    RefundService refundService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    CustomerOrderService customerOrderService;

    private Integer getNewTemporaryOperationId() {
        return ThreadLocalRandom.current().nextInt(1, 1000000000);
    }

    private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
            String manualAccountingDocumentNumber, LocalDate manualAccountingDocumentDate, String label,
            BigDecimal creditAmount,
            BigDecimal debitAmount, AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice,
            CustomerOrder customerOrder, AccountingJournal journal, Payment payment, Refund refund,
            BankTransfert bankTransfert) throws OsirisClientMessageException, OsirisException {
        AccountingRecord accountingRecord = new AccountingRecord();
        accountingRecord.setOperationDateTime(operationDatetime);
        accountingRecord.setTemporaryOperationId(operationId);
        accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
        accountingRecord.setManualAccountingDocumentDate(manualAccountingDocumentDate);
        accountingRecord.setLabel(label);
        if (creditAmount != null)
            accountingRecord.setCreditAmount(creditAmount);
        if (debitAmount != null)
            accountingRecord.setDebitAmount(debitAmount);
        accountingRecord.setAccountingAccount(accountingAccount);
        accountingRecord.setIsTemporary(true);
        accountingRecord.setInvoiceItem(invoiceItem);
        accountingRecord.setIsANouveau(false);
        accountingRecord.setInvoice(invoice);
        accountingRecord.setCustomerOrder(customerOrder);
        accountingRecord.setAccountingJournal(journal);
        accountingRecord.setIsCounterPart(false);
        accountingRecord.setPayment(payment);
        accountingRecord.setRefund(refund);
        accountingRecord.setBankTransfert(bankTransfert);
        accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, false);

        if (accountingRecord.getCreditAmount() != null
                && accountingRecord.getCreditAmount().compareTo(zeroValue) < 0
                || accountingRecord.getDebitAmount() != null
                        && accountingRecord.getDebitAmount().compareTo(zeroValue) < 0)
            throw new OsirisClientMessageException("Negative debit or credit !");
        return accountingRecord;
    }

    @Override
    public void counterPartExistingManualRecords(List<AccountingRecord> records, LocalDateTime counterPartDateTime)
            throws OsirisException {
        Integer operationId = getNewTemporaryOperationId();
        BigDecimal balance = new BigDecimal(0);

        if (records != null && counterPartDateTime != null)
            for (AccountingRecord accountingRecord : records) {
                if (accountingRecord.getCreditAmount() != null)
                    balance = balance.add(accountingRecord.getCreditAmount());
                else
                    balance = balance.subtract(accountingRecord.getDebitAmount());
                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId,
                        accountingRecord.getAccountingJournal(),
                        "Annulation - " + accountingRecord.getLabel(), counterPartDateTime);
                accountingRecord.setContrePasse(counterPart);
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, false);
                letterCounterPartRecords(accountingRecord, counterPart);
            }

        checkBalance(balance);
    }

    private AccountingRecord getCounterPart(AccountingRecord originalAccountingRecord, Integer operationId,
            AccountingJournal journal, String label, LocalDateTime operationDateTime) throws OsirisException {
        AccountingRecord newAccountingRecord = new AccountingRecord();
        newAccountingRecord.setAccountingAccount(originalAccountingRecord.getAccountingAccount());
        newAccountingRecord.setAccountingJournal(journal);
        newAccountingRecord.setLetteringDateTime(null);
        newAccountingRecord.setAccountingDateTime(null);
        newAccountingRecord.setCreditAmount(originalAccountingRecord.getDebitAmount());
        newAccountingRecord.setDebitAmount(originalAccountingRecord.getCreditAmount());
        newAccountingRecord.setRefund(originalAccountingRecord.getRefund());
        newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
        newAccountingRecord.setInvoiceItem(originalAccountingRecord.getInvoiceItem());
        newAccountingRecord.setIsANouveau(false);
        newAccountingRecord.setIsTemporary(true);
        newAccountingRecord.setLabel(label);
        newAccountingRecord.setManualAccountingDocumentDate(originalAccountingRecord.getManualAccountingDocumentDate());
        newAccountingRecord
                .setManualAccountingDocumentNumber(originalAccountingRecord.getManualAccountingDocumentNumber());
        newAccountingRecord.setPayment(originalAccountingRecord.getPayment());
        newAccountingRecord.setTemporaryOperationId(operationId);
        newAccountingRecord.setOperationDateTime(operationDateTime != null ? operationDateTime : LocalDateTime.now());
        newAccountingRecord.setCustomerOrder(originalAccountingRecord.getCustomerOrder());
        newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
        newAccountingRecord.setIsCounterPart(true);
        newAccountingRecord.setIsManual(originalAccountingRecord.getIsManual());
        return accountingRecordService.addOrUpdateAccountingRecord(newAccountingRecord, false);
    }

    private void checkBalance(BigDecimal balance) throws OsirisValidationException {
        if (balance.multiply(oneHundredValue).setScale(0, RoundingMode.HALF_EVEN).abs()
                .compareTo(new BigDecimal(1)) > 0)
            throw new OsirisValidationException("Balance not null");
    }

    public void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
        if (letterInvoice(invoice)) {
            invoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
            if (invoice.getCustomerOrder() != null) {
                invoice.getCustomerOrder().setIsPayed(true);
                customerOrderService.addOrUpdateCustomerOrder(invoice.getCustomerOrder(), false, false);
            }
            invoiceService.addOrUpdateInvoice(invoice);
        }
    }

    private void checkRefundForLettrage(Refund refund) throws OsirisException {
        refund = refundService.getRefund(refund.getId());

        AccountingAccount account = null;

        if (refund.getTiers() != null)
            account = refund.getTiers().getAccountingAccountCustomer();

        if (account == null)
            throw new OsirisException(null, "Accounting account not found for refund");

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndRefund(account, refund);

        Double balance = 0.0;

        if (accountingRecords != null && accountingRecords.size() > 0) {
            for (AccountingRecord accountingRecord : accountingRecords) {
                if (accountingRecord.getDebitAmount() != null)
                    balance += accountingRecord.getDebitAmount().doubleValue();
                if (accountingRecord.getCreditAmount() != null)
                    balance -= accountingRecord.getCreditAmount().doubleValue();
            }

            if (Math.round(Math.abs(balance) * 100f) / 100f <= 0.01) {

                Integer maxLetteringNumber = accountingRecordService
                        .findMaxLetteringNumberForMinLetteringDateTime(
                                LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                                        .with(ChronoField.HOUR_OF_DAY, 0)
                                        .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

                if (maxLetteringNumber == null)
                    maxLetteringNumber = 0;
                maxLetteringNumber++;

                for (AccountingRecord accountingRecord : accountingRecords) {
                    accountingRecord.setLetteringDateTime(LocalDateTime.now());
                    accountingRecord.setLetteringNumber(maxLetteringNumber);
                    this.accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
                }

            }
        }
    }

    private boolean letterInvoice(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccount;
        invoice = invoiceService.getInvoice(invoice.getId());
        if (invoice.getProvider() != null)
            accountingAccount = invoice.getProvider().getAccountingAccountProvider();
        else
            accountingAccount = invoice.getResponsable().getTiers().getAccountingAccountCustomer();

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndInvoice(accountingAccount, invoice);

        Double balance = 0.0;

        if (accountingRecords != null && accountingRecords.size() > 0) {
            for (AccountingRecord accountingRecord : accountingRecords) {
                if (accountingRecord.getDebitAmount() != null)
                    balance += accountingRecord.getDebitAmount().doubleValue();
                if (accountingRecord.getCreditAmount() != null)
                    balance -= accountingRecord.getCreditAmount().doubleValue();
            }

            if (Math.round(Math.abs(balance) * 100f) / 100f <= 0.01) {

                Integer maxLetteringNumber = accountingRecordService
                        .findMaxLetteringNumberForMinLetteringDateTime(
                                LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                                        .with(ChronoField.HOUR_OF_DAY, 0)
                                        .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

                if (maxLetteringNumber == null)
                    maxLetteringNumber = 0;
                maxLetteringNumber++;

                for (AccountingRecord accountingRecord : accountingRecords) {
                    accountingRecord.setLetteringDateTime(LocalDateTime.now());
                    accountingRecord.setLetteringNumber(maxLetteringNumber);
                    this.accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
                }

                return true;
            }
        }
        return false;
    }

    private void letterCounterPartRecords(AccountingRecord record, AccountingRecord counterPart)
            throws OsirisException {

        Integer maxLetteringNumber = accountingRecordService
                .findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                        .with(ChronoField.HOUR_OF_DAY, 0)
                        .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

        if (maxLetteringNumber == null)
            maxLetteringNumber = 0;
        maxLetteringNumber++;

        record.setLetteringDateTime(LocalDateTime.now());
        record.setLetteringNumber(maxLetteringNumber);
        counterPart.setLetteringDateTime(LocalDateTime.now());
        counterPart.setLetteringNumber(maxLetteringNumber);
        this.accountingRecordService.addOrUpdateAccountingRecord(record, true);
        this.accountingRecordService.addOrUpdateAccountingRecord(counterPart, true);
    }

    private void unletterInvoiceEmitted(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccountCustomer = invoice.getResponsable().getTiers()
                .getAccountingAccountCustomer();

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndInvoice(accountingAccountCustomer, invoice);

        if (accountingRecords != null)
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecord.setLetteringDateTime(null);
                accountingRecord.setLetteringNumber(null);
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
            }
        invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());
        if (invoice.getCustomerOrder() != null) {
            invoice.getCustomerOrder().setIsPayed(false);
            customerOrderService.addOrUpdateCustomerOrder(invoice.getCustomerOrder(), false, false);
        }
        invoiceService.addOrUpdateInvoice(invoice);
    }

    private void unletterInvoiceReceived(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccountProvider = invoice.getProvider().getAccountingAccountProvider();

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndInvoice(accountingAccountProvider, invoice);

        if (accountingRecords != null)
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecord.setLetteringDateTime(null);
                accountingRecord.setLetteringNumber(null);
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
            }
        if (invoice.getIsCreditNote())
            invoice.setInvoiceStatus(constantService.getInvoiceStatusCreditNoteReceived());
        else
            invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
        invoiceService.addOrUpdateInvoice(invoice);
    }

    private LocalDateTime getInvoiceOperationDateTime(Invoice invoice) {
        if (invoice.getManualAccountingDocumentDate() != null)
            return invoice.getManualAccountingDocumentDate().atTime(12, 00);
        if (invoice.getCreatedDate() != null)
            return invoice.getCreatedDate();
        return LocalDateTime.now();
    }

    @Override
    public void generateAccountingRecordsOnInvoiceEmission(Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal salesJournal = constantService.getAccountingJournalSales();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (invoice.getCustomerOrder() == null && invoice.getResponsable() == null)
            throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

        String labelPrefix = invoice.getCustomerOrder() != null
                ? ("Facture pour la commande n°" + invoice.getCustomerOrder().getId())
                : ("Facture libre n°" + invoice.getId());

        labelPrefix += " - " + ((invoice.getResponsable().getTiers().getDenomination() != null)
                ? invoice.getResponsable().getTiers().getDenomination()
                : (invoice.getResponsable().getTiers().getFirstname() + " "
                        + invoice.getResponsable().getTiers().getLastname()));

        AccountingAccount accountingAccountCustomer = invoice.getResponsable().getTiers()
                .getAccountingAccountCustomer();
        BigDecimal balance = new BigDecimal(0);
        Integer operationId = getNewTemporaryOperationId();

        // One write on customer account for all invoice
        generateNewAccountingRecord(getInvoiceOperationDateTime(invoice),
                operationId, invoice.getManualAccountingDocumentNumber(),
                invoice.getManualAccountingDocumentDate(), labelPrefix, null,
                invoiceHelper.getPriceTotal(invoice),
                accountingAccountCustomer, null, invoice, null, salesJournal, null, null, null);

        balance = balance.add(invoiceHelper.getPriceTotal(invoice));

        // For each invoice item, one write on product and VAT account for each invoice
        // item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getIsGifted() != null && invoiceItem.getIsGifted())
                continue;

            if (invoiceItem.getBillingItem() == null)
                throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

            if (invoiceItem.getBillingItem().getBillingType() == null)
                throw new OsirisException(null,
                        "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

            if (invoiceItem.getPreTaxPrice() == null)
                invoiceItem.setPreTaxPrice(zeroValue);

            AccountingAccount producAccountingAccount = invoiceItem.getBillingItem().getBillingType()
                    .getAccountingAccountProduct();

            if (producAccountingAccount == null)
                throw new OsirisException(null, "No product accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            BigDecimal billingItemPrice = invoiceItem.getPreTaxPrice().subtract(
                    invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : zeroValue);

            balance = balance.subtract(billingItemPrice);

            if (invoiceItem.getOriginProviderInvoice() != null
                    && invoiceItem.getOriginProviderInvoice().getIsCreditNote()
                    && invoiceItem.getPreTaxPrice().compareTo(zeroValue) < 0) {
                generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                        invoice.getManualAccountingDocumentNumber(),
                        invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - produit " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        null, billingItemPrice.abs(),
                        producAccountingAccount, invoiceItem, invoice, null, salesJournal, null, null, null);

                if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                        && invoiceItem.getVatPrice().compareTo(zeroValue) != 0) {
                    generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                            invoice.getManualAccountingDocumentNumber(),
                            invoice.getManualAccountingDocumentDate(),
                            labelPrefix + " - TVA pour le produit "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel(),
                            null, invoiceItem.getVatPrice().abs(), invoiceItem.getVat().getAccountingAccount(),
                            invoiceItem,
                            invoice, null,
                            salesJournal, null, null, null);

                    balance = balance.subtract(invoiceItem.getVatPrice());
                }
            } else if (billingItemPrice.compareTo(zeroValue) != 0) {
                generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                        invoice.getManualAccountingDocumentNumber(),
                        invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - produit " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        billingItemPrice,
                        null, producAccountingAccount, invoiceItem, invoice, null, salesJournal, null, null, null);

                if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                        && invoiceItem.getVatPrice().compareTo(zeroValue) > 0) {
                    generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                            invoice.getManualAccountingDocumentNumber(),
                            invoice.getManualAccountingDocumentDate(),
                            labelPrefix + " - TVA pour le produit "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel(),
                            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(), invoiceItem,
                            invoice, null,
                            salesJournal, null, null, null);

                    balance = balance.subtract(invoiceItem.getVatPrice());
                }
            }
        }

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsOnInvoiceEmissionCancellation(Invoice invoice, Invoice creditNote)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        unletterInvoiceEmitted(invoice);

        AccountingJournal salesJournal = constantService.getAccountingJournalSales();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (invoice.getCustomerOrder() == null && invoice.getResponsable() == null)
            throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

        String labelPrefix = invoice.getCustomerOrder() != null
                ? ("Avoir n°" + creditNote.getId() + " - Commande n°" + invoice.getCustomerOrder().getId())
                : ("Avoir n°" + creditNote.getId() + " - Facture libre n°" + invoice.getId());

        labelPrefix += " - " + ((invoice.getResponsable().getTiers().getDenomination() != null)
                ? invoice.getResponsable().getTiers().getDenomination()
                : (invoice.getResponsable().getTiers().getFirstname() + " "
                        + invoice.getResponsable().getTiers().getLastname()));

        BigDecimal balance = new BigDecimal(0);
        Integer operationId = getNewTemporaryOperationId();

        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                if (accountingRecord.getAccountingJournal().getId()
                        .equals(constantService.getAccountingJournalSales().getId())) {
                    AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, salesJournal,
                            labelPrefix + " - " + accountingRecord.getLabel(), null);
                    accountingRecord.setContrePasse(counterPart);
                    accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
                    letterCounterPartRecords(accountingRecord, counterPart);

                    if (counterPart.getCreditAmount() != null)
                        balance = balance.add(counterPart.getCreditAmount());
                    else
                        balance = balance.subtract(counterPart.getDebitAmount());
                }
            }
        else {
            throw new OsirisException("no records found");
        }
        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsOnCreditNoteReception(Invoice invoice, Invoice originalInvoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        String labelPrefix = "Avoir n°" + invoice.getId();
        if (originalInvoice != null)
            labelPrefix += " pour la facture " + originalInvoice.getId();

        labelPrefix += " - " + invoice.getProvider().getLabel();

        AccountingAccount accountingAccountProvider = invoice.getProvider().getAccountingAccountProvider();

        BigDecimal balance = new BigDecimal(0);
        Integer operationId = getNewTemporaryOperationId();

        // One write on customer account for all invoice
        generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                invoice.getManualAccountingDocumentNumber(),
                invoice.getManualAccountingDocumentDate(),
                labelPrefix, null,
                invoiceHelper.getPriceTotal(invoice),
                accountingAccountProvider, null, invoice, null, pushasingJournal, null, null, null);

        balance = balance.add(invoiceHelper.getPriceTotal(invoice));

        // For each invoice item, one write on product and VAT account for each invoice
        // item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getBillingItem() == null)
                throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

            if (invoiceItem.getBillingItem().getBillingType() == null)
                throw new OsirisException(null,
                        "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

            if (invoiceItem.getPreTaxPrice() == null)
                invoiceItem.setPreTaxPrice(zeroValue);

            AccountingAccount chargeAccountingAccount = invoiceItem.getBillingItem().getBillingType()
                    .getAccountingAccountCharge();

            if (chargeAccountingAccount == null)
                throw new OsirisException(null, "No charge accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            BigDecimal billingItemPrice = invoiceItem.getPreTaxPrice().subtract(
                    invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : zeroValue);

            generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                    invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    billingItemPrice, null,
                    chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null, null);

            balance = balance.subtract(billingItemPrice);

            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                    && invoiceItem.getVatPrice().compareTo(zeroValue) > 0) {
                generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                        invoice.getManualAccountingDocumentNumber(),
                        invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - TVA pour la charge "
                                + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(), invoiceItem,
                        invoice, null,
                        pushasingJournal, null, null, null);

                balance = balance.subtract(invoiceItem.getVatPrice());
            }
        }

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsOnInvoiceReception(Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
                : ("Facture libre n°" + invoice.getId());

        if (invoice.getRff() != null)
            labelPrefix += " - " + invoice.getResponsable().getTiers().getDenomination();

        else
            labelPrefix += " - " + invoice.getProvider().getLabel();

        AccountingAccount accountingAccountProvider = null;
        if (invoice.getRff() != null)
            accountingAccountProvider = invoice.getResponsable().getTiers().getAccountingAccountCustomer();
        else
            accountingAccountProvider = invoice.getProvider().getAccountingAccountProvider();

        BigDecimal balance = new BigDecimal(0);
        balance = balance.add(invoiceHelper.getPriceTotal(invoice));
        Integer operationId = getNewTemporaryOperationId();

        // One write on provider account for all invoice
        generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                invoice.getManualAccountingDocumentNumber(),
                invoice.getManualAccountingDocumentDate(),
                labelPrefix, balance, null, accountingAccountProvider, null, invoice, null, pushasingJournal, null,
                null, null);

        // For each invoice item, one write on product and VAT account for each invoice
        // item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getBillingItem() == null)
                throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

            if (invoiceItem.getBillingItem().getBillingType() == null)
                throw new OsirisException(null,
                        "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

            AccountingAccount chargeAccountingAccount = invoiceItem.getBillingItem().getBillingType()
                    .getAccountingAccountCharge();

            if (chargeAccountingAccount == null)
                throw new OsirisException(null, "No charge accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            BigDecimal billingItemPrice = invoiceItem.getPreTaxPrice().subtract(
                    invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : zeroValue);

            generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                    invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(), null,
                    billingItemPrice,
                    chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null, null);

            balance = balance.subtract(billingItemPrice);

            if (invoiceItem.getVat() != null) {

                if (invoiceItem.getVat().getAccountingAccount() == null)
                    throw new OsirisException(null, "No accounting account for VAT " + invoiceItem.getVat().getLabel());

                if (invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice().compareTo(zeroValue) > 0)
                    generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                            invoice.getManualAccountingDocumentNumber(),
                            invoice.getManualAccountingDocumentDate(),
                            labelPrefix + " - TVA pour la charge "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel(),
                            null,
                            invoiceItem.getVatPrice(), invoiceItem.getVat().getAccountingAccount(), invoiceItem,
                            invoice, null,
                            pushasingJournal, null, null, null);

                balance = balance.subtract(invoiceItem.getVatPrice());
            }
        }

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsOnInvoiceReceptionCancellation(Invoice invoice)
            throws OsirisException, OsirisValidationException {
        unletterInvoiceReceived(invoice);

        AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        String labelPrefix = "";
        if (invoice.getCustomerOrder() != null)
            labelPrefix = "Annulation - Commande n°" + invoice.getCustomerOrder().getId();
        else if (invoice.getIsCreditNote())
            labelPrefix = "Annulation - Avoir n°" + invoice.getId();
        else
            labelPrefix = "Annulation - Facture libre n°" + invoice.getId();

        labelPrefix += " - " + invoice.getProvider().getLabel();

        BigDecimal balance = new BigDecimal(0);
        Integer operationId = getNewTemporaryOperationId();

        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                if (accountingRecord.getCreditAmount() != null)
                    balance = balance.subtract(accountingRecord.getCreditAmount());
                else
                    balance = balance.add(accountingRecord.getDebitAmount());

                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, pushasingJournal,
                        labelPrefix, null);
                accountingRecord.setContrePasse(counterPart);
                accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
                letterCounterPartRecords(accountingRecord, counterPart);
            }

        checkBalance(balance);
        letterInvoice(invoice);
    }

    private String getPaymentOriginLabel(Payment payment) {
        if (payment.getOriginPayment() == null)
            return "";
        return " depuis le paiement n°" + payment.getOriginPayment().getId();
    }

    @Override
    public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment, boolean isOdJournal,
            boolean isOriginalPayment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal bankJournal = !isOdJournal ? constantService.getAccountingJournalBank()
                : constantService.getAccountingJournalMiscellaneousOperations();
        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            bankJournal = constantService.getAccountingJournalCash();

        if (payment.getPaymentAmount().compareTo(zeroValue) < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(getPaymentDateForAccounting(payment, isOriginalPayment), operationId,
                        null, null,
                        "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                        null,
                        payment.getPaymentAmount(),
                        payment.getSourceAccountingAccount(), null, null, null, bankJournal, payment, null, null));

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(getPaymentDateForAccounting(payment, isOriginalPayment), operationId,
                        null, null,
                        "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                        payment.getPaymentAmount(), null,
                        payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, null, null));
    }

    @Override
    public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment, Boolean isOdJournal,
            boolean isOriginalPayment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal bankJournal = payment.getPaymentType().getId()
                .equals(constantService.getPaymentTypeEspeces().getId()) ? constantService.getAccountingJournalCash()
                        : constantService.getAccountingJournalBank();

        if (isOdJournal)
            bankJournal = this.constantService.getAccountingJournalMiscellaneousOperations();

        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            bankJournal = constantService.getAccountingJournalCash();

        if (payment.getPaymentAmount().compareTo(zeroValue) > 0)
            throw new OsirisException(null, "Outgoing payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(getPaymentDateForAccounting(payment, isOriginalPayment), operationId,
                        null, null,
                        "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                        payment.getPaymentAmount().abs(), null, payment.getSourceAccountingAccount(), null, null,
                        null,
                        bankJournal, payment, payment.getRefund(), payment.getBankTransfert()));

        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(), null,
                payment.getPaymentAmount().abs(),
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, payment.getRefund(),
                payment.getBankTransfert()));
    }

    @Override
    public void generateAccountingRecordOnPaymentCancellation(Payment payment)
            throws OsirisException, OsirisValidationException {
        AccountingJournal bankJournal = constantService.getAccountingJournalMiscellaneousOperations();

        if (payment.getAccountingRecords() == null || payment.getAccountingRecords().size() == 0)
            throw new OsirisException(null, "No accounting record for payment n°" + payment.getId());

        if (payment.getInvoice() != null && payment.getInvoice().getInvoiceStatus().getId()
                .equals(constantService.getInvoiceStatusPayed().getId())) {
            if (payment.getInvoice().getProvider() != null)
                unletterInvoiceReceived(payment.getInvoice());
            else if (payment.getInvoice().getResponsable() != null && !payment.getInvoice().getIsCreditNote())
                unletterInvoiceEmitted(payment.getInvoice());
        }

        Integer operationId = getNewTemporaryOperationId();
        BigDecimal balance = new BigDecimal(0);

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        ArrayList<AccountingRecord> newAccountingRecords = new ArrayList<AccountingRecord>();
        if (getPaymentDateForAccounting(payment, true).getYear() == payment.getPaymentDate().getYear()) {
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (accountingRecord.getCreditAmount() != null)
                    balance = balance.subtract(accountingRecord.getCreditAmount());
                else
                    balance = balance.add(accountingRecord.getDebitAmount());

                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, bankJournal,
                        "Annulation du paiement " + payment.getId(), null);

                newAccountingRecords.add(counterPart);
                accountingRecord.setContrePasse(counterPart);
                accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
                letterCounterPartRecords(accountingRecord, counterPart);
            }
        } else {
            // It's a closed payment
            // Grab former bank accounting record to generate counter part

            // Customer or provider part
            AccountingRecord accountingRecord = null;

            for (AccountingRecord record : payment.getAccountingRecords()) {
                if (!record.getAccountingAccount().getPrincipalAccountingAccount().getId()
                        .equals(constantService.getPrincipalAccountingAccountBank().getId())
                        && !record.getAccountingAccount().getId()
                                .equals(constantService.getAccountingAccountCaisse().getId())) {
                    accountingRecord = record;
                }
            }

            if (accountingRecord == null)
                throw new OsirisClientMessageException("accounting records not found");

            if (accountingRecord.getCreditAmount() != null)
                balance = balance.subtract(accountingRecord.getCreditAmount());
            else
                balance = balance.add(accountingRecord.getDebitAmount());

            AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, bankJournal,
                    "Annulation du paiement " + payment.getId(), null);

            newAccountingRecords.add(counterPart);
            accountingRecord.setContrePasse(counterPart);
            accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
            letterCounterPartRecords(accountingRecord, counterPart);

            // Bank part
            List<AccountingRecord> accountingRecords = accountingRecordService
                    .getClosedAccountingRecordsForPayment(payment);
            accountingRecord = null;
            for (AccountingRecord record : accountingRecords) {
                if (record.getAccountingAccount().getPrincipalAccountingAccount().getId()
                        .equals(constantService.getPrincipalAccountingAccountBank().getId())
                        || record.getAccountingAccount().getId()
                                .equals(constantService.getAccountingAccountCaisse().getId())) {
                    accountingRecord = record;
                }
            }

            // Account closure not done
            if (accountingRecord == null) {
                for (AccountingRecord record : payment.getAccountingRecords()) {
                    if (record.getAccountingAccount().getPrincipalAccountingAccount().getId()
                            .equals(constantService.getPrincipalAccountingAccountBank().getId())
                            || record.getAccountingAccount().getId()
                                    .equals(constantService.getAccountingAccountCaisse().getId())) {
                        accountingRecord = record;
                    }
                }
            }

            if (accountingRecord != null)
                if (accountingRecord.getCreditAmount() != null)
                    balance = balance.subtract(accountingRecord.getCreditAmount());
                else
                    balance = balance.add(accountingRecord.getDebitAmount());

            counterPart = getCounterPart(accountingRecord, operationId, bankJournal,
                    "Annulation du paiement " + payment.getId(), null);

            newAccountingRecords.add(counterPart);
            accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
        }

        if (newAccountingRecords.size() > 0)
            payment.getAccountingRecords().addAll(newAccountingRecords);

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment,
            boolean isOriginalPayment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount().compareTo(zeroValue) < 0 && !payment.getIsAppoint())
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();
        String isPaymentOrAppoint = payment.getIsAppoint() ? "Appoint" : "Paiement";

        // One write on customer order deposit account to equilibrate
        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(getPaymentDateForAccounting(payment, isOriginalPayment), operationId,
                        null, null,
                        isPaymentOrAppoint + " n°" + payment.getId() + getPaymentOriginLabel(payment)
                                + " - Paiement pour la facture n°" + invoice.getId()
                                + " - "
                                + payment.getLabel(),
                        null, payment.getPaymentAmount().abs(),
                        payment.getSourceAccountingAccount(), null, invoice, null, journal, payment, null, null));

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(getPaymentDateForAccounting(payment, isOriginalPayment), operationId,
                        null, null,
                        isPaymentOrAppoint + " n°" + payment.getId() + getPaymentOriginLabel(payment)
                                + " - Paiement pour la facture n°" + invoice.getId(),
                        payment.getPaymentAmount().abs(), null,
                        payment.getTargetAccountingAccount(), null, invoice, null, journal, payment, null, null));

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForSaleOnCustomerOrderDeposit(CustomerOrder customerOrder, Payment payment,
            boolean isOriginalPayment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (customerOrder == null)
            throw new OsirisException(null, "No customerOrder provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with customer order " + customerOrder.getId());

        if (payment.getPaymentAmount().compareTo(zeroValue) < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Acompte pour la commande n°"
                        + customerOrder.getId() + " - "
                        + payment.getLabel(),
                null, payment.getPaymentAmount(),
                payment.getSourceAccountingAccount(), null, null, null, journal, payment, null, null));

        // One write on customer order deposit account to equilibrate
        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Acompte pour la commande n°"
                        + customerOrder.getId(),
                payment.getPaymentAmount(), null,
                payment.getTargetAccountingAccount(), null, null, customerOrder, journal, payment, null, null));
    }

    @Override
    public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment,
            boolean isOriginalPayment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount().compareTo(zeroValue) < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Remboursement de la facture n°"
                        + invoice.getId() + " - "
                        + payment.getLabel(),
                payment.getPaymentAmount(), null,
                payment.getTargetAccountingAccount(), null, invoice, null, journal, payment, null, null));

        // One write on provider account to equilibrate
        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Remboursement de la facture n°"
                        + invoice.getId(),
                null, payment.getPaymentAmount(),
                payment.getSourceAccountingAccount(), null, invoice, null, journal, payment, null, null));

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForPurschaseOnInvoicePayment(Invoice invoice, Payment payment,
            boolean isOriginalPayment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount().compareTo(zeroValue) > 0)
            throw new OsirisException(null, "Outgoin payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        AccountingJournal journal = constantService.getAccountingJournalBank();
        // if accounting payment, use OD journal
        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeAccount().getId()))
            journal = constantService.getAccountingJournalMiscellaneousOperations();
        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Paiement pour la facture n°"
                        + invoice.getId()
                        + " - "
                        + payment.getLabel(),
                null, payment.getPaymentAmount().negate(),
                payment.getSourceAccountingAccount(), null, invoice, null, journal, payment, null, null));

        // One write on customer order deposit account to equilibrate
        payment.getAccountingRecords().add(generateNewAccountingRecord(
                getPaymentDateForAccounting(payment, isOriginalPayment),
                operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Paiement pour la facture n°"
                        + invoice.getId(),
                payment.getPaymentAmount().negate(), null,
                payment.getTargetAccountingAccount(), null, invoice, null, journal, payment, null, null));

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForRefundGeneration(Refund refund)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        if (refund.getPayments() == null || refund.getPayments().size() != 1)
            throw new OsirisException(null, "Impossible to find refund payment");
        Payment payment = refund.getPayments().get(0);
        AccountingJournal bankJournal = constantService.getAccountingJournalMiscellaneousOperations();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        AccountingAccount sourceAccountingAccount = payment.getSourceAccountingAccount();
        // /!\ Override it
        // to put debit amount in CentralPay account and not JSS Bank account
        // when payment to refund is a CB
        if (payment.getOriginPayment() != null && payment.getOriginPayment().getPaymentType().getId()
                .equals(constantService.getPaymentTypeCB().getId()))
            sourceAccountingAccount = constantService.getAccountingAccountBankCentralPay();

        generateNewAccountingRecord(getPaymentDateForAccounting(payment.getOriginPayment(), false), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, payment.getPaymentAmount().abs(), sourceAccountingAccount, null, null,
                null,
                bankJournal, payment, refund, null);

        generateNewAccountingRecord(getPaymentDateForAccounting(payment.getOriginPayment(), false), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                payment.getPaymentAmount().abs(), null,
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, refund, null);

        checkRefundForLettrage(refund);
    }

    @Override
    public void generateAccountingRecordsForRefundExport(Refund refund)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        if (refund.getPayments() == null || refund.getPayments().size() > 2)
            throw new OsirisException(null, "Impossible to find refund payment");
        Payment payment = refund.getPayments().get(0);
        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        generateNewAccountingRecord(getPaymentDateForAccounting(payment, false), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, payment.getPaymentAmount().abs(), payment.getTargetAccountingAccount(), null, null,
                null,
                bankJournal, payment, refund, null);

        generateNewAccountingRecord(getPaymentDateForAccounting(payment, false), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                payment.getPaymentAmount().abs(), null,
                payment.getSourceAccountingAccount(), null, null, null, bankJournal, payment, refund, null);

        checkRefundForLettrage(refund);
    }

    @Override
    public void generateAccountingRecordOnIncomingPaymentOnAccountingAccount(Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        generateNewAccountingRecord(getPaymentDateForAccounting(payment, false), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, payment.getPaymentAmount().abs(), payment.getSourceAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

        generateNewAccountingRecord(getPaymentDateForAccounting(payment, false), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                payment.getPaymentAmount().abs(), null, payment.getTargetAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

    }

    @Override
    public void generateAccountingRecordOnOutgoingPaymentOnAccountingAccount(Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        generateNewAccountingRecord(getPaymentDateForAccounting(payment, false), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, payment.getPaymentAmount().abs(), payment.getTargetAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

        generateNewAccountingRecord(getPaymentDateForAccounting(payment, false), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                payment.getPaymentAmount().abs(), null, payment.getSourceAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

    }

    @Override
    public LocalDateTime getPaymentDateForAccounting(Payment payment, boolean isOriginalPayment)
            throws OsirisException {
        if (!isOriginalPayment)
            return LocalDateTime.now();

        LocalDateTime closedDate = constantService.getDateAccountingClosureForAll().atTime(12, 0);
        if (payment.getPaymentDate().isBefore(closedDate))
            return closedDate;
        return payment.getPaymentDate();
    }

    @Override
    public void generateAccountingRecordForSageRecord(List<SageRecord> sageRecords) throws OsirisException {
        Integer operationId = getNewTemporaryOperationId();
        AccountingJournal salaryJournal = constantService.getAccountingJournalSalary();
        List<AccountingAccount> targetAccountingAccount = new ArrayList<AccountingAccount>();
        BigDecimal balance = new BigDecimal(0);

        if (sageRecords != null && !sageRecords.isEmpty()) {
            for (SageRecord sageRecord : sageRecords) {
                targetAccountingAccount = accountingAccountService
                        .getAccountingAccountByLabelOrCode(sageRecord.getTargetAccountingAccountCode());
                if (targetAccountingAccount == null || targetAccountingAccount.isEmpty())
                    throw new OsirisException(null,
                            "Invalid target accounting account provided for sage record ");

                if (sageRecord.getCreditAmount() != null) {
                    generateNewAccountingRecord(sageRecord.getOperationDate().atTime(0, 0), operationId, null, null,
                            "OD paie - " + sageRecord.getOperationDate() + " - " + sageRecord.getLabel(),
                            sageRecord.getCreditAmount().abs(), null, targetAccountingAccount.get(0), null, null,
                            null,
                            salaryJournal, null, null, null);
                    balance = balance.add(sageRecord.getCreditAmount());
                }
                if (sageRecord.getDebitAmount() != null) {
                    generateNewAccountingRecord(sageRecord.getOperationDate().atTime(0, 0), operationId, null, null,
                            "OD paie - " + sageRecord.getOperationDate() + " - " + sageRecord.getLabel(),
                            null, sageRecord.getDebitAmount(), targetAccountingAccount.get(0), null, null,
                            null,
                            salaryJournal, null, null, null);
                    balance = balance.subtract(sageRecord.getDebitAmount());
                }
            }
            checkBalance(balance);
        }
    }

}
