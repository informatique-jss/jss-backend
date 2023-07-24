package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.mail.model.MailComputeResult;
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
import com.jss.osiris.modules.accounting.model.BillingClosureReceiptValue;
import com.jss.osiris.modules.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.invoicing.model.ICreatedDate;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.invoicing.service.RefundService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.CentralPayDelegateService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

  @Value("${payment.cb.entry.point}")
  private String paymentCbEntryPoint;

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
  ResponsableService responsableService;

  @Autowired
  ConfrereService confrereService;

  @Autowired
  DocumentService documentService;

  @Autowired
  ConstantService constantService;

  @Autowired
  AttachmentService attachmentService;

  @Autowired
  MailHelper mailHelper;

  @Autowired
  MailComputeHelper mailComputeHelper;

  @Autowired
  ActiveDirectoryHelper activeDirectoryHelper;

  @Autowired
  GlobalExceptionHandler globalExceptionHandler;

  @Autowired
  CentralPayDelegateService centralPayDelegateService;

  @Autowired
  CustomerOrderStatusService customerOrderStatusService;

  @Autowired
  CustomerOrderService customerOrderService;

  @Autowired
  GeneratePdfDelegate generatePdfDelegate;

  @Autowired
  RefundService refundService;

  @Override
  public AccountingRecord getAccountingRecord(Integer id) {
    Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
    if (accountingRecord.isPresent())
      return accountingRecord.get();
    return null;
  }

  private AccountingRecord addOrUpdateAccountingRecord(
      AccountingRecord accountingRecord) {
    // Do not save null or 0 € records
    if (accountingRecord.getId() == null
        && (accountingRecord.getCreditAmount() == null || accountingRecord.getCreditAmount() == 0f)
        && (accountingRecord.getDebitAmount() == null || accountingRecord.getDebitAmount() == 0f))
      return null;
    return accountingRecordRepository.save(accountingRecord);
  }

  private void deleteAccountingRecord(AccountingRecord accountingRecord) {
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

  private void checkBalance(Float balance) throws OsirisValidationException {
    if (Math.round(balance * 100f) != 0)
      throw new OsirisValidationException("Balance not null");
  }

  @Override
  public void generateAccountingRecordsOnInvoiceEmission(Invoice invoice)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    AccountingJournal salesJournal = constantService.getAccountingJournalSales();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
      throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);
    Float balance = 0f;
    Integer operationId = getNewTemporaryOperationId();

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(), labelPrefix, null, invoiceHelper.getPriceTotal(invoice),
        accountingAccountCustomer, null, invoice, null, salesJournal, null, null);

    balance += invoiceHelper.getPriceTotal(invoice);

    // For each invoice item, one write on product and VAT account for each invoice
    // item
    for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
      if (invoiceItem.getBillingItem() == null)
        throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

      if (invoiceItem.getBillingItem().getBillingType() == null)
        throw new OsirisException(null,
            "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

      if (invoiceItem.getPreTaxPrice() == null)
        invoiceItem.setPreTaxPrice(0f);

      AccountingAccount producAccountingAccount = invoiceItem.getBillingItem().getBillingType()
          .getAccountingAccountProduct();

      if (producAccountingAccount == null)
        throw new OsirisException(null, "No product accounting account defined in billing type n°"
            + invoiceItem.getBillingItem().getBillingType().getId());

      Float billingItemPrice = invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      balance -= billingItemPrice;

      generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
          invoice.getManualAccountingDocumentDate(),
          labelPrefix + " - produit " + invoiceItem.getBillingItem().getBillingType().getLabel(), billingItemPrice,
          null, producAccountingAccount, invoiceItem, invoice, null, salesJournal, null, null);

      if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
        generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
            invoice.getManualAccountingDocumentDate(),
            labelPrefix + " - TVA pour le produit " + invoiceItem.getBillingItem().getBillingType().getLabel(),
            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(), invoiceItem, invoice, null,
            salesJournal, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    checkBalance(balance);
  }

  @Override
  public void generateAccountingRecordsOnCreditNoteReception(Invoice invoice)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    String labelPrefix = "Avoir n°" + invoice.getId();

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    Float balance = 0f;
    Integer operationId = getNewTemporaryOperationId();

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(),
        labelPrefix, null,
        invoiceHelper.getPriceTotal(invoice),
        accountingAccountProvider, null, invoice, null, pushasingJournal, null, null);

    balance += invoiceHelper.getPriceTotal(invoice);

    // For each invoice item, one write on product and VAT account for each invoice
    // item
    for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
      if (invoiceItem.getBillingItem() == null)
        throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

      if (invoiceItem.getBillingItem().getBillingType() == null)
        throw new OsirisException(null,
            "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

      if (invoiceItem.getPreTaxPrice() == null)
        invoiceItem.setPreTaxPrice(0f);

      AccountingAccount chargeAccountingAccount = invoiceItem.getBillingItem().getBillingType()
          .getAccountingAccountCharge();

      if (chargeAccountingAccount == null)
        throw new OsirisException(null, "No product accounting account defined in billing type n°"
            + invoiceItem.getBillingItem().getBillingType().getId());

      Float billingItemPrice = invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
          invoice.getManualAccountingDocumentDate(),
          labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(), billingItemPrice, null,
          chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null);

      balance -= billingItemPrice;

      if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
        generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
            invoice.getManualAccountingDocumentDate(),
            labelPrefix + " - TVA pour la charge " + invoiceItem.getBillingItem().getBillingType().getLabel(),
            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(), invoiceItem, invoice, null,
            pushasingJournal, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    checkBalance(balance);
  }

  @Override
  public void generateAccountingRecordsOnInvoiceReception(Invoice invoice)
      throws OsirisException, OsirisValidationException {
    AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    Float balance = 0f;
    balance += invoiceHelper.getPriceTotal(invoice);
    Integer operationId = getNewTemporaryOperationId();

    // One write on provider account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(),
        labelPrefix, balance, null, accountingAccountProvider, null, invoice, null, pushasingJournal, null, null);

    // For each invoice item, one write on product and VAT account for each invoice
    // item
    for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
      if (invoiceItem.getBillingItem() == null)
        throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

      if (invoiceItem.getBillingItem().getBillingType() == null)
        throw new OsirisException(null,
            "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

      AccountingAccount chargeAccountingAccount = invoiceItem.getBillingItem().getBillingType()
          .getAccountingAccountCharge();

      if (chargeAccountingAccount == null)
        throw new OsirisException(null, "No product accounting account defined in billing type n°"
            + invoiceItem.getBillingItem().getBillingType().getId());

      Float billingItemPrice = invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
          invoice.getManualAccountingDocumentDate(),
          labelPrefix + " - charge " + invoiceItem.getBillingItem().getBillingType().getLabel(), null, billingItemPrice,
          chargeAccountingAccount, invoiceItem, invoice, null, pushasingJournal, null, null);

      balance -= billingItemPrice;

      if (invoiceItem.getVat() != null) {

        if (invoiceItem.getVat().getAccountingAccount() == null)
          throw new OsirisException(null, "No accounting account for VAT " + invoiceItem.getVat().getLabel());

        if (invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0)
          generateNewAccountingRecord(LocalDateTime.now(), operationId, invoice.getManualAccountingDocumentNumber(),
              invoice.getManualAccountingDocumentDate(),
              labelPrefix + " - TVA pour la charge " + invoiceItem.getBillingItem().getBillingType().getLabel(), null,
              invoiceItem.getVatPrice(), invoiceItem.getVat().getAccountingAccount(), invoiceItem, invoice, null,
              pushasingJournal, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    checkBalance(balance);
  }

  @Override
  public void generateAccountingRecordOnIncomingPaymentCreation(Payment payment) throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
      bankJournal = constantService.getAccountingJournalCash();

    if (payment.getPaymentAmount() < 0)
      throw new OsirisException(null, "Incoming payment expected");

    if (payment.getTargetAccountingAccount() == null)
      throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

    Integer operationId = getNewTemporaryOperationId();

    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Paiement n°" + payment.getId() + " - " + payment.getLabel(), null, payment.getPaymentAmount(),
        payment.getTargetAccountingAccount(), null, null, null, bankJournal, payment, null);

    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Mise en attente du règlement n°" + payment.getId(), payment.getPaymentAmount(), null,
        accountingAccountService.getWaitingAccountingAccount(), null, null, null, bankJournal, payment, null);
  }

  @Override
  public void generateAccountingRecordOnOutgoingPaymentCreation(Payment payment, Invoice invoice, Refund refund)
      throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
      bankJournal = constantService.getAccountingJournalCash();

    if (payment.getPaymentAmount() > 0)
      throw new OsirisException(null, "Outgoing payment expected");

    if (payment.getTargetAccountingAccount() == null)
      throw new OsirisException(null, "No target accounting account for payment n°" + payment.getId());

    Integer operationId = getNewTemporaryOperationId();

    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null, "Paiement n°" + payment.getId(),
        Math.abs(payment.getPaymentAmount()), null, constantService.getAccountingAccountBankJss(), null, null, null,
        bankJournal, payment, null);

    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null, "Paiement n°" + payment.getId(), null,
        Math.abs(payment.getPaymentAmount()), payment.getTargetAccountingAccount(), null, invoice, null,
        bankJournal, payment, refund);

    if (invoice != null)
      checkInvoiceForLettrage(invoice);

    if (refund != null)
      checkRefundForLettrage(refund);
  }

  @Override
  public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
      throws OsirisException, OsirisValidationException {

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (payment == null)
      throw new OsirisException(null, "No payments nor deposits provided with invoice " + invoice.getId());

    AccountingJournal journal = constantService.getAccountingJournalBank();
    if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
      journal = constantService.getAccountingJournalCash();

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    Integer operationId = getNewTemporaryOperationId();
    Float balance = 0f;

    // Check if waiting record withount counter part exists for payment
    // If temporary => delete it
    // If not temporary => counter part it and continue with OD journal

    AccountingRecord waitingAccountingRecord = null;
    if (payment.getAccountingRecords() != null)
      for (AccountingRecord accountingRecord : payment.getAccountingRecords())
        if (accountingRecord.getAccountingAccount().getId()
            .equals(accountingAccountService.getWaitingAccountingAccount().getId())
            && accountingRecord.getContrePasse() == null) {
          waitingAccountingRecord = accountingRecord;
          break;
        }

    if (waitingAccountingRecord != null) {
      if (waitingAccountingRecord.getIsTemporary() != null && waitingAccountingRecord.getIsTemporary()) {
        balance -= waitingAccountingRecord.getCreditAmount() != null ? waitingAccountingRecord.getCreditAmount()
            : waitingAccountingRecord.getDebitAmount();
        deleteAccountingRecord(waitingAccountingRecord);
      } else {
        journal = constantService.getAccountingJournalMiscellaneousOperations();
        AccountingRecord counterPartWaitingAccountingRecord = getCounterPart(waitingAccountingRecord, operationId,
            journal, "Sortie du compte d'attente du paiement n°" + payment.getId());
        waitingAccountingRecord.setContrePasse(counterPartWaitingAccountingRecord);
        balance -= counterPartWaitingAccountingRecord.getCreditAmount() != null
            ? counterPartWaitingAccountingRecord.getCreditAmount()
            : counterPartWaitingAccountingRecord.getDebitAmount();

        addOrUpdateAccountingRecord(waitingAccountingRecord);
        letterCounterPartRecords(waitingAccountingRecord, counterPartWaitingAccountingRecord);
      }
    }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Réglement de la facture n°" + invoice.getId(), payment.getPaymentAmount(), null,
        accountingAccountCustomer, null, invoice, null, journal, null, null);

    balance += payment.getPaymentAmount();
    checkBalance(balance);

    // Trigger lettrage
    checkInvoiceForLettrage(invoice);
  }

  @Override
  public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment)
      throws OsirisException, OsirisValidationException {

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (payment == null)
      throw new OsirisException(null, "No payments nor deposits provided with invoice " + invoice.getId());

    AccountingJournal journal = constantService.getAccountingJournalBank();
    if (payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
      journal = constantService.getAccountingJournalCash();

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    Integer operationId = getNewTemporaryOperationId();
    Float balance = 0f;

    // Check if waiting record withount counter part exists for payment
    // If temporary => delete it
    // If not temporary => counter part it and continue with OD journal

    AccountingRecord waitingAccountingRecord = null;
    if (payment.getAccountingRecords() != null)
      for (AccountingRecord accountingRecord : payment.getAccountingRecords())
        if (accountingRecord.getAccountingAccount().getId()
            .equals(accountingAccountService.getWaitingAccountingAccount().getId())
            && accountingRecord.getContrePasse() == null) {
          waitingAccountingRecord = accountingRecord;
          break;
        }

    if (waitingAccountingRecord != null) {
      if (waitingAccountingRecord.getIsTemporary() != null && waitingAccountingRecord.getIsTemporary()) {
        balance -= waitingAccountingRecord.getCreditAmount() != null ? waitingAccountingRecord.getCreditAmount()
            : waitingAccountingRecord.getDebitAmount();
        deleteAccountingRecord(waitingAccountingRecord);
      } else {
        journal = constantService.getAccountingJournalMiscellaneousOperations();
        AccountingRecord counterPartWaitingAccountingRecord = getCounterPart(waitingAccountingRecord, operationId,
            journal, "Sortie du compte d'attente du paiement n°" + payment.getId());
        waitingAccountingRecord.setContrePasse(counterPartWaitingAccountingRecord);
        balance -= counterPartWaitingAccountingRecord.getCreditAmount() != null
            ? counterPartWaitingAccountingRecord.getCreditAmount()
            : counterPartWaitingAccountingRecord.getDebitAmount();

        addOrUpdateAccountingRecord(waitingAccountingRecord);
        letterCounterPartRecords(waitingAccountingRecord, counterPartWaitingAccountingRecord);
      }
    }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Remboursement de la facture n°" + invoice.getId(), payment.getPaymentAmount(), null,
        accountingAccountProvider, null, invoice, null, journal, null, null);

    balance += payment.getPaymentAmount();
    checkBalance(balance);
  }

  private void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
    AccountingAccount accountingAccount;
    invoice = invoiceService.getInvoice(invoice.getId());
    if (invoice.getIsInvoiceFromProvider() != null && invoice.getIsInvoiceFromProvider()
        || invoice.getIsProviderCreditNote() != null && invoice.getIsProviderCreditNote())
      accountingAccount = getProviderAccountingAccountForInvoice(invoice);
    else
      accountingAccount = getCustomerAccountingAccountForInvoice(invoice);

    List<AccountingRecord> accountingRecords = accountingRecordRepository
        .findByAccountingAccountAndInvoice(accountingAccount, invoice);

    Float balance = 0f;

    if (accountingRecords != null && accountingRecords.size() > 0) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        if (accountingRecord.getDebitAmount() != null)
          balance += accountingRecord.getDebitAmount();
        if (accountingRecord.getCreditAmount() != null)
          balance -= accountingRecord.getCreditAmount();
      }

      if (Math.round(Math.abs(balance) * 100f) / 100f <= 0.01) {

        Integer maxLetteringNumber = accountingRecordRepository
            .findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

        if (maxLetteringNumber == null)
          maxLetteringNumber = 0;
        maxLetteringNumber++;

        for (AccountingRecord accountingRecord : accountingRecords) {
          accountingRecord.setLetteringDateTime(LocalDateTime.now());
          accountingRecord.setLetteringNumber(maxLetteringNumber);
          this.addOrUpdateAccountingRecord(accountingRecord);
        }

        invoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
        invoiceService.addOrUpdateInvoice(invoice);
      }
    }
  }

  @Override
  public void unletterInvoiceEmitted(Invoice invoice) throws OsirisException {
    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    List<AccountingRecord> accountingRecords = accountingRecordRepository
        .findByAccountingAccountAndInvoice(accountingAccountCustomer, invoice);

    if (accountingRecords != null)
      for (AccountingRecord accountingRecord : accountingRecords) {
        accountingRecord.setLetteringDateTime(null);
        accountingRecord.setLetteringNumber(null);
        addOrUpdateAccountingRecord(accountingRecord);
      }
    invoice.setInvoiceStatus(constantService.getInvoiceStatusSend());
    invoiceService.addOrUpdateInvoice(invoice);
  }

  @Override
  public void unletterInvoiceReceived(Invoice invoice) throws OsirisException {
    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    List<AccountingRecord> accountingRecords = accountingRecordRepository
        .findByAccountingAccountAndInvoice(accountingAccountProvider, invoice);

    if (accountingRecords != null)
      for (AccountingRecord accountingRecord : accountingRecords) {
        accountingRecord.setLetteringDateTime(null);
        accountingRecord.setLetteringNumber(null);
        addOrUpdateAccountingRecord(accountingRecord);
      }
    invoice.setInvoiceStatus(constantService.getInvoiceStatusReceived());
    invoiceService.addOrUpdateInvoice(invoice);
  }

  private void checkRefundForLettrage(Refund refund) throws OsirisException {
    refund = refundService.getRefund(refund.getId());

    AccountingAccount account = null;

    if (refund.getTiers() != null)
      account = refund.getTiers().getAccountingAccountCustomer();

    if (refund.getConfrere() != null)
      account = refund.getConfrere().getAccountingAccountCustomer();

    if (account == null)
      throw new OsirisException(null, "Accounting account not found for refund");

    List<AccountingRecord> accountingRecords = accountingRecordRepository
        .findByAccountingAccountAndRefund(account, refund);

    Float balance = 0f;

    if (accountingRecords != null && accountingRecords.size() > 0) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        if (accountingRecord.getDebitAmount() != null)
          balance += accountingRecord.getDebitAmount();
        if (accountingRecord.getCreditAmount() != null)
          balance -= accountingRecord.getCreditAmount();
      }

      if (Math.round(Math.abs(balance) * 100f) / 100f <= 0.01) {

        Integer maxLetteringNumber = accountingRecordRepository
            .findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

        if (maxLetteringNumber == null)
          maxLetteringNumber = 0;
        maxLetteringNumber++;

        for (AccountingRecord accountingRecord : accountingRecords) {
          accountingRecord.setLetteringDateTime(LocalDateTime.now());
          accountingRecord.setLetteringNumber(maxLetteringNumber);
          this.addOrUpdateAccountingRecord(accountingRecord);
        }

      }
    }
  }

  private void letterCounterPartRecords(AccountingRecord record, AccountingRecord counterPart) throws OsirisException {
    Integer maxLetteringNumber = accountingRecordRepository
        .findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
            .with(ChronoField.HOUR_OF_DAY, 0)
            .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

    if (maxLetteringNumber == null)
      maxLetteringNumber = 0;
    maxLetteringNumber++;

    record.setLetteringDateTime(LocalDateTime.now());
    record.setLetteringNumber(maxLetteringNumber);
    counterPart.setLetteringDateTime(LocalDateTime.now());
    counterPart.setLetteringNumber(maxLetteringNumber);
    this.addOrUpdateAccountingRecord(record);
    this.addOrUpdateAccountingRecord(counterPart);
  }

  @Override
  public void letterCreditNoteAndInvoice(Invoice invoice, Invoice creditNote) throws OsirisException {
    Integer maxLetteringNumber = accountingRecordRepository
        .findMaxLetteringNumberForMinLetteringDateTime(LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
            .with(ChronoField.HOUR_OF_DAY, 0)
            .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

    if (maxLetteringNumber == null)
      maxLetteringNumber = 0;
    maxLetteringNumber++;

    for (AccountingRecord record : invoice.getAccountingRecords()) {
      if (record.getAccountingAccount().getPrincipalAccountingAccount().getId()
          .equals(constantService.getPrincipalAccountingAccountCustomer().getId()) && record.getDebitAmount() != null
          || record.getAccountingAccount().getPrincipalAccountingAccount().getId()
              .equals(constantService.getPrincipalAccountingAccountProvider().getId())
              && record.getCreditAmount() != null)
        if (record.getIsCounterPart() == false) {
          record.setLetteringDateTime(LocalDateTime.now());
          record.setLetteringNumber(maxLetteringNumber);
          record.getContrePasse().setLetteringDateTime(LocalDateTime.now());
          record.getContrePasse().setLetteringNumber(maxLetteringNumber);
          this.addOrUpdateAccountingRecord(record);
          this.addOrUpdateAccountingRecord(record.getContrePasse());
        }
    }
  }

  private AccountingAccount getCustomerAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
    ITiers customerOrder = invoiceHelper.getCustomerOrder(invoice);
    AccountingAccount accountingAccountCustomer = null;
    if (customerOrder instanceof Responsable)
      accountingAccountCustomer = ((Responsable) customerOrder).getTiers().getAccountingAccountCustomer();
    else
      accountingAccountCustomer = customerOrder.getAccountingAccountCustomer();

    if (accountingAccountCustomer == null)
      throw new OsirisException(null, "No customer accounting account in ITiers " + customerOrder.getId());

    return accountingAccountCustomer;
  }

  private AccountingAccount getProviderAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
    AccountingAccount accountingAccount = null;
    if (invoice.getProvider() != null)
      accountingAccount = invoice.getProvider().getAccountingAccountProvider();
    if (invoice.getCompetentAuthority() != null)
      accountingAccount = invoice.getCompetentAuthority().getAccountingAccountProvider();
    if (invoice.getConfrere() != null)
      accountingAccount = invoice.getConfrere().getAccountingAccountProvider();
    if (invoice.getTiers() != null)
      accountingAccount = invoice.getTiers().getAccountingAccountProvider();
    if (invoice.getResponsable() != null && invoice.getResponsable().getTiers() != null)
      accountingAccount = invoice.getResponsable().getTiers().getAccountingAccountProvider();
    if (accountingAccount == null)
      throw new OsirisException(null, "No customer accounting account in Provider ");

    return accountingAccount;
  }

  private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
      String manualAccountingDocumentNumber, LocalDate manualAccountingDocumentDate, String label, Float creditAmount,
      Float debitAmount, AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice,
      CustomerOrder customerOrder, AccountingJournal journal, Payment payment, Refund refund) {
    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setOperationDateTime(operationDatetime);
    accountingRecord.setTemporaryOperationId(operationId);
    accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
    accountingRecord.setManualAccountingDocumentDate(manualAccountingDocumentDate);
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
    accountingRecord.setIsCounterPart(false);
    accountingRecord.setPayment(payment);
    accountingRecord.setRefund(refund);
    addOrUpdateAccountingRecord(accountingRecord);
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
            addOrUpdateAccountingRecord(accountingRecord);
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

    if (accountingRecordSearch.getConfrereId() == null)
      accountingRecordSearch.setConfrereId(0);

    if (accountingRecordSearch.getStartDate() == null)
      accountingRecordSearch.setStartDate(LocalDateTime.now().minusYears(100));

    if (accountingRecordSearch.getEndDate() == null)
      accountingRecordSearch.setEndDate(LocalDateTime.now().plusYears(100));

    return accountingRecordRepository.searchAccountingRecords(accountingAccountId, accountingClass, journalId,
        accountingRecordSearch.getTiersId(),
        accountingRecordSearch.getConfrereId(),
        accountingRecordSearch.getHideLettered(),
        accountingRecordSearch.getStartDate().withHour(0).withMinute(0),
        accountingRecordSearch.getEndDate().withHour(23).withMinute(59),
        activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));
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

  private AccountingRecord getCounterPart(AccountingRecord originalAccountingRecord, Integer operationId,
      AccountingJournal journal, String label) {
    AccountingRecord newAccountingRecord = new AccountingRecord();
    newAccountingRecord.setAccountingAccount(originalAccountingRecord.getAccountingAccount());
    newAccountingRecord.setAccountingJournal(journal);
    newAccountingRecord.setCreditAmount(originalAccountingRecord.getDebitAmount());
    newAccountingRecord.setDebitAmount(originalAccountingRecord.getCreditAmount());
    newAccountingRecord.setRefund(originalAccountingRecord.getRefund());
    newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
    newAccountingRecord.setInvoiceItem(originalAccountingRecord.getInvoiceItem());
    newAccountingRecord.setIsANouveau(false);
    newAccountingRecord.setIsTemporary(true);
    newAccountingRecord.setLabel(label);
    newAccountingRecord.setManualAccountingDocumentDate(originalAccountingRecord.getManualAccountingDocumentDate());
    newAccountingRecord.setManualAccountingDocumentNumber(originalAccountingRecord.getManualAccountingDocumentNumber());
    newAccountingRecord.setPayment(originalAccountingRecord.getPayment());
    newAccountingRecord.setTemporaryOperationId(operationId);
    newAccountingRecord.setOperationDateTime(LocalDateTime.now());
    newAccountingRecord.setCustomerOrder(originalAccountingRecord.getCustomerOrder());
    newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
    newAccountingRecord.setIsCounterPart(true);
    return addOrUpdateAccountingRecord(newAccountingRecord);
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
      throws OsirisException, OsirisValidationException {
    List<AccountingRecord> accountingRecords = getAccountingRecordsByTemporaryOperationId(temporaryOperationId);

    List<Invoice> invoicesToUnleter = new ArrayList<Invoice>();

    if (accountingRecords != null) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        if (accountingRecord.getIsTemporary() == false)
          throw new OsirisValidationException("Accounting record not temporary");
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
        invoiceService.unletterInvoiceEmitted(invoice);
    return null;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<AccountingRecord> doCounterPartByOperationId(Integer operationId) throws OsirisException {
    List<AccountingRecord> accountingRecords = getAccountingRecordsByOperationId(operationId);

    List<Invoice> invoicesToUnleter = new ArrayList<Invoice>();
    Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
    if (accountingRecords != null) {
      for (AccountingRecord accountingRecord : accountingRecords) {
        getCounterPart(accountingRecord, operationIdCounterPart, accountingRecord.getAccountingJournal(),
            "Contre passe de : " + accountingRecord.getLabel());
        accountingRecord.setInvoice(null);
        accountingRecord.setPayment(null);
        boolean invoiceFound = false;
        if (invoicesToUnleter != null && invoicesToUnleter.size() > 0)
          for (Invoice invoice : invoicesToUnleter)
            if (invoice.getId().equals(accountingRecord.getInvoice().getId()))
              invoiceFound = true;
        if (!invoiceFound && accountingRecord.getInvoice() != null)
          invoicesToUnleter.add(accountingRecord.getInvoice());
      }
    }

    // Unleter invoices
    if (invoicesToUnleter.size() > 0)
      for (Invoice invoice : invoicesToUnleter)
        invoiceService.unletterInvoiceEmitted(invoice);
    return null;
  }

  @Override
  @Transactional
  public void sendBillingClosureReceipt()
      throws OsirisException, OsirisClientMessageException, OsirisValidationException {
    List<Tiers> tiers = tiersService.findAllTiersForBillingClosureReceiptSend();
    if (tiers != null && tiers.size() > 0)
      for (Tiers tier : tiers) {
        getBillingClosureReceiptFile(tier.getId(), false);
      }

    List<Confrere> confreres = confrereService.getConfreres();
    if (confreres != null && confreres.size() > 0)
      for (Confrere confrere : confreres) {
        getBillingClosureReceiptFile(confrere.getId(), false);
      }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public File getBillingClosureReceiptFile(Integer tiersId, boolean downloadFile)
      throws OsirisException, OsirisClientMessageException, OsirisValidationException {

    ITiers tier = null;
    tier = tiersService.getTiers(tiersId);

    if (tier == null)
      tier = responsableService.getResponsable(tiersId);

    if (tier == null)
      tier = confrereService.getConfrere(tiersId);

    Document billingClosureDocument = null;
    if (tier instanceof Responsable)
      billingClosureDocument = documentService
          .getBillingClosureDocument(((Responsable) tier).getTiers().getDocuments());
    else
      billingClosureDocument = documentService.getBillingClosureDocument(tier.getDocuments());

    if (billingClosureDocument != null) {
      boolean isOrderingByEventDate = billingClosureDocument.getBillingClosureType() != null && !billingClosureDocument
          .getBillingClosureType().getId().equals(constantService.getBillingClosureTypeAffaire().getId());

      if (downloadFile) {
        ArrayList<ITiers> tiers = new ArrayList<ITiers>();
        if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
          tiers.addAll(((Tiers) tier).getResponsables());
        else
          tiers.add(tier);
        List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tiers,
            isOrderingByEventDate);
        return generatePdfDelegate.getBillingClosureReceiptFile(tier, values);
      }

      // Send all to tiers
      if (billingClosureDocument.getBillingClosureRecipientType() != null
          && (billingClosureDocument.getBillingClosureRecipientType().getId()
              .equals(constantService.getBillingClosureRecipientTypeClient().getId())
              || billingClosureDocument.getBillingClosureRecipientType().getId()
                  .equals(constantService.getBillingClosureRecipientTypeOther().getId()))) {

        ArrayList<ITiers> tiers = new ArrayList<ITiers>();
        if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
          tiers.addAll(((Tiers) tier).getResponsables());
        else
          tiers.add(tier);

        List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tiers, isOrderingByEventDate);
        if (values.size() > 0) {
          try {
            sendBillingClosureReceiptFile(
                generatePdfDelegate.getBillingClosureReceiptFile(tier, values),
                tier);
          } catch (Exception e) {
            globalExceptionHandler.persistLog(
                new OsirisException(e, "Impossible to generate billing closure for Tiers " + tiersId),
                OsirisLog.UNHANDLED_LOG);
          }
        }

      } else if (billingClosureDocument.getBillingClosureRecipientType() != null
          && billingClosureDocument.getBillingClosureRecipientType().getId()
              .equals(constantService.getBillingClosureRecipientTypeResponsable().getId())) {
        // Send to each responsable
        if (tier instanceof Tiers && ((Tiers) tier).getResponsables() != null)
          for (Responsable responsable : ((Tiers) tier).getResponsables()) {

            ArrayList<ITiers> tiers = new ArrayList<ITiers>();
            tiers.add(responsable);
            List<BillingClosureReceiptValue> values = generateBillingClosureValuesForITiers(tiers,
                isOrderingByEventDate);
            if (values.size() > 0) {
              try {
                sendBillingClosureReceiptFile(
                    generatePdfDelegate.getBillingClosureReceiptFile(responsable, values),
                    tier);
              } catch (Exception e) {
                globalExceptionHandler.persistLog(
                    new OsirisException(e, "Impossible to generate billing closure for Tiers " + tiersId),
                    OsirisLog.UNHANDLED_LOG);
              }
            }
          }
      }
    }
    return null;
  }

  private List<BillingClosureReceiptValue> generateBillingClosureValuesForITiers(ArrayList<ITiers> tiers,
      boolean isOrderingByEventDate) throws OsirisException, OsirisClientMessageException {

    // Find all elements
    ArrayList<BillingClosureReceiptValue> values = new ArrayList<BillingClosureReceiptValue>();

    // Find customer orders
    OrderingSearch search = new OrderingSearch();
    ArrayList<Tiers> tiersList = new ArrayList<Tiers>();
    for (ITiers tiersIn : tiers) {
      tiersList = new ArrayList<Tiers>();
      boolean hadSomeValues = false;
      if (tiersIn instanceof Tiers) {
        Tiers t = (Tiers) tiersIn;
        tiersList.add(t);
        values.add(new BillingClosureReceiptValue(
            t.getDenomination() != null ? t.getDenomination() : (t.getFirstname() + " " + t.getLastname())));
      } else if (tiersIn instanceof Confrere) {
        Tiers fakeTiers = new Tiers();
        fakeTiers.setId(tiersIn.getId());
        tiersList.add(fakeTiers);
        values.add(new BillingClosureReceiptValue(((Confrere) tiersIn).getLabel()));
      } else if (tiersIn instanceof Responsable) {
        Tiers fakeTiers = new Tiers();
        fakeTiers.setId(tiersIn.getId());
        tiersList.add(fakeTiers);
        Responsable t = (Responsable) tiersIn;
        values.add(new BillingClosureReceiptValue((t.getFirstname() + " " + t.getLastname())));
      }

      search.setCustomerOrders(tiersList);

      search.setCustomerOrderStatus(customerOrderStatusService.getCustomerOrderStatus().stream()
          .filter(status -> !status.getCode().equals(CustomerOrderStatus.BILLED) &&
              !status.getCode().equals(CustomerOrderStatus.ABANDONED))
          .collect(Collectors.toList()));

      List<OrderingSearchResult> customerOrdersList = customerOrderService.searchOrders(search);
      ArrayList<CustomerOrder> customerOrders = new ArrayList<CustomerOrder>();

      if (customerOrdersList != null && customerOrdersList.size() > 0) {
        for (OrderingSearchResult customerOrder : customerOrdersList) {
          if (customerOrder.getDepositTotalAmount() != null && customerOrder.getDepositTotalAmount() > 0) {
            CustomerOrder completeCustomerOrder = customerOrderService
                .getCustomerOrder(customerOrder.getCustomerOrderId());
            customerOrders.add(completeCustomerOrder);
          }
        }
      }

      // Find invoices
      InvoiceSearch invoiceSearch = new InvoiceSearch();
      invoiceSearch.setCustomerOrders(tiersList);
      invoiceSearch.setInvoiceStatus(Arrays.asList(constantService.getInvoiceStatusSend()));

      List<InvoiceSearchResult> invoiceList = invoiceService.searchInvoices(invoiceSearch);
      ArrayList<Invoice> invoices = new ArrayList<Invoice>();
      if (invoiceList != null && invoiceList.size() > 0) {
        for (InvoiceSearchResult invoice : invoiceList) {
          Invoice completeInvoice = invoiceService.getInvoice(invoice.getInvoiceId());
          invoices.add(completeInvoice);
        }
      }

      if (isOrderingByEventDate) {
        ArrayList<ICreatedDate> allInputs = new ArrayList<ICreatedDate>();
        if (customerOrders != null && customerOrders.size() > 0) {
          for (CustomerOrder customerOrder : customerOrders) {
            allInputs.add(customerOrder);
            if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0)
              for (Payment payment : customerOrder.getPayments())
                allInputs.add(payment);
          }
        }

        if (invoices != null && invoices.size() > 0) {
          for (Invoice invoice : invoices) {
            allInputs.add(invoice);
            if (invoice.getPayments() != null && invoice.getPayments().size() > 0)
              for (Payment payment : invoice.getPayments())
                allInputs.add(payment);
          }
        }

        if (allInputs.size() > 0) {
          hadSomeValues = true;
          allInputs.sort(new Comparator<ICreatedDate>() {
            @Override
            public int compare(ICreatedDate o1, ICreatedDate o2) {
              return o1.getCreatedDate().compareTo(o2.getCreatedDate());
            }
          });

          for (ICreatedDate input : allInputs) {
            if (input instanceof CustomerOrder)
              values.add(getBillingClosureReceiptValueForCustomerOrder((CustomerOrder) input));
            if (input instanceof Invoice)
              values.add(getBillingClosureReceiptValueForInvoice((Invoice) input));
            if (input instanceof Payment) {
              CustomerOrder customerOrder = null;
              Payment payment = (Payment) input;
              if (payment.getIsDeposit()) {
                if (payment.getCustomerOrder() != null)
                  customerOrder = payment.getCustomerOrder();
                else if (payment.getInvoice() != null)
                  customerOrder = payment.getInvoice().getCustomerOrder();
                values.add(getBillingClosureReceiptValueForDeposit(payment, customerOrder, true));
              } else {
                values.add(getBillingClosureReceiptValueForPayment(payment, true));
              }
            }
          }
        }
      } else {
        // Order by affaire
        ArrayList<Object> allInputs = new ArrayList<Object>();
        if (customerOrders != null && customerOrders.size() > 0)
          allInputs.addAll(customerOrders);
        if (invoices != null && invoices.size() > 0)
          allInputs.addAll(invoices);

        if (allInputs.size() > 0) {
          hadSomeValues = true;
          allInputs.sort(new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
              Affaire affaire1 = null;
              Affaire affaire2 = null;
              if (o1 instanceof CustomerOrder)
                affaire1 = ((CustomerOrder) o1).getAssoAffaireOrders().get(0).getAffaire();
              if (o1 instanceof Invoice && ((Invoice) o1).getCustomerOrder() != null)
                affaire1 = ((Invoice) o1).getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();
              if (o2 instanceof CustomerOrder)
                affaire2 = ((CustomerOrder) o2).getAssoAffaireOrders().get(0).getAffaire();
              if (o2 instanceof Invoice && ((Invoice) o2).getCustomerOrder() != null)
                affaire2 = ((Invoice) o2).getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();

              if (affaire1 != null && affaire2 == null)
                return 1;
              if (affaire1 == null && affaire2 != null)
                return -1;
              if (affaire1 == null && affaire2 == null)
                return 0;

              if (affaire1 != null && affaire2 != null)
                return (affaire1.getDenomination() != null ? affaire1.getDenomination()
                    : (affaire1.getFirstname() + affaire1.getLastname()))
                    .compareTo((affaire2.getDenomination() != null ? affaire2.getDenomination()
                        : (affaire2.getFirstname() + affaire2.getLastname())));
              return 0;
            }
          });
        }

        for (Object input : allInputs) {
          if (input instanceof CustomerOrder) {
            CustomerOrder customerOrder = (CustomerOrder) input;
            BillingClosureReceiptValue valueCustomerOrder = getBillingClosureReceiptValueForCustomerOrder(
                customerOrder);
            values.add(valueCustomerOrder);
            if (customerOrder.getPayments() != null && customerOrder.getPayments().size() > 0) {
              valueCustomerOrder.setDisplayBottomBorder(false);
              for (Payment payment : customerOrder.getPayments())
                values.add(getBillingClosureReceiptValueForDeposit(payment, customerOrder,
                    customerOrder.getPayments().indexOf(payment) == customerOrder.getPayments().size() - 1));
            }
          }
          if (input instanceof Invoice) {
            Invoice invoice = (Invoice) input;
            BillingClosureReceiptValue valueInvoice = getBillingClosureReceiptValueForInvoice(invoice);
            values.add(valueInvoice);
            if (invoice.getPayments() != null && invoice.getPayments().size() > 0) {
              valueInvoice.setDisplayBottomBorder(false);
              for (Payment payment : invoice.getPayments())
                values.add(getBillingClosureReceiptValueForDeposit(payment, invoice.getCustomerOrder(),
                    invoice.getCustomerOrder().getPayments()
                        .indexOf(payment) == invoice.getCustomerOrder().getPayments().size() - 1));
            }
            if (invoice.getPayments() != null && invoice.getPayments().size() > 0) {
              valueInvoice.setDisplayBottomBorder(false);
              for (Payment payment : invoice.getPayments())
                values.add(getBillingClosureReceiptValueForPayment(payment,
                    invoice.getPayments().indexOf(payment) == invoice.getPayments().size() - 1));
            }

          }
        }
      }

      if (!hadSomeValues)
        values.remove(values.size() - 1);
    }
    return values;
  }

  private BillingClosureReceiptValue getBillingClosureReceiptValueForCustomerOrder(CustomerOrder customerOrder)
      throws OsirisException, OsirisClientMessageException {
    BillingClosureReceiptValue value = new BillingClosureReceiptValue();
    value.setDisplayBottomBorder(true);
    value.setDebitAmount(null);
    value.setCreditAmount(null);
    value.setEventDateTime(customerOrder.getCreatedDate());
    value.setEventDateString(customerOrder.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    value.setEventDescription(
        "Commande n°" + customerOrder.getId() + " - " + customerOrder.getCustomerOrderStatus().getLabel()
            + "<br/>"
            + String.join("<br/>", getAllAffairesLabelForCustomerOrder(customerOrder)).replaceAll("&", "<![CDATA[&]]>")
            + "<br/>"
            + String.join("<br/>", getAllProvisionLabelForCustomerOrder(customerOrder)).replaceAll("&",
                "<![CDATA[&]]>"));

    if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.WAITING_DEPOSIT)) {
      MailComputeResult mailComputeResult = mailComputeHelper
          .computeMailForCustomerOrderFinalizationAndInvoice(customerOrder);
      value.setEventCbLink(paymentCbEntryPoint + "/order/deposit?mail="
          + mailComputeResult.getRecipientsMailTo().get(0).getMail() + "&customerOrderId=" + customerOrder.getId());
    }

    return value;
  }

  private BillingClosureReceiptValue getBillingClosureReceiptValueForInvoice(Invoice invoice)
      throws OsirisException, OsirisClientMessageException {
    BillingClosureReceiptValue value = new BillingClosureReceiptValue();
    value.setDisplayBottomBorder(true);
    value.setDebitAmount(invoice.getTotalPrice());
    value.setCreditAmount(null);
    value.setEventDateTime(invoice.getCreatedDate());
    value.setEventDateString(invoice.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    value.setEventDescription("Facture n°" + invoice.getId());

    if (invoice.getCustomerOrder() != null) {
      String customerOrderValue = invoice.getCustomerOrder().getId() != null
          ? " / Commande n°" + invoice.getCustomerOrder().getId()
          : "";
      value.setEventDescription(
          "Facture n°" + invoice.getId() + customerOrderValue
              + "<br/>"
              + String.join("<br/>", getAllAffairesLabelForCustomerOrder(invoice.getCustomerOrder())).replaceAll("&",
                  "<![CDATA[&]]>")
              + "<br/>"
              + String.join("<br/>", getAllProvisionLabelForCustomerOrder(invoice.getCustomerOrder())).replaceAll("&",
                  "<![CDATA[&]]>"));

      MailComputeResult mailComputeResult = mailComputeHelper
          .computeMailForCustomerOrderFinalizationAndInvoice(invoice.getCustomerOrder());
      value.setEventCbLink(paymentCbEntryPoint + "/order/invoice?mail="
          + mailComputeResult.getRecipientsMailTo().get(0).getMail() + "&customerOrderId="
          + invoice.getCustomerOrder().getId());
    }

    return value;
  }

  private BillingClosureReceiptValue getBillingClosureReceiptValueForDeposit(Payment payment,
      CustomerOrder customerOrder, boolean displayBottomBorder) {
    BillingClosureReceiptValue value = new BillingClosureReceiptValue();
    value.setDisplayBottomBorder(displayBottomBorder);
    value.setDebitAmount(null);
    value.setCreditAmount(payment.getPaymentAmount());
    value.setEventDateTime(payment.getPaymentDate());
    value.setEventDateString(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    String description = payment.getLabel().replaceAll("&", "<![CDATA[&]]>");
    if (customerOrder != null) {
      description += "<br/>"
          + String.join("<br/>", getAllAffairesLabelForCustomerOrder(customerOrder)).replaceAll("&",
              "<![CDATA[&]]>");
    }
    value.setEventDescription(description);

    return value;
  }

  private BillingClosureReceiptValue getBillingClosureReceiptValueForPayment(Payment payment,
      boolean displayBottomBorder) {

    BillingClosureReceiptValue value = new BillingClosureReceiptValue();
    value.setDisplayBottomBorder(displayBottomBorder);
    value.setDebitAmount(null);
    value.setCreditAmount(payment.getPaymentAmount());
    value.setEventDateTime(payment.getPaymentDate());
    value.setEventDateString(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    value.setEventDescription(payment.getLabel().replaceAll("&", "<![CDATA[&]]>"));

    return value;
  }

  private List<String> getAllAffairesLabelForCustomerOrder(CustomerOrder customerOrder) {
    ArrayList<String> affaires = new ArrayList<String>();
    if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
        && customerOrder.getAssoAffaireOrders().size() > 0)
      for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
        Affaire affaire = asso.getAffaire();
        affaires.add((affaire.getDenomination() != null ? affaire.getDenomination()
            : (affaire.getFirstname() + " " + affaire.getLastname())) + ", " + affaire.getAddress() + ", "
            + (affaire.getCity() != null ? affaire.getCity().getLabel() : ""));
      }
    return affaires;
  }

  private List<String> getAllProvisionLabelForCustomerOrder(CustomerOrder customerOrder) {
    ArrayList<String> provisions = new ArrayList<String>();
    if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
        && customerOrder.getAssoAffaireOrders().size() > 0)
      for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
        if (asso.getProvisions() != null && asso.getProvisions().size() > 0)
          for (Provision provision : asso.getProvisions())
            provisions
                .add(provision.getProvisionFamilyType().getLabel() + " - " + provision.getProvisionType().getLabel());
      }
    return provisions;
  }

  private void sendBillingClosureReceiptFile(File billingClosureReceipt, ITiers tiers)
      throws OsirisException, OsirisClientMessageException, OsirisValidationException {
    List<Attachment> attachments = new ArrayList<Attachment>();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

      String tiersType = "";
      if (tiers instanceof Tiers)
        tiersType = Tiers.class.getSimpleName();
      if (tiers instanceof Responsable)
        tiersType = Responsable.class.getSimpleName();
      if (tiers instanceof Confrere)
        tiersType = Confrere.class.getSimpleName();

      List<Attachment> attachmentsList = attachmentService.addAttachment(
          new FileInputStream(billingClosureReceipt), tiers.getId(),
          tiersType, constantService.getAttachmentTypeBillingClosure(),
          "Relevé de compte du " + LocalDateTime.now().format(formatter) + ".pdf", false,
          "Relevé de compte du " + LocalDateTime.now().format(formatter));

      for (Attachment attachment : attachmentsList)
        if (attachment.getUploadedFile().getFilename()
            .equals("Relevé de compte du " + LocalDateTime.now().format(formatter) + ".pdf")) {
          attachments.add(attachment);
          break;
        }
    } catch (FileNotFoundException e) {
      throw new OsirisException(e,
          "Impossible to read excel of billing closure for ITiers " + tiers.getId());
    }

    try {
      mailHelper.sendBillingClosureToCustomer(attachments, tiers, false);
    } catch (Exception e) {
      globalExceptionHandler.persistLog(
          new OsirisException(e, "Impossible to send billing closure mail for Tiers " + tiers.getId()),
          OsirisLog.UNHANDLED_LOG);
    }
  }
}
