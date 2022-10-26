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
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountBinome;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.repository.AccountingAccountRepository;
import com.jss.osiris.modules.miscellaneous.model.BillingType;

@Service
public class AccountingAccountServiceImpl implements AccountingAccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountingAccountServiceImpl.class);

    @Autowired
    AccountingAccountRepository accountingAccountRepository;

    @Autowired
    AccountingAccountClassService accountingAccountClassService;

    @Value("${accounting.account.number.customer}")
    private String customerAccountingAccountNumber;

    @Value("${accounting.account.number.deposit}")
    private String depositAccountingAccountNumber;

    @Value("${accounting.account.number.provider}")
    private String providerAccountingAccountNumber;

    @Value("${accounting.account.number.product}")
    private String productAccountingAccountNumber;

    @Value("${accounting.account.number.charge}")
    private String chargeAccountingAccountNumber;

    @Value("${accounting.account.number.bank}")
    String bankAccountingAccountNumber;

    @Value("${accounting.account.number.waiting}")
    String waitingAccountingAccountNumber;

    @Value("${accounting.account.number.profit}")
    String profitAccountingAccountNumber;

    @Value("${accounting.account.number.lost}")
    String lostAccountingAccountNumber;

    @Override
    public List<AccountingAccount> getAccountingAccounts() {
        return IterableUtils.toList(accountingAccountRepository.findAll());
    }

    @Override
    public AccountingAccount getAccountingAccount(Integer id) {
        Optional<AccountingAccount> accountingAccount = accountingAccountRepository.findById(id);
        if (accountingAccount.isPresent())
            return accountingAccount.get();
        return null;
    }

    @Override
    public List<AccountingAccount> getAccountingAccountByAccountingAccountNumber(String accountingAccountNumber) {
        return accountingAccountRepository.findByAccountingAccountNumber(accountingAccountNumber);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountingAccount addOrUpdateAccountingAccountFromUser(AccountingAccount accountingAccount)
            throws Exception {
        return addOrUpdateAccountingAccount(accountingAccount);
    }

    @Override
    public AccountingAccount addOrUpdateAccountingAccount(
            AccountingAccount accountingAccount) throws Exception {
        AccountingAccountClass accountingAccountClass = accountingAccountClassService
                .getAccountingAccountClassByCode(accountingAccount.getAccountingAccountNumber().substring(0, 1));
        if (accountingAccountClass == null) {
            logger.error(
                    "Unable to find accountingAccountClass for number "
                            + accountingAccount.getAccountingAccountNumber().substring(0, 1));
            throw new Exception(
                    "Unable to find accountingAccountClass for number "
                            + accountingAccount.getAccountingAccountNumber().substring(0, 1));
        }
        accountingAccount.setAccountingAccountClass(accountingAccountClass);
        return accountingAccountRepository.save(accountingAccount);
    }

    @Override
    public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label) {
        return accountingAccountRepository.findByLabelOrCodeContainingIgnoreCase(label);
    }

    @Override
    public AccountingAccountTrouple generateAccountingAccountsForEntity(String label) throws Exception {

        AccountingAccountTrouple accountingAccountTrouple = new AccountingAccountTrouple();

        Integer currentMaxSubAccountCustomer = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(StringUtils.leftPad(customerAccountingAccountNumber, 3, "0"));
        Integer currentMaxSubAccountProvider = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(StringUtils.leftPad(providerAccountingAccountNumber, 3, "0"));
        Integer currentMaxSubAccountDeposit = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(StringUtils.leftPad(depositAccountingAccountNumber, 3, "0"));

        Integer maxSubAccount = 0;

        if (currentMaxSubAccountCustomer != null && currentMaxSubAccountCustomer > maxSubAccount)
            maxSubAccount = currentMaxSubAccountCustomer;
        if (currentMaxSubAccountProvider != null && currentMaxSubAccountProvider > maxSubAccount)
            maxSubAccount = currentMaxSubAccountProvider;
        if (currentMaxSubAccountDeposit != null && currentMaxSubAccountDeposit > maxSubAccount)
            maxSubAccount = currentMaxSubAccountDeposit;

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
        accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount + "");
        accountingAccountProvider
                .setLabel("Fournisseur - " + (label != null ? label : ""));
        accountingAccountRepository.save(accountingAccountProvider);
        accountingAccountTrouple.setAccountingAccountProvider(accountingAccountProvider);

        AccountingAccount accountingAccountCustomer = new AccountingAccount();
        accountingAccountCustomer.setAccountingAccountClass(accountingAccountClass);
        accountingAccountCustomer.setAccountingAccountNumber(customerAccountingAccountNumber);
        accountingAccountCustomer.setAccountingAccountSubNumber(maxSubAccount + "");
        accountingAccountCustomer
                .setLabel("Client - " + (label != null ? label : ""));
        accountingAccountRepository.save(accountingAccountCustomer);
        accountingAccountTrouple.setAccountingAccountCustomer(accountingAccountCustomer);

        AccountingAccount accountingAccountDeposit = new AccountingAccount();
        accountingAccountDeposit.setAccountingAccountClass(accountingAccountClass);
        accountingAccountDeposit.setAccountingAccountNumber(depositAccountingAccountNumber);
        accountingAccountDeposit.setAccountingAccountSubNumber(maxSubAccount + "");
        accountingAccountDeposit
                .setLabel("Acompte - " + (label != null ? label : ""));
        accountingAccountRepository.save(accountingAccountDeposit);
        accountingAccountTrouple.setAccountingAccountDeposit(accountingAccountDeposit);

        return accountingAccountTrouple;
    }

    @Override
    public AccountingAccountBinome generateAccountingAccountsForBillingType(BillingType billingType) throws Exception {

        AccountingAccountBinome accountingAccountBinome = new AccountingAccountBinome();

        Integer currentMaxSubAccountProduct = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(productAccountingAccountNumber);

        Integer currentMaxSubAccountCharge = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(chargeAccountingAccountNumber);

        Integer maxSubAccount = 0;

        if (currentMaxSubAccountCharge != null && currentMaxSubAccountCharge > maxSubAccount)
            maxSubAccount = currentMaxSubAccountCharge;
        if (currentMaxSubAccountProduct != null && currentMaxSubAccountProduct > maxSubAccount)
            maxSubAccount = currentMaxSubAccountProduct;

        AccountingAccountClass accountingAccountClassCharge = accountingAccountClassService
                .getAccountingAccountClassByCode(productAccountingAccountNumber.substring(0, 1));
        if (accountingAccountClassCharge == null) {
            logger.error(
                    "Unable to find accountingAccountClass for number "
                            + productAccountingAccountNumber.substring(0, 1));
            throw new Exception(
                    "Unable to find accountingAccountClass for number "
                            + productAccountingAccountNumber.substring(0, 1));
        }

        AccountingAccountClass accountingAccountClassProduct = accountingAccountClassService
                .getAccountingAccountClassByCode(chargeAccountingAccountNumber.substring(0, 1));
        if (accountingAccountClassProduct == null) {
            logger.error(
                    "Unable to find accountingAccountClass for number "
                            + chargeAccountingAccountNumber.substring(0, 1));
            throw new Exception(
                    "Unable to find accountingAccountClass for number "
                            + chargeAccountingAccountNumber.substring(0, 1));
        }

        maxSubAccount++;

        AccountingAccount accountingAccountProduct = new AccountingAccount();
        accountingAccountProduct.setAccountingAccountClass(accountingAccountClassProduct);
        accountingAccountProduct.setAccountingAccountNumber(productAccountingAccountNumber);
        accountingAccountProduct.setAccountingAccountSubNumber(StringUtils.leftPad(maxSubAccount + "", 4, "0"));
        accountingAccountProduct
                .setLabel("Produit - " + (billingType.getLabel() != null ? billingType.getLabel() : ""));
        accountingAccountRepository.save(accountingAccountProduct);
        accountingAccountBinome.setAccountingAccountProduct(accountingAccountProduct);

        AccountingAccount accountingAccounChartCharge = new AccountingAccount();
        accountingAccounChartCharge.setAccountingAccountClass(accountingAccountClassCharge);
        accountingAccounChartCharge.setAccountingAccountNumber(chargeAccountingAccountNumber);
        accountingAccounChartCharge.setAccountingAccountSubNumber(StringUtils.leftPad(maxSubAccount + "", 4, "0"));
        accountingAccounChartCharge
                .setLabel("Charge - " + (billingType.getLabel() != null ? billingType.getLabel() : ""));
        accountingAccountRepository.save(accountingAccounChartCharge);
        accountingAccountBinome.setAccountingAccountCharge(accountingAccounChartCharge);

        return accountingAccountBinome;
    }

    @Override
    public AccountingAccount generateAccountingAccountsForProduct(String label) throws Exception {

        Integer currentMaxSubAccountProduct = accountingAccountRepository
                .findMaxSubAccontNumberForAccountNumber(StringUtils.leftPad(productAccountingAccountNumber, 3, "0"));

        Integer maxSubAccount = 0;

        if (currentMaxSubAccountProduct != null && currentMaxSubAccountProduct > maxSubAccount)
            maxSubAccount = currentMaxSubAccountProduct;

        AccountingAccountClass accountingAccountClass = accountingAccountClassService
                .getAccountingAccountClassByCode(productAccountingAccountNumber.substring(0, 1));
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
        accountingAccountProvider.setAccountingAccountNumber(productAccountingAccountNumber);
        accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount + "");
        accountingAccountProvider
                .setLabel("Produit - " + (label != null ? label : ""));
        accountingAccountRepository.save(accountingAccountProvider);

        return accountingAccountProvider;
    }

    @Override
    public AccountingAccount getBankAccountingAccount() throws Exception {
        List<AccountingAccount> bankAccountingAccounts = getAccountingAccountByAccountingAccountNumber(
                bankAccountingAccountNumber);

        if (bankAccountingAccounts == null || bankAccountingAccounts.size() == 0)
            throw new Exception("Bank accounting account not found");

        if (bankAccountingAccounts.size() > 1)
            throw new Exception("Multiple Bank accounting account found for accounting account number "
                    + bankAccountingAccountNumber);

        return bankAccountingAccounts.get(0);
    }

    @Override
    public AccountingAccount getWaitingAccountingAccount() throws Exception {
        List<AccountingAccount> waitingAccountingAccounts = getAccountingAccountByAccountingAccountNumber(
                waitingAccountingAccountNumber);

        if (waitingAccountingAccounts == null || waitingAccountingAccounts.size() == 0)
            throw new Exception("Wainting accounting account not found");

        if (waitingAccountingAccounts.size() > 1)
            throw new Exception("Multiple waiting accounting account found for accounting account number "
                    + bankAccountingAccountNumber);

        return waitingAccountingAccounts.get(0);
    }

    @Override
    public AccountingAccount getProfitAccountingAccount() throws Exception {
        List<AccountingAccount> profitAccountingAccounts = getAccountingAccountByAccountingAccountNumber(
                profitAccountingAccountNumber);

        if (profitAccountingAccounts == null || profitAccountingAccounts.size() == 0)
            throw new Exception("Profit accounting account not found");

        if (profitAccountingAccounts.size() > 1)
            throw new Exception("Multiple profit accounting account found for accounting account number "
                    + bankAccountingAccountNumber);

        return profitAccountingAccounts.get(0);
    }

    @Override
    public AccountingAccount getLostAccountingAccount() throws Exception {
        List<AccountingAccount> profitAccountingAccounts = getAccountingAccountByAccountingAccountNumber(
                lostAccountingAccountNumber);

        if (profitAccountingAccounts == null || profitAccountingAccounts.size() == 0)
            throw new Exception("Lost accounting account not found");

        if (profitAccountingAccounts.size() > 1)
            throw new Exception("Multiple lost accounting account found for accounting account number "
                    + bankAccountingAccountNumber);

        return profitAccountingAccounts.get(0);
    }
}
