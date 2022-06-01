package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.QuotationLabelType;
import com.jss.jssbackend.modules.quotation.repository.QuotationLabelTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!quotationLabelType.isEmpty())
            return quotationLabelType.get();
        return null;
    }
}
