package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.RffFrequency;

public interface RffFrequencyService {
    public List<RffFrequency> getRffFrequencies();

    public RffFrequency getRffFrequency(Integer id);

    public RffFrequency addOrUpdateRffFrequency(RffFrequency rffFrequency);
}
