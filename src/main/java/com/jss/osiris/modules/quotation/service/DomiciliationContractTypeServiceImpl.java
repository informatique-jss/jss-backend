package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.repository.DomiciliationContractTypeRepository;

@Service
public class DomiciliationContractTypeServiceImpl implements DomiciliationContractTypeService {

    @Autowired
    DomiciliationContractTypeRepository domiciliationContractTypeRepository;

    @Override
    public List<DomiciliationContractType> getDomiciliationContractTypes() {
        return IterableUtils.toList(domiciliationContractTypeRepository.findAll());
    }

    @Override
    public DomiciliationContractType getDomiciliationContractType(Integer id) {
        Optional<DomiciliationContractType> domiciliationContractType = domiciliationContractTypeRepository
                .findById(id);
        if (!domiciliationContractType.isEmpty())
            return domiciliationContractType.get();
        return null;
    }
}
