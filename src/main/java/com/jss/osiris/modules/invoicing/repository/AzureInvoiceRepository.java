package com.jss.osiris.modules.invoicing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;

public interface AzureInvoiceRepository extends QueryCacheCrudRepository<AzureInvoice, Integer> {

    AzureInvoice findByInvoiceId(String invoiceId);

    List<AzureInvoice> findByToCheck(Boolean displayOnlyToCheck);

    @Query(nativeQuery = true, value = " " +
            " select distinct i.* " +
            " from azure_invoice i " +
            " left join invoice invoice on invoice.id_azure_invoice = i.id " +
            " join attachment a on a.id_azure_invoice = i.id " +
            " join provision p on p.id = a.id_provision " +
            " join asso_affaire_order asso on asso.id = p.id_asso_affaire_order " +
            " join customer_order c on c.id = asso.id_customer_order " +
            " where a.id_provision is not null  and c.id_customer_order_status not in (:customerOrderStatusExcluded) and invoice.id is null ")
    List<AzureInvoice> findInvoicesToMatch(
            @Param("customerOrderStatusExcluded") List<Integer> customerOrderStatusExcluded);
}