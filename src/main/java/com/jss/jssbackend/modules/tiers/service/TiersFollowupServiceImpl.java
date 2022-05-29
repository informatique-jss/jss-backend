package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.TiersFollowup;
import com.jss.jssbackend.modules.tiers.repository.TiersFollowupRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersFollowupServiceImpl implements TiersFollowupService {

    @Autowired
    TiersFollowupRepository tiersFollowupRepository;

    @Override
    public List<TiersFollowup> getTiersFollowups() {
        return IterableUtils.toList(tiersFollowupRepository.findAll());
    }

    @Override
    public TiersFollowup getTiersFollowup(Integer id) {
        Optional<TiersFollowup> tiersFollowup = tiersFollowupRepository.findById(id);
        if (!tiersFollowup.isEmpty())
            return tiersFollowup.get();
        return null;
    }

    @Override
    public List<TiersFollowup> addTiersFollowup(TiersFollowup tiersFollowup) {
        tiersFollowupRepository.save(tiersFollowup);
        if (tiersFollowup.getTiers() != null)
            return tiersFollowupRepository.findByTiersId(tiersFollowup.getTiers().getId());
        return tiersFollowupRepository.findByResponsableId(tiersFollowup.getResponsable().getId());
    }
}
