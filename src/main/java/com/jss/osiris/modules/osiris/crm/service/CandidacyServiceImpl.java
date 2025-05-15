package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.crm.repository.CandidacyRepository;

@Service
public class CandidacyServiceImpl implements CandidacyService {

    @Autowired
    CandidacyRepository candidacyRepository;

    @Override
    public List<Candidacy> getCandidacies() {
        return IterableUtils.toList(candidacyRepository.findAll());
    }

    @Override
    public Candidacy getCandidacy(Integer id) {
        Optional<Candidacy> candidacy = candidacyRepository.findById(id);
        if (candidacy.isPresent())
            return candidacy.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Candidacy addOrUpdateCandidacy(
            Candidacy candidacy) {
        return candidacyRepository.save(candidacy);
    }
}
