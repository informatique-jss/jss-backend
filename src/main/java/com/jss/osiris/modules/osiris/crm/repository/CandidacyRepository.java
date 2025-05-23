package com.jss.osiris.modules.osiris.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface CandidacyRepository extends QueryCacheCrudRepository<Candidacy, Integer> {
    Candidacy findByMail(Mail mail);

    @Query("select c from Candidacy c where :isDisplayTreated = true or isTreated=false")
    List<Candidacy> findByIsTrated(Boolean isDisplayTreated);
}