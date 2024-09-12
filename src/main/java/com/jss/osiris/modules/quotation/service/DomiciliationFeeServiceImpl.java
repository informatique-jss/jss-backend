package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.quotation.model.DomiciliationFee;
import com.jss.osiris.modules.quotation.repository.DomiciliationFeeRepository;

@Service
public class DomiciliationFeeServiceImpl implements DomiciliationFeeService {

    @Autowired
    DomiciliationFeeRepository domiciliationFeeRepository;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Override
    public DomiciliationFee getDomiciliationFee(Integer id) {
        Optional<DomiciliationFee> domiciliationFee = domiciliationFeeRepository.findById(id);
        if (domiciliationFee.isPresent())
            return domiciliationFee.get();
        return null;
    }

    @Override
    public void deleteDomiciliationFee(DomiciliationFee domiciliationFee) {
        DomiciliationFee fee = getDomiciliationFee(domiciliationFee.getId());
        if (fee.getInvoiceItems() != null && fee.getInvoiceItems().size() > 0)
            invoiceItemService.deleteInvoiceItem(fee.getInvoiceItems().get(0));
        domiciliationFeeRepository.delete(domiciliationFee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DomiciliationFee addDomiciliationFee(DomiciliationFee domiciliationFee) {
        return domiciliationFeeRepository.save(domiciliationFee);
    }

}
