package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;

public interface FormaliteRepository extends QueryCacheCrudRepository<Formalite, Integer> {

    @Query(nativeQuery = true, value = "" +
            " select f.*   " +
            " from formalite f   " +
            " join provision p on p.id_formalite  = f.id " +
            " join service s on s.id  = p.id_service " +
            " join asso_affaire_order aao on aao.id = s.id_asso_affaire_order " +
            " join customer_order co on co.id = aao.id_customer_order  " +
            " join formalite_status fs2 on fs2.id = f.id_formalite_status   " +
            " where f.id in (select id_formalite from formalite_guichet_unique fgu where id_formalite is not null) "
            +
            " and co.id_customer_order_status <> :idCustomerOrderStatusBilled ")
    List<Formalite> getFormaliteForGURefresh(@Param("idCustomerOrderStatusBilled") Integer idCustomerOrderStatusBilled);
}