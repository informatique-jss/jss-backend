package com.jss.osiris.modules.osiris.accounting.service;

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
import com.jss.osiris.modules.osiris.quotation.service.BankTransfertService;

@Service
public class AccountingRecordGenerationServiceImpl implements AccountingRecordGenerationService {

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
    BankTransfertService bankTransfertService;

    @Autowired
    AccountingRecordService accountingRecordService;

    private Integer getNewTemporaryOperationId() {
        return ThreadLocalRandom.current().nextInt(1, 1000000000);
    }

    private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
            String manualAccountingDocumentNumber, LocalDate manualAccountingDocumentDate, String label,
            Float creditAmount,
            Float debitAmount, AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice,
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

        if (accountingRecord.getCreditAmount() != null && accountingRecord.getCreditAmount() < 0
                || accountingRecord.getDebitAmount() != null && accountingRecord.getDebitAmount() < 0)
            throw new OsirisClientMessageException("Negative debit or credit !");
        return accountingRecord;
    }

    private AccountingRecord getCounterPart(AccountingRecord originalAccountingRecord, Integer operationId,
            AccountingJournal journal, String label) throws OsirisException {
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
        newAccountingRecord.setOperationDateTime(originalAccountingRecord.getOperationDateTime());
        newAccountingRecord.setCustomerOrder(originalAccountingRecord.getCustomerOrder());
        newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
        newAccountingRecord.setIsCounterPart(true);
        return accountingRecordService.addOrUpdateAccountingRecord(newAccountingRecord, false);
    }

    private void checkBalance(Float balance) throws OsirisValidationException {
        if (Math.abs(Math.round(balance * 100f)) > 1)
            throw new OsirisValidationException("Balance not null");
    }

    public void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
        if (letterInvoice(invoice)) {
            invoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
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

        Float balance = 0f;

        if (accountingRecords != null && accountingRecords.size() > 0) {
            for (AccountingRecord accountingRecord : accountingRecords) {
                if (accountingRecord.getDebitAmount() != null)
                    balance += accountingRecord.getDebitAmount();
                if (accountingRecord.getCreditAmount() != null)
                    balance -= accountingRecord.getCreditAmount();
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

        Float balance = 0f;

        if (accountingRecords != null && accountingRecords.size() > 0) {
            for (AccountingRecord accountingRecord : accountingRecords) {
                if (accountingRecord.getDebitAmount() != null)
                    balance += accountingRecord.getDebitAmount();
                if (accountingRecord.getCreditAmount() != null)
                    balance -= accountingRecord.getCreditAmount();
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
        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        // One write on customer account for all invoice
        generateNewAccountingRecord(getInvoiceOperationDateTime(invoice),
                operationId, invoice.getManualAccountingDocumentNumber(),
                invoice.getManualAccountingDocumentDate(), labelPrefix, null, invoiceHelper.getPriceTotal(invoice),
                accountingAccountCustomer, null, invoice, null, salesJournal, null, null, null);

        balance += invoiceHelper.getPriceTotal(invoice);

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
                invoiceItem.setPreTaxPrice(0f);

            AccountingAccount producAccountingAccount = invoiceItem.getBillingItem().getBillingType()
                    .getAccountingAccountProduct();

            if (producAccountingAccount == null)
                throw new OsirisException(null, "No product accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            Float billingItemPrice = invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

            balance -= billingItemPrice;

            if (invoiceItem.getOriginProviderInvoice() != null
                    && invoiceItem.getOriginProviderInvoice().getIsCreditNote()
                    && invoiceItem.getPreTaxPrice() < 0) {
                generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                        invoice.getManualAccountingDocumentNumber(),
                        invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - produit " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        null, Math.abs(billingItemPrice),
                        producAccountingAccount, invoiceItem, invoice, null, salesJournal, null, null, null);

                if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                        && invoiceItem.getVatPrice() != 0) {
                    generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                            invoice.getManualAccountingDocumentNumber(),
                            invoice.getManualAccountingDocumentDate(),
                            labelPrefix + " - TVA pour le produit "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel(),
                            null, Math.abs(invoiceItem.getVatPrice()), invoiceItem.getVat().getAccountingAccount(),
                            invoiceItem,
                            invoice, null,
                            salesJournal, null, null, null);

                    balance -= invoiceItem.getVatPrice();
                }
            } else {
                generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                        invoice.getManualAccountingDocumentNumber(),
                        invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - produit " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        billingItemPrice,
                        null, producAccountingAccount, invoiceItem, invoice, null, salesJournal, null, null, null);

                if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null
                        && invoiceItem.getVatPrice() > 0) {
                    generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                            invoice.getManualAccountingDocumentNumber(),
                            invoice.getManualAccountingDocumentDate(),
                            labelPrefix + " - TVA pour le produit "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel(),
                            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(), invoiceItem,
                            invoice, null,
                            salesJournal, null, null, null);

                    balance -= invoiceItem.getVatPrice();
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

        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                if (accountingRecord.getAccountingJournal().getId()
                        .equals(constantService.getAccountingJournalSales().getId())) {
                    AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, salesJournal,
                            labelPrefix + " - " + accountingRecord.getLabel());
                    accountingRecord.setContrePasse(counterPart);
                    accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
                    letterCounterPartRecords(accountingRecord, counterPart);

                    if (counterPart.getCreditAmount() != null)
                        balance += counterPart.getCreditAmount();
                    else
                        balance -= counterPart.getDebitAmount();
                }
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

        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        // One write on customer account for all invoice
        generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                invoice.getManualAccountingDocumentNumber(),
                invoice.getManualAccountingDocumentDate(),
                labelPrefix, null,
                invoiceHelper.getPriceTotal(invoice),
                accountingAccountProvider, null, invoice, null, pushasingJournal, null, null, null);

        balance += invoiceHelper.getPriceTotal(invoice);

        // For each invoice item, one write on product and VAT account for each invoice
        // item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getBillingItem() == null)
                throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

            if (invoiceItem.getBillingItem().getBillingType() == null)
                throw new OsirisException(null,
                        "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

            if (invoiceItem.getPreTaxPrice() == null)
                invoiceItem.setPreTaxPrice(0f);

            AccountingAccount chargeAccountingAccount = invoiceItem.getBillingItem().getBillingType()
                    .getAccountingAccountCharge();

            if (chargeAccountingAccount == null)
                throw new OsirisException(null, "No charge accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            Float billingItemPrice = invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

            generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                    invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    billingItemPrice, null,
                    chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null, null);

            balance -= billingItemPrice;

            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
                generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                        invoice.getManualAccountingDocumentNumber(),
                        invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - TVA pour la charge "
                                + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(), invoiceItem,
                        invoice, null,
                        pushasingJournal, null, null, null);

                balance -= invoiceItem.getVatPrice();
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

        labelPrefix += " - " + invoice.getProvider().getLabel();

        AccountingAccount accountingAccountProvider = null;
        if (invoice.getRff() != null)
            accountingAccountProvider = invoice.getResponsable().getTiers().getAccountingAccountCustomer();
        accountingAccountProvider = invoice.getProvider().getAccountingAccountProvider();

        Float balance = 0f;
        balance += invoiceHelper.getPriceTotal(invoice);
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

            Float billingItemPrice = invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

            generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                    invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(), null,
                    billingItemPrice,
                    chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null, null);

            balance -= billingItemPrice;

            if (invoiceItem.getVat() != null) {

                if (invoiceItem.getVat().getAccountingAccount() == null)
                    throw new OsirisException(null, "No accounting account for VAT " + invoiceItem.getVat().getLabel());

                if (invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0)
                    generateNewAccountingRecord(getInvoiceOperationDateTime(invoice), operationId,
                            invoice.getManualAccountingDocumentNumber(),
                            invoice.getManualAccountingDocumentDate(),
                            labelPrefix + " - TVA pour la charge "
                                    + invoiceItem.getBillingItem().getBillingType().getLabel(),
                            null,
                            invoiceItem.getVatPrice(), invoiceItem.getVat().getAccountingAccount(), invoiceItem,
                            invoice, null,
                            pushasingJournal, null, null, null);

                balance -= invoiceItem.getVatPrice();
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

        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                if (accountingRecord.getCreditAmount() != null)
                    balance -= accountingRecord.getCreditAmount();
                else
                    balance += accountingRecord.getDebitAmount();

                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, pushasingJournal,
                        labelPrefix);
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
    public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment, boolean isOdJournal)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal bankJournal = !isOdJournal ? constantService.getAccountingJournalBank()
                : constantService.getAccountingJournalMiscellaneousOperations();
        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            bankJournal = constantService.getAccountingJournalCash();

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                        "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                        null,
                        payment.getPaymentAmount(),
                        payment.getSourceAccountingAccount(), null, null, null, bankJournal, payment, null, null));

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                        "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                        payment.getPaymentAmount(), null,
                        payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, null, null));
    }

    @Override
    public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment, Boolean isOdJournal)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal bankJournal = payment.getPaymentType().getId()
                .equals(constantService.getPaymentTypeEspeces().getId()) ? constantService.getAccountingJournalCash()
                        : constantService.getAccountingJournalBank();

        if (isOdJournal)
            bankJournal = this.constantService.getAccountingJournalMiscellaneousOperations();

        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            bankJournal = constantService.getAccountingJournalCash();

        if (payment.getPaymentAmount() > 0)
            throw new OsirisException(null, "Outgoing payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                        "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                        Math.abs(payment.getPaymentAmount()), null, payment.getSourceAccountingAccount(), null, null,
                        null,
                        bankJournal, payment, payment.getRefund(), payment.getBankTransfert()));

        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(), null,
                Math.abs(payment.getPaymentAmount()),
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
        Float balance = 0f;

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        ArrayList<AccountingRecord> newAccountingRecords = new ArrayList<AccountingRecord>();
        for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
            if (accountingRecord.getCreditAmount() != null)
                balance -= accountingRecord.getCreditAmount();
            else
                balance += accountingRecord.getDebitAmount();

            AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, bankJournal,
                    "Annulation du paiement " + payment.getId());

            newAccountingRecords.add(counterPart);
            accountingRecord.setContrePasse(counterPart);
            accountingRecordService.addOrUpdateAccountingRecord(counterPart, false);
            letterCounterPartRecords(accountingRecord, counterPart);
        }

        if (newAccountingRecords.size() > 0)
            payment.getAccountingRecords().addAll(newAccountingRecords);

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount() < 0 && !payment.getIsAppoint())
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
                .add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                        isPaymentOrAppoint + " n°" + payment.getId() + getPaymentOriginLabel(payment)
                                + " - Paiement pour la facture n°" + invoice.getId()
                                + " - "
                                + payment.getLabel(),
                        null, Math.abs(payment.getPaymentAmount()),
                        payment.getSourceAccountingAccount(), null, invoice, null, journal, payment, null, null));

        payment.getAccountingRecords()
                .add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                        isPaymentOrAppoint + " n°" + payment.getId() + getPaymentOriginLabel(payment)
                                + " - Paiement pour la facture n°" + invoice.getId(),
                        Math.abs(payment.getPaymentAmount()), null,
                        payment.getTargetAccountingAccount(), null, invoice, null, journal, payment, null, null));

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForSaleOnCustomerOrderDeposit(CustomerOrder customerOrder, Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (customerOrder == null)
            throw new OsirisException(null, "No customerOrder provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with customer order " + customerOrder.getId());

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Acompte pour la commande n°"
                        + customerOrder.getId() + " - "
                        + payment.getLabel(),
                null, payment.getPaymentAmount(),
                payment.getSourceAccountingAccount(), null, null, null, journal, payment, null, null));

        // One write on customer order deposit account to equilibrate
        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Acompte pour la commande n°"
                        + customerOrder.getId(),
                payment.getPaymentAmount(), null,
                payment.getTargetAccountingAccount(), null, null, customerOrder, journal, payment, null, null));
    }

    @Override
    public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        if (payment.getAccountingRecords() == null)
            payment.setAccountingRecords(new ArrayList<AccountingRecord>());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();

        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Remboursement de la facture n°"
                        + invoice.getId() + " - "
                        + payment.getLabel(),
                payment.getPaymentAmount(), null,
                payment.getTargetAccountingAccount(), null, invoice, null, journal, payment, null, null));

        // One write on provider account to equilibrate
        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Remboursement de la facture n°"
                        + invoice.getId(),
                null, payment.getPaymentAmount(),
                payment.getSourceAccountingAccount(), null, invoice, null, journal, payment, null, null));

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForPurschaseOnInvoicePayment(Invoice invoice, Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount() > 0)
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

        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Paiement pour la facture n°"
                        + invoice.getId()
                        + " - "
                        + payment.getLabel(),
                null, -payment.getPaymentAmount(),
                payment.getSourceAccountingAccount(), null, invoice, null, journal, payment, null, null));

        // One write on customer order deposit account to equilibrate
        payment.getAccountingRecords().add(generateNewAccountingRecord(payment.getPaymentDate(), operationId, null,
                null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - Paiement pour la facture n°"
                        + invoice.getId(),
                -payment.getPaymentAmount(), null,
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

        generateNewAccountingRecord(payment.getOriginPayment().getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, Math.abs(payment.getPaymentAmount()), sourceAccountingAccount, null, null,
                null,
                bankJournal, payment, refund, null);

        generateNewAccountingRecord(payment.getOriginPayment().getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null,
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, refund, null);

        checkRefundForLettrage(refund);
    }

    @Override
    public void generateAccountingRecordsForRefundExport(Refund refund)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        if (refund.getPayments() == null || refund.getPayments().size() != 1)
            throw new OsirisException(null, "Impossible to find refund payment");
        Payment payment = refund.getPayments().get(0);
        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, Math.abs(payment.getPaymentAmount()), payment.getTargetAccountingAccount(), null, null,
                null,
                bankJournal, payment, refund, null);

        generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null,
                payment.getSourceAccountingAccount(), null, null, null, bankJournal, payment, refund, null);

        checkRefundForLettrage(refund);
    }

    @Override
    public void generateAccountingRecordOnIncomingPaymentOnDepositCompetentAuthorityAccount(Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, Math.abs(payment.getPaymentAmount()), payment.getSourceAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

        generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null, payment.getTargetAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

    }

    @Override
    public void generateAccountingRecordOnOutgoingPaymentOnDepositCompetentAuthorityAccount(Payment payment)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {

        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        if (payment.getSourceAccountingAccount() == null)
            throw new OsirisException(null, "No source accounting account for payment n°" + payment.getId());

        generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                null, Math.abs(payment.getPaymentAmount()), payment.getTargetAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

        generateNewAccountingRecord(payment.getPaymentDate(), operationId, null, null,
                "Paiement n°" + payment.getId() + getPaymentOriginLabel(payment) + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null, payment.getSourceAccountingAccount(), null, null, null,
                bankJournal, payment, null, null);

    }
}
