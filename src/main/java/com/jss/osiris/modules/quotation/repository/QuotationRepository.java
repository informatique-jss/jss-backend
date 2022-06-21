package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Quotation;

public interface QuotationRepository extends CrudRepository<Quotation, Integer> {
}