package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.AgeRange;

public interface AgeRangeService {
    public List<AgeRange> getAgeRanges();

    public AgeRange getAgeRange(Integer id);

    public AgeRange addOrUpdateAgeRange(AgeRange ageRange);
}
