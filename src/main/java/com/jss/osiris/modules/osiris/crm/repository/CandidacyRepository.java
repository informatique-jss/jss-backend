package com.jss.osiris.modules.osiris.crm.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface CandidacyRepository extends QueryCacheCrudRepository<Candidacy, Integer> {
    Candidacy findByMail(Mail mail);
}