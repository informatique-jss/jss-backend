package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.tiers.model.TiersType;
import com.jss.jssbackend.modules.tiers.repository.TiersTypeRepository;

@Service
public class TiersTypeServiceImpl implements TiersTypeService {

    @Autowired
    TiersTypeRepository tiersTypeRepository;

    @Override
    public List<TiersType> getTiersTypes() {
        return IterableUtils.toList(tiersTypeRepository.findAll());
    }

    @Override
    public TiersType getTiersType(Integer id) {
        Optional<TiersType> tiersType = tiersTypeRepository.findById(id);
        if (!tiersType.isEmpty())
            return tiersType.get();
        return null;
    }
}
