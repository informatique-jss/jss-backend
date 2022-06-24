package com.jss.osiris.modules.quotation.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.jss.osiris.modules.miscellaneous.model.Department;
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
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.FundType;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.RecordType;
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
      validationHelper.validateString(actType.getCode(), true);
      validationHelper.validateString(actType.getLabel(), true);

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

  @GetMapping(inputEntryPoint + "/character-price")
  public ResponseEntity<CharacterPrice> getCharacterPrice(@RequestParam Integer departmentId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date date) {
    CharacterPrice characterPrice = null;
    if (departmentId == null || date == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Department department = departmentService.getDepartment(departmentId);

    if (department == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    try {
      characterPrice = characterPriceService.getCharacterPrice(department, date);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching journalType", e);
      return new ResponseEntity<CharacterPrice>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching journalType", e);
      return new ResponseEntity<CharacterPrice>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<CharacterPrice>(characterPrice, HttpStatus.OK);
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

  @GetMapping(inputEntryPoint + "/quotation-label-ypes")
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

  @PostMapping(inputEntryPoint + "/quotation")
  public ResponseEntity<Quotation> addOrUpdateQuotation(@RequestBody Quotation quotation) {
    try {
      validationHelper.validateReferential(quotation.getSpecialOffer(), false);
      validationHelper.validateReferential(quotation.getTiers(), false);
      validationHelper.validateReferential(quotation.getResponsable(), false);
      validationHelper.validateReferential(quotation.getQuotationLabelType(), true);
      validationHelper.validateString(quotation.getQuotationLabel(), false, 40);
      validationHelper.validateReferential(quotation.getRecordType(), true);

      if (quotation.getResponsable() == null && quotation.getTiers() == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      if (quotation.getQuotationLabel() == null && quotation.getQuotationLabelType() == null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      if (quotation.getProvisions() == null || quotation.getProvisions().size() == 0)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      for (Provision provision : quotation.getProvisions()) {
        if (provision.getAffaire() == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Affaire affaire = provision.getAffaire();

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

        // Domiciliation
        if (provision.getDomiciliation() != null) {
          Domiciliation domiciliation = provision.getDomiciliation();
          validationHelper.validateReferential(domiciliation.getDomiciliationContractType(), true);
          validationHelper.validateReferential(domiciliation.getLanguage(), true);
          validationHelper.validateReferential(domiciliation.getBuildingDomiciliation(), true);
          validationHelper.validateReferential(domiciliation.getMailRedirectionType(), true);
          validationHelper.validateString(domiciliation.getAddress(), false, 60);
          validationHelper.validateString(domiciliation.getPostalCode(), false, 10);
          validationHelper.validateString(domiciliation.getMailRecipient(), false, 60);
          validationHelper.validateString(domiciliation.getActivityAddress(), false, 60);
          validationHelper.validateReferential(domiciliation.getCity(), false);
          validationHelper.validateReferential(domiciliation.getCountry(), false);
          validationHelper.validateString(domiciliation.getAccountingRecordDomiciliation(), true, 60);

          if (domiciliation.isLegalPerson()) {
            if ((domiciliation.getLegalGardianSiren() == null
                || !validationHelper.validateSiren(domiciliation.getLegalGardianSiren())))
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            validationHelper.validateString(domiciliation.getLegalGardianDenomination(), true, 60);
            validationHelper.validateReferential(domiciliation.getLegalGardianLegalForm(), true);
          } else {
            validationHelper.validateReferential(domiciliation.getLegalGardianCivility(), true);
            validationHelper.validateString(domiciliation.getLegalGardianFirstname(), true, 20);
            validationHelper.validateString(domiciliation.getLegalGardianLastname(), true, 20);
            validationHelper.validateDateMax(domiciliation.getLegalGardianBirthdate(), true, new Date());
            validationHelper.validateString(domiciliation.getLegalGardianPlaceOfBirth(), true, 60);
            validationHelper.validateString(domiciliation.getLegalGardianJob(), true, 30);
          }

          validationHelper.validateString(domiciliation.getLegalGardianMailRecipient(), true, 60);
          validationHelper.validateString(domiciliation.getLegalGardianAddress(), true, 60);
          if (domiciliation.getCountry() != null && domiciliation.getCountry().getCode().equals("FR"))
            validationHelper.validateString(domiciliation.getLegalGardianPostalCode(), true, 10);
          validationHelper.validateReferential(domiciliation.getLegalGardianCity(), true);
          validationHelper.validateReferential(domiciliation.getLegalGardianCountry(), true);

        }
        // Shal
        if (provision.getShal() != null) {
          Shal shal = provision.getShal();
          validationHelper.validateDateMin(shal.getPublicationDate(), true, new Date());
          validationHelper.validateReferential(shal.getDepartment(), true);
          validationHelper.validateReferential(shal.getConfrere(), true);
          validationHelper.validateReferential(shal.getJournalType(), true);
          validationHelper.validateReferential(shal.getNoticeTypeFamily(), true);
          if (shal.getNoticeTypes() == null || shal.getNoticeTypes().size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
          for (NoticeType noticeType : shal.getNoticeTypes()) {
            validationHelper.validateReferential(noticeType, true);
          }
          validationHelper.validateString(shal.getNotice(), true);
        }

        if (provision.getBodacc() != null) {
          Bodacc bodacc = provision.getBodacc();
          validationHelper.validateReferential(bodacc.getPaymentType(), false);
          validationHelper.validateReferential(bodacc.getBodaccPublicationType(), true);
          validationHelper.validateReferential(bodacc.getTransfertFundsType(), false);

          if (bodacc.getBodaccSale() != null) {
            BodaccSale bodaccSale = bodacc.getBodaccSale();

            validationHelper.validateString(bodaccSale.getDivestedBusinessAddress(), false, 100);
            validationHelper.validateReferential(bodaccSale.getFundType(), true);
            validationHelper.validateString(bodaccSale.getOwnerFirstname(), false, 30);
            validationHelper.validateString(bodaccSale.getOwnerLastname(), false, 30);
            validationHelper.validateString(bodaccSale.getOwnerDenomination(), false, 60);
            validationHelper.validateString(bodaccSale.getOwnerSiren(), false, 9);
            validationHelper.validateSiren(bodaccSale.getOwnerSiren());
            validationHelper.validateString(bodaccSale.getOwnerAddress(), true, 100);
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
            validationHelper.validateDate(bodaccSale.getPurchaserActivityStartDate(), true);

            validationHelper.validateDate(bodaccSale.getDeedDate(), true);
            validationHelper.validateDate(bodaccSale.getRegistrationDate(), true);
            validationHelper.validateReferential(bodaccSale.getRegistrationAuthority(), true);
            validationHelper.validateString(bodaccSale.getRegistrationReferences(), false, 50);
            validationHelper.validateReferential(bodaccSale.getActType(), true);
            validationHelper.validateString(bodaccSale.getWritor(), false, 60);
            validationHelper.validateString(bodaccSale.getWritorAddress(), false, 100);
            validationHelper.validateString(bodaccSale.getValidityObjectionAddress(), true, 100);
            validationHelper.validateString(bodaccSale.getMailObjectionAddress(), false, 100);
            validationHelper.validateDate(bodaccSale.getLeaseResilisationDate(), true);
            validationHelper.validateString(bodaccSale.getLeaseAddress(), false, 100);

            validationHelper.validateString(bodaccSale.getTenantFirstname(), false, 30);
            validationHelper.validateString(bodaccSale.getTenantLastname(), false, 30);
            validationHelper.validateString(bodaccSale.getTenantAddress(), true, 100);
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
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            for (BodaccFusionAbsorbedCompany bodaccFusionAbsorbedCompany : bodaccFusion
                .getBodaccFusionAbsorbedCompanies()) {
              validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanyDenomination(), true, 60);
              validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren(), true, 9);
              validationHelper.validateRna(bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren());
              validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanyAddress(), true, 100);
              validationHelper.validateReferential(bodaccFusionAbsorbedCompany.getAbsorbedCompanyLegalForm(), false);
              validationHelper.validateDate(bodaccFusionAbsorbedCompany.getAbsorbedCompanyRcsDeclarationDate(), true);
              validationHelper
                  .validateReferential(bodaccFusionAbsorbedCompany.getAbsorbedCompanyRcsCompetentAuthority(), true);
            }

            if (bodaccFusion.getBodaccFusionMergingCompanies() == null
                || bodaccFusion.getBodaccFusionMergingCompanies().size() == 0)
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            for (BodaccFusionMergingCompany bodaccFusionMergingCompany : bodaccFusion
                .getBodaccFusionMergingCompanies()) {
              validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanyDenomination(), true, 60);
              validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanySiren(), true, 9);
              validationHelper.validateRna(bodaccFusionMergingCompany.getMergingCompanySiren());
              validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanyAddress(), true, 100);
              validationHelper.validateReferential(bodaccFusionMergingCompany.getMergingCompanyLegalForm(), false);
              validationHelper.validateDate(bodaccFusionMergingCompany.getMergingCompanyRcsDeclarationDate(), true);
              validationHelper.validateReferential(bodaccFusionMergingCompany.getMergingCompanyRcsCompetentAuthority(),
                  true);
            }
          }

          if (bodacc.getBodaccSplit() != null) {
            BodaccSplit bodaccSplit = bodacc.getBodaccSplit();

            if (bodaccSplit.getBodaccSplitBeneficiaries() == null
                || bodaccSplit.getBodaccSplitBeneficiaries().size() == 0)
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            for (BodaccSplitBeneficiary bodaccSplitBeneficiary : bodaccSplit.getBodaccSplitBeneficiaries()) {
              validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanyDenomination(), true, 60);
              validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanySiren(), true, 9);
              validationHelper.validateRna(bodaccSplitBeneficiary.getBeneficiaryCompanySiren());
              validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanyAddress(), true, 100);
              validationHelper.validateReferential(bodaccSplitBeneficiary.getBeneficiaryCompanyLegalForm(), false);
              validationHelper.validateDate(bodaccSplitBeneficiary.getBeneficiaryCompanyRcsDeclarationDate(), true);
              validationHelper.validateReferential(bodaccSplitBeneficiary.getBeneficiaryCompanyRcsCompetentAuthority(),
                  true);
            }

            if (bodaccSplit.getBodaccSplitCompanies() == null || bodaccSplit.getBodaccSplitCompanies().size() == 0)
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            for (BodaccSplitCompany bodaccSplitCompany : bodaccSplit.getBodaccSplitCompanies()) {
              validationHelper.validateString(bodaccSplitCompany.getSplitCompanyDenomination(), true, 60);
              validationHelper.validateString(bodaccSplitCompany.getSplitCompanySiren(), true, 9);
              validationHelper.validateRna(bodaccSplitCompany.getSplitCompanySiren());
              validationHelper.validateString(bodaccSplitCompany.getSplitCompanyAddress(), true, 100);
              validationHelper.validateReferential(bodaccSplitCompany.getSplitCompanyLegalForm(), false);
              validationHelper.validateDate(bodaccSplitCompany.getSplitCompanyRcsDeclarationDate(), true);
              validationHelper.validateReferential(bodaccSplitCompany.getSplitCompanyRcsCompetentAuthority(), true);
            }
          }

          validationHelper.validateDateMin(bodacc.getDateOfPublication(), false, new Date());
        }
      }

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

}