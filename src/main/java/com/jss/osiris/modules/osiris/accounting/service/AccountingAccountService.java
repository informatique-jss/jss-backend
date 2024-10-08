package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountBinome;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;

public interface AccountingAccountService {
        public List<AccountingAccount> getAccountingAccounts();

        public AccountingAccount getAccountingAccount(Integer id);

        public void deleteAccountingAccount(AccountingAccount accountingAccount);

        public AccountingAccount addOrUpdateAccountingAccountFromUser(AccountingAccount accountingAccount)
                        throws OsirisException;

        public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label);

        /**
         * Generate provider and customer accounting accounts for an entity
         * WARNING ! accounting accounts are persisted after operation but not
         * associated to entity : it must be done in called method
         * 
         * @param label Custom label. Leave null will put account number as
         *              label
         * @throws OsirisException
         * 
         */
        public AccountingAccountTrouple generateAccountingAccountsForEntity(String label, boolean isDepositForProvider)
                        throws OsirisException;

        public AccountingAccountBinome generateAccountingAccountsForBillingType(BillingType billingType)
                        throws OsirisException;

        public AccountingAccount getWaitingAccountingAccount() throws OsirisException;

        public AccountingAccount generateAccountingAccountsForProduct(String label) throws OsirisException;

        public AccountingAccount updateAccountingAccountLabel(AccountingAccount accountingAccount, String label)
                        throws OsirisException;
}
