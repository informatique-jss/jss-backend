package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;

public interface CandidacyService {
    public List<Candidacy> getCandidacies();

    public List<Candidacy> getCandidacies(Boolean isDisplayTreated);

    public Candidacy getCandidacy(Integer id);

    public Candidacy declareNewCandidacy(Candidacy candidacy) throws OsirisException;

    public Candidacy addOrUpdateCandidacy(Candidacy candidacy) throws OsirisException;

    public void markCandidacyAsUnTreated(Candidacy candidacy) throws OsirisException;

    public void markCandidacyAsTreated(Candidacy candidacy) throws OsirisException;
}
