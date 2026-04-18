package com.jss.osiris.modules.osiris.invoicing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSequence;
import com.jss.osiris.modules.osiris.invoicing.repository.InvoiceSequenceRepository;

@Service
public class InvoiceSequenceServiceImpl implements InvoiceSequenceService {

    @Autowired
    private InvoiceSequenceRepository invoiceSequenceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Integer getNextInvoiceNumber() {
        InvoiceSequence seq = invoiceSequenceRepository.findAndLock();

        Integer nextValue = seq.getLastValue() + 1;
        seq.setLastValue(nextValue);

        invoiceSequenceRepository.save(seq);

        return nextValue;
    }
}
