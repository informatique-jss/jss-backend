package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.DebourDel;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface DebourDelRepository extends QueryCacheCrudRepository<DebourDel, Integer> {

    List<DebourDel> findByProvision(Provision provision);

    @Modifying
    @Query(nativeQuery = true, value = " update debour_del set id_invoice_item = null where id_invoice_item  in "
            + "(select id from invoice_item where id_invoice in (select distinct i.id from invoice i "
            + " join customer_order co on co.id = i.id_customer_order_for_inbound_invoice "
            + " join provision p on p.id = i.id_provision "
            + " join formalite_guichet_unique fgu on fgu.id_formalite  = p.id_formalite "
            + " join accounting_record ar on i.id = ar.id_invoice "
            + " where co.id_customer_order_status not in (13,12) "
            + " and i.id_competent_authority=1279 "
            + " and not exists (select 1 from cart c where c.id_invoice = i.id) and to_char(ar.operation_date_time, 'yyyy')>=:year  ) );")
    void deleteDuplicateDebourDel(@Param("year") String year);

}