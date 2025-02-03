package com.jss.osiris.modules.osiris.miscellaneous.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
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
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.service.CategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.osiris.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.invoicing.service.RefundService;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.AssoSpecialOfferBillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Civility;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Constant;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Gift;
import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaperSetType;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.model.Region;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.model.Vat;
import com.jss.osiris.modules.osiris.miscellaneous.model.VatCollectionType;
import com.jss.osiris.modules.osiris.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.osiris.miscellaneous.service.ActiveDirectoryGroupService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.BillingItemService;
import com.jss.osiris.modules.osiris.miscellaneous.service.BillingTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CivilityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CountryService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CustomerOrderFrequencyService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CustomerOrderOriginService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DeliveryServiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DepartmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DepartmentVatSettingService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.GiftService;
import com.jss.osiris.modules.osiris.miscellaneous.service.LanguageService;
import com.jss.osiris.modules.osiris.miscellaneous.service.LegalFormService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PaperSetTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PaymentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ProviderService;
import com.jss.osiris.modules.osiris.miscellaneous.service.RegionService;
import com.jss.osiris.modules.osiris.miscellaneous.service.SpecialOfferService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatCollectionTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.miscellaneous.service.WeekDayService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;
import com.jss.osiris.modules.osiris.quotation.service.AssoAffaireOrderService;
import com.jss.osiris.modules.osiris.quotation.service.BankTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.DirectDebitTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeDocumentService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

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
    TypeDocumentService typeDocumentService;

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
    PaymentService paymentService;

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

    @Autowired
    CustomerMailService customerMailService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    DirectDebitTransfertService directDebitTransfertService;

    @Autowired
    CustomerOrderOriginService customerOrderOriginService;

    @Autowired
    DepartmentVatSettingService departmentVatSettingService;

    @Autowired
    CustomerOrderFrequencyService customerOrderFrequencyService;

    @Autowired
    ActiveDirectoryGroupService activeDirectoryGroupService;

    @Autowired
    PaperSetTypeService paperSetTypeService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    PostService postService;

    @GetMapping(inputEntryPoint + "/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<List<Category>>(categoryService.getAvailableCategories(), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/paper-set-types")
    public ResponseEntity<List<PaperSetType>> getPaperSetTypes() {
        return new ResponseEntity<List<PaperSetType>>(paperSetTypeService.getPaperSetTypes(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/paper-set-type")
    public ResponseEntity<PaperSetType> addOrUpdatePaperSetType(
            @RequestBody PaperSetType paperSetTypes) throws OsirisValidationException, OsirisException {
        if (paperSetTypes.getId() != null)
            validationHelper.validateReferential(paperSetTypes, true, "paperSetTypes");
        validationHelper.validateString(paperSetTypes.getCode(), true, "code");
        validationHelper.validateString(paperSetTypes.getLabel(), true, "label");

        return new ResponseEntity<PaperSetType>(paperSetTypeService.addOrUpdatePaperSetType(paperSetTypes),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/active-directory-groups")
    public ResponseEntity<List<ActiveDirectoryGroup>> getActiveDirectoryGroups() {
        return new ResponseEntity<List<ActiveDirectoryGroup>>(activeDirectoryGroupService.getActiveDirectoryGroups(),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/active-directory-group")
    public ResponseEntity<ActiveDirectoryGroup> addOrUpdateActiveDirectoryGroup(
            @RequestBody ActiveDirectoryGroup activeDirectoryGroups) throws OsirisValidationException, OsirisException {
        if (activeDirectoryGroups.getId() != null)
            validationHelper.validateReferential(activeDirectoryGroups, true, "activeDirectoryGroups");
        validationHelper.validateString(activeDirectoryGroups.getCode(), true, "code");
        validationHelper.validateString(activeDirectoryGroups.getLabel(), true, "label");
        validationHelper.validateString(activeDirectoryGroups.getActiveDirectoryPath(), true, "activeDirectoryPath");

        return new ResponseEntity<ActiveDirectoryGroup>(
                activeDirectoryGroupService.addOrUpdateActiveDirectoryGroup(activeDirectoryGroups), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-order-frequencies")
    public ResponseEntity<List<CustomerOrderFrequency>> getCustomerOrderFrequencies() {
        return new ResponseEntity<List<CustomerOrderFrequency>>(
                customerOrderFrequencyService.getCustomerOrderFrequencies(), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/customer-order-frequency")
    public ResponseEntity<CustomerOrderFrequency> addOrUpdateCustomerOrderFrequency(
            @RequestBody CustomerOrderFrequency customerOrderFrequencies)
            throws OsirisValidationException, OsirisException {
        if (customerOrderFrequencies.getId() != null)
            validationHelper.validateReferential(customerOrderFrequencies, true, "customerOrderFrequencies");
        validationHelper.validateString(customerOrderFrequencies.getCode(), true, "code");
        validationHelper.validateString(customerOrderFrequencies.getLabel(), true, "label");
        validationHelper.validateInteger(customerOrderFrequencies.getMonthNumber(), true, "daysNumber");

        return new ResponseEntity<CustomerOrderFrequency>(
                customerOrderFrequencyService.addOrUpdateCustomerOrderFrequency(customerOrderFrequencies),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/department-vat-settings")
    public ResponseEntity<List<DepartmentVatSetting>> getDepartmentVatSettings() {
        return new ResponseEntity<List<DepartmentVatSetting>>(departmentVatSettingService.getDepartmentVatSettings(),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/department-vat-setting")
    public ResponseEntity<DepartmentVatSetting> addOrUpdateDepartmentVatSetting(
            @RequestBody DepartmentVatSetting departmentVatSettings) throws OsirisValidationException, OsirisException {
        if (departmentVatSettings.getId() != null)
            validationHelper.validateReferential(departmentVatSettings, true, "departmentVatSettings");
        validationHelper.validateString(departmentVatSettings.getCode(), true, "code");
        validationHelper.validateReferential(departmentVatSettings.getDepartment(), true, "department");
        validationHelper.validateReferential(departmentVatSettings.getIntermediateVat(), true, "IntermediateVat");
        validationHelper.validateReferential(departmentVatSettings.getReducedVat(), true, "ReducedVat");
        validationHelper.validateReferential(departmentVatSettings.getIntermediateVatForPurshase(), true,
                "IntermediateVatForPurshase");
        validationHelper.validateReferential(departmentVatSettings.getReducedVatForPurshase(), true,
                "ReducedVatForPurshase");

        return new ResponseEntity<DepartmentVatSetting>(
                departmentVatSettingService.addOrUpdateDepartmentVatSetting(departmentVatSettings), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-order-origins")
    public ResponseEntity<List<CustomerOrderOrigin>> getCustomerOrderOrigins() {
        return new ResponseEntity<List<CustomerOrderOrigin>>(customerOrderOriginService.getCustomerOrderOrigins(),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/customer-order-origin")
    public ResponseEntity<CustomerOrderOrigin> addOrUpdateCustomerOrderOrigin(
            @RequestBody CustomerOrderOrigin customerOrderOrigins) throws OsirisValidationException, OsirisException {
        if (customerOrderOrigins.getId() != null)
            validationHelper.validateReferential(customerOrderOrigins, true, "customerOrderOrigins");
        validationHelper.validateString(customerOrderOrigins.getCode(), true, "code");
        validationHelper.validateString(customerOrderOrigins.getLabel(), true, "label");

        return new ResponseEntity<CustomerOrderOrigin>(
                customerOrderOriginService.addOrUpdateCustomerOrderOrigin(customerOrderOrigins), HttpStatus.OK);
    }

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

        Employee employee = employeeService.getCurrentEmployee();

        List<Employee> backupEmployee = null;
        if (employee != null) {
            backupEmployee = employeeService.getMyHolidaymaker((Employee) employee);

            if (notification.getNotificationType().equals(Notification.PERSONNAL)) {
                backupEmployee = new ArrayList<Employee>();
                backupEmployee.add((Employee) employee);
            }

            boolean found = false;

            if (backupEmployee != null)
                for (Employee employeeLoop : backupEmployee)
                    if (notification.getEmployee().getId().equals(employeeLoop.getId())) {
                        found = true;
                    }
            if (!found)
                throw new OsirisValidationException("employee");
        }

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
        validationHelper.validateReferential(constant.getFurtherInformationServiceFieldType(), true,
                "ServiceFieldType");
        validationHelper.validateReferential(constant.getTiersCategoryPresse(), true, "tiersCategoryPresse");
        validationHelper.validateReferential(constant.getRffFrequencyAnnual(), true, "RffFrequencyAnnual");
        validationHelper.validateReferential(constant.getRffFrequencyMonthly(), true, "RffFrequencyMonthly");
        validationHelper.validateReferential(constant.getRffFrequencyQuarterly(), true, "RffFrequencyQuarterly");
        validationHelper.validateReferential(constant.getPaymentDeadLineType30(), true,
                "PaymentDeadLineType30");
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
        validationHelper.validateReferential(constant.getDocumentTypeBilling(), true, "DocumentTypeBilling");
        validationHelper.validateReferential(constant.getDocumentTypeDunning(), true, "DocumentTypeDunning");
        validationHelper.validateReferential(constant.getDocumentTypeRefund(), true, "DocumentTypeRefund");
        validationHelper.validateReferential(constant.getDocumentTypeBillingClosure(), true,
                "DocumentTypeBillingClosure");
        validationHelper.validateReferential(constant.getDocumentTypeProvisionnalReceipt(), true,
                "DocumentTypeProvisionnalReceipt");
        validationHelper.validateReferential(constant.getAttachmentTypeKbis(), true, "AttachmentTypeKbis");
        validationHelper.validateReferential(constant.getAttachmentTypeCni(), true, "AttachmentTypeCni");
        validationHelper.validateReferential(constant.getAttachmentTypeLogo(), true, "AttachmentTypeLogo");
        validationHelper.validateReferential(constant.getAttachmentTypeJournal(), true, "AttachmentTypeJournal");
        validationHelper.validateReferential(constant.getAttachmentTypeQuotation(), true, "AttachmentTypeQuotation");
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
        validationHelper.validateReferential(constant.getVatTwenty(), true, "VatTwenty");
        validationHelper.validateReferential(constant.getDepartmentMartinique(), true, "DepartmentMartinique");
        validationHelper.validateReferential(constant.getDepartmentGuadeloupe(), true, "DepartmentGuadeloupe");
        validationHelper.validateReferential(constant.getDepartmentReunion(), true, "DepartmentReunion");
        validationHelper.validateReferential(constant.getTypePersonnePersonnePhysique(), true,
                "TypePersonnePersonnePhysique");
        validationHelper.validateReferential(constant.getTypePersonneExploitation(), true,
                "TypePersonneExploitation");
        validationHelper.validateReferential(constant.getTypePersonnePersonneMorale(), true,
                "TypePersonnePersonneMorale");
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
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountBank(), true,
                "PrincipalAccountingAccountBank");
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountCharge(), true,
                "PrincipalAccountingAccountCharge");
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountCustomer(), true,
                "PrincipalAccountingCustomer");
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountDeposit(), true,
                "PrincipalAccountingDeposit");
        validationHelper.validateReferential(constant.getAccountingAccountLost(), true,
                "PrincipalAccountingLost");
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountProduct(), true,
                "PrincipalAccountingProduct");
        validationHelper.validateReferential(constant.getAccountingAccountProfit(), true,
                "PrincipalAccountingProfit");
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountProvider(), true,
                "PrincipalAccountingAccountProvider");
        validationHelper.validateReferential(constant.getPrincipalAccountingAccountWaiting(), true,
                "PrincipalAccountingAccountWaiting");
        validationHelper.validateReferential(constant.getBillingTypeVacationUpdateBeneficialOwners(), true,
                "BillingTypeVacationUpdateBeneficialOwners");
        validationHelper.validateReferential(constant.getBillingTypeFormalityAdditionalDeclaration(), true,
                "BillingTypeFormalityAdditionalDeclaration");
        validationHelper.validateReferential(constant.getBillingTypeCorrespondenceFees(), true,
                "BillingTypeCorrespondenceFees");
        validationHelper.validateReferential(constant.getCompetentAuthorityTypeInsee(), true,
                "CompetentAuthorityTypeInsee");

        return new ResponseEntity<Constant>(constantService.addOrUpdateConstant(constant), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/providers")
    public ResponseEntity<List<Provider>> getProviders() {
        return new ResponseEntity<List<Provider>>(providerService.getProviders(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE + "||" + ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/provider")
    public ResponseEntity<Provider> addOrUpdateProvider(
            @RequestBody Provider provider)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (provider.getId() != null)
            validationHelper.validateReferential(provider, true, "provider");
        validationHelper.validateString(provider.getLabel(), true, "Label");
        validationHelper.validateIban(provider.getIban(),
                provider.getPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId()), "Iban");
        validationHelper.validateBic(provider.getBic(),
                provider.getPaymentType().getId().equals(constantService.getPaymentTypeVirement().getId()), "Bic");
        validationHelper.validateString(provider.getJssReference(), false, 20, "JssReference");
        validationHelper.validateReferential(provider.getVatCollectionType(), true, "VatCollectionType");
        validationHelper.validateReferential(provider.getPaymentType(), true, "PaymentType");
        validationHelper.validateReferential(provider.getDefaultBillingItem(), false, "DefaultBillingItem");
        if (provider.getCountry() != null
                && provider.getCountry().getId().equals(constantService.getCountryFrance().getId()))
            validationHelper.validateString(provider.getPostalCode(), true, 10, "PostalCode");
        validationHelper.validateString(provider.getCedexComplement(), false, 20, "CedexComplement");
        validationHelper.validateString(provider.getAddress(), true, 100, "Address");
        validationHelper.validateString(provider.getSiren(), false, 9, "SIREN");
        validationHelper.validateString(provider.getSiret(), false, 14, "SIRET");

        return new ResponseEntity<Provider>(providerService.addOrUpdateProvider(provider), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/vat-collection-types")
    public ResponseEntity<List<VatCollectionType>> getVatCollectionTypes() {
        return new ResponseEntity<List<VatCollectionType>>(vatCollectionTypeService.getVatCollectionTypes(),
                HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/special-offer")
    public ResponseEntity<SpecialOffer> addOrUpdateSpecialOffer(
            @RequestBody SpecialOffer specialOffers) throws OsirisValidationException, OsirisException {
        if (specialOffers.getId() != null)
            validationHelper.validateReferential(specialOffers, true, "specialOffers");
        validationHelper.validateString(specialOffers.getCode(), true, 20, "code");
        validationHelper.validateString(specialOffers.getLabel(), true, 100, "label");
        validationHelper.validateString(specialOffers.getCustomLabel(), true, 100, "customLabel");

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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/city")
    public ResponseEntity<City> addOrUpdateCity(
            @RequestBody City cities) throws OsirisValidationException, OsirisException {
        if (cities.getId() != null)
            validationHelper.validateReferential(cities, true, "cities");
        validationHelper.validateString(cities.getCode(), true, 20, "Code");
        validationHelper.validateString(cities.getLabel(), true, 100, "Label");
        validationHelper.validateString(cities.getPostalCode(), false, 10, "PostalCode");
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
            @RequestParam String city, @RequestParam(required = false) String postalCode) {
        return new ResponseEntity<List<City>>(cityService.getCitiesByCountry(countryId, city, postalCode),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/cities/search/competent-authority")
    public ResponseEntity<List<City>> getCitiesForCompetentAuthority(@RequestParam Integer competentAuthorityId)
            throws OsirisValidationException {
        CompetentAuthority competentAuthority = competentAuthorityService.getCompetentAuthority(competentAuthorityId);
        if (competentAuthority == null)
            throw new OsirisValidationException("competentAuthorityId");

        return new ResponseEntity<List<City>>(competentAuthority.getCities(), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-types")
    public ResponseEntity<List<BillingType>> getBillingTypes() {
        return new ResponseEntity<List<BillingType>>(billingTypeService.getBillingTypes(), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-types/debour")
    public ResponseEntity<List<BillingType>> getBillingTypesDebour() {
        return new ResponseEntity<List<BillingType>>(billingTypeService.getBillingTypesDebour(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/billing-type")
    public ResponseEntity<BillingType> addOrUpdateBillingType(
            @RequestBody BillingType billingType) throws OsirisValidationException, OsirisException {
        if (billingType.getId() != null)
            validationHelper.validateReferential(billingType, true, "billingType");
        validationHelper.validateString(billingType.getCode(), true, 20, "code");
        validationHelper.validateString(billingType.getLabel(), true, 255, "label");
        validationHelper.validateReferential(billingType.getVat(), billingType.getIsOverrideVat(), "Vat");

        if (billingType.getIsUsedForFormaliteRff() == null)
            billingType.setIsUsedForFormaliteRff(false);

        if (billingType.getIsUsedForInsertionRff() == null)
            billingType.setIsUsedForInsertionRff(false);

        if (!billingType.getIsOverrideVat())
            billingType.setVat(null);

        return new ResponseEntity<BillingType>(billingTypeService.addOrUpdateBillingType(billingType), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/billing-items")
    public ResponseEntity<List<BillingItem>> getBillingItems() {
        return new ResponseEntity<List<BillingItem>>(billingItemService.getBillingItems(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/vat")
    public ResponseEntity<Vat> addOrUpdateVat(
            @RequestBody Vat vat) throws OsirisValidationException, OsirisException {
        if (vat.getId() != null)
            validationHelper.validateReferential(vat, true, "vat");
        validationHelper.validateReferential(vat.getAccountingAccount(), true, "AccountingAccount");
        validationHelper.validateString(vat.getCode(), true, 20, "Code");
        validationHelper.validateString(vat.getLabel(), true, 100, "Label");
        validationHelper.validateBigDecimal(vat.getRate(), true, "Rate");

        return new ResponseEntity<Vat>(vatService.addOrUpdateVat(vat), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/regions")
    public ResponseEntity<List<Region>> getRegions() {
        return new ResponseEntity<List<Region>>(regionService.getRegions(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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
            @RequestParam(required = false) String authority) {
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

    @GetMapping(inputEntryPoint + "/competent-authorities/search/competent-authority-type")
    public ResponseEntity<List<CompetentAuthority>> getCompetentAuthoritiesByType(Integer competentAuthorityTypeId)
            throws OsirisValidationException {
        if (competentAuthorityTypeId == null)
            throw new OsirisValidationException("competentAuthorityTypeId");
        return new ResponseEntity<List<CompetentAuthority>>(
                competentAuthorityService.getCompetentAuthorityByAuthorityType(competentAuthorityTypeId),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/competent-authority-types")
    public ResponseEntity<List<CompetentAuthorityType>> getCompetentAuthorityTypes() {
        return new ResponseEntity<List<CompetentAuthorityType>>(
                competentAuthorityTypeService.getCompetentAuthorityTypes(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @GetMapping(inputEntryPoint + "/competent-authority")
    public ResponseEntity<CompetentAuthority> getCompetentAuthority(@RequestParam Integer id) {
        return new ResponseEntity<CompetentAuthority>(competentAuthorityService.getCompetentAuthority(id),
                HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/competent-authority")
    public ResponseEntity<CompetentAuthority> addOrUpdateCompetentAuthority(
            @RequestBody CompetentAuthority competentAuthority)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (competentAuthority.getId() != null)
            validationHelper.validateReferential(competentAuthority, true, "competentAuthorities");
        validationHelper.validateString(competentAuthority.getLabel(), true, 200, "label");
        validationHelper.validateString(competentAuthority.getSchedulle(), false, 2000, "Schedulle");
        validationHelper.validateString(competentAuthority.getIntercommunityVat(), false, 20, "IntercommunityVat");
        validationHelper.validateString(competentAuthority.getInpiReference(), false, 250, "InpiReference");
        validationHelper.validateReferential(competentAuthority.getProvider(), false, "Provider");
        validationHelper.validateReferential(competentAuthority.getCompetentAuthorityType(), true,
                "CompetentAuthorityType");
        if (competentAuthority.getCities() == null && competentAuthority.getDepartments() == null
                && competentAuthority.getRegions() == null)
            throw new OsirisValidationException("Cities or Departments or Regions");

        if (competentAuthority.getCities() != null)
            for (City city : competentAuthority.getCities())
                validationHelper.validateReferential(city, false, "city");

        if (competentAuthority.getRegions() != null)
            for (Region region : competentAuthority.getRegions())
                validationHelper.validateReferential(region, false, "region");

        if (competentAuthority.getDepartments() != null)
            for (Department department : competentAuthority.getDepartments())
                validationHelper.validateReferential(department, false, "department");

        validationHelper.validateString(competentAuthority.getAzureCustomReference(), false, 250,
                "azureCustomReference");
        validationHelper.validateString(competentAuthority.getContact(), false, 40, "Contact");
        validationHelper.validateString(competentAuthority.getMailRecipient(), false, 60, "MailRecipient");
        validationHelper.validateString(competentAuthority.getAddress(), false, 200, "Address");
        validationHelper.validateString(competentAuthority.getPostalCode(), false, 10, "PostalCode");
        validationHelper.validateString(competentAuthority.getCedexComplement(), false, 40, "CedexComplement");
        validationHelper.validateReferential(competentAuthority.getCity(), false, "City");
        validationHelper.validateReferential(competentAuthority.getCountry(), false, "Country");

        return new ResponseEntity<CompetentAuthority>(
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/weekdays")
    public ResponseEntity<List<WeekDay>> getWeekDays() {
        return new ResponseEntity<List<WeekDay>>(weekDayService.getWeekDays(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
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

        attachmentService.disableAttachment(attachment);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/attachment/upload")
    public ResponseEntity<List<Attachment>> uploadAttachment(@RequestParam MultipartFile file,
            @RequestParam(required = false) Integer idEntity, @RequestParam(required = false) String codeEntity,
            @RequestParam String entityType,
            @RequestParam Integer idAttachmentType,
            @RequestParam String filename, @RequestParam Boolean replaceExistingAttachementType,
            @RequestParam(name = "pageSelection", required = false) String pageSelection,
            @RequestParam(name = "typeDocumentCode", required = false) String typeDocumentCode)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (idAttachmentType == null)
            throw new OsirisValidationException("idAttachmentType");

        AttachmentType attachmentType = attachmentTypeService.getAttachmentType(idAttachmentType);

        if (attachmentType == null)
            throw new OsirisValidationException("attachmentType");

        if (filename == null || filename.equals(""))
            throw new OsirisValidationException("filename");

        if (idEntity == null && codeEntity == null)
            throw new OsirisValidationException("idEntity or codeEntity");

        if (entityType == null)
            throw new OsirisValidationException("entityType");

        TypeDocument typeDocument = null;
        if (typeDocumentCode != null) {
            typeDocument = typeDocumentService.getTypeDocumentByCode(typeDocumentCode);
            if (typeDocument == null)
                throw new OsirisValidationException("typeDocument");
        }

        if (!entityType.equals(Tiers.class.getSimpleName())
                && !entityType.equals("Ofx")
                && !entityType.equals(MissingAttachmentQuery.class.getSimpleName())
                && !entityType.equals(Responsable.class.getSimpleName())
                && !entityType.equals(Quotation.class.getSimpleName())
                && !entityType.equals(CustomerOrder.class.getSimpleName())
                && !entityType.equals(Provider.class.getSimpleName())
                && !entityType.equals(CompetentAuthority.class.getSimpleName())
                && !entityType.equals(Provision.class.getSimpleName())
                && !entityType.equals(Affaire.class.getSimpleName())
                && !entityType.equals(AssoServiceDocument.class.getSimpleName())
                && !entityType.equals(TypeDocument.class.getSimpleName())
                && !entityType.equals(Invoice.class.getSimpleName()))

            throw new OsirisValidationException("entityType");

        return new ResponseEntity<List<Attachment>>(
                attachmentService.addAttachment(file, idEntity, codeEntity, entityType, attachmentType, filename,
                        replaceExistingAttachementType, pageSelection, typeDocument),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment/download")
    @Transactional
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
                throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
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
                throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
            }
            if (mimeType == null)
                mimeType = "application/octet-stream";
            headers.set("content-type", mimeType);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/all")
    public ResponseEntity<Boolean> reindexAll() throws OsirisException {
        postService.reindexPosts();
        invoiceService.reindexInvoices();
        tiersService.reindexTiers();
        refundService.reindexRefunds();
        responsableService.reindexResponsable();
        quotationService.reindexQuotation();
        customerOrderService.reindexCustomerOrder();
        assoAffaireOrderService.reindexAffaires();
        affaireService.reindexAffaire();
        bankTransfertService.reindexBankTransfert();
        paymentService.reindexPayments();
        directDebitTransfertService.reindexDirectDebitTransfert();

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/directDebitTransfert")
    public ResponseEntity<Boolean> reindexDirectDebitTransfert() throws OsirisException {
        directDebitTransfertService.reindexDirectDebitTransfert();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/bankTransfert")
    public ResponseEntity<Boolean> reindexBankTransfert() throws OsirisException {
        bankTransfertService.reindexBankTransfert();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/affaire")
    public ResponseEntity<Boolean> reindexAffaire() throws OsirisException {
        affaireService.reindexAffaire();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/post")
    public ResponseEntity<Boolean> reindexPost() throws OsirisException {
        postService.reindexPosts();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/assoAffaireOrder")
    public ResponseEntity<Boolean> reindexAffaires() throws OsirisException {
        assoAffaireOrderService.reindexAffaires();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/payment")
    public ResponseEntity<Boolean> reindexPayments() throws OsirisException {
        paymentService.reindexPayments();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/customerOrder")
    public ResponseEntity<Boolean> reindexCustomerOrder() throws OsirisException {
        customerOrderService.reindexCustomerOrder();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/quotation")
    public ResponseEntity<Boolean> reindexQuotation() throws OsirisException {
        quotationService.reindexQuotation();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/responsable")
    public ResponseEntity<Boolean> reindexResponsable() throws OsirisException {
        responsableService.reindexResponsable();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/refund")
    public ResponseEntity<Boolean> reindexRefunds() throws OsirisException {
        refundService.reindexRefunds();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/tiers")
    public ResponseEntity<Boolean> reindexTiers() throws OsirisException {
        tiersService.reindexTiers();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @GetMapping(inputEntryPoint + "/index/reindex/invoice")
    public ResponseEntity<Boolean> reindexInvoices() throws OsirisException {
        invoiceService.reindexInvoices();
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

    @GetMapping(inputEntryPoint + "/customer-mail/send/immediatly")
    public ResponseEntity<Boolean> sendCustomerMailImmediatly(@RequestParam Integer idCustomerMail)
            throws OsirisValidationException, OsirisException {
        CustomerMail customerMail = customerMailService.getMail(idCustomerMail);
        if (customerMail == null)
            throw new OsirisValidationException("customerMail");
        customerMailService.sendCustomerMailImmediatly(customerMail);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-mail/cancel")
    public ResponseEntity<Boolean> cancelCustomerMail(@RequestParam Integer idCustomerMail)
            throws OsirisValidationException, OsirisException {
        CustomerMail customerMail = customerMailService.getMail(idCustomerMail);
        if (customerMail == null)
            throw new OsirisValidationException("customerMail");

        if (customerMail.getIsSent() != null && customerMail.getIsSent() == false
                && customerMail.getIsCancelled() != null && customerMail.getIsCancelled() == false
                && customerMail.getToSendAfter() != null && customerMail.getToSendAfter().isAfter(LocalDateTime.now()))
            customerMailService.cancelCustomerMail(customerMail);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-mail/quotation")
    public ResponseEntity<List<CustomerMail>> getCustomerMailByQuotation(@RequestParam Integer idQuotation)
            throws OsirisValidationException, OsirisException {
        Quotation quotation = new Quotation();
        quotation.setId(idQuotation);
        quotation = (Quotation) validationHelper.validateReferential(quotation, true, "Quotation");
        return new ResponseEntity<List<CustomerMail>>(customerMailService.getMailsByQuotation(quotation),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-mail/customer-order")
    public ResponseEntity<List<CustomerMail>> getCustomerMailByCustomerOrder(@RequestParam Integer idCustomerOrder)
            throws OsirisValidationException, OsirisException {
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setId(idCustomerOrder);
        customerOrder = (CustomerOrder) validationHelper.validateReferential(customerOrder, true, "CustomerOrder");
        return new ResponseEntity<List<CustomerMail>>(customerMailService.getMailsByCustomerOrder(customerOrder),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-mail/tiers")
    public ResponseEntity<List<CustomerMail>> getCustomerMailByTiers(@RequestParam Integer idTiers)
            throws OsirisValidationException, OsirisException {
        Tiers tiers = new Tiers();
        tiers.setId(idTiers);
        tiers = (Tiers) validationHelper.validateReferential((Tiers) tiers, true, "tiers");
        return new ResponseEntity<List<CustomerMail>>(customerMailService.getMailsByTiers(tiers),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-mail/responsable")
    public ResponseEntity<List<CustomerMail>> getCustomerMailByResponsable(@RequestParam Integer idResponsable)
            throws OsirisValidationException, OsirisException {
        Responsable responsable = new Responsable();
        responsable.setId(idResponsable);
        responsable = (Responsable) validationHelper.validateReferential(responsable, true, "Responsable");
        return new ResponseEntity<List<CustomerMail>>(customerMailService.getMailsByResponsable(responsable),
                HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/customer-mail/confrere")
    public ResponseEntity<List<CustomerMail>> getCustomerMailByConfrere(@RequestParam Integer idConfrere)
            throws OsirisValidationException, OsirisException {
        Confrere confrere = new Confrere();
        confrere.setId(idConfrere);
        confrere = (Confrere) validationHelper.validateReferential(confrere, true, "Responsable");
        return new ResponseEntity<List<CustomerMail>>(customerMailService.getMailsByConfrere(confrere),
                HttpStatus.OK);
    }
}