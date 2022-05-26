package com.jss.jssbackend.modules.tiers.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.jss.jssbackend.libs.ValidationHelper;
import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.model.DeliveryService;
import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.model.PaymentType;
import com.jss.jssbackend.modules.miscellaneous.model.Region;
import com.jss.jssbackend.modules.miscellaneous.model.VatRate;
import com.jss.jssbackend.modules.miscellaneous.service.CityService;
import com.jss.jssbackend.modules.miscellaneous.service.CivilityService;
import com.jss.jssbackend.modules.miscellaneous.service.CountryService;
import com.jss.jssbackend.modules.miscellaneous.service.DeliveryServiceService;
import com.jss.jssbackend.modules.miscellaneous.service.DepartmentService;
import com.jss.jssbackend.modules.miscellaneous.service.LanguageService;
import com.jss.jssbackend.modules.miscellaneous.service.PaymentTypeService;
import com.jss.jssbackend.modules.miscellaneous.service.RegionService;
import com.jss.jssbackend.modules.miscellaneous.service.VatRateService;
import com.jss.jssbackend.modules.profile.service.EmployeeService;
import com.jss.jssbackend.modules.tiers.model.AttachmentType;
import com.jss.jssbackend.modules.tiers.model.BillingClosureRecipientType;
import com.jss.jssbackend.modules.tiers.model.BillingClosureType;
import com.jss.jssbackend.modules.tiers.model.BillingItem;
import com.jss.jssbackend.modules.tiers.model.BillingLabelType;
import com.jss.jssbackend.modules.tiers.model.BillingType;
import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.model.PaymentDeadlineType;
import com.jss.jssbackend.modules.tiers.model.Phone;
import com.jss.jssbackend.modules.tiers.model.RefundType;
import com.jss.jssbackend.modules.tiers.model.SpecialOffer;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.model.TiersAttachment;
import com.jss.jssbackend.modules.tiers.model.TiersCategory;
import com.jss.jssbackend.modules.tiers.model.TiersDocument;
import com.jss.jssbackend.modules.tiers.model.TiersDocumentType;
import com.jss.jssbackend.modules.tiers.model.TiersType;
import com.jss.jssbackend.modules.tiers.service.AttachmentTypeService;
import com.jss.jssbackend.modules.tiers.service.BillingClosureRecipientTypeService;
import com.jss.jssbackend.modules.tiers.service.BillingClosureTypeService;
import com.jss.jssbackend.modules.tiers.service.BillingItemService;
import com.jss.jssbackend.modules.tiers.service.BillingLabelTypeService;
import com.jss.jssbackend.modules.tiers.service.BillingTypeService;
import com.jss.jssbackend.modules.tiers.service.MailService;
import com.jss.jssbackend.modules.tiers.service.PaymentDeadlineTypeService;
import com.jss.jssbackend.modules.tiers.service.PhoneService;
import com.jss.jssbackend.modules.tiers.service.RefundTypeService;
import com.jss.jssbackend.modules.tiers.service.SpecialOfferService;
import com.jss.jssbackend.modules.tiers.service.TiersAttachmentService;
import com.jss.jssbackend.modules.tiers.service.TiersCategoryService;
import com.jss.jssbackend.modules.tiers.service.TiersDocumentTypeService;
import com.jss.jssbackend.modules.tiers.service.TiersService;
import com.jss.jssbackend.modules.tiers.service.TiersTypesService;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TiersController {

  private static final String inputEntryPoint = "/tiers";

  private static final Logger logger = LoggerFactory.getLogger(TiersController.class);

  @Autowired
  TiersTypesService clientTypesService;

  @Autowired
  TiersService tiersService;

  @Autowired
  CivilityService civilityService;

  @Autowired
  DeliveryServiceService deliveryServiceService;

  @Autowired
  TiersCategoryService tiersCategoryService;

  @Autowired
  LanguageService languageService;

  @Autowired
  EmployeeService employeeService;

  @Autowired
  CountryService countryService;

  @Autowired
  CityService cityService;

  @Autowired
  DepartmentService departmentService;

  @Autowired
  RegionService regionService;

  @Autowired
  SpecialOfferService specialOfferService;

  @Autowired
  BillingItemService billingItemService;

  @Autowired
  BillingTypeService billingTypeService;

  @Autowired
  VatRateService vatRateService;

  @Autowired
  MailService mailService;

  @Autowired
  PhoneService phoneService;

  @Autowired
  TiersDocumentTypeService tiersDocumentTypeService;

  @Autowired
  PaymentTypeService paymentTypeService;

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
  AttachmentTypeService attachmentTypeService;

  @Autowired
  TiersAttachmentService tiersAttachmentService;

  @GetMapping(inputEntryPoint + "/attachment-types")
  public ResponseEntity<List<AttachmentType>> getAttachmentTypes() {
    List<AttachmentType> attachmentTypes = null;
    try {
      attachmentTypes = attachmentTypeService.getAttachmentTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching attachmentType", e);
      return new ResponseEntity<List<AttachmentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching attachmentType", e);
      return new ResponseEntity<List<AttachmentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<AttachmentType>>(attachmentTypes, HttpStatus.OK);
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

  @GetMapping(inputEntryPoint + "/payment-types")
  public ResponseEntity<List<PaymentType>> getPaymentTypes() {
    List<PaymentType> paymentTypes = null;
    try {
      paymentTypes = paymentTypeService.getPaymentTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching paymentType", e);
      return new ResponseEntity<List<PaymentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching paymentType", e);
      return new ResponseEntity<List<PaymentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<PaymentType>>(paymentTypes, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/document-types")
  public ResponseEntity<List<TiersDocumentType>> getDocumentTypes() {
    List<TiersDocumentType> documentTypes = null;
    try {
      documentTypes = tiersDocumentTypeService.getTiersDocumentTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching documentTypes", e);
      return new ResponseEntity<List<TiersDocumentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching documentTypes", e);
      return new ResponseEntity<List<TiersDocumentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TiersDocumentType>>(documentTypes, HttpStatus.OK);
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

  @GetMapping(inputEntryPoint + "/vat-rates")
  public ResponseEntity<List<VatRate>> getVatRates() {
    List<VatRate> vatRates = null;
    try {
      vatRates = vatRateService.getVatRates();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching vatRate", e);
      return new ResponseEntity<List<VatRate>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching vatRate", e);
      return new ResponseEntity<List<VatRate>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<VatRate>>(vatRates, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billingTypes")
  public ResponseEntity<List<BillingType>> getBillingTypes() {
    List<BillingType> billingTypes = null;
    try {
      billingTypes = billingTypeService.getBillingTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billingType", e);
      return new ResponseEntity<List<BillingType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billingType", e);
      return new ResponseEntity<List<BillingType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BillingType>>(billingTypes, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/billing-items")
  public ResponseEntity<List<BillingItem>> getBillingItems() {
    List<BillingItem> billingItems = null;
    try {
      billingItems = billingItemService.getBillingItems();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching billingItem", e);
      return new ResponseEntity<List<BillingItem>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching billingItem", e);
      return new ResponseEntity<List<BillingItem>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BillingItem>>(billingItems, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/special-offers")
  public ResponseEntity<List<SpecialOffer>> getSpecialOffers() {
    List<SpecialOffer> specialOffers = null;
    try {
      specialOffers = specialOfferService.getSpecialOffers();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching specialOffer", e);
      return new ResponseEntity<List<SpecialOffer>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching specialOffer", e);
      return new ResponseEntity<List<SpecialOffer>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<SpecialOffer>>(specialOffers, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/regions")
  public ResponseEntity<List<Region>> getRegions() {
    List<Region> regions = null;
    try {
      regions = regionService.getRegions();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching region", e);
      return new ResponseEntity<List<Region>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching region", e);
      return new ResponseEntity<List<Region>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Region>>(regions, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/departments")
  public ResponseEntity<List<Department>> getDepartments() {
    List<Department> departments = null;
    try {
      departments = departmentService.getDepartments();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching department", e);
      return new ResponseEntity<List<Department>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching department", e);
      return new ResponseEntity<List<Department>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Department>>(departments, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/cities")
  public ResponseEntity<List<City>> getCities() {
    List<City> cities = null;
    try {
      cities = cityService.getCities();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching city", e);
      return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching city", e);
      return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/cities/search/postal-code")
  public ResponseEntity<List<City>> getCitiesByPostalCode(@RequestParam String postalCode) {
    List<City> cities = null;
    try {
      cities = cityService.getCitiesByPostalCode(postalCode);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching city", e);
      return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching city", e);
      return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/cities/search/country")
  public ResponseEntity<List<City>> getCitiesByCountry(@RequestParam(required = false) Integer countryId,
      @RequestParam String city) {
    List<City> cities = null;
    try {
      cities = cityService.getCitiesByCountry(countryId, city);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching city", e);
      return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching city", e);
      return new ResponseEntity<List<City>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/countries")
  public ResponseEntity<List<Country>> getCountries() {
    List<Country> countries = null;
    try {
      countries = countryService.getCountries();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching country", e);
      return new ResponseEntity<List<Country>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching country", e);
      return new ResponseEntity<List<Country>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Country>>(countries, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/types")
  public ResponseEntity<List<TiersType>> getClientTypes() {
    List<TiersType> clientTypes = null;
    try {
      clientTypes = clientTypesService.getTiersTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<TiersType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<TiersType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TiersType>>(clientTypes, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/languages")
  public ResponseEntity<List<Language>> getLanguages() {
    List<Language> languages = null;
    try {
      languages = languageService.getLanguages();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<Language>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<Language>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Language>>(languages, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/civilities")
  public ResponseEntity<List<Civility>> getCivilities() {
    List<Civility> civilities = null;
    try {
      civilities = civilityService.getCivilities();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<Civility>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<Civility>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Civility>>(civilities, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/delivery-services")
  public ResponseEntity<List<DeliveryService>> getDeliveryServices() {
    List<DeliveryService> deliveryServices = null;
    try {
      deliveryServices = deliveryServiceService.getDeliveryServices();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<List<DeliveryService>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<List<DeliveryService>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<DeliveryService>>(deliveryServices, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/categories")
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

  @GetMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> getTiersById(@RequestParam Integer id) {
    Tiers tiers = null;
    try {
      tiers = tiersService.getTiersById(id);
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

  @PostMapping(inputEntryPoint + "/tiers")
  public ResponseEntity<Tiers> addOrUpdateTiers(@RequestBody Tiers tiers) {
    if (tiers.getIsIndividual() == null)
      tiers.setIsIndividual(false);

    if (tiers.getTiersType() == null || tiers.getTiersType().getId() == null
        || clientTypesService.getTiersType(tiers.getTiersType().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getIsIndividual() == true && tiers.getDenomination() != null && !tiers.getDenomination().equals("")
        || tiers.getIsIndividual() == false
            && (tiers.getDenomination() == null || tiers.getDenomination().length() > 60))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getIsIndividual() == true
        && (tiers.getCivility() == null || tiers.getCivility().getId() == null
            || civilityService.getCivility(tiers.getCivility().getId()) == null)
        || tiers.getIsIndividual() == false && tiers.getCivility() != null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getTiersCategory() != null && tiers.getTiersCategory().getId() != null
        && tiersCategoryService.getTiersCategory(tiers.getTiersCategory().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getSalesEmployee() == null || tiers.getSalesEmployee().getId() == null
        || employeeService.getEmployeeById(tiers.getSalesEmployee().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getFormalisteEmployee() != null && (tiers.getSalesEmployee().getId() == null
        || employeeService.getEmployeeById(tiers.getFormalisteEmployee().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getInsertionEmployee() != null && (tiers.getInsertionEmployee().getId() == null
        || employeeService.getEmployeeById(tiers.getInsertionEmployee().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getLanguage() == null || tiers.getLanguage().getId() == null
        || languageService.getLanguageById(tiers.getLanguage().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getDeliveryService() == null || tiers.getDeliveryService().getId() == null
        || deliveryServiceService.getDeliveryServiceById(tiers.getDeliveryService().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getAddress() == null || tiers.getAddress().equals("") || tiers.getAddress().length() > 60)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getCountry() != null && tiers.getCountry().getCode().equals("FR")
        && (tiers.getPostalCode() == null || tiers.getPostalCode().equals("")
            || cityService.getCitiesByPostalCode(tiers.getPostalCode()).size() == 0))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getCity() == null || tiers.getCity().getLabel() == null || tiers.getCity().getLabel().equals(""))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getCity().getId() != null && cityService.getCity(tiers.getCity().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getCity().getId() == null && tiers.getCity().getLabel().length() > 30)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // TODO : crééer la ville en base si pas d'ID renseigné

    if (tiers.getCountry() == null || tiers.getCountry().getId() == null
        || countryService.getCountry(tiers.getCountry().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getIntercom() != null && tiers.getIntercom().length() > 12)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getIsIndividual() == false
        && (tiers.getIntercommunityVat() == null || tiers.getIntercommunityVat().equals("")
            || tiers.getIntercommunityVat().length() > 20))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getSpecialOffer() != null && (tiers.getSpecialOffer().getId() == null
        || specialOfferService.getSpecialOffer(tiers.getSpecialOffer().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getMails() != null && tiers.getMails().size() > 0) {
      if (!this.validateMailList(tiers.getMails()))
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (tiers.getPhones() != null && tiers.getPhones().size() > 0) {
      for (Phone phone : tiers.getPhones()) {
        if (!ValidationHelper.validateFrenchPhone(phone.getPhoneNumber())
            || !ValidationHelper.validateInternationalPhone(phone.getPhoneNumber()))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    // TODO : add control for document management ( checkbox
    // exclusivity and data)
    if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
      for (TiersDocument document : tiers.getDocuments()) {
        if (document.getTiersDocumentType() == null || document.getTiersDocumentType().getId() == null
            || tiersDocumentTypeService
                .getTiersDocumentType(document.getTiersDocumentType().getId()) == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
          if (!this.validateMailList(document.getMailsAffaire()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
          if (!this.validateMailList(document.getMailsClient()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getAffaireAddress() != null && document.getAffaireAddress().length() > 60)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getClientAddress() != null && document.getClientAddress().length() > 60)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getAffaireRecipient() != null && document.getAffaireRecipient().length() > 40)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getClientRecipient() != null && document.getClientRecipient().length() > 40)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getBillingLabel() != null && document.getBillingLabel().length() > 40)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getCommandNumber() != null && document.getCommandNumber().length() > 40)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getPaymentDeadlineType() != null && (document.getPaymentDeadlineType().getId() == null
            || paymentDeadlineTypeService.getPaymentDeadlineType(document.getPaymentDeadlineType().getId()) == null))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getRefundType() != null && (document.getRefundType().getId() == null
            || refundTypeService.getRefundType(document.getRefundType().getId()) == null))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getRefundIBAN() != null && document.getRefundIBAN().length() > 40)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getBillingClosureType() != null && (document.getBillingClosureType().getId() == null
            || billingClosureTypeService.getBillingClosureType(document.getBillingClosureType().getId()) == null))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (document.getBillingClosureRecipientType() != null
            && (document.getBillingClosureRecipientType().getId() == null
                || billingClosureRecipientTypeService
                    .getBillingClosureRecipientType(document.getBillingClosureRecipientType().getId()) == null))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    if (tiers.getPaymentType() == null || tiers.getPaymentType().getId() == null
        || paymentTypeService.getPaymentType(tiers.getPaymentType().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (tiers.getPaymentIBAN() != null && tiers.getPaymentIBAN().length() > 40)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    try {
      tiers = tiersService.addOrUpdateTiers(tiers);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<Tiers>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Tiers>(tiers, HttpStatus.OK);
  }

  private boolean validateMailList(List<Mail> mails) {
    EmailValidator emailvalidator = EmailValidator.getInstance();
    for (Mail mail : mails) {
      if (mail.getMail() == null || mail.getMail().length() > 30 || !emailvalidator.isValid(mail.getMail()))
        return false;
    }
    return true;
  }

  @PostMapping(inputEntryPoint + "/tiers-attachment/upload")
  public ResponseEntity<List<TiersAttachment>> uploadAttachment(@RequestParam("file") MultipartFile file,
      @RequestParam("idTiers") Integer idTiers, @RequestParam("idAttachmentType") Integer idAttachmentType,
      @RequestParam("filename") String filename) {
    try {
      if (idAttachmentType == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      AttachmentType attachmentType = attachmentTypeService.getAttachmentType(idAttachmentType);

      if (attachmentType == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      if (filename == null || filename.equals(""))
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      if (idTiers == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      Tiers tiers = tiersService.getTiersById(idTiers);
      List<TiersAttachment> tiersAttachments = tiersAttachmentService.addTiersAttachment(file, tiers, attachmentType,
          filename);

      return new ResponseEntity<List<TiersAttachment>>(tiersAttachments, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Could not upload the file: " + file.getOriginalFilename() + "!", e);
      return new ResponseEntity<List<TiersAttachment>>(HttpStatus.EXPECTATION_FAILED);
    }
  }

  @GetMapping(inputEntryPoint + "/tiers-attachment/preview")
  public ResponseEntity<byte[]> downloadAttachment(@RequestParam("idAttachment") Integer idAttachment) {
    byte[] data = null;
    HttpHeaders headers = null;
    try {
      TiersAttachment tiersAttachment = tiersAttachmentService.getTiersAttachment(idAttachment);

      if (tiersAttachment == null || tiersAttachment.getUploadedFile() == null
          || tiersAttachment.getUploadedFile().getPath() == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      File file = new File(tiersAttachment.getUploadedFile().getPath());

      if (file != null) {
        data = Files.readAllBytes(file.toPath());

        headers = new HttpHeaders();
        headers.add("filename", tiersAttachment.getUploadedFile().getFilename());
        headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
        headers.setContentLength(data.length);

        // Compute content type
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null)
          mimeType = "application/octet-stream";
        headers.set("content-type", mimeType);

      }
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
  }
}