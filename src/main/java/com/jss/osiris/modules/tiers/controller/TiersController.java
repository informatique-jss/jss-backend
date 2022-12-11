package com.jss.osiris.modules.tiers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.CountryService;
import com.jss.osiris.modules.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.tiers.model.BillingClosureType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.Competitor;
import com.jss.osiris.modules.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.model.TiersCategory;
import com.jss.osiris.modules.tiers.model.TiersFollowup;
import com.jss.osiris.modules.tiers.model.TiersFollowupType;
import com.jss.osiris.modules.tiers.model.TiersType;
import com.jss.osiris.modules.tiers.service.BillingClosureRecipientTypeService;
import com.jss.osiris.modules.tiers.service.BillingClosureTypeService;
import com.jss.osiris.modules.tiers.service.BillingLabelTypeService;
import com.jss.osiris.modules.tiers.service.CompetitorService;
import com.jss.osiris.modules.tiers.service.PaymentDeadlineTypeService;
import com.jss.osiris.modules.tiers.service.RefundTypeService;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.SubscriptionPeriodTypeService;
import com.jss.osiris.modules.tiers.service.TiersCategoryService;
import com.jss.osiris.modules.tiers.service.TiersFollowupService;
import com.jss.osiris.modules.tiers.service.TiersFollowupTypeService;
import com.jss.osiris.modules.tiers.service.TiersService;
import com.jss.osiris.modules.tiers.service.TiersTypeService;

@RestController
public class TiersController {

  private static final String inputEntryPoint = "/tiers";

  @Autowired
  ValidationHelper validationHelper;

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

  @GetMapping(inputEntryPoint + "/competitors")
  public ResponseEntity<List<Competitor>> getCompetitors() {
    return new ResponseEntity<List<Competitor>>(competitorService.getCompetitors(), HttpStatus.OK);
  }

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

  @GetMapping(inputEntryPoint + "/tiers-followup-types")
  public ResponseEntity<List<TiersFollowupType>> getTiersFollowupTypes() {
    return new ResponseEntity<List<TiersFollowupType>>(tiersFollowupTypeService.getTiersFollowupTypes(), HttpStatus.OK);
  }

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

  @PostMapping(inputEntryPoint + "/tiers-followup")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowup(@RequestBody TiersFollowup tiersFollowup)
      throws OsirisValidationException, OsirisException {
    if (tiersFollowup.getTiers() == null && tiersFollowup.getResponsable() == null
        && tiersFollowup.getConfrere() == null)
      throw new OsirisValidationException("tiers or responsable");
    validationHelper.validateReferential(tiersFollowup.getTiers(), false, "Tiers");
    validationHelper.validateReferential(tiersFollowup.getResponsable(), false, "Responsable");
    validationHelper.validateReferential(tiersFollowup.getConfrere(), false, "Responsable");
    validationHelper.validateReferential(tiersFollowup.getTiersFollowupType(), true, "TiersFollowupType");
    validationHelper.validateReferential(tiersFollowup.getSalesEmployee(), true, "SalesEmployee");
    validationHelper.validateDate(tiersFollowup.getFollowupDate(), true, "FollowupDate");
    validationHelper.validateReferential(tiersFollowup.getGift(), false, "Gift");

    return new ResponseEntity<List<TiersFollowup>>(tiersFollowupService.addTiersFollowup(tiersFollowup), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-closure-recipient-types")
  public ResponseEntity<List<BillingClosureRecipientType>> getBillingClosureRecipientTypes() {
    return new ResponseEntity<List<BillingClosureRecipientType>>(
        billingClosureRecipientTypeService.getBillingClosureRecipientTypes(), HttpStatus.OK);
  }

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

  @GetMapping(inputEntryPoint + "/phones/search")
  public ResponseEntity<List<Phone>> findPhones(@RequestParam String phone) {
    return new ResponseEntity<List<Phone>>(phoneService.findPhones(phone), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mails/search")
  public ResponseEntity<List<Mail>> findMails(@RequestParam String mail) {
    return new ResponseEntity<List<Mail>>(mailService.findMails(mail), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-types")
  public ResponseEntity<List<TiersType>> getClientTypes() {
    return new ResponseEntity<List<TiersType>>(tiersTypeService.getTiersTypes(), HttpStatus.OK);
  }

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

  @GetMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> getTiersById(@RequestParam Integer id) {
    return new ResponseEntity<Tiers>(tiersService.getTiers(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/individual/search")
  public ResponseEntity<List<Tiers>> getIndividualTiersByKeyword(@RequestParam String searchedValue) {
    return new ResponseEntity<List<Tiers>>(tiersService.getIndividualTiersByKeyword(searchedValue), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsable/search")
  public ResponseEntity<List<Responsable>> getResponsableByKeyword(@RequestParam String searchedValue) {
    return new ResponseEntity<List<Responsable>>(responsableService.getResponsableByKeyword(searchedValue),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsable")
  public ResponseEntity<Tiers> getTiersByIdResponsable(@RequestParam Integer idResponsable) {
    return new ResponseEntity<Tiers>(tiersService.getTiersByIdResponsable(idResponsable), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> addOrUpdateTiers(@RequestBody Tiers tiers)
      throws OsirisValidationException, OsirisException {
    validationHelper.validateReferential(tiers.getTiersType(), true, "TiersType");

    if (tiers.getIsIndividual()) {
      validationHelper.validateReferential(tiers.getCivility(), true, "Civility");
      validationHelper.validateString(tiers.getFirstname(), true, 40, "Firstname");
      validationHelper.validateString(tiers.getLastname(), true, 40, "Lastname");
    } else {
      validationHelper.validateString(tiers.getDenomination(), true, 60, "Denomination");
      if (tiers.getIntercommunityVat() != null && tiers.getIntercommunityVat().length() > 20)
        throw new OsirisValidationException("IntercommunityVat");
    }

    validationHelper.validateReferential(tiers.getTiersCategory(), false, "TiersCategory");
    validationHelper.validateReferential(tiers.getSalesEmployee(), true, "SalesEmployee");
    validationHelper.validateReferential(tiers.getFormalisteEmployee(), false, "FormalisteEmployee");
    validationHelper.validateReferential(tiers.getInsertionEmployee(), false, "InsertionEmployee");
    validationHelper.validateReferential(tiers.getLanguage(), true, "Language");
    validationHelper.validateReferential(tiers.getDeliveryService(), true, "DeliveryService");

    validationHelper.validateString(tiers.getAddress(), true, 60, "Address");
    validationHelper.validateReferential(tiers.getCountry(), true, "Country");
    if (tiers.getCountry() != null
        && tiers.getCountry().getId().equals(constantService.getCountryFrance().getId()))
      validationHelper.validateString(tiers.getPostalCode(), true, 10, "PostalCode");
    validationHelper.validateString(tiers.getCedexComplement(), false, 20, "CedexComplement");
    validationHelper.validateReferential(tiers.getCity(), true, "City");

    validationHelper.validateString(tiers.getIntercom(), false, 12, "Intercom");
    if (tiers.getSpecialOffers() != null) {
      for (SpecialOffer specialOffer : tiers.getSpecialOffers()) {
        validationHelper.validateReferential(specialOffer, false, "specialOffer");
      }
    }

    if (tiers.getMails() != null && tiers.getMails().size() > 0) {
      if (!validationHelper.validateMailList(tiers.getMails()))
        throw new OsirisValidationException("Mails");
    }

    if (tiers.getPhones() != null && tiers.getPhones().size() > 0) {
      for (Phone phone : tiers.getPhones()) {
        if (!validationHelper.validateFrenchPhone(phone.getPhoneNumber())
            || !validationHelper.validateInternationalPhone(phone.getPhoneNumber()))
          throw new OsirisValidationException("Phones");
      }
    }

    if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
      for (Document document : tiers.getDocuments()) {

        validationHelper.validateReferential(document.getDocumentType(), true, "DocumentType");

        if (document.getMailsAffaire() != null && !validationHelper.validateMailList(document.getMailsAffaire()))
          throw new OsirisValidationException("MailsAffaire");
        if (document.getMailsClient() != null && document.getMailsClient() != null
            && document.getMailsClient().size() > 0)
          if (!validationHelper.validateMailList(document.getMailsClient()))
            throw new OsirisValidationException("MailsClient");

        validationHelper.validateString(document.getAffaireAddress(), false, 60, "AffaireAddress");
        validationHelper.validateString(document.getClientAddress(), false, 60, "ClientAddress");
        validationHelper.validateString(document.getAffaireRecipient(), false, 40, "AffaireRecipient");
        validationHelper.validateString(document.getClientRecipient(), false, 40, "ClientRecipient");
        validationHelper.validateString(document.getCommandNumber(), false, 40, "CommandNumber");
        validationHelper.validateReferential(document.getPaymentDeadlineType(), false, "PaymentDeadlineType");
        validationHelper.validateReferential(document.getRefundType(), false, "RefundType");
        validationHelper.validateString(document.getRefundIBAN(), false, 40, "RefundIBAN");
        validationHelper.validateReferential(document.getBillingClosureType(), false, "BillingClosureType");
        validationHelper.validateReferential(document.getBillingClosureRecipientType(), false,
            "BillingClosureRecipientType");
        validationHelper.validateReferential(document.getBillingLabelCity(), false, "BillingLabelCity");
        validationHelper.validateReferential(document.getBillingLabelCountry(), false, "BillingLabelCountry");
        validationHelper.validateString(document.getBillingAddress(), false, 60, "BillingAddress");
        validationHelper.validateString(document.getBillingLabel(), false, 60, "BillingLabel");
        validationHelper.validateString(document.getBillingPostalCode(), false, 10, "BillingPostalCode");
        validationHelper.validateString(document.getCedexComplement(), false, 20, "CedexComplement");

        if (document.getIsMailingPaper() == null)
          document.setIsMailingPaper(false);
        if (document.getIsMailingPdf() == null)
          document.setIsMailingPdf(false);
        if (document.getIsRecipientAffaire() == null)
          document.setIsRecipientAffaire(false);
        if (document.getIsRecipientClient() == null)
          document.setIsRecipientClient(false);

      }
    }

    validationHelper.validateReferential(tiers.getPaymentType(),
        !tiers.getTiersType().getId().equals(constantService.getTiersTypeProspect().getId()), "PaymentType");
    validationHelper.validateString(tiers.getPaymentIBAN(), false, 40, "PaymentIBAN");

    if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
      for (Responsable responsable : tiers.getResponsables()) {

        validationHelper.validateReferential(responsable.getCivility(), true, "Civility");
        validationHelper.validateString(responsable.getFirstname(), true, 40, "Firstname");
        validationHelper.validateString(responsable.getLastname(), true, 40, "Lastname");
        validationHelper.validateReferential(responsable.getTiersType(), true, "TiersType");
        validationHelper.validateReferential(responsable.getTiersCategory(), false, "TiersCategory");
        validationHelper.validateReferential(responsable.getSalesEmployee(), true, "SalesEmployee");
        validationHelper.validateReferential(responsable.getFormalisteEmployee(), false, "FormalisteEmployee");
        validationHelper.validateReferential(responsable.getInsertionEmployee(), false, "InsertionEmployee");
        validationHelper.validateReferential(responsable.getLanguage(), true, "Language");
        validationHelper.validateString(responsable.getAddress(), false, 60, "Address");
        validationHelper.validateReferential(responsable.getCountry(), false, "Country");
        validationHelper.validateReferential(responsable.getCity(), false, "City");
        validationHelper.validateString(responsable.getPostalCode(), false, 10, "PostalCode");
        validationHelper.validateString(responsable.getCedexComplement(), false, 20, "CedexComplement");
        validationHelper.validateString(responsable.getFunction(), false, 20, "Function");
        validationHelper.validateString(responsable.getBuilding(), false, 20, "Building");
        validationHelper.validateString(responsable.getFloor(), false, 20, "Floor");
        validationHelper.validateReferential(responsable.getSubscriptionPeriodType(), false, "SubscriptionPeriodType");

        if (responsable.getDocuments() != null && responsable.getDocuments().size() > 0) {
          for (Document document : responsable.getDocuments()) {
            validationHelper.validateReferential(document.getDocumentType(), true, "DocumentType");

            if (document.getMailsAffaire() != null && !validationHelper.validateMailList(document.getMailsAffaire()))
              throw new OsirisValidationException("MailsAffaire");
            if (document.getMailsClient() != null && document.getMailsClient() != null
                && document.getMailsClient().size() > 0)
              if (!validationHelper.validateMailList(document.getMailsClient()))
                throw new OsirisValidationException("MailsClient");
            if (document.getMailsCCResponsableAffaire() != null
                && document.getMailsCCResponsableAffaire().size() > 0) {
              for (Responsable res : document.getMailsCCResponsableAffaire()) {
                if (res.getId() == null || responsableService.getResponsable(res.getId()) == null)
                  throw new OsirisValidationException("Responsable");
              }
            }
            if (document.getMailsCCResponsableClient() != null && document.getMailsCCResponsableClient().size() > 0) {
              for (Responsable res : document.getMailsCCResponsableClient()) {
                if (res.getId() == null || responsableService.getResponsable(res.getId()) == null)
                  throw new OsirisValidationException("Responsable");
              }
            }

            validationHelper.validateString(document.getAffaireAddress(), false, 60, "AffaireAddress");
            validationHelper.validateString(document.getClientAddress(), false, 60, "ClientAddress");
            validationHelper.validateString(document.getAffaireRecipient(), false, 40, "AffaireRecipient");
            validationHelper.validateString(document.getClientRecipient(), false, 40, "ClientRecipient");
            validationHelper.validateString(document.getCommandNumber(), false, 40, "CommandNumber");
            validationHelper.validateReferential(document.getPaymentDeadlineType(), false, "PaymentDeadlineType");
            validationHelper.validateReferential(document.getRefundType(), false, "RefundType");
            validationHelper.validateString(document.getRefundIBAN(), false, 40, "RefundIBAN");
            validationHelper.validateReferential(document.getBillingClosureType(), false, "BillingClosureType");
            validationHelper.validateReferential(document.getBillingClosureRecipientType(), false,
                "BillingClosureRecipientType");
            validationHelper.validateReferential(document.getBillingLabelCity(), false, "BillingLabelCity");
            validationHelper.validateReferential(document.getBillingLabelCountry(), false, "BillingLabelCountry");
            validationHelper.validateString(document.getBillingAddress(), false, 60, "BillingAddress");
            validationHelper.validateString(document.getBillingLabel(), false, 60, "BillingLabel");
            validationHelper.validateString(document.getBillingPostalCode(), false, 10, "BillingPostalCode");
            validationHelper.validateString(document.getCedexComplement(), false, 20, "CedexComplement");

          }
        }
      }
    }

    return new ResponseEntity<Tiers>(tiersService.addOrUpdateTiers(tiers), HttpStatus.OK);
  }

}