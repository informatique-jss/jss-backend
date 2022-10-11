package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.repository.QuotationLabelTypeRepository;

@Service
public class QuotationLabelTypeServiceImpl implements QuotationLabelTypeService {

    @Autowired
    QuotationLabelTypeRepository quotationLabelTypeRepository;

    @Override
    public List<QuotationLabelType> getQuotationLabelTypes() {
        return IterableUtils.toList(quotationLabelTypeRepository.findAll());
    }

    @Override
    public QuotationLabelType getQuotationLabelType(Integer id) {
        Optional<QuotationLabelType> quotationLabelType = quotationLabelTypeRepository.findById(id);
        if (quotationLabelType.isPresent())
            return quotationLabelType.get();
        return null;
    }

    @Override
    public QuotationLabelType addOrUpdateQuotationLabelType(QuotationLabelType quotationLabelType) {
        return quotationLabelTypeRepository.save(quotationLabelType);
    }
}
