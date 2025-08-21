package com.jss.osiris.modules.osiris.tiers.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.PrintDelegate;
import com.jss.osiris.libs.TiersValidationHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.jackson.JacksonViews.OsirisDetailedView;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRepairHelper;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.PhoneSearch;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CountryService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ProviderService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureType;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.Competitor;
import com.jss.osiris.modules.osiris.tiers.model.IResponsableSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.ITiersSearchResult;
import com.jss.osiris.modules.osiris.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.osiris.tiers.model.RefundType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Rff;
import com.jss.osiris.modules.osiris.tiers.model.RffFrequency;
import com.jss.osiris.modules.osiris.tiers.model.RffSearch;
import com.jss.osiris.modules.osiris.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.model.TiersCategory;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowup;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowupType;
import com.jss.osiris.modules.osiris.tiers.model.TiersSearch;
import com.jss.osiris.modules.osiris.tiers.model.TiersType;
import com.jss.osiris.modules.osiris.tiers.service.BillingClosureRecipientTypeService;
import com.jss.osiris.modules.osiris.tiers.service.BillingClosureTypeService;
import com.jss.osiris.modules.osiris.tiers.service.BillingLabelTypeService;
import com.jss.osiris.modules.osiris.tiers.service.CompetitorService;
import com.jss.osiris.modules.osiris.tiers.service.PaymentDeadlineTypeService;
import com.jss.osiris.modules.osiris.tiers.service.RefundTypeService;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.RffFrequencyService;
import com.jss.osiris.modules.osiris.tiers.service.RffService;
import com.jss.osiris.modules.osiris.tiers.service.SubscriptionPeriodTypeService;
import com.jss.osiris.modules.osiris.tiers.service.TiersCategoryService;
import com.jss.osiris.modules.osiris.tiers.service.TiersFollowupService;
import com.jss.osiris.modules.osiris.tiers.service.TiersFollowupTypeService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;
import com.jss.osiris.modules.osiris.tiers.service.TiersTypeService;

@RestController
public class TiersController {

  private static final String inputEntryPoint = "/tiers";

  @Autowired
  ValidationHelper validationHelper;

  @Autowired
  TiersValidationHelper tiersValidationHelper;

  @Autowired
  TiersTypeService tiersTypeService;

  @Autowired
  TiersService tiersService;

  @Autowired
  ResponsableService responsableService;

  @Autowired
  TiersCategoryService tiersCategoryService;

  @Autowired
  EmployeeService employeeService;

  @Autowired
  CountryService countryService;

  @Autowired
  ConstantService constantService;

  @Autowired
  MailService mailService;

  @Autowired
  PhoneService phoneService;

  @Autowired
  DocumentTypeService tiersDocumentTypeService;

  @Autowired
  BillingLabelTypeService billingLabelTypeService;

  @Autowired
  PaymentDeadlineTypeService paymentDeadlineTypeService;

  @Autowired
  RefundTypeService refundTypeService;

  @Autowired
  BillingClosureTypeService billingClosureTypeService;

  @Autowired
  BillingClosureRecipientTypeService billingClosureRecipientTypeService;

  @Autowired
  TiersFollowupTypeService tiersFollowupTypeService;

  @Autowired
  TiersFollowupService tiersFollowupService;

  @Autowired
  SubscriptionPeriodTypeService subscriptionPeriodTypeService;

  @Autowired
  CompetitorService competitorService;

  @Autowired
  DocumentTypeService documentTypeService;

  @Autowired
  InvoiceService invoiceService;

  @Autowired
  AffaireService affaireService;

  @Autowired
  RffService rffService;

  @Autowired
  RffFrequencyService rffFrequencyService;

  @Autowired
  PrintDelegate printDelegate;

  @Autowired
  ProviderService providerService;

  @Autowired
  AccountingRepairHelper accountingRepairHelper;

  @GetMapping(inputEntryPoint + "/rff-frequencies")
  public ResponseEntity<List<RffFrequency>> getRffFrequencies() {
    return new ResponseEntity<List<RffFrequency>>(rffFrequencyService.getRffFrequencies(), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/rff-frequency")
  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  public ResponseEntity<RffFrequency> addOrUpdateRffFrequency(
      @RequestBody RffFrequency rffFrequencies) throws OsirisValidationException, OsirisException {
    if (rffFrequencies.getId() != null)
      validationHelper.validateReferential(rffFrequencies, true, "rffFrequencies");
    validationHelper.validateString(rffFrequencies.getCode(), true, "code");
    validationHelper.validateString(rffFrequencies.getLabel(), true, "label");

    return new ResponseEntity<RffFrequency>(rffFrequencyService.addOrUpdateRffFrequency(rffFrequencies), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/rffs")
  public ResponseEntity<List<Rff>> getRffs(@RequestBody RffSearch rffSearch)
      throws OsirisValidationException, OsirisException {
    if (rffSearch == null)
      throw new OsirisValidationException("rffSearch");

    if (rffSearch.getStartDate() == null)
      throw new OsirisValidationException("startDate");

    if (rffSearch.getEndDate() == null)
      throw new OsirisValidationException("endDate");
    return new ResponseEntity<List<Rff>>(rffService.getRffs(rffSearch), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/rff")
  public ResponseEntity<Rff> addOrUpdateRff(
      @RequestBody Rff rffs) throws OsirisValidationException, OsirisException {
    if (rffs.getId() != null)
      validationHelper.validateReferential(rffs, true, "rffs");

    return new ResponseEntity<Rff>(rffService.addOrUpdateRff(rffs), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/rff/cancel")
  public ResponseEntity<Rff> cancelRff(@RequestParam Integer idRff) throws OsirisValidationException, OsirisException {
    if (idRff == null)
      throw new OsirisValidationException("idRff");

    Rff rff = rffService.getRff(idRff);
    if (rff == null)
      throw new OsirisValidationException("Rff");

    if (rff.getIsSent() == true || rff.getInvoices() != null && rff.getInvoices().size() > 0)
      throw new OsirisValidationException("Rff");

    return new ResponseEntity<Rff>(rffService.cancelRff(rff), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/rff/send")
  public ResponseEntity<Rff> sendRff(@RequestParam Integer idRff, @RequestParam BigDecimal amount,
      @RequestParam boolean sendToMe)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {
    if (idRff == null)
      throw new OsirisValidationException("idRff");

    Rff rff = rffService.getRff(idRff);
    if (rff == null)
      throw new OsirisValidationException("Rff");

    if (rff.getIsCancelled() == true || rff.getIsSent() || rff.getInvoices() != null && rff.getInvoices().size() > 0)
      throw new OsirisValidationException("Rff");

    return new ResponseEntity<Rff>(rffService.sendRff(rff, amount, sendToMe), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/competitors")
  public ResponseEntity<List<Competitor>> getCompetitors() {
    return new ResponseEntity<List<Competitor>>(competitorService.getCompetitors(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/competitor")
  public ResponseEntity<Competitor> addOrUpdateCompetitor(
      @RequestBody Competitor competitors) throws OsirisValidationException, OsirisException {
    if (competitors.getId() != null)
      validationHelper.validateReferential(competitors, true, "competitors");
    validationHelper.validateString(competitors.getCode(), true, "code");
    validationHelper.validateString(competitors.getLabel(), true, "label");

    return new ResponseEntity<Competitor>(competitorService.addOrUpdateCompetitor(competitors), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/subscription-period-types")
  public ResponseEntity<List<SubscriptionPeriodType>> getSubscriptionPeriodTypes() {
    return new ResponseEntity<List<SubscriptionPeriodType>>(subscriptionPeriodTypeService.getSubscriptionPeriodTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/subscription-period-type")
  public ResponseEntity<SubscriptionPeriodType> addOrUpdateSubscriptionPeriodType(
      @RequestBody SubscriptionPeriodType subscriptionPeriodTypes) throws OsirisValidationException, OsirisException {
    if (subscriptionPeriodTypes.getId() != null)
      validationHelper.validateReferential(subscriptionPeriodTypes, true, "subscriptionPeriodTypes");
    validationHelper.validateString(subscriptionPeriodTypes.getCode(), true, 20, "code");
    validationHelper.validateString(subscriptionPeriodTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<SubscriptionPeriodType>(
        subscriptionPeriodTypeService.addOrUpdateSubscriptionPeriodType(subscriptionPeriodTypes), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/phone/search")
  public ResponseEntity<List<PhoneSearch>> getByPhoneNumber(@RequestParam String phoneNumber) throws OsirisException {
    return new ResponseEntity<List<PhoneSearch>>(phoneService.getByPhoneNumber(phoneNumber), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-followup-types")
  public ResponseEntity<List<TiersFollowupType>> getTiersFollowupTypes() {
    return new ResponseEntity<List<TiersFollowupType>>(tiersFollowupTypeService.getTiersFollowupTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/tiers-followup-type")
  public ResponseEntity<TiersFollowupType> addOrUpdateTiersFollowupType(
      @RequestBody TiersFollowupType tiersFollowupTypes) throws OsirisValidationException, OsirisException {
    if (tiersFollowupTypes.getId() != null)
      validationHelper.validateReferential(tiersFollowupTypes, true, "tiersFollowupTypes");
    validationHelper.validateString(tiersFollowupTypes.getCode(), true, 20, "code");
    validationHelper.validateString(tiersFollowupTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<TiersFollowupType>(
        tiersFollowupTypeService.addOrUpdateTiersFollowupType(tiersFollowupTypes), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup/tiers")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowupForTiers(@RequestBody TiersFollowup tiersFollowup,
      @RequestParam Integer idTiers)
      throws OsirisValidationException, OsirisException {
    Tiers tiers = tiersService.getTiers(idTiers);
    if (tiers == null)
      throw new OsirisValidationException("Tiers");

    tiersFollowup.setTiers(tiers);
    return saveTiersFollomUp(tiersFollowup);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup/provider")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowupForProvider(@RequestBody TiersFollowup tiersFollowup,
      @RequestParam Integer idProvider)
      throws OsirisValidationException, OsirisException {
    Provider provider = providerService.getProvider(idProvider);
    if (provider == null)
      throw new OsirisValidationException("Provider");

    tiersFollowup.setProvider(provider);
    return saveTiersFollomUp(tiersFollowup);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup/responsable")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowupForResponsable(@RequestBody TiersFollowup tiersFollowup,
      @RequestParam Integer idResponsable)
      throws OsirisValidationException, OsirisException {
    Responsable responsable = responsableService.getResponsable(idResponsable);
    if (responsable == null)
      throw new OsirisValidationException("Responsable");

    tiersFollowup.setResponsable(responsable);
    return saveTiersFollomUp(tiersFollowup);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup/invoice")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowupForInvoice(@RequestBody TiersFollowup tiersFollowup,
      @RequestParam Integer idInvoice)
      throws OsirisValidationException, OsirisException {
    Invoice invoice = invoiceService.getInvoice(idInvoice);
    if (invoice == null)
      throw new OsirisValidationException("Invoice");

    tiersFollowup.setInvoice(invoice);
    return saveTiersFollomUp(tiersFollowup);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup/affaire")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowupForAffaire(@RequestBody TiersFollowup tiersFollowup,
      @RequestParam Integer idAffaire)
      throws OsirisValidationException, OsirisException {
    Affaire affaire = affaireService.getAffaire(idAffaire);
    if (affaire == null)
      throw new OsirisValidationException("affaire");

    tiersFollowup.setAffaire(affaire);
    return saveTiersFollomUp(tiersFollowup);
  }

  private ResponseEntity<List<TiersFollowup>> saveTiersFollomUp(TiersFollowup tiersFollowup)
      throws OsirisValidationException, OsirisException {
    validationHelper.validateReferential(tiersFollowup.getTiersFollowupType(), true, "TiersFollowupType");
    validationHelper.validateReferential(tiersFollowup.getSalesEmployee(), true, "SalesEmployee");
    validationHelper.validateDate(tiersFollowup.getFollowupDate(), true, "FollowupDate");
    validationHelper.validateReferential(tiersFollowup.getGift(), false, "Gift");

    if (tiersFollowup.getGift() != null && (tiersFollowup.getGiftNumber() == null || tiersFollowup.getGiftNumber() < 0))
      tiersFollowup.setGiftNumber(1);

    return new ResponseEntity<List<TiersFollowup>>(tiersFollowupService.addTiersFollowup(tiersFollowup), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-closure-recipient-types")
  public ResponseEntity<List<BillingClosureRecipientType>> getBillingClosureRecipientTypes() {
    return new ResponseEntity<List<BillingClosureRecipientType>>(
        billingClosureRecipientTypeService.getBillingClosureRecipientTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/billing-closure-recipient-type")
  public ResponseEntity<BillingClosureRecipientType> addOrUpdateBillingClosureRecipientType(
      @RequestBody BillingClosureRecipientType billingClosureRecipientType)
      throws OsirisValidationException, OsirisException {
    if (billingClosureRecipientType.getId() != null)
      validationHelper.validateReferential(billingClosureRecipientType, true, "billingClosureRecipientType");
    validationHelper.validateString(billingClosureRecipientType.getCode(), true, 20, "code");
    validationHelper.validateString(billingClosureRecipientType.getLabel(), true, 100, "label");

    return new ResponseEntity<BillingClosureRecipientType>(
        billingClosureRecipientTypeService.addOrUpdateBillingClosureRecipientType(billingClosureRecipientType),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-closure-types")
  public ResponseEntity<List<BillingClosureType>> getBillingClosureTypes() {
    return new ResponseEntity<List<BillingClosureType>>(billingClosureTypeService.getBillingClosureTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/billing-closure-type")
  public ResponseEntity<BillingClosureType> addOrUpdateBillingClosureType(
      @RequestBody BillingClosureType billingClosureTypes) throws OsirisValidationException, OsirisException {
    if (billingClosureTypes.getId() != null)
      validationHelper.validateReferential(billingClosureTypes, true, "billingClosureTypes");
    validationHelper.validateString(billingClosureTypes.getCode(), true, 20, "code");
    validationHelper.validateString(billingClosureTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<BillingClosureType>(
        billingClosureTypeService.addOrUpdateBillingClosureType(billingClosureTypes), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/refund-types")
  public ResponseEntity<List<RefundType>> getRefundTypes() {
    return new ResponseEntity<List<RefundType>>(refundTypeService.getRefundTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/refund-type")
  public ResponseEntity<RefundType> addOrUpdateRefundType(
      @RequestBody RefundType refundTypes) throws OsirisValidationException, OsirisException {
    if (refundTypes.getId() != null)
      validationHelper.validateReferential(refundTypes, true, "refundTypes");
    validationHelper.validateString(refundTypes.getCode(), true, 20, "code");
    validationHelper.validateString(refundTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<RefundType>(refundTypeService.addOrUpdateRefundType(refundTypes), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/payment-deadline-types")
  public ResponseEntity<List<PaymentDeadlineType>> getPaymentDeadlineTypes() {
    return new ResponseEntity<List<PaymentDeadlineType>>(paymentDeadlineTypeService.getPaymentDeadlineTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/payment-deadline-type")
  public ResponseEntity<PaymentDeadlineType> addOrUpdatePaymentDeadlineType(
      @RequestBody PaymentDeadlineType paymentDeadlineTypes) throws OsirisValidationException, OsirisException {
    if (paymentDeadlineTypes.getId() != null)
      validationHelper.validateReferential(paymentDeadlineTypes, true, "paymentDeadlineTypes");
    validationHelper.validateString(paymentDeadlineTypes.getCode(), true, 20, "code");
    validationHelper.validateString(paymentDeadlineTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<PaymentDeadlineType>(
        paymentDeadlineTypeService.addOrUpdatePaymentDeadlineType(paymentDeadlineTypes), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-label-types")
  public ResponseEntity<List<BillingLabelType>> getBillingLabels() {
    return new ResponseEntity<List<BillingLabelType>>(billingLabelTypeService.getBillingLabelTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/billing-label-type")
  public ResponseEntity<BillingLabelType> addOrUpdateBillingLabelType(
      @RequestBody BillingLabelType billingLabelTypes) throws OsirisValidationException, OsirisException {
    if (billingLabelTypes.getId() != null)
      validationHelper.validateReferential(billingLabelTypes, true, "billingLabelTypes");
    validationHelper.validateString(billingLabelTypes.getCode(), true, 20, "code");
    validationHelper.validateString(billingLabelTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<BillingLabelType>(billingLabelTypeService.addOrUpdateBillingLabelType(billingLabelTypes),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mails/search")
  public ResponseEntity<List<Mail>> findMails(@RequestParam String mail) {
    return new ResponseEntity<List<Mail>>(mailService.findMails(mail), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-types")
  public ResponseEntity<List<TiersType>> getClientTypes() {
    return new ResponseEntity<List<TiersType>>(tiersTypeService.getTiersTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/tiers-type")
  public ResponseEntity<TiersType> addOrUpdateTiersType(
      @RequestBody TiersType tiersTypes) throws OsirisValidationException, OsirisException {
    if (tiersTypes.getId() != null)
      validationHelper.validateReferential(tiersTypes, true, "tiersTypes");
    validationHelper.validateString(tiersTypes.getCode(), true, 20, "code");
    validationHelper.validateString(tiersTypes.getLabel(), true, 100, "label");

    return new ResponseEntity<TiersType>(tiersTypeService.addOrUpdateTiersType(tiersTypes), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-categories")
  public ResponseEntity<List<TiersCategory>> getCategories() {
    return new ResponseEntity<List<TiersCategory>>(tiersCategoryService.getTiersCategories(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/tiers-category")
  public ResponseEntity<TiersCategory> addOrUpdateTiersCategory(
      @RequestBody TiersCategory tiersCategories) throws OsirisValidationException, OsirisException {
    if (tiersCategories.getId() != null)
      validationHelper.validateReferential(tiersCategories, true, "tiersCategories");
    validationHelper.validateString(tiersCategories.getCode(), true, 20, "code");
    validationHelper.validateString(tiersCategories.getLabel(), true, 100, "label");

    return new ResponseEntity<TiersCategory>(tiersCategoryService.addOrUpdateTiersCategory(tiersCategories),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers/delete")
  public ResponseEntity<Boolean> deleteTiers(@RequestParam Integer idTiers)
      throws OsirisValidationException, OsirisClientMessageException, OsirisException, OsirisDuplicateException {
    if (idTiers == null)
      throw new OsirisValidationException("idTiers");

    Tiers tiers = tiersService.getTiers(idTiers);
    if (tiers == null)
      throw new OsirisValidationException("tiers");

    return new ResponseEntity<Boolean>(tiersService.deleteTiers(tiers), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers/account/repair")
  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  public ResponseEntity<Boolean> repairTierAccounts(@RequestParam Integer idTiers)
      throws OsirisValidationException, OsirisClientMessageException, OsirisException, OsirisDuplicateException {
    if (idTiers == null)
      throw new OsirisValidationException("idTiers");

    Tiers tiers = tiersService.getTiers(idTiers);
    if (tiers == null)
      throw new OsirisValidationException("tiers");

    accountingRepairHelper.repairTierAccounts(tiers);

    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/responsable/search")
  public ResponseEntity<List<IResponsableSearchResult>> getResponsableSearch(@RequestBody TiersSearch tiersSearch)
      throws OsirisValidationException, OsirisException {
    if (tiersSearch == null)
      throw new OsirisValidationException("tiersSearch");

    return new ResponseEntity<List<IResponsableSearchResult>>(responsableService.searchResponsables(tiersSearch),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/search")
  public ResponseEntity<List<ITiersSearchResult>> getTiersSearch(@RequestBody TiersSearch tiersSearch)
      throws OsirisValidationException, OsirisException {
    if (tiersSearch == null)
      throw new OsirisValidationException("tiersSearch");

    return new ResponseEntity<List<ITiersSearchResult>>(tiersService.searchTiers(tiersSearch),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsable")
  public ResponseEntity<Responsable> getResponsableById(@RequestParam Integer id) {
    return new ResponseEntity<Responsable>(responsableService.getResponsable(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers/responsable")
  public ResponseEntity<Tiers> getTiersByIdResponsable(@RequestParam Integer idResponsable) {
    return new ResponseEntity<Tiers>(tiersService.getTiersByIdResponsable(idResponsable), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> addOrUpdateTiers(@RequestBody Tiers tiers)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

    tiersValidationHelper.validateTiers(tiers);

    return new ResponseEntity<Tiers>(tiersService.addOrUpdateTiers(tiers), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/label/print")
  public ResponseEntity<Boolean> printMailingLabel(@RequestParam Integer idTiers)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {

    Tiers tiers = tiersService.getTiers(idTiers);
    if (tiers == null)
      throw new OsirisValidationException("tiers");

    printDelegate.printTiersLabel(tiers);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation/document/apply")
  public ResponseEntity<Document> applyParametersDocumentToQuotation(@RequestParam Integer idDocumentType,
      @RequestParam Integer idResponsable) throws OsirisValidationException {
    if (idResponsable == null)
      throw new OsirisValidationException("idResponsable");
    if (idDocumentType == null)
      throw new OsirisValidationException("idDocumentType");
    Responsable responsable = responsableService.getResponsable(idResponsable);
    DocumentType documentType = documentTypeService.getDocumentType(idDocumentType);
    return new ResponseEntity<Document>(
        responsableService.applyParametersDocumentToQuotation(documentType, responsable),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsable/search")
  @JsonView(JacksonViews.OsirisListView.class)
  public ResponseEntity<List<Responsable>> getResponsables(@RequestParam String searchedValue) {
    return new ResponseEntity<List<Responsable>>(responsableService.getResponsables(searchedValue), HttpStatus.OK);
  }

  /*
   * |============================================================================
   * |______________________METHODS FOR OSIRIS V2_________________________________
   * |============================================================================
   */

  @GetMapping(inputEntryPoint + "/tiers")
  @JsonView(OsirisDetailedView.class)
  public ResponseEntity<Tiers> getTiersById(@RequestParam Integer id) {
    return new ResponseEntity<Tiers>(tiersService.getTiersFromUser(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsables")
  @JsonView(JacksonViews.OsirisListView.class)
  public ResponseEntity<List<Responsable>> getResponsablesByTiers(@RequestParam Integer idTiers) {

    if (tiersService.getTiers(idTiers) == null) {
      return new ResponseEntity<List<Responsable>>(new ArrayList<>(), HttpStatus.OK);
    }

    return new ResponseEntity<List<Responsable>>(
        responsableService.getResponsablesByTiers(tiersService.getTiers(idTiers)), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsables/kpi")
  @JsonView(JacksonViews.OsirisListView.class)
  public ResponseEntity<KpiCrm> getKpiByTiers(@RequestParam Integer idTiers) {

    List<Responsable> responsables = responsableService.getResponsablesByTiers(tiersService.getTiers(idTiers));

    return new ResponseEntity<KpiCrm>(responsableService.computeKpiOpportunityClosingAverageTime(responsables),
        HttpStatus.OK);
  }
}