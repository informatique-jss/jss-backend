package com.jss.osiris.modules.tiers.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.service.CountryService;
import com.jss.osiris.modules.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.tiers.model.BillingClosureType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
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

  private static final Logger logger = LoggerFactory.getLogger(TiersController.class);

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

  @GetMapping(inputEntryPoint + "/subscription-period-types")
  public ResponseEntity<List<SubscriptionPeriodType>> getSubscriptionPeriodTypes() {
    List<SubscriptionPeriodType> subscriptionPeriodTypes = null;
    try {
      subscriptionPeriodTypes = subscriptionPeriodTypeService.getSubscriptionPeriodTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching subscriptionPeriodType", e);
      return new ResponseEntity<List<SubscriptionPeriodType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching subscriptionPeriodType", e);
      return new ResponseEntity<List<SubscriptionPeriodType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<SubscriptionPeriodType>>(subscriptionPeriodTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/subscription-period-type")
  public ResponseEntity<SubscriptionPeriodType> addOrUpdateSubscriptionPeriodType(
      @RequestBody SubscriptionPeriodType subscriptionPeriodTypes) {
    SubscriptionPeriodType outSubscriptionPeriodType;
    try {
      if (subscriptionPeriodTypes.getId() != null)
        validationHelper.validateReferential(subscriptionPeriodTypes, true);
      validationHelper.validateString(subscriptionPeriodTypes.getCode(), true, 20);
      validationHelper.validateString(subscriptionPeriodTypes.getLabel(), true, 100);

      outSubscriptionPeriodType = subscriptionPeriodTypeService
          .addOrUpdateSubscriptionPeriodType(subscriptionPeriodTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching subscriptionPeriodType", e);
      return new ResponseEntity<SubscriptionPeriodType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching subscriptionPeriodType", e);
      return new ResponseEntity<SubscriptionPeriodType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<SubscriptionPeriodType>(outSubscriptionPeriodType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-followup-types")
  public ResponseEntity<List<TiersFollowupType>> getTiersFollowupTypes() {
    List<TiersFollowupType> tiersFollowupTypes = null;
    try {
      tiersFollowupTypes = tiersFollowupTypeService.getTiersFollowupTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching tiersFollowupType", e);
      return new ResponseEntity<List<TiersFollowupType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching tiersFollowupType", e);
      return new ResponseEntity<List<TiersFollowupType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TiersFollowupType>>(tiersFollowupTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup-type")
  public ResponseEntity<TiersFollowupType> addOrUpdateTiersFollowupType(
      @RequestBody TiersFollowupType tiersFollowupTypes) {
    TiersFollowupType outTiersFollowupType;
    try {
      if (tiersFollowupTypes.getId() != null)
        validationHelper.validateReferential(tiersFollowupTypes, true);
      validationHelper.validateString(tiersFollowupTypes.getCode(), true, 20);
      validationHelper.validateString(tiersFollowupTypes.getLabel(), true, 100);

      outTiersFollowupType = tiersFollowupTypeService
          .addOrUpdateTiersFollowupType(tiersFollowupTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching tiersFollowupType", e);
      return new ResponseEntity<TiersFollowupType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching tiersFollowupType", e);
      return new ResponseEntity<TiersFollowupType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<TiersFollowupType>(outTiersFollowupType, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers-followup")
  public ResponseEntity<List<TiersFollowup>> addTiersFollowup(@RequestBody TiersFollowup tiersFollowup) {
    if (tiersFollowup.getTiers() == null && tiersFollowup.getResponsable() == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiersFollowup.getTiers() != null) {
      Tiers tiers = tiersService.getTiers(tiersFollowup.getTiers().getId());
      if (tiers == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      tiersFollowup.setTiers(tiers);
    }

    if (tiersFollowup.getResponsable() != null) {
      Responsable responsable = responsableService.getResponsable(tiersFollowup.getResponsable().getId());
      if (responsable == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      tiersFollowup.setResponsable(responsable);
    }

    if (tiersFollowup.getTiersFollowupType() == null || tiersFollowup.getTiersFollowupType().getId() == null
        || tiersFollowupTypeService.getTiersFollowupType(tiersFollowup.getTiersFollowupType().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiersFollowup.getSalesEmployee() == null || tiersFollowup.getSalesEmployee().getId() == null ||
        employeeService.getEmployee(tiersFollowup.getSalesEmployee().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiersFollowup.getFollowupDate() == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    List<TiersFollowup> tiersFollowups = null;
    try {
      validationHelper.validateReferential(tiersFollowup.getGift(), false);
      tiersFollowups = tiersFollowupService.addTiersFollowup(tiersFollowup);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching TiersFollowup", e);
      return new ResponseEntity<List<TiersFollowup>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching TiersFollowup", e);
      return new ResponseEntity<List<TiersFollowup>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TiersFollowup>>(tiersFollowups, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-closure-recipient-types")
  public ResponseEntity<List<BillingClosureRecipientType>> getBillingClosureRecipientTypes() {
    List<BillingClosureRecipientType> billingClosureRecipientTypes = null;
    try {
      billingClosureRecipientTypes = billingClosureRecipientTypeService.getBillingClosureRecipientTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billingClosureRecipientType", e);
      return new ResponseEntity<List<BillingClosureRecipientType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billingClosureRecipientType", e);
      return new ResponseEntity<List<BillingClosureRecipientType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BillingClosureRecipientType>>(billingClosureRecipientTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/billing-closure-recipient-type")
  public ResponseEntity<BillingClosureRecipientType> addOrUpdateBillingClosureRecipientType(
      @RequestBody BillingClosureRecipientType billingClosureRecipientType) {
    BillingClosureRecipientType outBillingClosureRecipientType;
    try {
      if (billingClosureRecipientType.getId() != null)
        validationHelper.validateReferential(billingClosureRecipientType, true);
      validationHelper.validateString(billingClosureRecipientType.getCode(), true, 20);
      validationHelper.validateString(billingClosureRecipientType.getLabel(), true, 100);

      outBillingClosureRecipientType = billingClosureRecipientTypeService
          .addOrUpdateTransfertFundsType(billingClosureRecipientType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<BillingClosureRecipientType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<BillingClosureRecipientType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<BillingClosureRecipientType>(outBillingClosureRecipientType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-closure-types")
  public ResponseEntity<List<BillingClosureType>> getBillingClosureTypes() {
    List<BillingClosureType> billingClosureTypes = null;
    try {
      billingClosureTypes = billingClosureTypeService.getBillingClosureTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billingClosureType", e);
      return new ResponseEntity<List<BillingClosureType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billingClosureType", e);
      return new ResponseEntity<List<BillingClosureType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BillingClosureType>>(billingClosureTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/billing-closure-type")
  public ResponseEntity<BillingClosureType> addOrUpdateBillingClosureType(
      @RequestBody BillingClosureType billingClosureTypes) {
    BillingClosureType outBillingClosureType;
    try {
      if (billingClosureTypes.getId() != null)
        validationHelper.validateReferential(billingClosureTypes, true);
      validationHelper.validateString(billingClosureTypes.getCode(), true, 20);
      validationHelper.validateString(billingClosureTypes.getLabel(), true, 100);

      outBillingClosureType = billingClosureTypeService
          .addOrUpdateBillingClosureType(billingClosureTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billing closure type", e);
      return new ResponseEntity<BillingClosureType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billing closure type", e);
      return new ResponseEntity<BillingClosureType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<BillingClosureType>(outBillingClosureType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/refund-types")
  public ResponseEntity<List<RefundType>> getRefundTypes() {
    List<RefundType> refundTypes = null;
    try {
      refundTypes = refundTypeService.getRefundTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching refundType", e);
      return new ResponseEntity<List<RefundType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching refundType", e);
      return new ResponseEntity<List<RefundType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<RefundType>>(refundTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/refund-type")
  public ResponseEntity<RefundType> addOrUpdateRefundType(
      @RequestBody RefundType refundTypes) {
    RefundType outRefundType;
    try {
      if (refundTypes.getId() != null)
        validationHelper.validateReferential(refundTypes, true);
      validationHelper.validateString(refundTypes.getCode(), true, 20);
      validationHelper.validateString(refundTypes.getLabel(), true, 100);

      outRefundType = refundTypeService
          .addOrUpdateRefundType(refundTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching refundType", e);
      return new ResponseEntity<RefundType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching refundType", e);
      return new ResponseEntity<RefundType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<RefundType>(outRefundType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/payment-deadline-types")
  public ResponseEntity<List<PaymentDeadlineType>> getPaymentDeadlineTypes() {
    List<PaymentDeadlineType> paymentDeadlineTypes = null;
    try {
      paymentDeadlineTypes = paymentDeadlineTypeService.getPaymentDeadlineTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching paymentDeadlineType", e);
      return new ResponseEntity<List<PaymentDeadlineType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching paymentDeadlineType", e);
      return new ResponseEntity<List<PaymentDeadlineType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<PaymentDeadlineType>>(paymentDeadlineTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/payment-deadline-type")
  public ResponseEntity<PaymentDeadlineType> addOrUpdatePaymentDeadlineType(
      @RequestBody PaymentDeadlineType paymentDeadlineTypes) {
    PaymentDeadlineType outPaymentDeadlineType;
    try {
      if (paymentDeadlineTypes.getId() != null)
        validationHelper.validateReferential(paymentDeadlineTypes, true);
      validationHelper.validateString(paymentDeadlineTypes.getCode(), true, 20);
      validationHelper.validateString(paymentDeadlineTypes.getLabel(), true, 100);

      outPaymentDeadlineType = paymentDeadlineTypeService
          .addOrUpdatePaymentDeadlineType(paymentDeadlineTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching paymentDeadlineType", e);
      return new ResponseEntity<PaymentDeadlineType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching paymentDeadlineType", e);
      return new ResponseEntity<PaymentDeadlineType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<PaymentDeadlineType>(outPaymentDeadlineType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-label-types")
  public ResponseEntity<List<BillingLabelType>> getBillingLabels() {
    List<BillingLabelType> billingLabels = null;
    try {
      billingLabels = billingLabelTypeService.getBillingLabelTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billingLabel", e);
      return new ResponseEntity<List<BillingLabelType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billingLabel", e);
      return new ResponseEntity<List<BillingLabelType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BillingLabelType>>(billingLabels, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/billing-label-type")
  public ResponseEntity<BillingLabelType> addOrUpdateBillingLabelType(
      @RequestBody BillingLabelType billingLabelTypes) {
    BillingLabelType outBillingLabelType;
    try {
      if (billingLabelTypes.getId() != null)
        validationHelper.validateReferential(billingLabelTypes, true);
      validationHelper.validateString(billingLabelTypes.getCode(), true, 20);
      validationHelper.validateString(billingLabelTypes.getLabel(), true, 100);

      outBillingLabelType = billingLabelTypeService
          .addOrUpdateBillingLabelType(billingLabelTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billingLabelType", e);
      return new ResponseEntity<BillingLabelType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billingLabelType", e);
      return new ResponseEntity<BillingLabelType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<BillingLabelType>(outBillingLabelType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/phones/search")
  public ResponseEntity<List<Phone>> findPhones(@RequestParam String phone) {
    List<Phone> phones = null;
    try {
      phones = phoneService.findPhones(phone);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching phone", e);
      return new ResponseEntity<List<Phone>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching phone", e);
      return new ResponseEntity<List<Phone>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Phone>>(phones, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mails/search")
  public ResponseEntity<List<Mail>> findMails(@RequestParam String mail) {
    List<Mail> mails = null;
    try {
      mails = mailService.findMails(mail);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching mail", e);
      return new ResponseEntity<List<Mail>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching mail", e);
      return new ResponseEntity<List<Mail>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Mail>>(mails, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-types")
  public ResponseEntity<List<TiersType>> getClientTypes() {
    List<TiersType> clientTypes = null;
    try {
      clientTypes = tiersTypeService.getTiersTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<TiersType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<TiersType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TiersType>>(clientTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers-type")
  public ResponseEntity<TiersType> addOrUpdateTiersType(
      @RequestBody TiersType tiersTypes) {
    TiersType outTiersType;
    try {
      if (tiersTypes.getId() != null)
        validationHelper.validateReferential(tiersTypes, true);
      validationHelper.validateString(tiersTypes.getCode(), true, 20);
      validationHelper.validateString(tiersTypes.getLabel(), true, 100);

      outTiersType = tiersTypeService
          .addOrUpdateTiersType(tiersTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching tiersType", e);
      return new ResponseEntity<TiersType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching tiersType", e);
      return new ResponseEntity<TiersType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<TiersType>(outTiersType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers-categories")
  public ResponseEntity<List<TiersCategory>> getCategories() {
    List<TiersCategory> civilities = null;
    try {
      civilities = tiersCategoryService.getTiersCategories();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<TiersCategory>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<TiersCategory>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TiersCategory>>(civilities, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers-category")
  public ResponseEntity<TiersCategory> addOrUpdateTiersCategory(
      @RequestBody TiersCategory tiersCategories) {
    TiersCategory outTiersCategory;
    try {
      if (tiersCategories.getId() != null)
        validationHelper.validateReferential(tiersCategories, true);
      validationHelper.validateString(tiersCategories.getCode(), true, 20);
      validationHelper.validateString(tiersCategories.getLabel(), true, 100);

      outTiersCategory = tiersCategoryService
          .addOrUpdateTiersCategory(tiersCategories);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching tiersCategory", e);
      return new ResponseEntity<TiersCategory>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching tiersCategory", e);
      return new ResponseEntity<TiersCategory>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<TiersCategory>(outTiersCategory, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> getTiersById(@RequestParam Integer id) {
    Tiers tiers = null;
    try {
      tiers = tiersService.getTiers(id);
      if (tiers == null)
        tiers = new Tiers();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Tiers>(tiers, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/individual/search")
  public ResponseEntity<List<Tiers>> getIndividualTiersByKeyword(@RequestParam String searchedValue) {
    List<Tiers> tiers = null;
    try {
      tiers = tiersService.getIndividualTiersByKeyword(searchedValue);
      if (tiers == null)
        tiers = new ArrayList<Tiers>();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<Tiers>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<Tiers>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Tiers>>(tiers, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsable/search")
  public ResponseEntity<List<Responsable>> getResponsableByKeyword(@RequestParam String searchedValue) {
    List<Responsable> responsables = null;
    try {
      responsables = responsableService.getResponsableByKeyword(searchedValue);
      if (responsables == null)
        responsables = new ArrayList<Responsable>();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<Responsable>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<Responsable>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Responsable>>(responsables, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/responsable")
  public ResponseEntity<Tiers> getTiersByIdResponsable(@RequestParam Integer idResponsable) {
    Tiers tiers = null;
    try {
      tiers = tiersService.getTiersByIdResponsable(idResponsable);
      if (tiers == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Tiers>(tiers, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> addOrUpdateTiers(@RequestBody Tiers tiers) {
    try {

      validationHelper.validateReferential(tiers.getTiersType(), true);

      if (tiers.getIsIndividual()) {
        validationHelper.validateReferential(tiers.getCivility(), true);
        validationHelper.validateString(tiers.getFirstname(), true, 20);
        validationHelper.validateString(tiers.getLastname(), true, 20);
      } else {
        validationHelper.validateString(tiers.getDenomination(), true, 60);
        if ((tiers.getIntercommunityVat() == null || tiers.getIntercommunityVat().equals("")
            || tiers.getIntercommunityVat().length() > 20))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      validationHelper.validateReferential(tiers.getTiersCategory(), false);
      validationHelper.validateReferential(tiers.getSalesEmployee(), true);
      validationHelper.validateReferential(tiers.getFormalisteEmployee(), false);
      validationHelper.validateReferential(tiers.getInsertionEmployee(), false);
      validationHelper.validateReferential(tiers.getLanguage(), true);
      validationHelper.validateReferential(tiers.getDeliveryService(), true);

      validationHelper.validateString(tiers.getAddress(), true, 60);
      validationHelper.validateReferential(tiers.getCountry(), true);
      if (tiers.getCountry() != null && tiers.getCountry().getCode().equals("FR"))
        validationHelper.validateString(tiers.getPostalCode(), true, 10);
      validationHelper.validateReferential(tiers.getCity(), true);

      validationHelper.validateString(tiers.getIntercom(), false, 12);
      if (tiers.getSpecialOffers() != null) {
        for (SpecialOffer specialOffer : tiers.getSpecialOffers()) {
          validationHelper.validateReferential(specialOffer, false);
        }
      }

      if (tiers.getMails() != null && tiers.getMails().size() > 0) {
        if (!validationHelper.validateMailList(tiers.getMails()))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      if (tiers.getPhones() != null && tiers.getPhones().size() > 0) {
        for (Phone phone : tiers.getPhones()) {
          if (!validationHelper.validateFrenchPhone(phone.getPhoneNumber())
              || !validationHelper.validateInternationalPhone(phone.getPhoneNumber()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
      }

      if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
        for (Document document : tiers.getDocuments()) {

          validationHelper.validateReferential(document.getDocumentType(), true);

          if (document.getMailsAffaire() != null && !validationHelper.validateMailList(document.getMailsAffaire()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          if (document.getMailsClient() != null && document.getMailsClient() != null
              && document.getMailsClient().size() > 0)
            if (!validationHelper.validateMailList(document.getMailsClient()))
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          validationHelper.validateString(document.getAffaireAddress(), false, 60);
          validationHelper.validateString(document.getClientAddress(), false, 60);
          validationHelper.validateString(document.getAffaireRecipient(), false, 40);
          validationHelper.validateString(document.getClientRecipient(), false, 40);
          validationHelper.validateString(document.getBillingLabel(), false, 40);
          validationHelper.validateString(document.getCommandNumber(), false, 40);
          validationHelper.validateReferential(document.getPaymentDeadlineType(), false);
          validationHelper.validateReferential(document.getRefundType(), false);
          validationHelper.validateString(document.getRefundIBAN(), false, 40);
          validationHelper.validateReferential(document.getBillingClosureType(), false);
          validationHelper.validateReferential(document.getBillingClosureRecipientType(), false);

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

      validationHelper.validateReferential(tiers.getPaymentType(), true);
      validationHelper.validateString(tiers.getPaymentIBAN(), false, 40);

      if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
        for (Responsable responsable : tiers.getResponsables()) {

          validationHelper.validateReferential(responsable.getCivility(), true);
          validationHelper.validateString(responsable.getFirstname(), true, 20);
          validationHelper.validateString(responsable.getLastname(), true, 20);
          validationHelper.validateReferential(responsable.getTiersType(), true);
          validationHelper.validateReferential(responsable.getTiersCategory(), false);
          validationHelper.validateReferential(responsable.getSalesEmployee(), true);
          validationHelper.validateReferential(responsable.getFormalisteEmployee(), false);
          validationHelper.validateReferential(responsable.getInsertionEmployee(), false);
          validationHelper.validateReferential(responsable.getLanguage(), true);
          validationHelper.validateString(responsable.getAddress(), true, 60);
          validationHelper.validateReferential(responsable.getCountry(), true);
          validationHelper.validateReferential(responsable.getCity(), true);
          if (responsable.getCountry() != null && responsable.getCountry().getCode().equals("FR"))
            validationHelper.validateString(responsable.getPostalCode(), true, 10);
          validationHelper.validateString(responsable.getFunction(), false, 20);
          validationHelper.validateString(responsable.getBuilding(), false, 20);
          validationHelper.validateString(responsable.getFloor(), false, 20);

          validationHelper.validateReferential(responsable.getSubscriptionPeriodType(), false);

          if (responsable.getDocuments() != null && responsable.getDocuments().size() > 0) {
            for (Document document : responsable.getDocuments()) {
              validationHelper.validateReferential(document.getDocumentType(), true);

              if (document.getMailsAffaire() != null && !validationHelper.validateMailList(document.getMailsAffaire()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
              if (document.getMailsClient() != null && document.getMailsClient() != null
                  && document.getMailsClient().size() > 0)
                if (!validationHelper.validateMailList(document.getMailsClient()))
                  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
              if (document.getMailsCCResponsableAffaire() != null
                  && document.getMailsCCResponsableAffaire().size() > 0) {
                for (Responsable res : document.getMailsCCResponsableAffaire()) {
                  if (res.getId() == null || responsableService.getResponsable(res.getId()) == null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
              }
              if (document.getMailsCCResponsableClient() != null && document.getMailsCCResponsableClient().size() > 0) {
                for (Responsable res : document.getMailsCCResponsableClient()) {
                  if (res.getId() == null || responsableService.getResponsable(res.getId()) == null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
              }

              validationHelper.validateString(document.getAffaireAddress(), false, 60);
              validationHelper.validateString(document.getClientAddress(), false, 60);
              validationHelper.validateString(document.getAffaireRecipient(), false, 40);
              validationHelper.validateString(document.getClientRecipient(), false, 40);
              validationHelper.validateString(document.getBillingLabel(), false, 40);
              validationHelper.validateString(document.getCommandNumber(), false, 40);
              validationHelper.validateReferential(document.getPaymentDeadlineType(), false);
              validationHelper.validateReferential(document.getRefundType(), false);
              validationHelper.validateString(document.getRefundIBAN(), false, 40);
              validationHelper.validateReferential(document.getBillingClosureType(), false);
              validationHelper.validateReferential(document.getBillingClosureRecipientType(), false);

            }
          }
        }
      }

      tiers = tiersService.addOrUpdateTiers(tiers);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Tiers>(tiers, HttpStatus.OK);
  }

}