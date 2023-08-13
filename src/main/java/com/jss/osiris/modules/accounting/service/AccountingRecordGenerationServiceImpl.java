package com.jss.osiris.modules.accounting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.RefundService;
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.tiers.model.Responsable;

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
            BankTransfert bankTransfert) {
        AccountingRecord accountingRecord = new AccountingRecord();
        accountingRecord.setOperationDateTime(operationDatetime);
        accountingRecord.setTemporaryOperationId(operationId);
        accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
        accountingRecord.setManualAccountingDocumentDate(manualAccountingDocumentDate);
        accountingRecord.setLabel(label);
        accountingRecord.setCreditAmount(creditAmount);
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
        accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
        return accountingRecord;
    }

    private AccountingRecord getCounterPart(AccountingRecord originalAccountingRecord, Integer operationId,
            AccountingJournal journal, String label) {
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
        newAccountingRecord.setOperationDateTime(LocalDateTime.now());
        newAccountingRecord.setCustomerOrder(originalAccountingRecord.getCustomerOrder());
        newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
        newAccountingRecord.setIsCounterPart(true);
        return accountingRecordService.addOrUpdateAccountingRecord(newAccountingRecord);
    }

    private AccountingAccount getProviderAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccount = null;
        if (invoice.getProvider() != null)
            accountingAccount = invoice.getProvider().getAccountingAccountProvider();
        if (invoice.getCompetentAuthority() != null)
            accountingAccount = invoice.getCompetentAuthority().getAccountingAccountProvider();
        if (invoice.getConfrere() != null)
            accountingAccount = invoice.getConfrere().getAccountingAccountProvider();
        if (invoice.getTiers() != null)
            accountingAccount = invoice.getTiers().getAccountingAccountProvider();
        if (invoice.getResponsable() != null && invoice.getResponsable().getTiers() != null)
            accountingAccount = invoice.getResponsable().getTiers().getAccountingAccountProvider();
        if (accountingAccount == null)
            throw new OsirisException(null, "No customer accounting account in Provider ");

        return accountingAccount;
    }

    private AccountingAccount getCustomerAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
        IGenericTiers customerOrder = invoiceHelper.getCustomerOrder(invoice);
        AccountingAccount accountingAccountCustomer = null;
        if (customerOrder instanceof Responsable)
            accountingAccountCustomer = ((Responsable) customerOrder).getTiers().getAccountingAccountCustomer();
        else
            accountingAccountCustomer = customerOrder.getAccountingAccountCustomer();

        if (accountingAccountCustomer == null)
            throw new OsirisException(null, "No customer accounting account in ITiers " + customerOrder.getId());

        return accountingAccountCustomer;
    }

    private AccountingAccount getCustomerDepositAccountingAccountForCustomerOrder(CustomerOrder inCustomerOrder)
            throws OsirisException {
        IGenericTiers customerOrder = invoiceHelper.getCustomerOrder(inCustomerOrder);
        AccountingAccount accountingAccountCustomer = null;
        if (customerOrder instanceof Responsable)
            accountingAccountCustomer = ((Responsable) customerOrder).getTiers().getAccountingAccountDeposit();
        else
            accountingAccountCustomer = customerOrder.getAccountingAccountDeposit();

        if (accountingAccountCustomer == null)
            throw new OsirisException(null,
                    "No customer deposit accounting account in ITiers " + customerOrder.getId());

        return accountingAccountCustomer;
    }

    private void checkBalance(Float balance) throws OsirisValidationException {
        if (Math.round(balance * 100f) != 0)
            throw new OsirisValidationException("Balance not null");
    }

    private void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
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

        if (refund.getConfrere() != null)
            account = refund.getConfrere().getAccountingAccountCustomer();

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
                    this.accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
                }

            }
        }
    }

    private void checkBankTransfertForLettrage(BankTransfert bankTransfert) throws OsirisException {
        bankTransfert = bankTransfertService.getBankTransfert(bankTransfert.getId());

        if (bankTransfert.getInvoices() == null || bankTransfert.getInvoices().size() != 1)
            throw new OsirisException(null, "Impossible to find invoice of bank transfert " + bankTransfert.getId());

        IGenericTiers tiers = invoiceHelper.getCustomerOrder(bankTransfert.getInvoices().get(0));

        AccountingAccount account = tiers.getAccountingAccountProvider();

        if (account == null)
            throw new OsirisException(null, "Accounting account not found for refund");

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndBankTransfert(account, bankTransfert);

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
                    this.accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
                }

            }
        }
    }

    private boolean letterInvoice(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccount;
        invoice = invoiceService.getInvoice(invoice.getId());
        if (invoice.getIsInvoiceFromProvider() != null && invoice.getIsInvoiceFromProvider()
                || invoice.getIsProviderCreditNote() != null && invoice.getIsProviderCreditNote())
            accountingAccount = getProviderAccountingAccountForInvoice(invoice);
        else
            accountingAccount = getCustomerAccountingAccountForInvoice(invoice);

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
                    this.accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
                }

                return true;
            }
        }
        return false;
    }

    private void letterCounterPartRecords(AccountingRecord record, AccountingRecord counterPart)
            throws OsirisException {

        // Need to letter only provider and client accounts
        if (!record.getAccountingAccount().getPrincipalAccountingAccount().getId()
                .equals(constantService.getPrincipalAccountingAccountCustomer().getId())
                && !record.getAccountingAccount().getPrincipalAccountingAccount().getId()
                        .equals(constantService.getPrincipalAccountingAccountProvider().getId()))
            return;

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
        this.accountingRecordService.addOrUpdateAccountingRecord(record);
        this.accountingRecordService.addOrUpdateAccountingRecord(counterPart);
    }

    private void unletterInvoiceEmitted(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndInvoice(accountingAccountCustomer, invoice);

        if (accountingRecords != null)
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecord.setLetteringDateTime(null);
                accountingRecord.setLetteringNumber(null);
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
            }
        invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());
        invoiceService.addOrUpdateInvoice(invoice);
    }

    private void unletterInvoiceReceived(Invoice invoice) throws OsirisException {
        AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

        List<AccountingRecord> accountingRecords = accountingRecordService
                .findByAccountingAccountAndInvoice(accountingAccountProvider, invoice);

        if (accountingRecords != null)
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecord.setLetteringDateTime(null);
                accountingRecord.setLetteringNumber(null);
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord);
            }
        invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
        invoiceService.addOrUpdateInvoice(invoice);
    }

    @Override
    public void generateAccountingRecordsOnInvoiceEmission(Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal salesJournal = constantService.getAccountingJournalSales();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
            throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

        String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
                : ("Facture libre n°" + invoice.getId());

        AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);
        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        // One write on customer account for all invoice
        generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
                invoice.getManualAccountingDocumentDate(), labelPrefix, null, invoiceHelper.getPriceTotal(invoice),
                accountingAccountCustomer, null, invoice, null, salesJournal, null, null, null);

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

            AccountingAccount producAccountingAccount = invoiceItem.getBillingItem().getBillingType()
                    .getAccountingAccountProduct();

            if (producAccountingAccount == null)
                throw new OsirisException(null, "No product accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            Float billingItemPrice = invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

            balance -= billingItemPrice;

            generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - produit " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    billingItemPrice,
                    null, producAccountingAccount, invoiceItem, invoice, null, salesJournal, null, null, null);

            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
                generateNewAccountingRecord(LocalDateTime.now(), operationId,
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

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsOnInvoiceEmissionCancellation(Invoice invoice, Invoice creditNote)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        unletterInvoiceEmitted(invoice);

        AccountingJournal salesJournal = constantService.getAccountingJournalSales();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
            throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

        String labelPrefix = invoice.getCustomerOrder() != null
                ? ("Avoir n°" + creditNote.getId() + " - Commande n°" + invoice.getCustomerOrder().getId())
                : ("Avoir n°" + creditNote.getId() + " - Facture libre n°" + invoice.getId());

        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, salesJournal, labelPrefix);
                accountingRecord.setContrePasse(counterPart);
                accountingRecordService.addOrUpdateAccountingRecord(counterPart);
                letterCounterPartRecords(accountingRecord, counterPart);

                if (counterPart.getCreditAmount() != null)
                    balance += counterPart.getCreditAmount();
                else
                    balance -= counterPart.getDebitAmount();
            }

        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsOnCreditNoteReception(Invoice invoice)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        String labelPrefix = "Avoir n°" + invoice.getId();

        AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        // One write on customer account for all invoice
        generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
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
                throw new OsirisException(null, "No product accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            Float billingItemPrice = invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

            generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    billingItemPrice, null,
                    chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null, null);

            balance -= billingItemPrice;

            if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
                generateNewAccountingRecord(LocalDateTime.now(), operationId,
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
            throws OsirisException, OsirisValidationException {
        AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
                : ("Facture libre n°" + invoice.getId());

        AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

        Float balance = 0f;
        balance += invoiceHelper.getPriceTotal(invoice);
        Integer operationId = getNewTemporaryOperationId();

        // One write on provider account for all invoice
        generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
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
                throw new OsirisException(null, "No product accounting account defined in billing type n°"
                        + invoiceItem.getBillingItem().getBillingType().getId());

            Float billingItemPrice = invoiceItem.getPreTaxPrice()
                    - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

            generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
                    invoice.getManualAccountingDocumentDate(),
                    labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(), null,
                    billingItemPrice,
                    chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null, null);

            balance -= billingItemPrice;

            if (invoiceItem.getVat() != null) {

                if (invoiceItem.getVat().getAccountingAccount() == null)
                    throw new OsirisException(null, "No accounting account for VAT " + invoiceItem.getVat().getLabel());

                if (invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0)
                    generateNewAccountingRecord(LocalDateTime.now(), operationId,
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

        String labelPrefix = invoice.getCustomerOrder() != null
                ? ("Annulation - Commande n°" + invoice.getCustomerOrder().getId())
                : ("Annulation - Facture libre n°" + invoice.getId());

        Float balance = 0f;
        Integer operationId = getNewTemporaryOperationId();

        if (invoice.getAccountingRecords() != null)
            for (AccountingRecord accountingRecord : invoice.getAccountingRecords()) {
                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, pushasingJournal,
                        labelPrefix);
                accountingRecord.setContrePasse(counterPart);
                accountingRecordService.addOrUpdateAccountingRecord(counterPart);
                letterCounterPartRecords(accountingRecord, counterPart);

                if (counterPart.getCreditAmount() != null)
                    balance += counterPart.getCreditAmount();
                else
                    balance -= counterPart.getDebitAmount();
            }

        checkBalance(balance);
        letterInvoice(invoice);
    }

    @Override
    public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment, boolean isOdJournal)
            throws OsirisException {
        AccountingJournal bankJournal = !isOdJournal ? constantService.getAccountingJournalBank()
                : constantService.getAccountingJournalMiscellaneousOperations();
        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            bankJournal = constantService.getAccountingJournalCash();

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        Integer operationId = getNewTemporaryOperationId();

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(), null, payment.getPaymentAmount(),
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, null, null);

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(), payment.getPaymentAmount(), null,
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, null, null);
    }

    @Override
    public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment)
            throws OsirisException {
        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
            bankJournal = constantService.getAccountingJournalCash();

        if (payment.getPaymentAmount() > 0)
            throw new OsirisException(null, "Outgoing payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        Integer operationId = getNewTemporaryOperationId();

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null, constantService.getAccountingAccountBankJss(), null, null,
                null,
                bankJournal, payment, payment.getRefund(), payment.getBankTransfert());

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(), null,
                Math.abs(payment.getPaymentAmount()),
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, payment.getRefund(),
                payment.getBankTransfert());
    }

    @Override
    public void generateAccountingRecordOnPaymentCancellation(Payment payment)
            throws OsirisException, OsirisValidationException {
        AccountingJournal bankJournal = constantService.getAccountingJournalMiscellaneousOperations();

        if (payment.getAccountingRecords() == null)
            throw new OsirisException(null, "No accounting record for payment n°" + payment.getId());

        Integer operationId = getNewTemporaryOperationId();
        Float balance = 0f;

        for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
            if (accountingRecord.getCreditAmount() != null)
                balance += accountingRecord.getCreditAmount();
            else
                balance -= accountingRecord.getDebitAmount();

            if (accountingRecord.getIsTemporary()) {
                accountingRecordService.deleteAccountingRecord(accountingRecord);
            } else {
                AccountingRecord counterPart = getCounterPart(accountingRecord, operationId, bankJournal,
                        "Annulation du paiement " + payment.getId());
                accountingRecord.setContrePasse(counterPart);
                accountingRecordService.addOrUpdateAccountingRecord(counterPart);
                letterCounterPartRecords(accountingRecord, counterPart);
            }
        }
        checkBalance(balance);
    }

    @Override
    public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
            throws OsirisException, OsirisValidationException {
        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();
        AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);
        String isPaymentOrAppoint = payment.getIsAppoint() ? "Appoint" : "Paiement";

        // One write on customer order deposit account to equilibrate
        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                isPaymentOrAppoint + " n°" + payment.getId() + " - Paiement pour la facture n°" + invoice.getId()
                        + " - "
                        + payment.getLabel(),
                null, payment.getPaymentAmount(),
                payment.getTargetAccountingAccount(), null, invoice, null, journal, payment, null, null);

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                isPaymentOrAppoint + " n°" + payment.getId() + " - Paiement pour la facture n°" + invoice.getId(),
                payment.getPaymentAmount(), null,
                accountingAccountCustomer, null, invoice, null, journal, null, null, null);

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForSaleOnCustomerOrderDeposit(CustomerOrder customerOrder, Payment payment)
            throws OsirisException, OsirisValidationException {
        if (customerOrder == null)
            throw new OsirisException(null, "No customerOrder provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with customer order " + customerOrder.getId());

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();
        AccountingAccount accountingAccountCustomer = getCustomerDepositAccountingAccountForCustomerOrder(
                customerOrder);

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - Acompte pour la commande n°" + customerOrder.getId() + " - "
                        + payment.getLabel(),
                null, payment.getPaymentAmount(),
                payment.getTargetAccountingAccount(), null, null, null, journal, payment, null, null);

        // One write on customer order deposit account to equilibrate
        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - Acompte pour la commande n°" + customerOrder.getId(),
                payment.getPaymentAmount(), null,
                accountingAccountCustomer, null, null, customerOrder, journal, null, null, null);
    }

    @Override
    public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment)
            throws OsirisException, OsirisValidationException {

        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount() < 0)
            throw new OsirisException(null, "Incoming payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();

        Integer operationId = getNewTemporaryOperationId();
        AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - Remboursement de la facture n°" + invoice.getId() + " - "
                        + payment.getLabel(),
                null, payment.getPaymentAmount(),
                payment.getTargetAccountingAccount(), null, null, null, journal, payment, null, null);

        // One write on provider account to equilibrate
        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - Remboursement de la facture n°" + invoice.getId(),
                payment.getPaymentAmount(), null,
                accountingAccountProvider, null, invoice, null, journal, null, null, null);

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForPurschaseOnInvoicePayment(Invoice invoice, Payment payment)
            throws OsirisException, OsirisValidationException {
        if (invoice == null)
            throw new OsirisException(null, "No invoice provided");

        if (payment == null)
            throw new OsirisException(null, "No payment provided with invoice " + invoice.getId());

        if (payment.getPaymentAmount() > 0)
            throw new OsirisException(null, "Outgoin payment expected");

        if (payment.getTargetAccountingAccount() == null)
            throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

        AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();
        Integer operationId = getNewTemporaryOperationId();
        String isPaymentOrAppoint = payment.getIsAppoint() ? "Appoint" : "Paiement";

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                isPaymentOrAppoint + " n°" + payment.getId() + " - Paiement pour la facture n°" + invoice.getId()
                        + " - "
                        + payment.getLabel(),
                payment.getPaymentAmount(), null,
                constantService.getAccountingAccountBankJss(), null, invoice, null, journal, payment, null, null);

        // One write on customer order deposit account to equilibrate
        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                isPaymentOrAppoint + " n°" + payment.getId() + " - Paiement pour la facture n°" + invoice.getId(), null,
                payment.getPaymentAmount(),
                payment.getTargetAccountingAccount(), null, invoice, null, journal, null, null, null);

        checkInvoiceForLettrage(invoice);
    }

    @Override
    public void generateAccountingRecordsForRefundExport(Refund refund)
            throws OsirisException, OsirisValidationException {

        if (refund.getPayments() == null || refund.getPayments().size() != 1)
            throw new OsirisException(null, "Impossible to find refund payment");
        Payment payment = refund.getPayments().get(0);
        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(),
                null, Math.abs(payment.getPaymentAmount()), constantService.getAccountingAccountBankJss(), null, null,
                null,
                bankJournal, payment, refund, null);

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null,
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, refund, null);

        checkRefundForLettrage(refund);
    }

    @Override
    public void generateAccountingRecordsForBankTransfertExport(BankTransfert bankTransfert)
            throws OsirisException, OsirisValidationException {

        if (bankTransfert.getPayments() == null || bankTransfert.getPayments().size() != 1)
            throw new OsirisException(null, "Impossible to find refund payment");
        Payment payment = bankTransfert.getPayments().get(0);
        AccountingJournal bankJournal = constantService.getAccountingJournalBank();
        Integer operationId = getNewTemporaryOperationId();

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(),
                null, Math.abs(payment.getPaymentAmount()), constantService.getAccountingAccountBankJss(), null, null,
                null,
                bankJournal, payment, null, bankTransfert);

        generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
                "Paiement n°" + payment.getId() + " - " + payment.getLabel(),
                Math.abs(payment.getPaymentAmount()), null,
                payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, null, bankTransfert);

        checkBankTransfertForLettrage(bankTransfert);
    }
}
