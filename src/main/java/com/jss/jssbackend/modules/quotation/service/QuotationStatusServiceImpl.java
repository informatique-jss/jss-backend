package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.QuotationStatus;
import com.jss.jssbackend.modules.quotation.repository.QuotationStatusRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuotationStatusServiceImpl implements QuotationStatusService {

    @Autowired
    QuotationStatusRepository quotationStatusRepository;

    @Override
    public List<QuotationStatus> getQuotationStatus() {
        return IterableUtils.toList(quotationStatusRepository.findAll());
    }

    @Override
    public QuotationStatus getQuotationStatus(Integer id) {
        Optional<QuotationStatus> quotationStatus = quotationStatusRepository.findById(id);
        if (!quotationStatus.isEmpty())
            return quotationStatus.get();
        return null;
    }
}
