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
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.service.AccountingAccountClassService;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingJournalService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;

import springfox.documentation.annotations.ApiIgnore;

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

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    AccountingJournalService accountingJournalService;

    @GetMapping(inputEntryPoint + "/accounting-journals")
    @ApiIgnore
    public ResponseEntity<List<AccountingJournal>> getAccountingJournals() {
        List<AccountingJournal> accountingJournals = null;
        try {
            accountingJournals = accountingJournalService.getAccountingJournals();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingJournal", e);
            return new ResponseEntity<List<AccountingJournal>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingJournal", e);
            return new ResponseEntity<List<AccountingJournal>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingJournal>>(accountingJournals, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-journal")
    @ApiIgnore
    public ResponseEntity<AccountingJournal> addOrUpdateAccountingJournal(
            @RequestBody AccountingJournal accountingJournals) {
        AccountingJournal outAccountingJournal;
        try {
            if (accountingJournals.getId() != null)
                validationHelper.validateReferential(accountingJournals, true);
            validationHelper.validateString(accountingJournals.getCode(), true);
            validationHelper.validateString(accountingJournals.getLabel(), true);

            outAccountingJournal = accountingJournalService
                    .addOrUpdateAccountingJournal(accountingJournals);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingJournal", e);
            return new ResponseEntity<AccountingJournal>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingJournal", e);
            return new ResponseEntity<AccountingJournal>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<AccountingJournal>(outAccountingJournal, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records")
    @ApiIgnore
    public ResponseEntity<List<AccountingRecord>> getAccountingRecords() {
        List<AccountingRecord> accountingRecords = null;
        try {
            accountingRecords = accountingRecordService.getAccountingRecords();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(accountingRecords, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-record")
    @ApiIgnore
    public ResponseEntity<AccountingRecord> addOrUpdateAccountingRecord(
            @RequestBody AccountingRecord accountingRecords) {
        AccountingRecord outAccountingRecord;
        try {
            if (accountingRecords.getId() != null)
                validationHelper.validateReferential(accountingRecords, true);
            validationHelper.validateString(accountingRecords.getLabel(), true, 100);
            accountingRecords.setAccountingDateTime(null);
            accountingRecords.setOperationDateTime(null);
            accountingRecords.setOperationId(null);
            validationHelper.validateString(accountingRecords.getManualAccountingDocumentNumber(), false, 150);

            if (accountingRecords.getCreditAmount() != null && accountingRecords.getCreditAmount() != 0
                    && accountingRecords.getDebitAmount() != null && accountingRecords.getDebitAmount() != 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            validationHelper.validateReferential(accountingRecords.getAccountingAccount(), true);
            validationHelper.validateReferential(accountingRecords.getInvoice(), false);
            validationHelper.validateReferential(accountingRecords.getInvoice(), false);
            validationHelper.validateReferential(accountingRecords.getAccountingJournal(), true);

            outAccountingRecord = accountingRecordService
                    .addOrUpdateAccountingRecord(accountingRecords);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingRecord", e);
            return new ResponseEntity<AccountingRecord>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingRecord", e);
            return new ResponseEntity<AccountingRecord>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<AccountingRecord>(outAccountingRecord, HttpStatus.OK);
    }

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
            validationHelper.validateString(accountingAccountClasses.getCode(), true, 20);
            validationHelper.validateString(accountingAccountClasses.getLabel(), true, 100);

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
            validationHelper.validateString(accountingAccounts.getLabel(), true, 100);
            validationHelper.validateString(accountingAccounts.getAccountingAccountNumber(), true, 3);
            validationHelper.validateInteger(accountingAccounts.getAccountingAccountSubNumber(), false);
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
            accountingAccounts = accountingAccountService.getAccountingAccountByLabelOrCode(label);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching city", e);
            return new ResponseEntity<List<AccountingAccount>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching city", e);
            return new ResponseEntity<List<AccountingAccount>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingAccount>>(accountingAccounts, HttpStatus.OK);
    }

    // TODO : to protect !
    @GetMapping(inputEntryPoint + "/accounting/close/daily")
    public ResponseEntity<Boolean> dailyAccountClosing() {
        try {
            accountingRecordService.dailyAccountClosing();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching city", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching city", e);
            return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

}