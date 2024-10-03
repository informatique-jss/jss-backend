package com.jss.osiris.modules.osiris.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;

import jakarta.persistence.QueryHint;

public interface AccountingAccountRepository extends QueryCacheCrudRepository<AccountingAccount, Integer> {
        @Query(nativeQuery = true, value = "" +
                        "  select a.* " +
                        "  from accounting_account a  " +
                        "  join principal_accounting_account paa on paa.id = a.id_principal_accounting_account  " +
                        "  where  upper(a.label)  like '%' || upper(cast(:label as text)) || '%' or  " +
                        "  concat(paa.code, lpad(concat(accounting_account_sub_number,''),8-length(paa.code),'0')) like   upper(cast(:label as text)) || '%'   "
                        +
                        " limit 1000 ")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<AccountingAccount> findByLabelOrCodeContainingIgnoreCase(@Param("label") String label);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<AccountingAccount> findByPrincipalAccountingAccount(PrincipalAccountingAccount principalAccountingAccount);

        @Query("select a from AccountingAccount a where :canViewRestricted=true or isViewRestricted=false")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<AccountingAccount> findAllAccountingAccounts(@Param("canViewRestricted") boolean canViewRestricted);

        @Query("select max(accountingAccountSubNumber) from AccountingAccount where  principalAccountingAccount=:principalAccountingAccount")
        Integer findMaxSubAccontNumberForPrincipalAccountingAccount(
                        @Param("principalAccountingAccount") PrincipalAccountingAccount principalAccountingAccount);

}