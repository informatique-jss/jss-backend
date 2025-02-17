package com.jss.osiris.modules.osiris.accounting.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalance;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.osiris.accounting.service.AccountingAccountClassService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingJournalService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.osiris.accounting.service.PrincipalAccountingAccountService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

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

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

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
        Integer accountingJournalId = null;

        for (AccountingRecord accountingRecord : accountingRecords) {
            if (accountingRecord.getInvoiceItem() != null
                    || accountingRecord.getInvoice() != null
                    || accountingRecord.getLetteringDateTime() != null
                    || accountingRecord.getLetteringNumber() != null)
                throw new OsirisValidationException("accountingRecords completude");

            if (accountingJournalId != null
                    && !accountingJournalId.equals(accountingRecord.getAccountingJournal().getId()))
                throw new OsirisValidationException("Not same journal");
            accountingJournalId = accountingRecord.getAccountingJournal().getId();

            validationHelper.validateReferential(accountingRecord.getAccountingAccount(), true, "getAccountingAccount");
            validationHelper.validateReferential(accountingRecord.getAccountingJournal(), true, "getAccountingJournal");
            validationHelper.validateDateTime(accountingRecord.getOperationDateTime(), true,
                    "ManualAccountingDocumentDate");
            validationHelper.validateString(accountingRecord.getManualAccountingDocumentNumber(), true, 150,
                    "ManualAccountingDocumentNumber");
            validationHelper.validateString(accountingRecord.getLabel(), true, 100, "Label");
            validationHelper.validateDate(accountingRecord.getManualAccountingDocumentDeadline(), false,
                    "ManualAccountingDocumentDeadline");
            validationHelper.validateDateMin(accountingRecord.getOperationDateTime().toLocalDate(), false,
                    constantService.getDateAccountingClosureForAll(), "ManualAccountingDocumentDate");
            validationHelper.validateDateMax(accountingRecord.getOperationDateTime().toLocalDate(), false,
                    LocalDate.now().plusDays(1), "ManualAccountingDocumentDate");

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

    @GetMapping(inputEntryPoint + "/accounting-account")
    public ResponseEntity<AccountingAccount> getAccountingAccountById(@RequestParam Integer id) {
        return new ResponseEntity<AccountingAccount>(
                accountingAccountService.getAccountingAccount(id), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/accounting/close/daily")
    public ResponseEntity<Boolean> dailyAccountClosing() throws OsirisException {
        accountingRecordService.dailyAccountClosing();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-record/search")
    public ResponseEntity<List<AccountingRecordSearchResult>> searchAccountingRecords(
            @RequestBody AccountingRecordSearch accountingRecordSearch)
            throws OsirisException {
        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getIdPayment() != null)
            return new ResponseEntity<List<AccountingRecordSearchResult>>(
                    accountingRecordService.searchAccountingRecords(accountingRecordSearch, false), HttpStatus.OK);

        if (accountingRecordSearch.getTiersId() == null) {
            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                throw new OsirisValidationException("StartDate or EndDate");
        }

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        return new ResponseEntity<List<AccountingRecordSearchResult>>(
                accountingRecordService.searchAccountingRecords(accountingRecordSearch, false), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/temporary-operation-id")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<List<AccountingRecord>> getAccountingRecordsByOperationId(
            @RequestParam Integer temporaryOperationId) throws OsirisValidationException {
        if (temporaryOperationId == null)
            throw new OsirisValidationException("temporaryOperationId");
        return new ResponseEntity<List<AccountingRecord>>(
                accountingRecordService.getAccountingRecordsByTemporaryOperationId(temporaryOperationId),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/accounting-account-id")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Number> getAccountingRecordBalanceByAccountingAccountId(
            @RequestParam Integer accountingAccountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime accountingDate)
            throws OsirisValidationException {
        if (accountingAccountId == null)
            throw new OsirisValidationException("accountingAccountId");
        return new ResponseEntity<Number>(
                accountingRecordService.getAccountingRecordBalanceByAccountingAccountId(accountingAccountId,
                        accountingDate),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/bank-transfert-total")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Number> getBankTransfertTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime accountingDate) {
        return new ResponseEntity<Number>(
                accountingRecordService.getBankTransfertTotal(accountingDate),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/refund-total")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Number> getRefundTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime accountingDate) {
        return new ResponseEntity<Number>(
                accountingRecordService.getRefundTotal(accountingDate),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/check-total")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Number> getCheckTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime accountingDate) {
        return new ResponseEntity<Number>(
                accountingRecordService.getCheckTotal(accountingDate),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/direct-debit-transfert-total")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Number> getDirectDebitTransfertTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime accountingDate) {
        return new ResponseEntity<Number>(
                accountingRecordService.getDirectDebitTransfertTotal(accountingDate),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/accounting-record/letter")
    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
    public ResponseEntity<Boolean> letterRecordsForAs400(
            @RequestParam List<Integer> recordIds)
            throws OsirisValidationException, OsirisClientMessageException, OsirisException {
        if (recordIds == null)
            throw new OsirisValidationException("recordIds");

        boolean as400Found = false;
        ArrayList<AccountingRecord> fetchRecords = new ArrayList<AccountingRecord>();
        for (Integer record : recordIds) {
            AccountingRecord fetchRecord = accountingRecordService.getAccountingRecord(record);
            if (fetchRecord == null)
                throw new OsirisValidationException("fetchRecord");
            if (fetchRecord.getLetteringNumber() != null)
                throw new OsirisValidationException("letteringNumber");
            if (fetchRecord.getIsFromAs400() != null && fetchRecord.getIsFromAs400())
                as400Found = true;
            fetchRecords.add(fetchRecord);
        }

        if (!as400Found && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ADMINISTRATEUR_GROUP)
                && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP))
            throw new OsirisValidationException("as400Found");

        return new ResponseEntity<Boolean>(
                accountingRecordService.letterRecordsForAs400(fetchRecords), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE)
    @GetMapping(inputEntryPoint + "/accounting-record/delete")
    public ResponseEntity<Boolean> deleteAccountingRecords(@RequestParam Integer accountingRecordId)
            throws OsirisValidationException, OsirisClientMessageException, OsirisException {
        AccountingRecord accountingRecord = accountingRecordService.getAccountingRecord(accountingRecordId);
        if (accountingRecord == null)
            throw new OsirisValidationException("accountingRecord");

        if (!accountingRecord.getAccountingJournal().getId()
                .equals(constantService.getAccountingJournalBilan().getId())
                && !accountingRecord.getAccountingJournal().getId()
                        .equals(constantService.getAccountingJournalSalary().getId())) {
            throw new OsirisValidationException("accountingRecord");
        }

        return new ResponseEntity<Boolean>(accountingRecordService.deleteAccountingRecords(accountingRecord),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/grand-livre/export")
    public ResponseEntity<byte[]> downloadGrandLivre(@RequestBody AccountingRecordSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getTiersId() == null) {
            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                throw new OsirisValidationException("StartDate or EndDate");
        }

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        File grandLivre = accountingRecordService.getGrandLivreExport(accountingRecordSearch);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Grand livre - "
                            + (accountingRecordSearch.getAccountingClass() != null
                                    ? accountingRecordSearch.getAccountingClass().getLabel() + " - "
                                    : "")
                            + accountingRecordSearch.getStartDate().format(dateFormatter) + " - "
                            + accountingRecordSearch.getEndDate().format(dateFormatter) + ".xlsx");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            grandLivre.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/journal/export")
    public ResponseEntity<byte[]> downloadJournal(@RequestBody AccountingRecordSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getTiersId() == null) {
            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                throw new OsirisValidationException("StartDate or EndDate");
        }

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        validationHelper.validateReferential(accountingRecordSearch.getAccountingJournal(), true, "accoutingJournal");

        File grandLivre = accountingRecordService.getJournalExport(accountingRecordSearch);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Journal - " + accountingRecordSearch.getAccountingJournal().getLabel() + " - "
                            + accountingRecordSearch.getStartDate().format(dateFormatter)
                            + " - "
                            + accountingRecordSearch.getEndDate().format(dateFormatter) + ".xlsx");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            grandLivre.delete();

        }

        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-account/export")
    public ResponseEntity<byte[]> downloadAccountingAccount(@RequestBody AccountingRecordSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getTiersId() == null) {
            if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
                throw new OsirisValidationException("StartDate or EndDate");
        }

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        validationHelper.validateReferential(accountingRecordSearch.getAccountingAccount(), true, "accoutingAccount");

        File grandLivre = accountingRecordService.getAccountingAccountExport(accountingRecordSearch);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Compte - "
                            + accountingRecordSearch.getAccountingAccount().getPrincipalAccountingAccount().getCode()
                            + accountingRecordSearch.getAccountingAccount().getAccountingAccountSubNumber() + " - "
                            + accountingRecordSearch.getStartDate().format(dateFormatter) + " - "
                            + accountingRecordSearch.getEndDate().format(dateFormatter) + ".xlsx");
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);
            headers.set("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            grandLivre.delete();

        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-closure-receipt/download")
    public ResponseEntity<byte[]> downloadBillingClosureReceipt(
            @RequestParam(value = "tiersId", required = false) Integer tiersId,
            @RequestParam(value = "responsableId", required = false) Integer responsableId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (tiersId == null && responsableId == null)
            throw new OsirisValidationException("tiersId");

        File billingClosureExport = accountingRecordService.getBillingClosureReceiptFile(tiersId, responsableId, true);

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

    @GetMapping(inputEntryPoint + "/billing-closure-receipt/send")
    public ResponseEntity<Boolean> sendBillingClosureReceipt(@RequestParam("tiersId") Integer tiersId,
            @RequestParam("responsableId") Integer responsableId)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (tiersId == null || responsableId == null)
            throw new OsirisValidationException("tiersId");

        accountingRecordService.getBillingClosureReceiptFile(tiersId, responsableId, false);

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/search")
    public ResponseEntity<List<AccountingBalance>> searchAccountingBalance(
            @RequestBody AccountingBalanceSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        if (accountingRecordSearch.getPrincipalAccountingAccounts() != null)
            for (PrincipalAccountingAccount principalAccountingAccount : accountingRecordSearch
                    .getPrincipalAccountingAccounts())
                validationHelper.validateReferential(principalAccountingAccount, false,
                        "PrincipalAccountingAccount");

        return new ResponseEntity<List<AccountingBalance>>(
                accountingRecordService.searchAccountingBalance(accountingRecordSearch), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/export")
    public ResponseEntity<byte[]> downloadAccountingBalance(@RequestBody AccountingBalanceSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        File grandLivre = accountingRecordService.getAccountingBalanceExport(accountingRecordSearch);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Balances - "
                            + accountingRecordSearch.getStartDate().format(dateFormatter) + " - "
                            + accountingRecordSearch.getEndDate().format(dateFormatter) + ".xlsx");
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

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        if (accountingRecordSearch.getPrincipalAccountingAccounts() != null)
            for (PrincipalAccountingAccount principalAccountingAccount : accountingRecordSearch
                    .getPrincipalAccountingAccounts())
                validationHelper.validateReferential(principalAccountingAccount, false,
                        "PrincipalAccountingAccount");

        return new ResponseEntity<List<AccountingBalance>>(
                accountingRecordService.searchAccountingBalanceGenerale(accountingRecordSearch), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/accounting-balance/generale/export")
    public ResponseEntity<byte[]> downloadAccountingBalanceGenerale(
            @RequestBody AccountingBalanceSearch accountingRecordSearch)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;

        if (accountingRecordSearch == null)
            throw new OsirisValidationException("accountingRecordSearch");

        if (accountingRecordSearch.getStartDate() == null || accountingRecordSearch.getEndDate() == null)
            throw new OsirisValidationException("StartDate or EndDate");

        if (accountingRecordSearch.getStartDate().getYear() != (accountingRecordSearch.getEndDate().getYear())
                && accountingRecordSearch.getStartDate().getYear() != constantService.getDateAccountingClosureForAll()
                        .getYear())
            throw new OsirisValidationException("Not in fiscal year");

        File grandLivre = accountingRecordService.getAccountingBalanceGeneraleExport(accountingRecordSearch);

        if (grandLivre != null) {
            try {
                data = Files.readAllBytes(grandLivre.toPath());
            } catch (IOException e) {
                throw new OsirisException(e, "Unable to read file " + grandLivre.toPath());
            }

            headers = new HttpHeaders();
            headers.add("filename",
                    "SPPS - Balances - "
                            + accountingRecordSearch.getStartDate().format(dateFormatter) + " - "
                            + accountingRecordSearch.getEndDate().format(dateFormatter) + ".xlsx");
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