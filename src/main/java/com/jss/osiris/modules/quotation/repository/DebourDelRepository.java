package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.DebourDel;
import com.jss.osiris.modules.quotation.model.Provision;

public interface DebourDelRepository extends QueryCacheCrudRepository<DebourDel, Integer> {

    List<DebourDel> findByProvision(Provision provision);

    @Modifying
    @Query(nativeQuery = true, value = " update debour_del set id_invoice_item = null where id_invoice_item  in "
            + "(select id from invoice_item where id_invoice in (select id from reprise_inpi_del)) ")
    void deleteDuplicateDebourDel();

}