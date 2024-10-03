package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.QuotationAbandonReason;
import com.jss.osiris.modules.osiris.quotation.repository.QuotationAbandonReasonRepository;

@Service
public class QuotationAbandonReasonServiceImpl implements QuotationAbandonReasonService {

    @Autowired
    QuotationAbandonReasonRepository quotationAbandonReasonRepository;

    @Override
    public List<QuotationAbandonReason> getQuotationAbandonReasons() {
        return IterableUtils.toList(quotationAbandonReasonRepository.findAll());
    }

    @Override
    public QuotationAbandonReason getAbandonReason(Integer id) {
        Optional<QuotationAbandonReason> abandonReason = quotationAbandonReasonRepository.findById(id);
        if (abandonReason.isPresent())
            return abandonReason.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotationAbandonReason addOrUpdateQutationAbandonReason(QuotationAbandonReason abandonReason) {
        return quotationAbandonReasonRepository.save(abandonReason);
    }

}