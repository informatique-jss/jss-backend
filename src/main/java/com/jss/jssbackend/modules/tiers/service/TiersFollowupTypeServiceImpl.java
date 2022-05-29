package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.TiersFollowupType;
import com.jss.jssbackend.modules.tiers.repository.TiersFollowupTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersFollowupTypeServiceImpl implements TiersFollowupTypeService {

    @Autowired
    TiersFollowupTypeRepository tiersFollowupTypeRepository;

    @Override
    public List<TiersFollowupType> getTiersFollowupTypes() {
        return IterableUtils.toList(tiersFollowupTypeRepository.findAll());
    }

    @Override
    public TiersFollowupType getTiersFollowupType(Integer id) {
        Optional<TiersFollowupType> tiersFollowupType = tiersFollowupTypeRepository.findById(id);
        if (!tiersFollowupType.isEmpty())
            return tiersFollowupType.get();
        return null;
    }
}
