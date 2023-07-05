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
import com.jss.osiris.modules.invoicing.model.Appoint;
import com.jss.osiris.modules.invoicing.model.Deposit;
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
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentRequest;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayTransaction;
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

  @Override
  public AccountingRecord getAccountingRecord(Integer id) {
    Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
    if (accountingRecord.isPresent())
      return accountingRecord.get();
    return null;
  }

  @Override
  public List<AccountingRecord> getAccountingRecordForDebour(Debour debour) {
    return accountingRecordRepository.findByDebour(debour);
  }

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
  public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    AccountingJournal salesJournal = constantService.getAccountingJournalSales();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (invoice.getCustomerOrder() == null && invoiceHelper.getCustomerOrder(invoice) == null)
      throw new OsirisException(null, "No customer order or ITiers in invoice " + invoice.getId());

    String labelPrefix = invoice.getCustomerOrder() != null ? ("Commande n°" + invoice.getCustomerOrder().getId())
        : ("Facture libre n°" + invoice.getId());

    AccountingAccount accountingAccountCustomer = getCustomerAccountingAccountForInvoice(invoice);

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(),
        labelPrefix, null,
        invoiceHelper.getPriceTotal(invoice),
        accountingAccountCustomer, null, invoice, null, salesJournal, null, null, null, null, null);

    // For each invoice item, one write on product and VAT account for each invoice
    // item
    for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
      if (invoiceItem.getBillingItem() == null)
        throw new OsirisException(null, "No billing item defined in invoice item n°" + invoiceItem.getId());

      if (invoiceItem.getBillingItem().getBillingType() == null)
        throw new OsirisException(null,
            "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

      if (!invoiceItem.getBillingItem().getBillingType().getIsDebour()
          && !invoiceItem.getBillingItem().getBillingType().getIsFee()) {
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
            invoiceItem, invoice, null, salesJournal, null, null, null, null, null);

        if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
          generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
              invoice.getManualAccountingDocumentDate(),
              labelPrefix + " - TVA pour le produit "
                  + invoiceItem.getBillingItem().getBillingType().getLabel(),
              invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
              invoiceItem, invoice, null, salesJournal, null, null, null, null, null);

        }
      } else {
        if (invoice.getCustomerOrder() != null && invoice.getCustomerOrder().getAssoAffaireOrders() != null) {
          for (AssoAffaireOrder asso : invoice.getCustomerOrder().getAssoAffaireOrders()) {
            if (asso.getProvisions() != null) {
              for (Provision provision : asso.getProvisions()) {
                if (provision.getDebours() != null && provision.getDebours().size() > 0) {
                  for (Debour debour : provision.getDebours()) {

                    Vat vatDebour = vatService.getGeographicalApplicableVatForSales(invoice,
                        constantService.getVatDeductible());

                    // Compute debour prices
                    Float total = 0f;
                    Float debourAmount = debour.getInvoicedAmount() != null ? debour.getInvoicedAmount()
                        : debour.getDebourAmount();
                    if (debour.getBillingType().getIsNonTaxable() || vatDebour == null)
                      total += debourAmount;
                    else
                      total += debourAmount / ((100 + vatDebour.getRate()) / 100f);
                    Float vatAmount = debourAmount - total;

                    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(),
                        invoice.getManualAccountingDocumentNumber(), invoice.getManualAccountingDocumentDate(),
                        labelPrefix + " - produit " + debour.getBillingType().getLabel(), total,
                        null, debour.getBillingType().getAccountingAccountProduct(), invoiceItem,
                        invoice, null, salesJournal, null, null, null, null, null);

                    if (vatAmount > 0 && vatDebour != null) {
                      generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(),
                          invoice.getManualAccountingDocumentNumber(), invoice.getManualAccountingDocumentDate(),
                          labelPrefix + " - TVA pour le produit "
                              + invoiceItem.getBillingItem().getBillingType().getLabel(),
                          vatAmount, null, vatDebour.getAccountingAccount(),
                          invoiceItem, invoice, null, salesJournal, null, null, null, null, null);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

  }

  @Override
  public void generateAccountingRecordsForPurshaseOnInvoiceRefund(Invoice invoice)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    AccountingJournal pushasingJournal = constantService.getAccountingJournalPurchases();

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    String labelPrefix = "Avoir n°" + invoice.getId();

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    // One write on customer account for all invoice
    generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
        invoice.getManualAccountingDocumentDate(),
        labelPrefix, null,
        invoiceHelper.getPriceTotal(invoice),
        accountingAccountProvider, null, invoice, null, pushasingJournal, null, null, null, null, null);

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

      generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
          invoice.getManualAccountingDocumentDate(),
          labelPrefix + " - charge "
              + invoiceItem.getBillingItem().getBillingType().getLabel(),
          invoiceItem.getPreTaxPrice()
              - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount() : 0f),
          null, chargeAccountingAccount,
          invoiceItem, invoice, null, pushasingJournal, null, null, null, null, null);

      if (invoiceItem.getVat() != null && invoiceItem.getVatPrice() != null && invoiceItem.getVatPrice() > 0) {
        generateNewAccountingRecord(LocalDateTime.now(), invoice.getId(), invoice.getManualAccountingDocumentNumber(),
            invoice.getManualAccountingDocumentDate(),
            labelPrefix + " - TVA pour la charge "
                + invoiceItem.getBillingItem().getBillingType().getLabel(),
            invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
            invoiceItem, invoice, null, pushasingJournal, null, null, null, null, null);

      }
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
        null, null);

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
          invoiceItem, invoice, null, pushasingJournal, null, null, null, null, null);

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
              invoiceItem, invoice, null, pushasingJournal, null, null, null, null, null);

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
              letterCounterPartRecords(accountingRecord,
                  generateCounterPart(accountingRecord, operationIdCounterPart, journal));
      }
      operationId = invoice.getId() + payment.getId();
    }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Réglement de la facture n°" + invoice.getId(), payment.getPaymentAmount(), null,
        accountingAccountCustomer, null, invoice, null, journal, null, null, null, null, null);

    // Trigger lettrage
    checkInvoiceForLettrage(invoice);
  }

  @Override
  public void generateAccountingRecordsForProviderInvoiceRefund(Invoice invoice, Payment payment)
      throws OsirisException {

    if (invoice == null)
      throw new OsirisException(null, "No invoice provided");

    if (payment == null)
      throw new OsirisException(null, "No payments nor deposits provided with invoice " + invoice.getId());

    AccountingJournal journal = constantService.getAccountingJournalBank();

    AccountingAccount accountingAccountProvider = getProviderAccountingAccountForInvoice(invoice);

    Integer operationId = 0;

    if (payment != null) {
      Integer operationIdCounterPart = ThreadLocalRandom.current().nextInt(1, 1000000000);
      if (payment.getAccountingRecords() != null && payment.getAccountingRecords().size() > 0) {
        for (AccountingRecord accountingRecord : payment.getAccountingRecords())
          // Counter part waiting account record
          if (accountingRecord.getIsCounterPart() == null || !accountingRecord.getIsCounterPart())
            if (accountingRecord.getAccountingAccount().getPrincipalAccountingAccount().getId()
                .equals(constantService.getPrincipalAccountingAccountWaiting().getId()))
              letterCounterPartRecords(accountingRecord,
                  generateCounterPart(accountingRecord, operationIdCounterPart, journal));
      }
      operationId = invoice.getId() + payment.getId();
    }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Remboursement de la facture n°" + invoice.getId(), payment.getPaymentAmount(), null,
        accountingAccountProvider, null, invoice, null, journal, null, null, null, null, null);
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
              letterCounterPartRecords(accountingRecord,
                  generateCounterPart(accountingRecord, operationIdCounterPart, bankJournal));
      }

    // One write on customer account to equilibrate invoice
    generateNewAccountingRecord(LocalDateTime.now(), operationId, null, null,
        "Réglement de la facture n°" + invoice.getId(), null, amountToUse,
        accountingAccountProvider, null, invoice, null, bankJournal, null, null, null, null, null);

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
        null, invoice, null, journal, null, deposit, null, null, null);
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
        null, null, customerOrder, journal, null, deposit, null, null, null);
  }

  @Override
  public void generateAppointForPayment(Payment payment, float remainingMoney, ITiers customerOrder, Appoint appoint,
      Invoice invoice)
      throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();

    if (remainingMoney > 0) {
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
          "Appoint pour le paiement n°" + payment.getId(),
          remainingMoney, null, accountingAccountService.getProfitAccountingAccount(), null, invoice, null,
          bankJournal, payment, null, null, null, appoint);
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
          "Appoint pour le paiement n°" + payment.getId(),
          null, remainingMoney, customerOrder.getAccountingAccountCustomer(), null, invoice, null,
          bankJournal, payment, null, null, null, appoint);
    } else if (remainingMoney < 0) {
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
          "Appoint pour le paiement n°" + payment.getId(),
          null, Math.abs(remainingMoney), accountingAccountService.getLostAccountingAccount(), null, invoice, null,
          bankJournal, payment, null, null, null, appoint);
      generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
          "Appoint pour le paiement n°" + payment.getId(),
          Math.abs(remainingMoney), null, customerOrder.getAccountingAccountCustomer(), null, invoice, null,
          bankJournal, payment, null, null, null, appoint);
    }
  }

  @Override
  public void generateAppointForDeposit(Deposit deposit, float remainingMoney, ITiers customerOrder, Appoint appoint,
      Invoice invoice)
      throws OsirisException {
    AccountingJournal miscellaneousJournal = constantService.getAccountingJournalMiscellaneousOperations();

    if (remainingMoney > 0) {
      generateNewAccountingRecord(LocalDateTime.now(), deposit.getId(), null, null,
          "Appoint pour l'acompte n°" + deposit.getId(),
          remainingMoney, null, accountingAccountService.getProfitAccountingAccount(), null, invoice, null,
          miscellaneousJournal, null, deposit, null, null, appoint);
      generateNewAccountingRecord(LocalDateTime.now(), deposit.getId(), null, null,
          "Appoint pour l'acompte n°" + deposit.getId(),
          null, remainingMoney, customerOrder.getAccountingAccountCustomer(), null, invoice, null,
          miscellaneousJournal, null, deposit, null, null, appoint);
    } else if (remainingMoney < 0) {
      generateNewAccountingRecord(LocalDateTime.now(), deposit.getId(), null, null,
          "Appoint pour l'acompte n°" + deposit.getId(),
          null, Math.abs(remainingMoney), accountingAccountService.getLostAccountingAccount(), null, invoice, null,
          miscellaneousJournal, null, deposit, null, null, appoint);
      generateNewAccountingRecord(LocalDateTime.now(), deposit.getId(), null, null,
          "Appoint pour l'acompte n°" + deposit.getId(),
          Math.abs(remainingMoney), null, customerOrder.getAccountingAccountCustomer(), null, invoice, null,
          miscellaneousJournal, null, deposit, null, null, appoint);
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
        invoice, customerOrder, accountingJournalBank, payment, deposit, null, null, null);

    CentralPayTransaction transaction = centralPayDelegateService.getTransaction(centralPayPaymentRequest);

    Float commission = (transaction.getCommission() != null ? transaction.getCommission() : 0f) / 100f;
    Float preTaxPrice = commission / ((100 + billingTypeCentralPayCommission.getVat().getRate()) / 100f);
    Float taxPrice = commission - preTaxPrice;

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, commission, null,
        accountingAccountBankCentralPay, null, invoice, customerOrder, accountingJournalPurshases, payment, deposit,
        null, null, null);

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, null, preTaxPrice,
        billingTypeCentralPayCommission.getAccountingAccountCharge(), null, invoice, customerOrder,
        accountingJournalPurshases, payment, deposit, null, null, null);

    generateNewAccountingRecord(payment.getPaymentDate(), payment.getId(), null, null,
        label, null, taxPrice,
        billingTypeCentralPayCommission.getVat().getAccountingAccount(), null, invoice, customerOrder,
        accountingJournalPurshases, payment, deposit, null, null, null);

  }

  @Override
  public void generateAccountingRecordsForWaitingInboundPayment(Payment payment) throws OsirisException {
    AccountingAccount waitingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Mise en attente du règlement n°" + payment.getId(), payment.getPaymentAmount(), null, waitingAccountingAccount,
        null, null, null, bankJournal, payment, null, null, null, null);
  }

  @Override
  public void generateAccountingRecordsForWaintingOutboundPayment(Payment payment) throws OsirisException {
    AccountingAccount waitingAccountingAccount = accountingAccountService.getWaitingAccountingAccount();
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();
    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Mise en attente du règlement n°" + payment.getId(), null, payment.getPaymentAmount(), waitingAccountingAccount,
        null, null, null, bankJournal, payment, null, null, null, null);
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

    if (refund.getAppoint() == null) {
      generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null, "Remboursement n°" + refund.getId(),
          refund.getRefundAmount(), null, constantService.getAccountingAccountBankJss(), null, null, null,
          bankJournal, null, null, null, refund, null);
      generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null, "Remboursement n°" + refund.getId(),
          null, refund.getRefundAmount(), customerAccountingAccount, null, null, null,
          bankJournal, null, null, null, refund, null);
    } else {
      generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null,
          "Remboursement n°" + refund.getId(),
          null, refund.getRefundAmount(), accountingAccountService.getProfitAccountingAccount(), null, null, null,
          bankJournal, null, null, null, refund, null);
      generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null,
          "Remboursement n°" + refund.getId(),
          refund.getRefundAmount(), null, customerAccountingAccount, null, null, null,
          bankJournal, null, null, null, refund, null);

      generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null,
          "Remboursement n°" + refund.getId(),
          refund.getRefundAmount(), null, constantService.getAccountingAccountBankJss(), null, null, null,
          bankJournal, null, null, null, refund, null);
      generateNewAccountingRecord(LocalDateTime.now(), refund.getId(), null, null,
          "Remboursement n°" + refund.getId(),
          null, refund.getRefundAmount(), customerAccountingAccount, null, null, null,
          bankJournal, null, null, null, refund, null);
    }
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
        bankJournal, null, null, null, refund, null);
  }

  @Override
  public void generateBankAccountingRecordsForInboundPayment(Payment payment,
      AccountingAccount targetBankAccountingAccount) throws OsirisException {
    AccountingJournal bankJournal = payment.getPaymentType().getId()
        .equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Paiement n°" + payment.getId() + " - " + payment.getLabel(), null, payment.getPaymentAmount(),
        targetBankAccountingAccount != null ? targetBankAccountingAccount
            : constantService.getAccountingAccountBankJss(),
        null, null, null, bankJournal, payment, null, null, null, null);

  }

  @Override
  public void generateBankAccountingRecordsForOutboundPayment(Payment payment) throws OsirisException {
    AccountingJournal bankJournal = payment.getPaymentType().getId()
        .equals(constantService.getPaymentTypeEspeces().getId())
            ? constantService.getAccountingJournalCash()
            : constantService.getAccountingJournalBank();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Paiement n°" + payment.getId() + " - " + payment.getLabel(), payment.getPaymentAmount(), null,
        constantService.getAccountingAccountBankJss(),
        null, null, null, bankJournal, payment, null, null, null, null);
  }

  @Override
  public void generateBankAccountingRecordsForInboundCashPayment(Payment payment) throws OsirisException {
    AccountingJournal cashJournal = constantService.getAccountingJournalCash();

    generateNewAccountingRecord(LocalDateTime.now(), payment.getId(), null, null,
        "Paiement n°" + payment.getId() + " - " + payment.getLabel(), null, payment.getPaymentAmount(),
        constantService.getAccountingAccountCaisse(),
        null, null, null, cashJournal, payment, null, null, null, null);
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
        null, null, customerOrder, bankJournal, null, null, debour, null, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, bankJournal, null, null, debour, null, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getCompetentAuthority().getAccountingAccountProvider(),
          null, null, customerOrder, bankJournal, null, null, debour, null, null);
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
        null, null, customerOrder, bankJournal, null, null, debour, null, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, bankJournal, null, null, debour, null, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getCompetentAuthority().getAccountingAccountProvider(),
          null, null, customerOrder, bankJournal, null, null, debour, null, null);
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
        null, null, customerOrder, cashJournal, null, null, debour, null, null);

    if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getBillingType().getAccountingAccountCharge(),
          null, null, customerOrder, cashJournal, null, null, debour, null, null);
    else
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(),
          debour.getCompetentAuthority().getAccountingAccountProvider(),
          null, null, customerOrder, cashJournal, null, null, debour, null, null);
  }

  private void generateBankAccountingRecordsForOutboundDebourAccountPayment(Debour debour,
      CustomerOrder customerOrder) throws OsirisException {
    AccountingJournal bankJournal = constantService.getAccountingJournalBank();

    if (debour.getBillingType().getAccountingAccountCharge() == null)
      throw new OsirisException(
          null, "No accounting account for charge for billing type n°" + debour.getBillingType().getId());

    AccountingAccount accountingAccountProvider = debour.getCompetentAuthority().getAccountingAccountProvider();
    AccountingAccount accountingAccountDepositProvider = debour.getCompetentAuthority()
        .getAccountingAccountDepositProvider();

    // If debour from INPI, accounting account of INPI is used
    if (debour.getCartRate() != null) {
      accountingAccountProvider = constantService.getCompetentAuthorityInpi().getAccountingAccountProvider();
      accountingAccountDepositProvider = constantService.getCompetentAuthorityInpi()
          .getAccountingAccountDepositProvider();
    }

    if (debour.getDebourAmount() > 0) {
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), null, debour.getDebourAmount(), accountingAccountProvider,
          null, null, customerOrder, bankJournal, null, null, debour, null, null);

      if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
        generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
            "Debour n°" + debour.getId(), debour.getDebourAmount(), null,
            debour.getBillingType().getAccountingAccountCharge(),
            null, null, customerOrder, bankJournal, null, null, debour, null, null);
      else
        generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
            "Debour n°" + debour.getId(), debour.getDebourAmount(), null, accountingAccountDepositProvider,
            null, null, customerOrder, bankJournal, null, null, debour, null, null);
    } else {
      generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
          "Debour n°" + debour.getId(), Math.abs(debour.getDebourAmount()), null, accountingAccountProvider,
          null, null, customerOrder, bankJournal, null, null, debour, null, null);

      if (debour.getCompetentAuthority().getCompetentAuthorityType().getIsDirectCharge())
        generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
            "Debour n°" + debour.getId(), null, Math.abs(debour.getDebourAmount()),
            debour.getBillingType().getAccountingAccountCharge(),
            null, null, customerOrder, bankJournal, null, null, debour, null, null);
      else
        generateNewAccountingRecord(LocalDateTime.now(), debour.getId(), null, null,
            "Debour n°" + debour.getId(), null, Math.abs(debour.getDebourAmount()), accountingAccountDepositProvider,
            null, null, customerOrder, bankJournal, null, null, debour, null, null);
    }
  }

  @Override
  public void checkInvoiceForLettrage(Invoice invoice) throws OsirisException {
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
  public void letterCounterPartRecords(AccountingRecord record, AccountingRecord counterPart) throws OsirisException {
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
      Payment payment, Deposit deposit, Debour debour, Refund refund, Appoint appoint) {
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
    accountingRecord.setAppoint(appoint);
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

  @Override
  public AccountingRecord generateCounterPart(AccountingRecord originalAccountingRecord, Integer operationId,
      AccountingJournal journal) {
    AccountingRecord newAccountingRecord = new AccountingRecord();
    newAccountingRecord.setAccountingAccount(originalAccountingRecord.getAccountingAccount());
    newAccountingRecord.setAccountingJournal(journal);
    newAccountingRecord.setCreditAmount(originalAccountingRecord.getDebitAmount());
    newAccountingRecord.setDebitAmount(originalAccountingRecord.getCreditAmount());
    newAccountingRecord.setDeposit(originalAccountingRecord.getDeposit());
    newAccountingRecord.setDebour(originalAccountingRecord.getDebour());
    newAccountingRecord.setAppoint(originalAccountingRecord.getAppoint());
    newAccountingRecord.setRefund(originalAccountingRecord.getRefund());
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

  @Override
  @Transactional(rollbackFor = Exception.class)
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
        if (values.size() > 0)
          sendBillingClosureReceiptFile(
              generatePdfDelegate.getBillingClosureReceiptFile(tier, values),
              tier);

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
            if (values.size() > 0)
              sendBillingClosureReceiptFile(
                  generatePdfDelegate.getBillingClosureReceiptFile(responsable, values),
                  responsable);

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
            if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0)
              for (Deposit deposit : customerOrder.getDeposits())
                allInputs.add(deposit);
          }
        }

        if (invoices != null && invoices.size() > 0) {
          for (Invoice invoice : invoices) {
            allInputs.add(invoice);
            if (invoice.getDeposits() != null && invoice.getDeposits().size() > 0)
              for (Deposit deposit : invoice.getDeposits())
                allInputs.add(deposit);
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
            if (input instanceof Deposit) {
              CustomerOrder customerOrder = null;
              Deposit deposit = (Deposit) input;
              if (deposit.getCustomerOrder() != null)
                customerOrder = deposit.getCustomerOrder();
              else if (deposit.getInvoice() != null)
                customerOrder = deposit.getInvoice().getCustomerOrder();
              values.add(getBillingClosureReceiptValueForDeposit(deposit, customerOrder, true));
            }
            if (input instanceof Payment)
              values.add(getBillingClosureReceiptValueForPayment((Payment) input, true));
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
            if (customerOrder.getDeposits() != null && customerOrder.getDeposits().size() > 0) {
              valueCustomerOrder.setDisplayBottomBorder(false);
              for (Deposit deposit : customerOrder.getDeposits())
                values.add(getBillingClosureReceiptValueForDeposit(deposit, customerOrder,
                    customerOrder.getDeposits().indexOf(deposit) == customerOrder.getDeposits().size() - 1));
            }
          }
          if (input instanceof Invoice) {
            Invoice invoice = (Invoice) input;
            BillingClosureReceiptValue valueInvoice = getBillingClosureReceiptValueForInvoice(invoice);
            values.add(valueInvoice);
            if (invoice.getDeposits() != null && invoice.getDeposits().size() > 0) {
              valueInvoice.setDisplayBottomBorder(false);
              for (Deposit deposit : invoice.getDeposits())
                values.add(getBillingClosureReceiptValueForDeposit(deposit, invoice.getCustomerOrder(),
                    invoice.getCustomerOrder().getDeposits()
                        .indexOf(deposit) == invoice.getCustomerOrder().getDeposits().size() - 1));
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

  private BillingClosureReceiptValue getBillingClosureReceiptValueForDeposit(Deposit deposit,
      CustomerOrder customerOrder, boolean displayBottomBorder) {
    BillingClosureReceiptValue value = new BillingClosureReceiptValue();
    value.setDisplayBottomBorder(displayBottomBorder);
    value.setDebitAmount(null);
    value.setCreditAmount(deposit.getDepositAmount());
    value.setEventDateTime(deposit.getDepositDate());
    value.setEventDateString(deposit.getDepositDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    String description = deposit.getLabel().replaceAll("&", "<![CDATA[&]]>");
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
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
    }
  }
}
