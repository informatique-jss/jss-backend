package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.repository.InvoiceStatusRepository;

@Service
public class InvoiceStatusServiceImpl implements InvoiceStatusService {

    @Autowired
    InvoiceStatusRepository invoiceStatusRepository;

    @Override
    public List<InvoiceStatus> getInvoiceStatus() {
        return IterableUtils.toList(invoiceStatusRepository.findAll());
    }

    @Override
    public InvoiceStatus getInvoiceStatus(Integer id) {
        Optional<InvoiceStatus> invoiceStatus = invoiceStatusRepository.findById(id);
        if (invoiceStatus.isPresent())
            return invoiceStatus.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceStatus addOrUpdateInvoiceStatus(
            InvoiceStatus invoiceStatus) {
        return invoiceStatusRepository.save(invoiceStatus);
    }

    @Override
    public InvoiceStatus getInvoiceStatusByCode(String invoiceStatusPayedCode) {
        return invoiceStatusRepository.findByCode(invoiceStatusPayedCode);
    }
}
