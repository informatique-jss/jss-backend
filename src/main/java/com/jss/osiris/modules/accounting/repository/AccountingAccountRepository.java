package com.jss.osiris.modules.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

public interface AccountingAccountRepository extends CrudRepository<AccountingAccount, Integer> {
    @Query("select a from AccountingAccount a where   upper(cast(label as string))  like '%' || cast(upper(:label) as string) || '%' or   upper(cast(accountingAccountNumber as string))  like '%' || cast(upper(:label) as string) || '%' or  upper(cast(accountingAccountSubNumber as string))   like '%' || cast(upper(:label) as string) || '%' ")
    List<AccountingAccount> findByLabelOrCodeContainingIgnoreCase(@Param("label") String label);

    List<AccountingAccount> findByAccountingAccountNumber(String code);

    @Query("select max(accountingAccountSubNumber) from AccountingAccount where  accountingAccountNumber=:accountNumber")
    Integer findMaxSubAccontNumberForAccountNumber(@Param("accountNumber") String accountNumber);

}