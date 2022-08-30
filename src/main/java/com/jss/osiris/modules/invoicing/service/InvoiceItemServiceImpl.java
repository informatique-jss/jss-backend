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
        if (!invoiceItem.isEmpty())
            return invoiceItem.get();
        return null;
    }

    @Override
    public InvoiceItem addOrUpdateInvoiceItem(
            InvoiceItem invoiceItem) {
        return invoiceItemRepository.save(invoiceItem);
    }

}
