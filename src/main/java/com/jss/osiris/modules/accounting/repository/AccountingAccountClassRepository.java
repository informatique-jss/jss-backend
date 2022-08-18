package com.jss.osiris.modules.accounting.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.accounting.model.AccountingAccountClass;

public interface AccountingAccountClassRepository extends CrudRepository<AccountingAccountClass, Integer> {
    AccountingAccountClass findByCode(String code);
}