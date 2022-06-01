package com.jss.jssbackend.modules.quotation.repository;

import com.jss.jssbackend.modules.quotation.model.Quotation;

import org.springframework.data.repository.CrudRepository;

public interface QuotationRepository extends CrudRepository<Quotation, Integer> {
}