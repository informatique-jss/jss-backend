package com.jss.osiris.modules.miscellaneous.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.RefundService;
import com.jss.osiris.modules.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.model.Gift;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.model.Regie;
import com.jss.osiris.modules.miscellaneous.model.Region;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.model.VatCollectionType;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.miscellaneous.service.BillingTypeService;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.CivilityService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityTypeService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.CountryService;
import com.jss.osiris.modules.miscellaneous.service.DeliveryServiceService;
import com.jss.osiris.modules.miscellaneous.service.DepartmentService;
import com.jss.osiris.modules.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.miscellaneous.service.GiftService;
import com.jss.osiris.modules.miscellaneous.service.LanguageService;
import com.jss.osiris.modules.miscellaneous.service.LegalFormService;
import com.jss.osiris.modules.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.miscellaneous.service.ProviderService;
import com.jss.osiris.modules.miscellaneous.service.RegieService;
import com.jss.osiris.modules.miscellaneous.service.RegionService;
import com.jss.osiris.modules.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.miscellaneous.service.VatCollectionTypeService;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.miscellaneous.service.WeekDayService;
import com.jss.osiris.modules.pao.model.Journal;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.SimpleProvision;
import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@RestController
public class MiscellaneousController {

    private static final String inputEntryPoint = "/miscellaneous";

    @Autowired
    AttachmentTypeService attachmentTypeService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    DocumentTypeService documentTypeService;

    @Autowired
    LegalFormService legalFormService;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    WeekDayService weekDayService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    CompetentAuthorityTypeService competentAuthorityTypeService;

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    CivilityService civilityService;

    @Autowired
    CountryService countryService;

    @Autowired
    DeliveryServiceService deliveryServiceService;

    @Autowired
    LanguageService languageService;

    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    RegionService regionService;

    @Autowired
    VatService vatService;

    @Autowired
    BillingItemService billingItemService;

    @Autowired
    BillingTypeService billingTypeService;

    @Autowired
    CityService cityService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    SpecialOfferService specialOfferService;

    @Autowired
    GiftService giftService;

    @Autowired
    VatCollectionTypeService vatCollectionTypeService;

    @Autowired
    ProviderService providerService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    RegieService regieService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    ConstantService constantService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    RefundService refundService;

    @GetMapping(inputEntryPoint + "/notifications")
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam Boolean displayFuture) {
        return new ResponseEntity<List<Notification>>(
                notificationService.getNotificationsForCurrentEmployee(displayFuture), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/notification/personnal")
    public ResponseEntity<Notification> addPOrUpdatePersonnalNotification(
            @RequestBody Notification notifications) {
        return new ResponseEntity<Notification>(notificationService
                .addOrUpdatePersonnalNotification(notifications), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/notification")
    public ResponseEntity<Notification> addOrUpdateNotification(
            @RequestBody Notification notifications) throws OsirisValidationException, OsirisException {
        boolean isRead = notifications.getIsRead();
        // You can only modify read property ;)
        notifications = (Notification) validationHelper.validateReferential(notifications, true, "notifications");
        notifications.setIsRead(isRead);
        return new ResponseEntity<Notification>(notificationService
                .addOrUpdateNotificationFromUser(notifications), HttpStatus.OK);
    }

    @DeleteMapping(inputEntryPoint + "/notification")
    public ResponseEntity<Boolean> deleteNotification(
            @RequestParam Integer notificationId) throws OsirisValidationException {
        if (notificationId == null)
            throw new OsirisValidationException("notificationId");

        Notification notification = notificationService.getNotification(notificationId);

        if (notification == null)
            throw new OsirisValidationException("notification");

        List<Employee> backupEmployee = employeeService
                .getMyHolidaymaker(employeeService.getCurrentEmployee());

        if (notification.getNotificationType().equals(Notification.PERSONNAL)) {
            backupEmployee = new ArrayList<Employee>();
            backupEmployee.add(employeeService.getCurrentEmployee());
        }

        boolean found = false;

        for (Employee employee : backupEmployee)
            if (notification.getEmployee().getId().equals(employee.getId())) {
                found = true;
            }
        if (!found)
            throw new OsirisValidationException("employee");

        notificationService.deleteNotification(notification);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/constants")
    public ResponseEntity<Constant> getConstants() throws OsirisException {
        return new ResponseEntity<Constant>(constantService.getConstants(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/constant")
    public ResponseEntity<Constant> addOrUpdateConstant(
            @RequestBody Constant constant) throws OsirisException, OsirisValidationException {
        if (constant.getId() != null)
            validationHelper.validateReferential(constant, true, "constant");
        validationHelper.validateReferential(constant.getBillingLabelTypeCodeAffaire(), true,
                "BillingLabelTypeCodeAffaire");
        validationHelper.validateReferential(constant.getBillingLabelTypeCodeAffaire(), true,
                "BillingLabelTypeCodeAffaire");
        validationHelper.validateReferential(constant.getBillingLabelTypeOther(), true, "BillingLabelTypeOther");
        validationHelper.validateReferential(constant.getBillingLabelTypeCustomer(), true,
                "BillingLabelTypeCustomer");
        validationHelper.validateReferential(constant.getAccountingJournalSales(), true, "AccountingJournalSales");
        validationHelper.validateReferential(constant.getAccountingJournalPurchases(), true,
                "AccountingJournalPurchases");
        validationHelper.validateReferential(constant.getAccountingJournalANouveau(), true,
                "AccountingJournalANouveau");
        validationHelper.validateReferential(constant.getTiersTypeProspect(), true, "TiersTypeProspect");
        validationHelper.validateReferential(constant.getTiersTypeClient(), true, "TiersTypeClient");
        validationHelper.validateReferential(constant.getDocumentTypePublication(), true,
                "DocumentTypePublication");
        validationHelper.validateReferential(constant.getDocumentTypeCfe(), true, "DocumentTypeCfe");
        validationHelper.validateReferential(constant.getDocumentTypeKbis(), true, "DocumentTypeKbis");
        validationHelper.validateReferential(constant.getDocumentTypeBilling(), true, "DocumentTypeBilling");
        validationHelper.validateReferential(constant.getDocumentTypeDunning(), true, "DocumentTypeDunning");
        validationHelper.validateReferential(constant.getDocumentTypeRefund(), true, "DocumentTypeRefund");
        validationHelper.validateReferential(constant.getDocumentTypeBillingClosure(), true,
                "DocumentTypeBillingClosure");
        validationHelper.validateReferential(constant.getDocumentTypeProvisionnalReceipt(), true,
                "DocumentTypeProvisionnalReceipt");
        validationHelper.validateReferential(constant.getDocumentTypeProofReading(), true,
                "DocumentTypeProofReading");
        validationHelper.validateReferential(constant.getDocumentTypePublicationCertificate(), true,
                "DocumentTypePublicationCertificate");
        validationHelper.validateReferential(constant.getAttachmentTypeKbis(), true, "AttachmentTypeKbis");
        validationHelper.validateReferential(constant.getAttachmentTypeCni(), true, "AttachmentTypeCni");
        validationHelper.validateReferential(constant.getAttachmentTypeLogo(), true, "AttachmentTypeLogo");
        validationHelper.validateReferential(constant.getAttachmentTypeJournal(), true, "AttachmentTypeJournal");
        validationHelper.validateReferential(constant.getAttachmentTypeProofOfAddress(), true,
                "AttachmentTypeProofOfAddress");
        validationHelper.validateReferential(constant.getAttachmentTypePublicationProof(), true,
                "AttachmentTypePublicationProof");
        validationHelper.validateReferential(constant.getAttachmentTypeBillingClosure(), true,
                "AttachmentTypeBillingClosure");
        validationHelper.validateReferential(constant.getCountryFrance(), true, "CountryFrance");
        validationHelper.validateReferential(constant.getCountryMonaco(), true, "CountryMonaco");
        validationHelper.validateReferential(constant.getBillingTypeLogo(), true, "BillingTypeLogo");
        validationHelper.validateReferential(constant.getPaymentTypePrelevement(), true, "PaymentTypePrelevement");
        validationHelper.validateReferential(constant.getPaymentTypeVirement(), true, "PaymentTypeVirement");
        validationHelper.validateReferential(constant.getPaymentTypeCB(), true, "PaymentTypeCB");
        validationHelper.validateReferential(constant.getPaymentTypeEspeces(), true, "PaymentTypeEspeces");
        validationHelper.validateReferential(constant.getRefundTypeVirement(), true, "RefundTypeVirement");
        validationHelper.validateReferential(constant.getSubscriptionPeriodType12M(), true,
                "SubscriptionPeriodType12M");
        validationHelper.validateReferential(constant.getLegalFormUnregistered(), true, "LegalFormUnregistered");
        validationHelper.validateReferential(constant.getJournalTypeSpel(), true, "JournalTypeSpel");
        validationHelper.validateReferential(constant.getConfrereJssPaper(), true, "ConfrereJss");
        validationHelper.validateReferential(constant.getConfrereJssSpel(), true, "ConfrereJss");
        validationHelper.validateReferential(constant.getDomiciliationContractTypeKeepMail(), true,
                "DomiciliationContractTypeKeepMail");
        validationHelper.validateReferential(constant.getDomiciliationContractTypeRouteMail(), true,
                "DomiciliationContractTypeRouteMail");
        validationHelper.validateReferential(constant.getDomiciliationContractTypeRouteEmailAndMail(), true,
                "DomiciliationContractTypeRouteEmailAndMail");
        validationHelper.validateReferential(constant.getDomiciliationContractTypeRouteEmail(), true,
                "DomiciliationContractTypeRouteEmail");
        validationHelper.validateReferential(constant.getMailRedirectionTypeOther(), true,
                "MailRedirectionTypeOther");
        validationHelper.validateReferential(constant.getBodaccPublicationTypeMerging(), true,
                "BodaccPublicationTypeMerging");
        validationHelper.validateReferential(constant.getBodaccPublicationTypeSplit(), true,
                "BodaccPublicationTypeSplit");
        validationHelper.validateReferential(constant.getBodaccPublicationTypePartialSplit(), true,
                "BodaccPublicationTypePartialSplit");
        validationHelper.validateReferential(constant.getBodaccPublicationTypePossessionDispatch(), true,
                "BodaccPublicationTypePossessionDispatch");
        validationHelper.validateReferential(constant.getBodaccPublicationTypeEstateRepresentativeDesignation(),
                true, "BodaccPublicationTypeEstateRepresentativeDesignation");
        validationHelper.validateReferential(constant.getBodaccPublicationTypeSaleOfBusiness(), true,
                "BodaccPublicationTypeSaleOfBusiness");
        validationHelper.validateReferential(constant.getActTypeSeing(), true, "ActTypeSeing");
        validationHelper.validateReferential(constant.getActTypeAuthentic(), true, "ActTypeAuthentic");
        validationHelper.validateReferential(constant.getAssignationTypeEmployee(), true,
                "AssignationTypeEmployee");
        validationHelper.validateReferential(constant.getEmployeeBillingResponsible(), true,
                "EmployeeBillingResponsible");
        validationHelper.validateReferential(constant.getEmployeeMailResponsible(), true,
                "EmployeeMailResponsible");
        validationHelper.validateReferential(constant.getTransfertFundsTypePhysique(), true,
                "TransfertFundsTypePhysique");
        validationHelper.validateReferential(constant.getTransfertFundsTypeMoral(), true,
                "TransfertFundsTypeMoral");
        validationHelper.validateReferential(constant.getTransfertFundsTypeBail(), true, "TransfertFundsTypeBail");
        validationHelper.validateReferential(constant.getCompetentAuthorityTypeRcs(), true,
                "CompetentAuthorityTypeRcs");
        validationHelper.validateReferential(constant.getCompetentAuthorityTypeCfp(), true,
                "CompetentAuthorityTypeCfp");
        validationHelper.validateReferential(constant.getInvoiceStatusSend(), true, "InvoiceStatusSend");
        validationHelper.validateReferential(constant.getInvoiceStatusReceived(), true, "InvoiceStatusReceived");
        validationHelper.validateReferential(constant.getInvoiceStatusPayed(), true, "InvoiceStatusPayed");
        validationHelper.validateReferential(constant.getInvoiceStatusCancelled(), true, "InvoiceStatusCancelled");
        validationHelper.validateReferential(constant.getPaymentWayInbound(), true, "PaymentWayInbound");
        validationHelper.validateReferential(constant.getPaymentWayOutboud(), true, "PaymentWayOutboud");
        validationHelper.validateReferential(constant.getVatTwenty(), true, "VatTwenty");
        validationHelper.validateReferential(constant.getVatEight(), true, "VatEight");
        validationHelper.validateReferential(constant.getDepartmentMartinique(), true, "DepartmentMartinique");
        validationHelper.validateReferential(constant.getDepartmentGuadeloupe(), true, "DepartmentGuadeloupe");
        validationHelper.validateReferential(constant.getDepartmentReunion(), true, "DepartmentReunion");
        validationHelper.validateReferential(constant.getTypePersonnePersonnePhysique(), true,
                "TypePersonnePersonnePhysique");
        validationHelper.validateReferential(constant.getTypePersonneExploitation(), true,
                "TypePersonneExploitation");
        validationHelper.validateReferential(constant.getTypePersonnePersonneMorale(), true,
                "TypePersonnePersonneMorale");
        validationHelper.validateReferential(constant.getFormeJuridiqueEntrepreneurIndividuel(), true,
                "FormeJuridiqueEntrepreneurIndividuel");
        validationHelper.validateReferential(constant.getTypeFormaliteCessation(), true, "TypeFormaliteCessation");
        validationHelper.validateReferential(constant.getTypeFormaliteCorrection(), true,
                "TypeFormaliteCorrection");
        validationHelper.validateReferential(constant.getTypeFormaliteModification(), true,
                "TypeFormaliteModification");
        validationHelper.validateReferential(constant.getTypeFormaliteCreation(), true, "TypeFormaliteCreation");
        validationHelper.validateReferential(constant.getBillingClosureRecipientTypeClient(), true,
                "BillingClosureRecipientTypeClient");
        validationHelper.validateReferential(constant.getBillingClosureRecipientTypeResponsable(), true,
                "BillingClosureRecipientTypeResponsable");
        validationHelper.validateReferential(constant.getBillingClosureTypeAffaire(), true,
                "BillingClosureRecipientTypeResponsable");

        return new ResponseEntity<Constant>(constantService.addOrUpdateConstant(constant), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/regies")
    public ResponseEntity<List<Regie>> getRegies() {
        return new ResponseEntity<List<Regie>>(regieService.getRegies(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/regie")
    public ResponseEntity<Regie> addOrUpdateRegie(
            @RequestBody Regie regie) throws OsirisValidationException, OsirisException {
        if (regie.getId() != null)
            validationHelper.validateReferential(regie, true, "regie");
        validationHelper.validateString(regie.getCode(), true, "Code");
        validationHelper.validateString(regie.getLabel(), true, "Label");
        validationHelper.validateReferential(regie.getCountry(), true, "Country");
        validationHelper.validateReferential(regie.getCity(), true, "City");
        validationHelper.validateString(regie.getPostalCode(), false, 6, "PostalCode");
        validationHelper.validateString(regie.getCedexComplement(), false, 20, "CedexComplement");
        validationHelper.validateString(regie.getAddress(), true, 60, "Address");
        validationHelper.validateString(regie.getIban(), true, 40, "Iban");

        return new ResponseEntity<Regie>(regieService.addOrUpdateRegie(regie), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/providers")
    public ResponseEntity<List<Provider>> getProviders() {
        return new ResponseEntity<List<Provider>>(providerService.getProviders(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/provider")
    public ResponseEntity<Provider> addOrUpdateProvider(
            @RequestBody Provider provider) throws OsirisValidationException, OsirisException {
        if (provider.getId() != null)
            validationHelper.validateReferential(provider, true, "provider");
        validationHelper.validateString(provider.getLabel(), true, "Label");
        validationHelper.validateString(provider.getIban(), false, 40, "Iban");
        validationHelper.validateString(provider.getJssReference(), false, 20, "JssReference");
        validationHelper.validateReferential(provider.getVatCollectionType(), true, "VatCollectionType");
        validationHelper.validateReferential(provider.getPaymentType(), false, "PaymentType");
        validationHelper.validateReferential(provider.getDefaultBillingItem(), false, "DefaultBillingItem");

        return new ResponseEntity<Provider>(providerService.addOrUpdateProvider(provider), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/vat-collection-types")
    public ResponseEntity<List<VatCollectionType>> getVatCollectionTypes() {
        return new ResponseEntity<List<VatCollectionType>>(vatCollectionTypeService.getVatCollectionTypes(),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/vat-collection-type")
    public ResponseEntity<VatCollectionType> addOrUpdateVatCollectionType(
            @RequestBody VatCollectionType vatCollectionTypes) throws OsirisValidationException, OsirisException {
        if (vatCollectionTypes.getId() != null)
            validationHelper.validateReferential(vatCollectionTypes, true, "vatCollectionTypes");
        validationHelper.validateString(vatCollectionTypes.getCode(), true, "code");
        validationHelper.validateString(vatCollectionTypes.getLabel(), true, "label");

        return new ResponseEntity<VatCollectionType>(
                vatCollectionTypeService.addOrUpdateVatCollectionType(vatCollectionTypes), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/gifts")
    public ResponseEntity<List<Gift>> getGifts() {
        return new ResponseEntity<List<Gift>>(giftService.getGifts(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/gift")
    public ResponseEntity<Gift> addOrUpdateGift(
            @RequestBody Gift gifts) throws OsirisValidationException, OsirisException {
        if (gifts.getId() != null)
            validationHelper.validateReferential(gifts, true, "gifts");
        validationHelper.validateString(gifts.getCode(), true, 20, "code");
        validationHelper.validateString(gifts.getLabel(), true, 100, "label");
        validationHelper.validateReferential(gifts.getAccountingAccount(), false, "AccountingAccount");

        return new ResponseEntity<Gift>(giftService.addOrUpdateGift(gifts), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/special-offers")
    public ResponseEntity<List<SpecialOffer>> getSpecialOffers() {
        return new ResponseEntity<List<SpecialOffer>>(specialOfferService.getSpecialOffers(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/special-offer")
    public ResponseEntity<SpecialOffer> addOrUpdateSpecialOffer(
            @RequestBody SpecialOffer specialOffers) throws OsirisValidationException, OsirisException {
        if (specialOffers.getId() != null)
            validationHelper.validateReferential(specialOffers, true, "specialOffers");
        validationHelper.validateString(specialOffers.getCode(), true, 20, "code");
        validationHelper.validateString(specialOffers.getLabel(), true, 100, "label");

        if (specialOffers.getAssoSpecialOfferBillingTypes() == null
                || specialOffers.getAssoSpecialOfferBillingTypes().size() == 0)
            throw new OsirisValidationException("AssoSpecialOfferBillingTypes");

        for (AssoSpecialOfferBillingType asso : specialOffers.getAssoSpecialOfferBillingTypes())
            validationHelper.validateReferential(asso.getBillingType(), true, "BillingType");

        return new ResponseEntity<SpecialOffer>(specialOfferService.addOrUpdateSpecialOffer(specialOffers),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return new ResponseEntity<List<Department>>(departmentService.getDepartments(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/department")
    public ResponseEntity<Department> addOrUpdateDepartment(
            @RequestBody Department departments) throws OsirisValidationException, OsirisException {
        if (departments.getId() != null)
            validationHelper.validateReferential(departments, true, "departments");
        validationHelper.validateString(departments.getCode(), true, 20, "code");
        validationHelper.validateString(departments.getLabel(), true, 100, "label");

        return new ResponseEntity<Department>(departmentService.addOrUpdateDepartment(departments), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/cities")
    public ResponseEntity<List<City>> getCities() {
        return new ResponseEntity<List<City>>(cityService.getCities(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/city")
    public ResponseEntity<City> addOrUpdateCity(
            @RequestBody City cities) throws OsirisValidationException, OsirisException {
        if (cities.getId() != null)
            validationHelper.validateReferential(cities, true, "cities");
        validationHelper.validateString(cities.getCode(), true, 20, "Code");
        validationHelper.validateString(cities.getLabel(), true, 100, "Label");
        validationHelper.validateString(cities.getPostalCode(), false, 6, "PostalCode");
        validationHelper.validateReferential(cities.getDepartment(), false, "Department");
        validationHelper.validateReferential(cities.getCountry(), true, "Country");

        return new ResponseEntity<City>(cityService.addOrUpdateCity(cities), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/cities/search/postal-code")
    public ResponseEntity<List<City>> getCitiesByPostalCode(@RequestParam String postalCode) {
        return new ResponseEntity<List<City>>(cityService.getCitiesByPostalCode(postalCode), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/cities/search/country")
    public ResponseEntity<List<City>> getCitiesByCountry(@RequestParam(required = false) Integer countryId,
            @RequestParam String city) {
        return new ResponseEntity<List<City>>(cityService.getCitiesByCountry(countryId, city), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-types")
    public ResponseEntity<List<BillingType>> getBillingTypes() {
        return new ResponseEntity<List<BillingType>>(billingTypeService.getBillingTypes(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/billing-type")
    public ResponseEntity<BillingType> addOrUpdateBillingType(
            @RequestBody BillingType billingType) throws OsirisValidationException, OsirisException {
        if (billingType.getId() != null)
            validationHelper.validateReferential(billingType, true, "billingType");
        validationHelper.validateString(billingType.getCode(), true, 20, "code");
        validationHelper.validateString(billingType.getLabel(), true, 100, "label");
        validationHelper.validateReferential(billingType.getVat(), billingType.getIsOverrideVat(), "Vat");
        if (!billingType.getIsOverrideVat())
            billingType.setVat(null);

        return new ResponseEntity<BillingType>(billingTypeService.addOrUpdateBillingType(billingType), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-items")
    public ResponseEntity<List<BillingItem>> getBillingItems() {
        return new ResponseEntity<List<BillingItem>>(billingItemService.getBillingItems(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/billing-item")
    public ResponseEntity<BillingItem> addOrUpdateBillingItem(
            @RequestBody BillingItem billingItems) throws OsirisValidationException, OsirisException {
        if (billingItems.getId() != null)
            validationHelper.validateReferential(billingItems, true, "billingItems");
        validationHelper.validateReferential(billingItems.getBillingType(), true, "BillingType");

        validationHelper.validateDate(billingItems.getStartDate(), true, "StartDate");

        return new ResponseEntity<BillingItem>(billingItemService.addOrUpdateBillingItem(billingItems), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/vats")
    public ResponseEntity<List<Vat>> getVats() {
        return new ResponseEntity<List<Vat>>(vatService.getVats(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/vat")
    public ResponseEntity<Vat> addOrUpdateVat(
            @RequestBody Vat vat) throws OsirisValidationException, OsirisException {
        if (vat.getId() != null)
            validationHelper.validateReferential(vat, true, "vat");
        validationHelper.validateReferential(vat.getAccountingAccount(), true, "AccountingAccount");
        validationHelper.validateString(vat.getCode(), true, 20, "Code");
        validationHelper.validateString(vat.getLabel(), true, 100, "Label");
        validationHelper.validateFloat(vat.getRate(), true, "Rate");

        return new ResponseEntity<Vat>(vatService.addOrUpdateVat(vat), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/regions")
    public ResponseEntity<List<Region>> getRegions() {
        return new ResponseEntity<List<Region>>(regionService.getRegions(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/region")
    public ResponseEntity<Region> addOrUpdateRegion(
            @RequestBody Region regions) throws OsirisValidationException, OsirisException {
        if (regions.getId() != null)
            validationHelper.validateReferential(regions, true, "regions");
        validationHelper.validateString(regions.getCode(), true, 20, "code");
        validationHelper.validateString(regions.getLabel(), true, 100, "label");

        return new ResponseEntity<Region>(regionService.addOrUpdateRegion(regions), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/payment-types")
    public ResponseEntity<List<PaymentType>> getPaymentTypes() {
        return new ResponseEntity<List<PaymentType>>(paymentTypeService.getPaymentTypes(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/payment-type")
    public ResponseEntity<PaymentType> addOrUpdatePaymentType(
            @RequestBody PaymentType paymentTypes) throws OsirisValidationException, OsirisException {
        if (paymentTypes.getId() != null)
            validationHelper.validateReferential(paymentTypes, true, "paymentTypes");
        validationHelper.validateString(paymentTypes.getCode(), true, 20, "code");
        validationHelper.validateString(paymentTypes.getLabel(), true, 100, "label");

        return new ResponseEntity<PaymentType>(paymentTypeService.addOrUpdatePaymentType(paymentTypes), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/languages")
    public ResponseEntity<List<Language>> getLanguages() {
        return new ResponseEntity<List<Language>>(languageService.getLanguages(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/language")
    public ResponseEntity<Language> addOrUpdateLanguage(
            @RequestBody Language languages) throws OsirisValidationException, OsirisException {
        if (languages.getId() != null)
            validationHelper.validateReferential(languages, true, "languages");
        validationHelper.validateString(languages.getCode(), true, 20, "code");
        validationHelper.validateString(languages.getLabel(), true, 100, "label");

        return new ResponseEntity<Language>(languageService.addOrUpdateLanguage(languages), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/delivery-services")
    public ResponseEntity<List<DeliveryService>> getDeliveryServices() {
        return new ResponseEntity<List<DeliveryService>>(deliveryServiceService.getDeliveryServices(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/delivery-service")
    public ResponseEntity<DeliveryService> addOrUpdateDeliveryService(
            @RequestBody DeliveryService deliveryServices) throws OsirisValidationException, OsirisException {
        if (deliveryServices.getId() != null)
            validationHelper.validateReferential(deliveryServices, true, "deliveryServices");
        validationHelper.validateString(deliveryServices.getCode(), true, 20, "code");
        validationHelper.validateString(deliveryServices.getLabel(), true, 100, "label");

        return new ResponseEntity<DeliveryService>(deliveryServiceService.addOrUpdateDeliveryService(deliveryServices),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/countries")
    public ResponseEntity<List<Country>> getCountries() {
        return new ResponseEntity<List<Country>>(countryService.getCountries(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/country")
    public ResponseEntity<Country> addOrUpdateCountry(
            @RequestBody Country countries) throws OsirisValidationException, OsirisException {
        if (countries.getId() != null)
            validationHelper.validateReferential(countries, true, "countries");
        validationHelper.validateString(countries.getCode(), true, 20, "code");
        validationHelper.validateString(countries.getLabel(), true, 100, "label");

        return new ResponseEntity<Country>(countryService.addOrUpdateCountry(countries), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/civilities")
    public ResponseEntity<List<Civility>> getCivilities() {
        return new ResponseEntity<List<Civility>>(civilityService.getCivilities(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/civility")
    public ResponseEntity<Civility> addOrUpdateCivility(
            @RequestBody Civility civilities) throws OsirisValidationException, OsirisException {
        if (civilities.getId() != null)
            validationHelper.validateReferential(civilities, true, "civilities");
        validationHelper.validateString(civilities.getCode(), true, 20, "code");
        validationHelper.validateString(civilities.getLabel(), true, 100, "label");

        return new ResponseEntity<Civility>(civilityService.addOrUpdateCivility(civilities), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authorities/search/department")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthorityByDepartmentAndName(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam String authority) {
        return new ResponseEntity<List<CompetentAuthority>>(
                competentAuthorityService.getCompetentAuthorityByDepartment(departmentId, authority), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authorities/search/city")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthorityByCity(Integer cityId)
            throws OsirisValidationException {
        if (cityId == null)
            throw new OsirisValidationException("cityId");
        return new ResponseEntity<List<CompetentAuthority>>(
                competentAuthorityService.getCompetentAuthorityByCity(cityId), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authority-types")
    public ResponseEntity<List<CompetentAuthorityType>> getCompetentAuthorityTypes() {
        return new ResponseEntity<List<CompetentAuthorityType>>(
                competentAuthorityTypeService.getCompetentAuthorityTypes(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/competent-authority-type")
    public ResponseEntity<CompetentAuthorityType> addOrUpdateCompetentAuthorityType(
            @RequestBody CompetentAuthorityType competentAuthorityTypes)
            throws OsirisValidationException, OsirisException {
        if (competentAuthorityTypes.getId() != null)
            validationHelper.validateReferential(competentAuthorityTypes, true, "competentAuthorityTypes");
        validationHelper.validateString(competentAuthorityTypes.getCode(), true, 20, "code");
        validationHelper.validateString(competentAuthorityTypes.getLabel(), true, 100, "label");

        return new ResponseEntity<CompetentAuthorityType>(
                competentAuthorityTypeService.addOrUpdateCompetentAuthorityType(competentAuthorityTypes),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authorities")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthorities() {
        return new ResponseEntity<List<CompetentAuthority>>(competentAuthorityService.getCompetentAuthorities(),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/competent-authority")
    public ResponseEntity<CompetentAuthority> addOrUpdateCompetentAuthority(
            @RequestBody CompetentAuthority competentAuthorities) throws OsirisValidationException, OsirisException {
        if (competentAuthorities.getId() != null)
            validationHelper.validateReferential(competentAuthorities, true, "competentAuthorities");
        validationHelper.validateString(competentAuthorities.getCode(), true, 20, "code");
        validationHelper.validateString(competentAuthorities.getLabel(), true, 100, "label");
        validationHelper.validateString(competentAuthorities.getSchedulle(), false, 150, "Schedulle");
        validationHelper.validateReferential(competentAuthorities.getCompetentAuthorityType(), true,
                "CompetentAuthorityType");
        if (competentAuthorities.getCities() == null && competentAuthorities.getDepartments() == null
                && competentAuthorities.getRegions() == null)
            throw new OsirisValidationException("Cities or Departments or Regions");

        if (competentAuthorities.getCities() != null)
            for (City city : competentAuthorities.getCities())
                validationHelper.validateReferential(city, false, "city");

        if (competentAuthorities.getRegions() != null)
            for (Region region : competentAuthorities.getRegions())
                validationHelper.validateReferential(region, false, "region");

        if (competentAuthorities.getDepartments() != null)
            for (Department department : competentAuthorities.getDepartments())
                validationHelper.validateReferential(department, false, "department");

        validationHelper.validateString(competentAuthorities.getIban(), competentAuthorities.getHasAccount(), 40,
                "Iban");
        validationHelper.validateString(competentAuthorities.getJssAccount(), false, 40, "JssAccount");
        validationHelper.validateString(competentAuthorities.getContact(), false, 40, "Contact");
        validationHelper.validateString(competentAuthorities.getMailRecipient(), false, 60, "MailRecipient");
        validationHelper.validateString(competentAuthorities.getAddress(), false, 60, "Address");
        validationHelper.validateString(competentAuthorities.getPostalCode(), false, 10, "PostalCode");
        validationHelper.validateString(competentAuthorities.getCedexComplement(), false, 20, "CedexComplement");
        validationHelper.validateReferential(competentAuthorities.getCity(), false, "City");
        validationHelper.validateReferential(competentAuthorities.getCountry(), false, "Country");

        return new ResponseEntity<CompetentAuthority>(
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthorities), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/weekdays")
    public ResponseEntity<List<WeekDay>> getWeekDays() {
        return new ResponseEntity<List<WeekDay>>(weekDayService.getWeekDays(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/weekday")
    public ResponseEntity<WeekDay> addOrUpdateWeekDay(
            @RequestBody WeekDay weekDays) throws OsirisException, OsirisValidationException {
        if (weekDays.getId() != null)
            validationHelper.validateReferential(weekDays, true, "weekDays");
        validationHelper.validateString(weekDays.getCode(), true, 20, "code");
        validationHelper.validateString(weekDays.getLabel(), true, 100, "label");

        return new ResponseEntity<WeekDay>(weekDayService.addOrUpdateWeekDay(weekDays), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/legal-forms")
    public ResponseEntity<List<LegalForm>> getLegalForms() {
        return new ResponseEntity<List<LegalForm>>(legalFormService.getLegalForms(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/legal-form")
    public ResponseEntity<LegalForm> addOrUpdateLegalForm(
            @RequestBody LegalForm legalForms) throws OsirisValidationException, OsirisException {
        if (legalForms.getId() != null)
            validationHelper.validateReferential(legalForms, true, "legalForms");
        validationHelper.validateString(legalForms.getCode(), true, 20, "code");
        validationHelper.validateString(legalForms.getLabel(), true, 100, "label");
        validationHelper.validateString(legalForms.getDescription(), false, 400, "description");

        return new ResponseEntity<LegalForm>(legalFormService.addOrUpdateLegalForm(legalForms), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/document-types")
    public ResponseEntity<List<DocumentType>> getDocumentTypes() {
        return new ResponseEntity<List<DocumentType>>(documentTypeService.getDocumentTypes(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/document-type")
    public ResponseEntity<DocumentType> addOrUpdateDocumentType(
            @RequestBody DocumentType documentTypes) throws OsirisValidationException, OsirisException {
        if (documentTypes.getId() != null)
            validationHelper.validateReferential(documentTypes, true, "documentTypes");
        validationHelper.validateString(documentTypes.getCode(), true, 20, "code");
        validationHelper.validateString(documentTypes.getLabel(), true, 100, "label");

        return new ResponseEntity<DocumentType>(documentTypeService.addOrUpdateDocumentType(documentTypes),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment-types")
    public ResponseEntity<List<AttachmentType>> getAttachmentTypes() {
        return new ResponseEntity<List<AttachmentType>>(attachmentTypeService.getAttachmentTypes(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/attachment-type")
    public ResponseEntity<AttachmentType> addOrUpdateAttachmentType(
            @RequestBody AttachmentType attachmentTypes) throws OsirisValidationException, OsirisException {
        if (attachmentTypes.getId() != null)
            validationHelper.validateReferential(attachmentTypes, true, "attachmentTypes");
        validationHelper.validateString(attachmentTypes.getCode(), true, 20, "code");
        validationHelper.validateString(attachmentTypes.getLabel(), true, 100, "label");
        validationHelper.validateString(attachmentTypes.getDescription(), false, 400, "description");

        return new ResponseEntity<AttachmentType>(attachmentTypeService.addOrUpdateAttachmentType(attachmentTypes),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment/disabled")
    public ResponseEntity<Boolean> disableDocument(@RequestParam Integer idAttachment)
            throws OsirisValidationException {
        if (idAttachment == null)
            throw new OsirisValidationException("idAttachment");

        Attachment attachment = attachmentService.getAttachment(idAttachment);
        if (attachment == null)
            throw new OsirisValidationException("attachment");

        attachmentService.disableDocument(attachment);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/attachment/upload")
    public ResponseEntity<List<Attachment>> uploadAttachment(@RequestParam MultipartFile file,
            @RequestParam Integer idEntity, @RequestParam String entityType,
            @RequestParam Integer idAttachmentType,
            @RequestParam String filename, @RequestParam Boolean replaceExistingAttachementType)
            throws OsirisValidationException, OsirisException {
        if (idAttachmentType == null)
            throw new OsirisValidationException("idAttachmentType");

        AttachmentType attachmentType = attachmentTypeService.getAttachmentType(idAttachmentType);

        if (attachmentType == null)
            throw new OsirisValidationException("attachmentType");

        if (filename == null || filename.equals(""))
            throw new OsirisValidationException("filename");

        if (idEntity == null)
            throw new OsirisValidationException("idEntity");

        if (entityType == null)
            throw new OsirisValidationException("entityType");

        if (!entityType.equals(Tiers.class.getSimpleName())
                && !entityType.equals(Responsable.class.getSimpleName())
                && !entityType.equals(Quotation.class.getSimpleName())
                && !entityType.equals(Announcement.class.getSimpleName())
                && !entityType.equals(Domiciliation.class.getSimpleName())
                && !entityType.equals(CustomerOrder.class.getSimpleName())
                && !entityType.equals(Provision.class.getSimpleName())
                && !entityType.equals(Formalite.class.getSimpleName())
                && !entityType.equals(Journal.class.getSimpleName())
                && !entityType.equals(Bodacc.class.getSimpleName())
                && !entityType.equals(SimpleProvision.class.getSimpleName()))
            throw new OsirisValidationException("entityType");

        return new ResponseEntity<List<Attachment>>(
                attachmentService.addAttachment(file, idEntity, entityType, attachmentType, filename,
                        replaceExistingAttachementType),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment/download")
    public ResponseEntity<byte[]> downloadAttachment(@RequestParam("idAttachment") Integer idAttachment)
            throws OsirisValidationException, OsirisException {
        byte[] data = null;
        HttpHeaders headers = null;
        Attachment tiersAttachment = attachmentService.getAttachment(idAttachment);

        if (tiersAttachment == null || tiersAttachment.getUploadedFile() == null
                || tiersAttachment.getUploadedFile().getPath() == null)
            throw new OsirisValidationException("tiersAttachment or UploadedFile or Path");

        File file = new File(tiersAttachment.getUploadedFile().getPath());

        if (file != null) {
            try {
                data = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new OsirisException("Unable to read file " + file.getAbsolutePath());
            }

            headers = new HttpHeaders();
            headers.add("filename", tiersAttachment.getUploadedFile().getFilename());
            headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
            headers.setContentLength(data.length);

            // Compute content type
            String mimeType = null;
            try {
                mimeType = Files.probeContentType(file.toPath());
            } catch (IOException e) {
                throw new OsirisException("Unable to read file " + file.getAbsolutePath());
            }
            if (mimeType == null)
                mimeType = "application/octet-stream";
            headers.set("content-type", mimeType);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/all")
    @Transactional
    public ResponseEntity<Boolean> reindexAll() {
        invoiceService.reindexInvoices();
        tiersService.reindexTiers();
        refundService.reindexRefunds();
        responsableService.reindexResponsable();
        quotationService.reindexQuotation();
        customerOrderService.reindexCustomerOrder();
        assoAffaireOrderService.reindexAffaires();
        affaireService.reindexAffaire();

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/logs")
    public ResponseEntity<List<OsirisLog>> getLogs(@RequestParam boolean hideRead) {
        return new ResponseEntity<List<OsirisLog>>(globalExceptionHandler.getLogs(hideRead), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/log")
    public ResponseEntity<OsirisLog> getLog(@RequestParam Integer id) {
        return new ResponseEntity<OsirisLog>(globalExceptionHandler.getLog(id), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/logs")
    public ResponseEntity<OsirisLog> addOrUpdateLogs(@RequestBody OsirisLog osirisLog)
            throws OsirisValidationException, OsirisException {
        return new ResponseEntity<OsirisLog>(globalExceptionHandler.addOrUpdateLog(osirisLog), HttpStatus.OK);
    }
}