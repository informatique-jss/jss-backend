package com.jss.osiris.modules.osiris.profile.repository;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.profile.model.DailyConnexion;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface DailyConnexionRepository extends QueryCacheCrudRepository<DailyConnexion, Integer> {

    List<DailyConnexion> findByConnexionDate(LocalDate day);

    DailyConnexion findByResponsableAndConnexionDate(Responsable responsable, LocalDate now);

}