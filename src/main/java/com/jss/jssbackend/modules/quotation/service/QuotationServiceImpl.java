package com.jss.jssbackend.modules.quotation.service;

import java.util.Date;
import java.util.Optional;

import com.jss.jssbackend.libs.search.service.IndexEntityService;
import com.jss.jssbackend.modules.quotation.model.Quotation;
import com.jss.jssbackend.modules.quotation.repository.QuotationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (!quotation.isEmpty())
            return quotation.get();
        return null;
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation) {
        if (quotation.getId() == null)
            quotation.setCreatedDate(new Date());
        quotation = quotationRepository.save(quotation);
        indexEntityService.indexEntity(quotation, quotation.getId());
        return quotation;
    }
}
