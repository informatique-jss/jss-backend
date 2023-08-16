package com.jss.osiris.modules.tiers.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.model.TiersType;

public interface TiersRepository extends QueryCacheCrudRepository<Tiers, Integer> {

        @Query(value = "select a from Tiers a where id = :idTiers and isIndividual = true")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Tiers> findByIsIndividualAndIdTiers(@Param("idTiers") Integer idTiers);

        List<Tiers> findByTiersType(TiersType tiersTypeClient);

        Tiers findByDenomination(String tiersLabel);

        @Query(nativeQuery = true, value = "select distinct t.* " +
                        " from tiers t " +
                        " left join responsable r on r.id_tiers = t.id " +
                        " left join invoice i1 on i1.id_tiers = t.id and i1.id_invoice_status = :invoiceStatusSendId " +
                        " left join invoice i2 on i2.id_responsable = r.id and i2.id_invoice_status = :invoiceStatusSendId "
                        +
                        " left join customer_order c1 on c1.id_tiers = t.id  " +
                        " left join customer_order c2 on c2.id_responsable = t.id  " +
                        " left join deposit d1 on d1.id_customer_order = c1.id and d1.is_cancelled = false " +
                        " left join deposit d2 on d2.id_customer_order = c2.id and d2.is_cancelled = false  " +
                        " where t.id_tiers_type = :tiersTypeClientId " +
                        " and coalesce(i1.id, i2.id, d1.id, d2.id) is not null ")
        List<Tiers> findAllTiersForBillingClosureReceiptSend(@Param("invoiceStatusSendId") Integer invoiceStatusSendId,
                        @Param("tiersTypeClientId") Integer tiersTypeClientId);

}