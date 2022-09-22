package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.service.IndexEntityService;
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

    @Value("${miscellaneous.document.billing.label.type.other.code}")
    private String billingLabelOtherCode;

    @Value("${invoicing.invoice.status.send.code}")
    private String invoiceStatusSendCode;

    @Autowired
    IndexEntityService indexEntityService;

    @Override
    public List<Invoice> getAllInvoices() {
        return IterableUtils.toList(invoiceRepository.findAll());
    }

    @Override
    @Cacheable(value = "invoice", key = "#id")
    public Invoice getInvoice(Integer id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (!invoice.isEmpty())
            return invoice.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "invoice", key = "#invoice.id")
    })
    public Invoice addOrUpdateInvoice(
            Invoice invoice) {
        invoiceRepository.save(invoice);
        indexEntityService.indexEntity(invoice, invoice.getId());
        return invoice;
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

        // Defined billing label
        if (billingLabelOtherCode.equals(billingDocument.getBillingLabelType().getCode())) {
            invoice.setBillingLabel(billingDocument.getBillingLabel());
            invoice.setBillingLabelAddress(billingDocument.getBillingLabelAddress());
            invoice.setBillingLabelPostalCode(billingDocument.getBillingLabelPostalCode());
            invoice.setBillingLabelCity(billingDocument.getBillingLabelCity());
            invoice.setBillingLabelCountry(billingDocument.getBillingLabelCountry());
            invoice.setBillingLabelIsIndividual(billingDocument.getBillingLabelIsIndividual());
            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoice.setCommandNumber(billingDocument.getCommandNumber());
        } else if (billingLabelAffaireCode.equals(billingDocument.getBillingLabelType().getCode())) {
            Affaire affaire = customerOrder.getProvisions().get(0).getAffaire();
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

        InvoiceStatus statusSent = invoiceStatusService.getInvoiceStatusByCode(invoiceStatusSendCode);
        if (statusSent == null)
            throw new Exception("Status Sent for invoice not found for parameter " + invoiceStatusSendCode);

        invoice.setInvoiceStatus(statusSent);

        this.addOrUpdateInvoice(invoice);
        return invoice;
    }

    @Override
    public Float getDiscountTotal(Invoice invoice) {
        Float discountTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                discountTotal += invoiceItem.getDiscountAmount();
            }
        }
        return discountTotal;
    }

    @Override
    public Float getPreTaxPriceTotal(Invoice invoice) {
        Float preTaxTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                preTaxTotal += invoiceItem.getPreTaxPrice();
            }
        }
        return preTaxTotal;
    }

    @Override
    public Float getVatTotal(Invoice invoice) {
        Float vatTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                vatTotal += invoiceItem.getVatPrice();
            }
        }
        return vatTotal;
    }

    @Override
    public Float getPriceTotal(Invoice invoice) {
        Float total = this.getPreTaxPriceTotal(invoice) - this.getDiscountTotal(invoice) + this.getVatTotal(invoice);
        return total;
    }

    @Override
    public Invoice setPriceTotal(Invoice invoice) {
        if (invoice != null) {
            invoice.setTotalPrice(this.getPriceTotal(invoice));
            this.addOrUpdateInvoice(invoice);
        }
        return invoice;
    }

    @Override
    public ITiers getCustomerOrder(Invoice invoice) throws Exception {
        if (invoice.getConfrere() != null)
            return invoice.getConfrere();

        if (invoice.getResponsable() != null)
            return invoice.getResponsable();

        if (invoice.getTiers() != null)
            return invoice.getTiers();

        throw new Exception("No customer order declared on Invoice " + invoice.getId());
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
                invoiceSearch.getEndDate());
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