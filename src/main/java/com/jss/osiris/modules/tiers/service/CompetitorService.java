package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.Competitor;

public interface CompetitorService {
    public List<Competitor> getCompetitors();

    public Competitor getCompetitor(Integer id);
	
	 public Competitor addOrUpdateCompetitor(Competitor competitor);
}
