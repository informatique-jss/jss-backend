package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class InvoiceAndAccountRecordDelegate {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DocumentService documentService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    PaymentService paymentService;

    @Transactional(rollbackFor = Exception.class)
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice) throws Exception {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice))
            throw new Exception("No invoice item found on manual invoice");

        invoice.setCreatedDate(LocalDateTime.now());

        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setVatPrice(invoiceItem.getVat().getRate() * invoiceItem.getPreTaxPrice() / 100);
        }

        // Defined billing label
        if (!constantService.getBillingLabelTypeOther().getId().equals(invoice.getBillingLabelType().getId())) {

            ITiers customerOrder = invoice.getTiers();
            if (invoice.getResponsable() != null)
                customerOrder = invoice.getResponsable();
            if (invoice.getConfrere() != null)
                customerOrder = invoice.getConfrere();
            if (invoice.getProvider() != null)
                customerOrder = invoice.getProvider();
            Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

            invoiceHelper.setInvoiceLabel(invoice, billingDocument, null, customerOrder);
        }

        InvoiceStatus statusSent = constantService.getInvoiceStatusSend();

        invoice.setInvoiceStatus(statusSent);
        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setInvoice(invoice);
        }

        invoiceHelper.setPriceTotal(invoice);

        // Save before to have an ID on invoice
        invoiceService.addOrUpdateInvoice(invoice);

        if (invoice.getProvider() == null)
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(invoice);
        else
            accountingRecordService.generateAccountingRecordsForPurshaseOnInvoiceGeneration(invoice);

        invoiceService.addOrUpdateInvoice(invoice);
        return invoice;
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(Invoice invoice) {
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems())
            if (invoiceItem.getPreTaxPrice() > 0) {
                return true;

            }
        return false;
    }

    public void unletterInvoice(Invoice invoice) throws Exception {
        AccountingAccount accountingAccountCustomer = accountingRecordService
                .getCustomerAccountingAccountForInvoice(invoice);

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

    @Transactional(rollbackFor = Exception.class)
    public List<AccountingRecord> deleteRecordsByTemporaryOperationId(Integer temporaryOperationId) throws Exception {
        List<AccountingRecord> accountingRecords = accountingRecordService
                .getAccountingRecordsByTemporaryOperationId(temporaryOperationId);

        List<Invoice> invoicesToUnleter = new ArrayList<Invoice>();

        if (accountingRecords != null) {
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecordService.deleteAccountingRecord(accountingRecord);
                boolean invoiceFound = false;
                for (Invoice invoice : invoicesToUnleter)
                    if (invoice.getId().equals(accountingRecord.getInvoice().getId()))
                        invoiceFound = true;
                if (!invoiceFound)
                    invoicesToUnleter.add(accountingRecord.getInvoice());
            }
        }

        // Unleter invoices
        if (invoicesToUnleter.size() > 0)
            for (Invoice invoice : invoicesToUnleter)
                unletterInvoice(invoice);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AccountingRecord> doCounterPartByOperationId(Integer operationId) throws Exception {
        List<AccountingRecord> accountingRecords = accountingRecordService
                .getAccountingRecordsByOperationId(operationId);

        List<Invoice> invoicesToUnleter = new ArrayList<Invoice>();

        if (accountingRecords != null) {
            for (AccountingRecord accountingRecord : accountingRecords) {
                accountingRecordService.generateCounterPart(accountingRecord);
                accountingRecord.setInvoice(null);
                paymentService.unlinkPaymentFromInvoiceCustomerOrder(accountingRecord.getPayment());
                accountingRecord.setPayment(null);
                accountingRecord.setDeposit(null);
                boolean invoiceFound = false;
                for (Invoice invoice : invoicesToUnleter)
                    if (invoice.getId().equals(accountingRecord.getInvoice().getId()))
                        invoiceFound = true;
                if (!invoiceFound)
                    invoicesToUnleter.add(accountingRecord.getInvoice());
            }
        }

        // Unleter invoices
        if (invoicesToUnleter.size() > 0)
            for (Invoice invoice : invoicesToUnleter)
                unletterInvoice(invoice);
        return null;
    }
}
