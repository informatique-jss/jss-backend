package com.jss.osiris.modules.invoicing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.jss.osiris.libs.QueryCacheCrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeInvoice;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

public interface OwncloudGreffeInvoiceRepository extends QueryCacheCrudRepository<OwncloudGreffeInvoice, Integer> {

    @Query("select i from OwncloudGreffeInvoice i where i.customerOrder=:customerOrder and i.debour is null")
    List<OwncloudGreffeInvoice> findCorrespondingGreffeInvoiceForCustomerOrder(
            @Param("customerOrder") CustomerOrder customerOrder);

    @Query("select i from OwncloudGreffeInvoice i where i.debour is null and lower(i.numero) like lower(concat('%', :numero,'%'))")
    List<OwncloudGreffeInvoice> findCorrespondingGreffeInvoiceForNumero(@Param("numero") String numero);
}