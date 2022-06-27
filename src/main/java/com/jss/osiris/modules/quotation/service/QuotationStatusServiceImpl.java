package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.repository.QuotationStatusRepository;

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

    @Override
    public QuotationStatus addOrUpdateQuotationStatus(QuotationStatus quotationStatus) {
        return quotationStatusRepository.save(quotationStatus);
    }
}
