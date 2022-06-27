package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.DomiciliationContractType;

public interface DomiciliationContractTypeService {
    public List<DomiciliationContractType> getDomiciliationContractTypes();

    public DomiciliationContractType getDomiciliationContractType(Integer id);

    public DomiciliationContractType addOrUpdateDomiciliationContractType(
            DomiciliationContractType domiciliationContractType);
}
