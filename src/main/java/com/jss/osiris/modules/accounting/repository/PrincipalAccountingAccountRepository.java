package com.jss.osiris.modules.accounting.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;

public interface PrincipalAccountingAccountRepository extends CrudRepository<PrincipalAccountingAccount, Integer> {

    PrincipalAccountingAccount findByCode(String code);
}