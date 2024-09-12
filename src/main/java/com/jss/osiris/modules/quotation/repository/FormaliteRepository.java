package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.Formalite;

public interface FormaliteRepository extends QueryCacheCrudRepository<Formalite, Integer> {

    @Query(nativeQuery = true, value = "" +
            " select f.*  " +
            " from formalite f  " +
            " join formalite_status fs2 on fs2.id = f.id_formalite_status  " +
            " where f.id in (select id_formalite from formalite_guichet_unique fgu) and fs2.is_close_state  ")
    List<Formalite> getFormaliteForGURefresh();
}