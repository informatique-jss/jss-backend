package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;

public interface FormaliteRepository extends CrudRepository<Formalite, Integer> {
}