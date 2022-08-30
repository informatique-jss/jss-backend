package com.jss.osiris.modules.invoicing.repository;

import com.jss.osiris.modules.invoicing.model.InvoiceStatus;

import org.springframework.data.repository.CrudRepository;

public interface InvoiceStatusRepository extends CrudRepository<InvoiceStatus, Integer> {
}