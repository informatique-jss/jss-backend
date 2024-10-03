package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.tiers.model.TiersFollowupType;
import com.jss.osiris.modules.osiris.tiers.repository.TiersFollowupTypeRepository;

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
        if (tiersFollowupType.isPresent())
            return tiersFollowupType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TiersFollowupType addOrUpdateTiersFollowupType(
            TiersFollowupType tiersFollowupType) {
        return tiersFollowupTypeRepository.save(tiersFollowupType);
    }
}
