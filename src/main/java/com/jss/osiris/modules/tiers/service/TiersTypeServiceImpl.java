package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.tiers.model.TiersType;
import com.jss.osiris.modules.tiers.repository.TiersTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	 @Override
    public TiersType addOrUpdateTiersType(
            TiersType tiersType) {
        return tiersTypeRepository.save(tiersType);
    }
}
