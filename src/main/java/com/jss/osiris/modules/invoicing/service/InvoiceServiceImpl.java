package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.repository.InvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    DocumentService documentService;

    @Autowired
    InvoiceStatusService invoiceStatusService;

    @Value("${miscellaneous.document.billing.label.type.affaire.code}")
    private String billingLabelAffaireCode;

    @Value("${miscellaneous.document.billing.label.type.customer.code}")
    private String billingLabelCustomerCode;

    @Value("${miscellaneous.document.billing.label.type.other.code}")
    private String billingLabelBillingOther;

    @Value("${invoicing.invoice.status.send.code}")
    private String invoiceStatusSendCode;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Override
    public List<Invoice> getAllInvoices() {
        return IterableUtils.toList(invoiceRepository.findAll());
    }

    @Override
    public Invoice getInvoice(Integer id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent())
            return invoice.get();
        return null;
    }

    @Override
    public Invoice addOrUpdateInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
        indexEntityService.indexEntity(invoice, invoice.getId());
        return invoice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice addOrUpdateInvoiceFromUser(Invoice invoice) throws Exception {
        if (!hasAtLeastOneInvoiceItemNotNull(invoice))
            throw new Exception("No invoice item found on manual invoice");

        invoice.setCreatedDate(LocalDateTime.now());

        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setVatPrice(invoiceItem.getVat().getRate() * invoiceItem.getPreTaxPrice() / 100);
        }

        // Defined billing label
        if (!billingLabelBillingOther.equals(invoice.getBillingLabelType().getCode())) {

            ITiers customerOrder = invoice.getTiers();
            if (invoice.getResponsable() != null)
                customerOrder = invoice.getResponsable();
            if (invoice.getConfrere() != null)
                customerOrder = invoice.getConfrere();
            if (invoice.getProvider() != null)
                customerOrder = invoice.getProvider();
            Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

            setInvoiceLabel(invoice, billingDocument, null, customerOrder);
        }

        InvoiceStatus statusSent = invoiceStatusService.getInvoiceStatusByCode(invoiceStatusSendCode);
        if (statusSent == null)
            throw new Exception("Status Sent for invoice not found for parameter " + invoiceStatusSendCode);

        invoice.setInvoiceStatus(statusSent);
        // Associate invoice to invoice item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            invoiceItem.setInvoice(invoice);
        }

        invoiceHelper.setPriceTotal(invoice);

        // Save before to have an ID on invoice
        addOrUpdateInvoice(invoice);

        if (invoice.getProvider() == null)
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(invoice);
        else
            accountingRecordService.generateAccountingRecordsForPurshaseOnInvoiceGeneration(invoice);

        addOrUpdateInvoice(invoice);
        return invoice;
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(Invoice invoice) {
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems())
            if (invoiceItem.getPreTaxPrice() > 0) {
                return true;

            }
        return false;
    }

    @Override
    public Invoice createInvoice(CustomerOrder customerOrder, ITiers orderingCustomer) throws Exception {
        Invoice invoice = new Invoice();
        invoice.setCreatedDate(LocalDateTime.now());
        Document billingDocument = documentService.getBillingDocument(customerOrder.getDocuments());

        if (billingDocument == null)
            throw new Exception("Billing document not found for ordering customer provided");

        if (orderingCustomer instanceof Tiers)
            invoice.setTiers((Tiers) orderingCustomer);

        if (orderingCustomer instanceof Responsable)
            invoice.setResponsable((Responsable) orderingCustomer);

        if (orderingCustomer instanceof Confrere)
            invoice.setConfrere((Confrere) orderingCustomer);

        Integer nbrOfDayFromDueDate = 30;
        if (billingDocument.getPaymentDeadlineType() != null)
            nbrOfDayFromDueDate = billingDocument.getPaymentDeadlineType().getNumberOfDay();
        invoice.setDueDate(LocalDate.now().plusDays(nbrOfDayFromDueDate));

        setInvoiceLabel(invoice, billingDocument, customerOrder, orderingCustomer);

        InvoiceStatus statusSent = invoiceStatusService.getInvoiceStatusByCode(invoiceStatusSendCode);
        if (statusSent == null)
            throw new Exception("Status Sent for invoice not found for parameter " + invoiceStatusSendCode);

        invoice.setInvoiceStatus(statusSent);
        this.addOrUpdateInvoice(invoice);
        return invoice;
    }

    private void setInvoiceLabel(Invoice invoice, Document billingDocument, CustomerOrder customerOrder,
            ITiers orderingCustomer) throws Exception {
        // Defined billing label
        if (billingLabelBillingOther.equals(billingDocument.getBillingLabelType().getCode())) {
            if (billingDocument.getRegie() != null) {
                invoice.setBillingLabel(billingDocument.getRegie().getLabel());
                invoice.setBillingLabelAddress(billingDocument.getRegie().getAddress());
                invoice.setBillingLabelPostalCode(billingDocument.getRegie().getPostalCode());
                invoice.setBillingLabelCity(billingDocument.getRegie().getCity());
                invoice.setBillingLabelCountry(billingDocument.getRegie().getCountry());
                invoice.setBillingLabelIsIndividual(false);
                invoice.setBillingLabelType(billingDocument.getBillingLabelType());
                invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoice.setCommandNumber(billingDocument.getCommandNumber());
            } else {
                invoice.setBillingLabel(billingDocument.getBillingLabel());
                invoice.setBillingLabelAddress(billingDocument.getBillingAddress());
                invoice.setBillingLabelPostalCode(billingDocument.getBillingPostalCode());
                invoice.setBillingLabelCity(billingDocument.getBillingLabelCity());
                invoice.setBillingLabelCountry(billingDocument.getBillingLabelCountry());
                invoice.setBillingLabelIsIndividual(billingDocument.getBillingLabelIsIndividual());
                invoice.setBillingLabelType(billingDocument.getBillingLabelType());
                invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoice.setCommandNumber(billingDocument.getCommandNumber());
            }
        } else if (customerOrder != null
                && billingLabelAffaireCode.equals(billingDocument.getBillingLabelType().getCode())) {
            if (customerOrder.getAssoAffaireOrders() == null || customerOrder.getAssoAffaireOrders().size() == 0)
                throw new Exception("No affaire in the customer order " + customerOrder.getId());
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            invoice.setBillingLabel(affaire.getIsIndividual() ? affaire.getFirstname() + " " + affaire.getLastname()
                    : affaire.getDenomination());
            invoice.setBillingLabelAddress(affaire.getAddress());
            invoice.setBillingLabelPostalCode(affaire.getPostalCode());
            invoice.setBillingLabelCity(affaire.getCity());
            invoice.setBillingLabelCountry(affaire.getCountry());
            invoice.setBillingLabelIsIndividual(affaire.getIsIndividual());
            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoice.setCommandNumber(billingDocument.getCommandNumber());
        } else {
            if (invoice.getTiers() != null)
                invoice.setBillingLabel(invoice.getTiers().getIsIndividual()
                        ? invoice.getTiers().getFirstname() + " " + invoice.getTiers().getLastname()
                        : invoice.getTiers().getDenomination());
            if (invoice.getResponsable() != null)
                invoice.setBillingLabel(
                        invoice.getResponsable().getFirstname() + " " + invoice.getResponsable().getLastname());
            if (invoice.getConfrere() != null)
                invoice.setBillingLabel(invoice.getConfrere().getLabel());
            invoice.setBillingLabelAddress(orderingCustomer.getAddress());
            invoice.setBillingLabelPostalCode(orderingCustomer.getPostalCode());
            invoice.setBillingLabelCity(orderingCustomer.getCity());
            invoice.setBillingLabelCountry(orderingCustomer.getCountry());
            invoice.setBillingLabelIsIndividual(orderingCustomer.getIsIndividual());
            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoice.setCommandNumber(billingDocument.getCommandNumber());
        }
    }

    @Override
    public Invoice getInvoiceForCustomerOrder(Integer customerOrderId) {
        return invoiceRepository.findByCustomerOrderId(customerOrderId);
    }

    @Override
    public LocalDate getFirstBillingDateForTiers(Tiers tiers) {
        return invoiceRepository.findFirstBillingDateForTiers(tiers);
    }

    @Override
    public LocalDate getFirstBillingDateForResponsable(Responsable responsable) {
        return invoiceRepository.findFirstBillingDateForResponsable(responsable);
    }

    @Override
    public List<Invoice> searchInvoices(InvoiceSearch invoiceSearch) throws Exception {
        List<Invoice> invoices = invoiceRepository.findInvoice(invoiceSearch.getInvoiceStatus(),
                invoiceSearch.getStartDate(),
                invoiceSearch.getEndDate(), invoiceSearch.getMinAmount(), invoiceSearch.getMaxAmount());
        return invoices;
    }

    @Override
    public void reindexInvoices() {
        List<Invoice> invoices = getAllInvoices();
        if (invoices != null)
            for (Invoice invoice : invoices)
                indexEntityService.indexEntity(invoice, invoice.getId());
    }

}