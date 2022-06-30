package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.QuotationStatus;

public interface QuotationStatusRepository extends CrudRepository<QuotationStatus, Integer> {

    QuotationStatus findByCode(String code);
}