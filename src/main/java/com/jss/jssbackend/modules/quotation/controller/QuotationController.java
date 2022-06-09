package com.jss.jssbackend.modules.quotation.controller;

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

import com.jss.jssbackend.libs.ValidationHelper;
import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.miscellaneous.service.CityService;
import com.jss.jssbackend.modules.miscellaneous.service.CivilityService;
import com.jss.jssbackend.modules.miscellaneous.service.CountryService;
import com.jss.jssbackend.modules.miscellaneous.service.DepartmentService;
import com.jss.jssbackend.modules.miscellaneous.service.LanguageService;
import com.jss.jssbackend.modules.miscellaneous.service.LegalFormService;
import com.jss.jssbackend.modules.miscellaneous.service.SpecialOfferService;
import com.jss.jssbackend.modules.quotation.model.Affaire;
import com.jss.jssbackend.modules.quotation.model.BuildingDomiciliation;
import com.jss.jssbackend.modules.quotation.model.CharacterPrice;
import com.jss.jssbackend.modules.quotation.model.Confrere;
import com.jss.jssbackend.modules.quotation.model.Domiciliation;
import com.jss.jssbackend.modules.quotation.model.DomiciliationContractType;
import com.jss.jssbackend.modules.quotation.model.JournalType;
import com.jss.jssbackend.modules.quotation.model.MailRedirectionType;
import com.jss.jssbackend.modules.quotation.model.NoticeType;
import com.jss.jssbackend.modules.quotation.model.NoticeTypeFamily;
import com.jss.jssbackend.modules.quotation.model.Provision;
import com.jss.jssbackend.modules.quotation.model.ProvisionFamilyType;
import com.jss.jssbackend.modules.quotation.model.ProvisionType;
import com.jss.jssbackend.modules.quotation.model.Quotation;
import com.jss.jssbackend.modules.quotation.model.QuotationLabelType;
import com.jss.jssbackend.modules.quotation.model.QuotationStatus;
import com.jss.jssbackend.modules.quotation.model.RecordType;
import com.jss.jssbackend.modules.quotation.model.Rna;
import com.jss.jssbackend.modules.quotation.model.Siren;
import com.jss.jssbackend.modules.quotation.model.Siret;
import com.jss.jssbackend.modules.quotation.service.AffaireService;
import com.jss.jssbackend.modules.quotation.service.BuildingDomiciliationService;
import com.jss.jssbackend.modules.quotation.service.CharacterPriceService;
import com.jss.jssbackend.modules.quotation.service.ConfrereService;
import com.jss.jssbackend.modules.quotation.service.DomiciliationContractTypeService;
import com.jss.jssbackend.modules.quotation.service.JournalTypeService;
import com.jss.jssbackend.modules.quotation.service.MailRedirectionTypeService;
import com.jss.jssbackend.modules.quotation.service.NoticeTypeFamilyService;
import com.jss.jssbackend.modules.quotation.service.NoticeTypeService;
import com.jss.jssbackend.modules.quotation.service.ProvisionFamilyTypeService;
import com.jss.jssbackend.modules.quotation.service.ProvisionTypeService;
import com.jss.jssbackend.modules.quotation.service.QuotationLabelTypeService;
import com.jss.jssbackend.modules.quotation.service.QuotationService;
import com.jss.jssbackend.modules.quotation.service.QuotationStatusService;
import com.jss.jssbackend.modules.quotation.service.RecordTypeService;
import com.jss.jssbackend.modules.quotation.service.RnaDelegateService;
import com.jss.jssbackend.modules.quotation.service.SireneDelegateService;
import com.jss.jssbackend.modules.tiers.service.ResponsableService;
import com.jss.jssbackend.modules.tiers.service.TiersService;

@RestController
public class QuotationController {

  private static final String inputEntryPoint = "/quotation";

  private static final Logger logger = LoggerFactory.getLogger(QuotationController.class);

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
  public ResponseEntity<Siren> getSiren(@RequestParam String siren) {
    if (siren == null || siren.equals("") || siren.replaceAll(" ", "").length() != 9)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Siren sirenFound = null;
    try {
      sirenFound = sireneDelegateService.getSiren(siren.replaceAll(" ", ""));
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siren", e);
      return new ResponseEntity<Siren>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Siren>(sirenFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/siret")
  public ResponseEntity<Siret> getSiret(@RequestParam String siret) {
    if (siret == null || siret.equals("") || siret.replaceAll(" ", "").length() != 14)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Siret siretFound = null;
    try {
      siretFound = sireneDelegateService.getSiret(siret.replaceAll(" ", ""));
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siret", e);
      return new ResponseEntity<Siret>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Siret>(siretFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/rna")
  public ResponseEntity<Rna> getRna(@RequestParam String rna) {
    if (rna == null || rna.equals("")
        || rna.replaceAll(" ", "").length() != 10 && !rna.toUpperCase().subSequence(0, 1).equals("W"))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Rna rnaFound = null;
    try {
      rnaFound = rnaDelegateService.getRna(rna.replaceAll(" ", ""));
    } catch (HttpStatusCodeException e) {
      return null;
    } catch (Exception e) {
      logger.error("Error when fetching siret", e);
      return new ResponseEntity<Rna>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Rna>(rnaFound, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/affaire")
  public ResponseEntity<Affaire> getRna(@RequestParam Integer id) {
    Affaire affaireFound = null;
    if (id == null || id.equals(""))
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

    if (quotation.getSpecialOffer() != null && (quotation.getSpecialOffer().getId() == null
        || quotationService.getQuotation(quotation.getSpecialOffer().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getTiers() != null
        && (quotation.getTiers().getId() == null || tiersService.getTiersById(quotation.getTiers().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getResponsable() != null && (quotation.getResponsable().getId() == null
        || responsableService.getResponsable(quotation.getResponsable().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getResponsable() == null && quotation.getTiers() == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getQuotationLabelType() != null && (quotation.getQuotationLabelType().getId() == null
        || quotationLabelTypeService.getQuotationLabelType(quotation.getQuotationLabelType().getId()) == null))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getQuotationLabel() != null && quotation.getQuotationLabel().length() > 40)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getQuotationLabel() == null && quotation.getQuotationLabelType() == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getRecordType() == null || quotation.getQuotationLabelType().getId() == null
        || quotationLabelTypeService.getQuotationLabelType(quotation.getQuotationLabelType().getId()) == null)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    if (quotation.getProvisions() != null && quotation.getProvisions().size() > 0) {
      for (Provision provision : quotation.getProvisions()) {
        if (provision.getAffaire() == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Affaire affaire = provision.getAffaire();
        if (affaire.getAddress() == null || affaire.getAddress().length() > 60)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO : réaliser la création de la city si absente du référentiel
        if (affaire.getCity().getId() != null && cityService.getCity(affaire.getCity().getId()) == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getIsIndividual() && (affaire.getCivility() == null || affaire.getCivility().getId() == null
            || civilityService.getCivility(affaire.getCity().getId()) == null))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getCountry() == null || affaire.getCountry().getId() == null
            || countryService.getCountry(affaire.getCountry().getId()) == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getIsIndividual() == false && affaire.getDenomination() == null
            || affaire.getDenomination().length() > 60)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getExternalReference() != null && affaire.getExternalReference().length() > 60)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getIsIndividual() && (affaire.getFirstname() == null || affaire.getFirstname().length() > 20))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getIsIndividual() && (affaire.getLastname() == null || affaire.getLastname().length() > 20))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getLegalForm() == null || affaire.getLegalForm().getId() == null
            || legalFormService.getLegalForm(affaire.getLegalForm().getId()) == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getCountry() != null && affaire.getCountry().getCode().equals("FR")
            && (affaire.getPostalCode() == null || affaire.getPostalCode().equals("")
                || cityService.getCitiesByPostalCode(affaire.getPostalCode()).size() == 0))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getSiren() == null || affaire.getSiret() == null && affaire.getRna() == null)
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getRna() != null
            && !ValidationHelper.validateRna(affaire.getRna().toUpperCase().replaceAll(" ", "")))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getSiren() != null
            && !ValidationHelper.validateSiren(affaire.getSiren().toUpperCase().replaceAll(" ", "")))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (affaire.getSiret() != null
            && !ValidationHelper.validateSiret(affaire.getSiret().toUpperCase().replaceAll(" ", "")))
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Domiciliation
        if (provision.getDomiciliation() != null) {
          Domiciliation domiciliation = provision.getDomiciliation();
          if (domiciliation.getDomiciliationContractType() == null
              || domiciliation.getDomiciliationContractType().getId() == null || domiciliationContractTypeService
                  .getDomiciliationContractType(domiciliation.getDomiciliationContractType().getId()) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }

          if (domiciliation.getLanguage() == null || domiciliation.getLanguage().getId() == null
              || languageService.getLanguageById(domiciliation.getLanguage().getId()) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }

          if (domiciliation.getBuildingDomiciliation() == null
              || domiciliation.getBuildingDomiciliation().getId() == null || buildingDomiciliationService
                  .getBuildingDomiciliation(domiciliation.getBuildingDomiciliation().getId()) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }

          if (domiciliation.getMailRedirectionType() == null || domiciliation.getMailRedirectionType().getId() == null
              || mailRedirectionTypeService
                  .getMailRedirectionType(domiciliation.getMailRedirectionType().getId()) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }

          if (domiciliation.getAddress() != null && domiciliation.getAddress().length() > 60)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getPostalCode() != null && domiciliation.getPostalCode().length() > 10)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getMailRecipient() != null && domiciliation.getMailRecipient().length() > 60)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getActivityAddress() == null || domiciliation.getActivityAddress().length() > 60)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getCity() != null && (domiciliation.getCity().getId() == null
              || cityService.getCity(domiciliation.getCity().getId()) == null))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getCountry() != null && (domiciliation.getCountry().getId() == null
              || countryService.getCountry(domiciliation.getCountry().getId()) == null))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getAccountingRecordDomiciliation() == null
              || domiciliation.getAccountingRecordDomiciliation().length() > 60)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() == false &&
              (domiciliation.getLegalGardianCivility() == null
                  || domiciliation.getLegalGardianCivility().getId() == null
                  ||
                  civilityService.getCivility(domiciliation.getLegalGardianCivility().getId()) == null))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() == false &&
              (domiciliation.getLegalGardianFirstname() == null
                  || domiciliation.getLegalGardianFirstname().length() > 20))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() == false &&
              (domiciliation.getLegalGardianLastname() == null
                  || domiciliation.getLegalGardianLastname().length() > 20))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() == false &&
              (domiciliation.getLegalGardianBirthdate() == null
                  || domiciliation.getLegalGardianBirthdate().after(new Date())))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() == false &&
              (domiciliation.getLegalGardianPlaceOfBirth() == null
                  || domiciliation.getLegalGardianPlaceOfBirth().length() > 60))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() == false
              && (domiciliation.getLegalGardianJob() == null ||
                  domiciliation.getLegalGardianJob().length() > 30))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() && (domiciliation.getLegalGardianSiren() == null
              || !ValidationHelper.validateSiren(domiciliation.getLegalGardianSiren())))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() &&
              (domiciliation.getLegalGardianDenomination() == null
                  || domiciliation.getLegalGardianDenomination().length() > 60))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.isLegalPerson() &&
              (domiciliation.getLegalGardianLegalForm() == null
                  || domiciliation.getLegalGardianLegalForm().getId() == null
                  ||
                  legalFormService.getLegalForm(domiciliation.getLegalGardianLegalForm().getId()) == null))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getLegalGardianMailRecipient() == null
              || domiciliation.getLegalGardianMailRecipient().length() > 60)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getLegalGardianAddress() == null || domiciliation.getLegalGardianAddress().length() > 60)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getLegalGardianPostalCode() != null
              && domiciliation.getLegalGardianPostalCode().length() > 10)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getLegalGardianCity() != null && (domiciliation.getLegalGardianCity().getId() == null
              || cityService.getCity(domiciliation.getLegalGardianCity().getId()) == null))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

          if (domiciliation.getLegalGardianCountry() != null && (domiciliation.getLegalGardianCountry().getId() == null
              || countryService.getCountry(domiciliation.getLegalGardianCountry().getId()) == null))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
      }
    }

    try

    {
      quotation = quotationService.addOrUpdateQuotation(quotation);
    } catch (HttpStatusCodeException e) {
      logger.error("HTTP error when fetching client types", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error when fetching client types", e);
      return new ResponseEntity<Quotation>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
  }

}