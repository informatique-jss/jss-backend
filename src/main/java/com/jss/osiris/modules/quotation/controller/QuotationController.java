package com.jss.osiris.modules.quotation.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.MailComputeHelper;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.mail.model.MailComputeResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.CivilityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.CountryService;
import com.jss.osiris.modules.miscellaneous.service.DepartmentService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.miscellaneous.service.LanguageService;
import com.jss.osiris.modules.miscellaneous.service.LegalFormService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AffaireSearch;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementListSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementNoticeTemplate;
import com.jss.osiris.modules.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.quotation.model.AttachmentMailRequest;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.BuildingDomiciliation;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.DebourDel;
import com.jss.osiris.modules.quotation.model.DirectDebitTransfert;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.DomiciliationFee;
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.FundType;
import com.jss.osiris.modules.quotation.model.IPaperSetResult;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.PaperSet;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationAbandonReason;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.RecordType;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.ServiceFamily;
import com.jss.osiris.modules.quotation.model.ServiceFamilyGroup;
import com.jss.osiris.modules.quotation.model.ServiceType;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.ValidationRequest;
import com.jss.osiris.modules.quotation.service.ActTypeService;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.AnnouncementNoticeTemplateService;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.AssoServiceDocumentService;
import com.jss.osiris.modules.quotation.service.BankTransfertService;
import com.jss.osiris.modules.quotation.service.BuildingDomiciliationService;
import com.jss.osiris.modules.quotation.service.CharacterPriceService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DebourDelService;
import com.jss.osiris.modules.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.quotation.service.DomiciliationContractTypeService;
import com.jss.osiris.modules.quotation.service.DomiciliationFeeService;
import com.jss.osiris.modules.quotation.service.DomiciliationService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.FundTypeService;
import com.jss.osiris.modules.quotation.service.JournalTypeService;
import com.jss.osiris.modules.quotation.service.MailRedirectionTypeService;
import com.jss.osiris.modules.quotation.service.MissingAttachmentQueryService;
import com.jss.osiris.modules.quotation.service.NoticeTypeFamilyService;
import com.jss.osiris.modules.quotation.service.NoticeTypeService;
import com.jss.osiris.modules.quotation.service.PaperSetService;
import com.jss.osiris.modules.quotation.service.PricingHelper;
import com.jss.osiris.modules.quotation.service.ProvisionFamilyTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionScreenTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.ProvisionTypeService;
import com.jss.osiris.modules.quotation.service.QuotationAbandonReasonService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.quotation.service.RecordTypeService;
import com.jss.osiris.modules.quotation.service.RnaDelegateService;
import com.jss.osiris.modules.quotation.service.ServiceFamilyGroupService;
import com.jss.osiris.modules.quotation.service.ServiceFamilyService;
import com.jss.osiris.modules.quotation.service.ServiceService;
import com.jss.osiris.modules.quotation.service.ServiceTypeService;
import com.jss.osiris.modules.quotation.service.SimpleProvisionStatusService;
import com.jss.osiris.modules.quotation.service.TransfertFundsTypeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.FormaliteGuichetUniqueService;
import com.jss.osiris.modules.quotation.service.guichetUnique.GuichetUniqueDelegateService;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@RestController
public class QuotationController {

  private static final String inputEntryPoint = "/quotation";

  @Autowired
  ValidationHelper validationHelper;

  @Autowired
  QuotationService quotationService;

  @Autowired
  QuotationStatusService quotationStatusService;

  @Autowired
  CustomerOrderStatusService customerOrderStatusService;

  @Autowired
  TiersService tiersService;

  @Autowired
  ResponsableService responsableService;

  @Autowired
  SpecialOfferService specialOfferService;

  @Autowired
  RecordTypeService recordTypeService;

  @Autowired
  RnaDelegateService rnaDelegateService;

  @Autowired
  CityService cityService;

  @Autowired
  GeneratePdfDelegate generatePdfDelegate;

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
  TransfertFundsTypeService transfertFundsTypeService;

  @Autowired
  FundTypeService fundTypeService;

  @Autowired
  ActTypeService actTypeService;

  @Autowired
  AnnouncementNoticeTemplateService announcementNoticeTemplateService;

  @Autowired
  CustomerOrderService customerOrderService;

  @Autowired
  ConstantService constantService;

  @Autowired
  AssoAffaireOrderService assoAffaireOrderService;

  @Autowired
  ProvisionScreenTypeService provisionScreenTypeService;

  @Autowired
  AssignationTypeService assignationTypeService;

  @Autowired
  EmployeeService employeeService;

  @Autowired
  ProvisionService provisionService;

  @Autowired
  DocumentService documentService;

  @Autowired
  MailHelper mailHelper;

  @Autowired
  QuotationAbandonReasonService quotationAbandonReasonService;

  @Autowired
  AnnouncementStatusService announcementStatusService;

  @Autowired
  DomiciliationStatusService domiciliationStatusService;

  @Autowired
  DomiciliationService domiciliationService;

  @Autowired
  FormaliteStatusService formaliteStatusService;

  @Autowired
  GlobalExceptionHandler globalExceptionHandler;

  @Autowired
  AnnouncementService announcementService;

  @Autowired
  SimpleProvisionStatusService simpleProvisonStatusService;

  @Autowired
  MailComputeHelper mailComputeHelper;

  @Autowired
  PricingHelper pricingHelper;

  @Autowired
  BankTransfertService bankTransfertService;

  @Autowired
  PaymentService paymentService;

  @Autowired
  InvoiceService invoiceService;

  @Autowired
  DirectDebitTransfertService directDebitTransfertService;

  @Autowired
  QuotationValidationHelper quotationValidationHelper;

  @Autowired
  GuichetUniqueDelegateService guichetUniqueDelegateService;

  @Autowired
  FormaliteGuichetUniqueService formaliteGuichetUniqueService;

  @Autowired
  DebourDelService debourDelService;

  @Autowired
  ServiceTypeService serviceTypeService;

  @Autowired
  ServiceFamilyService serviceFamilyService;

  @Autowired
  ServiceFamilyGroupService serviceFamilyGroupService;

  @Autowired
  ServiceService serviceService;

  @Autowired
  MissingAttachmentQueryService missingAttachmentQueryService;

  @Autowired
  AssoServiceDocumentService assoServiceDocumentService;

  @Autowired
  DomiciliationFeeService domiciliationFeeService;

  @Autowired
  CustomerOrderCommentService customerOrderCommentService;

  @Autowired
  ActiveDirectoryHelper activeDirectoryHelper;

  @Autowired
  PaperSetService paperSetService;

  @GetMapping(inputEntryPoint + "/paper-sets/search")
  public ResponseEntity<List<IPaperSetResult>> searchPaperSets() throws OsirisValidationException, OsirisException {
    return new ResponseEntity<List<IPaperSetResult>>(paperSetService.searchPaperSets(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/paper-set/cancel")
  public ResponseEntity<PaperSet> cancelPaperSet(
      @RequestParam Integer paperSetId) throws OsirisValidationException, OsirisException {
    PaperSet paperSet = paperSetService.getPaperSet(paperSetId);

    if (paperSet == null)
      throw new OsirisValidationException("paperSet");
    return new ResponseEntity<PaperSet>(paperSetService.cancelPaperSet(paperSet), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/paper-set/validate")
  public ResponseEntity<PaperSet> validatePaperSet(
      @RequestParam Integer paperSetId) throws OsirisValidationException, OsirisException {
    PaperSet paperSet = paperSetService.getPaperSet(paperSetId);

    if (paperSet == null)
      throw new OsirisValidationException("paperSet");
    return new ResponseEntity<PaperSet>(paperSetService.validatePaperSet(paperSet), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/paper-set")
  public ResponseEntity<PaperSet> addOrUpdatePaperSet(
      @RequestBody PaperSet paperSet) throws OsirisValidationException, OsirisException {
    PaperSet currentPaperSet = null;
    if (paperSet.getId() != null) {
      currentPaperSet = (PaperSet) validationHelper.validateReferential(paperSet, true, "paperSets");
      paperSet.setLocationNumber(currentPaperSet.getLocationNumber());
    }
    validationHelper.validateReferential(paperSet.getCustomerOrder(), true, "CustomerOrder");
    validationHelper.validateReferential(paperSet.getPaperSetType(), true, "paperSetType");

    return new ResponseEntity<PaperSet>(paperSetService.addOrUpdatePaperSet(paperSet), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order-comment")
  public ResponseEntity<CustomerOrderComment> addOrUpdateCustomerOrderComment(
      @RequestBody CustomerOrderComment customerOrderComment) throws OsirisValidationException, OsirisException {
    CustomerOrderComment customerOrderCommentOriginal = null;
    if (customerOrderComment.getId() != null) {
      customerOrderCommentOriginal = (CustomerOrderComment) validationHelper.validateReferential(customerOrderComment,
          true, "customerOrderComments");
      customerOrderComment.setProvision(customerOrderCommentOriginal.getProvision());
      customerOrderComment.setCustomerOrder(customerOrderCommentOriginal.getCustomerOrder());
      customerOrderComment.setQuotation(customerOrderCommentOriginal.getQuotation());

      if (!employeeService.getCurrentEmployee().getId().equals(customerOrderComment.getEmployee().getId())
          && !activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ADMINISTRATEUR_GROUP)) {
        throw new OsirisValidationException("not authorizes");
      }
    }
    validationHelper.validateString(customerOrderComment.getComment(), true, "comment");

    validationHelper.validateReferential(customerOrderComment.getEmployee(), true, "employee");

    if (customerOrderComment.getProvision() != null)
      validationHelper.validateReferential(customerOrderComment.getProvision(), false, "provision");

    if (customerOrderComment.getCustomerOrder() != null)
      validationHelper.validateReferential(customerOrderComment.getCustomerOrder(), false, "customerOrder");

    if (customerOrderComment.getQuotation() != null)
      validationHelper.validateReferential(customerOrderComment.getQuotation(), false, "quotation");

    if (customerOrderComment.getCustomerOrder() == null && customerOrderComment.getProvision() == null
        && customerOrderComment.getQuotation() == null)
      throw new OsirisValidationException("customerOrder, quotation and provision are null");

    if (customerOrderComment.getActiveDirectoryGroups() != null)
      for (ActiveDirectoryGroup group : customerOrderComment.getActiveDirectoryGroups()) {
        validationHelper.validateReferential(group, false, "group");
      }

    if (customerOrderComment.getId() == null)
      customerOrderComment.setCreatedDateTime(LocalDateTime.now());
    else if (customerOrderCommentOriginal != null)
      customerOrderComment.setCreatedDateTime(customerOrderCommentOriginal.getCreatedDateTime());

    return new ResponseEntity<CustomerOrderComment>(
        customerOrderCommentService.addOrUpdateCustomerOrderComment(customerOrderComment), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/domiciliation/contract")
  public ResponseEntity<Provision> generateDomiciliationContract(@RequestParam Integer provisionId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

    Provision provision = provisionService.getProvision(provisionId);
    if (provision == null || provision.getDomiciliation() == null)
      throw new OsirisValidationException("provision");

    Affaire affaire = provision.getService().getAssoAffaireOrder().getAffaire();
    Domiciliation domiciliation = provision.getDomiciliation();
    if (affaire.getLegalForm() == null)
      throw new OsirisClientMessageException("La forme juridique de l'affaire doit être renseignée");
    if (affaire.getCompetentAuthority() == null)
      throw new OsirisClientMessageException("L'autorité compétente de l'affaire doit être renseignée");
    if (affaire.getCompetentAuthority().getCity() == null)
      throw new OsirisClientMessageException("La ville de l'autorité compétente de l'affaire doit être renseignée");
    if (affaire.getSiren() == null)
      throw new OsirisClientMessageException("La SIREN de l'affaire doit être renseignée");

    if (domiciliation.getIsLegalPerson() == null || !domiciliation.getIsLegalPerson()) {
      if (domiciliation.getLegalGardianFirstname() == null || domiciliation.getLegalGardianLastname() == null
          || domiciliation.getLegalGardianBirthdate() == null || domiciliation.getLegalGardianPlaceOfBirth() == null
          || domiciliation.getLegalGardianCivility() == null || domiciliation.getLegalGardianJob() == null)
        throw new OsirisClientMessageException("L'ensemble des informations du représentant légal sont obligatoires");
    } else if (domiciliation.getLegalGardianDenomination() == null
        || domiciliation.getLegalGardianLegalForm() == null) {
      throw new OsirisClientMessageException("L'ensemble des informations du représentant légal sont obligatoires");
    }
    if (domiciliation.getLegalGardianAddress() == null || domiciliation.getLegalGardianCity() == null
        || domiciliation.getLegalGardianCountry() == null || domiciliation.getLegalGardianPostalCode() == null)
      throw new OsirisClientMessageException("L'ensemble des informations du représentant légal sont obligatoires");

    if (domiciliation.getActivityDescription() == null)
      throw new OsirisClientMessageException("La description de l'activité doit être renseignée");

    return new ResponseEntity<Provision>(
        domiciliationService.generateDomiciliationContract(provision),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/service/modify")
  public ResponseEntity<Service> modifyServiceType(@RequestParam Integer serviceId,
      @RequestParam Integer serviceTypeId) throws OsirisValidationException {

    Service service = serviceService.getService(serviceId);
    if (service == null)
      throw new OsirisValidationException("service");

    ServiceType serviceType = serviceTypeService.getServiceType(serviceTypeId);
    if (serviceType == null)
      throw new OsirisValidationException("ServiceType");

    return new ResponseEntity<Service>(serviceService.modifyServiceType(serviceType, service),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/service-type/provision")
  public ResponseEntity<Service> getServiceForServiceTypeAndAffaire(@RequestParam Integer idAffaire,
      @RequestParam Integer serviceTypeId) throws OsirisValidationException {

    Affaire affaire = affaireService.getAffaire(idAffaire);
    if (affaire == null)
      throw new OsirisValidationException("Affaire");

    ServiceType serviceType = serviceTypeService.getServiceType(serviceTypeId);
    if (serviceType == null)
      throw new OsirisValidationException("ServiceType");

    return new ResponseEntity<Service>(serviceService.getServiceForServiceTypeAndAffaire(serviceType, affaire),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/service-family-groups")
  public ResponseEntity<List<ServiceFamilyGroup>> getServiceFamilyGroups() {
    return new ResponseEntity<List<ServiceFamilyGroup>>(serviceFamilyGroupService.getServiceFamilyGroups(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/service-family-group")
  public ResponseEntity<ServiceFamilyGroup> addOrUpdateServiceFamilyGroup(
      @RequestBody ServiceFamilyGroup serviceFamilyGroups) throws OsirisValidationException, OsirisException {
    if (serviceFamilyGroups.getId() != null)
      validationHelper.validateReferential(serviceFamilyGroups, true, "serviceFamilyGroups");
    validationHelper.validateString(serviceFamilyGroups.getCode(), true, "code");
    validationHelper.validateString(serviceFamilyGroups.getLabel(), true, "label");

    return new ResponseEntity<ServiceFamilyGroup>(
        serviceFamilyGroupService.addOrUpdateServiceFamilyGroup(serviceFamilyGroups), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/service-families")
  public ResponseEntity<List<ServiceFamily>> getServiceFamilies() {
    return new ResponseEntity<List<ServiceFamily>>(serviceFamilyService.getServiceFamilies(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/service-family")
  public ResponseEntity<ServiceFamily> addOrUpdateServiceFamily(
      @RequestBody ServiceFamily serviceFamilies) throws OsirisValidationException, OsirisException {
    if (serviceFamilies.getId() != null)
      validationHelper.validateReferential(serviceFamilies, true, "serviceFamilies");
    validationHelper.validateString(serviceFamilies.getCode(), true, "code");
    validationHelper.validateString(serviceFamilies.getLabel(), true, "label");
    validationHelper.validateReferential(serviceFamilies.getServiceFamilyGroup(), true, "familyGroup");

    return new ResponseEntity<ServiceFamily>(serviceFamilyService.addOrUpdateServiceFamily(serviceFamilies),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/service-types")
  public ResponseEntity<List<ServiceType>> getServiceTypes() {
    return new ResponseEntity<List<ServiceType>>(serviceTypeService.getServiceTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/service-type")
  public ResponseEntity<ServiceType> addOrUpdateServiceType(
      @RequestBody ServiceType serviceType) throws OsirisValidationException, OsirisException {
    if (serviceType.getId() != null)
      validationHelper.validateReferential(serviceType, true, "services");
    validationHelper.validateString(serviceType.getCode(), true, "code");
    validationHelper.validateString(serviceType.getLabel(), true, "label");
    validationHelper.validateReferential(serviceType.getServiceFamily(), true, "serviceFamily");

    if (serviceType.getAssoServiceProvisionTypes() != null) {
      for (AssoServiceProvisionType assoServiceProvisionType : serviceType.getAssoServiceProvisionTypes()) {
        validationHelper.validateReferential(assoServiceProvisionType.getProvisionType(), true, "provisionType");
        validationHelper.validateInteger(assoServiceProvisionType.getMaxEmployee(), false, "maxEmployee");
        validationHelper.validateInteger(assoServiceProvisionType.getMinEmployee(), false, "minEmployee");
        validationHelper.validateString(assoServiceProvisionType.getCustomerMessageWhenAdded(), false,
            "customerMessageWhenAdded");
      }
    }

    return new ResponseEntity<ServiceType>(serviceTypeService.addOrUpdateServiceType(serviceType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/associate")
  public ResponseEntity<Quotation> associateCustomerOrderToQuotation(@RequestParam Integer idQuotation,
      @RequestParam Integer idCustomerOrder)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    Quotation quotation = quotationService.getQuotation(idQuotation);
    if (quotation == null)
      throw new OsirisValidationException("idQuotation");

    if (!quotation.getQuotationStatus().getCode().equals(QuotationStatus.SENT_TO_CUSTOMER))
      throw new OsirisClientMessageException("Le devis doit être au statut Envoyé au client");

    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
    if (customerOrder == null)
      throw new OsirisValidationException("idCustomerOrder");

    if (quotation.getCustomerOrders() != null && quotation.getCustomerOrders().size() > 0)
      throw new OsirisValidationException("Devis déjà associé à une commande");
    if (customerOrder.getQuotations() != null && customerOrder.getQuotations().size() > 0)
      throw new OsirisValidationException("Commande déjà associée à un devis");

    return new ResponseEntity<Quotation>(quotationService.associateCustomerOrderToQuotation(quotation, customerOrder),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/bank-transferts")
  public ResponseEntity<List<BankTransfert>> getBankTransfers() {
    return new ResponseEntity<List<BankTransfert>>(bankTransfertService.getBankTransfers(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
  @GetMapping(inputEntryPoint + "/bank-transfert/cancel")
  public ResponseEntity<BankTransfert> cancelBankTransfer(@RequestParam Integer idBankTranfert)
      throws OsirisValidationException, OsirisException {
    BankTransfert bankTransfert = bankTransfertService.getBankTransfert(idBankTranfert);
    if (bankTransfert == null)
      throw new OsirisValidationException("bankTransfert");
    return new ResponseEntity<BankTransfert>(bankTransfertService.cancelBankTransfert(bankTransfert),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
  @GetMapping(inputEntryPoint + "/direct-debit-transfert/cancel")
  public ResponseEntity<DirectDebitTransfert> cancelDirectDebitTransfert(@RequestParam Integer idDirectDebitTranfert)
      throws OsirisValidationException, OsirisException {
    DirectDebitTransfert directDebitTransfert = directDebitTransfertService
        .getDirectDebitTransfert(idDirectDebitTranfert);
    if (directDebitTransfert == null)
      throw new OsirisValidationException("directDebitTransfert");
    return new ResponseEntity<DirectDebitTransfert>(
        directDebitTransfertService.cancelDirectDebitTransfert(directDebitTransfert),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/bank-transfert/export/select")
  public ResponseEntity<BankTransfert> selectBankTransfertForExport(@RequestParam Integer idBankTranfert)
      throws OsirisValidationException, OsirisException {
    BankTransfert bankTransfert = bankTransfertService.getBankTransfert(idBankTranfert);
    if (bankTransfert == null)
      throw new OsirisValidationException("bankTransfert");
    return new ResponseEntity<BankTransfert>(bankTransfertService.selectBankTransfertForExport(bankTransfert, true),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/bank-transfert/comment")
  public ResponseEntity<BankTransfert> addOrUpdateTransfertComment(@RequestBody String comment,
      @RequestParam Integer idBankTransfert)
      throws OsirisValidationException, OsirisException {

    BankTransfert bankTransfert = bankTransfertService.getBankTransfert(idBankTransfert);

    if (bankTransfert == null)
      throw new OsirisValidationException("bankTransfert");

    bankTransfert.setComment(comment);
    return new ResponseEntity<>(bankTransfertService.addOrUpdateBankTransfert(bankTransfert), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/bank-transfert/export/unselect")
  public ResponseEntity<BankTransfert> unselectBankTransfertForExport(@RequestParam Integer idBankTranfert)
      throws OsirisValidationException, OsirisException {
    BankTransfert bankTransfert = bankTransfertService.getBankTransfert(idBankTranfert);
    if (bankTransfert == null)
      throw new OsirisValidationException("bankTransfert");
    return new ResponseEntity<BankTransfert>(bankTransfertService.selectBankTransfertForExport(bankTransfert, false),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/simple-provision-status")
  public ResponseEntity<List<SimpleProvisionStatus>> getSimpleProvisionStatus() {
    return new ResponseEntity<List<SimpleProvisionStatus>>(simpleProvisonStatusService.getSimpleProvisionStatus(),
        HttpStatus.OK);
  }

  // TODO : delete
  @GetMapping(inputEntryPoint + "/debour")
  public ResponseEntity<List<DebourDel>> getDebourByProvision(@RequestParam Integer idProvision) {
    Provision provision = provisionService.getProvision(idProvision);
    return new ResponseEntity<List<DebourDel>>(debourDelService.getDebourByProvision(provision), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/formalite-status")
  public ResponseEntity<List<FormaliteStatus>> getFormaliteStatus() {
    return new ResponseEntity<List<FormaliteStatus>>(formaliteStatusService.getFormaliteStatus(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/domiciliation-status")
  public ResponseEntity<List<DomiciliationStatus>> getDomiciliationStatus() {
    return new ResponseEntity<List<DomiciliationStatus>>(domiciliationStatusService.getDomiciliationStatus(),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/announcement-status")
  public ResponseEntity<List<AnnouncementStatus>> getAnnouncementStatus() {
    return new ResponseEntity<List<AnnouncementStatus>>(announcementStatusService.getAnnouncementStatus(),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/announcement")
  public ResponseEntity<Announcement> getAnnouncementById(@RequestParam Integer id) {
    return new ResponseEntity<Announcement>(announcementService.getAnnouncement(id),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation/customer-order")
  public ResponseEntity<List<QuotationSearchResult>> getQuotationByCustomerOrderId(
      @RequestParam Integer idCustomerOrder) {
    return new ResponseEntity<List<QuotationSearchResult>>(quotationService.searchByCustomerOrderId(idCustomerOrder),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/quotation")
  public ResponseEntity<List<OrderingSearchResult>> getCustomerOrderByQuotationId(@RequestParam Integer idQuotation) {
    return new ResponseEntity<List<OrderingSearchResult>>(customerOrderService.searchByQuotationId(idQuotation),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/recurring")
  public ResponseEntity<List<OrderingSearchResult>> getCustomerOrderByCustomerOrderParentRecurringId(
      @RequestParam Integer idCustomerOrderRecurring) {
    return new ResponseEntity<List<OrderingSearchResult>>(
        customerOrderService.searchByCustomerOrderParentRecurringId(idCustomerOrderRecurring),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/recurring/parent")
  public ResponseEntity<List<OrderingSearchResult>> getCustomerOrderParentRecurringByCustomerOrderId(
      @RequestParam Integer idCustomerOrderRecurring) {
    return new ResponseEntity<List<OrderingSearchResult>>(
        customerOrderService.searchByCustomerOrderParentRecurringByCustomerOrderId(idCustomerOrderRecurring),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/announcement/confrere")
  public ResponseEntity<Confrere> getConfrereForAnnouncement(@RequestParam Integer idAnnouncement) {
    return new ResponseEntity<Confrere>(announcementService.getConfrereForAnnouncement(idAnnouncement),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/invoice/customer-order")
  public ResponseEntity<CustomerOrder> getCustomerOrderForInvoice(@RequestParam Integer idInvoice) {
    return new ResponseEntity<CustomerOrder>(invoiceService.getCustomerOrderByIdInvoice(idInvoice),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/announcement/search")
  public ResponseEntity<List<AnnouncementSearchResult>> searchAnnouncementsForWebsite(
      @RequestBody AnnouncementSearch search)
      throws OsirisValidationException, OsirisException {
    if (search.getAffaireName() == null && search.getDepartment() == null && search.getStartDate() == null
        && search.getEndDate() == null && search.getNoticeType() == null)
      throw new OsirisValidationException("Give at least one filter");

    validationHelper.validateReferential(search.getDepartment(), false, "Department");
    validationHelper.validateReferential(search.getNoticeType(), false, "NoticeType");
    validationHelper.validateDate(search.getEndDate(), false, "EndDate");
    validationHelper.validateDate(search.getStartDate(), false, "StartDate");

    return new ResponseEntity<List<AnnouncementSearchResult>>(announcementService.searchAnnouncementsForWebSite(search),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail/billing/compute")
  public ResponseEntity<MailComputeResult> computeMailForBilling(
      @RequestBody Quotation quotation) throws OsirisException, OsirisClientMessageException {
    return new ResponseEntity<MailComputeResult>(
        mailComputeHelper.computeMailForCustomerOrderFinalizationAndInvoice(quotation),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail/digital/compute")
  public ResponseEntity<MailComputeResult> computeMailForDigitalDocument(
      @RequestBody Quotation quotation) throws OsirisException, OsirisClientMessageException {
    return new ResponseEntity<MailComputeResult>(
        mailComputeHelper.computeMailForGenericDigitalDocument(quotation),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/quotation")
  public ResponseEntity<Quotation> generateQuotationMail(@RequestParam Integer quotationId)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    Quotation quotation = quotationService.getQuotation(quotationId);
    if (quotation == null)
      throw new OsirisValidationException("quotation");

    MailComputeResult mailComputeResult = mailComputeHelper.computeMailForQuotationCreationConfirmation(quotation);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.generateQuotationMail(quotation);
    return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/affaire/request/rib")
  public ResponseEntity<Affaire> generateQuotationMail(@RequestParam Integer idAffaire,
      @RequestParam Integer idAssoAffaireOrder)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    AssoAffaireOrder assoAffaireOrder = assoAffaireOrderService.getAssoAffaireOrder(idAssoAffaireOrder);
    if (assoAffaireOrder == null)
      throw new OsirisValidationException("assoAffaireOrder");

    Affaire affaire = affaireService.getAffaire(idAffaire);
    if (affaire == null)
      throw new OsirisValidationException("affaire");

    if (affaire.getMails() == null || affaire.getMails().size() == 0)
      throw new OsirisClientMessageException("Aucun mail trouvé sur l'affaire");

    mailHelper.sendRibRequestToAffaire(affaire, assoAffaireOrder);
    return new ResponseEntity<Affaire>(affaire, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/customer-order-confirmation")
  public ResponseEntity<CustomerOrder> generateCustomerOrderCreationConfirmationToCustomer(
      @RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    MailComputeResult mailComputeResult = mailComputeHelper
        .computeMailForCustomerOrderCreationConfirmation(customerOrder);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.sendCustomerOrderInProgressToCustomer(customerOrder, true);
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/invoice")
  public ResponseEntity<CustomerOrder> generateInvoiceMail(@RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisClientMessageException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");
    try {
      MailComputeResult mailComputeResult = mailComputeHelper
          .computeMailForCustomerOrderFinalizationAndInvoice(customerOrder);
      if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
        throw new OsirisValidationException("MailTo");
      customerOrderService.generateInvoiceMail(customerOrder);
    } catch (OsirisException e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
    }
    return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/send/invoice")
  public ResponseEntity<CustomerOrder> sendInvoiceMail(@RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisClientMessageException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");
    try {
      MailComputeResult mailComputeResult = mailComputeHelper
          .computeMailForCustomerOrderFinalizationAndInvoice(customerOrder);
      if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
        throw new OsirisValidationException("MailTo");
      customerOrderService.sendInvoiceMail(customerOrder);
    } catch (OsirisException e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
    }
    return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/confrere/request")
  public ResponseEntity<CustomerOrder> generateAnnouncementRequestToConfrereMail(@RequestParam Integer idCustomerOrder,
      @RequestParam Integer idAnnouncement, @RequestParam Integer idProvision, @RequestParam Integer idAssoAffaireOrder)
      throws OsirisValidationException, OsirisClientMessageException, OsirisException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    if (announcement == null || announcement.getConfrere() == null || announcement.getPublicationDate() == null
        || announcement.getNoticeTypes() == null
        || announcement.getNoticeTypes().size() == 0)
      throw new OsirisValidationException("announcement");

    Provision provision = provisionService.getProvision(idProvision);
    if (provision == null)
      throw new OsirisValidationException("provision");

    AssoAffaireOrder assoAffaireOrder = assoAffaireOrderService.getAssoAffaireOrder(idAssoAffaireOrder);
    if (assoAffaireOrder == null)
      throw new OsirisValidationException("assoAffaireOrder");

    MailComputeResult mailComputeResult = mailComputeHelper.computeMailForSendAnnouncementToConfrere(announcement);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.sendAnnouncementRequestToConfrere(customerOrder, assoAffaireOrder, true, provision, announcement, false);
    return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail/generate/attachments")
  @Transactional
  public ResponseEntity<CustomerOrder> generateAttachmentMail(@RequestBody AttachmentMailRequest attachmentMailRequest)
      throws OsirisValidationException, OsirisClientMessageException, OsirisException {
    attachmentMailRequest.setCustomerOrder((CustomerOrder) validationHelper
        .validateReferential(attachmentMailRequest.getCustomerOrder(), true, "customer order"));
    attachmentMailRequest.setAssoAffaireOrder((AssoAffaireOrder) validationHelper
        .validateReferential(attachmentMailRequest.getAssoAffaireOrder(), true, "asso affaire order"));

    if (attachmentMailRequest.getAttachements() == null || attachmentMailRequest.getAttachements().size() == 0)
      throw new OsirisValidationException("attachments");

    ArrayList<Attachment> outAttachment = new ArrayList<Attachment>();
    for (Attachment attachment : attachmentMailRequest.getAttachements())
      outAttachment.add(
          (Attachment) validationHelper.validateReferential(attachment, true, "attachment n°" + attachment.getId()));

    MailComputeResult mailComputeResult = mailComputeHelper
        .computeMailForSendNumericAttachment(attachmentMailRequest.getCustomerOrder());
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.sendCustomerOrderAttachmentsToCustomer(attachmentMailRequest.getCustomerOrder(),
        attachmentMailRequest.getAssoAffaireOrder(), attachmentMailRequest.getSendToMe(),
        outAttachment);
    return new ResponseEntity<CustomerOrder>(new CustomerOrder(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision/assignedTo")
  public ResponseEntity<Boolean> updateAssignedToForProvision(@RequestParam Integer provisionId,
      @RequestParam Integer employeeId) throws OsirisValidationException {
    Provision provision = provisionService.getProvision(provisionId);
    if (provision == null)
      throw new OsirisValidationException("provision");

    Employee employee = employeeService.getEmployee(employeeId);
    if (employee == null)
      throw new OsirisValidationException("employee");

    provisionService.updateAssignedToForProvision(provision, employee);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/asso/affaire/order/assignedTo")
  public ResponseEntity<Boolean> updateAssignedToForAsso(@RequestParam Integer assoId,
      @RequestParam Integer employeeId) throws OsirisValidationException, OsirisException {
    AssoAffaireOrder asso = assoAffaireOrderService.getAssoAffaireOrder(assoId);
    if (asso == null)
      throw new OsirisValidationException("asso");

    Employee employee = employeeService.getEmployee(employeeId);
    if (employee == null)
      throw new OsirisValidationException("employee");

    assoAffaireOrderService.updateAssignedToForAsso(asso, employee);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/assign")
  public ResponseEntity<Boolean> updateAssignedToForCustomerOrder(@RequestParam Integer customerOrderId,
      @RequestParam Integer employeeId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    Employee employee = employeeService.getEmployee(employeeId);
    if (employee == null)
      throw new OsirisValidationException("employee");

    customerOrderService.updateAssignedToForCustomerOrder(customerOrder, employee);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/offer")
  public ResponseEntity<Boolean> offerCustomerOrder(@RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    customerOrderService.offerCustomerOrder(customerOrder);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation/assign")
  public ResponseEntity<Boolean> updateAssignedToForQuotation(@RequestParam Integer quotationId,
      @RequestParam Integer employeeId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

    Quotation quotation = quotationService.getQuotation(quotationId);
    if (quotation == null)
      throw new OsirisValidationException("quotationId");

    Employee employee = employeeService.getEmployee(employeeId);
    if (employee == null)
      throw new OsirisValidationException("employee");

    quotationService.updateAssignedToForQuotation(quotation, employee);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/asso/affaire/order/search")
  public ResponseEntity<ArrayList<AssoAffaireOrderSearchResult>> searchForAsso(
      @RequestBody AffaireSearch affaireSearch) throws OsirisValidationException {

    if (affaireSearch.getLabel() == null
        && affaireSearch.getAssignedTo() == null && affaireSearch.getResponsible() == null
        && affaireSearch.getStatus() == null && affaireSearch.getCustomerOrders() == null
        && affaireSearch.getAffaire() == null && affaireSearch.getWaitedCompetentAuthority() == null
        && affaireSearch.getCommercial() == null && affaireSearch.getFormaliteGuichetUniqueStatus() == null)
      throw new OsirisValidationException("Label or AssignedTo or Responsible or Status");

    if (affaireSearch.getLabel() == null)
      affaireSearch.setLabel("");

    affaireSearch.setLabel(affaireSearch.getLabel().trim());

    return new ResponseEntity<ArrayList<AssoAffaireOrderSearchResult>>(
        assoAffaireOrderService.searchForAsso(affaireSearch), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/asso/affaire/order/update")
  public ResponseEntity<AssoAffaireOrder> addOrUpdateAssoAffaireOrder(@RequestBody AssoAffaireOrder assoAffaireOrder)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    validationHelper.validateReferential(assoAffaireOrder, true, "assoAffaireOrder");
    validationHelper.validateReferential(assoAffaireOrder.getAffaire(), true, "Affaire");
    validationHelper.validateReferential(assoAffaireOrder.getAssignedTo(), true, "AssignedTo");
    validationHelper.validateReferential(assoAffaireOrder.getCustomerOrder(), true, "CustomerOrder");

    if (assoAffaireOrder.getServices() == null)
      throw new OsirisValidationException("Services");

    for (Service service : assoAffaireOrder.getServices())
      if (service.getProvisions() == null)
        throw new OsirisValidationException("Provisions");

    CustomerOrder customerOrder = assoAffaireOrder.getCustomerOrder() != null ? assoAffaireOrder.getCustomerOrder()
        : null;

    if (customerOrder == null)
      throw new OsirisValidationException("CustomerOrder");

    for (Service service : assoAffaireOrder.getServices())
      for (Provision provision : service.getProvisions())
        quotationValidationHelper.validateProvisionTransactionnal(provision, null, customerOrder);

    assoAffaireOrderService.addOrUpdateAssoAffaireOrderFromUser(assoAffaireOrder);

    return new ResponseEntity<AssoAffaireOrder>(assoAffaireOrderService.getAssoAffaireOrder(assoAffaireOrder.getId()),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/asso/affaire/order")
  public ResponseEntity<AssoAffaireOrder> getAsso(@RequestParam Integer id) throws OsirisValidationException {
    if (id == null)
      throw new OsirisValidationException("Id");

    return new ResponseEntity<AssoAffaireOrder>(assoAffaireOrderService.getAssoAffaireOrder(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/asso/affaire/order/provision")
  public ResponseEntity<AssoAffaireOrder> getAssoFromProvision(@RequestParam Integer id)
      throws OsirisValidationException {
    if (id == null)
      throw new OsirisValidationException("Id");

    return new ResponseEntity<AssoAffaireOrder>(provisionService.getProvision(id).getService().getAssoAffaireOrder(),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/assignation-types")
  public ResponseEntity<List<AssignationType>> getAssignationTypes() {
    return new ResponseEntity<List<AssignationType>>(assignationTypeService.getAssignationTypes(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision-screen-types")
  public ResponseEntity<List<ProvisionScreenType>> getProvisionScreenTypes() {
    return new ResponseEntity<List<ProvisionScreenType>>(provisionScreenTypeService.getProvisionScreenTypes(),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/announcement-notice-templates")
  public ResponseEntity<List<AnnouncementNoticeTemplate>> getAnnouncementNoticeTemplates() {
    return new ResponseEntity<List<AnnouncementNoticeTemplate>>(
        announcementNoticeTemplateService.getAnnouncementNoticeTemplates(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/announcement-notice-template")
  public ResponseEntity<AnnouncementNoticeTemplate> addOrUpdateAnnouncementNoticeTemplate(
      @RequestBody AnnouncementNoticeTemplate announcementNoticeTemplates)
      throws OsirisValidationException, OsirisException {
    if (announcementNoticeTemplates.getId() != null)
      validationHelper.validateReferential(announcementNoticeTemplates, true, "announcementNoticeTemplates");
    validationHelper.validateString(announcementNoticeTemplates.getCode(), true, 40, "code");
    validationHelper.validateString(announcementNoticeTemplates.getLabel(), true, 100, "label");
    if (announcementNoticeTemplates.getProvisionFamilyTypes() != null)
      for (ProvisionFamilyType provisionFamilyType : announcementNoticeTemplates.getProvisionFamilyTypes())
        validationHelper.validateReferential(provisionFamilyType, false, "provisionFamilyType");

    return new ResponseEntity<AnnouncementNoticeTemplate>(
        announcementNoticeTemplateService.addOrUpdateAnnouncementNoticeTemplate(announcementNoticeTemplates),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/act-types")
  public ResponseEntity<List<ActType>> getActTypes() {
    return new ResponseEntity<List<ActType>>(actTypeService.getActTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/act-type")
  public ResponseEntity<ActType> addOrUpdateActType(@RequestBody ActType actType)
      throws OsirisValidationException, OsirisException {
    if (actType.getId() != null)
      validationHelper.validateReferential(actType, true, "actType");
    validationHelper.validateString(actType.getCode(), true, 20, "code");
    validationHelper.validateString(actType.getLabel(), true, 100, "label");

    return new ResponseEntity<ActType>(actTypeService.addOrUpdateActType(actType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/fund-types")
  public ResponseEntity<List<FundType>> getFundTypes() {
    return new ResponseEntity<List<FundType>>(fundTypeService.getFundTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/fund-type")
  public ResponseEntity<FundType> addOrUpdateFundType(
      @RequestBody FundType fundType) throws OsirisValidationException, OsirisException {
    if (fundType.getId() != null)
      validationHelper.validateReferential(fundType, true, "fundType");
    validationHelper.validateString(fundType.getCode(), true, 20, "code");
    validationHelper.validateString(fundType.getLabel(), true, 100, "label");

    return new ResponseEntity<FundType>(fundTypeService.addOrUpdateFundType(fundType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/transfert-fund-types")
  public ResponseEntity<List<TransfertFundsType>> getTransfertFundsTypes() {
    return new ResponseEntity<List<TransfertFundsType>>(transfertFundsTypeService.getTransfertFundsTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/transfert-fund-type")
  public ResponseEntity<TransfertFundsType> addOrUpdateTransfertFundsType(
      @RequestBody TransfertFundsType transfertFundsType) throws OsirisValidationException, OsirisException {
    if (transfertFundsType.getId() != null)
      validationHelper.validateReferential(transfertFundsType, true, "transfertFundsType");
    validationHelper.validateString(transfertFundsType.getCode(), true, 20, "code");
    validationHelper.validateString(transfertFundsType.getLabel(), true, 100, "label");

    return new ResponseEntity<TransfertFundsType>(
        transfertFundsTypeService.addOrUpdateTransfertFundsType(transfertFundsType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/notice-type-families")
  public ResponseEntity<List<NoticeTypeFamily>> getNoticeTypeFamilies() {
    return new ResponseEntity<List<NoticeTypeFamily>>(noticeTypeFamilyService.getNoticeTypeFamilies(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/notice-type-family")
  public ResponseEntity<NoticeTypeFamily> addOrUpdateNoticeTypeFamily(
      @RequestBody NoticeTypeFamily noticeTypeFamily) throws OsirisValidationException, OsirisException {
    if (noticeTypeFamily.getId() != null)
      validationHelper.validateReferential(noticeTypeFamily, true, "noticeTypeFamily");
    validationHelper.validateString(noticeTypeFamily.getCode(), true, 20, "code");
    validationHelper.validateString(noticeTypeFamily.getLabel(), true, 100, "label");

    return new ResponseEntity<NoticeTypeFamily>(noticeTypeFamilyService.addOrUpdateNoticeTypeFamily(noticeTypeFamily),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/notice-types")
  public ResponseEntity<List<NoticeType>> getNoticeTypes() {
    return new ResponseEntity<List<NoticeType>>(noticeTypeService.getNoticeTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/notice-type")
  public ResponseEntity<NoticeType> addOrUpdateNoticeType(
      @RequestBody NoticeType noticeTypes) throws OsirisValidationException, OsirisException {
    if (noticeTypes.getId() != null)
      validationHelper.validateReferential(noticeTypes, true, "noticeTypes");
    validationHelper.validateString(noticeTypes.getCode(), true, 20, "code");
    validationHelper.validateString(noticeTypes.getLabel(), true, 200, "label");

    return new ResponseEntity<NoticeType>(noticeTypeService.addOrUpdateNoticeType(noticeTypes), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/character-price")
  public ResponseEntity<CharacterPrice> getCharacterPrice(@RequestParam Integer departmentId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate date)
      throws OsirisValidationException {
    if (departmentId == null || date == null)
      throw new OsirisValidationException("DepartmentId or date");

    Department department = departmentService.getDepartment(departmentId);
    if (department == null)
      throw new OsirisValidationException("department");

    return new ResponseEntity<CharacterPrice>(characterPriceService.getCharacterPriceFromUser(department, date),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/character/number")
  public ResponseEntity<Integer> getCharacterNumber(@RequestBody Provision provision)
      throws OsirisValidationException {

    return new ResponseEntity<Integer>(characterPriceService.getCharacterNumber(provision, false), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/character-prices")
  public ResponseEntity<List<CharacterPrice>> getCharacterPrices() {
    return new ResponseEntity<List<CharacterPrice>>(characterPriceService.getCharacterPrices(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/character-price")
  public ResponseEntity<CharacterPrice> addOrUpdateCharacterPrice(
      @RequestBody CharacterPrice characterPrices) throws OsirisValidationException, OsirisException {
    if (characterPrices.getId() != null)
      validationHelper.validateReferential(characterPrices, true, "characterPrices");
    if (characterPrices.getDepartments() == null || characterPrices.getDepartments().size() == 0)
      throw new OsirisValidationException("Departments");

    for (Department department : characterPrices.getDepartments())
      validationHelper.validateReferential(department, true, "department");

    validationHelper.validateDate(characterPrices.getStartDate(), true, "characterPrices");

    return new ResponseEntity<CharacterPrice>(characterPriceService.addOrUpdateCharacterPrice(characterPrices),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/journal-types")
  public ResponseEntity<List<JournalType>> getJournalTypes() {
    return new ResponseEntity<List<JournalType>>(journalTypeService.getJournalTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/journal-type")
  public ResponseEntity<JournalType> addOrUpdateJournalType(
      @RequestBody JournalType journalType) throws OsirisValidationException, OsirisException {
    if (journalType.getId() != null)
      validationHelper.validateReferential(journalType, true, "journalType");
    validationHelper.validateString(journalType.getCode(), true, 20, "code");
    validationHelper.validateString(journalType.getLabel(), true, 100, "label");

    return new ResponseEntity<JournalType>(journalTypeService.addOrUpdateJournalType(journalType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/confrere")
  public ResponseEntity<Confrere> getConfrere(@RequestParam Integer id) {
    return new ResponseEntity<Confrere>(confrereService.getConfrere(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/confreres")
  public ResponseEntity<List<Confrere>> getConfreres() {
    return new ResponseEntity<List<Confrere>>(confrereService.getConfreres(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/confreres/search")
  public ResponseEntity<List<Confrere>> getConfreres(@RequestParam(required = false) Integer departmentId,
      @RequestParam String label) {
    Department department = null;
    if (departmentId != null)
      department = departmentService.getDepartment(departmentId);
    return new ResponseEntity<List<Confrere>>(
        confrereService.searchConfrereFilteredByDepartmentAndName(department, label), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/confrere")
  public ResponseEntity<Confrere> addOrUpdateConfrere(
      @RequestBody Confrere confrere) throws OsirisValidationException, OsirisException, OsirisClientMessageException {
    if (confrere.getId() != null)
      validationHelper.validateReferential(confrere, true, "confrere");
    validationHelper.validateString(confrere.getCode(), true, 20, "code");
    validationHelper.validateString(confrere.getLabel(), true, 100, "label");

    if (confrere.getDepartments() != null && confrere.getDepartments().size() > 0)
      for (Department department : confrere.getDepartments())
        validationHelper.validateReferential(department, false, "department");

    if (confrere.getWeekDays() != null && confrere.getWeekDays().size() > 0)
      for (WeekDay weekDay : confrere.getWeekDays())
        validationHelper.validateReferential(weekDay, false, "weekDay");

    validationHelper.validateReferential(confrere.getJournalType(), true, "JournalType");
    validationHelper.validateString(confrere.getLastShipmentForPublication(), false, 200, "LastShipmentForPublication");
    validationHelper.validateString(confrere.getBoardGrade(), false, 20, "BoardGrade");
    validationHelper.validateString(confrere.getPaperGrade(), false, 20, "PaperGrade");
    validationHelper.validateString(confrere.getBillingGrade(), false, 20, "BillingGrade");
    validationHelper.validateString(confrere.getPublicationCertificateDocumentGrade(), false, 20,
        "PublicationCertificateDocumentGrade");
    validationHelper.validateString(confrere.getMailRecipient(), false, 60, "MailRecipient");
    validationHelper.validateString(confrere.getAddress(), false, 60, "Address");
    validationHelper.validateString(confrere.getPostalCode(), false, 10, "PostalCode");
    validationHelper.validateString(confrere.getCedexComplement(), false, 20, "CedexComplement");
    validationHelper.validateReferential(confrere.getCity(), false, "City");
    validationHelper.validateReferential(confrere.getCountry(), false, "Country");
    validationHelper.validateReferential(confrere.getVatCollectionType(), true, "VatCollectionType");
    validationHelper.validateReferential(confrere.getPaymentType(), true, "PaymentType");
    validationHelper.validateIban(confrere.getPaymentIban(), false, "PaymentIBAN");
    validationHelper.validateBic(confrere.getPaymentBic(), false, "PaymentBic");
    validationHelper.validateString(confrere.getIntercommunityVat(), false, 20, "intercommunityVat");

    if (confrere.getPaymentType() != null
        && confrere.getPaymentType().getId().equals(constantService.getPaymentTypePrelevement().getId())) {
      validationHelper.validateDate(confrere.getSepaMandateSignatureDate(), true, "SepaMandateSignatureDate");
    }

    if (confrere.getSpecialOffers() != null) {
      for (SpecialOffer specialOffer : confrere.getSpecialOffers()) {
        validationHelper.validateReferential(specialOffer, false, "specialOffer");
      }
    }

    if (confrere.getDocuments() != null && confrere.getDocuments().size() > 0) {
      for (Document document : confrere.getDocuments()) {

        validationHelper.validateReferential(document.getDocumentType(), true, "DocumentType");

        if (document.getMailsAffaire() != null && !validationHelper.validateMailList(document.getMailsAffaire()))
          throw new OsirisValidationException("MailsAffaire");
        if (document.getMailsClient() != null && document.getMailsClient() != null
            && document.getMailsClient().size() > 0)
          if (!validationHelper.validateMailList(document.getMailsClient()))
            throw new OsirisValidationException("MailsClient");

        validationHelper.validateString(document.getAffaireAddress(), false, 200, "AffaireAddress");
        validationHelper.validateString(document.getClientAddress(), false, 200, "ClientAddress");
        validationHelper.validateString(document.getBillingAddress(), false, 200, "BillingAddress");
        validationHelper.validateString(document.getBillingLabel(), false, 200, "BillingLabel");
        validationHelper.validateString(document.getAffaireRecipient(), false, 200, "AffaireRecipient");
        validationHelper.validateString(document.getClientRecipient(), false, 200, "ClientRecipient");
        validationHelper.validateString(document.getCommandNumber(), false, 40, "CommandNumber");
        validationHelper.validateReferential(document.getPaymentDeadlineType(), false, "PaymentDeadlineType");
        validationHelper.validateReferential(document.getRefundType(), false, "RefundType");
        validationHelper.validateIban(document.getRefundIBAN(), false, "RefundIBAN");
        validationHelper.validateBic(document.getRefundBic(), false, "RefundBic");
        validationHelper.validateReferential(document.getBillingClosureType(), false, "BillingClosureType");
        validationHelper.validateReferential(document.getBillingClosureRecipientType(), false,
            "BillingClosureRecipientType");
        validationHelper.validateReferential(document.getRegie(), false, "Regie");

        if (document.getIsRecipientAffaire() == null)
          document.setIsRecipientAffaire(false);
        if (document.getIsRecipientClient() == null)
          document.setIsRecipientClient(false);

      }
    }

    return new ResponseEntity<Confrere>(confrereService.addOrUpdateConfrere(confrere), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail-redirection-types")
  public ResponseEntity<List<MailRedirectionType>> getMailRedirectionTypes() {
    return new ResponseEntity<List<MailRedirectionType>>(mailRedirectionTypeService.getMailRedirectionTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/mail-redirection-type")
  public ResponseEntity<MailRedirectionType> addOrUpdateMailRedirectionType(
      @RequestBody MailRedirectionType mailRedirectionType) throws OsirisValidationException, OsirisException {
    if (mailRedirectionType.getId() != null)
      validationHelper.validateReferential(mailRedirectionType, true, "mailRedirectionType");
    validationHelper.validateString(mailRedirectionType.getCode(), true, 20, "code");
    validationHelper.validateString(mailRedirectionType.getLabel(), true, 100, "label");

    return new ResponseEntity<MailRedirectionType>(
        mailRedirectionTypeService.addOrUpdateMailRedirectionType(mailRedirectionType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/domiciliation/fee/delete")
  public ResponseEntity<Boolean> deleteDomiciliationFee(@RequestParam Integer domiciliationFeeId)
      throws OsirisValidationException {
    DomiciliationFee domiciliationFee = domiciliationFeeService.getDomiciliationFee(domiciliationFeeId);
    if (domiciliationFee == null)
      throw new OsirisValidationException("domiciliationFee");

    domiciliationFeeService.deleteDomiciliationFee(domiciliationFee);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/domiciliation/fee/add")
  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  public ResponseEntity<DomiciliationFee> addDomiciliationFee(
      @RequestBody DomiciliationFee domiciliationFee) throws OsirisValidationException, OsirisException {
    if (domiciliationFee.getId() != null)
      validationHelper.validateReferential(domiciliationFee, true, "domiciliationFee");
    validationHelper.validateReferential(domiciliationFee.getBillingType(), true, "BillingType");
    validationHelper.validateReferential(domiciliationFee.getDomiciliation(), true, "Domiciliation");
    validationHelper.validateFloat(domiciliationFee.getAmount(), true, "amount");
    validationHelper.validateDateMax(domiciliationFee.getFeeDate(), true, LocalDate.now(), "feeDate");

    return new ResponseEntity<DomiciliationFee>(
        domiciliationFeeService.addDomiciliationFee(domiciliationFee), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/building-domiciliations")
  public ResponseEntity<List<BuildingDomiciliation>> getBuildingDomiciliations() {
    return new ResponseEntity<List<BuildingDomiciliation>>(buildingDomiciliationService.getBuildingDomiciliations(),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/building-domiciliation")
  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  public ResponseEntity<BuildingDomiciliation> addOrUpdateBuildingDomiciliation(
      @RequestBody BuildingDomiciliation buildingDomiciliation) throws OsirisValidationException, OsirisException {
    if (buildingDomiciliation.getId() != null)
      validationHelper.validateReferential(buildingDomiciliation, true, "buildingDomiciliation");
    validationHelper.validateString(buildingDomiciliation.getCode(), true, 20, "Code");
    validationHelper.validateString(buildingDomiciliation.getLabel(), true, 100, "Label");
    validationHelper.validateString(buildingDomiciliation.getAddress(), false, 60, "Address");
    validationHelper.validateString(buildingDomiciliation.getPostalCode(), false, 10, "PostalCode");
    validationHelper.validateString(buildingDomiciliation.getCedexComplement(), false, 20, "CedexComplement");
    validationHelper.validateReferential(buildingDomiciliation.getCity(), false, "City");
    validationHelper.validateReferential(buildingDomiciliation.getCountry(), false, "Country");

    return new ResponseEntity<BuildingDomiciliation>(
        buildingDomiciliationService.addOrUpdateBuildingDomiciliation(buildingDomiciliation), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/domiciliation-contract-types")
  public ResponseEntity<List<DomiciliationContractType>> getContractTypes() {
    return new ResponseEntity<List<DomiciliationContractType>>(contractTypeService.getDomiciliationContractTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/domiciliation-contract-type")
  public ResponseEntity<DomiciliationContractType> addOrUpdateDomiciliationContractType(
      @RequestBody DomiciliationContractType domiciliationContractType)
      throws OsirisValidationException, OsirisException {
    if (domiciliationContractType.getId() != null)
      validationHelper.validateReferential(domiciliationContractType, true, "domiciliationContractType");
    validationHelper.validateString(domiciliationContractType.getCode(), true, 20, "code");
    validationHelper.validateString(domiciliationContractType.getLabel(), true, 100, "label");
    validationHelper.validateString(domiciliationContractType.getEnglishLabel(), true, 100, "englishLabel");

    return new ResponseEntity<DomiciliationContractType>(
        domiciliationContractTypeService.addOrUpdateDomiciliationContractType(domiciliationContractType),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision-types")
  public ResponseEntity<List<ProvisionType>> getProvisionTypes() {
    return new ResponseEntity<List<ProvisionType>>(provisionTypeService.getProvisionTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/provision-type")
  public ResponseEntity<ProvisionType> addOrUpdateProvisionType(
      @RequestBody ProvisionType provisionType) throws OsirisValidationException, OsirisException {
    if (provisionType.getId() != null)
      validationHelper.validateReferential(provisionType, true, "provisionType");
    validationHelper.validateString(provisionType.getCode(), true, 20, "Code");
    validationHelper.validateString(provisionType.getLabel(), true, 255, "Label");
    validationHelper.validateReferential(provisionType.getProvisionScreenType(), true, "ProvisionScreenType");
    validationHelper.validateReferential(provisionType.getAssignationType(), true, "AssignationType");
    validationHelper.validateReferential(provisionType.getDefaultCompetentAuthorityServiceProvider(), false,
        "defaultCompetentAuthorityServiceProvider");
    validationHelper.validateReferential(provisionType.getRecurringFrequency(),
        provisionType.getIsRecurring() != null && provisionType.getIsRecurring(), "recurringFrequency");
    if (provisionType.getAssignationType().getCode().equals(AssignationType.EMPLOYEE))
      ;
    validationHelper.validateReferential(provisionType.getDefaultEmployee(), true, "DefaultEmployee");

    if (provisionType.getBillingTypes() != null && provisionType.getBillingTypes().size() > 0)
      for (BillingType billingType : provisionType.getBillingTypes())
        validationHelper.validateReferential(billingType, false, "billingType");

    return new ResponseEntity<ProvisionType>(provisionTypeService.addOrUpdateProvisionType(provisionType),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision-family-types")
  public ResponseEntity<List<ProvisionFamilyType>> getProvisionFamilyTypes() {
    return new ResponseEntity<List<ProvisionFamilyType>>(provisionFamilyTypeService.getProvisionFamilyTypes(),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/provision-family-type")
  public ResponseEntity<ProvisionFamilyType> addOrUpdateProvisionFamilyType(
      @RequestBody ProvisionFamilyType provisionFamilyType) throws OsirisValidationException, OsirisException {
    if (provisionFamilyType.getId() != null)
      validationHelper.validateReferential(provisionFamilyType, true, "provisionFamilyType");
    validationHelper.validateString(provisionFamilyType.getCode(), true, 20, "code");
    validationHelper.validateString(provisionFamilyType.getLabel(), true, 100, "label");

    return new ResponseEntity<ProvisionFamilyType>(
        provisionFamilyTypeService.addOrUpdateProvisionFamilyType(provisionFamilyType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/siren")
  public ResponseEntity<List<Affaire>> getAffairesFromSiren(@RequestParam String siren)
      throws OsirisClientMessageException, OsirisValidationException, OsirisException {
    if (siren == null)
      throw new OsirisValidationException("siren");
    return new ResponseEntity<List<Affaire>>(affaireService.getAffairesFromSiren(siren.trim().replaceAll(" ", "")),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/siret")
  public ResponseEntity<List<Affaire>> getAffairesFromSiret(@RequestParam String siret)
      throws OsirisClientMessageException, OsirisValidationException, OsirisException {
    if (siret == null)
      throw new OsirisValidationException("siren");
    return new ResponseEntity<List<Affaire>>(affaireService.getAffairesFromSiret(siret.trim().replaceAll(" ", "")),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/rna")
  public ResponseEntity<List<Affaire>> getAffairesFromRna(@RequestParam String rna)
      throws OsirisClientMessageException, OsirisValidationException, OsirisException {
    if (rna == null)
      throw new OsirisValidationException("siren");
    return new ResponseEntity<List<Affaire>>(affaireService.getAffairesFromRna(rna.trim().replaceAll(" ", "")),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/affaire")
  public ResponseEntity<Affaire> getAffaire(@RequestParam Integer id) throws OsirisValidationException {
    if (id == null)
      throw new OsirisValidationException("id");

    return new ResponseEntity<Affaire>(affaireService.getAffaire(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/affaires")
  public ResponseEntity<List<Affaire>> getAffaires() {
    return new ResponseEntity<List<Affaire>>(affaireService.getAffaires(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/record-types")
  public ResponseEntity<List<RecordType>> getRecordTypes() {
    return new ResponseEntity<List<RecordType>>(recordTypeService.getRecordTypes(), HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @PostMapping(inputEntryPoint + "/record-type")
  public ResponseEntity<RecordType> addOrUpdateRecordType(
      @RequestBody RecordType recordType) throws OsirisValidationException, OsirisException {
    if (recordType.getId() != null)
      validationHelper.validateReferential(recordType, true, "recordType");
    validationHelper.validateString(recordType.getCode(), true, 20, "code");
    validationHelper.validateString(recordType.getLabel(), true, 100, "label");

    return new ResponseEntity<RecordType>(recordTypeService.addOrUpdateRecordType(recordType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation-status")
  public ResponseEntity<List<QuotationStatus>> getQuotationStatus() {
    return new ResponseEntity<List<QuotationStatus>>(quotationStatusService.getQuotationStatus(), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order-status")
  public ResponseEntity<List<CustomerOrderStatus>> getCustomerOrderStatus() {
    return new ResponseEntity<List<CustomerOrderStatus>>(customerOrderStatusService.getCustomerOrderStatus(),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation")
  public ResponseEntity<Quotation> getQuotation(@RequestParam Integer id) {
    return new ResponseEntity<Quotation>(quotationService.getQuotation(id), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order")
  public ResponseEntity<CustomerOrder> getCustomerOrder(@RequestParam Integer id) {
    return new ResponseEntity<CustomerOrder>(customerOrderService.getCustomerOrder(id), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation")
  public ResponseEntity<Quotation> addOrUpdateQuotation(@RequestBody Quotation quotation)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    validateQuotationAndCustomerOrder(quotation);

    QuotationStatus openQuotationStatus = quotationStatusService.getQuotationStatusByCode(QuotationStatus.OPEN);
    if (openQuotationStatus == null)
      if (quotation.getQuotationStatus() == null)
        throw new OsirisException(null, "OPEN Quotation status not found");

    if (quotation.getQuotationStatus() == null)
      quotation.setQuotationStatus(openQuotationStatus);
    return new ResponseEntity<Quotation>(quotationService.addOrUpdateQuotationFromUser(quotation), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/order/search")
  public ResponseEntity<List<OrderingSearchResult>> searchOrders(@RequestBody OrderingSearch orderingSearch)
      throws OsirisValidationException, OsirisException {
    if (orderingSearch == null)
      throw new OsirisValidationException("orderingSearch");

    validationHelper.validateReferential(orderingSearch.getSalesEmployee(), false, "SalesEmployee");
    if (orderingSearch.getCustomerOrderStatus() != null)
      for (CustomerOrderStatus status : orderingSearch.getCustomerOrderStatus())
        validationHelper.validateReferential(status, false, "status");

    return new ResponseEntity<List<OrderingSearchResult>>(customerOrderService.searchOrders(orderingSearch),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation/search")
  public ResponseEntity<List<QuotationSearchResult>> searchQuotations(@RequestBody QuotationSearch quotationSearch)
      throws OsirisValidationException, OsirisException {
    if (quotationSearch == null)
      throw new OsirisValidationException("quotationSearch");

    validationHelper.validateReferential(quotationSearch.getSalesEmployee(), false, "SalesEmployee");
    if (quotationSearch.getQuotationStatus() != null)
      for (QuotationStatus status : quotationSearch.getQuotationStatus())
        validationHelper.validateReferential(status, false, "status");

    return new ResponseEntity<List<QuotationSearchResult>>(quotationService.searchQuotations(quotationSearch),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation-abandon-reasons")
  public ResponseEntity<List<QuotationAbandonReason>> getQuotationAbandonReasons() {
    return new ResponseEntity<List<QuotationAbandonReason>>(quotationAbandonReasonService.getQuotationAbandonReasons(),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation-abandon-reason")
  public ResponseEntity<QuotationAbandonReason> addOrUpdateQutationAbandonReason(
      @RequestBody QuotationAbandonReason abandonReason)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {

    return new ResponseEntity<QuotationAbandonReason>(
        quotationAbandonReasonService.addOrUpdateQutationAbandonReason(abandonReason),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order")
  public ResponseEntity<CustomerOrder> addOrUpdateCustomerOrder(@RequestBody CustomerOrder customerOrder)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException, OsirisDuplicateException {
    validateQuotationAndCustomerOrder(customerOrder);
    CustomerOrderStatus customerOrderStatus = customerOrderStatusService
        .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
    if (customerOrderStatus == null)
      if (customerOrder.getCustomerOrderStatus() == null)
        throw new OsirisException(null, "OPEN Customer Order status not found");

    if (customerOrder.getCustomerOrderStatus() == null)
      customerOrder.setCustomerOrderStatus(customerOrderStatus);

    return new ResponseEntity<CustomerOrder>(customerOrderService.addOrUpdateCustomerOrderFromUser(customerOrder),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order/status")
  public ResponseEntity<CustomerOrder> addOrUpdateCustomerOrderStatus(@RequestBody CustomerOrder customerOrder,
      @RequestParam String targetStatusCode)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {

    customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
    if (!targetStatusCode.equals(CustomerOrderStatus.ABANDONED)) {
      quotationValidationHelper.validateQuotationAndCustomerOrder(customerOrder, targetStatusCode);
    }
    boolean found = true;
    if (customerOrder.getCustomerOrderStatus() != null) {
      if (customerOrder.getCustomerOrderStatus().getSuccessors() != null)
        for (CustomerOrderStatus status : customerOrder.getCustomerOrderStatus().getSuccessors())
          if (status.getCode().equals(targetStatusCode))
            found = true;
      if (customerOrder.getCustomerOrderStatus().getPredecessors() != null)
        for (CustomerOrderStatus status : customerOrder.getCustomerOrderStatus().getPredecessors())
          if (status.getCode().equals(targetStatusCode))
            found = true;
    }
    if (!found)
      throw new OsirisValidationException("Transition forbidden");

    return new ResponseEntity<CustomerOrder>(
        customerOrderService.addOrUpdateCustomerOrderStatusFromUser(customerOrder, targetStatusCode), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation/status")
  public ResponseEntity<Quotation> addOrUpdateQuotationStatus(@RequestBody Quotation quotation,
      @RequestParam String targetStatusCode)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    quotation = quotationService.getQuotation(quotation.getId());
    if (!targetStatusCode.equals(QuotationStatus.ABANDONED))
      validateQuotationAndCustomerOrder(quotation);
    boolean found = true;
    if (quotation.getQuotationStatus() != null) {
      if (quotation.getQuotationStatus().getSuccessors() != null)
        for (QuotationStatus status : quotation.getQuotationStatus().getSuccessors())
          if (status.getCode().equals(targetStatusCode))
            found = true;
      if (quotation.getQuotationStatus().getPredecessors() != null)
        for (QuotationStatus status : quotation.getQuotationStatus().getPredecessors())
          if (status.getCode().equals(targetStatusCode))
            found = true;
    }
    if (!found)
      throw new OsirisValidationException("Transition forbidden");

    return new ResponseEntity<Quotation>(quotationService.addOrUpdateQuotationStatus(quotation, targetStatusCode),
        HttpStatus.OK);
  }

  private void validateQuotationAndCustomerOrder(IQuotation quotation)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {
    quotationValidationHelper.validateQuotationAndCustomerOrder(quotation, null);
  }

  @PostMapping(inputEntryPoint + "/affaire")
  public ResponseEntity<Affaire> addOrUpdateAffaire(@RequestBody Affaire affaire)
      throws OsirisValidationException, OsirisException, OsirisDuplicateException {

    validationHelper.validateReferential(affaire.getCountry(), true, "Country");
    validationHelper.validateReferential(affaire.getMainActivity(), false, "MainActivity");
    validationHelper.validateReferential(affaire.getLegalForm(), false, "LegalForm");
    validationHelper.validateReferential(affaire.getCompetentAuthority(), false, "CompetentAuthority");
    validationHelper.validateString(affaire.getExternalReference(), false, 60, "ExternalReference");
    validationHelper.validateString(affaire.getIntercommunityVat(), false, 20, "IntercommunityVat");
    if (affaire.getCountry() != null && affaire.getCountry().getId().equals(constantService.getCountryFrance().getId()))
      validationHelper.validateString(affaire.getPostalCode(), true, 10, "PostalCode");
    validationHelper.validateString(affaire.getCedexComplement(), false, 20, "CedexComplement");
    validationHelper.validateString(affaire.getAddress(), true, 100, "Address");
    validationHelper.validateReferential(affaire.getCity(), true, "City");

    if (affaire.getIsIndividual()) {
      validationHelper.validateReferential(affaire.getCivility(), true, "Civility");
      validationHelper.validateString(affaire.getFirstname(), true, 40, "Firstname");
      validationHelper.validateString(affaire.getLastname(), true, 40, "Lastname");
      affaire.setDenomination(null);
      if (affaire.getLastname() != null)
        affaire.setLastname(affaire.getLastname().toUpperCase());

    } else {
      validationHelper.validateString(affaire.getDenomination(), true, 150, "Denomination");
      affaire.setFirstname(null);
      affaire.setLastname(null);
      if (affaire.getRna() != null
          && !validationHelper.validateRna(affaire.getRna().toUpperCase().replaceAll(" ", "")))
        throw new OsirisValidationException("RNA");
      if (affaire.getSiren() != null
          && !validationHelper.validateSiren(affaire.getSiren().toUpperCase().replaceAll(" ", "")))
        throw new OsirisValidationException("SIREN");
      if (affaire.getSiret() != null
          && !validationHelper.validateSiret(affaire.getSiret().toUpperCase().replaceAll(" ", "")))
        throw new OsirisValidationException("SIRET");
    }
    return new ResponseEntity<Affaire>(affaireService.addOrUpdateAffaire(affaire), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/affaire/refresh/rne")
  public ResponseEntity<Affaire> refreshAffaireFromRne(@RequestParam Integer idAffaire)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    if (idAffaire == null)
      throw new OsirisValidationException("idAffaire");

    Affaire affaire = affaireService.getAffaire(idAffaire);
    if (affaire == null)
      throw new OsirisValidationException("affaire");

    if (affaire.getSiret() == null || affaire.getSiret().equals(""))
      throw new OsirisValidationException("siret");

    return new ResponseEntity<Affaire>(affaireService.refreshAffaireFromRne(affaire), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/invoice-item/generate")
  public ResponseEntity<IQuotation> generateInvoiceItemForQuotation(@RequestBody Quotation quotation)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    return new ResponseEntity<IQuotation>(pricingHelper.getAndSetInvoiceItemsForQuotationForFront(quotation, false),
        HttpStatus.OK);
  }

  // Payment deposit

  @GetMapping(inputEntryPoint + "/payment/cb/quotation/validate")
  public ResponseEntity<String> validateQuotationFromCustomer(@RequestParam Integer quotationId,
      @RequestParam String validationToken) {
    try {
      Quotation quotation = quotationService.getQuotation(quotationId);
      if (quotation == null)
        throw new OsirisValidationException("quotation");

      if (validationToken == null || validationToken.equals("")
          || !validationToken.equals(quotation.getValidationToken()))
        throw new OsirisValidationException("validationToken");

      quotationService.validateQuotationFromCustomer(quotation);
      return new ResponseEntity<String>(
          mailHelper.generateGenericHtmlConfirmation("Devis validé", null, "Devis n°" + quotationId,
              "Votre validation pour le devis n°" + quotationId
                  + " a bien été pris en compte. Nous débutons immédiatement le traitement de ce dernier.",
              null, "Bonne journée !"),
          HttpStatus.OK);
    } catch (Exception e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
      return new ResponseEntity<String>(
          mailHelper.generateGenericHtmlConfirmation("Erreur !", null, "Devis n°" + quotationId,
              "Nous sommes désolé, mais une erreur est survenue lors de votre validation.",
              "Veuillez réessayer en utilisant le lien présent dans le mail de notification.", "Bonne journée !"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(inputEntryPoint + "/payment/cb/quotation/deposit")
  public ResponseEntity<String> getCardPaymentLinkForQuotationDeposit(@RequestParam Integer quotationId,
      @RequestParam String mail) {
    try {
      Quotation quotation = quotationService.getQuotation(quotationId);
      if (quotation == null)
        throw new OsirisValidationException("quotation");

      String link = quotationService.getCardPaymentLinkForQuotationDeposit(quotation, mail,
          "Paiement de l'acompte pour le devis n°" + quotationId);
      if (link.startsWith("http")) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(link));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
      } else {
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Paiement validé", null, "Devis n°" + quotationId,
                "Votre acompte pour le devis n°" + quotationId
                    + " a bien été pris en compte. Nous débutons immédiatement le traitement de ce dernier.",
                null, "Bonne journée !"),
            HttpStatus.OK);
      }
    } catch (Exception e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
      return new ResponseEntity<String>(
          mailHelper.generateGenericHtmlConfirmation("Erreur !", null, "Devis n°" + quotationId,
              "Nous sommes désolé, mais une erreur est survenue lors de votre paiement.",
              "Veuillez réessayer en utilisant le lien présent dans le mail de notification.", "Bonne journée !"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(inputEntryPoint + "/payment/cb/order/deposit")
  public ResponseEntity<String> getCardPaymentLinkForCustomerOrderDeposit(@RequestParam Integer customerOrderId,
      @RequestParam String mail) {
    try {
      CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
      if (customerOrder == null)
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Erreur !", null, "Commande inconnue",
                "Nous sommes désolé, mais ce numéro de commande est inconnue.", null, "Bonne journée !"),
            HttpStatus.INTERNAL_SERVER_ERROR);

      String link = customerOrderService.getCardPaymentLinkForCustomerOrderDeposit(customerOrder, mail,
          "Paiement de l'acompte pour la commande n°" + customerOrderId);
      if (link.startsWith("http")) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(link));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
      } else {
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Paiement validé", null, "Commande n°" + customerOrderId,
                "Votre acompte pour la commande n°" + customerOrderId
                    + " a bien été pris en compte. Nous débutons immédiatement le traitement de cette dernière.",
                null, "Bonne journée !"),
            HttpStatus.OK);
      }
    } catch (Exception e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
      return new ResponseEntity<String>(
          mailHelper.generateGenericHtmlConfirmation("Erreur !", null, "Commande n°" + customerOrderId,
              "Nous sommes désolé, mais une erreur est survenue lors de votre paiement.",
              "Veuillez réessayer en utilisant le lien présent dans le mail de notification.", "Bonne journée !"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Payment invoice

  @GetMapping(inputEntryPoint + "/payment/cb/order/invoice")
  public ResponseEntity<String> getCardPaymentLinkForPaymentInvoice(@RequestParam Integer customerOrderId,
      @RequestParam String mail) {
    try {
      CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
      if (customerOrder == null)
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Erreur !", null, "Commande inconnue",
                "Nous sommes désolé, mais ce numéro de commande est inconnue.", null, "Bonne journée !"),
            HttpStatus.INTERNAL_SERVER_ERROR);

      String link = customerOrderService.getCardPaymentLinkForPaymentInvoice(customerOrder, mail,
          "Paiement de la facture pour la commande n°" + customerOrderId);
      if (link.startsWith("http")) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(link));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
      } else {
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Paiement validé", null, "Commande n°" + customerOrderId,
                "Votre réglement pour la commande n°" + customerOrderId
                    + " a bien été pris en compte. Nous vous remercions pour votre confiance.",
                null, "Bonne journée !"),
            HttpStatus.OK);
      }
    } catch (Exception e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
      return new ResponseEntity<String>(
          mailHelper.generateGenericHtmlConfirmation("Erreur !", null, "Commande n°" + customerOrderId,
              "Nous sommes désolé, mais une erreur est survenue lors de votre paiement.",
              "Veuillez réessayer en utilisant le lien présent dans le mail de notification.", "Bonne journée !"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(inputEntryPoint + "/publication/receipt/download")
  public ResponseEntity<byte[]> downloadPublicationReceipt(@RequestParam("idAnnouncement") Integer idAnnouncement,
      @RequestParam("idProvision") Integer idProvision)
      throws OsirisValidationException, OsirisException {
    byte[] data = null;
    HttpHeaders headers = null;

    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    Provision provision = provisionService.getProvision(idProvision);

    if (announcement == null)
      throw new OsirisValidationException("Annonce non trouvée");

    File file = generatePdfDelegate.generatePublicationForAnnouncement(announcement, provision, false, true, false);

    if (file != null) {
      try {
        data = Files.readAllBytes(file.toPath());
      } catch (IOException e) {
        throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
      }

      headers = new HttpHeaders();
      headers.setContentLength(data.length);
      headers.add("filename", file.getName());
      headers.setAccessControlExposeHeaders(Arrays.asList("filename"));

      // Compute content type
      String mimeType = null;
      try {
        mimeType = Files.probeContentType(file.toPath());
      } catch (IOException e) {
        throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
      }
      if (mimeType == null)
        mimeType = "application/pdf";
      headers.set("content-type", mimeType);
      file.delete();
    }
    return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/publication/receipt/store")
  public ResponseEntity<Provision> storePublicationReceipt(@RequestParam("idAnnouncement") Integer idAnnouncement,
      @RequestParam("idProvision") Integer idProvision)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    Provision provision = provisionService.getProvision(idProvision);

    if (announcement == null)
      throw new OsirisValidationException("Annonce non trouvée");

    announcementService.generateAndStorePublicationReceipt(announcement, provision);
    return new ResponseEntity<Provision>(provision, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/proof/reading/download")
  public ResponseEntity<byte[]> downloadProofReading(@RequestParam("idAnnouncement") Integer idAnnouncement,
      @RequestParam("idProvision") Integer idProvision)
      throws OsirisValidationException, OsirisException {
    byte[] data = null;
    HttpHeaders headers = null;

    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    Provision provision = provisionService.getProvision(idProvision);

    if (announcement == null)
      throw new OsirisValidationException("Annonce non trouvée");

    File file = generatePdfDelegate.generatePublicationForAnnouncement(announcement, provision, false, false, true);

    if (file != null) {
      try {
        data = Files.readAllBytes(file.toPath());
      } catch (IOException e) {
        throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
      }

      headers = new HttpHeaders();
      headers.setContentLength(data.length);
      headers.add("filename", file.getName());
      headers.setAccessControlExposeHeaders(Arrays.asList("filename"));

      // Compute content type
      String mimeType = null;
      try {
        mimeType = Files.probeContentType(file.toPath());
      } catch (IOException e) {
        throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
      }
      if (mimeType == null)
        mimeType = "application/pdf";
      headers.set("content-type", mimeType);
      file.delete();
    }
    return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/publication/flag/download")
  public ResponseEntity<byte[]> downloadPublicationFlag(@RequestParam("idAnnouncement") Integer idAnnouncement,
      @RequestParam(name = "idProvision", required = false) Integer idProvision)
      throws OsirisValidationException, OsirisException {
    byte[] data = null;
    HttpHeaders headers = null;

    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    Provision provision = null;
    if (idProvision == null) {
      CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
      // Get provision
      if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
          for (Service service : asso.getServices())
            for (Provision orderProvision : service.getProvisions())
              if (orderProvision.getAnnouncement() != null
                  && orderProvision.getAnnouncement().getId().equals(announcement.getId())) {
                provision = orderProvision;
                break;
              }
    } else {
      provision = provisionService.getProvision(idProvision);
    }

    if (announcement == null)
      throw new OsirisValidationException("Annonce non trouvée");
    if (provision == null)
      throw new OsirisValidationException("Provision non trouvée");

    File file = generatePdfDelegate.generatePublicationForAnnouncement(announcement, provision, true, false, false);

    if (file != null) {
      try {
        data = Files.readAllBytes(file.toPath());
      } catch (IOException e) {
        throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
      }

      headers = new HttpHeaders();
      headers.setContentLength(data.length);
      headers.add("filename", file.getName());
      headers.setAccessControlExposeHeaders(Arrays.asList("filename"));

      // Compute content type
      String mimeType = null;
      try {
        mimeType = Files.probeContentType(file.toPath());
      } catch (IOException e) {
        throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
      }
      if (mimeType == null)
        mimeType = "application/pdf";
      headers.set("content-type", mimeType);
      file.delete();
    }
    return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/publication/flag/store")
  public ResponseEntity<Provision> storePublicationFlag(@RequestParam("idAnnouncement") Integer idAnnouncement,
      @RequestParam(name = "idProvision", required = false) Integer idProvision)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    Provision provision = null;
    if (idProvision == null) {
      CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
      // Get provision
      if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
          for (Service service : asso.getServices())
            for (Provision orderProvision : service.getProvisions())
              if (orderProvision.getAnnouncement() != null
                  && orderProvision.getAnnouncement().getId().equals(announcement.getId())) {
                provision = orderProvision;
                break;
              }
    } else {
      provision = provisionService.getProvision(idProvision);
    }

    if (announcement == null)
      throw new OsirisValidationException("Annonce non trouvée");
    if (provision == null)
      throw new OsirisValidationException("Provision non trouvée");

    announcementService.generateAndStorePublicationFlag(announcement, provision);

    return new ResponseEntity<Provision>(provision, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/publication/receipt")
  public ResponseEntity<CustomerOrder> generatePublicationReceiptMail(@RequestParam Integer idCustomerOrder,
      @RequestParam Integer idAnnouncement)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    if (announcement == null)
      throw new OsirisValidationException("announcement");

    MailComputeResult mailComputeResult = mailComputeHelper.computeMailForPublicationReceipt(customerOrder);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.sendPublicationReceiptToCustomer(customerOrder, true, announcement);
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail/generate/missing-attachment")
  public ResponseEntity<MissingAttachmentQuery> generateAttachmentTypeMail(@RequestBody MissingAttachmentQuery query)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {

    if (query.getAssoServiceDocument() == null || query.getAssoServiceDocument().size() == 0)
      throw new OsirisValidationException("assoServiceDocumentList");

    AssoServiceDocument asso = null;
    for (AssoServiceDocument assoServiceDocument : query.getAssoServiceDocument()) {
      asso = assoServiceDocumentService.getAssoServiceDocument(assoServiceDocument.getId());
      if (asso == null)
        throw new OsirisValidationException("assoServiceDocument");
    }

    if (asso != null) {
      MailComputeResult mailComputeResult = mailComputeHelper
          .computeMailForPublicationReceipt(asso.getService().getAssoAffaireOrder().getCustomerOrder());
      if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
        throw new OsirisValidationException("MailTo");

    }
    return new ResponseEntity<MissingAttachmentQuery>(
        missingAttachmentQueryService.sendMissingAttachmentQueryToCustomer(query, false), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/missing-attachment/reminder")
  public ResponseEntity<MissingAttachmentQuery> sendMissingAttachmentQueryImmediatly(
      @RequestParam Integer missingAttachmentQueryId)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {

    MissingAttachmentQuery query = missingAttachmentQueryService.getMissingAttachmentQuery(missingAttachmentQueryId);

    if (missingAttachmentQueryId == null)
      throw new OsirisValidationException("missingAttachmentQuery");

    return new ResponseEntity<MissingAttachmentQuery>(
        missingAttachmentQueryService.sendMissingAttachmentQueryImmediatly(query), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/publication/flag")
  public ResponseEntity<CustomerOrder> generatePublicationFlagMail(@RequestParam Integer idCustomerOrder,
      @RequestParam Integer idAnnouncement)
      throws OsirisException, OsirisValidationException, OsirisClientMessageException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idCustomerOrder);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    Announcement announcement = announcementService.getAnnouncement(idAnnouncement);
    if (announcement == null)
      throw new OsirisValidationException("announcement");

    MailComputeResult mailComputeResult = mailComputeHelper.computeMailForPublicationFlag(customerOrder);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.sendPublicationFlagToCustomer(customerOrder, true, announcement);
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/announcement")
  public ResponseEntity<CustomerOrder> getCustomerOrderOfAnnouncement(@RequestParam Integer idAnnouncement)
      throws OsirisValidationException {
    Announcement announcement = this.announcementService.getAnnouncement(idAnnouncement);

    if (announcement == null)
      throw new OsirisValidationException("announcement");

    return new ResponseEntity<CustomerOrder>(customerOrderService.getCustomerOrderForAnnouncement(announcement),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation/announcement")
  public ResponseEntity<Quotation> getQuotationOfAnnouncement(@RequestParam Integer idAnnouncement)
      throws OsirisValidationException {
    Announcement announcement = this.announcementService.getAnnouncement(idAnnouncement);

    if (announcement == null)
      throw new OsirisValidationException("announcement");

    return new ResponseEntity<Quotation>(quotationService.getQuotationForAnnouncement(announcement),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/announcements/search")
  public ResponseEntity<List<Announcement>> getAnnouncements(@RequestBody AnnouncementListSearch announcementSearch)
      throws OsirisValidationException, OsirisException {
    if (announcementSearch == null)
      throw new OsirisValidationException("paymentSearch");

    validationHelper.validateReferential(announcementSearch.getConfrere(), false, "confrere");

    return new ResponseEntity<List<Announcement>>(announcementService.searchAnnouncements(announcementSearch),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/customer-order/print/label")
  public ResponseEntity<byte[]> printMailingLabel(@RequestParam List<String> customerOrders,
      @RequestParam boolean printLabel, @RequestParam boolean printLetters)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {

    return customerOrderService.printMailingLabel(customerOrders, printLabel, printLetters);
  }

  @PostMapping(inputEntryPoint + "/dashboard/employee")
  public ResponseEntity<List<ProvisionBoardResult>> getDashboardEmployee(@RequestBody List<Employee> employees)
      throws OsirisValidationException, OsirisException {
    if (employees == null || employees.size() == 0)
      throw new OsirisValidationException("employees");

    for (Employee employee : employees)
      validationHelper.validateReferential(employee, true, "Employee");
    return new ResponseEntity<List<ProvisionBoardResult>>(provisionService.getDashboardEmployee(employees),
        HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @GetMapping(inputEntryPoint + "/announcement/actulegale")
  public ResponseEntity<Boolean> publishAnnouncementsToActuLegale()
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {

    announcementService.publishAnnouncementsToActuLegale();
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/formalite-guichet-unique/search")
  public ResponseEntity<List<FormaliteGuichetUnique>> findFormaliteGuichetUniqueServiceByReference(
      @RequestParam String value)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException {

    List<FormaliteGuichetUnique> formalites = null;

    if (value != null && value.length() > 2) {
      formalites = formaliteGuichetUniqueService.getFormaliteGuichetUniqueByLiasseNumber(value.toUpperCase());

      if (formalites == null || formalites.size() == 0)
        formalites = guichetUniqueDelegateService.getAllFormalitiesByRefenceMandataire(value);

      // Put empty partner center when id is set to null
      if (formalites != null && formalites.size() > 0)
        for (FormaliteGuichetUnique formalite : formalites)
          if (formalite.getValidationsRequests() != null && formalite.getValidationsRequests().size() > 0)
            for (ValidationRequest validationRequest : formalite.getValidationsRequests())
              if (validationRequest.getPartnerCenter() != null && validationRequest.getPartner().getId() == null)
                validationRequest.setPartnerCenter(null);
    }

    return new ResponseEntity<List<FormaliteGuichetUnique>>(formalites, HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
  @GetMapping(inputEntryPoint + "/customer-order/credit-note")
  public ResponseEntity<Boolean> generateCreditNoteForCustomerOrderInvoice(
      @RequestParam Integer customerOrderId, @RequestParam Integer invoiceId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    Invoice invoice = invoiceService.getInvoice(invoiceId);
    if (invoice == null)
      throw new OsirisValidationException("invoice");

    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    customerOrderService.generateCreditNoteForCustomerOrderInvoice(customerOrder, invoice);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ACCOUNTING)
  @GetMapping(inputEntryPoint + "/customer-order/invoicing/reinit")
  public ResponseEntity<Boolean> reinitInvoicing(@RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    if (customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED) ||
        customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED))
      throw new OsirisValidationException("customerOrder status");

    customerOrderService.reinitInvoicing(customerOrder);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

}