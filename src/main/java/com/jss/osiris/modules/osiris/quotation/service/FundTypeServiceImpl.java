package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.FundType;
import com.jss.osiris.modules.osiris.quotation.repository.FundTypeRepository;

@Service
public class FundTypeServiceImpl implements FundTypeService {

    @Autowired
    FundTypeRepository fundTypeRepository;

    @Override
    public List<FundType> getFundTypes() {
        return IterableUtils.toList(fundTypeRepository.findAll());
    }

    @Override
    public FundType getFundType(Integer id) {
        Optional<FundType> fundType = fundTypeRepository.findById(id);
        if (fundType.isPresent())
            return fundType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FundType addOrUpdateFundType(FundType fundType) {
        return fundTypeRepository.save(fundType);
    }
}
