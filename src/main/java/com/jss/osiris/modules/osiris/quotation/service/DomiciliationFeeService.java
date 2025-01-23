package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.modules.osiris.quotation.model.DomiciliationFee;

public interface DomiciliationFeeService {
    public DomiciliationFee getDomiciliationFee(Integer id);

    public void deleteDomiciliationFee(DomiciliationFee domiciliationFee);

    public DomiciliationFee addDomiciliationFee(DomiciliationFee domiciliationFee);
}
