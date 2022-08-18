package com.jss.osiris.modules.quotation.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.repository.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

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
    public Invoice createInvoice() {
        Invoice invoice = new Invoice();
        invoice.setCreatedDate(new Date());
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
                vatTotal += invoiceItem.getPreTaxPrice();
            }
        }
        return vatTotal;
    }

    @Override
    public Float getPriceTotal(Invoice invoice) {
        return this.getPreTaxPriceTotal(invoice) - this.getDiscountTotal(invoice) + this.getVatTotal(invoice);
    }

}