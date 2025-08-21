package com.jss.osiris.modules.osiris.accounting.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class AccountingRepairHelper {

    @Autowired
    TiersService tiersService;

    @Autowired
    AccountingRecordRepository accountingRecordRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Transactional(rollbackFor = Exception.class)
    public void repairTierAccounts(Tiers tiers) throws OsirisException {
        tiers = tiersService.getTiers(tiers.getId());

        List<AccountingRecord> records = accountingRecordRepository.getAccountingRecordToRepairs(Arrays
                .asList(tiers.getAccountingAccountCustomer().getId(), tiers.getAccountingAccountDeposit().getId()));

        if (records != null) {

            // Lettering
            Integer maxLetteringNumber = accountingRecordService.findMaxLetteringNumberForMinLetteringDateTime(
                    LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                            .with(ChronoField.HOUR_OF_DAY, 0)
                            .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

            if (maxLetteringNumber == null)
                maxLetteringNumber = 0;
            maxLetteringNumber++;

            BigDecimal soldeDeposit = new BigDecimal(0);
            BigDecimal soldeCustomer = new BigDecimal(0);
            LocalDateTime operationDateTime = LocalDateTime.now();

            for (AccountingRecord accountingRecord : records) {
                accountingRecord.setLetteringNumberOld(accountingRecord.getLetteringNumber());
                accountingRecord.setLetteringDateTimeOld(accountingRecord.getLetteringDateTime());
                accountingRecord.setLetteringDateTime(operationDateTime);
                accountingRecord.setLetteringNumber(maxLetteringNumber);
                if (accountingRecord.getAccountingAccount().getId()
                        .equals(tiers.getAccountingAccountCustomer().getId())) {
                    if (accountingRecord.getCreditAmount() != null)
                        soldeCustomer = soldeCustomer.add(accountingRecord.getCreditAmount());
                    if (accountingRecord.getDebitAmount() != null)
                        soldeCustomer = soldeCustomer.subtract(accountingRecord.getDebitAmount());
                } else {
                    if (accountingRecord.getCreditAmount() != null)
                        soldeDeposit = soldeDeposit.add(accountingRecord.getCreditAmount());
                    if (accountingRecord.getDebitAmount() != null)
                        soldeDeposit = soldeDeposit.subtract(accountingRecord.getDebitAmount());
                }
                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
            }

            Integer operationId = getNewTemporaryOperationId();
            AccountingJournal journal = constantService.getAccountingJournalMiscellaneousOperations();
            AccountingAccount repriseAccount = accountingAccountService.getAccountingAccount(675657);
            List<AccountingRecord> newRecord = new ArrayList<AccountingRecord>();
            if (soldeCustomer.compareTo(new BigDecimal(0)) < 0) {
                newRecord.add(generateNewAccountingRecord(operationDateTime, operationId,
                        "Reprise comptabilité 2024 - " + tiers.getAccountingAccountCustomer().getLabel(),
                        soldeCustomer.abs(),
                        null, tiers.getAccountingAccountCustomer(), journal));
            }
            if (soldeCustomer.compareTo(new BigDecimal(0)) > 0) {
                newRecord.add(generateNewAccountingRecord(operationDateTime, operationId,
                        "Reprise comptabilité 2024 - " + tiers.getAccountingAccountCustomer().getLabel(), null,
                        soldeCustomer.abs(), tiers.getAccountingAccountCustomer(), journal));
            }
            if (soldeDeposit.compareTo(new BigDecimal(0)) < 0) {
                newRecord.add(generateNewAccountingRecord(operationDateTime, operationId,
                        "Reprise comptabilité 2024 - " + tiers.getAccountingAccountCustomer().getLabel(),
                        soldeDeposit.abs(),
                        null, tiers.getAccountingAccountDeposit(), journal));
            }
            if (soldeDeposit.compareTo(new BigDecimal(0)) > 0) {
                newRecord.add(generateNewAccountingRecord(operationDateTime, operationId,
                        "Reprise comptabilité 2024 - " + tiers.getAccountingAccountCustomer().getLabel(), null,
                        soldeDeposit.abs(), tiers.getAccountingAccountDeposit(), journal));
            }

            BigDecimal totalSolde = soldeCustomer.add(soldeDeposit);
            if (totalSolde.compareTo(new BigDecimal(0)) < 0) {
                newRecord.add(generateNewAccountingRecord(operationDateTime, operationId,
                        "Reprise comptabilité 2024 - " + tiers.getAccountingAccountCustomer().getLabel(),
                        null, totalSolde.abs(),
                        repriseAccount, journal));
            }
            if (totalSolde.compareTo(new BigDecimal(0)) > 0) {
                newRecord.add(generateNewAccountingRecord(operationDateTime, operationId,
                        "Reprise comptabilité 2024 - " + tiers.getAccountingAccountCustomer().getLabel(),
                        totalSolde.abs(), null, repriseAccount, journal));
            }

            if (newRecord != null) {
                for (AccountingRecord record : newRecord) {
                    record.setLetteringDateTime(operationDateTime);
                    record.setLetteringNumber(maxLetteringNumber);
                    accountingRecordService.addOrUpdateAccountingRecord(record, true);
                }
            }

        }
    }

    private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
            String label, BigDecimal creditAmount, BigDecimal debitAmount, AccountingAccount accountingAccount,
            AccountingJournal journal) throws OsirisClientMessageException, OsirisException {
        AccountingRecord accountingRecord = new AccountingRecord();
        accountingRecord.setOperationDateTime(operationDatetime);
        accountingRecord.setTemporaryOperationId(operationId);
        accountingRecord.setLabel(label);
        if (creditAmount != null)
            accountingRecord.setCreditAmount(creditAmount);
        if (debitAmount != null)
            accountingRecord.setDebitAmount(debitAmount);
        accountingRecord.setAccountingAccount(accountingAccount);
        accountingRecord.setIsTemporary(true);
        accountingRecord.setIsANouveau(false);
        accountingRecord.setAccountingJournal(journal);
        accountingRecord.setIsCounterPart(false);

        if (accountingRecord.getCreditAmount() != null
                && accountingRecord.getCreditAmount().compareTo(new BigDecimal(0)) < 0
                || accountingRecord.getDebitAmount() != null
                        && accountingRecord.getDebitAmount().compareTo(new BigDecimal(0)) < 0)
            throw new OsirisClientMessageException("Negative debit or credit !");
        return accountingRecord;
    }

    private Integer getNewTemporaryOperationId() {
        return ThreadLocalRandom.current().nextInt(1, 1000000000);
    }
}
