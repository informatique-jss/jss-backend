package com.jss.osiris.modules.myjss.profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.profile.model.UserScope;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface UserScopeRepository extends QueryCacheCrudRepository<UserScope, Integer> {

    @Query(nativeQuery = true, value = "" +
            " select r.id " +
            " from responsable r " +
            " where r.id_mail = :idMail " +
            " union  " +
            " select r3.id " +
            " from responsable r2  " +
            " join tiers t on t.id = r2.id_tiers and r2.can_view_all_tiers_in_web " +
            " join responsable r3 on r3.id_tiers = t.id " +
            " where r2.id_mail = :idMail ")
    List<Integer> getPotentialUserScope(@Param("idMail") Integer idMail);

    List<UserScope> findByResponsable(Responsable responsable);

    List<UserScope> findByResponsableAndResponsableViewed(Responsable currentUser, Responsable responsableToAdd);
}