package com.jss.osiris.modules.myjss.crm.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.crm.model.WebinarParticipant;
import com.jss.osiris.modules.osiris.crm.model.Webinar;

public interface WebinarParticipantRepository extends QueryCacheCrudRepository<WebinarParticipant, Integer> {
    List<WebinarParticipant> findByWebinarsAndIsParticipating(Webinar webinar, Boolean isParticipating);
}
