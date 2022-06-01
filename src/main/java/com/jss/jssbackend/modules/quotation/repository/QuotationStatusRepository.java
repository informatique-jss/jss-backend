package com.jss.jssbackend.modules.quotation.repository;

import com.jss.jssbackend.modules.quotation.model.QuotationStatus;

import org.springframework.data.repository.CrudRepository;

public interface QuotationStatusRepository extends CrudRepository<QuotationStatus, Integer> {
}