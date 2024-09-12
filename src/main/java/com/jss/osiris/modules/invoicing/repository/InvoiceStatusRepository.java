package com.jss.osiris.modules.invoicing.repository;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;

import jakarta.persistence.QueryHint;

public interface InvoiceStatusRepository extends QueryCacheCrudRepository<InvoiceStatus, Integer> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    InvoiceStatus findByCode(String invoiceStatusPayedCode);
}