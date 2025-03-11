package com.jss.osiris.modules.osiris.crm.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;

public interface CommunicationPreferenceRepository extends QueryCacheCrudRepository<CommunicationPreference, Integer> {

    CommunicationPreference findByMail_Mail(String mail);
}
