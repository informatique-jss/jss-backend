package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountBinome;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.repository.AccountingAccountRepository;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;

@Service
public class AccountingAccountServiceImpl implements AccountingAccountService {

        @Autowired
        AccountingAccountRepository accountingAccountRepository;

        @Autowired
        AccountingAccountClassService accountingAccountClassService;

        @Autowired
        ConstantService constantService;

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
        @Transactional(rollbackFor = Exception.class)
        public AccountingAccount addOrUpdateAccountingAccountFromUser(AccountingAccount accountingAccount)
                        throws OsirisException {
                return addOrUpdateAccountingAccount(accountingAccount);
        }

        @Override
        public AccountingAccount addOrUpdateAccountingAccount(
                        AccountingAccount accountingAccount) throws OsirisException {
                return accountingAccountRepository.save(accountingAccount);
        }

        @Override
        public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label) {
                return accountingAccountRepository.findByLabelOrCodeContainingIgnoreCase(label);
        }

        @Override
        public AccountingAccountTrouple generateAccountingAccountsForEntity(String label) throws OsirisException {

                AccountingAccountTrouple accountingAccountTrouple = new AccountingAccountTrouple();

                Integer currentMaxSubAccountCustomer = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountCustomer());
                Integer currentMaxSubAccountProvider = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountProvider());
                Integer currentMaxSubAccountDeposit = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountDeposit());

                Integer maxSubAccount = 0;

                if (currentMaxSubAccountCustomer != null && currentMaxSubAccountCustomer > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountCustomer;
                if (currentMaxSubAccountProvider != null && currentMaxSubAccountProvider > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountProvider;
                if (currentMaxSubAccountDeposit != null && currentMaxSubAccountDeposit > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountDeposit;

                maxSubAccount++;

                AccountingAccount accountingAccountProvider = new AccountingAccount();
                accountingAccountProvider
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountProvider());
                accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountProvider
                                .setLabel("Fournisseur - " + (label != null ? label : ""));
                accountingAccountRepository.save(accountingAccountProvider);
                accountingAccountTrouple.setAccountingAccountProvider(accountingAccountProvider);

                AccountingAccount accountingAccountCustomer = new AccountingAccount();
                accountingAccountCustomer
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountCustomer());
                accountingAccountCustomer.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountCustomer
                                .setLabel("Client - " + (label != null ? label : ""));
                accountingAccountRepository.save(accountingAccountCustomer);
                accountingAccountTrouple.setAccountingAccountCustomer(accountingAccountCustomer);

                AccountingAccount accountingAccountDeposit = new AccountingAccount();
                accountingAccountDeposit
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountDeposit());
                accountingAccountDeposit.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountDeposit
                                .setLabel("Acompte - " + (label != null ? label : ""));
                accountingAccountRepository.save(accountingAccountDeposit);
                accountingAccountTrouple.setAccountingAccountDeposit(accountingAccountDeposit);

                return accountingAccountTrouple;
        }

        @Override
        public AccountingAccountBinome generateAccountingAccountsForBillingType(BillingType billingType)
                        throws OsirisException {

                AccountingAccountBinome accountingAccountBinome = new AccountingAccountBinome();

                Integer currentMaxSubAccountProduct = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountProduct());

                Integer currentMaxSubAccountCharge = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountCharge());

                Integer maxSubAccount = 0;

                if (currentMaxSubAccountCharge != null && currentMaxSubAccountCharge > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountCharge;
                if (currentMaxSubAccountProduct != null && currentMaxSubAccountProduct > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountProduct;

                maxSubAccount++;

                AccountingAccount accountingAccountProduct = new AccountingAccount();
                accountingAccountProduct
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountProduct());
                accountingAccountProduct.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountProduct.setLabel(
                                "Produit - " + (billingType.getLabel() != null ? billingType.getLabel() : ""));
                accountingAccountRepository.save(accountingAccountProduct);
                accountingAccountBinome.setAccountingAccountProduct(accountingAccountProduct);

                AccountingAccount accountingAccountCharge = new AccountingAccount();
                accountingAccountCharge
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountCharge());
                accountingAccountCharge.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountCharge
                                .setLabel("Charge - " + (billingType.getLabel() != null ? billingType.getLabel() : ""));
                accountingAccountRepository.save(accountingAccountCharge);
                accountingAccountBinome.setAccountingAccountCharge(accountingAccountCharge);

                return accountingAccountBinome;
        }

        @Override
        public AccountingAccount generateAccountingAccountsForProduct(String label) throws OsirisException {

                Integer currentMaxSubAccountProduct = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountProduct());

                Integer maxSubAccount = 0;

                if (currentMaxSubAccountProduct != null && currentMaxSubAccountProduct > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountProduct;

                maxSubAccount++;

                AccountingAccount accountingAccountProvider = new AccountingAccount();
                accountingAccountProvider
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountProvider());
                accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountProvider
                                .setLabel("Produit - " + (label != null ? label : ""));
                accountingAccountRepository.save(accountingAccountProvider);

                return accountingAccountProvider;
        }

        @Override
        public AccountingAccount getWaitingAccountingAccount() throws OsirisException {
                List<AccountingAccount> waitingAccountingAccounts = accountingAccountRepository
                                .findByPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountWaiting());

                if (waitingAccountingAccounts == null || waitingAccountingAccounts.size() == 0)
                        throw new OsirisException(null, "Waiting accounting account not found");

                if (waitingAccountingAccounts.size() > 1)
                        throw new OsirisException(null,
                                        "Multiple waiting accounting account found for waiting accounting account ");

                return waitingAccountingAccounts.get(0);
        }

        @Override
        public AccountingAccount getProfitAccountingAccount() throws OsirisException {
                List<AccountingAccount> waitingAccountingAccounts = accountingAccountRepository
                                .findByPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountProfit());

                if (waitingAccountingAccounts == null || waitingAccountingAccounts.size() == 0)
                        throw new OsirisException(null, "Profit accounting account not found");

                if (waitingAccountingAccounts.size() > 1)
                        throw new OsirisException(null,
                                        "Multiple waiting accounting account found for profit accounting account ");

                return waitingAccountingAccounts.get(0);
        }

        @Override
        public AccountingAccount getLostAccountingAccount() throws OsirisException {
                List<AccountingAccount> waitingAccountingAccounts = accountingAccountRepository
                                .findByPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountLost());

                if (waitingAccountingAccounts == null || waitingAccountingAccounts.size() == 0)
                        throw new OsirisException(null, "Lost accounting account not found");

                if (waitingAccountingAccounts.size() > 1)
                        throw new OsirisException(null,
                                        "Multiple waiting accounting account found for lost accounting account ");

                return waitingAccountingAccounts.get(0);
        }

        @Override
        public AccountingAccount getBankAccountingAccount() throws OsirisException {
                List<AccountingAccount> waitingAccountingAccounts = accountingAccountRepository
                                .findByPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountBank());

                if (waitingAccountingAccounts == null || waitingAccountingAccounts.size() == 0)
                        throw new OsirisException(null, "Bank accounting account not found");

                if (waitingAccountingAccounts.size() > 1)
                        throw new OsirisException(null,
                                        "Multiple waiting accounting account found for bank accounting account ");

                return waitingAccountingAccounts.get(0);
        }
}
