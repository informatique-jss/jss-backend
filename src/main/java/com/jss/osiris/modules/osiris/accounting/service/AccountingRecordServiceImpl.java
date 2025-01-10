package com.jss.osiris.modules.osiris.accounting.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalance;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.osiris.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.Refund;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;
import com.jss.osiris.modules.osiris.quotation.service.ConfrereService;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

  @Autowired
  AccountingRecordRepository accountingRecordRepository;

  @Autowired
  AccountingJournalService accountingJournalService;

  @Autowired
  AccountingExportHelper accountingExportHelper;

  @Autowired
  TiersService tiersService;

  @Autowired
  ConfrereService confrereService;

  @Autowired
  ActiveDirectoryHelper activeDirectoryHelper;

  @Autowired
  BillingClosureReceiptHelper billingClosureReceiptDelegate;

  @Autowired
  CustomerMailService customerMailService;

  @Autowired
  BatchService batchService;

  @Autowired
  ConstantService constantService;

  private String ACCOUNTING_RECORD_TABLE_NAME = "accounting_record";
  private String CLOSED_ACCOUNTING_RECORD_TABLE_NAME = "closed_accounting_record";

  private String getAccountingRecordTableName(LocalDate searchedDate) throws OsirisException {
    if (searchedDate == null
        || searchedDate.getYear() >= constantService.getDateAccountingClosureForAccountant().getYear())
      return this.ACCOUNTING_RECORD_TABLE_NAME;
    return this.CLOSED_ACCOUNTING_RECORD_TABLE_NAME;
  }

  @Autowired
  AccountingRecordGenerationService accountingRecordGenerationService;

  @Override
  public AccountingRecord getAccountingRecord(Integer id) {
    Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
    if (accountingRecord.isPresent())
      return accountingRecord.get();
    return null;
  }

  @Override
  public List<AccountingRecord> getAccountingRecordsByTemporaryOperationId(Integer temporaryOperationId) {
    return accountingRecordRepository.findByTemporaryOperationId(temporaryOperationId);
  }

  @Override
  public Number getAccountingRecordBalanceByAccountingAccountId(Integer accountingAccountId) {
    return accountingRecordRepository.getAccountingRecordBalanceByAccountingAccountId(accountingAccountId);
  }

  @Override
  public Number getBankTransfertTotal() {
    return accountingRecordRepository.getBankTransfertTotal();
  }

  @Override
  public Number getRefundTotal() {
    return accountingRecordRepository.getRefundTotal();
  }

  @Override
  public Number getCheckTotal() {
    return accountingRecordRepository.getCheckTotal();
  }

  @Override
  public Number getDirectDebitTransfertTotal() {
    return accountingRecordRepository.getDirectDebitTransfertTotal();
  }

  @Override
  public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord,
      boolean byPassOperationDateTimeCheck) throws OsirisException {
    // Do not save null or 0 € records
    if (accountingRecord.getId() == null
        && (accountingRecord.getCreditAmount() == null
            || accountingRecord.getCreditAmount().compareTo(BigDecimal.ZERO) == 0)
        && (accountingRecord.getDebitAmount() == null
            || accountingRecord.getDebitAmount().compareTo(BigDecimal.ZERO) == 0))
      return null;

    if (!byPassOperationDateTimeCheck)
      if (accountingRecord.getOperationDateTime().toLocalDate()
          .isBefore(constantService.getDateAccountingClosureForAccountant())) {
        throw new OsirisException(null, "Impossible to write accounting record, it's before closure all date");
      } else if (accountingRecord.getOperationDateTime().toLocalDate()
          .isBefore(constantService.getDateAccountingClosureForAll())
          && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP)) {
        throw new OsirisException(null,
            "Impossible to write accounting record, it's before closure accounting date. It can only be done by accounting responsible");
      }

    return accountingRecordRepository.save(accountingRecord);
  }

  @Override
  public void deleteAccountingRecord(AccountingRecord accountingRecord) {
    if (accountingRecord.getIsTemporary() != null && accountingRecord.getIsTemporary())
      accountingRecordRepository.delete(accountingRecord);
  }

  private Integer getNewTemporaryOperationId() {
    return ThreadLocalRandom.current().nextInt(1, 1000000000);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords)
      throws OsirisException {
    Integer operationId = getNewTemporaryOperationId();
    BigDecimal balance = new BigDecimal(0f);
    for (AccountingRecord accountingRecord : accountingRecords) {

      if (accountingRecord.getCreditAmount() != null)
        balance = balance.subtract(accountingRecord.getCreditAmount());
      else
        balance = balance.add(accountingRecord.getDebitAmount());

      if (accountingRecord.getOperationDateTime() == null)
        accountingRecord.setOperationDateTime(LocalDateTime.now());
      accountingRecord.setTemporaryOperationId(operationId);
      accountingRecord.setIsTemporary(true);
      accountingRecord.setIsANouveau(false);
      accountingRecord.setIsManual(true);
      addOrUpdateAccountingRecord(accountingRecord, false);
    }

    if (balance.setScale(2, RoundingMode.HALF_EVEN).abs().compareTo(new BigDecimal(1)) >= 1)
      throw new OsirisValidationException("Balance not null");

    return accountingRecords;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void dailyAccountClosing() throws OsirisException {
    List<AccountingJournal> journals = accountingJournalService.getAccountingJournals();

    Integer maxIdOperation = accountingRecordRepository
        .findMaxIdOperationForMinOperationDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
            .with(ChronoField.HOUR_OF_DAY, 0)
            .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

    if (maxIdOperation == null)
      maxIdOperation = 0;
    maxIdOperation++;

    HashMap<Integer, Integer> definitiveIdOperation = new HashMap<Integer, Integer>();

    if (journals != null && journals.size() > 0) {
      for (AccountingJournal accountingJournal : journals) {
        List<AccountingRecord> accountingRecords = accountingRecordRepository
            .findByAccountingJournalAndIsTemporary(accountingJournal, true);

        Integer maxIdAccounting = accountingRecordRepository
            .findMaxIdAccountingForAccontingJournalAndMinOperationDateTime(
                accountingJournal,
                LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                    .with(ChronoField.HOUR_OF_DAY, 0)
                    .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));
        if (maxIdAccounting == null)
          maxIdAccounting = 0;
        maxIdAccounting++;

        if (accountingRecords != null && accountingRecords.size() > 0) {
          for (AccountingRecord accountingRecord : accountingRecords) {

            if (definitiveIdOperation.get(accountingRecord.getTemporaryOperationId()) == null) {
              definitiveIdOperation.put(accountingRecord.getTemporaryOperationId(), maxIdOperation);
              maxIdOperation++;
            }
            accountingRecord.setAccountingDateTime(LocalDateTime.now());
            accountingRecord.setAccountingId(maxIdAccounting);
            accountingRecord.setOperationId(definitiveIdOperation.get(accountingRecord.getTemporaryOperationId()));
            accountingRecord.setIsTemporary(false);
            addOrUpdateAccountingRecord(accountingRecord, true);
            maxIdAccounting++;
          }
        }
      }
    }
  }

  @Override
  public List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccount,
      Invoice invoice) {
    return accountingRecordRepository.findByAccountingAccountAndInvoice(accountingAccount, invoice);
  }

  @Override
  public List<AccountingRecord> findByAccountingAccountAndRefund(AccountingAccount accountingAccount,
      Refund refund) {
    return accountingRecordRepository.findByAccountingAccountAndRefund(accountingAccount, refund);
  }

  @Override
  public List<AccountingRecord> findByAccountingAccountAndBankTransfert(AccountingAccount accountingAccount,
      BankTransfert bankTransfert) {
    return accountingRecordRepository.findByAccountingAccountAndBankTransfert(accountingAccount, bankTransfert);
  }

  @Override
  public Integer findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime minLetteringDateTime) {
    return accountingRecordRepository.findMaxLetteringNumberForMinLetteringDateTime(minLetteringDateTime);
  }

  @Override
  public void deleteDuplicateAccountingRecord() {
    accountingRecordRepository.deleteDuplicateAccountingRecord();

  }

  @Override
  public List<AccountingRecordSearchResult> searchAccountingRecords(AccountingRecordSearch accountingRecordSearch,
      boolean fetchAll) throws OsirisException {
    ArrayList<Integer> accountingAccountId = new ArrayList<Integer>();
    if (accountingRecordSearch.getAccountingAccount() != null) {
      accountingAccountId.add(accountingRecordSearch.getAccountingAccount().getId());
    } else {
      accountingAccountId.add(0);
    }

    Integer journalId = 0;
    if (accountingRecordSearch.getAccountingJournal() != null)
      journalId = accountingRecordSearch.getAccountingJournal().getId();

    Integer accountingClass = 0;
    if (accountingRecordSearch.getAccountingClass() != null)
      accountingClass = accountingRecordSearch.getAccountingClass().getId();

    if (accountingRecordSearch.getHideLettered() == null)
      accountingRecordSearch.setHideLettered(false);

    if (accountingRecordSearch.getIsFromAs400() == null)
      accountingRecordSearch.setIsFromAs400(false);

    if (accountingRecordSearch.getIsManual() == null)
      accountingRecordSearch.setIsManual(false);

    if (accountingRecordSearch.getTiersId() == null)
      accountingRecordSearch.setTiersId(0);

    if (accountingRecordSearch.getStartDate() == null)
      accountingRecordSearch.setStartDate(LocalDateTime.now().minusYears(100));
    else
      accountingRecordSearch.setStartDate(accountingRecordSearch.getStartDate().withHour(0).withMinute(0));

    if (accountingRecordSearch.getEndDate() == null)
      accountingRecordSearch.setEndDate(LocalDateTime.now().plusYears(100));
    else
      accountingRecordSearch.setEndDate(accountingRecordSearch.getEndDate().withHour(23).withMinute(59));

    if (accountingRecordSearch.getIdPayment() == null)
      accountingRecordSearch.setIdPayment(0);
    if (accountingRecordSearch.getIdBankTransfert() == null)
      accountingRecordSearch.setIdBankTransfert(0);
    if (accountingRecordSearch.getIdCustomerOrder() == null)
      accountingRecordSearch.setIdCustomerOrder(0);
    if (accountingRecordSearch.getIdInvoice() == null)
      accountingRecordSearch.setIdInvoice(0);
    if (accountingRecordSearch.getIdRefund() == null)
      accountingRecordSearch.setIdRefund(0);

    if (getAccountingRecordTableName(accountingRecordSearch.getStartDate().toLocalDate())
        .equals(this.ACCOUNTING_RECORD_TABLE_NAME)) {
      if (accountingRecordSearch.getTiersId() != 0) {
        // See all if for a Tiers
        accountingRecordSearch.setStartDate(accountingRecordSearch.getStartDate().minusYears(2));
        accountingRecordSearch.setEndDate(accountingRecordSearch.getEndDate().plusYears(2));
      }
      return accountingRecordRepository.searchAccountingRecordsCurrent(accountingAccountId, accountingClass, journalId,
          accountingRecordSearch.getTiersId(),
          accountingRecordSearch.getHideLettered(),
          accountingRecordSearch.getIsFromAs400(),
          accountingRecordSearch.getStartDate().withHour(0).withMinute(0),
          accountingRecordSearch.getEndDate().withHour(23).withMinute(59),
          activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
          accountingRecordSearch.getIdPayment(),
          accountingRecordSearch.getIdCustomerOrder(),
          accountingRecordSearch.getIdInvoice(),
          accountingRecordSearch.getIdRefund(),
          accountingRecordSearch.getIdBankTransfert(), fetchAll ? Integer.MAX_VALUE : 1000);
    }
    return accountingRecordRepository.searchAccountingRecordsClosed(accountingAccountId, accountingClass, journalId,
        accountingRecordSearch.getTiersId(),
        accountingRecordSearch.getHideLettered(),
        accountingRecordSearch.getIsFromAs400(),
        accountingRecordSearch.getStartDate().withHour(0).withMinute(0),
        accountingRecordSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
        accountingRecordSearch.getIdPayment(),
        accountingRecordSearch.getIdCustomerOrder(),
        accountingRecordSearch.getIdInvoice(),
        accountingRecordSearch.getIdRefund(),
        accountingRecordSearch.getIdBankTransfert(), fetchAll ? Integer.MAX_VALUE : 1000);
  }

  @Override
  public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch)
      throws OsirisException {
    Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
        ? accountingBalanceSearch.getAccountingAccount().getId()
        : 0;
    Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
        ? accountingBalanceSearch.getAccountingClass().getId()
        : 0;
    Integer accountingJournalId = accountingBalanceSearch.getAccountingJournal() != null
        ? accountingBalanceSearch.getAccountingJournal().getId()
        : 0;
    List<Integer> principalAccountingAccountIds = new ArrayList<Integer>();
    if (accountingBalanceSearch.getPrincipalAccountingAccounts() != null)
      for (PrincipalAccountingAccount principalAccountingAccount : accountingBalanceSearch
          .getPrincipalAccountingAccounts())
        principalAccountingAccountIds.add(principalAccountingAccount.getId());
    else
      principalAccountingAccountIds.add(0);

    if (accountingBalanceSearch.getIsFromAs400() == null)
      accountingBalanceSearch.setIsFromAs400(false);

    if (getAccountingRecordTableName(accountingBalanceSearch.getStartDate().toLocalDate())
        .equals(this.ACCOUNTING_RECORD_TABLE_NAME))
      return accountingRecordRepository.searchAccountingBalanceCurrent(
          accountingClassId, accountingJournalId,
          accountingAccountId, principalAccountingAccountIds,
          accountingBalanceSearch.getStartDate().withHour(0).withMinute(0),
          accountingBalanceSearch.getEndDate().withHour(23).withMinute(59),
          activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
          accountingBalanceSearch.getIsFromAs400());

    return accountingRecordRepository.searchAccountingBalanceClosed(
        accountingClassId, accountingJournalId,
        accountingAccountId, principalAccountingAccountIds,
        accountingBalanceSearch.getStartDate().withHour(0).withMinute(0),
        accountingBalanceSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
        accountingBalanceSearch.getIsFromAs400());
  }

  @Override
  public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingBalanceSearch)
      throws OsirisException {
    Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
        ? accountingBalanceSearch.getAccountingAccount().getId()
        : 0;
    Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
        ? accountingBalanceSearch.getAccountingClass().getId()
        : 0;
    Integer accountingJournalId = accountingBalanceSearch.getAccountingJournal() != null
        ? accountingBalanceSearch.getAccountingJournal().getId()
        : 0;
    List<Integer> principalAccountingAccountIds = new ArrayList<Integer>();
    if (accountingBalanceSearch.getPrincipalAccountingAccounts() != null)
      for (PrincipalAccountingAccount principalAccountingAccount : accountingBalanceSearch
          .getPrincipalAccountingAccounts())
        principalAccountingAccountIds.add(principalAccountingAccount.getId());
    else
      principalAccountingAccountIds.add(0);

    if (accountingBalanceSearch.getIsFromAs400() == null)
      accountingBalanceSearch.setIsFromAs400(false);

    if (getAccountingRecordTableName(accountingBalanceSearch.getStartDate().toLocalDate())
        .equals(this.ACCOUNTING_RECORD_TABLE_NAME))
      return accountingRecordRepository.searchAccountingBalanceGeneraleCurrent(
          accountingClassId, accountingJournalId,
          accountingAccountId, principalAccountingAccountIds,
          accountingBalanceSearch.getStartDate().withHour(0).withMinute(0),
          accountingBalanceSearch.getEndDate().withHour(23).withMinute(59),
          activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
          accountingBalanceSearch.getIsFromAs400());

    return accountingRecordRepository.searchAccountingBalanceGeneraleClosed(
        accountingClassId, accountingJournalId,
        accountingAccountId, principalAccountingAccountIds,
        accountingBalanceSearch.getStartDate().withHour(0).withMinute(0),
        accountingBalanceSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
        accountingBalanceSearch.getIsFromAs400());

  }

  @Override
  public File getGrandLivreExport(AccountingRecordSearch accountingRecordSearch) throws OsirisException {
    return accountingExportHelper.getGrandLivre(searchAccountingRecords(accountingRecordSearch, true),
        accountingRecordSearch.getStartDate(), accountingRecordSearch.getEndDate());
  }

  @Override
  public File getJournalExport(AccountingRecordSearch accountingRecordSearch)
      throws OsirisException {
    return accountingExportHelper.getJournal(searchAccountingRecords(accountingRecordSearch, true),
        accountingRecordSearch.getAccountingJournal(), accountingRecordSearch.getStartDate(),
        accountingRecordSearch.getEndDate());
  }

  @Override
  public File getAccountingAccountExport(AccountingRecordSearch accountingRecordSearch) throws OsirisException {
    return accountingExportHelper.getAccountingAccount(searchAccountingRecords(accountingRecordSearch, true),
        accountingRecordSearch.getAccountingAccount(), accountingRecordSearch.getStartDate(),
        accountingRecordSearch.getEndDate());
  }

  @Override
  public File getAccountingBalanceExport(AccountingBalanceSearch accountingRecordSearch)
      throws OsirisException {
    return accountingExportHelper.getBalance(searchAccountingBalance(accountingRecordSearch), false,
        accountingRecordSearch.getStartDate(), accountingRecordSearch.getEndDate());
  }

  @Override
  public File getAccountingBalanceGeneraleExport(AccountingBalanceSearch accountingRecordSearch)
      throws OsirisException {
    return accountingExportHelper.getBalance(searchAccountingBalanceGenerale(accountingRecordSearch), true,
        accountingRecordSearch.getStartDate(), accountingRecordSearch.getEndDate());
  }

  @Override
  @Transactional
  public void sendBillingClosureReceipt()
      throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
    List<Tiers> tiers = tiersService.findAllTiersForBillingClosureReceiptSend();
    if (tiers != null && tiers.size() > 0)
      for (Tiers tier : tiers) {
        batchService.declareNewBatch(Batch.SEND_BILLING_CLOSURE_RECEIPT, tier.getId());
      }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public File getBillingClosureReceiptFile(Integer tiersId, Integer responsableId, boolean downloadFile)
      throws OsirisException, OsirisClientMessageException, OsirisValidationException {
    return billingClosureReceiptDelegate.getBillingClosureReceiptFile(tiersId, responsableId, downloadFile);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public Boolean deleteAccountingRecords(AccountingRecord inAccountingRecord) throws OsirisValidationException {
    inAccountingRecord = getAccountingRecord(inAccountingRecord.getId());
    List<AccountingRecord> accountingRecords = accountingRecordRepository
        .findByTemporaryOperationId(inAccountingRecord.getTemporaryOperationId());

    if (accountingRecords != null) {
      BigDecimal balance = new BigDecimal(0);
      for (AccountingRecord accountingRecord : accountingRecords) {
        if (accountingRecord.getCreditAmount() != null)
          balance = balance.add(accountingRecord.getCreditAmount());
        if (accountingRecord.getDebitAmount() != null)
          balance = balance.subtract(accountingRecord.getDebitAmount());
      }

      if (balance.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_EVEN).abs()
          .compareTo(new BigDecimal(1)) > 0)
        throw new OsirisValidationException("Balance not null");

      accountingRecordRepository.deleteAll(accountingRecords);
    }
    return true;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public Boolean letterRecordsForAs400(List<AccountingRecord> accountingRecords)
      throws OsirisValidationException, OsirisClientMessageException, OsirisException {

    Integer lastAccountId = null;
    BigDecimal balance = new BigDecimal(0);
    ArrayList<AccountingRecord> fetchRecords = new ArrayList<AccountingRecord>();
    Invoice invoiceToLetter = null;
    Integer countInvoice = 0;

    for (AccountingRecord record : accountingRecords) {
      AccountingRecord fetchRecord = getAccountingRecord(record.getId());
      fetchRecords.add(fetchRecord);

      if (lastAccountId != null && !lastAccountId.equals(fetchRecord.getAccountingAccount().getId()))
        throw new OsirisClientMessageException("L'ensemble des enregistrements doivent être sur le même compte");

      if (fetchRecord.getLetteringNumber() != null)
        throw new OsirisClientMessageException("Des lignes sont déjà lettrées");

      lastAccountId = fetchRecord.getAccountingAccount().getId();
      if (record.getCreditAmount() != null)
        balance = balance.add(record.getCreditAmount());
      if (record.getDebitAmount() != null)
        balance = balance.subtract(record.getDebitAmount());

      // check if there's one invoice or more in record lines, we need to set invoice
      // on record lines if there's only invoice selected before applying
      // checkInvoiceForLettrage()
      if (record.getInvoice() != null) {
        countInvoice++;
        invoiceToLetter = record.getInvoice();
      }
    }
    if (balance.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_EVEN).abs()
        .compareTo(new BigDecimal(1)) > 0)
      throw new OsirisValidationException("Balance not null");

    // Lettering
    Integer maxLetteringNumber = findMaxLetteringNumberForMinLetteringDateTime(
        LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
            .with(ChronoField.HOUR_OF_DAY, 0)
            .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

    if (maxLetteringNumber == null)
      maxLetteringNumber = 0;
    maxLetteringNumber++;

    for (AccountingRecord accountingRecord : fetchRecords) {
      if (countInvoice == 1 && accountingRecord.getInvoice() == null)
        accountingRecord.setInvoice(invoiceToLetter);
      accountingRecord.setLetteringDateTime(LocalDateTime.now());
      accountingRecord.setLetteringNumber(maxLetteringNumber);
      addOrUpdateAccountingRecord(accountingRecord, true);
    }
    if (countInvoice == 1 && invoiceToLetter != null)
      accountingRecordGenerationService.checkInvoiceForLettrage(invoiceToLetter);
    return true;
  }

  @Override
  public List<AccountingRecord> getClosedAccountingRecordsForPayment(Payment payment) {
    return accountingRecordRepository.findClosedAccountingRecordsForPayment(payment.getId());
  }
}
