package com.jss.osiris.modules.accounting.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.accounting.service.AccountingAccountClassService;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.accounting.service.AccountingJournalService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.accounting.service.PrincipalAccountingAccountService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.tiers.service.TiersService;

@RestController
public class AccountingController {

    private static final String inputEntryPoint = "/accounting";

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

    @Autowired
    PrincipalAccountingAccountService principalAccountingAccountService;

    @Autowired
    ConstantService constantService;

    @Autowired
    TiersService tiersService;

    @GetMapping(inputEntryPoint + "/principal-accounting-accounts")
    public ResponseEntity<List<PrincipalAccountingAccount>> getPrincipalAccountingAccounts() {
        return new ResponseEntity<List<PrincipalAccountingAccount>>(
                principalAccountingAccountService.getPrincipalAccountingAccounts(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/principal-accounting-account")
    public ResponseEntity<PrincipalAccountingAccount> addOrUpdatePrincipalAccountingAccount(
            @RequestBody PrincipalAccountingAccount principalAccountingAccounts)
            throws OsirisValidationException, OsirisException {
        if (principalAccountingAccounts.getId() != null)
            validationHelper.validateReferential(principalAccountingAccounts, true, "principalAccountingAccounts");
        validationHelper.validateString(principalAccountingAccounts.getCode(), true, "code");
        validationHelper.validateString(principalAccountingAccounts.getLabel(), true, "label");
        validationHelper.validateReferential(principalAccountingAccounts.getAccountingAccountClass(), true,
                "AccountingAccountClass");

        return new ResponseEntity<PrincipalAccountingAccount>(
                principalAccountingAccountService.addOrUpdatePrincipalAccountingAccount(principalAccountingAccounts),
                HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    @PostMapping(inputEntryPoint + "/accounting-records/manual/add")
    public ResponseEntity<List<AccountingRecord>> addOrUpdateAccountingRecords(
            @RequestBody List<AccountingRecord> accountingRecords) throws OsirisValidationException, OsirisException {
        if (accountingRecords == null || accountingRecords.size() == 0)
            throw new OsirisValidationException("accountingRecords");

        AccountingJournal salesJournal = constantService.getAccountingJournalSales();
        AccountingJournal purchasesJournal = constantService.getAccountingJournalPurchases();
        AccountingJournal aNouveauJournal = constantService.getAccountingJournalANouveau();

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
                throw new OsirisValidationException("accountingRecords completude");

            validationHelper.validateReferential(accountingRecord.getAccountingAccount(), true, "getAccountingAccount");
            validationHelper.validateReferential(accountingRecord.getAccountingJournal(), true, "getAccountingJournal");
            validationHelper.validateDate(accountingRecord.getManualAccountingDocumentDate(), true,
                    "ManualAccountingDocumentDate");
            validationHelper.validateString(accountingRecord.getManualAccountingDocumentNumber(), true, 150,
                    "ManualAccountingDocumentNumber");
            validationHelper.validateString(accountingRecord.getLabel(), true, 100, "Label");
            validationHelper.validateDate(accountingRecord.getManualAccountingDocumentDeadline(), false,
                    "ManualAccountingDocumentDeadline");

            if (accountingRecord.getAccountingJournal().getId().equals(purchasesJournal.getId())
                    || accountingRecord.getAccountingJournal().getId().equals(salesJournal.getId())
                    || accountingRecord.getAccountingJournal().getId().equals(aNouveauJournal.getId()))
                throw new OsirisValidationException("accountingRecords wrong journal");
        }

        return new ResponseEntity<List<AccountingRecord>>(
                accountingRecordService.addOrUpdateAccountingRecords(accountingRecords), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-journals")
    public ResponseEntity<List<AccountingJournal>> getAccountingJournals() {
        return new ResponseEntity<List<AccountingJournal>>(accountingJournalService.getAccountingJournals(),
                HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/accounting-journal")
    public ResponseEntity<AccountingJournal> addOrUpdateAccountingJournal(
            @RequestBody AccountingJournal accountingJournals) throws OsirisValidationException, OsirisException {
        if (accountingJournals.getId() != null)
            validationHelper.validateReferential(accountingJournals, true, "accountingJournals");
        validationHelper.validateString(accountingJournals.getCode(), true, "code");
        validationHelper.validateString(accountingJournals.getLabel(), true, "label");

        return new ResponseEntity<AccountingJournal>(
                accountingJournalService.addOrUpdateAccountingJournal(accountingJournals), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records/search/temporary")
    public ResponseEntity<List<AccountingRecord>> getAccountingRecordsByTemporaryOperationId(
            @RequestParam Integer temporaryOperationId) throws OsirisValidationException {
        if (temporaryOperationId == null)
            throw new OsirisValidationException("temporaryOperationId");

        return new ResponseEntity<List<AccountingRecord>>(
                accountingRecordService.getAccountingRecordsByTemporaryOperationId(temporaryOperationId),
                HttpStatus.OK);
    }

    @DeleteMapping(inputEntryPoint + "/accounting-records/delete/temporary")
    public ResponseEntity<List<AccountingRecord>> deleteRecordsByTemporaryOperationId(
            @RequestParam Integer temporaryOperationId) throws OsirisValidationException, OsirisException {
        if (temporaryOperationId == null)
            throw new OsirisValidationException("temporaryOperationId");

        return new ResponseEntity<List<AccountingRecord>>(
                accountingRecordService.deleteRecordsByTemporaryOperationId(temporaryOperationId), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records/search")
    public ResponseEntity<List<AccountingRecord>> getAccountingRecordsByOperationId(@RequestParam Integer operationId)
            throws OsirisValidationException {
        if (operationId == null)
            throw new OsirisValidationException("operationId");

        return new ResponseEntity<List<AccountingRecord>>(
                accountingRecordService.getAccountingRecordsByOperationId(operationId), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-records/counter-part")
    public ResponseEntity<List<AccountingRecord>> doCounterPartByOperationId(@RequestParam Integer operationId)
            throws OsirisValidationException, OsirisException {
        if (operationId == null)
            throw new OsirisValidationException("operationId");

        return new ResponseEntity<List<AccountingRecord>>(
                accountingRecordService.doCounterPartByOperationId(operationId), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-account-classes")
    public ResponseEntity<List<AccountingAccountClass>> getAccountingAccountClasses() {
        return new ResponseEntity<List<AccountingAccountClass>>(
                accountingAccountClassService.getAccountingAccountClasses(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/accounting-account-class")
    public ResponseEntity<AccountingAccountClass> addOrUpdateAccountingAccountClass(
            @RequestBody AccountingAccountClass accountingAccountClasses)
            throws OsirisValidationException, OsirisException {
        if (accountingAccountClasses.getId() != null)
            validationHelper.validateReferential(accountingAccountClasses, true, "accountingAccountClasses");
        validationHelper.validateString(accountingAccountClasses.getCode(), true, 20, "code");
        validationHelper.validateString(accountingAccountClasses.getLabel(), true, 100, "label");

        return new ResponseEntity<AccountingAccountClass>(
                accountingAccountClassService.addOrUpdateAccountingAccountClass(accountingAccountClasses),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-accounts")
    public ResponseEntity<List<AccountingAccount>> getAccountingAccounts() {
        return new ResponseEntity<List<AccountingAccount>>(accountingAccountService.getAccountingAccounts(),
                HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/accounting-account")
    public ResponseEntity<AccountingAccount> addOrUpdateAccountingAccount(
            @RequestBody AccountingAccount accountingAccounts) throws OsirisValidationException, OsirisException {
        if (accountingAccounts.getId() != null)
            validationHelper.validateReferential(accountingAccounts, true, "accountingAccounts");
        validationHelper.validateString(accountingAccounts.getLabel(), true, 200, "label");
        validationHelper.validateReferential(accountingAccounts.getPrincipalAccountingAccount(), true,
                "PrincipalAccountingAccount");

        return new ResponseEntity<AccountingAccount>(
                accountingAccountService.addOrUpdateAccountingAccountFromUser(accountingAccounts), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-account/search")
    public ResponseEntity<List<AccountingAccount>> getAccountingAccountByLabel(@RequestParam String label) {
        return new ResponseEntity<List<AccountingAccount>>(
                accountingAccountService.getAccountingAccountByLabelOrCode(label), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/accounting/close/daily")
    public ResponseEntity<Boolean> dailyAccountClosing() {
        accountingRecordService.dailyAccountClosing();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-record/search")
    public ResponseEntity<List<AccountingRecordSearchResult>> searchAccountingRecords(
            @RequestBody AccountingRecordSearch accountingRecordSearch) throws OsirisValidationException {
        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getAccountingAccount() == null
                && accountingRecordSearch.getAccountingClass() == null
                && accountingRecordSearch.getAccountingJournal() == null
                && accountingRecordSearch.getResponsableId() == null
                && accountingRecordSearch.getConfrereId() == null
                && accountingRecordSearch.getTiersId() == null)
            throw new OsirisValidationException("AccountingAccount or AccountingClass or AccountingJournal");

        if (accountingRecordSearch.getResponsableId() == null && accountingRecordSearch.getTiersId() == null
                && accountingRecordSearch.getConfrereId() == null) {
            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                throw new OsirisValidationException("StartDate or EndDate");

            Duration duration = Duration.between(accountingRecordSearch.getStartDate(),
                    accountingRecordSearch.getEndDate());

            if (duration.toDays() > 366)
                throw new OsirisValidationException("Duration");
        }

        return new ResponseEntity<List<AccountingRecordSearchResult>>(
                accountingRecordService.searchAccountingRecords(accountingRecordSearch), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/grand-livre/export")
    public ResponseEntity<byte[]> downloadGrandLivre(
            @RequestParam(name = "accountingClassId", required = false) Integer accountingClassId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        AccountingAccountClass accountingClass = null;

        if (accountingClassId != null) {
            accountingClass = accountingAccountClassService
                    .getAccountingAccountClass(accountingClassId);

            if (accountingClass == null)
                throw new OsirisValidationException("accountingClass");
        }

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");
        File grandLivre = accountingRecordService.getGrandLivreExport(accountingClass, startDate, endDate);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

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
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/journal/export")
    public ResponseEntity<byte[]> downloadJournal(@RequestParam("accountingJournalId") Integer accountingJournalId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingJournalId == null)
            throw new OsirisValidationException("accountingJournalId");

        AccountingJournal accountingJournal = accountingJournalService.getAccountingJournal(accountingJournalId);

        if (accountingJournal == null)
            throw new OsirisValidationException("accountingJournal");

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");

        File grandLivre = accountingRecordService.getJournalExport(accountingJournal, startDate, endDate);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

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

        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-account/export")
    public ResponseEntity<byte[]> downloadAccountingAccount(
            @RequestParam("accountingAccountId") Integer accountingAccountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingAccountId == null)
            throw new OsirisValidationException("accountingAccountId");

        AccountingAccount accountingAccount = accountingAccountService.getAccountingAccount(accountingAccountId);

        if (accountingAccount == null)
            throw new OsirisValidationException("accountingAccount");

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");

        File grandLivre = accountingRecordService.getAccountingAccountExport(accountingAccount, startDate, endDate);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Compte - " + accountingAccount.getPrincipalAccountingAccount().getCode()
                            + accountingAccount.getAccountingAccountSubNumber() + " - "
                            + startDate.format(dateFormatter) + " - "
                            + endDate.format(dateFormatter) + ".xlsx");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            grandLivre.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-closure-receipt/download")
    public ResponseEntity<byte[]> downloadBillingClosureReceiptV2(@RequestParam("tiersId") Integer tiersId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (tiersId == null)
            throw new OsirisValidationException("tiersId");

        File billingClosureExport = accountingRecordService.getBillingClosureReceiptFile(tiersId, true);

        if (billingClosureExport != null) {
            try {
                data = Files.readAllBytes(billingClosureExport.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + billingClosureExport.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Relevé de comptes - "
                            + LocalDate.now().format(dateFormatter) + ".pdf");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/pdf");

            billingClosureExport.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/search")
    public ResponseEntity<List<AccountingBalance>> searchAccountingBalance(
            @RequestBody AccountingBalanceSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(accountingRecordSearch.getStartDate(),
                accountingRecordSearch.getEndDate());

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");

        validationHelper.validateReferential(accountingRecordSearch.getPrincipalAccountingAccount(), false,
                "PrincipalAccountingAccount");

        return new ResponseEntity<List<AccountingBalance>>(
                accountingRecordService.searchAccountingBalance(accountingRecordSearch), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-balance/export")
    public ResponseEntity<byte[]> downloadAccountingBalance(
            @RequestParam(name = "accountingClassId", required = false) Integer accountingClassId,
            @RequestParam(name = "principalAccountingAccountId", required = false) Integer principalAccountingAccountId,
            @RequestParam(name = "accountingAccountId", required = false) Integer accountingAccountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        AccountingAccountClass accountingClass = null;

        if (accountingClassId != null) {
            accountingClass = accountingAccountClassService
                    .getAccountingAccountClass(accountingClassId);

            if (accountingClass == null)
                throw new OsirisValidationException("accountingClass");
        }

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("duration");
        File grandLivre = accountingRecordService.getAccountingBalanceExport(accountingClassId,
                principalAccountingAccountId, accountingAccountId, startDate, endDate);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

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
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/generale/search")
    public ResponseEntity<List<AccountingBalance>> searchAccountingBalanceGenerale(
            @RequestBody AccountingBalanceSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(accountingRecordSearch.getStartDate(),
                accountingRecordSearch.getEndDate());

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");

        validationHelper.validateReferential(accountingRecordSearch.getPrincipalAccountingAccount(), false,
                "PrincipalAccountingAccount");

        return new ResponseEntity<List<AccountingBalance>>(
                accountingRecordService.searchAccountingBalanceGenerale(accountingRecordSearch), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-balance/generale/export")
    public ResponseEntity<byte[]> downloadAccountingBalanceGenerale(
            @RequestParam(name = "accountingClassId", required = false) Integer accountingClassId,
            @RequestParam(name = "principalAccountingAccountId", required = false) Integer principalAccountingAccountId,
            @RequestParam(name = "accountingAccountId", required = false) Integer accountingAccountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        AccountingAccountClass accountingClass = null;

        if (accountingClassId != null) {
            accountingClass = accountingAccountClassService
                    .getAccountingAccountClass(accountingClassId);

            if (accountingClass == null)
                throw new OsirisValidationException("accountingClass");
        }

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");
        File grandLivre = accountingRecordService.getAccountingBalanceGeneraleExport(accountingClassId,
                principalAccountingAccountId, accountingAccountId, startDate, endDate);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

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
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/bilan")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<List<AccountingBalanceViewTitle>> getBilan(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException {

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");

        return new ResponseEntity<List<AccountingBalanceViewTitle>>(
                accountingRecordService.getBilan(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/profit-lost")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<List<AccountingBalanceViewTitle>> getProfitAndLost(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException {

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");

        return new ResponseEntity<List<AccountingBalanceViewTitle>>(
                accountingRecordService.getProfitAndLost(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/profit-lost/export")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<byte[]> downloadProfitLost(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");
        File profitAndLost = accountingRecordService.getProfitLostExport(startDate, endDate);

        if (profitAndLost != null) {
            try {
                data = Files.readAllBytes(profitAndLost.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + profitAndLost.toPath());
            }

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
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/bilan/export")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<byte[]> downloadBilan(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (startDate == null || endDate == null)
            throw new OsirisValidationException("StartDate or EndDate");

        Duration duration = Duration.between(startDate, endDate);

        if (duration.toDays() > 366)
            throw new OsirisValidationException("Duration");
        File bilan = accountingRecordService.getBilanExport(startDate, endDate);

        if (bilan != null) {
            try {
                data = Files.readAllBytes(bilan.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + bilan.toPath());
            }

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
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }
}