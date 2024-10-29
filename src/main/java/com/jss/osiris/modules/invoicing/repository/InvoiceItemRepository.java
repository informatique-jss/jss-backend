package com.jss.osiris.modules.invoicing.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.invoicing.model.InvoiceItem;

public interface InvoiceItemRepository extends QueryCacheCrudRepository<InvoiceItem, Integer> {
        @Modifying
        @Query(value = "delete from invoice_item where id_invoice in (select id from reprise_inpi_del)  ", nativeQuery = true)
        void deleteDuplicateInvoiceItem();

        @Modifying
        @Query(value = "update invoice_item set id_origin_provider_invoice = null where id_origin_provider_invoice  in (select id from reprise_inpi_del) ", nativeQuery = true)
        void deleteDuplicateInvoiceItemOrigin();

}