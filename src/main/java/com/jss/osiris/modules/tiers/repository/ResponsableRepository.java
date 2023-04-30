package com.jss.osiris.modules.tiers.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.tiers.model.Responsable;

public interface ResponsableRepository extends QueryCacheCrudRepository<Responsable, Integer> {

    Responsable findByLoginWeb(String loginWeb);
}