package com.jss.osiris.modules.accounting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
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

        @Autowired
        ActiveDirectoryHelper activeDirectoryHelper;

        @Override
        public List<AccountingAccount> getAccountingAccounts() {
                return accountingAccountRepository.findAllAccountingAccounts(activeDirectoryHelper
                                .isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));
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
                if (accountingAccount.getId() != null) {
                        AccountingAccount currentAccountingAccount = getAccountingAccount(accountingAccount.getId());
                        if (currentAccountingAccount.getIsViewRestricted() && !activeDirectoryHelper
                                        .isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP))
                                return accountingAccount;
                }
                return addOrUpdateAccountingAccount(accountingAccount);
        }

        private AccountingAccount addOrUpdateAccountingAccount(
                        AccountingAccount accountingAccount) throws OsirisException {
                if (accountingAccount.getIsViewRestricted() == null)
                        accountingAccount.setIsViewRestricted(false);
                return accountingAccountRepository.save(accountingAccount);
        }

        @Override
        public List<AccountingAccount> getAccountingAccountByLabelOrCode(String label) {
                return accountingAccountRepository.findByLabelOrCodeContainingIgnoreCase(label);
        }

        @Override
        public AccountingAccountTrouple generateAccountingAccountsForEntity(String label, boolean isDepositForProvider)
                        throws OsirisException {

                AccountingAccountTrouple accountingAccountTrouple = new AccountingAccountTrouple();

                Integer currentMaxSubAccountCustomer = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountCustomer());
                Integer currentMaxSubAccountProvider = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountProvider());
                Integer currentMaxSubAccountLitigious = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountCustomer());
                Integer currentMaxSubAccountSuspicious = accountingAccountRepository
                                .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountCustomer());

                Integer currentMaxSubAccountDeposit = 0;
                if (!isDepositForProvider)
                        currentMaxSubAccountDeposit = accountingAccountRepository
                                        .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                        constantService.getPrincipalAccountingAccountDeposit());
                else
                        currentMaxSubAccountDeposit = accountingAccountRepository
                                        .findMaxSubAccontNumberForPrincipalAccountingAccount(
                                                        constantService.getPrincipalAccountingAccountDepositProvider());

                Integer maxSubAccount = 0;

                if (currentMaxSubAccountCustomer != null && currentMaxSubAccountCustomer > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountCustomer;
                if (currentMaxSubAccountProvider != null && currentMaxSubAccountProvider > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountProvider;
                if (currentMaxSubAccountDeposit != null && currentMaxSubAccountDeposit > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountDeposit;
                if (currentMaxSubAccountLitigious != null && currentMaxSubAccountLitigious > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountLitigious;
                if (currentMaxSubAccountSuspicious != null && currentMaxSubAccountSuspicious > maxSubAccount)
                        maxSubAccount = currentMaxSubAccountSuspicious;
                maxSubAccount++;

                AccountingAccount accountingAccountProvider = new AccountingAccount();
                accountingAccountProvider
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountProvider());
                accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountProvider
                                .setLabel("Fournisseur - " + (label != null ? label : ""));
                addOrUpdateAccountingAccount(accountingAccountProvider);
                accountingAccountTrouple.setAccountingAccountProvider(accountingAccountProvider);

                AccountingAccount accountingAccountCustomer = new AccountingAccount();
                accountingAccountCustomer
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountCustomer());
                accountingAccountCustomer.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountCustomer
                                .setLabel("Client - " + (label != null ? label : ""));
                addOrUpdateAccountingAccount(accountingAccountCustomer);
                accountingAccountTrouple.setAccountingAccountCustomer(accountingAccountCustomer);

                AccountingAccount accountingAccountLitigious = new AccountingAccount();
                accountingAccountLitigious
                                .setPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountLitigious());
                accountingAccountLitigious.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountLitigious
                                .setLabel("Compte litigieux - " + (label != null ? label : ""));
                addOrUpdateAccountingAccount(accountingAccountLitigious);
                accountingAccountTrouple.setAccountingAccountLitigious(accountingAccountLitigious);

                AccountingAccount accountingAccountSuspicious = new AccountingAccount();
                accountingAccountSuspicious
                                .setPrincipalAccountingAccount(
                                                constantService.getPrincipalAccountingAccountSuspicious());
                accountingAccountSuspicious.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountSuspicious
                                .setLabel("Compte douteux - " + (label != null ? label : ""));
                addOrUpdateAccountingAccount(accountingAccountSuspicious);
                accountingAccountTrouple.setAccountingAccountSuspicious(accountingAccountSuspicious);

                AccountingAccount accountingAccountDeposit = new AccountingAccount();
                if (!isDepositForProvider)
                        accountingAccountDeposit
                                        .setPrincipalAccountingAccount(
                                                        constantService.getPrincipalAccountingAccountDeposit());
                else
                        accountingAccountDeposit
                                        .setPrincipalAccountingAccount(
                                                        constantService.getPrincipalAccountingAccountDepositProvider());
                accountingAccountDeposit.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountDeposit
                                .setLabel("Acompte - " + (label != null ? label : ""));
                addOrUpdateAccountingAccount(accountingAccountDeposit);
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
                accountingAccountProduct.setLabel((billingType.getLabel() != null ? billingType.getLabel() : ""));
                addOrUpdateAccountingAccount(accountingAccountProduct);
                accountingAccountBinome.setAccountingAccountProduct(accountingAccountProduct);

                AccountingAccount accountingAccountCharge = new AccountingAccount();
                accountingAccountCharge
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountCharge());
                accountingAccountCharge.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountCharge
                                .setLabel((billingType.getLabel() != null ? billingType.getLabel() : ""));
                addOrUpdateAccountingAccount(accountingAccountCharge);
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
                                .setPrincipalAccountingAccount(constantService.getPrincipalAccountingAccountProduct());
                accountingAccountProvider.setAccountingAccountSubNumber(maxSubAccount);
                accountingAccountProvider
                                .setLabel("Produit - " + (label != null ? label : ""));
                addOrUpdateAccountingAccount(accountingAccountProvider);

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
        public AccountingAccount updateAccountingAccountLabel(AccountingAccount accountingAccount, String label)
                        throws OsirisException {

                if (accountingAccount != null && accountingAccount.getPrincipalAccountingAccount() != null) {
                        if (accountingAccount.getPrincipalAccountingAccount().getId()
                                        .equals(constantService.getPrincipalAccountingAccountProvider().getId()))
                                label = "Fournisseur - " + label;
                        if (accountingAccount.getPrincipalAccountingAccount().getId()
                                        .equals(constantService.getPrincipalAccountingAccountCustomer().getId()))
                                label = "Client - " + label;
                        if (accountingAccount.getPrincipalAccountingAccount().getId()
                                        .equals(constantService.getPrincipalAccountingAccountDeposit().getId())
                                        || accountingAccount.getPrincipalAccountingAccount().getId()
                                                        .equals(constantService
                                                                        .getPrincipalAccountingAccountDepositProvider()
                                                                        .getId()))
                                label = "Acompte - " + label;
                        if (accountingAccount.getPrincipalAccountingAccount().getId()
                                        .equals(constantService.getPrincipalAccountingAccountLitigious().getId()))
                                label = "Compte litigieux - " + label;
                        if (accountingAccount.getPrincipalAccountingAccount().getId()
                                        .equals(constantService.getPrincipalAccountingAccountSuspicious().getId()))
                                label = "Compte douteux - " + label;
                        accountingAccount.setLabel(label);
                        addOrUpdateAccountingAccount(accountingAccount);
                }
                return accountingAccount;
        }

        @Override
        public void deleteAccountingAccount(AccountingAccount accountingAccount) {
                accountingAccountRepository.delete(accountingAccount);
        }
}
