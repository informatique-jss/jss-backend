package com.jss.osiris.modules.osiris.invoicing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;

public interface AzureInvoiceRepository extends QueryCacheCrudRepository<AzureInvoice, Integer> {

        AzureInvoice findByInvoiceId(String invoiceId);

        @Query(nativeQuery = true, value = " " +
                        " select distinct i.* " +
                        " from azure_invoice i " +
                        " left join invoice invoice on invoice.id_azure_invoice = i.id " +
                        " where i.is_disabled = false and  invoice.id is null  and i.invoice_id like '%' || trim(upper(CAST(:invoiceId as text)))  || '%' ")
        List<AzureInvoice> findByInvoiceIdContainingAndAndInvoice(@Param("invoiceId") String invoiceId);

        List<AzureInvoice> findByCompetentAuthorityAndInvoiceId(CompetentAuthority competentAuthority,
                        String invoiceId);

        List<AzureInvoice> findByCompetentAuthorityAndInvoiceIdContainingIgnoreCase(
                        CompetentAuthority competentAuthority,
                        String invoiceId);

        List<AzureInvoice> findByIsDisabled(boolean b);
}