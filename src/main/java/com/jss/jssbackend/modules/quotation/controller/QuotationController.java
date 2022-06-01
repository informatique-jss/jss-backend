package com.jss.jssbackend.modules.quotation.controller;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.service.SpecialOfferService;
import com.jss.jssbackend.modules.quotation.model.Quotation;
import com.jss.jssbackend.modules.quotation.model.QuotationLabelType;
import com.jss.jssbackend.modules.quotation.model.QuotationStatus;
import com.jss.jssbackend.modules.quotation.model.RecordType;
import com.jss.jssbackend.modules.quotation.model.Rna;
import com.jss.jssbackend.modules.quotation.model.Siren;
import com.jss.jssbackend.modules.quotation.model.Siret;
import com.jss.jssbackend.modules.quotation.service.QuotationLabelTypeService;
import com.jss.jssbackend.modules.quotation.service.QuotationService;
import com.jss.jssbackend.modules.quotation.service.QuotationStatusService;
import com.jss.jssbackend.modules.quotation.service.RecordTypeService;
import com.jss.jssbackend.modules.quotation.service.RnaDelegateService;
import com.jss.jssbackend.modules.quotation.service.SireneDelegateService;
import com.jss.jssbackend.modules.tiers.service.ResponsableService;
import com.jss.jssbackend.modules.tiers.service.TiersService;

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
  public ResponseEntity<Quotation> getTiersById(@RequestParam Integer id) {
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

    try {
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