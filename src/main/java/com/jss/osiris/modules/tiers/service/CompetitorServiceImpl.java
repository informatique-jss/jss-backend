package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.Competitor;
import com.jss.osiris.modules.tiers.repository.CompetitorRepository;

@Service
public class CompetitorServiceImpl implements CompetitorService {

    @Autowired
    CompetitorRepository competitorRepository;

    @Override
    public List<Competitor> getCompetitors() {
        return IterableUtils.toList(competitorRepository.findAll());
    }

    @Override
    public Competitor getCompetitor(Integer id) {
        Optional<Competitor> competitor = competitorRepository.findById(id);
        if (competitor.isPresent())
            return competitor.get();
        return null;
    }

    @Override
    public Competitor addOrUpdateCompetitor(
            Competitor competitor) {
        return competitorRepository.save(competitor);
    }
}
