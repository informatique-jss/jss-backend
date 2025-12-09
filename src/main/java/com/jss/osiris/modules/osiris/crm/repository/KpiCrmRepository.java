package com.jss.osiris.modules.osiris.crm.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;

public interface KpiCrmRepository extends QueryCacheCrudRepository<KpiCrm, Integer> {

  KpiCrm findByCode(String code);

}
