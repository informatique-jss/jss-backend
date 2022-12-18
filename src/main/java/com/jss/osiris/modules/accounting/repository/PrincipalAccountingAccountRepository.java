package com.jss.osiris.modules.accounting.repository;

import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;

import org.springframework.data.repository.CrudRepository;

public interface PrincipalAccountingAccountRepository extends CrudRepository<PrincipalAccountingAccount, Integer> {
}