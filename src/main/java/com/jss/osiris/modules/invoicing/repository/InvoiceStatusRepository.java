package com.jss.osiris.modules.invoicing.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;

public interface InvoiceStatusRepository extends QueryCacheCrudRepository<InvoiceStatus, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    InvoiceStatus findByCode(String invoiceStatusPayedCode);
}