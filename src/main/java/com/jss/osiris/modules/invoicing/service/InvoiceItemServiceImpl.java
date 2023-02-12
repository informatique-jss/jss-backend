package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.repository.InvoiceItemRepository;

@Service
public class InvoiceItemServiceImpl implements InvoiceItemService {

    @Autowired
    InvoiceItemRepository invoiceItemRepository;

    @Override
    public List<InvoiceItem> getInvoiceItems() {
        return IterableUtils.toList(invoiceItemRepository.findAll());
    }

    @Override
    public InvoiceItem getInvoiceItem(Integer id) {
        Optional<InvoiceItem> invoiceItem = invoiceItemRepository.findById(id);
        if (invoiceItem.isPresent())
            return invoiceItem.get();
        return null;
    }

    @Override
    public InvoiceItem addOrUpdateInvoiceItem(
            InvoiceItem invoiceItem) {
        return invoiceItemRepository.save(invoiceItem);
    }

    @Override
    public void deleteInvoiceItem(InvoiceItem invoiceItem) {
        invoiceItemRepository.delete(invoiceItem);
    }

    @Override
    public InvoiceItem cloneInvoiceItem(InvoiceItem invoiceItem) {
        InvoiceItem newInvoiceItem = new InvoiceItem();
        newInvoiceItem.setBillingItem(invoiceItem.getBillingItem());
        newInvoiceItem.setDiscountAmount(invoiceItem.getDiscountAmount());
        newInvoiceItem.setInvoice(invoiceItem.getInvoice());
        newInvoiceItem.setIsGifted(invoiceItem.getIsGifted());
        newInvoiceItem.setIsOverridePrice(invoiceItem.getIsOverridePrice());
        newInvoiceItem.setLabel(invoiceItem.getLabel());
        newInvoiceItem.setPreTaxPrice(invoiceItem.getPreTaxPrice());
        newInvoiceItem.setProvision(invoiceItem.getProvision());
        newInvoiceItem.setVat(invoiceItem.getVat());
        newInvoiceItem.setVatPrice(invoiceItem.getVatPrice());
        return newInvoiceItem;
    }

}
