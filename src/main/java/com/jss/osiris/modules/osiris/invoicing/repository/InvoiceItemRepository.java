package com.jss.osiris.modules.osiris.invoicing.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;

public interface InvoiceItemRepository extends QueryCacheCrudRepository<InvoiceItem, Integer> {
        @Modifying
        @Query(value = "delete from invoice_item where id_invoice in (select distinct i.id from invoice i "
                        + "join customer_order co on co.id = i.id_customer_order_for_inbound_invoice "
                        + " join provision p on p.id = i.id_provision "
                        + " join formalite_guichet_unique fgu on fgu.id_formalite  = p.id_formalite "
                        + "join accounting_record ar on i.id = ar.id_invoice "
                        + "where i.id_competent_authority=1279 "
                        + "and not exists (select 1 from cart c where c.id_invoice = i.id) and to_char(ar.operation_date_time, 'yyyy')>=:year );", nativeQuery = true)
        void deleteDuplicateInvoiceItem(@Param("year") String year);

        @Modifying
        @Query(value = "delete from invoice_item where id_invoice is null and id_origin_provider_invoice "
                        + "in (select distinct i.id  from invoice i "
                        + "join customer_order co on co.id = i.id_customer_order_for_inbound_invoice  "
                        + " join provision p on p.id = i.id_provision "
                        + " join formalite_guichet_unique fgu on fgu.id_formalite  = p.id_formalite "
                        + " join accounting_record ar on i.id = ar.id_invoice "
                        + " where i.id_competent_authority=1279 "
                        + "and not exists (select 1 from cart c where c.id_invoice = i.id) and to_char(ar.operation_date_time, 'yyyy')>=:year ) ;", nativeQuery = true)
        void deleteDuplicateInvoiceItemOrigin(@Param("year") String year);

}