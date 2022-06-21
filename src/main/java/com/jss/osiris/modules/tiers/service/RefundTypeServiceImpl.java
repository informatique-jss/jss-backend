package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.repository.RefundTypeRepository;

@Service
public class RefundTypeServiceImpl implements RefundTypeService {

    @Autowired
    RefundTypeRepository refundTypeRepository;

    @Override
    public List<RefundType> getRefundTypes() {
        return IterableUtils.toList(refundTypeRepository.findAll());
    }

    @Override
    public RefundType getRefundType(Integer id) {
        Optional<RefundType> refundType = refundTypeRepository.findById(id);
        if (!refundType.isEmpty())
            return refundType.get();
        return null;
    }
}
