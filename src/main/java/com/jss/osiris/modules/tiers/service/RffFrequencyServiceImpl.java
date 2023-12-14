package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.tiers.model.RffFrequency;
import com.jss.osiris.modules.tiers.repository.RffFrequencyRepository;

@Service
public class RffFrequencyServiceImpl implements RffFrequencyService {

    @Autowired
    RffFrequencyRepository rffFrequencyRepository;

    @Override
    public List<RffFrequency> getRffFrequencies() {
        return IterableUtils.toList(rffFrequencyRepository.findAll());
    }

    @Override
    public RffFrequency getRffFrequency(Integer id) {
        Optional<RffFrequency> rffFrequency = rffFrequencyRepository.findById(id);
        if (rffFrequency.isPresent())
            return rffFrequency.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RffFrequency addOrUpdateRffFrequency(
            RffFrequency rffFrequency) {
        return rffFrequencyRepository.save(rffFrequency);
    }
}
