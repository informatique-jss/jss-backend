package com.jss.osiris.modules.quotation.controller;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.mail.model.MailComputeResult;
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
import com.jss.osiris.modules.quotation.model.AnnouncementNoticeTemplate;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.BodaccFusion;
import com.jss.osiris.modules.quotation.model.BodaccFusionAbsorbedCompany;
import com.jss.osiris.modules.quotation.model.BodaccFusionMergingCompany;
import com.jss.osiris.modules.quotation.model.BodaccPublicationType;
import com.jss.osiris.modules.quotation.model.BodaccSale;
import com.jss.osiris.modules.quotation.model.BodaccSplit;
import com.jss.osiris.modules.quotation.model.BodaccSplitBeneficiary;
import com.jss.osiris.modules.quotation.model.BodaccSplitCompany;
import com.jss.osiris.modules.quotation.model.BodaccStatus;
import com.jss.osiris.modules.quotation.model.BuildingDomiciliation;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.FundType;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.quotation.model.ProvisionType;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.model.RecordType;
import com.jss.osiris.modules.quotation.model.Rna;
import com.jss.osiris.modules.quotation.model.Siren;
import com.jss.osiris.modules.quotation.model.Siret;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.model.centralPay.CentralPayPaymentShortRequest;
import com.jss.osiris.modules.quotation.model.guichetUnique.Content;
import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.NatureCreation;
import com.jss.osiris.modules.quotation.service.ActTypeService;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.AnnouncementNoticeTemplateService;
import com.jss.osiris.modules.quotation.service.AnnouncementStatusService;
import com.jss.osiris.modules.quotation.service.AssignationTypeService;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.BodaccPublicationTypeService;
import com.jss.osiris.modules.quotation.service.BodaccStatusService;
import com.jss.osiris.modules.quotation.service.BuildingDomiciliationService;
import com.jss.osiris.modules.quotation.service.CharacterPriceService;
import com.jss.osiris.modules.quotation.service.ConfrereService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationContractTypeService;
import com.jss.osiris.modules.quotation.service.DomiciliationStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.FundTypeService;
import com.jss.osiris.modules.quotation.service.JournalTypeService;
import com.jss.osiris.modules.quotation.service.MailRedirectionTypeService;
import com.jss.osiris.modules.quotation.service.NoticeTypeFamilyService;
import com.jss.osiris.modules.quotation.service.NoticeTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionFamilyTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionScreenTypeService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.ProvisionTypeService;
import com.jss.osiris.modules.quotation.service.QuotationLabelTypeService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.quotation.service.RecordTypeService;
import com.jss.osiris.modules.quotation.service.RnaDelegateService;
import com.jss.osiris.modules.quotation.service.SireneDelegateService;
import com.jss.osiris.modules.quotation.service.TransfertFundsTypeService;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
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
  BodaccStatusService bodaccStatusService;

  @Autowired
  AnnouncementStatusService announcementStatusService;

  @Autowired
  DomiciliationStatusService domiciliationStatusService;

  @Autowired
  FormaliteStatusService formaliteStatusService;

  @Autowired
  GlobalExceptionHandler globalExceptionHandler;

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

  @GetMapping(inputEntryPoint + "/bodacc-status")
  public ResponseEntity<List<BodaccStatus>> getBodaccStatus() {
    return new ResponseEntity<List<BodaccStatus>>(bodaccStatusService.getBodaccStatus(), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail/quotation/compute")
  public ResponseEntity<MailComputeResult> computeMailForQuotation(
      @RequestBody Quotation quotation) throws OsirisException {
    return new ResponseEntity<MailComputeResult>(mailHelper.computeMailForQuotationDocument(quotation), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/mail/billing/compute")
  public ResponseEntity<MailComputeResult> computeMailForBilling(
      @RequestBody Quotation quotation) throws OsirisException {
    return new ResponseEntity<MailComputeResult>(mailHelper.computeMailForBillingDocument(quotation), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/quotation")
  public ResponseEntity<Quotation> generateQuotationMail(@RequestParam Integer quotationId)
      throws OsirisException, OsirisValidationException {
    Quotation quotation = quotationService.getQuotation(quotationId);
    if (quotation == null)
      throw new OsirisValidationException("quotation");

    MailComputeResult mailComputeResult = mailHelper.computeMailForQuotationDocument(quotation);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.generateQuotationMail(quotation);
    return new ResponseEntity<Quotation>(quotation, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/customer-order-confirmation")
  public ResponseEntity<CustomerOrder> generateCustomerOrderCreationConfirmationToCustomer(
      @RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    MailComputeResult mailComputeResult = mailHelper.computeMailForBillingDocument(customerOrder);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.generateCustomerOrderCreationConfirmationToCustomer(customerOrder);
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/customer-order-deposit-confirmation")
  public ResponseEntity<CustomerOrder> generateCustomerOrderDepositConfirmationToCustomer(
      @RequestParam Integer customerOrderId)
      throws OsirisValidationException, OsirisException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");

    MailComputeResult mailComputeResult = mailHelper.computeMailForBillingDocument(customerOrder);
    if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
      throw new OsirisValidationException("MailTo");

    mailHelper.generateCustomerOrderDepositConfirmationToCustomer(customerOrder);
    return new ResponseEntity<CustomerOrder>(customerOrder, HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail/generate/invoice")
  public ResponseEntity<CustomerOrder> generateInvoiceMail(@RequestParam Integer customerOrderId)
      throws OsirisValidationException {
    CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
    if (customerOrder == null)
      throw new OsirisValidationException("customerOrder");
    try {
      MailComputeResult mailComputeResult = mailHelper.computeMailForBillingDocument(customerOrder);
      if (mailComputeResult.getRecipientsMailTo() == null || mailComputeResult.getRecipientsMailTo().size() == 0)
        throw new OsirisValidationException("MailTo");
      customerOrderService.generateInvoiceMail(customerOrder);
    } catch (OsirisException e) {
      globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
    }
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
      @RequestParam Integer employeeId) throws OsirisValidationException {
    AssoAffaireOrder asso = assoAffaireOrderService.getAssoAffaireOrder(assoId);
    if (asso == null)
      throw new OsirisValidationException("asso");

    Employee employee = employeeService.getEmployee(employeeId);
    if (employee == null)
      throw new OsirisValidationException("employee");

    assoAffaireOrderService.updateAssignedToForAsso(asso, employee);
    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/asso/affaire/order/search")
  public ResponseEntity<ArrayList<AssoAffaireOrderSearchResult>> searchForAsso(
      @RequestBody AffaireSearch affaireSearch) throws OsirisValidationException {

    if (affaireSearch.getLabel() == null
        && affaireSearch.getAssignedTo() == null && affaireSearch.getResponsible() == null
        && affaireSearch.getStatus() == null)
      throw new OsirisValidationException("Label or AssignedTo or Responsible or Status");

    if (affaireSearch.getLabel() == null)
      affaireSearch.setLabel("");

    affaireSearch.setLabel(affaireSearch.getLabel().trim());

    return new ResponseEntity<ArrayList<AssoAffaireOrderSearchResult>>(
        assoAffaireOrderService.searchForAsso(affaireSearch), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/asso/affaire/order/update")
  public ResponseEntity<AssoAffaireOrder> addOrUpdateAssoAffaireOrder(@RequestBody AssoAffaireOrder assoAffaireOrder)
      throws OsirisValidationException, OsirisException {
    validationHelper.validateReferential(assoAffaireOrder, true, "assoAffaireOrder");
    validationHelper.validateReferential(assoAffaireOrder.getAffaire(), true, "Affaire");
    validationHelper.validateReferential(assoAffaireOrder.getAssignedTo(), true, "AssignedTo");
    validationHelper.validateReferential(assoAffaireOrder.getCustomerOrder(), true, "CustomerOrder");
    validationHelper.validateReferential(assoAffaireOrder.getCustomerOrder(), assoAffaireOrder.getQuotation() == null,
        "CustomerOrder");
    validationHelper.validateReferential(assoAffaireOrder.getQuotation(), assoAffaireOrder.getCustomerOrder() == null,
        "Quotation");

    if (assoAffaireOrder.getProvisions() == null)
      throw new OsirisValidationException("Provisions");

    IQuotation quotation = assoAffaireOrder.getCustomerOrder() != null ? assoAffaireOrder.getCustomerOrder()
        : assoAffaireOrder.getQuotation();

    boolean isOpen = false;

    if (quotation instanceof CustomerOrder) {
      CustomerOrder customerOrder = (CustomerOrder) quotation;
      isOpen = customerOrder.getCustomerOrderStatus() != null
          && customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN);
    }

    if (quotation instanceof Quotation) {
      Quotation quotationQuotation = (Quotation) quotation;
      isOpen = quotationQuotation.getQuotationStatus() != null
          && quotationQuotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN);
    }

    boolean isCustomerOrder = quotation instanceof CustomerOrder && !isOpen;

    for (Provision provision : assoAffaireOrder.getProvisions())
      validateProvision(provision, isOpen, isCustomerOrder);

    return new ResponseEntity<AssoAffaireOrder>(assoAffaireOrderService.addOrUpdateAssoAffaireOrder(assoAffaireOrder),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/asso/affaire/order")
  public ResponseEntity<AssoAffaireOrder> getAsso(@RequestParam Integer id) throws OsirisValidationException {
    if (id == null)
      throw new OsirisValidationException("Id");

    return new ResponseEntity<AssoAffaireOrder>(assoAffaireOrderService.getAssoAffaireOrder(id), HttpStatus.OK);
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

  @GetMapping(inputEntryPoint + "/bodacc-publication-types")
  public ResponseEntity<List<BodaccPublicationType>> getBodaccPublicationTypes() {
    return new ResponseEntity<List<BodaccPublicationType>>(bodaccPublicationTypeService.getBodaccPublicationTypes(),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/bodacc-publication-type")
  public ResponseEntity<BodaccPublicationType> addOrUpdateActType(
      @RequestBody BodaccPublicationType bodaccPublicationType) throws OsirisValidationException, OsirisException {
    if (bodaccPublicationType.getId() != null)
      validationHelper.validateReferential(bodaccPublicationType, true, "bodaccPublicationType");
    validationHelper.validateString(bodaccPublicationType.getCode(), true, 20, "code");
    validationHelper.validateString(bodaccPublicationType.getLabel(), true, 100, "label");

    return new ResponseEntity<BodaccPublicationType>(
        bodaccPublicationTypeService.addOrUpdateBodaccPublicationType(bodaccPublicationType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/notice-type-families")
  public ResponseEntity<List<NoticeTypeFamily>> getNoticeTypeFamilies() {
    return new ResponseEntity<List<NoticeTypeFamily>>(noticeTypeFamilyService.getNoticeTypeFamilies(), HttpStatus.OK);
  }

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

  @PostMapping(inputEntryPoint + "/notice-type")
  public ResponseEntity<NoticeType> addOrUpdateNoticeType(
      @RequestBody NoticeType noticeTypes) throws OsirisValidationException, OsirisException {
    if (noticeTypes.getId() != null)
      validationHelper.validateReferential(noticeTypes, true, "noticeTypes");
    validationHelper.validateString(noticeTypes.getCode(), true, 20, "code");
    validationHelper.validateString(noticeTypes.getLabel(), true, 100, "label");

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

    return new ResponseEntity<CharacterPrice>(characterPriceService.getCharacterPrice(department, date), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/character-prices")
  public ResponseEntity<List<CharacterPrice>> getCharacterPrices() {
    return new ResponseEntity<List<CharacterPrice>>(characterPriceService.getCharacterPrices(), HttpStatus.OK);
  }

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

  @PostMapping(inputEntryPoint + "/journal-type")
  public ResponseEntity<JournalType> addOrUpdateJournalType(
      @RequestBody JournalType journalType) throws OsirisValidationException, OsirisException {
    if (journalType.getId() != null)
      validationHelper.validateReferential(journalType, true, "journalType");
    validationHelper.validateString(journalType.getCode(), true, 20, "code");
    validationHelper.validateString(journalType.getLabel(), true, 100, "label");

    return new ResponseEntity<JournalType>(journalTypeService.addOrUpdateJournalType(journalType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/confreres")
  public ResponseEntity<List<Confrere>> getConfreres() {
    return new ResponseEntity<List<Confrere>>(confrereService.getConfreres(), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/confrere")
  public ResponseEntity<Confrere> addOrUpdateConfrere(
      @RequestBody Confrere confrere) throws OsirisValidationException, OsirisException {
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
    validationHelper.validateString(confrere.getLastShipmentForPublication(), false, 60, "LastShipmentForPublication");
    validationHelper.validateString(confrere.getBoardGrade(), false, 20, "BoardGrade");
    validationHelper.validateString(confrere.getPaperGrade(), false, 20, "PaperGrade");
    validationHelper.validateString(confrere.getBillingGrade(), false, 20, "BillingGrade");
    validationHelper.validateString(confrere.getPublicationCertificateDocumentGrade(), false, 20,
        "PublicationCertificateDocumentGrade");
    validationHelper.validateString(confrere.getMailRecipient(), false, 60, "MailRecipient");
    validationHelper.validateString(confrere.getAddress(), false, 60, "Address");
    validationHelper.validateString(confrere.getPostalCode(), false, 10, "PostalCode");
    validationHelper.validateReferential(confrere.getCity(), false, "City");
    validationHelper.validateReferential(confrere.getCountry(), false, "Country");
    validationHelper.validateString(confrere.getIban(), false, 40, "Iban");
    validationHelper.validateReferential(confrere.getVatCollectionType(), true, "VatCollectionType");
    validationHelper.validateReferential(confrere.getPaymentType(), true, "PaymentType");

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
        validationHelper.validateReferential(document.getRegie(), false, "Regie");

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

    return new ResponseEntity<Confrere>(confrereService.addOrUpdateConfrere(confrere), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/mail-redirection-types")
  public ResponseEntity<List<MailRedirectionType>> getMailRedirectionTypes() {
    return new ResponseEntity<List<MailRedirectionType>>(mailRedirectionTypeService.getMailRedirectionTypes(),
        HttpStatus.OK);
  }

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

  @GetMapping(inputEntryPoint + "/building-domiciliations")
  public ResponseEntity<List<BuildingDomiciliation>> getBuildingDomiciliations() {
    return new ResponseEntity<List<BuildingDomiciliation>>(buildingDomiciliationService.getBuildingDomiciliations(),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/building-domiciliation")
  public ResponseEntity<BuildingDomiciliation> addOrUpdateBuildingDomiciliation(
      @RequestBody BuildingDomiciliation buildingDomiciliation) throws OsirisValidationException, OsirisException {
    if (buildingDomiciliation.getId() != null)
      validationHelper.validateReferential(buildingDomiciliation, true, "buildingDomiciliation");
    validationHelper.validateString(buildingDomiciliation.getCode(), true, 20, "Code");
    validationHelper.validateString(buildingDomiciliation.getLabel(), true, 100, "Label");
    validationHelper.validateString(buildingDomiciliation.getAddress(), false, 60, "Address");
    validationHelper.validateString(buildingDomiciliation.getPostalCode(), false, 10, "PostalCode");
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

  @PostMapping(inputEntryPoint + "/domiciliation-contract-type")
  public ResponseEntity<DomiciliationContractType> addOrUpdateDomiciliationContractType(
      @RequestBody DomiciliationContractType domiciliationContractType)
      throws OsirisValidationException, OsirisException {
    if (domiciliationContractType.getId() != null)
      validationHelper.validateReferential(domiciliationContractType, true, "domiciliationContractType");
    validationHelper.validateString(domiciliationContractType.getCode(), true, 20, "code");
    validationHelper.validateString(domiciliationContractType.getLabel(), true, 100, "label");

    return new ResponseEntity<DomiciliationContractType>(
        domiciliationContractTypeService.addOrUpdateDomiciliationContractType(domiciliationContractType),
        HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/provision-types")
  public ResponseEntity<List<ProvisionType>> getProvisionTypes() {
    return new ResponseEntity<List<ProvisionType>>(provisionTypeService.getProvisionTypes(), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/provision-type")
  public ResponseEntity<ProvisionType> addOrUpdateProvisionType(
      @RequestBody ProvisionType provisionType) throws OsirisValidationException, OsirisException {
    if (provisionType.getId() != null)
      validationHelper.validateReferential(provisionType, true, "provisionType");
    validationHelper.validateString(provisionType.getCode(), true, 20, "Code");
    validationHelper.validateString(provisionType.getLabel(), true, 100, "Label");
    validationHelper.validateReferential(provisionType.getProvisionScreenType(), true, "ProvisionScreenType");
    validationHelper.validateReferential(provisionType.getAssignationType(), true, "AssignationType");
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
  public ResponseEntity<List<Siren>> getSiren(@RequestParam String siren) {
    if (siren != null && !siren.equals("") && siren.replaceAll(" ", "").length() == 9)
      return new ResponseEntity<List<Siren>>(sireneDelegateService.getSiren(siren.replaceAll(" ", "")), HttpStatus.OK);
    return new ResponseEntity<List<Siren>>(HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/siret")
  public ResponseEntity<List<Siret>> getSiret(@RequestParam String siret) {
    if (siret != null && !siret.equals("") && siret.replaceAll(" ", "").length() == 14)
      return new ResponseEntity<List<Siret>>(sireneDelegateService.getSiret(siret.replaceAll(" ", "")), HttpStatus.OK);
    return new ResponseEntity<List<Siret>>(HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/rna")
  public ResponseEntity<List<Rna>> getRna(@RequestParam String rna) throws OsirisValidationException {
    if (rna == null || rna.equals("")
        || rna.replaceAll(" ", "").length() != 10 && !rna.toUpperCase().subSequence(0, 1).equals("W"))
      throw new OsirisValidationException("rna");

    if (rna != null && !rna.equals("") && rna.replaceAll(" ", "").length() == 14
        && rna.toUpperCase().subSequence(0, 1).equals("W"))
      return new ResponseEntity<List<Rna>>(rnaDelegateService.getRna(rna.replaceAll(" ", "")), HttpStatus.OK);
    return new ResponseEntity<List<Rna>>(HttpStatus.OK);
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

  @PostMapping(inputEntryPoint + "/record-type")
  public ResponseEntity<RecordType> addOrUpdateRecordType(
      @RequestBody RecordType recordType) throws OsirisValidationException, OsirisException {
    if (recordType.getId() != null)
      validationHelper.validateReferential(recordType, true, "recordType");
    validationHelper.validateString(recordType.getCode(), true, 20, "code");
    validationHelper.validateString(recordType.getLabel(), true, 100, "label");

    return new ResponseEntity<RecordType>(recordTypeService.addOrUpdateRecordType(recordType), HttpStatus.OK);
  }

  @GetMapping(inputEntryPoint + "/quotation-label-types")
  public ResponseEntity<List<QuotationLabelType>> getQuotationLabelTypes() {
    return new ResponseEntity<List<QuotationLabelType>>(quotationLabelTypeService.getQuotationLabelTypes(),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/quotation-label-type")
  public ResponseEntity<QuotationLabelType> addOrUpdateQuotationLabelType(
      @RequestBody QuotationLabelType quotationLabelType) throws OsirisValidationException, OsirisException {
    if (quotationLabelType.getId() != null)
      validationHelper.validateReferential(quotationLabelType, true, "quotationLabelType");
    validationHelper.validateString(quotationLabelType.getCode(), true, 20, "code");
    validationHelper.validateString(quotationLabelType.getLabel(), true, 100, "label");

    return new ResponseEntity<QuotationLabelType>(
        quotationLabelTypeService.addOrUpdateQuotationLabelType(quotationLabelType), HttpStatus.OK);
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
      throws OsirisValidationException, OsirisException {
    validateQuotationAndCustomerOrder(quotation);

    QuotationStatus openQuotationStatus = quotationStatusService.getQuotationStatusByCode(QuotationStatus.OPEN);
    if (openQuotationStatus == null)
      if (quotation.getQuotationStatus() == null)
        throw new OsirisException("OPEN Quotation status not found");

    if (quotation.getQuotationStatus() == null)
      quotation.setQuotationStatus(openQuotationStatus);
    return new ResponseEntity<Quotation>(quotationService.addOrUpdateQuotationFromUser(quotation), HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/order/search")
  public ResponseEntity<List<OrderingSearchResult>> searchOrders(@RequestBody OrderingSearch orderingSearch)
      throws OsirisValidationException, OsirisException {
    if (orderingSearch == null)
      throw new OsirisValidationException("orderingSearch");

    if (orderingSearch.getStartDate() == null || orderingSearch.getEndDate() == null)
      throw new OsirisValidationException("StartDate or EndDate");

    Duration duration = Duration.between(orderingSearch.getStartDate(), orderingSearch.getEndDate());

    if (duration.toDays() > 366)
      throw new OsirisValidationException("Duration");

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

    if (quotationSearch.getStartDate() == null || quotationSearch.getEndDate() == null)
      throw new OsirisValidationException("StartDate or EndDate");

    Duration duration = Duration.between(quotationSearch.getStartDate(), quotationSearch.getEndDate());

    if (duration.toDays() > 366)
      throw new OsirisValidationException("Duration");

    validationHelper.validateReferential(quotationSearch.getSalesEmployee(), false, "SalesEmployee");
    if (quotationSearch.getQuotationStatus() != null)
      for (QuotationStatus status : quotationSearch.getQuotationStatus())
        validationHelper.validateReferential(status, false, "status");

    return new ResponseEntity<List<QuotationSearchResult>>(quotationService.searchQuotations(quotationSearch),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order")
  public ResponseEntity<CustomerOrder> addOrUpdateCustomerOrder(@RequestBody CustomerOrder customerOrder)
      throws OsirisException, OsirisValidationException {
    validateQuotationAndCustomerOrder(customerOrder);
    CustomerOrderStatus customerOrderStatus = customerOrderStatusService
        .getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN);
    if (customerOrderStatus == null)
      if (customerOrder.getCustomerOrderStatus() == null)
        throw new OsirisException("OPEN Customer Order status not found");

    if (customerOrder.getCustomerOrderStatus() == null)
      customerOrder.setCustomerOrderStatus(customerOrderStatus);

    return new ResponseEntity<CustomerOrder>(customerOrderService.addOrUpdateCustomerOrderFromUser(customerOrder),
        HttpStatus.OK);
  }

  @PostMapping(inputEntryPoint + "/customer-order/status")
  public ResponseEntity<CustomerOrder> addOrUpdateCustomerOrderStatus(@RequestBody CustomerOrder customerOrder,
      @RequestParam String targetStatusCode) throws OsirisValidationException, OsirisException {
    validateQuotationAndCustomerOrder(customerOrder);
    customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
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
      @RequestParam String targetStatusCode) throws OsirisValidationException, OsirisException {
    validateQuotationAndCustomerOrder(quotation);
    quotation = quotationService.getQuotation(quotation.getId());
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
      throws OsirisValidationException, OsirisException {
    boolean isOpen = false;

    if (quotation.getIsCreatedFromWebSite() == null)
      quotation.setIsCreatedFromWebSite(false);

    if (quotation instanceof CustomerOrder) {
      CustomerOrder customerOrder = (CustomerOrder) quotation;
      isOpen = customerOrder.getCustomerOrderStatus() == null ||
          customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.OPEN);
    }

    if (quotation instanceof Quotation) {
      Quotation quotationQuotation = (Quotation) quotation;
      isOpen = quotationQuotation.getQuotationStatus() == null ||
          quotationQuotation.getQuotationStatus().getCode().equals(QuotationStatus.OPEN);
    }

    boolean isCustomerOrder = quotation instanceof CustomerOrder && !isOpen;

    if (quotation.getSpecialOffers() != null && quotation.getSpecialOffers().size() > 0)
      for (SpecialOffer specialOffer : quotation.getSpecialOffers())
        validationHelper.validateReferential(specialOffer, false, "specialOffer");
    quotation.setTiers((Tiers) validationHelper.validateReferential(quotation.getTiers(), false, "Tiers"));
    quotation.setResponsable(
        (Responsable) validationHelper.validateReferential(quotation.getResponsable(), false, "Responsable"));

    if (quotation.getTiers() != null && quotation.getConfrere() == null && quotation.getResponsable() == null
        && quotation.getTiers().getIsIndividual() == false)
      throw new OsirisValidationException("Tiers must be individual !");

    quotation.setConfrere((Confrere) validationHelper.validateReferential(quotation.getConfrere(), false, "Confrere"));
    validationHelper.validateReferential(quotation.getQuotationLabelType(), true, "QuotationLabelType");
    validationHelper.validateReferential(quotation.getCustomLabelResponsable(), false, "CustomLabelResponsable");
    validationHelper.validateReferential(quotation.getCustomLabelTiers(), false, "CustomLabelTiers");
    validationHelper.validateReferential(quotation.getRecordType(), true, "RecordType");

    if (quotation.getResponsable() == null && quotation.getTiers() == null && quotation.getConfrere() == null)
      throw new OsirisValidationException("No customer order");

    if (quotation.getAssoAffaireOrders() == null || quotation.getAssoAffaireOrders().size() == 0)
      throw new OsirisValidationException("No affaire");

    if (quotation.getAssoAffaireOrders().get(0).getAffaire() == null
        || quotation.getAssoAffaireOrders().get(0).getProvisions() == null
        || quotation.getAssoAffaireOrders().get(0).getProvisions().size() == 0)
      throw new OsirisValidationException("No provision");

    if (quotation.getDocuments() != null && quotation.getDocuments().size() > 0) {
      for (Document document : quotation.getDocuments()) {

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

    for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders()) {
      if (assoAffaireOrder.getAffaire() == null)
        throw new OsirisValidationException("Affaire");
      validationHelper.validateReferential(assoAffaireOrder.getAffaire(), true, "Affaire");
      for (Provision provision : assoAffaireOrder.getProvisions()) {
        validateProvision(provision, isOpen, isCustomerOrder);
      }
    }

    if (quotation.getAssoAffaireOrders().size() > 1) {
      if (quotation.getQuotationLabelType().getId().equals(constantService.getBillingLabelTypeCodeAffaire().getId()))
        throw new OsirisValidationException("To many affaire");
      Document quotationDocument = documentService.getQuotationDocument(quotation.getDocuments());
      if (quotationDocument != null && quotationDocument.getIsRecipientAffaire())
        throw new OsirisValidationException("To many affaire");
      Document billingDocument = documentService.getBillingDocument(quotation.getDocuments());
      if (billingDocument != null && billingDocument.getIsRecipientAffaire())
        throw new OsirisValidationException("To many affaire");
    }

  }

  private void validateProvision(Provision provision, boolean isOpen, boolean isCustomerOrder)
      throws OsirisValidationException, OsirisException {

    // Domiciliation
    if (provision.getDomiciliation() != null) {
      Domiciliation domiciliation = provision.getDomiciliation();
      validationHelper.validateReferential(domiciliation.getDomiciliationContractType(), !isOpen,
          "DomiciliationContractType");
      validationHelper.validateReferential(domiciliation.getLanguage(), !isOpen, "Language");
      validationHelper.validateReferential(domiciliation.getBuildingDomiciliation(), !isOpen, "BuildingDomiciliation");
      validationHelper.validateReferential(domiciliation.getMailRedirectionType(), !isOpen, "MailRedirectionType");
      validationHelper.validateString(domiciliation.getAddress(), false, 60, "Address");
      validationHelper.validateString(domiciliation.getPostalCode(), false, 10, "PostalCode");
      validationHelper.validateString(domiciliation.getMailRecipient(), false, 60, "MailRecipient");
      validationHelper.validateString(domiciliation.getActivityAddress(), false, 60, "ActivityAddress");
      validationHelper.validateReferential(domiciliation.getCity(), false, "City");
      validationHelper.validateReferential(domiciliation.getCountry(), false, "Country");
      validationHelper.validateString(domiciliation.getAccountingRecordDomiciliation(), isCustomerOrder, 60,
          "AccountingRecordDomiciliation");

      if (domiciliation.isLegalPerson()) {
        if ((domiciliation.getLegalGardianSiren() == null
            || !validationHelper.validateSiren(domiciliation.getLegalGardianSiren())))
          throw new OsirisValidationException("LegalGardianSiren");
        validationHelper.validateString(domiciliation.getLegalGardianDenomination(), isCustomerOrder, 60,
            "LegalGardianDenomination");
        validationHelper.validateReferential(domiciliation.getLegalGardianLegalForm(), isCustomerOrder,
            "LegalGardianLegalForm");
      } else {
        validationHelper.validateReferential(domiciliation.getLegalGardianCivility(), isCustomerOrder,
            "LegalGardianCivility");
        validationHelper.validateString(domiciliation.getLegalGardianFirstname(), isCustomerOrder, 20,
            "LegalGardianFirstname");
        validationHelper.validateString(domiciliation.getLegalGardianLastname(), isCustomerOrder, 20,
            "LegalGardianLastname");
        validationHelper.validateDateMax(domiciliation.getLegalGardianBirthdate(), isCustomerOrder, LocalDate.now(),
            "LegalGardianBirthdate");
        validationHelper.validateString(domiciliation.getLegalGardianPlaceOfBirth(), isCustomerOrder, 60,
            "LegalGardianPlaceOfBirth");
        validationHelper.validateString(domiciliation.getLegalGardianJob(), isCustomerOrder, 30, "LegalGardianJob");
      }

      validationHelper.validateString(domiciliation.getLegalGardianMailRecipient(), isCustomerOrder, 60,
          "LegalGardianMailRecipient");
      validationHelper.validateString(domiciliation.getLegalGardianAddress(), isCustomerOrder, 60,
          "LegalGardianAddress");
      if (domiciliation.getCountry() != null && domiciliation.getCountry().getCode().equals("FR"))
        validationHelper.validateString(domiciliation.getLegalGardianPostalCode(), isCustomerOrder, 10,
            "LegalGardianPostalCode");
      validationHelper.validateReferential(domiciliation.getLegalGardianCity(), isCustomerOrder, "LegalGardianCity");
      validationHelper.validateReferential(domiciliation.getLegalGardianCountry(), isCustomerOrder,
          "LegalGardianCountry");

    }
    // Announcement
    if (provision.getAnnouncement() != null) {
      Announcement announcement = provision.getAnnouncement();
      validationHelper.validateDateMin(announcement.getPublicationDate(), true, LocalDate.now().minusDays(1),
          "PublicationDate");
      validationHelper.validateReferential(announcement.getDepartment(), !isOpen, "Department");
      validationHelper.validateReferential(announcement.getConfrere(), isCustomerOrder, "Confrere");
      validationHelper.validateReferential(announcement.getNoticeTypeFamily(), isCustomerOrder, "NoticeTypeFamily");
      if (isCustomerOrder && (announcement.getNoticeTypes() == null || announcement.getNoticeTypes().size() == 0))
        throw new OsirisValidationException("NoticeTypes");

      if (announcement.getNoticeTypes() != null)
        for (NoticeType noticeType : announcement.getNoticeTypes()) {
          validationHelper.validateReferential(noticeType, isCustomerOrder, "noticeType");
        }
      validationHelper.validateString(announcement.getNotice(), !isOpen, "Notice");
    }

    // BODACC
    if (provision.getBodacc() != null) {
      Bodacc bodacc = provision.getBodacc();
      validationHelper.validateReferential(bodacc.getPaymentType(), false, "PaymentType");
      validationHelper.validateReferential(bodacc.getBodaccPublicationType(), !isOpen, "BodaccPublicationType");
      validationHelper.validateReferential(bodacc.getTransfertFundsType(), false, "TransfertFundsType");

      if (bodacc.getBodaccSale() != null) {
        BodaccSale bodaccSale = bodacc.getBodaccSale();

        validationHelper.validateString(bodaccSale.getDivestedBusinessAddress(), false, 100, "DivestedBusinessAddress");
        validationHelper.validateReferential(bodaccSale.getFundType(), isCustomerOrder, "FundType");
        validationHelper.validateString(bodaccSale.getOwnerFirstname(), false, 30, "OwnerFirstname");
        validationHelper.validateString(bodaccSale.getOwnerLastname(), false, 30, "OwnerLastname");
        validationHelper.validateString(bodaccSale.getOwnerDenomination(), false, 60, "OwnerDenomination");
        validationHelper.validateString(bodaccSale.getOwnerSiren(), false, 9, "OwnerSiren");
        validationHelper.validateSiren(bodaccSale.getOwnerSiren());
        validationHelper.validateString(bodaccSale.getOwnerAddress(), isCustomerOrder, 100, "OwnerAddress");
        validationHelper.validateString(bodaccSale.getOwnerAbbreviation(), false, 20, "OwnerAbbreviation");
        validationHelper.validateString(bodaccSale.getOwnerBusinessName(), false, 60, "OwnerBusinessName");
        validationHelper.validateReferential(bodaccSale.getOwnerLegalForm(), false, "OwnerLegalForm");
        validationHelper.validateString(bodaccSale.getPurchaserFirstname(), false, 30, "PurchaserFirstname");
        validationHelper.validateString(bodaccSale.getPurchaserLastname(), false, 30, "PurchaserLastname");
        validationHelper.validateString(bodaccSale.getPurchaserDenomination(), false, 60, "PurchaserDenomination");
        validationHelper.validateString(bodaccSale.getPurchaserSiren(), false, 9, "PurchaserSiren");
        validationHelper.validateSiren(bodaccSale.getPurchaserSiren());
        validationHelper.validateString(bodaccSale.getPurchaserBusinessName(), false, 60, "PurchaserBusinessName");
        validationHelper.validateString(bodaccSale.getPurchaserAbbreviation(), false, 20, "PurchaserAbbreviation");
        validationHelper.validateReferential(bodaccSale.getPurchaserLegalForm(), false, "PurchaserLegalForm");
        validationHelper.validateDate(bodaccSale.getPurchaserActivityStartDate(), isCustomerOrder,
            "PurchaserActivityStartDate");
        validationHelper.validateDate(bodaccSale.getDeedDate(), isCustomerOrder, "DeedDate");
        validationHelper.validateDate(bodaccSale.getRegistrationDate(), isCustomerOrder, "RegistrationDate");
        validationHelper.validateReferential(bodaccSale.getRegistrationAuthority(), isCustomerOrder,
            "RegistrationAuthority");
        validationHelper.validateString(bodaccSale.getRegistrationReferences(), false, 50, "RegistrationReferences");
        validationHelper.validateReferential(bodaccSale.getActType(), isCustomerOrder, "ActType");
        validationHelper.validateString(bodaccSale.getWritor(), false, 60, "Writor");
        validationHelper.validateString(bodaccSale.getWritorAddress(), false, 100, "WritorAddress");
        validationHelper.validateString(bodaccSale.getValidityObjectionAddress(), isCustomerOrder, 100,
            "ValidityObjectionAddress");
        validationHelper.validateString(bodaccSale.getMailObjectionAddress(), false, 100, "MailObjectionAddress");
        validationHelper.validateDate(bodaccSale.getLeaseResilisationDate(),
            isCustomerOrder
                && constantService.getTransfertFundsTypeBail().getId().equals(bodacc.getTransfertFundsType().getId()),
            "LeaseResilisationDate");
        validationHelper.validateString(bodaccSale.getLeaseAddress(), false, 100, "LeaseAddress");
        validationHelper.validateString(bodaccSale.getTenantFirstname(), false, 30, "TenantFirstname");
        validationHelper.validateString(bodaccSale.getTenantLastname(), false, 30, "TenantLastname");
        validationHelper.validateString(bodaccSale.getTenantAddress(), isCustomerOrder, 100, "TenantAddress");
        validationHelper.validateString(bodaccSale.getTenantDenomination(), false, 60, "TenantDenomination");
        validationHelper.validateString(bodaccSale.getTenantSiren(), false, 9, "TenantSiren");
        validationHelper.validateSiren(bodaccSale.getTenantSiren());
        validationHelper.validateString(bodaccSale.getTenantBusinessName(), false, 60, "TenantBusinessName");
        validationHelper.validateString(bodaccSale.getTenantAbbreviation(), false, 20, "TenantAbbreviation");
        validationHelper.validateReferential(bodaccSale.getTenantLegalForm(), false, "TenantLegalForm");

      }

      if (bodacc.getBodaccFusion() != null) {
        BodaccFusion bodaccFusion = bodacc.getBodaccFusion();

        if (bodaccFusion.getBodaccFusionAbsorbedCompanies() == null
            || bodaccFusion.getBodaccFusionAbsorbedCompanies().size() == 0)
          throw new OsirisValidationException("BodaccFusionAbsorbedCompanies");

        for (BodaccFusionAbsorbedCompany bodaccFusionAbsorbedCompany : bodaccFusion
            .getBodaccFusionAbsorbedCompanies()) {
          validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanyDenomination(), isCustomerOrder,
              60, "AbsorbedCompanyDenomination");
          validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren(), isCustomerOrder, 9,
              "AbsorbedCompanySiren");
          validationHelper.validateSiren(bodaccFusionAbsorbedCompany.getAbsorbedCompanySiren());
          validationHelper.validateString(bodaccFusionAbsorbedCompany.getAbsorbedCompanyAddress(), isCustomerOrder, 100,
              "AbsorbedCompanyAddress");
          validationHelper.validateReferential(bodaccFusionAbsorbedCompany.getAbsorbedCompanyLegalForm(), false,
              "AbsorbedCompanyLegalForm");
          validationHelper.validateDate(bodaccFusionAbsorbedCompany.getAbsorbedCompanyRcsDeclarationDate(),
              isCustomerOrder, "AbsorbedCompanyRcsDeclarationDate");
          validationHelper.validateReferential(bodaccFusionAbsorbedCompany.getAbsorbedCompanyRcsCompetentAuthority(),
              isCustomerOrder, "AbsorbedCompanyRcsCompetentAuthority");
        }

        if (bodaccFusion.getBodaccFusionMergingCompanies() == null
            || bodaccFusion.getBodaccFusionMergingCompanies().size() == 0)
          throw new OsirisValidationException("BodaccFusionMergingCompanies");

        for (BodaccFusionMergingCompany bodaccFusionMergingCompany : bodaccFusion
            .getBodaccFusionMergingCompanies()) {
          validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanyDenomination(), isCustomerOrder,
              60, "MergingCompanyDenomination");
          validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanySiren(), isCustomerOrder, 9,
              "MergingCompanySiren");
          validationHelper.validateSiren(bodaccFusionMergingCompany.getMergingCompanySiren());
          validationHelper.validateString(bodaccFusionMergingCompany.getMergingCompanyAddress(), isCustomerOrder, 100,
              "MergingCompanyAddress");
          validationHelper.validateReferential(bodaccFusionMergingCompany.getMergingCompanyLegalForm(), false,
              "MergingCompanyLegalForm");
          validationHelper.validateDate(bodaccFusionMergingCompany.getMergingCompanyRcsDeclarationDate(),
              isCustomerOrder, "MergingCompanyRcsDeclarationDate");
          validationHelper.validateReferential(bodaccFusionMergingCompany.getMergingCompanyRcsCompetentAuthority(),
              isCustomerOrder, "MergingCompanyRcsCompetentAuthority");
        }
      }

      if (bodacc.getBodaccSplit() != null) {
        BodaccSplit bodaccSplit = bodacc.getBodaccSplit();

        if (bodaccSplit.getBodaccSplitBeneficiaries() == null
            || bodaccSplit.getBodaccSplitBeneficiaries().size() == 0)
          throw new OsirisValidationException("BodaccSplitBeneficiaries");

        for (BodaccSplitBeneficiary bodaccSplitBeneficiary : bodaccSplit.getBodaccSplitBeneficiaries()) {
          validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanyDenomination(), isCustomerOrder,
              60, "BeneficiaryCompanyDenomination");
          validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanySiren(), isCustomerOrder, 9,
              "BeneficiaryCompanySiren");
          validationHelper.validateSiren(bodaccSplitBeneficiary.getBeneficiaryCompanySiren());
          validationHelper.validateString(bodaccSplitBeneficiary.getBeneficiaryCompanyAddress(), isCustomerOrder, 100,
              "BeneficiaryCompanyAddress");
          validationHelper.validateReferential(bodaccSplitBeneficiary.getBeneficiaryCompanyLegalForm(), false,
              "BeneficiaryCompanyLegalForm");
          validationHelper.validateDate(bodaccSplitBeneficiary.getBeneficiaryCompanyRcsDeclarationDate(),
              isCustomerOrder, "BeneficiaryCompanyRcsDeclarationDate");
          validationHelper.validateReferential(bodaccSplitBeneficiary.getBeneficiaryCompanyRcsCompetentAuthority(),
              true, "BeneficiaryCompanyRcsCompetentAuthority");
        }

        if (bodaccSplit.getBodaccSplitCompanies() == null || bodaccSplit.getBodaccSplitCompanies().size() == 0)
          throw new OsirisValidationException("BodaccSplitCompanies");
        for (BodaccSplitCompany bodaccSplitCompany : bodaccSplit.getBodaccSplitCompanies()) {
          validationHelper.validateString(bodaccSplitCompany.getSplitCompanyDenomination(), isCustomerOrder, 60,
              "SplitCompanyDenomination");
          validationHelper.validateString(bodaccSplitCompany.getSplitCompanySiren(), isCustomerOrder, 9,
              "SplitCompanySiren");
          validationHelper.validateSiren(bodaccSplitCompany.getSplitCompanySiren());
          validationHelper.validateString(bodaccSplitCompany.getSplitCompanyAddress(), isCustomerOrder, 100,
              "SplitCompanyAddress");
          validationHelper.validateReferential(bodaccSplitCompany.getSplitCompanyLegalForm(), false,
              "SplitCompanyLegalForm");
          validationHelper.validateDate(bodaccSplitCompany.getSplitCompanyRcsDeclarationDate(), isCustomerOrder,
              "SplitCompanyRcsDeclarationDate");
          validationHelper.validateReferential(bodaccSplitCompany.getSplitCompanyRcsCompetentAuthority(),
              isCustomerOrder, "SplitCompanyRcsCompetentAuthority");
        }
      }

      validationHelper.validateDateMin(bodacc.getDateOfPublication(), false, LocalDate.now(), "DateOfPublication");
    }

    // Formalite
    if (provision.getFormalite() != null) {
      Formalite formalite = provision.getFormalite();
      validationHelper.validateReferential(formalite.getTypePersonne(), true, "TypePersonne");
      validationHelper.validateString(formalite.getCompanyName(),
          !constantService.getTypePersonnePersonnePhysique().getCode().equals(formalite.getTypePersonne().getCode()),
          255, "CompanyName");
      validationHelper.validateReferential(formalite.getTypeFormalite(), true, "TypeFormalite");
      validationHelper.validateReferential(formalite.getDiffusionINSEE(),
          constantService.getTypePersonnePersonnePhysique().getCode().equals(formalite.getTypePersonne().getCode()),
          "DiffusionINSEE");
      validationHelper.validateReferential(formalite.getFormeJuridique(),
          constantService.getTypePersonnePersonneMorale().getCode().equals(formalite.getTypePersonne().getCode()),
          "FormeJuridique");

      if (formalite.getContent() == null)
        throw new OsirisValidationException("Content");

      Content content = formalite.getContent();

      validationHelper.validateReferential(content.getSuccursaleOuFiliale(),
          constantService.getTypePersonnePersonneMorale().getCode().equals(formalite.getTypePersonne().getCode())
              && constantService.getTypeFormaliteCreation().getCode().equals(formalite.getTypeFormalite().getCode()),
          "SuccursaleOuFiliale");
      validationHelper.validateReferential(content.getFormeExerciceActivitePrincipale(), true,
          "FormeExerciceActivitePrincipale");
      validationHelper.validateReferential(content.getNatureCessation(),
          formalite.getTypeFormalite().getCode().equals(constantService.getTypeFormaliteCessation().getCode()),
          "NatureCessation");
      validationHelper.validateString(content.getEvenementCessation(),
          formalite.getTypeFormalite().getCode().equals(constantService.getTypeFormaliteCessation().getCode()), 255,
          "EvenementCessation");
      validationHelper.validateString(content.getTvaIntraCommunautaire(),
          content.getNatureCreation() != null && content.getNatureCreation().getSocieteEtrangere(), 20,
          "TvaIntraCommunautaire");

      if (content.getNatureCreation() == null)
        throw new OsirisValidationException("NatureCreation");

      NatureCreation natureCreation = content.getNatureCreation();

      validationHelper.validateReferential(natureCreation.getFormeJuridique(), true, "FormeJuridique");
      validationHelper.validateReferential(natureCreation.getTypeExploitation(),
          formalite.getTypePersonne().getCode().equals(constantService.getTypePersonneExploitation().getCode()),
          "TypeExploitation");
    }

  }

  @PostMapping(inputEntryPoint + "/affaire")
  public ResponseEntity<Affaire> addOrUpdateAffaire(@RequestBody Affaire affaire)
      throws OsirisValidationException, OsirisException {
    validationHelper.validateString(affaire.getAddress(), true, 60, "Address");
    validationHelper.validateReferential(affaire.getCity(), true, "City");
    validationHelper.validateReferential(affaire.getCountry(), true, "Country");
    validationHelper.validateString(affaire.getExternalReference(), false, 60, "ExternalReference");
    if (affaire.getCountry() != null && affaire.getCountry().getId().equals(constantService.getCountryFrance().getId()))
      validationHelper.validateString(affaire.getPostalCode(), true, 10, "PostalCode");

    if (affaire.getIsIndividual()) {
      validationHelper.validateReferential(affaire.getCivility(), true, "Civility");
      validationHelper.validateString(affaire.getFirstname(), true, 40, "Firstname");
      validationHelper.validateString(affaire.getLastname(), true, 40, "Lastname");

    } else {
      validationHelper.validateString(affaire.getDenomination(), true, 150, "Denomination");
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

  @PostMapping(inputEntryPoint + "/invoice-item/generate")
  public ResponseEntity<IQuotation> generateInvoiceItemForQuotation(@RequestBody Quotation quotation)
      throws OsirisException {
    return new ResponseEntity<IQuotation>(quotationService.getAndSetInvoiceItemsForQuotation(quotation, false),
        HttpStatus.OK);
  }

  // Payment deposit

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

  @RequestMapping(path = inputEntryPoint
      + "/payment/cb/quotation/deposit/validate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<String> validateCardPaymentLinkForQuotationDeposit(CentralPayPaymentShortRequest paramMap,
      @RequestParam Integer quotationId) {

    try {
      Quotation quotation = quotationService.getQuotation(quotationId);
      if (quotation == null)
        throw new OsirisValidationException("quotation");

      Boolean status = quotationService.validateCardPaymentLinkForQuotationDeposit(quotation);

      if (status) {
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Paiement validé", null, "Devis n°" + quotationId,
                "Votre acompte pour le devis n°" + quotationId
                    + " a bien été pris en compte. Nous débutons immédiatement le traitement de ce dernier.",
                null, "Bonne journée !"),
            HttpStatus.OK);
      } else {
        throw new Exception();
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
        throw new OsirisValidationException("customerOrder");

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

  @RequestMapping(path = inputEntryPoint
      + "/payment/cb/order/deposit/validate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<String> validateCardPaymentLinkForCustomerOrderDeposit(CentralPayPaymentShortRequest paramMap,
      @RequestParam Integer customerOrderId) {

    try {
      CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
      if (customerOrder == null)
        throw new OsirisValidationException("customerOrder");

      Boolean status = customerOrderService.validateCardPaymentLinkForCustomerOrderDeposit(customerOrder);

      if (status) {
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Paiement validé", null, "Commande n°" + customerOrderId,
                "Votre acompte pour la commande n°" + customerOrderId
                    + " a bien été pris en compte. Nous débutons immédiatement le traitement de cette dernière.",
                null, "Bonne journée !"),
            HttpStatus.OK);
      } else {
        throw new Exception();
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
        throw new OsirisValidationException("customerOrder");

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

  @RequestMapping(path = inputEntryPoint
      + "/payment/cb/order/invoice/validate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<String> validateCardPaymentLinkForInvoice(CentralPayPaymentShortRequest paramMap,
      @RequestParam Integer customerOrderId) {

    try {
      CustomerOrder customerOrder = customerOrderService.getCustomerOrder(customerOrderId);
      if (customerOrder == null)
        throw new OsirisValidationException("customerOrder");

      Boolean status = customerOrderService.validateCardPaymentLinkForInvoice(customerOrder);

      if (status) {
        return new ResponseEntity<String>(
            mailHelper.generateGenericHtmlConfirmation("Paiement validé", null, "Commande n°" + customerOrderId,
                "Votre réglement pour la commande n°" + customerOrderId
                    + " a bien été pris en compte. Nous vous remercions pour votre confiance.",
                null, "Bonne journée !"),
            HttpStatus.OK);
      } else {
        throw new Exception();
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
}