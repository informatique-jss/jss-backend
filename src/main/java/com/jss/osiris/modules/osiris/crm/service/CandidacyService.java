package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.Candidacy;

public interface CandidacyService {
    public List<Candidacy> getCandidacies();

    public Candidacy getCandidacy(Integer id);

    public Candidacy addOrUpdateCandidacy(Candidacy candidacy);
}
