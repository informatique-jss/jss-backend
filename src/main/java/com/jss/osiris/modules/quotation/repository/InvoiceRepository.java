package com.jss.osiris.modules.quotation.repository;

import com.jss.osiris.modules.quotation.model.Invoice;

import org.springframework.data.repository.CrudRepository;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {
}