package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.repository.InvoiceStatusRepository;

@Service
public class InvoiceStatusServiceImpl implements InvoiceStatusService {

    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;

    @Override
    @Cacheable(value = "invoiceStatusList", key = "#root.methodName")
    public List<InvoiceStatus> getInvoiceStatus() {
        return IterableUtils.toList(invoiceStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "invoiceStatus", key = "#id")
    public InvoiceStatus getInvoiceStatus(Integer id) {
        Optional<InvoiceStatus> invoiceStatus = invoiceStatusRepository.findById(id);
        if (!invoiceStatus.isEmpty())
            return invoiceStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "invoiceStatusList", allEntries = true),
            @CacheEvict(value = "invoiceStatus", key = "#invoiceStatus.id")
    })
    public InvoiceStatus addOrUpdateInvoiceStatus(
            InvoiceStatus invoiceStatus) {
        return invoiceStatusRepository.save(invoiceStatus);
    }

    @Override
    public InvoiceStatus getInvoiceStatusByCode(String invoiceStatusPayedCode) {
        return invoiceStatusRepository.findByCode(invoiceStatusPayedCode);
    }
}
