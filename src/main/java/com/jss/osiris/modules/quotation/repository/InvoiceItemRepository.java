package com.jss.osiris.modules.quotation.repository;

import com.jss.osiris.modules.quotation.model.InvoiceItem;

import org.springframework.data.repository.CrudRepository;

public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Integer> {
}