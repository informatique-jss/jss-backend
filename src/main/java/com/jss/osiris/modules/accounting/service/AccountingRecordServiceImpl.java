package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
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
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

  @Autowired
  AccountingRecordRepository accountingRecordRepository;

  @Autowired
  InvoiceItemService invoiceItemService;

  @Autowired
  InvoiceHelper invoiceHelper;

  @Autowired
  AccountingAccountService accountingAccountService;

  @Autowired
  VatService vatService;

  @Autowired
  AccountingJournalService accountingJournalService;

  @Autowired
  AccountingBalanceHelper accountingBalanceHelper;

  @Autowired
  AccountingExportHelper accountingExportHelper;

  @Autowired
  QuotationService quotationService;

  @Autowired
  InvoiceService invoiceService;

  @Autowired
  PaymentService paymentService;

  @Autowired
  TiersService tiersService;

  @Autowired
  DocumentService documentService;

  @Autowired
  ConstantService constantService;

  @Autowired
  AttachmentService attachmentService;

  @Autowired
  MailHelper mailHelper;

  @Override
  public List<AccountingRecord> getAccountingRecords() {
    return IterableUtils.toList(accountingRecordRepository.findAll());
  }

  @Override
  public AccountingRecord getAccountingRecord(Integer id) {
    Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
    if (accountingRecord.isPresent())
      return accountingRecord.get();
    return null;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountingRecord addOrUpdateAccountingRecordFromUser(
      AccountingRecord accountingRecord) {
    return addOrUpdateAccountingRecord(accountingRecord);
  }

  public AccountingRecord addOrUpdateAccountingRecord(
      AccountingRecord accountingRecord) {
    return accountingRecordRepository.save(accountingRecord);
  }

  @Override
  public void deleteAccountingRecord(AccountingRecord accountingRecord) {
    accountingRecordRepository.delete(accountingRecord);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<AccountingRecord> addOrUpdateAccountingRecords(List<AccountingRecord> accountingRecords) {
    Integer operationId = ThreadLocalRandom.current().nextInt(1, 1000000000);
    for (AccountingRecord accountingRecord : accountingRecords) {
      accountingRecord.setOperationDateTime(LocalDateTime.now());
      accountingRecord.setTemporaryOperationId(operationId);
      accountingRecord.setIsTemporary(true);
      accountingRecord.setIsANouveau(false);
      addOrUpdateAccountingRecord(accountingRecord);
    }
    return accountingRecords;
  }

  public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws OsirisException {
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

    if (invoice == null)
      throw new OsirisException("No invoice provided");

    if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
      throw new OsirisException("No customer order or ITiers in invoice " + invoice.getId());

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    Float balance = 0f;
    balance += invoiceHelper.getPriceTotal(invoice);

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
        labelPrefix, null,
        balance,
        accountingAccountCustomer, null, invoice, null, salesJournal, null, null);

    // For each invoice item, one write on product and VAT account for each invoice
    // item
    for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
      if (invoiceItem.getBillingItem() == null)
        throw new OsirisException("No billing item defined in invoice item n°" + invoiceItem.getId());

      if (invoiceItem.getBillingItem().getBillingType() == null)
        throw new OsirisException(
            "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

      AccountingAccount producAccountingAccount = invoiceItem.getBillingItem().getBillingType()
          .getAccountingAccountProduct();

      if (producAccountingAccount == null)
        throw new OsirisException("No product accounting account defined in billing type n°"
            + invoiceItem.getBillingItem().getBillingType().getId());

      generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
          labelPrefix + " - produit "
              + invoiceItem.getBillingItem().getBillingType().getLabel(),
          invoiceItem.getPreTaxPrice()
              - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f),
          null, producAccountingAccount,
          invoiceItem, invoice, null, salesJournal, null, null);

      balance -= invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      if (invoiceItem.getVat() != null) {
        generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
            labelPrefix + " - TVA pour le produit "
                + invoiceItem.getBillingItem().getBillingType().getLabel(),
            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
            invoiceItem, invoice, null, salesJournal, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    // Check balance ok
    if (Math.round(balance * 1000f) / 1000f != 0) {
      throw new OsirisException("Accounting records  are not balanced for invoice " + invoice.getId());
    }
  }

  @Override
  public void generateAccountingRecordsForPurshaseOnInvoiceGeneration(Invoice invoice) throws OsirisException {
    AccountingJournal pushasingJournal = accountingJournalService.getPurchasesAccountingJournal();

    if (invoice == null)
      throw new OsirisException("No invoice provided");

    if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
      throw new OsirisException("No customer order or ITiers in invoice " + invoice.getId());

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountProvider = getCustomerAccountingAccountForInvoice(invoice);

    Float balance = 0f;
    balance += invoiceHelper.getPriceTotal(invoice);

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
        labelPrefix, balance, null, accountingAccountProvider, null, invoice, null, pushasingJournal, null, null);

    // For each invoice item, one write on product and VAT account for each invoice
    // item
    for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
      if (invoiceItem.getBillingItem() == null)
        throw new OsirisException("No billing item defined in invoice item n°" + invoiceItem.getId());

      if (invoiceItem.getBillingItem().getBillingType() == null)
        throw new OsirisException(
            "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

      AccountingAccount chargeAccountingAccount = invoiceItem.getBillingItem().getBillingType()
          .getAccountingAccountCharge();

      if (chargeAccountingAccount == null)
        throw new OsirisException("No product accounting account defined in billing type n°"
            + invoiceItem.getBillingItem().getBillingType().getId());

      generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
          labelPrefix + " - charge "
              + invoiceItem.getBillingItem().getBillingType().getLabel(),
          null,
          invoiceItem.getPreTaxPrice()
              - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f),
          chargeAccountingAccount,
          invoiceItem, invoice, null, pushasingJournal, null, null);

      balance -= invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      if (invoiceItem.getVat() != null) {
        generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), null,
            labelPrefix + " - TVA pour la charge "
                + invoiceItem.getBillingItem().getBillingType().getLabel(),
            null, invoiceItem.getVatPrice(), invoiceItem.getVat().getAccountingAccount(),
            invoiceItem, invoice, null, pushasingJournal, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    // Check balance ok
    if (Math.round(balance * 10000f) / 10000f != 0) {
      throw new OsirisException("Accounting records  are not balanced for invoice " + invoice.getId());
    }
  }

  @Override
  public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, List<Payment> payments,
      List<Deposit> deposits, Float amountToUse) throws OsirisException {
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

    if (invoice == null)
      throw new OsirisException("No invoice provided");

    if ((payments == null || payments.size() == 0) && (deposits == null || deposits.size() == 0))
      throw new OsirisException("No payments nor deposits provided with invoice " + invoice.getId());

    AccountingAccount bankAccountingAccount = accountingAccountService.getBankAccountingAccount();
    AccountingAccount waintingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    Integer operationId = 0;

    if (payments != null && payments.size() > 0)
      for (Payment payment : payments) {
        if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0)
          for (AccountingRecord accountingRecord : payment.getAccountingRecords())
            // Counter part waiting account record
            if (accountingRecord.getAccountingAccount().getId().equals(waintingAccountingAccount.getId()))
              generateCounterPart(accountingRecord);
      }

    if (deposits != null && deposits.size() > 0)
      for (Deposit deposit : deposits) {
        if (deposit.getAccountingRecords() != null && deposit.getAccountingRecords().size() > 0)
          for (AccountingRecord accountingRecord : deposit.getAccountingRecords())
            generateCounterPart(accountingRecord);

        operationId = invoice.getId() + deposits.get(0).getId();
        generateNewAccountingRecord(LocalDateTime.now(), operationId, null,
            "Réglement de la facture n°" + invoice.getId(), null, deposit.getDepositAmount(),
            bankAccountingAccount, null, invoice, null, salesJournal, null, deposit);
      }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null,
        "Réglement de la facture n°" + invoice.getId(), amountToUse, null,
        accountingAccountCustomer, null, invoice, null, salesJournal, null, null);

    // Trigger lettrage
    checkInvoiceForLettrage(invoice);
  }

  @Override
  public void generateAccountingRecordsForDepositAndInvoice(Deposit deposit, Invoice invoice, Payment payment)
      throws OsirisException {
    AccountingAccount depositAccountingAccount = getDepositAccountingAccountForCustomerOrder(
        invoiceHelper.getCustomerOrder(invoice));
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

    // If deposit is created from a payment, use the payment ID as operation ID to
    // keep the balance to 0 for a same operationID
    Integer operationId = deposit.getId();
    if (payment != null)
      operationId = payment.getId();
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null,
        "Acompte pour la facture n°" + invoice.getId(), deposit.getDepositAmount(), null, depositAccountingAccount,
        null, invoice, null, salesJournal, null, deposit);
  }

  @Override
  public void generateAppointForPayment(Payment payment, float remainingMoney, ITiers customerOrder)
      throws OsirisException {
    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForITiers(customerOrder);

    if (remainingMoney > 0) {
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null,
          "Appoint pour le paiement n°" + payment.getId(),
          remainingMoney, null, accountingAccountService.getProfitAccountingAccount(), null, null, null,
          accountingJournalService.getSalesAccountingJournal(), payment, null);
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null,
          "Appoint pour le paiement n°" + payment.getId(), null, remainingMoney, accountingAccountCustomer, null,
          null, null, accountingJournalService.getSalesAccountingJournal(), payment, null);
    } else if (remainingMoney < 0) {
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null,
          "Appoint pour le paiement n°" + payment.getId(),
          null, remainingMoney, accountingAccountService.getLostAccountingAccount(), null, null, null,
          accountingJournalService.getSalesAccountingJournal(), payment, null);
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null,
          "Appoint pour le paiement n°" + payment.getId(), remainingMoney, null, accountingAccountCustomer, null,
          null, null, accountingJournalService.getSalesAccountingJournal(), payment, null);
    }
  }

  @Override
  public void generateAccountingRecordsForDepositAndCustomerOrder(Deposit deposit, CustomerOrder customerOrder,
      Payment payment) throws OsirisException {

    AccountingAccount depositAccountingAccount = getDepositAccountingAccountForCustomerOrder(
        quotationService.getCustomerOrderOfQuotation(customerOrder));
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

    // If deposit is created from a payment, use the payment ID as operation ID to
    // keep the balance to 0 for a same operationID
    Integer operationId = deposit.getId();
    if (payment != null)
      operationId = payment.getId();

    generateNewAccountingRecord(LocalDateTime.now(), operationId, null,
        "Acompte pour la commande n°" + customerOrder.getId(), deposit.getDepositAmount(), null,
        depositAccountingAccount,
        null, null, customerOrder, salesJournal, null, deposit);
  }

  @Override
  public void generateAccountingRecordsForWaintingPayment(Payment payment) throws OsirisException {
    AccountingAccount waitingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();
    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null,
        "Mise en attente du règlement n°" + payment.getId(), payment.getPaymentAmount(), null, waitingAccountingAccount,
        null, null, null, salesJournal, payment, null);
  }

  @Override
  public void generateAccountingRecordsForRefund(Refund refund) throws OsirisException {
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();
    AccountingAccount customerAccountingAccount = null;
    if (refund.getConfrere() != null) {
      customerAccountingAccount = getCustomerAccountingAccountForITiers(refund.getConfrere());
    } else {
      customerAccountingAccount = getCustomerAccountingAccountForITiers(refund.getTiers());
    }
    generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, "Remboursement n°" + refund.getId(),
        refund.getRefundAmount(), null, accountingAccountService.getBankAccountingAccount(), null, null, null,
        salesJournal, null, null);
    generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, "Remboursement n°" + refund.getId(),
        null, refund.getRefundAmount(), customerAccountingAccount, null, null, null,
        salesJournal, null, null);
  }

  @Override
  public void generateBankAccountingRecordsForPayment(Payment payment) throws OsirisException {
    AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null,
        "Paiement n°" + payment.getId(), null, payment.getPaymentAmount(),
        accountingAccountService.getBankAccountingAccount(),
        null, null, null, salesJournal, payment, null);

  }

  private void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    List<AccountingRecord> accountingRecords = accountingRecordRepository
        .findByAccountingAccountAndInvoice(accountingAccountCustomer, invoice);

    Float balance = 0f;

    if (accountingRecords != null && accountingRecords.size() > 0) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        if (accountingRecord.getDebitAmount() != null)
          balance += accountingRecord.getDebitAmount();
        if (accountingRecord.getCreditAmount() != null)
          balance -= accountingRecord.getCreditAmount();
      }
    }

    if (balance == 0) {

      Integer maxLetteringNumber = accountingRecordRepository
          .findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
              .with(ChronoField.HOUR_OF_DAY, 0)
              .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

      if (maxLetteringNumber == null)
        maxLetteringNumber = 0;
      maxLetteringNumber++;

      if (accountingRecords != null)
        for (AccountingRecord accountingRecord : accountingRecords) {
          accountingRecord.setLetteringDateTime(LocalDateTime.now());
          accountingRecord.setLetteringNumber(maxLetteringNumber);
          this.addOrUpdateAccountingRecord(accountingRecord);
        }
    }

  }

  @Override
  public List<AccountingRecord> findByAccountingAccountAndInvoice(AccountingAccount accountingAccount,
      Invoice invoice) {
    return accountingRecordRepository.findByAccountingAccountAndInvoice(accountingAccount, invoice);
  }

  @Override
  public AccountingAccount getCustomerAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
    ITiers customerOrder = invoiceHelper.getCustomerOrder(invoice);
    return getCustomerAccountingAccountForITiers(customerOrder);
  }

  @Override
  public AccountingAccount getCustomerAccountingAccountForITiers(ITiers tiers) throws OsirisException {
    // If cusomter order is a Responsable, get accounting account of parent Tiers
    if (tiers.getAccountingAccountCustomer() == null || (tiers instanceof Responsable
        && ((Responsable) tiers).getTiers().getAccountingAccountCustomer() == null))
      throw new OsirisException("No customer accounting account in ITiers " + tiers.getId());

    AccountingAccount accountingAccountCustomer = null;
    if (tiers instanceof Responsable)
      accountingAccountCustomer = ((Responsable) tiers).getTiers().getAccountingAccountCustomer();
    else
      accountingAccountCustomer = tiers.getAccountingAccountCustomer();

    return accountingAccountCustomer;
  }

  @Override
  public AccountingAccount getProviderAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
    return getProviderAccountingAccountForITiers(invoice.getProvider());
  }

  @Override
  public AccountingAccount getProviderAccountingAccountForITiers(ITiers tiers) throws OsirisException {
    // If cusomter order is a Responsable, get accounting account of parent Tiers
    if (tiers.getAccountingAccountProvider() == null || (tiers instanceof Responsable
        && ((Responsable) tiers).getTiers().getAccountingAccountProvider() == null))
      throw new OsirisException("No customer accounting account in ITiers " + tiers.getId());

    AccountingAccount accountingAccountProvider = null;
    if (tiers instanceof Responsable)
      accountingAccountProvider = ((Responsable) tiers).getTiers().getAccountingAccountProvider();
    else
      accountingAccountProvider = tiers.getAccountingAccountProvider();

    return accountingAccountProvider;
  }

  private AccountingAccount getDepositAccountingAccountForCustomerOrder(ITiers customerOrder) throws OsirisException {
    // If cusomter order is a Responsable, get accounting account of parent Tiers
    if (customerOrder.getAccountingAccountCustomer() == null || (customerOrder instanceof Responsable
        && ((Responsable) customerOrder).getTiers().getAccountingAccountCustomer() == null))
      throw new OsirisException("No customer accounting account in ITiers " + customerOrder.getId());

    AccountingAccount accountingAccountDeposit = null;
    if (customerOrder instanceof Responsable)
      accountingAccountDeposit = ((Responsable) customerOrder).getTiers().getAccountingAccountDeposit();
    else
      accountingAccountDeposit = customerOrder.getAccountingAccountDeposit();

    return accountingAccountDeposit;
  }

  private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
      String manualAccountingDocumentNumber,
      String label, Float creditAmount, Float debitAmount,
      AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice, CustomerOrder customerOrder,
      AccountingJournal journal,
      Payment payment, Deposit deposit) {
    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setOperationDateTime(operationDatetime);
    accountingRecord.setTemporaryOperationId(operationId);
    accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
    accountingRecord.setLabel(label);
    accountingRecord.setCreditAmount(creditAmount);
    accountingRecord.setDebitAmount(debitAmount);
    accountingRecord.setAccountingAccount(accountingAccount);
    accountingRecord.setIsTemporary(true);
    accountingRecord.setInvoiceItem(invoiceItem);
    accountingRecord.setIsANouveau(false);
    accountingRecord.setInvoice(invoice);
    accountingRecord.setCustomerOrder(customerOrder);
    accountingRecord.setAccountingJournal(journal);
    accountingRecord.setDeposit(deposit);
    accountingRecord.setPayment(payment);
    accountingRecordRepository.save(accountingRecord);
    return accountingRecord;
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
            accountingRecordRepository.save(accountingRecord);
            maxIdAccounting++;
          }
        }
      }
    }
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

    if (accountingRecordSearch.getResponsableId() == null)
      accountingRecordSearch.setResponsableId(0);

    return accountingRecordRepository.searchAccountingRecords(accountingAccountId, accountingClass, journalId,
        accountingRecordSearch.getResponsableId(), accountingRecordSearch.getTiersId(),
        accountingRecordSearch.getHideLettered(),
        accountingRecordSearch.getStartDate(), accountingRecordSearch.getEndDate());
  }

  @Override
  public List<AccountingBalance> searchAccountingBalance(AccountingBalanceSearch accountingBalanceSearch) {
    Integer accountingAccountId = accountingBalanceSearch.getAccountingAccount() != null
        ? accountingBalanceSearch.getAccountingAccount().getId()
        : 0;
    Integer accountingClassId = accountingBalanceSearch.getAccountingClass() != null
        ? accountingBalanceSearch.getAccountingClass().getId()
        : 0;
    String accountNumber = accountingBalanceSearch.getAccountingAccountNumber() != null
        ? accountingBalanceSearch.getAccountingAccountNumber()
        : "";
    List<AccountingBalance> aa = accountingRecordRepository.searchAccountingBalance(
        accountingClassId,
        accountingAccountId, accountNumber,
        accountingBalanceSearch.getStartDate(), accountingBalanceSearch.getEndDate());

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
    String accountNumber = accountingBalanceSearch.getAccountingAccountNumber() != null
        ? accountingBalanceSearch.getAccountingAccountNumber()
        : "";
    return accountingRecordRepository.searchAccountingBalanceGenerale(
        accountingClassId,
        accountingAccountId, accountNumber,
        accountingBalanceSearch.getStartDate(), accountingBalanceSearch.getEndDate());

  }

  @Override
  public List<AccountingBalanceViewTitle> getBilan(LocalDateTime startDate, LocalDateTime endDate) {
    List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate, endDate);

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1));

    ArrayList<AccountingBalanceViewTitle> outBilan = new ArrayList<AccountingBalanceViewTitle>();

    outBilan.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));
    outBilan.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

    return outBilan;
  }

  @Override
  public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate) {
    List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate, endDate);

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1));

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
        .getAccountingRecordAggregateByAccountingNumber(startDate, endDate);

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1));

    ArrayList<AccountingBalanceViewTitle> outBilanActif = new ArrayList<AccountingBalanceViewTitle>();

    outBilanActif.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));

    ArrayList<AccountingBalanceViewTitle> outBilanPassif = new ArrayList<AccountingBalanceViewTitle>();

    outBilanPassif.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

    return accountingExportHelper.getBilan(outBilanActif, outBilanPassif);
  }

  @Override
  public File getAccountingBalanceExport(Integer accountingClassId, String accountingAccountNumber,
      Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws OsirisException {
    List<AccountingBalance> accountingBalanceRecords = accountingRecordRepository.searchAccountingBalance(
        accountingClassId != null ? accountingClassId : 0,
        accountingAccountId != null ? accountingAccountId : 0,
        (accountingAccountNumber != null && !accountingAccountNumber.equals("") ? accountingAccountNumber : ""),
        startDate, endDate);
    return accountingExportHelper.getBalance(accountingBalanceRecords, false);
  }

  @Override
  public File getAccountingBalanceGeneraleExport(Integer accountingClassId, String accountingAccountNumber,
      Integer accountingAccountId, LocalDateTime startDate, LocalDateTime endDate) throws OsirisException {
    List<AccountingBalance> accountingBalanceRecords = accountingRecordRepository.searchAccountingBalanceGenerale(
        accountingClassId != null ? accountingClassId : 0,
        accountingAccountId != null ? accountingAccountId : 0,
        (accountingAccountNumber != null && !accountingAccountNumber.equals("") ? accountingAccountNumber : ""),
        startDate, endDate);
    return accountingExportHelper.getBalance(accountingBalanceRecords, true);
  }

  @Override
  public void generateCounterPart(AccountingRecord originalAccountingRecord) {
    AccountingRecord newAccountingRecord = new AccountingRecord();
    newAccountingRecord.setAccountingAccount(originalAccountingRecord.getAccountingAccount());
    newAccountingRecord.setAccountingJournal(originalAccountingRecord.getAccountingJournal());
    newAccountingRecord.setCreditAmount(originalAccountingRecord.getDebitAmount());
    newAccountingRecord.setDebitAmount(originalAccountingRecord.getCreditAmount());
    newAccountingRecord.setDeposit(originalAccountingRecord.getDeposit());
    newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
    newAccountingRecord.setInvoiceItem(originalAccountingRecord.getInvoiceItem());
    newAccountingRecord.setIsANouveau(false);
    newAccountingRecord.setIsTemporary(true);
    newAccountingRecord.setLabel("Contre passe de : " + originalAccountingRecord.getLabel());
    newAccountingRecord.setManualAccountingDocumentDate(originalAccountingRecord.getManualAccountingDocumentDate());
    newAccountingRecord.setManualAccountingDocumentNumber(originalAccountingRecord.getManualAccountingDocumentNumber());
    newAccountingRecord.setPayment(originalAccountingRecord.getPayment());
    newAccountingRecord.setTemporaryOperationId(originalAccountingRecord.getTemporaryOperationId());
    newAccountingRecord.setOperationDateTime(LocalDateTime.now());
    addOrUpdateAccountingRecord(newAccountingRecord);
    originalAccountingRecord.setContrePasse(newAccountingRecord);
    addOrUpdateAccountingRecord(originalAccountingRecord);
  }

  @Override
  public Float getRemainingAmountToPayForInvoice(Invoice invoice) throws OsirisException {
    if (invoice != null) {
      List<AccountingRecord> accountingRecords = getAccountingRecordsForInvoice(invoice);
      if (accountingRecords == null || accountingRecords.size() == 0)
        return invoice.getTotalPrice();

      Float total = invoice.getTotalPrice();
      AccountingAccount customerAccountingAccount = getCustomerAccountingAccountForInvoice(invoice);
      for (AccountingRecord accountingRecord : accountingRecords) {
        if (accountingRecord.getAccountingAccount().getId().equals(customerAccountingAccount.getId())
            && accountingRecord.getCreditAmount() != null)
          total -= accountingRecord.getCreditAmount();
      }
      return Math.round(total * 100f) / 100f;
    }
    return 0f;
  }

  @Override
  public Float getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) {
    if (customerOrder != null) {
      Float total = 0f;
      // Total of items
      if (customerOrder.getAssoAffaireOrders() != null) {
        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders()) {
          if (assoAffaireOrder.getProvisions() != null) {
            for (Provision provision : assoAffaireOrder.getProvisions()) {
              if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0) {
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                  total += invoiceItem.getPreTaxPrice() + invoiceItem.getVatPrice()
                      - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);
                }
              }
            }
          }
        }
      }

      if (customerOrder.getAccountingRecords() == null || customerOrder.getAccountingRecords().size() == 0)
        return total;

      for (AccountingRecord accountingRecord : customerOrder.getAccountingRecords()) {
        if (accountingRecord.getCreditAmount() != null)
          total -= accountingRecord.getCreditAmount();
      }
      return Math.round(total * 100f) / 100f;
    }
    return 0f;
  }

  private List<AccountingRecord> getAccountingRecordsForInvoice(Invoice invoice) {
    return IterableUtils.toList(accountingRecordRepository.findByInvoice(invoice));
  }

  @Override
  public List<AccountingRecord> getAccountingRecordsByTemporaryOperationId(Integer temporaryOperationId) {
    if (temporaryOperationId != null)
      return accountingRecordRepository.findByTemporaryOperationId(temporaryOperationId);
    return null;
  }

  @Override
  public List<AccountingRecord> getAccountingRecordsByOperationId(Integer operationId) {
    if (operationId != null)
      return accountingRecordRepository.findByOperationId(operationId);
    return null;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<AccountingRecord> deleteRecordsByTemporaryOperationId(Integer temporaryOperationId)
      throws OsirisException {
    List<AccountingRecord> accountingRecords = getAccountingRecordsByTemporaryOperationId(temporaryOperationId);

    List<Invoice> invoicesToUnleter = new ArrayList<Invoice>();

    if (accountingRecords != null) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        deleteAccountingRecord(accountingRecord);
        boolean invoiceFound = false;
        for (Invoice invoice : invoicesToUnleter)
          if (invoice.getId().equals(accountingRecord.getInvoice().getId()))
            invoiceFound = true;
        if (!invoiceFound)
          invoicesToUnleter.add(accountingRecord.getInvoice());
      }
    }

    // Unleter invoices
    if (invoicesToUnleter.size() > 0)
      for (Invoice invoice : invoicesToUnleter)
        invoiceService.unletterInvoice(invoice);
    return null;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<AccountingRecord> doCounterPartByOperationId(Integer operationId) throws OsirisException {
    List<AccountingRecord> accountingRecords = getAccountingRecordsByOperationId(operationId);

    List<Invoice> invoicesToUnleter = new ArrayList<Invoice>();

    if (accountingRecords != null) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        generateCounterPart(accountingRecord);
        accountingRecord.setInvoice(null);
        paymentService.unlinkPaymentFromInvoiceCustomerOrder(accountingRecord.getPayment());
        accountingRecord.setPayment(null);
        accountingRecord.setDeposit(null);
        boolean invoiceFound = false;
        for (Invoice invoice : invoicesToUnleter)
          if (invoice.getId().equals(accountingRecord.getInvoice().getId()))
            invoiceFound = true;
        if (!invoiceFound)
          invoicesToUnleter.add(accountingRecord.getInvoice());
      }
    }

    // Unleter invoices
    if (invoicesToUnleter.size() > 0)
      for (Invoice invoice : invoicesToUnleter)
        invoiceService.unletterInvoice(invoice);
    return null;
  }

  @Override
  public AccountingRecord unassociateCustomerOrderPayementAndDeposit(AccountingRecord accountingRecord) {
    accountingRecord.setCustomerOrder(null);
    accountingRecord.setDeposit(null);
    accountingRecord.setPayment(null);
    return addOrUpdateAccountingRecord(accountingRecord);
  }

  @Transactional
  public void sendBillingClosureReceipt() throws OsirisException {
    List<Tiers> tiers = tiersService.findAllTiersTypeClient();
    if (tiers != null)
      for (Tiers tier : tiers) {
        Document billingClosureDocument = documentService.getBillingClosureDocument(tier.getDocuments());
        if (billingClosureDocument != null)
          if (billingClosureDocument.getBillingClosureRecipientType() != null
              && billingClosureDocument.getBillingClosureRecipientType().getId()
                  .equals(constantService.getBillingClosureRecipientTypeClient().getId())) {
            AccountingRecordSearch search = new AccountingRecordSearch();
            search.setTiersId(tier.getId());
            search.setHideLettered(true);
            search.setStartDate(LocalDateTime.now().minusYears(90));
            search.setEndDate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
            List<AccountingRecordSearchResult> accountingRecords = searchAccountingRecords(search);

            if (accountingRecords != null && accountingRecords.size() > 0) {
              File billingClosureReceipt = accountingExportHelper.generateBillingClosure(accountingRecords,
                  tier.getDenomination() != null ? tier.getDenomination()
                      : (tier.getFirstname() + " " + tier.getLastname()),
                  billingClosureDocument.getBillingClosureType().getId()
                      .equals(constantService.getBillingClosureTypeAffaire().getId()));

              List<Attachment> attachments = new ArrayList<Attachment>();
              try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                List<Attachment> attachmentsList = attachmentService.addAttachment(
                    new FileInputStream(billingClosureReceipt), tier.getId(),
                    Tiers.class.getSimpleName(), constantService.getAttachmentTypeBillingClosure(),
                    "Relevé de compte du " + LocalDateTime.now().format(formatter) + ".xlsx", false,
                    "Relevé de compte du " + LocalDateTime.now().format(formatter));

                for (Attachment attachment : attachmentsList)
                  if (attachment.getUploadedFile().getFilename()
                      .equals("Relevé de compte du " + LocalDateTime.now().format(formatter) + ".xlsx")) {
                    attachments.add(attachment);
                    break;
                  }
              } catch (FileNotFoundException e) {
                throw new OsirisException("Impossible to read excel of billing closure for tiers " + tier.getId());
              }

              mailHelper.sendPublicationReceiptToCustomer(attachments, tier, false);

            }

          } else if (billingClosureDocument.getBillingClosureRecipientType() != null
              && billingClosureDocument.getBillingClosureRecipientType().getId()
                  .equals(constantService.getBillingClosureRecipientTypeResponsable().getId())) {
            if (tier.getResponsables() != null && tier.getResponsables().size() > 0) {
              for (Responsable responsable : tier.getResponsables()) {
                AccountingRecordSearch search = new AccountingRecordSearch();
                search.setResponsableId(responsable.getId());
                search.setHideLettered(true);
                search.setStartDate(LocalDateTime.now().minusYears(90));
                search.setEndDate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
                List<AccountingRecordSearchResult> accountingRecords = searchAccountingRecords(search);

                if (accountingRecords != null && accountingRecords.size() > 0) {
                  File billingClosureReceipt = accountingExportHelper.generateBillingClosure(accountingRecords,
                      responsable.getFirstname() + " " + responsable.getLastname(),
                      billingClosureDocument.getBillingClosureType().getId()
                          .equals(constantService.getBillingClosureTypeAffaire().getId()));

                  List<Attachment> attachments = new ArrayList<Attachment>();
                  try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    List<Attachment> attachmentsList = attachmentService.addAttachment(
                        new FileInputStream(billingClosureReceipt), responsable.getId(),
                        Responsable.class.getSimpleName(), constantService.getAttachmentTypeBillingClosure(),
                        "Relevé de compte du " + LocalDateTime.now().format(formatter) + ".xlsx", false,
                        "Relevé de compte du " + LocalDateTime.now().format(formatter));

                    for (Attachment attachment : attachmentsList)
                      if (attachment.getUploadedFile().getFilename()
                          .equals("Relevé de compte du " + LocalDateTime.now().format(formatter) + ".xlsx")) {
                        attachments.add(attachment);
                        break;
                      }
                  } catch (FileNotFoundException e) {
                    throw new OsirisException(
                        "Impossible to read excel of billing closure for responsable " + responsable.getId());
                  }

                  mailHelper.sendPublicationReceiptToCustomer(attachments, responsable, false);
                }
              }
            }
          }
      }
  }

}
