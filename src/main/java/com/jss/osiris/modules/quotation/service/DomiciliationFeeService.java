package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.DomiciliationFee;

public interface DomiciliationFeeService {
    public DomiciliationFee getDomiciliationFee(Integer id);

    public void deleteDomiciliationFee(DomiciliationFee domiciliationFee);

    public DomiciliationFee addDomiciliationFee(DomiciliationFee domiciliationFee);
}
