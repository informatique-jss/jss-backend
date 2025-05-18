package com.jss.osiris.modules.osiris.invoicing.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.invoicing.model.InvoicingBlockage;
import com.jss.osiris.modules.osiris.invoicing.repository.InvoicingBlockageRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoicingBlockageServiceImpl implements InvoicingBlockageService {

    @Autowired
    InvoicingBlockageRepository invoicingBlockageRepository;

    @Override
    public List<InvoicingBlockage> getInvoicingBlockages() {
        return IterableUtils.toList(invoicingBlockageRepository.findAll());
    }

    @Override
    public InvoicingBlockage getInvoicingBlockage(Integer id) {
        Optional<InvoicingBlockage> invoicingBlockage = invoicingBlockageRepository.findById(id);
        if (invoicingBlockage.isPresent())
            return invoicingBlockage.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoicingBlockage addOrUpdateInvoicingBlockage(
            InvoicingBlockage invoicingBlockage) {
        return invoicingBlockageRepository.save(invoicingBlockage);
    }
}
