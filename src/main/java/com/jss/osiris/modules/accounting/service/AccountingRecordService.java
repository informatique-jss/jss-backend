package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceSearch;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface AccountingRecordService {
        public AccountingRecord addOrUpdateAccountingRecord(AccountingRecord accountingRecord);

        public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws OsirisException;

        public void generateAccountingRecordsForPurshaseOnInvoiceGeneration(Invoice invoice) throws OsirisException;

        public void generateAccountingRecordsForDebourPayment(Debour debour) throws OsirisException;

        public void dailyAccountClosing();

        public List<AccountingRecordSearchResult> searchAccountingRecords(
                        AccountingRecordSearch accountingRecordSearch);

        public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch);

        public File getGrandLivreExport(AccountingAccountClass accountingClass, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException;

        public File getJournalExport(AccountingJournal accountingJournal, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException;

        public File getAccountingAccountExport(AccountingAccount accountingAccount, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException;

        public File getProfitLostExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException;

        public File getBilanExport(LocalDateTime startDate, LocalDateTime endDate) throws OsirisException;

        public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate);

        public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate);

        public List<AccountingBalance> searchAccountingBalanceGenerale(AccountingBalanceSearch accountingRecordSearch);

        public File getAccountingBalanceExport(Integer accountingClassId, Integer principalAccountingAccountId,
                        Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate)
                        throws OsirisException;

        public File getAccountingBalanceGeneraleExport(Integer accountingClassId, Integer principalAccountingAccountId,
                        Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate)
                        throws OsirisException;

        public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
                        throws OsirisException;

        public void generateAccountingRecordsForPurshaseOnInvoicePayment(Invoice invoice, List<Payment> payments,
                        Float amountToUse) throws OsirisException;

        public void generateAccountingRecordsForDepositOnInvoice(Deposit deposit, Invoice invoice,
                        Integer overrideAccountingOperationId, boolean isFromOriginPayment) throws OsirisException;

        public void generateAccountingRecordsForDepositAndCustomerOrder(Deposit deposit, CustomerOrder customerOrder,
                        Integer overrideAccountingOperationId, boolean isFromOriginPayment) throws OsirisException;

        public void generateAccountingRecordsForRefundOnGeneration(Refund refund) throws OsirisException;

        public AccountingAccount getCustomerAccountingAccountForInvoice(Invoice invoice) throws OsirisException;

        public AccountingAccount getCustomerAccountingAccountForITiers(ITiers tiers) throws OsirisException;

        public AccountingAccount getProviderAccountingAccountForInvoice(Invoice invoice) throws OsirisException;

        public AccountingAccount getProviderAccountingAccountForITiers(ITiers tiers) throws OsirisException;

        public void generateAccountingRecordsForWaitingInboundPayment(Payment payment) throws OsirisException;

        public void generateAccountingRecordsForWaintingOutboundPayment(Payment payment) throws OsirisException;

        public void generateBankAccountingRecordsForInboundPayment(Payment payment) throws OsirisException;

        public void generateBankAccountingRecordsForOutboundPayment(Payment payment) throws OsirisException;

        public void generateAccountingRecordsForRefundOnVirement(Refund refund) throws OsirisException;

        public void generateAccountingRecordsForDebourOnDebour(Debour debour) throws OsirisException;

        public List<AccountingRecord> getAccountingRecordsByTemporaryOperationId(Integer operationId);

        public List<AccountingRecord> getAccountingRecordsByOperationId(Integer operationId);

        public void generateAppointForPayment(Payment payment, float remainingMoney, ITiers customerOrder)
                        throws OsirisException;

        public void generateAppointForDeposit(Deposit deposit, float remainingMoney, ITiers customerOrder)
                        throws OsirisException;

        public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords);

        public List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccount,
                        Invoice invoice);

        public AccountingRecord generateCounterPart(AccountingRecord originalAccountingRecord,
                        AccountingJournal overrideJournal,
                        Integer operationId);

        public void deleteAccountingRecord(AccountingRecord accountingRecord);

        public List<AccountingRecord> doCounterPartByOperationId(Integer operationId) throws OsirisException;

        public List<AccountingRecord> deleteRecordsByTemporaryOperationId(Integer temporaryOperationId)
                        throws OsirisException;

        public AccountingRecord unassociateCustomerOrderPayementAndDeposit(AccountingRecord accountingRecord);

        public void sendBillingClosureReceipt() throws OsirisException, OsirisClientMessageException;

        public void generateAccountingRecordsForCentralPayPayment(
                        CentralPayPaymentRequest centralPayPaymentRequest,
                        Payment payment, Deposit deposit, CustomerOrder customerOrder, Invoice invoice)
                        throws OsirisException;

        public void checkInvoiceForLettrage(Invoice invoice) throws OsirisException;

        public void letterWaitingRecords(AccountingRecord record, AccountingRecord counterPart) throws OsirisException;

}
