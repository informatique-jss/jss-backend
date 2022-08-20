package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.repository.InvoiceRepository;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    DocumentService documentService;

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
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice createInvoice(ITiers orderingCustomer) throws Exception {
        Invoice invoice = new Invoice();
        invoice.setCreatedDate(LocalDateTime.now());
        Document billingDocument = documentService.getBillingDocument(orderingCustomer.getDocuments());

        if (billingDocument == null)
            throw new Exception("Billing document not found for ordering customer provided");

        if (orderingCustomer instanceof Tiers)
            invoice.setTiers((Tiers) orderingCustomer);

        if (orderingCustomer instanceof Responsable)
            invoice.setResponsable((Responsable) orderingCustomer);

        if (orderingCustomer instanceof Confrere)
            invoice.setConfrere((Confrere) orderingCustomer);

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

        invoiceRepository.save(invoice);
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
        return this.getPreTaxPriceTotal(invoice) - this.getDiscountTotal(invoice) + this.getVatTotal(invoice);
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

}