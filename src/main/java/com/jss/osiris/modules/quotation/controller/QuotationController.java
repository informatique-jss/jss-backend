package com.jss.osiris.modules.quotation.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.CivilityService;
import com.jss.osiris.modules.miscellaneous.service.CountryService;
import com.jss.osiris.modules.miscellaneous.service.DepartmentService;
import com.jss.osiris.modules.miscellaneous.service.LanguageService;
import com.jss.osiris.modules.miscellaneous.service.LegalFormService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.BodaccFusion;
import com.jss.osiris.modules.quotation.model.BodaccFusionAbsorbedCompany;
import com.jss.osiris.modules.quotation.model.BodaccFusionMergingCompany;
import com.jss.osiris.modules.quotation.model.BodaccPublicationType;
import com.jss.osiris.modules.quotation.model.BodaccSale;
import com.jss.osiris.modules.quotation.model.BodaccSplit;
import com.jss.osiris.modules.quotation.model.BodaccSplitBeneficiary;
import com.jss.osiris.modules.quotation.model.BodaccSplitCompany;
import com.jss.osiris.modules.quotation.model.BuildingDomiciliation;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.FundType;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.RecordType;
import com.jss.osiris.modules.quotation.model.Regie;
import com.jss.osiris.modules.quotation.model.Rna;
import com.jss.osiris.modules.quotation.model.Shal;
import com.jss.osiris.modules.quotation.model.ShalNoticeTemplate;
import com.jss.osiris.modules.quotation.model.Siren;
import com.jss.osiris.modules.quotation.model.Siret;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.service.ActTypeService;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.BodaccPublicationTypeService;
import com.jss.osiris.modules.quotation.service.BuildingDomiciliationService;
import com.jss.osiris.modules.quotation.service.CharacterPriceService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DomiciliationContractTypeService;
import com.jss.osiris.modules.quotation.service.FundTypeService;
import com.jss.osiris.modules.quotation.service.JournalTypeService;
import com.jss.osiris.modules.quotation.service.MailRedirectionTypeService;
import com.jss.osiris.modules.quotation.service.NoticeTypeFamilyService;
import com.jss.osiris.modules.quotation.service.NoticeTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionFamilyTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionTypeService;
import com.jss.osiris.modules.quotation.service.QuotationLabelTypeService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.quotation.service.RecordTypeService;
import com.jss.osiris.modules.quotation.service.RegieService;
import com.jss.osiris.modules.quotation.service.RnaDelegateService;
import com.jss.osiris.modules.quotation.service.ShalNoticeTemplateService;
import com.jss.osiris.modules.quotation.service.SireneDelegateService;
import com.jss.osiris.modules.quotation.service.TransfertFundsTypeService;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@RestController
public class QuotationController {

  private static final String inputEntryPoint = "/quotation";

  private static final Logger logger = LoggerFactory.getLogger(QuotationController.class);

  @Autowired
  ValidationHelper validationHelper;

  @Autowired
  QuotationService quotationService;

  @Autowired
  QuotationStatusService quotationStatusService;

  @Autowired
  TiersService tiersService;

  @Autowired
  ResponsableService responsableService;

  @Autowired
  SpecialOfferService specialOfferService;

  @Autowired
  QuotationLabelTypeService quotationLabelTypeService;

  @Autowired
  RecordTypeService recordTypeService;

  @Autowired
  SireneDelegateService sireneDelegateService;

  @Autowired
  RnaDelegateService rnaDelegateService;

  @Autowired
  CityService cityService;

  @Autowired
  CountryService countryService;

  @Autowired
  CivilityService civilityService;

  @Autowired
  LegalFormService legalFormService;

  @Autowired
  ProvisionFamilyTypeService provisionFamilyTypeService;

  @Autowired
  ProvisionTypeService provisionTypeService;

  @Autowired
  DomiciliationContractTypeService contractTypeService;

  @Autowired
  BuildingDomiciliationService buildingDomiciliationService;

  @Autowired
  MailRedirectionTypeService mailRedirectionTypeService;

  @Autowired
  DomiciliationContractTypeService domiciliationContractTypeService;

  @Autowired
  LanguageService languageService;

  @Autowired
  AffaireService affaireService;

  @Autowired
  ConfrereService confrereService;

  @Autowired
  JournalTypeService journalTypeService;

  @Autowired
  CharacterPriceService characterPriceService;

  @Autowired
  DepartmentService departmentService;

  @Autowired
  NoticeTypeService noticeTypeService;

  @Autowired
  NoticeTypeFamilyService noticeTypeFamilyService;

  @Autowired
  BodaccPublicationTypeService bodaccPublicationTypeService;

  @Autowired
  TransfertFundsTypeService transfertFundsTypeService;

  @Autowired
  FundTypeService fundTypeService;

  @Autowired
  ActTypeService actTypeService;

  @Autowired
  ShalNoticeTemplateService shalNoticeTemplateService;

  @Autowired
  RegieService regieService;

  @Autowired
  CustomerOrderService customerOrderService;

  @Value("${miscellaneous.document.billing.label.type.affaire.code}")
  private String billingLabelAffaireCode;

  @GetMapping(inputEntryPoint + "/regies")
  public ResponseEntity<List<Regie>> getRegies() {
    List<Regie> regies = null;
    try {
      regies = regieService.getRegies();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching regie", e);
      return new ResponseEntity<List<Regie>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching regie", e);
      return new ResponseEntity<List<Regie>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Regie>>(regies, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/regie")
  public ResponseEntity<Regie> addOrUpdateRegie(
      @RequestBody Regie regies) {
    Regie outRegie;
    try {
      if (regies.getId() != null)
        validationHelper.validateReferential(regies, true);
      validationHelper.validateString(regies.getCode(), true, 20);
      validationHelper.validateString(regies.getLabel(), true, 100);

      validationHelper.validateString(regies.getIban(), true, 40);
      validationHelper.validateString(regies.getMailRecipient(), false, 60);
      validationHelper.validateString(regies.getAddress(), false, 60);
      validationHelper.validateString(regies.getPostalCode(), false, 10);
      validationHelper.validateReferential(regies.getCity(), false);
      validationHelper.validateReferential(regies.getCountry(), false);

      outRegie = regieService
          .addOrUpdateRegie(regies);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching regie", e);
      return new ResponseEntity<Regie>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching regie", e);
      return new ResponseEntity<Regie>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Regie>(outRegie, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/shal-notice-templates")
  public ResponseEntity<List<ShalNoticeTemplate>> getShalNoticeTemplates() {
    List<ShalNoticeTemplate> shalNoticeTemplates = null;
    try {
      shalNoticeTemplates = shalNoticeTemplateService.getShalNoticeTemplates();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching shalNoticeTemplate", e);
      return new ResponseEntity<List<ShalNoticeTemplate>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching shalNoticeTemplate", e);
      return new ResponseEntity<List<ShalNoticeTemplate>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<ShalNoticeTemplate>>(shalNoticeTemplates, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/shal-notice-template")
  public ResponseEntity<ShalNoticeTemplate> addOrUpdateShalNoticeTemplate(
      @RequestBody ShalNoticeTemplate shalNoticeTemplates) {
    ShalNoticeTemplate outShalNoticeTemplate;
    try {
      if (shalNoticeTemplates.getId() != null)
        validationHelper.validateReferential(shalNoticeTemplates, true);
      validationHelper.validateString(shalNoticeTemplates.getCode(), true, 20);
      validationHelper.validateString(shalNoticeTemplates.getLabel(), true, 100);

      outShalNoticeTemplate = shalNoticeTemplateService
          .addOrUpdateShalNoticeTemplate(shalNoticeTemplates);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching shalNoticeTemplate", e);
      return new ResponseEntity<ShalNoticeTemplate>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching shalNoticeTemplate", e);
      return new ResponseEntity<ShalNoticeTemplate>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<ShalNoticeTemplate>(outShalNoticeTemplate, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/act-types")
  public ResponseEntity<List<ActType>> getActTypes() {
    List<ActType> actTypes = null;
    try {
      actTypes = actTypeService.getActTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching actType", e);
      return new ResponseEntity<List<ActType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching actType", e);
      return new ResponseEntity<List<ActType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<ActType>>(actTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/act-type")
  public ResponseEntity<ActType> addOrUpdateActType(@RequestBody ActType actType) {
    ActType outActType;
    try {
      if (actType.getId() != null)
        validationHelper.validateReferential(actType, true);
      validationHelper.validateString(actType.getCode(), true, 20);
      validationHelper.validateString(actType.getLabel(), true, 100);

      outActType = actTypeService.addOrUpdateActType(actType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<ActType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<ActType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<ActType>(outActType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/fund-types")
  public ResponseEntity<List<FundType>> getFundTypes() {
    List<FundType> fundTypes = null;
    try {
      fundTypes = fundTypeService.getFundTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching fundType", e);
      return new ResponseEntity<List<FundType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching fundType", e);
      return new ResponseEntity<List<FundType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<FundType>>(fundTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/fund-type")
  public ResponseEntity<FundType> addOrUpdateFundType(
      @RequestBody FundType fundType) {
    FundType outFundType;
    try {
      if (fundType.getId() != null)
        validationHelper.validateReferential(fundType, true);
      validationHelper.validateString(fundType.getCode(), true, 20);
      validationHelper.validateString(fundType.getLabel(), true, 100);

      outFundType = fundTypeService
          .addOrUpdateFundType(fundType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<FundType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<FundType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<FundType>(outFundType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/transfert-fund-types")
  public ResponseEntity<List<TransfertFundsType>> getTransfertFundsTypes() {
    List<TransfertFundsType> transfertFundsTypes = null;
    try {
      transfertFundsTypes = transfertFundsTypeService.getTransfertFundsTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching transfertFundsType", e);
      return new ResponseEntity<List<TransfertFundsType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching transfertFundsType", e);
      return new ResponseEntity<List<TransfertFundsType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<TransfertFundsType>>(transfertFundsTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/transfert-fund-type")
  public ResponseEntity<TransfertFundsType> addOrUpdateTransfertFundsType(
      @RequestBody TransfertFundsType transfertFundsType) {
    TransfertFundsType outTransfertFundsType;
    try {
      if (transfertFundsType.getId() != null)
        validationHelper.validateReferential(transfertFundsType, true);
      validationHelper.validateString(transfertFundsType.getCode(), true, 20);
      validationHelper.validateString(transfertFundsType.getLabel(), true, 100);

      outTransfertFundsType = transfertFundsTypeService
          .addOrUpdateTransfertFundsType(transfertFundsType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<TransfertFundsType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<TransfertFundsType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<TransfertFundsType>(outTransfertFundsType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/bodacc-publication-types")
  public ResponseEntity<List<BodaccPublicationType>> getBodaccPublicationTypes() {
    List<BodaccPublicationType> bodaccPublicationTypes = null;
    try {
      bodaccPublicationTypes = bodaccPublicationTypeService.getBodaccPublicationTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching bodaccPublicationType", e);
      return new ResponseEntity<List<BodaccPublicationType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching bodaccPublicationType", e);
      return new ResponseEntity<List<BodaccPublicationType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BodaccPublicationType>>(bodaccPublicationTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/bodacc-publication-type")
  public ResponseEntity<BodaccPublicationType> addOrUpdateActType(
      @RequestBody BodaccPublicationType bodaccPublicationType) {
    BodaccPublicationType outBodaccPublicationType;
    try {
      if (bodaccPublicationType.getId() != null)
        validationHelper.validateReferential(bodaccPublicationType, true);
      validationHelper.validateString(bodaccPublicationType.getCode(), true, 20);
      validationHelper.validateString(bodaccPublicationType.getLabel(), true, 100);

      outBodaccPublicationType = bodaccPublicationTypeService.addOrUpdateBodaccPublicationType(bodaccPublicationType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<BodaccPublicationType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<BodaccPublicationType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<BodaccPublicationType>(outBodaccPublicationType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/notice-type-families")
  public ResponseEntity<List<NoticeTypeFamily>> getNoticeTypeFamilies() {
    List<NoticeTypeFamily> noticeTypeFamilies = null;
    try {
      noticeTypeFamilies = noticeTypeFamilyService.getNoticeTypeFamilies();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching noticeTypeFamily", e);
      return new ResponseEntity<List<NoticeTypeFamily>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching noticeTypeFamily", e);
      return new ResponseEntity<List<NoticeTypeFamily>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<NoticeTypeFamily>>(noticeTypeFamilies, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/notice-type-family")
  public ResponseEntity<NoticeTypeFamily> addOrUpdateNoticeTypeFamily(
      @RequestBody NoticeTypeFamily noticeTypeFamily) {
    NoticeTypeFamily outNoticeTypeFamily;
    try {
      if (noticeTypeFamily.getId() != null)
        validationHelper.validateReferential(noticeTypeFamily, true);
      validationHelper.validateString(noticeTypeFamily.getCode(), true, 20);
      validationHelper.validateString(noticeTypeFamily.getLabel(), true, 100);

      outNoticeTypeFamily = noticeTypeFamilyService.addOrUpdateNoticeTypeFamily(noticeTypeFamily);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<NoticeTypeFamily>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<NoticeTypeFamily>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<NoticeTypeFamily>(outNoticeTypeFamily, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/notice-types")
  public ResponseEntity<List<NoticeType>> getNoticeTypes() {
    List<NoticeType> noticeTypes = null;
    try {
      noticeTypes = noticeTypeService.getNoticeTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching noticeType", e);
      return new ResponseEntity<List<NoticeType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching noticeType", e);
      return new ResponseEntity<List<NoticeType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<NoticeType>>(noticeTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/notice-type")
  public ResponseEntity<NoticeType> addOrUpdateNoticeType(
      @RequestBody NoticeType noticeTypes) {
    NoticeType outNoticeType;
    try {
      if (noticeTypes.getId() != null)
        validationHelper.validateReferential(noticeTypes, true);
      validationHelper.validateString(noticeTypes.getCode(), true, 20);
      validationHelper.validateString(noticeTypes.getLabel(), true, 100);

      outNoticeType = noticeTypeService
          .addOrUpdateNoticeType(noticeTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching noticeType", e);
      return new ResponseEntity<NoticeType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching noticeType", e);
      return new ResponseEntity<NoticeType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<NoticeType>(outNoticeType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/character-price")
  public ResponseEntity<CharacterPrice> getCharacterPrice(@RequestParam Integer departmentId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date) {
    CharacterPrice characterPrice = null;
    if (departmentId == null || date == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Department department = departmentService.getDepartment(departmentId);

    if (department == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    try {
      characterPrice = characterPriceService.getCharacterPrice(department, date);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching character price", e);
      return new ResponseEntity<CharacterPrice>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching character price", e);
      return new ResponseEntity<CharacterPrice>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<CharacterPrice>(characterPrice, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/character-prices")
  public ResponseEntity<List<CharacterPrice>> getCharacterPrices() {
    List<CharacterPrice> characterPrices = null;
    try {
      characterPrices = characterPriceService.getCharacterPrices();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching characterPrice", e);
      return new ResponseEntity<List<CharacterPrice>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching characterPrice", e);
      return new ResponseEntity<List<CharacterPrice>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<CharacterPrice>>(characterPrices, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/character-price")
  public ResponseEntity<CharacterPrice> addOrUpdateCharacterPrice(
      @RequestBody CharacterPrice characterPrices) {
    CharacterPrice outCharacterPrice;
    try {
      if (characterPrices.getId() != null)
        validationHelper.validateReferential(characterPrices, true);
      if (characterPrices.getDepartments() == null || characterPrices.getDepartments().size() == 0)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      for (Department department : characterPrices.getDepartments())
        validationHelper.validateReferential(department, true);

      validationHelper.validateDate(characterPrices.getStartDate(), true);

      outCharacterPrice = characterPriceService
          .addOrUpdateCharacterPrice(characterPrices);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching characterPrice", e);
      return new ResponseEntity<CharacterPrice>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching characterPrice", e);
      return new ResponseEntity<CharacterPrice>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<CharacterPrice>(outCharacterPrice, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/journal-types")
  public ResponseEntity<List<JournalType>> getJournalTypes() {
    List<JournalType> journalTypes = null;
    try {
      journalTypes = journalTypeService.getJournalTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching journalType", e);
      return new ResponseEntity<List<JournalType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching journalType", e);
      return new ResponseEntity<List<JournalType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<JournalType>>(journalTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/journal-type")
  public ResponseEntity<JournalType> addOrUpdateJournalType(
      @RequestBody JournalType journalType) {
    JournalType outJournalType;
    try {
      if (journalType.getId() != null)
        validationHelper.validateReferential(journalType, true);
      validationHelper.validateString(journalType.getCode(), true, 20);
      validationHelper.validateString(journalType.getLabel(), true, 100);

      outJournalType = journalTypeService.addOrUpdateJournalType(journalType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<JournalType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<JournalType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<JournalType>(outJournalType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/confreres")
  public ResponseEntity<List<Confrere>> getConfreres() {
    List<Confrere> confreres = null;
    try {
      confreres = confrereService.getConfreres();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching confrere", e);
      return new ResponseEntity<List<Confrere>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching confrere", e);
      return new ResponseEntity<List<Confrere>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Confrere>>(confreres, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/confrere")
  public ResponseEntity<Confrere> addOrUpdateConfrere(
      @RequestBody Confrere confrere) {
    Confrere outConfrere;
    try {
      if (confrere.getId() != null)
        validationHelper.validateReferential(confrere, true);
      validationHelper.validateString(confrere.getCode(), true, 20);
      validationHelper.validateString(confrere.getLabel(), true, 100);

      if (confrere.getDepartments() != null && confrere.getDepartments().size() > 0)
        for (Department department : confrere.getDepartments())
          validationHelper.validateReferential(department, false);

      if (confrere.getWeekDays() != null && confrere.getWeekDays().size() > 0)
        for (WeekDay weekDay : confrere.getWeekDays())
          validationHelper.validateReferential(weekDay, false);

      validationHelper.validateReferential(confrere.getJournalType(), true);

      validationHelper.validateString(confrere.getLastShipmentForPublication(), false, 60);
      validationHelper.validateString(confrere.getBoardGrade(), false, 20);
      validationHelper.validateString(confrere.getPaperGrade(), false, 20);
      validationHelper.validateString(confrere.getBillingGrade(), false, 20);
      validationHelper.validateString(confrere.getPublicationCertificateDocumentGrade(), false, 20);
      validationHelper.validateString(confrere.getMailRecipient(), false, 60);
      validationHelper.validateString(confrere.getAddress(), false, 60);
      validationHelper.validateString(confrere.getPostalCode(), false, 10);
      validationHelper.validateReferential(confrere.getCity(), false);
      validationHelper.validateReferential(confrere.getCountry(), false);
      validationHelper.validateString(confrere.getIban(), false, 40);
      validationHelper.validateReferential(confrere.getRegie(), false);
      validationHelper.validateReferential(confrere.getVatCollectionType(), true);

      validationHelper.validateReferential(confrere.getPaymentType(), true);

      if (confrere.getSpecialOffers() != null) {
        for (SpecialOffer specialOffer : confrere.getSpecialOffers()) {
          validationHelper.validateReferential(specialOffer, false);
        }
      }

      if (confrere.getDocuments() != null && confrere.getDocuments().size() > 0) {
        for (Document document : confrere.getDocuments()) {

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

      outConfrere = confrereService
          .addOrUpdateConfrere(confrere);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching confrere", e);
      return new ResponseEntity<Confrere>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching confrere", e);
      return new ResponseEntity<Confrere>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Confrere>(outConfrere, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail-redirection-types")
  public ResponseEntity<List<MailRedirectionType>> getMailRedirectionTypes() {
    List<MailRedirectionType> mailRedirectionTypes = null;
    try {
      mailRedirectionTypes = mailRedirectionTypeService.getMailRedirectionTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching mailRedirectionType", e);
      return new ResponseEntity<List<MailRedirectionType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching mailRedirectionType", e);
      return new ResponseEntity<List<MailRedirectionType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<MailRedirectionType>>(mailRedirectionTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail-redirection-type")
  public ResponseEntity<MailRedirectionType> addOrUpdateMailRedirectionType(
      @RequestBody MailRedirectionType mailRedirectionType) {
    MailRedirectionType outMailRedirectionType;
    try {
      if (mailRedirectionType.getId() != null)
        validationHelper.validateReferential(mailRedirectionType, true);
      validationHelper.validateString(mailRedirectionType.getCode(), true, 20);
      validationHelper.validateString(mailRedirectionType.getLabel(), true, 100);

      outMailRedirectionType = mailRedirectionTypeService.addOrUpdateMailRedirectionType(mailRedirectionType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<MailRedirectionType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<MailRedirectionType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<MailRedirectionType>(outMailRedirectionType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/building-domiciliations")
  public ResponseEntity<List<BuildingDomiciliation>> getBuildingDomiciliations() {
    List<BuildingDomiciliation> buildingDomiciliations = null;
    try {
      buildingDomiciliations = buildingDomiciliationService.getBuildingDomiciliations();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching buildingDomiciliation", e);
      return new ResponseEntity<List<BuildingDomiciliation>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching buildingDomiciliation", e);
      return new ResponseEntity<List<BuildingDomiciliation>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<BuildingDomiciliation>>(buildingDomiciliations, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/building-domiciliation")
  public ResponseEntity<BuildingDomiciliation> addOrUpdateBuildingDomiciliation(
      @RequestBody BuildingDomiciliation buildingDomiciliation) {
    BuildingDomiciliation outBuildingDomiciliation;
    try {
      if (buildingDomiciliation.getId() != null)
        validationHelper.validateReferential(buildingDomiciliation, true);
      validationHelper.validateString(buildingDomiciliation.getCode(), true, 20);
      validationHelper.validateString(buildingDomiciliation.getLabel(), true, 100);
      validationHelper.validateString(buildingDomiciliation.getAddress(), false, 60);
      validationHelper.validateString(buildingDomiciliation.getPostalCode(), false, 10);
      validationHelper.validateReferential(buildingDomiciliation.getCity(), false);
      validationHelper.validateReferential(buildingDomiciliation.getCountry(), false);

      outBuildingDomiciliation = buildingDomiciliationService.addOrUpdateBuildingDomiciliation(buildingDomiciliation);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<BuildingDomiciliation>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<BuildingDomiciliation>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<BuildingDomiciliation>(outBuildingDomiciliation, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/domiciliation-contract-types")
  public ResponseEntity<List<DomiciliationContractType>> getContractTypes() {
    List<DomiciliationContractType> domiciliationContractTypes = null;
    try {
      domiciliationContractTypes = contractTypeService.getDomiciliationContractTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching contractType", e);
      return new ResponseEntity<List<DomiciliationContractType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching contractType", e);
      return new ResponseEntity<List<DomiciliationContractType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<DomiciliationContractType>>(domiciliationContractTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/domiciliation-contract-type")
  public ResponseEntity<DomiciliationContractType> addOrUpdateDomiciliationContractType(
      @RequestBody DomiciliationContractType domiciliationContractType) {
    DomiciliationContractType outDomiciliationContractType;
    try {
      if (domiciliationContractType.getId() != null)
        validationHelper.validateReferential(domiciliationContractType, true);
      validationHelper.validateString(domiciliationContractType.getCode(), true, 20);
      validationHelper.validateString(domiciliationContractType.getLabel(), true, 100);

      outDomiciliationContractType = domiciliationContractTypeService
          .addOrUpdateDomiciliationContractType(domiciliationContractType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<DomiciliationContractType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<DomiciliationContractType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<DomiciliationContractType>(outDomiciliationContractType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision-types")
  public ResponseEntity<List<ProvisionType>> getProvisionTypes() {
    List<ProvisionType> provisionTypes = null;
    try {
      provisionTypes = provisionTypeService.getProvisionTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching provisionType", e);
      return new ResponseEntity<List<ProvisionType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching provisionType", e);
      return new ResponseEntity<List<ProvisionType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<ProvisionType>>(provisionTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/provision-type")
  public ResponseEntity<ProvisionType> addOrUpdateProvisionType(
      @RequestBody ProvisionType provisionTypes) {
    ProvisionType outProvisionType;
    try {
      if (provisionTypes.getId() != null)
        validationHelper.validateReferential(provisionTypes, true);
      validationHelper.validateString(provisionTypes.getCode(), true, 20);
      validationHelper.validateString(provisionTypes.getLabel(), true, 100);

      if (provisionTypes.getBillingTypes() != null && provisionTypes.getBillingTypes().size() > 0)
        for (BillingType billingType : provisionTypes.getBillingTypes())
          validationHelper.validateReferential(billingType, false);

      outProvisionType = provisionTypeService
          .addOrUpdateProvisionType(provisionTypes);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching provisionType", e);
      return new ResponseEntity<ProvisionType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching provisionType", e);
      return new ResponseEntity<ProvisionType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<ProvisionType>(outProvisionType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision-family-types")
  public ResponseEntity<List<ProvisionFamilyType>> getProvisionFamilyTypes() {
    List<ProvisionFamilyType> provisionFamilyTypes = null;
    try {
      provisionFamilyTypes = provisionFamilyTypeService.getProvisionFamilyTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching provisionFamilyType", e);
      return new ResponseEntity<List<ProvisionFamilyType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching provisionFamilyType", e);
      return new ResponseEntity<List<ProvisionFamilyType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<ProvisionFamilyType>>(provisionFamilyTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/provision-family-type")
  public ResponseEntity<ProvisionFamilyType> addOrUpdateProvisionFamilyType(
      @RequestBody ProvisionFamilyType provisionFamilyType) {
    ProvisionFamilyType outProvisionFamilyType;
    try {
      if (provisionFamilyType.getId() != null)
        validationHelper.validateReferential(provisionFamilyType, true);
      validationHelper.validateString(provisionFamilyType.getCode(), true, 20);
      validationHelper.validateString(provisionFamilyType.getLabel(), true, 100);

      outProvisionFamilyType = provisionFamilyTypeService.addOrUpdateProvisionFamilyType(provisionFamilyType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<ProvisionFamilyType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<ProvisionFamilyType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<ProvisionFamilyType>(outProvisionFamilyType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/siren")
  public ResponseEntity<List<Siren>> getSiren(@RequestParam String siren) {
    if (siren == null || siren.equals("") || siren.replaceAll(" ", "").length() != 9)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    List<Siren> sirenFound = null;
    try {
      sirenFound = sireneDelegateService.getSiren(siren.replaceAll(" ", ""));
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siren", e);
      return new ResponseEntity<List<Siren>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Siren>>(sirenFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/siret")
  public ResponseEntity<List<Siret>> getSiret(@RequestParam String siret) {
    if (siret == null || siret.equals("") || siret.replaceAll(" ", "").length() != 14)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    List<Siret> siretFound = null;
    try {
      siretFound = sireneDelegateService.getSiret(siret.replaceAll(" ", ""));
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siret", e);
      return new ResponseEntity<List<Siret>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Siret>>(siretFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/rna")
  public ResponseEntity<List<Rna>> getRna(@RequestParam String rna) {
    if (rna == null || rna.equals("")
        || rna.replaceAll(" ", "").length() != 10 && !rna.toUpperCase().subSequence(0, 1).equals("W"))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    List<Rna> rnaFound = null;
    try {
      rnaFound = rnaDelegateService.getRna(rna.replaceAll(" ", ""));
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siret", e);
      return new ResponseEntity<List<Rna>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Rna>>(rnaFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/affaire")
  public ResponseEntity<Affaire> getRna(@RequestParam Integer id) {
    Affaire affaireFound = null;
    if (id == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    try {
      affaireFound = affaireService.getAffaire(id);
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siret", e);
      return new ResponseEntity<Affaire>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Affaire>(affaireFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/record-types")
  public ResponseEntity<List<RecordType>> getRecordTypes() {
    List<RecordType> recordTypes = null;
    try {
      recordTypes = recordTypeService.getRecordTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching recordType", e);
      return new ResponseEntity<List<RecordType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching recordType", e);
      return new ResponseEntity<List<RecordType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<RecordType>>(recordTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/record-type")
  public ResponseEntity<RecordType> addOrUpdateRecordType(
      @RequestBody RecordType recordType) {
    RecordType outRecordType;
    try {
      if (recordType.getId() != null)
        validationHelper.validateReferential(recordType, true);
      validationHelper.validateString(recordType.getCode(), true, 20);
      validationHelper.validateString(recordType.getLabel(), true, 100);

      outRecordType = recordTypeService.addOrUpdateRecordType(recordType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<RecordType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<RecordType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<RecordType>(outRecordType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation-label-types")
  public ResponseEntity<List<QuotationLabelType>> getQuotationLabelTypes() {
    List<QuotationLabelType> quotationLabelTypes = null;
    try {
      quotationLabelTypes = quotationLabelTypeService.getQuotationLabelTypes();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotationLabelType", e);
      return new ResponseEntity<List<QuotationLabelType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotationLabelType", e);
      return new ResponseEntity<List<QuotationLabelType>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<QuotationLabelType>>(quotationLabelTypes, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation-label-type")
  public ResponseEntity<QuotationLabelType> addOrUpdateQuotationLabelType(
      @RequestBody QuotationLabelType quotationLabelType) {
    QuotationLabelType outQuotationLabelType;
    try {
      if (quotationLabelType.getId() != null)
        validationHelper.validateReferential(quotationLabelType, true);
      validationHelper.validateString(quotationLabelType.getCode(), true, 20);
      validationHelper.validateString(quotationLabelType.getLabel(), true, 100);

      outQuotationLabelType = quotationLabelTypeService.addOrUpdateQuotationLabelType(quotationLabelType);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<QuotationLabelType>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<QuotationLabelType>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<QuotationLabelType>(outQuotationLabelType, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation-status")
  public ResponseEntity<List<QuotationStatus>> getQuotationStatus() {
    List<QuotationStatus> quotationStatus = null;
    try {
      quotationStatus = quotationStatusService.getQuotationStatus();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotationStatus", e);
      return new ResponseEntity<List<QuotationStatus>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotationStatus", e);
      return new ResponseEntity<List<QuotationStatus>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<QuotationStatus>>(quotationStatus, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation-status")
  public ResponseEntity<QuotationStatus> addOrUpdateQuotationStatus(
      @RequestBody QuotationStatus quotationStatus) {
    QuotationStatus outQuotationStatus;
    try {
      if (quotationStatus.getId() != null)
        validationHelper.validateReferential(quotationStatus, true);
      validationHelper.validateString(quotationStatus.getCode(), true, 20);
      validationHelper.validateString(quotationStatus.getLabel(), true, 100);

      outQuotationStatus = quotationStatusService.addOrUpdateQuotationStatus(quotationStatus);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<QuotationStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<QuotationStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<QuotationStatus>(outQuotationStatus, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation")
  public ResponseEntity<Quotation> getQuotation(@RequestParam Integer id) {
    Quotation quotation = null;
    try {
      quotation = quotationService.getQuotation(id);
      if (quotation == null)
        quotation = new Quotation();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order")
  public ResponseEntity<CustomerOrder> getCustomerOrder(@RequestParam Integer id) {
    CustomerOrder customerOrder = null;
    try {
      customerOrder = customerOrderService.getCustomerOrder(id);
      if (customerOrder == null)
        customerOrder = new CustomerOrder();
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching  customer-order", e);
      return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching customer-order", e);
      return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation")
  public ResponseEntity<Quotation> addOrUpdateQuotation(@RequestBody Quotation quotation) {
    try {
      validateQuotationAndCustomerOrder(quotation);

      QuotationStatus quotationStatus = quotationStatusService.getQuotationStatusByCode(QuotationStatus.OPEN);
      if (quotationStatus == null)
        if (quotation.getQuotationStatus() == null) {
          logger.error("OPEN Quotation status not found");
          return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

      if (quotation.getQuotationStatus() == null)
        quotation.setQuotationStatus(quotationStatus);
      quotation = quotationService.addOrUpdateQuotation(quotation);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/order/search")
  public ResponseEntity<List<CustomerOrder>> searchOrders(@RequestBody OrderingSearch orderingSearch) {
    List<CustomerOrder> orders;
    if (orderingSearch == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (orderingSearch.getStartDate() == null || orderingSearch.getEndDate() == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Duration duration = Duration.between(orderingSearch.getStartDate(), orderingSearch.getEndDate());

    if (duration.toDays() > 366)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    try {
      validationHelper.validateReferential(orderingSearch.getSalesEmployee(), false);
      if (orderingSearch.getQuotationStatus() != null)
        for (QuotationStatus status : orderingSearch.getQuotationStatus())
          validationHelper.validateReferential(status, false);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try {
      orders = customerOrderService.searchOrders(orderingSearch);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching payment", e);
      return new ResponseEntity<List<CustomerOrder>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching payment", e);
      return new ResponseEntity<List<CustomerOrder>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<CustomerOrder>>(orders, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation/search")
  public ResponseEntity<List<Quotation>> searchQuotations(@RequestBody OrderingSearch orderingSearch) {
    List<Quotation> orders;
    if (orderingSearch == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (orderingSearch.getStartDate() == null || orderingSearch.getEndDate() == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Duration duration = Duration.between(orderingSearch.getStartDate(), orderingSearch.getEndDate());

    if (duration.toDays() > 366)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    try {
      validationHelper.validateReferential(orderingSearch.getSalesEmployee(), false);
      if (orderingSearch.getQuotationStatus() != null)
        for (QuotationStatus status : orderingSearch.getQuotationStatus())
          validationHelper.validateReferential(status, false);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try {
      orders = quotationService.searchQuotations(orderingSearch);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching payment", e);
      return new ResponseEntity<List<Quotation>>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching payment", e);
      return new ResponseEntity<List<Quotation>>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<List<Quotation>>(orders, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order")
  public ResponseEntity<CustomerOrder> addOrUpdateCustomerOrder(@RequestBody CustomerOrder customerOrder) {
    try {
      validateQuotationAndCustomerOrder(customerOrder);
      QuotationStatus quotationStatus = quotationStatusService.getQuotationStatusByCode(QuotationStatus.OPEN);
      if (quotationStatus == null)
        if (customerOrder.getQuotationStatus() == null) {
          logger.error("OPEN Quotation status not found");
          return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

      if (customerOrder.getQuotationStatus() == null)
        customerOrder.setQuotationStatus(quotationStatus);
      customerOrder = customerOrderService.addOrUpdateCustomerOrder(customerOrder);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching CustomerOrder", e);
      return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching CustomerOrder", e);
      return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order/status")
  public ResponseEntity<CustomerOrder> addOrUpdateCustomerOrderStatus(@RequestBody CustomerOrder customerOrder,
      @RequestParam String targetStatusCode) {
    try {
      validateQuotationAndCustomerOrder(customerOrder);
      customerOrder = customerOrderService.addOrUpdateCustomerOrderStatus(customerOrder, targetStatusCode);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching CustomerOrder", e);
      return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching CustomerOrder", e);
      return new ResponseEntity<CustomerOrder>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation/status")
  public ResponseEntity<Quotation> addOrUpdateQuotationStatus(@RequestBody Quotation quotation,
      @RequestParam String targetStatusCode) {
    try {
      validateQuotationAndCustomerOrder(quotation);

      quotation = quotationService.addOrUpdateQuotationStatus(quotation, targetStatusCode);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
  }

  private void validateQuotationAndCustomerOrder(IQuotation quotation) throws Exception {
    boolean isOpen = quotation.getQuotationStatus() != null
        && quotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN);
    boolean isCustomerOrder = quotation instanceof CustomerOrder && !isOpen;
    if (quotation.getSpecialOffers() != null && quotation.getSpecialOffers().size() > 0)
      for (SpecialOffer specialOffer : quotation.getSpecialOffers())
        validationHelper.validateReferential(specialOffer, false);
    validationHelper.validateReferential(quotation.getTiers(), false);
    validationHelper.validateReferential(quotation.getResponsable(), false);
    validationHelper.validateReferential(quotation.getConfrere(), false);
    validationHelper.validateReferential(quotation.getQuotationLabelType(), true);
    validationHelper.validateReferential(quotation.getCustomLabelResponsable(), false);
    validationHelper.validateReferential(quotation.getCustomLabelTiers(), false);
    validationHelper.validateReferential(quotation.getRecordType(), true);

    if (quotation.getResponsable() == null && quotation.getTiers() == null && quotation.getConfrere() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    if (quotation.getProvisions() == null || quotation.getProvisions().size() == 0)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    if (quotation.getDocuments() != null && quotation.getDocuments().size() > 0) {
      for (Document document : quotation.getDocuments()) {

        validationHelper.validateReferential(document.getDocumentType(), true);

        if (document.getMailsAffaire() != null && !validationHelper.validateMailList(document.getMailsAffaire()))
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (document.getMailsClient() != null && document.getMailsClient() != null
            && document.getMailsClient().size() > 0)
          if (!validationHelper.validateMailList(document.getMailsClient()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        validationHelper.validateString(document.getAffaireAddress(), false, 60);
        validationHelper.validateString(document.getClientAddress(), false, 60);
        validationHelper.validateString(document.getAffaireRecipient(), false, 40);
        validationHelper.validateString(document.getClientRecipient(), false, 40);
        validationHelper.validateString(document.getBillingLabel(), false, 40);
        validationHelper.validateString(document.getBillingLabelAddress(), false, 60);
        validationHelper.validateString(document.getBillingLabelPostalCode(), false, 10);
        validationHelper.validateReferential(document.getBillingLabelCity(), false);
        validationHelper.validateReferential(document.getBillingLabelCountry(), false);

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

    int nbrOfAffaire = 0;
    Integer lastAffaireId = null;

    for (Provision provision : quotation.getProvisions()) {
      if (provision.getAffaire() == null && !isOpen)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

      Affaire affaire = provision.getAffaire();

      if (lastAffaireId == null || lastAffaireId.equals(affaire.getId())) {
        nbrOfAffaire++;
        lastAffaireId = affaire.getId();
      }

      validationHelper.validateString(affaire.getAddress(), true, 60);
      validationHelper.validateReferential(affaire.getCity(), true);
      validationHelper.validateReferential(affaire.getCountry(), true);
      validationHelper.validateString(affaire.getExternalReference(), false, 60);
      validationHelper.validateString(affaire.getPaymentIban(), false, 40);
      validationHelper.validateString(affaire.getPaymentBic(), false, 40);
      if (affaire.getCountry() != null && affaire.getCountry().getCode().equals("FR"))
        validationHelper.validateString(affaire.getPostalCode(), true, 10);

      if (affaire.getIsIndividual()) {
        validationHelper.validateReferential(affaire.getCivility(), true);
        validationHelper.validateString(affaire.getFirstname(), true, 20);
        validationHelper.validateString(affaire.getLastname(), true, 20);

      } else {
        validationHelper.validateString(affaire.getDenomination(), true, 60);
        if (affaire.getSiren() == null || affaire.getSiret() == null && affaire.getRna() == null)
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (affaire.getRna() != null
            && !validationHelper.validateRna(affaire.getRna().toUpperCase().replaceAll(" ", "")))
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (affaire.getSiren() != null
            && !validationHelper.validateSiren(affaire.getSiren().toUpperCase().replaceAll(" ", "")))
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (affaire.getSiret() != null
            && !validationHelper.validateSiret(affaire.getSiret().toUpperCase().replaceAll(" ", "")))
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      // Domiciliation
      if (provision.getDomiciliation() != null) {
        Domiciliation domiciliation = provision.getDomiciliation();
        validationHelper.validateReferential(domiciliation.getDomiciliationContractType(), !isOpen);
        validationHelper.validateReferential(domiciliation.getLanguage(), !isOpen);
        validationHelper.validateReferential(domiciliation.getBuildingDomiciliation(), !isOpen);
        validationHelper.validateReferential(domiciliation.getMailRedirectionType(), !isOpen);

        validationHelper.validateString(domiciliation.getAddress(), false, 60);
        validationHelper.validateString(domiciliation.getPostalCode(), false, 10);
        validationHelper.validateString(domiciliation.getMailRecipient(), false, 60);
        validationHelper.validateString(domiciliation.getActivityAddress(), false, 60);
        validationHelper.validateReferential(domiciliation.getCity(), false);
        validationHelper.validateReferential(domiciliation.getCountry(), false);
        validationHelper.validateString(domiciliation.getAccountingRecordDomiciliation(), isCustomerOrder, 60);

        if (domiciliation.isLegalPerson()) {
          if ((domiciliation.getLegalGardianSiren() == null
              || !validationHelper.validateSiren(domiciliation.getLegalGardianSiren())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
          validationHelper.validateString(domiciliation.getLegalGardianDenomination(), isCustomerOrder, 60);
          validationHelper.validateReferential(domiciliation.getLegalGardianLegalForm(), isCustomerOrder);
        } else {
          validationHelper.validateReferential(domiciliation.getLegalGardianCivility(), isCustomerOrder);
          validationHelper.validateString(domiciliation.getLegalGardianFirstname(), isCustomerOrder, 20);
          validationHelper.validateString(domiciliation.getLegalGardianLastname(), isCustomerOrder, 20);
          validationHelper.validateDateMax(domiciliation.getLegalGardianBirthdate(), isCustomerOrder, LocalDate.now());
          validationHelper.validateString(domiciliation.getLegalGardianPlaceOfBirth(), isCustomerOrder, 60);
          validationHelper.validateString(domiciliation.getLegalGardianJob(), isCustomerOrder, 30);
        }

        validationHelper.validateString(domiciliation.getLegalGardianMailRecipient(), isCustomerOrder, 60);
        validationHelper.validateString(domiciliation.getLegalGardianAddress(), isCustomerOrder, 60);
        if (domiciliation.getCountry() != null && domiciliation.getCountry().getCode().equals("FR"))
          validationHelper.validateString(domiciliation.getLegalGardianPostalCode(), isCustomerOrder, 10);
        validationHelper.validateReferential(domiciliation.getLegalGardianCity(), isCustomerOrder);
        validationHelper.validateReferential(domiciliation.getLegalGardianCountry(), isCustomerOrder);

      }
      // Shal
      if (provision.getShal() != null) {
        Shal shal = provision.getShal();
        validationHelper.validateDateMin(shal.getPublicationDate(), !isOpen, LocalDate.now());
        validationHelper.validateReferential(shal.getDepartment(), !isOpen);
        validationHelper.validateReferential(shal.getConfrere(), isCustomerOrder);
        validationHelper.validateReferential(shal.getNoticeTypeFamily(), isCustomerOrder);
        if (isCustomerOrder && (shal.getNoticeTypes() == null || shal.getNoticeTypes().size() == 0))
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (shal.getNoticeTypes() != null)
          for (NoticeType noticeType : shal.getNoticeTypes()) {
            validationHelper.validateReferential(noticeType, isCustomerOrder);
          }
        validationHelper.validateString(shal.getNotice(), !isOpen);
      }

      if (provision.getBodacc() != null) {
        Bodacc bodacc = provision.getBodacc();
        validationHelper.validateReferential(bodacc.getPaymentType(), false);
        validationHelper.validateReferential(bodacc.getBodaccPublicationType(), !isOpen);
        validationHelper.validateReferential(bodacc.getTransfertFundsType(), false);

        if (bodacc.getBodaccSale() != null) {
          BodaccSale bodaccSale = bodacc.getBodaccSale();

          validationHelper.validateString(bodaccSale.getDivestedBusinessAddress(), false, 100);
          validationHelper.validateReferential(bodaccSale.getFundType(), isCustomerOrder);
          validationHelper.validateString(bodaccSale.getOwnerFirstname(), false, 30);
          validationHelper.validateString(bodaccSale.getOwnerLastname(), false, 30);
          validationHelper.validateString(bodaccSale.getOwnerDenomination(), false, 60);
          validationHelper.validateString(bodaccSale.getOwnerSiren(), false, 9);
          validationHelper.validateSiren(bodaccSale.getOwnerSiren());
          validationHelper.validateString(bodaccSale.getOwnerAddress(), isCustomerOrder, 100);
          validationHelper.validateString(bodaccSale.getOwnerAbbreviation(), false, 20);
          validationHelper.validateString(bodaccSale.getOwnerBusinessName(), false, 60);
          validationHelper.validateReferential(bodaccSale.getOwnerLegalForm(), false);

          validationHelper.validateString(bodaccSale.getPurchaserFirstname(), false, 30);
          validationHelper.validateString(bodaccSale.getPurchaserLastname(), false, 30);
          validationHelper.validateString(bodaccSale.getPurchaserDenomination(), false, 60);
          validationHelper.validateString(bodaccSale.getPurchaserSiren(), false, 9);
          validationHelper.validateSiren(bodaccSale.getPurchaserSiren());
          validationHelper.validateString(bodaccSale.getPurchaserBusinessName(), false, 60);
          validationHelper.validateString(bodaccSale.getPurchaserAbbreviation(), false, 20);
          validationHelper.validateReferential(bodaccSale.getPurchaserLegalForm(), false);
          validationHelper.validateDate(bodaccSale.getPurchaserActivityStartDate(), isCustomerOrder);

          validationHelper.validateDate(bodaccSale.getDeedDate(), isCustomerOrder);
          validationHelper.validateDate(bodaccSale.getRegistrationDate(), isCustomerOrder);
          validationHelper.validateReferential(bodaccSale.getRegistrationAuthority(), isCustomerOrder);
          validationHelper.validateString(bodaccSale.getRegistrationReferences(), false, 50);
          validationHelper.validateReferential(bodaccSale.getActType(), isCustomerOrder);
          validationHelper.validateString(bodaccSale.getWritor(), false, 60);
          validationHelper.validateString(bodaccSale.getWritorAddress(), false, 100);
          validationHelper.validateString(bodaccSale.getValidityObjectionAddress(), isCustomerOrder, 100);
          validationHelper.validateString(bodaccSale.getMailObjectionAddress(), false, 100);
          validationHelper.validateDate(bodaccSale.getLeaseResilisationDate(), isCustomerOrder);
          validationHelper.validateString(bodaccSale.getLeaseAddress(), false, 100);

          validationHelper.validateString(bodaccSale.getTenantFirstname(), false, 30);
          validationHelper.validateString(bodaccSale.getTenantLastname(), false, 30);
          validationHelper.validateString(bodaccSale.getTenantAddress(), isCustomerOrder, 100);
          validationHelper.validateString(bodaccSale.getTenantDenomination(), false, 60);
          validationHelper.validateString(bodaccSale.getTenantSiren(), false, 9);
          validationHelper.validateSiren(bodaccSale.getTenantSiren());
          validationHelper.validateString(bodaccSale.getTenantBusinessName(), false, 60);
          validationHelper.validateString(bodaccSale.getTenantAbbreviation(), false, 20);
          validationHelper.validateReferential(bodaccSale.getTenantLegalForm(), false);

        }

        if (bodacc.getBodaccFusion() != null) {
          BodaccFusion bodaccFusion = bodacc.getBodaccFusion();

          if (bodaccFusion.getBodaccFusionAbsorbedCompanies() == null
              || bodaccFusion.getBodaccFusionAbsorbedCompanies().size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

          for (BodaccFusionAbsorbedCompany bodaccFusionAbsorbedCompany : bodaccFusion
              .getBodaccFusionAbsorbedCompanies()) {
            validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanyDenomination(),
                isCustomerOrder, 60);
            validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren(), isCustomerOrder, 9);
            validationHelper.validateRna(bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren());
            validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanyAddress(), isCustomerOrder,
                100);
            validationHelper.validateReferential(bodaccFusionAbsorbedCompany.getAbsorbedCompanyLegalForm(), false);
            validationHelper.validateDate(bodaccFusionAbsorbedCompany.getAbsorbedCompanyRcsDeclarationDate(),
                isCustomerOrder);
            validationHelper
                .validateReferential(bodaccFusionAbsorbedCompany.getAbsorbedCompanyRcsCompetentAuthority(),
                    isCustomerOrder);
          }

          if (bodaccFusion.getBodaccFusionMergingCompanies() == null
              || bodaccFusion.getBodaccFusionMergingCompanies().size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

          for (BodaccFusionMergingCompany bodaccFusionMergingCompany : bodaccFusion
              .getBodaccFusionMergingCompanies()) {
            validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanyDenomination(), isCustomerOrder,
                60);
            validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanySiren(), isCustomerOrder, 9);
            validationHelper.validateRna(bodaccFusionMergingCompany.getMergingCompanySiren());
            validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanyAddress(), isCustomerOrder,
                100);
            validationHelper.validateReferential(bodaccFusionMergingCompany.getMergingCompanyLegalForm(), false);
            validationHelper.validateDate(bodaccFusionMergingCompany.getMergingCompanyRcsDeclarationDate(),
                isCustomerOrder);
            validationHelper.validateReferential(bodaccFusionMergingCompany.getMergingCompanyRcsCompetentAuthority(),
                isCustomerOrder);
          }
        }

        if (bodacc.getBodaccSplit() != null) {
          BodaccSplit bodaccSplit = bodacc.getBodaccSplit();

          if (bodaccSplit.getBodaccSplitBeneficiaries() == null
              || bodaccSplit.getBodaccSplitBeneficiaries().size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

          for (BodaccSplitBeneficiary bodaccSplitBeneficiary : bodaccSplit.getBodaccSplitBeneficiaries()) {
            validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanyDenomination(), isCustomerOrder,
                60);
            validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanySiren(), isCustomerOrder, 9);
            validationHelper.validateRna(bodaccSplitBeneficiary.getBeneficiaryCompanySiren());
            validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanyAddress(), isCustomerOrder,
                100);
            validationHelper.validateReferential(bodaccSplitBeneficiary.getBeneficiaryCompanyLegalForm(), false);
            validationHelper.validateDate(bodaccSplitBeneficiary.getBeneficiaryCompanyRcsDeclarationDate(),
                isCustomerOrder);
            validationHelper.validateReferential(bodaccSplitBeneficiary.getBeneficiaryCompanyRcsCompetentAuthority(),
                true);
          }

          if (bodaccSplit.getBodaccSplitCompanies() == null || bodaccSplit.getBodaccSplitCompanies().size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
          for (BodaccSplitCompany bodaccSplitCompany : bodaccSplit.getBodaccSplitCompanies()) {
            validationHelper.validateString(bodaccSplitCompany.getSplitCompanyDenomination(), isCustomerOrder, 60);
            validationHelper.validateString(bodaccSplitCompany.getSplitCompanySiren(), isCustomerOrder, 9);
            validationHelper.validateRna(bodaccSplitCompany.getSplitCompanySiren());
            validationHelper.validateString(bodaccSplitCompany.getSplitCompanyAddress(), isCustomerOrder, 100);
            validationHelper.validateReferential(bodaccSplitCompany.getSplitCompanyLegalForm(), false);
            validationHelper.validateDate(bodaccSplitCompany.getSplitCompanyRcsDeclarationDate(), isCustomerOrder);
            validationHelper.validateReferential(bodaccSplitCompany.getSplitCompanyRcsCompetentAuthority(),
                isCustomerOrder);
          }
        }

        validationHelper.validateDateMin(bodacc.getDateOfPublication(), false, LocalDate.now());
      }
    }

    if (nbrOfAffaire > 1 && quotation.getQuotationLabelType().getCode().equals(billingLabelAffaireCode))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
  }

  @PostMapping(inputEntryPoint + "/affaire")
  public ResponseEntity<Affaire> addOrUpdateAffaire(@RequestBody Affaire affaire) {
    try {
      validationHelper.validateString(affaire.getAddress(), true, 60);
      validationHelper.validateReferential(affaire.getCity(), true);
      validationHelper.validateReferential(affaire.getCountry(), true);
      validationHelper.validateString(affaire.getExternalReference(), false, 60);
      if (affaire.getCountry() != null && affaire.getCountry().getCode().equals("FR"))
        validationHelper.validateString(affaire.getPostalCode(), true, 10);

      if (affaire.getIsIndividual()) {
        validationHelper.validateReferential(affaire.getCivility(), true);
        validationHelper.validateString(affaire.getFirstname(), true, 20);
        validationHelper.validateString(affaire.getLastname(), true, 20);

      } else {
        validationHelper.validateReferential(affaire.getLegalForm(), true);
        validationHelper.validateString(affaire.getDenomination(), true, 60);
        if (affaire.getSiren() == null || affaire.getSiret() == null && affaire.getRna() == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getRna() != null
            && !validationHelper.validateRna(affaire.getRna().toUpperCase().replaceAll(" ", "")))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getSiren() != null
            && !validationHelper.validateSiren(affaire.getSiren().toUpperCase().replaceAll(" ", "")))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getSiret() != null
            && !validationHelper.validateSiret(affaire.getSiret().toUpperCase().replaceAll(" ", "")))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      affaire = affaireService.addOrUpdateAffaire(affaire);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<Affaire>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<Affaire>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Affaire>(affaire, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/invoice-item/generate")
  public ResponseEntity<IQuotation> generateInvoiceItemForQuotation(@RequestBody Quotation quotation) {
    IQuotation outQuotation = quotation;
    try {
      outQuotation = quotationService.getAndSetInvoiceItemsForQuotation(quotation);
    } catch (

    ResponseStatusException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching quotation", e);
      return new ResponseEntity<IQuotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching quotation", e);
      return new ResponseEntity<IQuotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<IQuotation>(outQuotation, HttpStatus.OK);
  }

}