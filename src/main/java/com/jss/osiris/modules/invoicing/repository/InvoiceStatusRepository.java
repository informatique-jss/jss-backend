package com.jss.osiris.modules.invoicing.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.invoicing.model.InvoiceStatus;

public interface InvoiceStatusRepository extends CrudRepository<InvoiceStatus, Integer> {

    InvoiceStatus findByCode(String invoiceStatusPayedCode);
}