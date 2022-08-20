package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

    Invoice findByCustomerOrderId(Integer customerOrderId);
}