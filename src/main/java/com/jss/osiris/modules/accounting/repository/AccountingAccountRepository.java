package com.jss.osiris.modules.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

public interface AccountingAccountRepository extends CrudRepository<AccountingAccount, Integer> {
    List<AccountingAccount> findByLabelContainingIgnoreCase(String label);

    AccountingAccount findByAccountingAccountNumber(String code);

    @Query("select max(accountingAccountSubNumber) from AccountingAccount where  accountingAccountNumber=:accountNumber")
    Integer findMaxSubAccontNumberForAccountNumber(@Param("accountNumber") String accountNumber);
}