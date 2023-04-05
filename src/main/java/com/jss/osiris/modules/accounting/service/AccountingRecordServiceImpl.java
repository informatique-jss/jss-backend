package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
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
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayTransaction;
import com.jss.osiris.modules.quotation.service.CentralPayDelegateService;
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

  @Autowired
  ActiveDirectoryHelper activeDirectoryHelper;

  @Autowired
  GlobalExceptionHandler globalExceptionHandler;

  @Autowired
  CentralPayDelegateService centralPayDelegateService;

  public AccountingRecord addOrUpdateAccountingRecord(
      AccountingRecord accountingRecord) {
    if (accountingRecord.getId() == null
        && (accountingRecord.getCreditAmount() == null || accountingRecord.getCreditAmount() == 0f)
        && (accountingRecord.getDebitAmount() == null || accountingRecord.getDebitAmount() == 0f))
      return null;
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

  @Override
  public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws OsirisException {
    AccountingJournal salesJournal = constantService.getAccountingJournalSales();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
      throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    Float balance = 0f;
    balance += invoiceHelper.getPriceTotal(invoice);

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(),
        labelPrefix, null,
        balance,
        accountingAccountCustomer, null, invoice, null, salesJournal, null, null, null, null);

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

      generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
          invoice.getManualAccountingDocumentDate(),
          labelPrefix + " - produit "
              + invoiceItem.getBillingItem().getBillingType().getLabel(),
          invoiceItem.getPreTaxPrice()
              - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f),
          null, producAccountingAccount,
          invoiceItem, invoice, null, salesJournal, null, null, null, null);

      balance -= invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
        generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
            invoice.getManualAccountingDocumentDate(),
            labelPrefix + " - TVA pour le produit "
                + invoiceItem.getBillingItem().getBillingType().getLabel(),
            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
            invoiceItem, invoice, null, salesJournal, null, null, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    // Check balance ok
    if (Math.round(balance * 100f) / 100f != 0) {
      throw new OsirisException(null, "Accounting records  are not balanced for invoice " + invoice.getId());
    }
  }

  @Override
  public void generateAccountingRecordsForPurshaseOnInvoiceGeneration(Invoice invoice) throws OsirisException {
    AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    Float balance = 0f;
    balance += invoiceHelper.getPriceTotal(invoice);

    // One write on provider account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(),
        labelPrefix, balance, null, accountingAccountProvider, null, invoice, null, pushasingJournal, null, null, null,
        null);

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

      generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
          invoice.getManualAccountingDocumentDate(),
          labelPrefix + " - charge "
              + invoiceItem.getBillingItem().getBillingType().getLabel(),
          null,
          invoiceItem.getPreTaxPrice()
              - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f),
          chargeAccountingAccount,
          invoiceItem, invoice, null, pushasingJournal, null, null, null, null);

      balance -= invoiceItem.getPreTaxPrice()
          - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f);

      if (invoiceItem.getVat() != null) {

        if (invoiceItem.getVat().getAccountingAccount() == null)
          throw new OsirisException(null, "No accounting account for VAT " + invoiceItem.getVat().getLabel());

        if (invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0)
          generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
              invoice.getManualAccountingDocumentDate(),
              labelPrefix + " - TVA pour la charge "
                  + invoiceItem.getBillingItem().getBillingType().getLabel(),
              null, invoiceItem.getVatPrice(), invoiceItem.getVat().getAccountingAccount(),
              invoiceItem, invoice, null, pushasingJournal, null, null, null, null);

        balance -= invoiceItem.getVatPrice();
      }
    }

    // Check balance ok
    if (Math.round(balance * 100f) / 100f != 0) {
      throw new OsirisException(null, "Accounting records  are not balanced for invoice " + invoice.getId());
    }
  }

  @Override
  public void generateAccountingRecordsForSaleOnInvoicePayment(Invoice invoice, Payment payment)
      throws OsirisException {

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (payment == null)
      throw new OsirisException(null, "No payments nor deposits provided with invoice " + invoice.getId());

    AccountingJournal journal = payment.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId())
        ? constantService.getAccountingJournalCash()
        : constantService.getAccountingJournalBank();

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    Integer operationId = 0;

    if (payment != null) {
      Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
      if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
        for (AccountingRecord accountingRecord : payment.getAccountingRecords())
          // Counter part waiting account record
          if (accountingRecord.getIsCounterPart() == null || !accountingRecord.getIsCounterPart())
            if (accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().getId()
                .equals(constantService.getPrincipalAccountingAccountWaiting().getId()))
              letterWaitingRecords(accountingRecord,
                  generateCounterPart(accountingRecord, operationIdCounterPart, journal));
      }
      operationId = invoice.getId() + payment.getId();
    }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Réglement de la facture n°" + invoice.getId(), payment.getPaymentAmount(), null,
        accountingAccountCustomer, null, invoice, null, journal, null, null, null, null);

    // Trigger lettrage
    checkInvoiceForLettrage(invoice);
  }

  @Override
  public void generateAccountingRecordsForPurshaseOnInvoicePayment(Invoice invoice, List<Payment> payments,
      Float amountToUse) throws OsirisException {

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if ((payments == null || payments.size() == 0))
      throw new OsirisException(null, "No payments provided with invoice " + invoice.getId());

    AccountingJournal bankJournal = payments.get(0).getPaymentType().getId()
        .equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    AccountingAccount waitingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    Integer operationId = 0;
    Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
    if (payments != null && payments.size() > 0)
      for (Payment payment : payments) {
        if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0)
          for (AccountingRecord accountingRecord : payment.getAccountingRecords())
            // Counter part waiting account record
            if ((accountingRecord.getIsCounterPart() == null || !accountingRecord.getIsCounterPart())
                && accountingRecord.getAccountingAccount().getId().equals(waitingAccountingAccount.getId()))
              letterWaitingRecords(accountingRecord,
                  generateCounterPart(accountingRecord, operationIdCounterPart, bankJournal));
      }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Réglement de la facture n°" + invoice.getId(), null, amountToUse,
        accountingAccountProvider, null, invoice, null, bankJournal, null, null, null, null);

    // Trigger lettrage
    checkInvoiceForLettrage(invoice);
  }

  @Override
  public void generateAccountingRecordsForDepositOnInvoice(Deposit deposit, Invoice invoice,
      Integer overrideAccountingOperationId, boolean isFromOriginPayment) throws OsirisException {
    AccountingAccount customerAccountingAccount = getCustomerAccountingAccountForInvoice(invoice);
    AccountingJournal journal = deposit.getOriginPayment() != null
        && deposit.getOriginPayment().getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    if (!isFromOriginPayment)
      journal = constantService.getAccountingJournalMiscellaneousOperations();

    Integer operationId = deposit.getId();
    if (overrideAccountingOperationId != null)
      operationId = overrideAccountingOperationId;
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Acompte pour la facture n°" + invoice.getId(), deposit.getDepositAmount(), null, customerAccountingAccount,
        null, invoice, null, journal, null, deposit, null, null);
    checkInvoiceForLettrage(invoice);
  }

  @Override
  public void generateAccountingRecordsForDepositAndCustomerOrder(Deposit deposit, CustomerOrder customerOrder,
      Integer overrideAccountingOperationId, boolean isFromOriginPayment) throws OsirisException {

    AccountingAccount depositAccountingAccount = getDepositAccountingAccountForCustomerOrder(
        quotationService.getCustomerOrderOfQuotation(customerOrder));
    AccountingJournal journal = deposit.getOriginPayment() != null
        && deposit.getOriginPayment().getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    if (!isFromOriginPayment)
      journal = constantService.getAccountingJournalMiscellaneousOperations();

    // If deposit is created from a payment, use the payment ID as operation ID to
    // keep the balance to 0 for a same operationID
    Integer operationId = deposit.getId();
    if (overrideAccountingOperationId != null)
      operationId = overrideAccountingOperationId;

    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Acompte pour la commande n°" + customerOrder.getId(), deposit.getDepositAmount(), null,
        depositAccountingAccount,
        null, null, customerOrder, journal, null, deposit, null, null);
  }

  @Override
  public void generateAppointForPayment(Payment payment, float remainingMoney, ITiers customerOrder)
      throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();

    if (remainingMoney > 0) {
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
          "Appoint pour le paiement n°" + payment.getId(),
          remainingMoney, null, accountingAccountService.getProfitAccountingAccount(), null, null, null,
          bankJournal, payment, null, null, null);
    } else if (remainingMoney < 0) {
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
          "Appoint pour le paiement n°" + payment.getId(),
          null, remainingMoney, accountingAccountService.getLostAccountingAccount(), null, null, null,
          bankJournal, payment, null, null, null);
    }
  }

  @Override
  public void generateAppointForDeposit(Deposit deposit, float remainingMoney, ITiers customerOrder)
      throws OsirisException {
    AccountingJournal miscellaneousJournal = constantService.getAccountingJournalMiscellaneousOperations();

    if (remainingMoney > 0) {
      generateNewAccountingRecord(LocalDateTime.now(), deposit.getId(), null, null,
          "Appoint pour l'acompte n°" + deposit.getId(),
          remainingMoney, null, accountingAccountService.getProfitAccountingAccount(), null, null, null,
          miscellaneousJournal, null, deposit, null, null);
    } else if (remainingMoney < 0) {
      generateNewAccountingRecord(LocalDateTime.now(), deposit.getId(), null, null,
          "Appoint pour l'acompte n°" + deposit.getId(),
          null, remainingMoney, accountingAccountService.getLostAccountingAccount(), null, null, null,
          miscellaneousJournal, null, deposit, null, null);
    }
  }

  @Override
  public void generateAccountingRecordsForCentralPayPayment(CentralPayPaymentRequest centralPayPaymentRequest,
      Payment payment, Deposit deposit, CustomerOrder customerOrder, Invoice invoice) throws OsirisException {

    AccountingAccount accountingAccountBankCentralPay = constantService.getAccountingAccountBankCentralPay();
    AccountingJournal accountingJournalSales = constantService.getAccountingJournalSales();
    AccountingJournal accountingJournalPurshases = constantService.getAccountingJournalPurchases();
    AccountingJournal accountingJournalBank = constantService.getAccountingJournalBank();
    BillingType billingTypeCentralPayCommission = constantService.getBillingTypeCentralPayFees();

    if (accountingAccountBankCentralPay == null)
      throw new OsirisException(null, "Accounting account for Central Pay not defined in constants");
    if (accountingJournalSales == null)
      throw new OsirisException(null, "Accounting journal Sales not defined in constants");
    if (accountingJournalPurshases == null)
      throw new OsirisException(null, "Accounting journal Purshases not defined in constants");
    if (billingTypeCentralPayCommission == null)
      throw new OsirisException(null, "Billing type central pay fees not defined in constants");
    if (billingTypeCentralPayCommission.getVat() == null)
      throw new OsirisException(null, "VAT not defined in billing type central pay fees");
    if (billingTypeCentralPayCommission.getAccountingAccountCharge() == null)
      throw new OsirisException(null, "Charge accounting account not defined in billing type central pay fees");
    if (billingTypeCentralPayCommission.getAccountingAccountCharge() == null)
      throw new OsirisException(null, "Charge accounting account not defined in billing type central pay fees");
    if (billingTypeCentralPayCommission.getVat().getRate() == null)
      throw new OsirisException(null, "Rate not defined in VAT of billing type central pay fees");
    if (invoice == null && deposit == null)
      throw new OsirisException(null, "Must provide at least an invoice or a deposit");

    String label = "";
    if (deposit != null)
      label = "Paiement d'acompte pour la commande n°" + customerOrder.getId();
    else if (invoice != null)
      label = "Paiement pour la facture " + invoice.getId();

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, null, payment.getPaymentAmount(),
        accountingAccountBankCentralPay, null,
        invoice, customerOrder, accountingJournalBank, payment, deposit, null, null);

    CentralPayTransaction transaction = centralPayDelegateService.getTransaction(centralPayPaymentRequest);

    Float commission = (transaction.getCommission() != null ? transaction.getCommission() : 0f) / 100f;
    Float preTaxPrice = commission / ((100 + billingTypeCentralPayCommission.getVat().getRate()) / 100f);
    Float taxPrice = commission - preTaxPrice;

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, commission, null,
        accountingAccountBankCentralPay, null, invoice, customerOrder, accountingJournalPurshases, payment, deposit,
        null, null);

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, null, preTaxPrice,
        billingTypeCentralPayCommission.getAccountingAccountCharge(), null, invoice, customerOrder,
        accountingJournalPurshases, payment, deposit, null, null);

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, null, taxPrice,
        billingTypeCentralPayCommission.getVat().getAccountingAccount(), null, invoice, customerOrder,
        accountingJournalPurshases, payment, deposit, null, null);

  }

  @Override
  public void generateAccountingRecordsForWaitingInboundPayment(Payment payment) throws OsirisException {
    AccountingAccount waitingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Mise en attente du règlement n°" + payment.getId(), payment.getPaymentAmount(), null, waitingAccountingAccount,
        null, null, null, bankJournal, payment, null, null, null);
  }

  @Override
  public void generateAccountingRecordsForWaintingOutboundPayment(Payment payment) throws OsirisException {
    AccountingAccount waitingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Mise en attente du règlement n°" + payment.getId(), null, payment.getPaymentAmount(), waitingAccountingAccount,
        null, null, null, bankJournal, payment, null, null, null);
  }

  @Override
  public void generateAccountingRecordsForRefundOnVirement(Refund refund) throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    AccountingAccount customerAccountingAccount = null;
    if (refund.getConfrere() != null) {
      customerAccountingAccount = getCustomerAccountingAccountForITiers(refund.getConfrere());
    } else {
      customerAccountingAccount = getCustomerAccountingAccountForITiers(refund.getTiers());
    }
    generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null, "Remboursement n°" + refund.getId(),
        refund.getRefundAmount(), null, constantService.getAccountingAccountBankJss(), null, null, null,
        bankJournal, null, null, null, refund);
    generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null, "Remboursement n°" + refund.getId(),
        null, refund.getRefundAmount(), customerAccountingAccount, null, null, null,
        bankJournal, null, null, null, refund);
  }

  @Override
  public void generateAccountingRecordsForRefundOnGeneration(Refund refund) throws OsirisException {
    // Bank if refund payment, miscellaneous if we refund deposit
    AccountingJournal bankJournal = refund.getPayment() != null ? constantService.getAccountingJournalBank()
        : constantService.getAccountingJournalMiscellaneousOperations();
    AccountingAccount customerAccountingAccount = null;
    if (refund.getConfrere() != null) {
      customerAccountingAccount = getCustomerAccountingAccountForITiers(refund.getConfrere());
    } else {
      customerAccountingAccount = getCustomerAccountingAccountForITiers(refund.getTiers());
    }
    generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null, "Remboursement n°" + refund.getId(),
        refund.getRefundAmount(), null, customerAccountingAccount, null, null, null,
        bankJournal, null, null, null, refund);
  }

  @Override
  public void generateBankAccountingRecordsForInboundPayment(Payment payment) throws OsirisException {
    AccountingJournal bankJournal = payment.getPaymentType().getId()
        .equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Paiement n°" + payment.getId(), null, payment.getPaymentAmount(),
        constantService.getAccountingAccountBankJss(),
        null, null, null, bankJournal, payment, null, null, null);

  }

  @Override
  public void generateBankAccountingRecordsForOutboundPayment(Payment payment) throws OsirisException {
    AccountingJournal bankJournal = payment.getPaymentType().getId()
        .equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Paiement n°" + payment.getId(), payment.getPaymentAmount(), null,
        constantService.getAccountingAccountBankJss(),
        null, null, null, bankJournal, payment, null, null, null);
  }

  @Override
  public void generateBankAccountingRecordsForInboundCashPayment(Payment payment) throws OsirisException {
    AccountingJournal cashJournal = constantService.getAccountingJournalCash();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Paiement n°" + payment.getId(), null, payment.getPaymentAmount(),
        constantService.getAccountingAccountCaisse(),
        null, null, null, cashJournal, payment, null, null, null);
  }

  @Override
  public void generateBankAccountingRecordsForOutboundDebourPayment(Debour debour,
      CustomerOrder customerOrder) throws OsirisException {
    if (debour.getPaymentType() != null)
      if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeCheques().getId()))
        generateBankAccountingRecordsForOutboundDebourCheckOrBankTransfertPayment(debour, customerOrder);
      else if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeCB().getId()))
        generateBankAccountingRecordsForOutboundDebourCreditCardPayment(debour, customerOrder);
      else if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId()))
        generateBankAccountingRecordsForOutboundDebourCheckOrBankTransfertPayment(debour, customerOrder);
      else if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeEspeces().getId()))
        generateBankAccountingRecordsForOutboundDebourCashPayment(debour, customerOrder);
      else if (debour.getPaymentType().getId().equals(constantService.getPaymentTypeAccount().getId()))
        generateBankAccountingRecordsForOutboundDebourAccountPayment(debour, customerOrder);
  }

  private void generateBankAccountingRecordsForOutboundDebourCreditCardPayment(Debour debour,
      CustomerOrder customerOrder) throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();

    if (debour.getBillingType().getAccountingAccountCharge() == null)
      throw new OsirisException(
          null, "No accounting account for charge for billing type n°" + debour.getBillingType().getId());

    generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
        "Debour n°" + debour.getId(), debour.getDebourAmount(), null,
        constantService.getAccountingAccountBankJss(),
        null, null, customerOrder, bankJournal, null, null, debour, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, bankJournal, null, null, debour, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getCompetentAuthority().getAccountingAccountProvider(),
          null, null, customerOrder, bankJournal, null, null, debour, null);
  }

  private void generateBankAccountingRecordsForOutboundDebourCheckOrBankTransfertPayment(Debour debour,
      CustomerOrder customerOrder) throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();

    if (debour.getBillingType().getAccountingAccountCharge() == null)
      throw new OsirisException(
          null, "No accounting account for charge for billing type n°" + debour.getBillingType().getId());

    generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
        "Debour n°" + debour.getId(), debour.getDebourAmount(), null,
        constantService.getAccountingAccountBankJss(),
        null, null, customerOrder, bankJournal, null, null, debour, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, bankJournal, null, null, debour, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getCompetentAuthority().getAccountingAccountProvider(),
          null, null, customerOrder, bankJournal, null, null, debour, null);
  }

  private void generateBankAccountingRecordsForOutboundDebourCashPayment(Debour debour,
      CustomerOrder customerOrder) throws OsirisException {
    AccountingJournal cashJournal = constantService.getAccountingJournalCash();

    if (debour.getBillingType().getAccountingAccountCharge() == null)
      throw new OsirisException(
          null, "No accounting account for charge for billing type n°" + debour.getBillingType().getId());

    generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
        "Debour n°" + debour.getId(), debour.getDebourAmount(), null,
        constantService.getAccountingAccountCaisse(),
        null, null, customerOrder, cashJournal, null, null, debour, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, cashJournal, null, null, debour, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getCompetentAuthority().getAccountingAccountProvider(),
          null, null, customerOrder, cashJournal, null, null, debour, null);
  }

  private void generateBankAccountingRecordsForOutboundDebourAccountPayment(Debour debour,
      CustomerOrder customerOrder) throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();

    if (debour.getBillingType().getAccountingAccountCharge() == null)
      throw new OsirisException(
          null, "No accounting account for charge for billing type n°" + debour.getBillingType().getId());

    generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
        "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
        debour.getCompetentAuthority().getAccountingAccountProvider(),
        null, null, customerOrder, bankJournal, null, null, debour, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), debour.getDebourAmount(), null,
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, bankJournal, null, null, debour, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), debour.getDebourAmount(), null,
          debour.getCompetentAuthority().getAccountingAccountDepositProvider(),
          null, null, customerOrder, bankJournal, null, null, debour, null);
  }

  @Override
  public void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
    AccountingAccount accountingAccount;
    invoice = invoiceService.getInvoice(invoice.getId());
    if (invoice.getProvider() != null || invoice.getCompetentAuthority() != null)
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
    }

    if (Math.round(balance * 100f) / 100f == 0) {

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

      invoice.setInvoiceStatus(constantService.getInvoiceStatusPayed());
      invoiceService.addOrUpdateInvoice(invoice);
    }

  }

  @Override
  public void letterWaitingRecords(AccountingRecord record, AccountingRecord counterPart) throws OsirisException {
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
    AccountingAccount accountingAccountCustomer = null;
    if (tiers instanceof Responsable)
      accountingAccountCustomer = ((Responsable) tiers).getTiers().getAccountingAccountCustomer();
    else
      accountingAccountCustomer = tiers.getAccountingAccountCustomer();

    if (accountingAccountCustomer == null)
      throw new OsirisException(null, "No customer accounting account in ITiers " + tiers.getId());

    return accountingAccountCustomer;
  }

  @Override
  public AccountingAccount getProviderAccountingAccountForInvoice(Invoice invoice) throws OsirisException {
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

  @Override
  public AccountingAccount getProviderAccountingAccountForITiers(ITiers tiers) throws OsirisException {
    // If cusomter order is a Responsable, get accounting account of parent Tiers
    if (tiers.getAccountingAccountProvider() == null || (tiers instanceof Responsable
        && ((Responsable) tiers).getTiers().getAccountingAccountProvider() == null))
      throw new OsirisException(null, "No customer accounting account in ITiers " + tiers.getId());

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
      throw new OsirisException(null, "No customer accounting account in ITiers " + customerOrder.getId());

    AccountingAccount accountingAccountDeposit = null;
    if (customerOrder instanceof Responsable)
      accountingAccountDeposit = ((Responsable) customerOrder).getTiers().getAccountingAccountDeposit();
    else
      accountingAccountDeposit = customerOrder.getAccountingAccountDeposit();

    return accountingAccountDeposit;
  }

  private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime, Integer operationId,
      String manualAccountingDocumentNumber, LocalDate manualAccountingDocumentDate,
      String label, Float creditAmount, Float debitAmount,
      AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice, CustomerOrder customerOrder,
      AccountingJournal journal,
      Payment payment, Deposit deposit, Debour debour, Refund refund) {
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
    accountingRecord.setDeposit(deposit);
    accountingRecord.setIsCounterPart(false);
    accountingRecord.setDebour(debour);
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

    if (accountingRecordSearch.getResponsableId() == null)
      accountingRecordSearch.setResponsableId(0);

    if (accountingRecordSearch.getConfrereId() == null)
      accountingRecordSearch.setConfrereId(0);

    if (accountingRecordSearch.getStartDate() == null)
      accountingRecordSearch.setStartDate(LocalDateTime.now().minusYears(100));

    if (accountingRecordSearch.getEndDate() == null)
      accountingRecordSearch.setEndDate(LocalDateTime.now().plusYears(100));

    return accountingRecordRepository.searchAccountingRecords(accountingAccountId, accountingClass, journalId,
        accountingRecordSearch.getResponsableId(), accountingRecordSearch.getTiersId(),
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
        .getAccountingRecordAggregateByAccountingNumber(startDate, endDate,
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1),
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    ArrayList<AccountingBalanceViewTitle> outBilan = new ArrayList<AccountingBalanceViewTitle>();

    outBilan.add(accountingBalanceHelper.getBilanActif(accountingRecords, accountingRecordsN1));
    outBilan.add(accountingBalanceHelper.getBilanPassif(accountingRecords, accountingRecordsN1));

    return outBilan;
  }

  @Override
  public List<AccountingBalanceViewTitle> getProfitAndLost(LocalDateTime startDate, LocalDateTime endDate) {
    List<AccountingBalanceBilan> accountingRecords = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate, endDate,
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1),
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
        .getAccountingRecordAggregateByAccountingNumber(startDate, endDate,
            activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP));

    List<AccountingBalanceBilan> accountingRecordsN1 = accountingRecordRepository
        .getAccountingRecordAggregateByAccountingNumber(startDate.minusYears(1), endDate.minusYears(1),
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
  public AccountingRecord generateCounterPart(AccountingRecord originalAccountingRecord, Integer operationId,
      AccountingJournal journal) {
    AccountingRecord newAccountingRecord = new AccountingRecord();
    newAccountingRecord.setAccountingAccount(originalAccountingRecord.getAccountingAccount());
    newAccountingRecord.setAccountingJournal(journal);
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
    newAccountingRecord.setTemporaryOperationId(operationId);
    newAccountingRecord.setOperationDateTime(LocalDateTime.now());
    newAccountingRecord.setCustomerOrder(originalAccountingRecord.getCustomerOrder());
    newAccountingRecord.setInvoice(originalAccountingRecord.getInvoice());
    newAccountingRecord.setIsCounterPart(true);
    addOrUpdateAccountingRecord(newAccountingRecord);
    originalAccountingRecord.setContrePasse(newAccountingRecord);
    addOrUpdateAccountingRecord(originalAccountingRecord);
    return newAccountingRecord;
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
        generateCounterPart(accountingRecord, operationIdCounterPart, accountingRecord.getAccountingJournal());
        accountingRecord.setInvoice(null);
        accountingRecord.setPayment(null);
        accountingRecord.setDeposit(null);
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
  public AccountingRecord unassociateCustomerOrderPayementAndDeposit(AccountingRecord accountingRecord) {
    accountingRecord.setCustomerOrder(null);
    accountingRecord.setDeposit(null);
    accountingRecord.setPayment(null);
    return addOrUpdateAccountingRecord(accountingRecord);
  }

  @Transactional
  public void sendBillingClosureReceipt()
      throws OsirisException, OsirisClientMessageException, OsirisValidationException {
    List<Tiers> tiers = tiersService.findAllTiersTypeClient();
    if (tiers != null)
      for (Tiers tier : tiers) {
        Document billingClosureDocument = documentService.getBillingClosureDocument(tier.getDocuments());
        if (billingClosureDocument != null)
          if (billingClosureDocument.getBillingClosureRecipientType() != null
              && (billingClosureDocument.getBillingClosureRecipientType().getId()
                  .equals(constantService.getBillingClosureRecipientTypeClient().getId())
                  || billingClosureDocument.getBillingClosureRecipientType().getId()
                      .equals(constantService.getBillingClosureRecipientTypeOther().getId()))) {
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
                throw new OsirisException(e, "Impossible to read excel of billing closure for tiers " + tier.getId());
              }
              try {
                mailHelper.sendBillingClosureToCustomer(attachments, tier, false);
              } catch (Exception e) {
                globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
              }
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
                    throw new OsirisException(e,
                        "Impossible to read excel of billing closure for responsable " + responsable.getId());
                  }

                  try {
                    mailHelper.sendBillingClosureToCustomer(attachments, responsable, false);
                  } catch (Exception e) {
                    globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
                  }
                }
              }
            }
          }
      }

    // TODO confrères
  }

}
