package com.jss.osiris.modules.invoicing.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.invoicing.model.InvoiceItem;

public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Integer> {
}