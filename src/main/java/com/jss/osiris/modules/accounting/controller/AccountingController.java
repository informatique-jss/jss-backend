package com.jss.osiris.modules.accounting.controller;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.service.AccountingAccountClassService;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingJournalService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;

@RestController
public class AccountingController {

    private static final String inputEntryPoint = "/accounting";

    private static final Logger logger = LoggerFactory.getLogger(AccountingController.class);

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

    @PostMapping(inputEntryPoint + "/accounting-records/manual/add")
    public ResponseEntity<List<AccountingRecord>> addOrUpdateAccountingRecords(
            @RequestBody List<AccountingRecord> accountingRecords) {
        List<AccountingRecord> outAccountingRecords = null;
        try {
            if (accountingRecords == null || accountingRecords.size() == 0)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();
            AccountingJournal purchasesJournal = accountingJournalService.getPurchasesAccountingJournal();

            for (AccountingRecord accountingRecord : accountingRecords) {
                if (accountingRecord.getId() != null
                        || accountingRecord.getAccountingDateTime() != null
                        || accountingRecord.getOperationDateTime() != null
                        || accountingRecord.getIsTemporary() != null
                        || accountingRecord.getInvoiceItem() != null
                        || accountingRecord.getInvoice() != null
                        || accountingRecord.getOperationId() != null
                        || accountingRecord.getLetteringDateTime() != null
                        || accountingRecord.getLetteringNumber() != null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

                validationHelper.validateReferential(accountingRecord.getAccountingAccount(), true);
                validationHelper.validateReferential(accountingRecord.getAccountingJournal(), true);
                validationHelper.validateDate(accountingRecord.getManualAccountingDocumentDate(), true);
                validationHelper.validateString(accountingRecord.getManualAccountingDocumentNumber(), true, 150);
                validationHelper.validateString(accountingRecord.getLabel(), true, 100);
                validationHelper.validateDate(accountingRecord.getManualAccountingDocumentDeadline(), false);

                if (accountingRecord.getAccountingJournal().getId().equals(purchasesJournal.getId())
                        || accountingRecord.getAccountingJournal().getId().equals(salesJournal.getId()))
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            outAccountingRecords = accountingRecordService.addOrUpdateAccountingRecords(accountingRecords);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingJournal", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingJournal", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(outAccountingRecords, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-journals")
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

    @GetMapping(inputEntryPoint + "/accounting-records/search/temporary")
    public ResponseEntity<List<AccountingRecord>> getAccountingRecordsByTemporaryOperationId(
            @RequestParam Integer temporaryOperationId) {
        if (temporaryOperationId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<AccountingRecord> accountingRecords = null;
        try {
            accountingRecords = accountingRecordService
                    .getAccountingRecordsByTemporaryOperationId(temporaryOperationId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(accountingRecords, HttpStatus.OK);
    }

    @DeleteMapping(inputEntryPoint + "/accounting-records/delete/temporary")
    public ResponseEntity<List<AccountingRecord>> deleteRecordsByTemporaryOperationId(
            @RequestParam Integer temporaryOperationId) {
        if (temporaryOperationId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<AccountingRecord> accountingRecords = null;
        try {
            accountingRecords = accountingRecordService.deleteRecordsByTemporaryOperationId(temporaryOperationId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(accountingRecords, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records/search")
    public ResponseEntity<List<AccountingRecord>> getAccountingRecordsByOperationId(@RequestParam Integer operationId) {
        if (operationId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<AccountingRecord> accountingRecords = null;
        try {
            accountingRecords = accountingRecordService.getAccountingRecordsByOperationId(operationId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(accountingRecords, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records/counter-part")
    public ResponseEntity<List<AccountingRecord>> doCounterPartByOperationId(@RequestParam Integer operationId) {
        if (operationId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<AccountingRecord> accountingRecords = null;
        try {
            accountingRecords = accountingRecordService.doCounterPartByOperationId(operationId);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingRecord", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(accountingRecords, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records")
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
    public ResponseEntity<AccountingRecord> addOrUpdateAccountingRecord(
            @RequestBody AccountingRecord accountingRecords) {
        AccountingRecord outAccountingRecord;
        try {
            if (accountingRecords.getIsTemporary() == null && accountingRecords.getId() != null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (accountingRecords.getIsTemporary() == false)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (accountingRecords.getId() != null)
                validationHelper.validateReferential(accountingRecords, true);
            validationHelper.validateString(accountingRecords.getLabel(), true, 100);
            accountingRecords.setAccountingDateTime(null);
            validationHelper.validateDateTimeMax(accountingRecords.getOperationDateTime(), false, LocalDateTime.now());
            accountingRecords.setOperationDateTime(null);
            accountingRecords.setOperationId(null);
            validationHelper.validateString(accountingRecords.getManualAccountingDocumentNumber(), false, 150);

            if (accountingRecords.getCreditAmount() != null && accountingRecords.getCreditAmount() != 0
                    && accountingRecords.getDebitAmount() != null && accountingRecords.getDebitAmount() != 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            validationHelper.validateDateMax(accountingRecords.getManualAccountingDocumentDate(), false,
                    accountingRecords.getOperationDateTime().toLocalDate());
            validationHelper.validateReferential(accountingRecords.getAccountingAccount(), true);
            validationHelper.validateReferential(accountingRecords.getInvoice(), false);
            validationHelper.validateReferential(accountingRecords.getInvoice(), false);
            validationHelper.validateReferential(accountingRecords.getAccountingJournal(), true);

            outAccountingRecord = accountingRecordService
                    .addOrUpdateAccountingRecordFromUser(accountingRecords);
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
            validationHelper.validateString(accountingAccounts.getAccountingAccountNumber(), true, 6);
            validationHelper.validateString(accountingAccounts.getAccountingAccountSubNumber(), true, 20);
            outAccountingAccount = accountingAccountService
                    .addOrUpdateAccountingAccountFromUser(accountingAccounts);
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PostMapping(inputEntryPoint + "/accounting-record/search")
    public ResponseEntity<List<AccountingRecord>> searchAccountingRecords(
            @RequestBody AccountingRecordSearch accountingRecordSearch) {
        List<AccountingRecord> accountingRecords;
        try {
            if (accountingRecordSearch == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (accountingRecordSearch.getAccountingAccount() == null
                    && accountingRecordSearch.getAccountingClass() == null
                    && accountingRecordSearch.getAccountingJournal() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Duration duration = Duration.between(accountingRecordSearch.getStartDate(),
                    accountingRecordSearch.getEndDate());

            if (duration.toDays() > 366)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            accountingRecords = accountingRecordService.searchAccountingRecords(accountingRecordSearch);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingRecord>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingRecord>>(accountingRecords, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/grand-livre/export")
    public ResponseEntity<byte[]> downloadGrandLivre(
            @RequestParam(name = "accountingClassId", required = false) Integer accountingClassId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        AccountingAccountClass accountingClass = null;

        if (accountingClassId != null) {
            accountingClass = accountingAccountClassService
                    .getAccountingAccountClass(accountingClassId);

            if (accountingClass == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File grandLivre = accountingRecordService.getGrandLivreExport(accountingClass, startDate, endDate);

            if (grandLivre != null) {
                data = Files.readAllBytes(grandLivre.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Grand livre - " + (accountingClass != null ? accountingClass.getLabel() + " - " : "")
                                + startDate.format(dateFormatter) + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                grandLivre.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/journal/export")
    public ResponseEntity<byte[]> downloadJournal(@RequestParam("accountingJournalId") Integer accountingJournalId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingJournalId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        AccountingJournal accountingJournal = accountingJournalService.getAccountingJournal(accountingJournalId);

        if (accountingJournal == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File grandLivre = accountingRecordService.getJournalExport(accountingJournal, startDate, endDate);

            if (grandLivre != null) {
                data = Files.readAllBytes(grandLivre.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Journal - " + accountingJournal.getLabel() + " - " + startDate.format(dateFormatter)
                                + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                grandLivre.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-account/export")
    public ResponseEntity<byte[]> downloadAccountingAccount(
            @RequestParam("accountingAccountId") Integer accountingAccountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingAccountId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        AccountingAccount accountingAccount = accountingAccountService.getAccountingAccount(accountingAccountId);

        if (accountingAccount == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File grandLivre = accountingRecordService.getAccountingAccountExport(accountingAccount, startDate, endDate);

            if (grandLivre != null) {
                data = Files.readAllBytes(grandLivre.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Compte - " + accountingAccount.getAccountingAccountNumber() + "-"
                                + accountingAccount.getAccountingAccountSubNumber() + " - "
                                + startDate.format(dateFormatter) + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                grandLivre.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/search")
    public ResponseEntity<List<AccountingBalance>> searchAccountingBalance(
            @RequestBody AccountingBalanceSearch accountingRecordSearch) {
        List<AccountingBalance> accountingBalances;
        try {
            if (accountingRecordSearch == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Duration duration = Duration.between(accountingRecordSearch.getStartDate(),
                    accountingRecordSearch.getEndDate());

            if (duration.toDays() > 366)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            validationHelper.validateString(accountingRecordSearch.getAccountingAccountNumber(), false, 3);

            if (accountingRecordSearch.getAccountingAccountNumber() != null
                    && accountingRecordSearch.getAccountingAccountNumber().length() == 0)
                accountingRecordSearch.setAccountingAccountNumber(null);

            accountingBalances = accountingRecordService.searchAccountingBalance(accountingRecordSearch);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingBalance>>(accountingBalances, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-balance/export")
    public ResponseEntity<byte[]> downloadAccountingBalance(
            @RequestParam(name = "accountingClassId", required = false) Integer accountingClassId,
            @RequestParam(name = "accountingAccountNumber", required = false) String accountingAccountNumber,
            @RequestParam(name = "accountingAccountId", required = false) Integer accountingAccountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        AccountingAccountClass accountingClass = null;

        if (accountingClassId != null) {
            accountingClass = accountingAccountClassService
                    .getAccountingAccountClass(accountingClassId);

            if (accountingClass == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File grandLivre = accountingRecordService.getAccountingBalanceExport(accountingClassId,
                    accountingAccountNumber, accountingAccountId, startDate, endDate);

            if (grandLivre != null) {
                data = Files.readAllBytes(grandLivre.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Balances - "
                                + startDate.format(dateFormatter) + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                grandLivre.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/generale/search")
    public ResponseEntity<List<AccountingBalance>> searchAccountingBalanceGenerale(
            @RequestBody AccountingBalanceSearch accountingRecordSearch) {
        List<AccountingBalance> accountingBalances;
        try {
            if (accountingRecordSearch == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Duration duration = Duration.between(accountingRecordSearch.getStartDate(),
                    accountingRecordSearch.getEndDate());

            if (duration.toDays() > 366)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            validationHelper.validateString(accountingRecordSearch.getAccountingAccountNumber(), false, 3);

            if (accountingRecordSearch.getAccountingAccountNumber() != null
                    && accountingRecordSearch.getAccountingAccountNumber().length() == 0)
                accountingRecordSearch.setAccountingAccountNumber(null);

            accountingBalances = accountingRecordService.searchAccountingBalanceGenerale(accountingRecordSearch);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingBalance>>(accountingBalances, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-balance/generale/export")
    public ResponseEntity<byte[]> downloadAccountingBalanceGenerale(
            @RequestParam(name = "accountingClassId", required = false) Integer accountingClassId,
            @RequestParam(name = "accountingAccountNumber", required = false) String accountingAccountNumber,
            @RequestParam(name = "accountingAccountId", required = false) Integer accountingAccountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        AccountingAccountClass accountingClass = null;

        if (accountingClassId != null) {
            accountingClass = accountingAccountClassService
                    .getAccountingAccountClass(accountingClassId);

            if (accountingClass == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File grandLivre = accountingRecordService.getAccountingBalanceGeneraleExport(accountingClassId,
                    accountingAccountNumber, accountingAccountId, startDate, endDate);

            if (grandLivre != null) {
                data = Files.readAllBytes(grandLivre.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Balances - "
                                + startDate.format(dateFormatter) + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                grandLivre.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/bilan")
    public ResponseEntity<List<AccountingBalanceViewTitle>> getBilan(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<AccountingBalanceViewTitle> bilan;
        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            bilan = accountingRecordService.getBilan(startDate, endDate);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalanceViewTitle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalanceViewTitle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingBalanceViewTitle>>(bilan, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/profit-lost")
    public ResponseEntity<List<AccountingBalanceViewTitle>> getProfitAndLost(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<AccountingBalanceViewTitle> profitAndLost;
        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            profitAndLost = accountingRecordService.getProfitAndLost(startDate, endDate);
        } catch (

        ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalanceViewTitle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching accountingAccount", e);
            return new ResponseEntity<List<AccountingBalanceViewTitle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AccountingBalanceViewTitle>>(profitAndLost, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/profit-lost/export")
    public ResponseEntity<byte[]> downloadProfitLost(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File profitAndLost = accountingRecordService.getProfitLostExport(startDate, endDate);

            if (profitAndLost != null) {
                data = Files.readAllBytes(profitAndLost.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Compte de résultats - "
                                + startDate.format(dateFormatter) + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                profitAndLost.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/bilan/export")
    public ResponseEntity<byte[]> downloadBilan(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        byte[] data = null;
        HttpHeaders headers = null;

        if (startDate == null || endDate == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            File bilan = accountingRecordService.getBilanExport(startDate, endDate);

            if (bilan != null) {
                data = Files.readAllBytes(bilan.toPath());

                headers = new HttpHeaders();
                headers.add("filename",
                        "SPPS - Bilan - "
                                + startDate.format(dateFormatter) + " - "
                                + endDate.format(dateFormatter) + ".xlsx");
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);
                headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                bilan.delete();

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }
}