package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.accounting.model.PaySlipLineType;
import com.jss.osiris.modules.osiris.accounting.repository.PaySlipLineTypeRepository;

@Service
public class PaySlipLineTypeServiceImpl implements PaySlipLineTypeService {

    @Autowired
    PaySlipLineTypeRepository paySlipLineTypeRepository;

    @Override
    public List<PaySlipLineType> getPaySlipLineTypes() {
        return IterableUtils.toList(paySlipLineTypeRepository.findAll());
    }

    @Override
    public PaySlipLineType getPaySlipLineType(Integer id) {
        Optional<PaySlipLineType> paySlipLineType = paySlipLineTypeRepository.findById(id);
        if (paySlipLineType.isPresent())
            return paySlipLineType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaySlipLineType addOrUpdatePaySlipLineType(
            PaySlipLineType paySlipLineType) {
        return paySlipLineTypeRepository.save(paySlipLineType);
    }

    @Override
    public PaySlipLineType getPaySlipLineTypeByLabel(String label) {
        return paySlipLineTypeRepository.findByLabel(label);
    }
}
