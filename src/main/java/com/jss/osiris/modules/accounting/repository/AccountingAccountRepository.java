package com.jss.osiris.modules.accounting.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

public interface AccountingAccountRepository extends CrudRepository<AccountingAccount, Integer> {
    List<AccountingAccount> findByLabelOrCodeContainingIgnoreCase(String label, String code);
}