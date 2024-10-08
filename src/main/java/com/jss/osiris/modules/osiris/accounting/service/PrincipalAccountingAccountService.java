package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;

public interface PrincipalAccountingAccountService {
    public List<PrincipalAccountingAccount> getPrincipalAccountingAccounts();

    public PrincipalAccountingAccount getPrincipalAccountingAccount(Integer id);

    public PrincipalAccountingAccount addOrUpdatePrincipalAccountingAccount(
            PrincipalAccountingAccount principalAccountingAccount);

    public PrincipalAccountingAccount getPrincipalAccountingAccountByCode(String code);
}
