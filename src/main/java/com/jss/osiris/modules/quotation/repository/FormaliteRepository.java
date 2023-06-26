package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.quotation.model.Formalite;

public interface FormaliteRepository extends QueryCacheCrudRepository<Formalite, Integer> {

    @Query("select f from Formalite f  join f.formaliteStatus s where  f.competentAuthorityServiceProvider is not null and f.competentAuthorityServiceProvider=:competentAuthorityInpi and s.isCloseState = false")
    List<Formalite> getFormaliteForGURefresh(
            @Param("competentAuthorityInpi") CompetentAuthority competentAuthorityInpi);
}