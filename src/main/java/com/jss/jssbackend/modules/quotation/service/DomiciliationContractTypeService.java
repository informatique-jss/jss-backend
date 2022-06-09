package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.DomiciliationContractType;

public interface DomiciliationContractTypeService {
    public List<DomiciliationContractType> getDomiciliationContractTypes();

    public DomiciliationContractType getDomiciliationContractType(Integer id);
}
