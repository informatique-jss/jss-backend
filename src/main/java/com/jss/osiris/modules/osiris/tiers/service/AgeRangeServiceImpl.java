package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.tiers.model.AgeRange;
import com.jss.osiris.modules.osiris.tiers.repository.AgeRangeRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgeRangeServiceImpl implements AgeRangeService {

    @Autowired
    AgeRangeRepository ageRangeRepository;

    @Override
    public List<AgeRange> getAgeRanges() {
        return IterableUtils.toList(ageRangeRepository.findAll());
    }

    @Override
    public AgeRange getAgeRange(Integer id) {
        Optional<AgeRange> ageRange = ageRangeRepository.findById(id);
        if (ageRange.isPresent())
            return ageRange.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgeRange addOrUpdateAgeRange(
            AgeRange ageRange) {
        return ageRangeRepository.save(ageRange);
    }
}
