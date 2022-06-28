package com.jss.osiris.modules.accounting.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.service.AccountingAccountClassService;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;

@RestController
public class AccountingController {

    private static final String inputEntryPoint = "/accounting";

    private static final Logger logger = LoggerFactory.getLogger(AccountingController.class);

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    AccountingAccountClassService accountingAccountClassService;

    @GetMapping(inputEntryPoint + "/accounting-account-classes")
    public ResponseEntity<List<AccountingAccountClass>> getAccountingAccountClasses() {
        List<AccountingAccountClass> accountingAccountClasses = null;
        try {
            accountingAccountClasses = accountingAccountClassService.getAccountingAccountClasses();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccountClass", e);
            return new ResponseEntity<List<AccountingAccountClass>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccountClass", e);
            return new ResponseEntity<List<AccountingAccountClass>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingAccountClass>>(accountingAccountClasses, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-account-class")
    public ResponseEntity<AccountingAccountClass> addOrUpdateAccountingAccountClass(
            @RequestBody AccountingAccountClass accountingAccountClasses) {
        AccountingAccountClass outAccountingAccountClass;
        try {
            if (accountingAccountClasses.getId() != null)
                validationHelper.validateReferential(accountingAccountClasses, true);
            validationHelper.validateString(accountingAccountClasses.getCode(), true);
            validationHelper.validateString(accountingAccountClasses.getLabel(), true);

            outAccountingAccountClass = accountingAccountClassService
                    .addOrUpdateAccountingAccountClass(accountingAccountClasses);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccountClass", e);
            return new ResponseEntity<AccountingAccountClass>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccountClass", e);
            return new ResponseEntity<AccountingAccountClass>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<AccountingAccountClass>(outAccountingAccountClass, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-accounts")
    public ResponseEntity<List<AccountingAccount>> getAccountingAccounts() {
        List<AccountingAccount> accountingAccounts = null;
        try {
            accountingAccounts = accountingAccountService.getAccountingAccounts();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingAccount>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingAccount>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingAccount>>(accountingAccounts, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-account")
    public ResponseEntity<AccountingAccount> addOrUpdateAccountingAccount(
            @RequestBody AccountingAccount accountingAccounts) {
        AccountingAccount outAccountingAccount;
        try {
            if (accountingAccounts.getId() != null)
                validationHelper.validateReferential(accountingAccounts, true);
            validationHelper.validateString(accountingAccounts.getCode(), true);
            validationHelper.validateString(accountingAccounts.getLabel(), true);
            validationHelper.validateString(accountingAccounts.getAccountingAccountNumber(), true, 20);
            validationHelper.validateReferential(accountingAccounts.getVat(), false);
            validationHelper.validateReferential(accountingAccounts.getAccountingAccountClass(), true);
            outAccountingAccount = accountingAccountService
                    .addOrUpdateAccountingAccount(accountingAccounts);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<AccountingAccount>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<AccountingAccount>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<AccountingAccount>(outAccountingAccount, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-account/search")
    public ResponseEntity<List<AccountingAccount>> getAccountingAccountByLabel(@RequestParam String label) {
        List<AccountingAccount> accountingAccounts = null;
        try {
            accountingAccounts = accountingAccountService.getAccountingAccountByLabel(label);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching city", e);
            return new ResponseEntity<List<AccountingAccount>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching city", e);
            return new ResponseEntity<List<AccountingAccount>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingAccount>>(accountingAccounts, HttpStatus.OK);
    }
}