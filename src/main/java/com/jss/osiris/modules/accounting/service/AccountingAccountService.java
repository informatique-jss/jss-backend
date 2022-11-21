package com.jss.osiris.modules.accounting.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountBinome;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.miscellaneous.model.BillingType;

public interface AccountingAccountService {
        public List<AccountingAccount> getAccountingAccounts();

        public AccountingAccount getAccountingAccount(Integer id);

        public AccountingAccount addOrUpdateAccountingAccount(AccountingAccount accountingAccount)
                        throws OsirisException;

        public AccountingAccount addOrUpdateAccountingAccountFromUser(AccountingAccount accountingAccount)
                        throws OsirisException;

        public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label);

        public List<AccountingAccount> getAccountingAccountByAccountingAccountNumber(String accountingAccountNumber);

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
        public AccountingAccountTrouple generateAccountingAccountsForEntity(String label) throws OsirisException;

        public AccountingAccountBinome generateAccountingAccountsForBillingType(BillingType billingType)
                        throws OsirisException;

        public AccountingAccount getBankAccountingAccount() throws OsirisException;

        public AccountingAccount getWaitingAccountingAccount() throws OsirisException;

        public AccountingAccount generateAccountingAccountsForProduct(String label) throws OsirisException;

        public AccountingAccount getProfitAccountingAccount() throws OsirisException;

        public AccountingAccount getLostAccountingAccount() throws OsirisException;
}
