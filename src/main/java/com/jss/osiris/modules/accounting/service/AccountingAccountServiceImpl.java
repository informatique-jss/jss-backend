package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingAccountCouple;
import com.jss.osiris.modules.accounting.repository.AccountingAccountRepository;

@Service
public class AccountingAccountServiceImpl implements AccountingAccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountingAccountServiceImpl.class);

    @Autowired
    AccountingAccountRepository accountingAccountRepository;

    @Autowired
    AccountingAccountClassService accountingAccountClassService;

    @Value("${accounting.account.number.customer}")
    private String customerAccountingAccountNumber;

    @Value("${accounting.account.number.provider}")
    private String providerAccountingAccountNumber;

    @Override
    public List<AccountingAccount> getAccountingAccounts() {
        return IterableUtils.toList(accountingAccountRepository.findAll());
    }

    @Override
    public AccountingAccount getAccountingAccount(Integer id) {
        Optional<AccountingAccount> accountingAccount = accountingAccountRepository.findById(id);
        if (!accountingAccount.isEmpty())
            return accountingAccount.get();
        return null;
    }

    @Override
    public AccountingAccount getAccountingAccountByAccountingAccountNumber(String accountingAccountNumber) {
        return accountingAccountRepository.findByAccountingAccountNumber(accountingAccountNumber);
    }

    @Override
    public AccountingAccount addOrUpdateAccountingAccount(
            AccountingAccount accountingAccount) throws Exception {
        AccountingAccountClass accountingAccountClass = accountingAccountClassService
                .getAccountingAccountClassByCode(accountingAccount.getAccountingAccountNumber().substring(0, 1));
        if (accountingAccountClass == null) {
            logger.error(
                    "Unable to find accountingAccountClass for number "
                            + customerAccountingAccountNumber.substring(0, 1));
            throw new Exception(
                    "Unable to find accountingAccountClass for number "
                            + customerAccountingAccountNumber.substring(0, 1));
        }
        accountingAccount.setAccountingAccountClass(accountingAccountClass);
        return accountingAccountRepository.save(accountingAccount);
    }

    @Override
    public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label) {
        return accountingAccountRepository.findByLabelContainingIgnoreCase(label);
    }

    /**
     * Generate provider and customer accounting accounts for an entity
     * WARNING ! accounting accounts are persisted after operation but not
     * associated to entity : it must be done in called method
     * 
     * @param label Custom label. Leave null will put account number as
     *              label
     * 
     */
    @Override
    public AccountingAccountCouple generateAccountingAccountsForEntity(String label) throws Exception {

        AccountingAccountCouple accountingAccountCouple = new AccountingAccountCouple();

        Integer currentMaxSubAccountCustomer = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(StringUtils.leftPad(customerAccountingAccountNumber, 3, "0"));
        Integer currentMaxSubAccountProvider = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(StringUtils.leftPad(providerAccountingAccountNumber, 3, "0"));

        Integer maxSubAccount = null;
        if (currentMaxSubAccountCustomer == null && currentMaxSubAccountProvider == null)
            maxSubAccount = 1;

        if (currentMaxSubAccountCustomer == null && currentMaxSubAccountProvider != null) {
            maxSubAccount = currentMaxSubAccountProvider;
        } else {
            maxSubAccount = currentMaxSubAccountProvider;
        }

        if (maxSubAccount == null)
            maxSubAccount = 0;

        AccountingAccountClass accountingAccountClass = accountingAccountClassService
                .getAccountingAccountClassByCode(customerAccountingAccountNumber.substring(0, 1));
        if (accountingAccountClass == null) {
            logger.error(
                    "Unable to find accountingAccountClass for number "
                            + customerAccountingAccountNumber.substring(0, 1));
            throw new Exception(
                    "Unable to find accountingAccountClass for number "
                            + customerAccountingAccountNumber.substring(0, 1));
        }

        maxSubAccount++;

        AccountingAccount accountingAccountProvider = new AccountingAccount();
        accountingAccountProvider.setAccountingAccountClass(accountingAccountClass);
        accountingAccountProvider.setAccountingAccountNumber(providerAccountingAccountNumber);
        accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount);
        accountingAccountProvider
                .setLabel("Fournisseur - " + (label != null ? label : "") + " - " + providerAccountingAccountNumber
                        + maxSubAccount);
        accountingAccountRepository.save(accountingAccountProvider);
        accountingAccountCouple.setAccountingAccountProvider(accountingAccountProvider);

        AccountingAccount accountingAccountCustomer = new AccountingAccount();
        accountingAccountCustomer.setAccountingAccountClass(accountingAccountClass);
        accountingAccountCustomer.setAccountingAccountNumber(customerAccountingAccountNumber);
        accountingAccountCustomer.setAccountingAccountSubNumber(maxSubAccount);
        accountingAccountCustomer
                .setLabel("Client - " + (label != null ? label : "") + " - " + customerAccountingAccountNumber
                        + maxSubAccount);
        accountingAccountRepository.save(accountingAccountCustomer);
        accountingAccountCouple.setAccountingAccountCustomer(accountingAccountCustomer);

        return accountingAccountCouple;
    }

}
