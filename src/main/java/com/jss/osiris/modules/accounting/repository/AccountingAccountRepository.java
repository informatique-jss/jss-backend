package com.jss.osiris.modules.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;

public interface AccountingAccountRepository extends CrudRepository<AccountingAccount, Integer> {
    @Query("select a from AccountingAccount a join a.principalAccountingAccount p where   upper(cast(a.label as string))  like '%' || cast(upper(:label) as string) || '%' or p.code||a.accountingAccountSubNumber like '%' || cast(upper(:label) as string) || '%' or p.code||'-' ||a.accountingAccountSubNumber like '%' || cast(upper(:label) as string) || '%' ")
    List<AccountingAccount> findByLabelOrCodeContainingIgnoreCase(@Param("label") String label);

    List<AccountingAccount> findByPrincipalAccountingAccount(PrincipalAccountingAccount principalAccountingAccount);

    @Query("select a from AccountingAccount a where :canViewRestricted=true or isViewRestricted=false")
    List<AccountingAccount> findAllAccountingAccounts(@Param("canViewRestricted") boolean canViewRestricted);

    @Query("select max(accountingAccountSubNumber) from AccountingAccount where  principalAccountingAccount=:principalAccountingAccount")
    Integer findMaxSubAccontNumberForPrincipalAccountingAccount(
            @Param("principalAccountingAccount") PrincipalAccountingAccount principalAccountingAccount);

}