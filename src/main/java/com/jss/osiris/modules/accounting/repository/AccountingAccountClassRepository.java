package com.jss.osiris.modules.accounting.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.accounting.model.AccountingAccountClass;

public interface AccountingAccountClassRepository extends CrudRepository<AccountingAccountClass, Integer> {
    List<AccountingAccountClass> findByCode(String code);
}