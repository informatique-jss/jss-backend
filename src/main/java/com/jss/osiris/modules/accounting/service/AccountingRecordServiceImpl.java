package com.jss.osiris.modules.accounting.service;

import java.io.File;
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
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

  @Autowired
  AccountingRecordRepository accountingRecordRepository;

  @Autowired
  AccountingJournalService accountingJournalService;

  @Autowired
  AccountingBalanceHelper accountingBalanceHelper;

  @Autowired
  AccountingExportHelper accountingExportHelper;

  @Autowired
  TiersService tiersService;

  @Autowired
  ConfrereService confrereService;

  @Autowired
  ActiveDirectoryHelper activeDirectoryHelper;

  @Autowired
  BillingClosureReceiptDelegate billingClosureReceiptDelegate;

  @Autowired
  CustomerMailService customerMailService;

  @Autowired
  BatchService batchService;

  @Override
  public AccountingRecord getAccountingRecord(Integer id) {
    Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
    if (accountingRecord.isPresent())
      return accountingRecord.get();
    return null;
  }

  @Override
  public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord) {
    // Do not save null or 0 € records
    if (accountingRecord.getId() == null
        && (accountingRecord.getCreditAmount() == null || accountingRecord.getCreditAmount() == 0f)
        && (accountingRecord.getDebitAmount() == null || accountingRecord.getDebitAmount() == 0f))
      return null;
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
  public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords) {
    Integer operationId = getNewTemporaryOperationId();
    for (AccountingRecord accountingRecord : accountingRecords) {
      accountingRecord.setOperationDateTime(LocalDateTime.now());
      accountingRecord.setTemporaryOperationId(operationId);
      accountingRecord.setIsTemporary(true);
      accountingRecord.setIsANouveau(false);
      addOrUpdateAccountingRecord(accountingRecord);
    }
    return accountingRecords;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void dailyAccountClosing() {
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
            addOrUpdateAccountingRecord(accountingRecord);
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
  public List<AccountingRecordSearchResult> searchAccountingRecords(AccountingRecordSearch accountingRecordSearch) {
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

    if (accountingRecordSearch.getTiersId() == null)
      accountingRecordSearch.setTiersId(0);

    if (accountingRecordSearch.getConfrereId() == null)
      accountingRecordSearch.setConfrereId(0);

    if (accountingRecordSearch.getStartDate() == null)
      accountingRecordSearch.setStartDate(LocalDateTime.now().minusYears(100));

    if (accountingRecordSearch.getEndDate() == null)
      accountingRecordSearch.setEndDate(LocalDateTime.now().plusYears(100));

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

    return accountingRecordRepository.searchAccountingRecords(accountingAccountId, accountingClass, journalId,
        accountingRecordSearch.getTiersId(),
        accountingRecordSearch.getConfrereId(),
        accountingRecordSearch.getHideLettered(),
        accountingRecordSearch.getStartDate().withHour(0).withMinute(0),
        accountingRecordSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP),
        accountingRecordSearch.getIdPayment(),
        accountingRecordSearch.getIdCustomerOrder(),
        accountingRecordSearch.getIdInvoice(),
        accountingRecordSearch.getIdRefund(),
        accountingRecordSearch.getIdBankTransfert());
  }

  @Override
  public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch) {
    Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
        ? accountingBalanceSearch.getAccountingAccount().getId()
        : 0;
    Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
        ? accountingBalanceSearch.getAccountingClass().getId()
        : 0;
    Integer principalAccountingAccountId = accountingBalanceSearch.getPrincipalAccountingAccount() != null
        ? accountingBalanceSearch.getPrincipalAccountingAccount().getId()
        : 0;
    List<AccountingBalance> aa = accountingRecordRepository.searchAccountingBalance(
        accountingClassId,
        accountingAccountId, principalAccountingAccountId,
        accountingBalanceSearch.getStartDate().withHour(0).withMinute(0),
        accountingBalanceSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    return aa;
  }

  @Override
  public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingBalanceSearch) {
    Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
        ? accountingBalanceSearch.getAccountingAccount().getId()
        : 0;
    Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
        ? accountingBalanceSearch.getAccountingClass().getId()
        : 0;
    Integer principalAccountingAccountId = accountingBalanceSearch.getPrincipalAccountingAccount() != null
        ? accountingBalanceSearch.getPrincipalAccountingAccount().getId()
        : 0;
    return accountingRecordRepository.searchAccountingBalanceGenerale(
        accountingClassId,
        accountingAccountId, principalAccountingAccountId,
        accountingBalanceSearch.getStartDate().withHour(0).withMinute(0),
        accountingBalanceSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

  }

  @Override
  public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate) {
    List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.toLocalDate(), endDate.toLocalDate(),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1).toLocalDate(),
            endDate.minusYears(1).toLocalDate(),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    ArrayList<AccountingBalanceViewTitle> outBilan = new ArrayList<AccountingBalanceViewTitle>();

    outBilan.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));
    outBilan.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

    return outBilan;
  }

  @Override
  public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate) {
    List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.toLocalDate(), endDate.toLocalDate(),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1).toLocalDate(),
            endDate.minusYears(1).toLocalDate(),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    return accountingBalanceHelper.getProfitAndLost(accountingRecords, accountingRecordsN1);
  }

  @Override
  public File getGrandLivreExport(AccountingAccountClass accountingClass, LocalDateTime startDate,
      LocalDateTime endDate) throws OsirisException {
    return accountingExportHelper.getGrandLivre(accountingClass, startDate, endDate);
  }

  @Override
  public File getJournalExport(AccountingJournal accountingJournal, LocalDateTime startDate, LocalDateTime endDate)
      throws OsirisException {
    return accountingExportHelper.getJournal(accountingJournal, startDate, endDate);
  }

  @Override
  public File getAccountingAccountExport(AccountingAccount accountingAccount, LocalDateTime startDate,
      LocalDateTime endDate) throws OsirisException {
    return accountingExportHelper.getAccountingAccount(accountingAccount, startDate, endDate);
  }

  @Override
  public File getProfitLostExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException {
    return accountingExportHelper.getProfitAndLost(this.getProfitAndLost(startDate, endDate));
  }

  @Override
  public File getBilanExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException {
    List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.toLocalDate(), endDate.toLocalDate(),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1).toLocalDate(),
            endDate.minusYears(1).toLocalDate(),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    ArrayList<AccountingBalanceViewTitle> outBilanActif = new ArrayList<AccountingBalanceViewTitle>();

    outBilanActif.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));

    ArrayList<AccountingBalanceViewTitle> outBilanPassif = new ArrayList<AccountingBalanceViewTitle>();

    outBilanPassif.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

    return accountingExportHelper.getBilan(outBilanActif, outBilanPassif);
  }

  @Override
  public File getAccountingBalanceExport(Integer accountingClassId, Integer principalAccountingAccountId,
      Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws OsirisException {
    List<AccountingBalance> accountingBalanceRecords = accountingRecordRepository.searchAccountingBalance(
        accountingClassId != null ? accountingClassId : 0,
        accountingAccountId != null ? accountingAccountId : 0,
        (principalAccountingAccountId != null && !principalAccountingAccountId.equals(0) ? principalAccountingAccountId
            : 0),
        startDate, endDate, activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));
    return accountingExportHelper.getBalance(accountingBalanceRecords, false);
  }

  @Override
  public File getAccountingBalanceGeneraleExport(Integer accountingClassId, Integer principalAccountingAccountId,
      Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws OsirisException {
    List<AccountingBalance> accountingBalanceRecords = accountingRecordRepository.searchAccountingBalanceGenerale(
        accountingClassId != null ? accountingClassId : 0,
        accountingAccountId != null ? accountingAccountId : 0,
        (principalAccountingAccountId != null && !principalAccountingAccountId.equals(0) ? principalAccountingAccountId
            : 0),
        startDate, endDate, activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));
    return accountingExportHelper.getBalance(accountingBalanceRecords, true);
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
  public File getBillingClosureReceiptFile(Integer tiersId, boolean downloadFile)
      throws OsirisException, OsirisClientMessageException, OsirisValidationException {
    return billingClosureReceiptDelegate.getBillingClosureReceiptFile(tiersId, downloadFile);
  }

}
