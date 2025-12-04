package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeFieldType;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.NoticeType;
import com.jss.osiris.modules.osiris.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.osiris.quotation.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServiceTypeService serviceTypeService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    BatchService batchService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Autowired
    AssoServiceDocumentService assoServiceDocumentService;

    @Autowired
    AssoServiceFieldTypeService assoServiceFieldTypeService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    NotificationService notificationService;

    @Override
    public Service getService(Integer id) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent())
            return service.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Service addOrUpdateServiceFromUser(Service service) throws OsirisException {
        addOrUpdateService(service);

        if (service.getAssoAffaireOrder() != null && service.getAssoAffaireOrder().getCustomerOrder() != null)
            pricingHelper.getAndSetInvoiceItemsForQuotation(service.getAssoAffaireOrder().getCustomerOrder(), true);
        else if (service.getAssoAffaireOrder() != null && service.getAssoAffaireOrder().getQuotation() != null)
            pricingHelper.getAndSetInvoiceItemsForQuotation(service.getAssoAffaireOrder().getQuotation(), true);

        notificationService.notifyInformationAddToService(service);

        return service;
    }

    @Override
    public Service addOrUpdateService(Service service) throws OsirisException {
        computeServiceLabel(service);
        if (service.getCustomLabel() != null) {
            if (service.getCustomLabel().trim().length() == 0)
                service.setCustomLabel(null);
        }

        serviceRepository.save(service);

        if (service.getAssoAffaireOrder().getCustomerOrder() != null) {
            batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER, service.getAssoAffaireOrder().getId());
            batchService.declareNewBatch(Batch.REINDEX_CUSTOMER_ORDER,
                    service.getAssoAffaireOrder().getCustomerOrder().getId());
        } else if (service.getAssoAffaireOrder().getQuotation() != null)
            batchService.declareNewBatch(Batch.REINDEX_QUOTATION, service.getAssoAffaireOrder().getQuotation().getId());

        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addOrUpdateServices(List<ServiceType> services, Integer assoAffaireOrderId,
            String customLabel, Affaire affaire)
            throws OsirisException {

        AssoAffaireOrder assoAffaireOrder = assoAffaireOrderService.getAssoAffaireOrder(assoAffaireOrderId);

        if (assoAffaireOrder != null) {
            List<Service> servicesGenerated = generateServiceInstanceFromMultiServiceTypes(services, customLabel,
                    affaire);
            for (Service service : servicesGenerated) {
                service.setAssoAffaireOrder(assoAffaireOrder);
                addOrUpdateServiceFromUser(service);
                linkNewServiceWithAsso(service);
            }
        }
        return true;
    }

    private void linkNewServiceWithAsso(Service newService) throws OsirisException {
        for (AssoServiceDocument assoServiceDocument : newService.getAssoServiceDocuments()) {
            assoServiceDocument.setService(newService);
            assoServiceDocumentService.addOrUpdateAssoServiceDocument(assoServiceDocument);
        }
        for (AssoServiceFieldType assoServiceFieldType : newService.getAssoServiceFieldTypes()) {
            assoServiceFieldType.setService(newService);
            assoServiceFieldTypeService.addOrUpdateServiceFieldType(assoServiceFieldType);
        }
        for (Provision provision : newService.getProvisions()) {
            provision.setService(newService);
            provisionService.addOrUpdateProvision(provision);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteServiceFromUser(Service service) {
        service = getService(service.getId());
        deleteService(service, false);

        if (service.getAssoAffaireOrder().getCustomerOrder() != null
                && service.getAssoAffaireOrder().getCustomerOrder().getQuotations() != null
                && service.getAssoAffaireOrder().getCustomerOrder().getQuotations().size() > 0)
            notificationService.notifyQuotationModified(service.getAssoAffaireOrder().getCustomerOrder());
        return true;
    }

    private Boolean deleteService(Service service, boolean permanentlyDeleteAttachments) {
        if (service.getProvisions() != null && service.getProvisions().size() > 0) {
            for (Provision provision : service.getProvisions()) {
                if (provision.getAttachments() != null && provision.getAttachments().size() > 0) {
                    for (Attachment attachment : provision.getAttachments()) {
                        if (permanentlyDeleteAttachments)
                            attachmentService.definitivelyDeleteAttachment(attachment);
                        else
                            attachmentService.cleanAttachmentForDelete(attachment);
                    }
                }
            }
        }
        serviceRepository.delete(service);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Service> generateServiceInstanceFromMultiServiceTypes(List<ServiceType> serviceTypes,
            String customLabel, Affaire affaire) throws OsirisException {

        List<Service> services = new ArrayList<Service>();

        List<ServiceType> mergeableTypes = serviceTypes.stream().filter(s -> s.getIsMergeable()).toList();
        if (!mergeableTypes.isEmpty()) {
            services.add(getServiceForMultiServiceTypesAndAffaire(mergeableTypes, customLabel, affaire));
        }

        List<ServiceType> serviceTypeNonMergeables = serviceTypes.stream().filter(s -> !s.getIsMergeable())
                .toList();
        for (ServiceType serviceType : serviceTypeNonMergeables) {
            ArrayList<ServiceType> serviceTypeList = new ArrayList<ServiceType>();
            serviceTypeList.add(serviceType);
            services.add(
                    getServiceForMultiServiceTypesAndAffaire(serviceTypeList, customLabel, affaire));
        }

        return services;
    }

    private Service getServiceForMultiServiceTypesAndAffaire(List<ServiceType> serviceTypes,
            String customLabel, Affaire affaire)
            throws OsirisException {

        ArrayList<AssoServiceDocument> assoServiceDocuments = new ArrayList<AssoServiceDocument>();
        ArrayList<String> typeDocumentCodes = new ArrayList<String>();
        ArrayList<AssoServiceFieldType> assoServiceFieldTypes = new ArrayList<AssoServiceFieldType>();
        ArrayList<Integer> serviceFieldTypeIds = new ArrayList<Integer>();
        Boolean alreadyHasFurtherInformationServiceFieldType = false;

        ArrayList<AssoServiceProvisionType> assoServiceProvisionTypes = new ArrayList<AssoServiceProvisionType>();

        Service service = new Service();
        service.setServiceTypes(serviceTypes);

        for (ServiceType serviceType : serviceTypes) {
            // fill all provisions type in list
            if (!serviceType.getAssoServiceProvisionTypes().isEmpty())
                assoServiceProvisionTypes.addAll(serviceType.getAssoServiceProvisionTypes());

            if (serviceType.getAssoServiceTypeDocuments() != null)
                for (AssoServiceTypeDocument assoServiceTypeDocument : serviceType.getAssoServiceTypeDocuments()) {
                    AssoServiceDocument newAssoServiceDocument = getAssoServiceDocumentFromAssoServiceTypeDocument(
                            assoServiceTypeDocument, service);
                    String docLabel = assoServiceTypeDocument.getTypeDocument().getCustomLabel() != null
                            ? assoServiceTypeDocument.getTypeDocument().getCustomLabel()
                            : assoServiceTypeDocument.getTypeDocument().getCode();
                    if (newAssoServiceDocument.getTypeDocument() != null
                            && !typeDocumentCodes.contains(docLabel)) {
                        typeDocumentCodes.add(docLabel);
                        assoServiceDocuments.add(newAssoServiceDocument);
                    }
                }

            // Service Fields
            if (serviceType.getAssoServiceTypeFieldTypes() != null)
                for (AssoServiceTypeFieldType assoServiceTypeFieldType : serviceType
                        .getAssoServiceTypeFieldTypes()) {
                    AssoServiceFieldType newAssoServiceFieldType = getAssoServiceFieldTypeFromAssoServiceTypeFieldType(
                            assoServiceTypeFieldType, service);
                    if (newAssoServiceFieldType.getServiceFieldType() != null
                            && !serviceFieldTypeIds
                                    .contains(assoServiceTypeFieldType.getServiceFieldType().getId())) {
                        serviceFieldTypeIds.add(assoServiceTypeFieldType.getServiceFieldType().getId());
                        assoServiceFieldTypes.add(newAssoServiceFieldType);

                        if (!alreadyHasFurtherInformationServiceFieldType
                                && newAssoServiceFieldType.getServiceFieldType().getId()
                                        .equals(constantService.getFurtherInformationServiceFieldType().getId()))
                            alreadyHasFurtherInformationServiceFieldType = true;

                    }
                }
        }

        // Merge provisions Type before create instances
        if (service.getProvisions() != null && service.getProvisions().size() > 0)
            service.getProvisions().addAll(mergeProvisionTypes(assoServiceProvisionTypes, service, affaire));
        else
            service.setProvisions(mergeProvisionTypes(assoServiceProvisionTypes, service, affaire));

        // Always add further information field
        if (!alreadyHasFurtherInformationServiceFieldType) {
            AssoServiceFieldType newAssoServiceFieldType = new AssoServiceFieldType();
            newAssoServiceFieldType.setIsMandatory(false);
            newAssoServiceFieldType.setService(service);
            newAssoServiceFieldType.setServiceFieldType(constantService.getFurtherInformationServiceFieldType());
            assoServiceFieldTypes.add(newAssoServiceFieldType);
        }

        service.setAssoServiceFieldTypes(assoServiceFieldTypes);
        service.setAssoServiceDocuments(assoServiceDocuments);
        service = computeServiceLabel(service);
        return service;
    }

    private List<Provision> mergeProvisionTypes(ArrayList<AssoServiceProvisionType> assoServiceProvisionTypes,
            Service service, Affaire affaire) throws OsirisException {
        List<Provision> newProvisions = new ArrayList<Provision>();

        Set<ProvisionType> possibleProvisionTypes = assoServiceProvisionTypes.stream()
                .filter(p -> !p.getProvisionType().getProvisionScreenType().getCode()
                        .equals(ProvisionScreenType.ANNOUNCEMENT))
                .map(a -> a.getProvisionType()).collect(Collectors.toSet());

        for (ProvisionType provisionTypePossible : possibleProvisionTypes) {
            List<AssoServiceProvisionType> provisionTypeMergeable = assoServiceProvisionTypes.stream()
                    .filter(s -> Boolean.TRUE.equals(s.getProvisionType().getIsMergeable())
                            && s.getProvisionType().getId().equals(provisionTypePossible.getId())
                            && !s.getProvisionType().getProvisionScreenType().getCode()
                                    .equals(ProvisionScreenType.ANNOUNCEMENT))
                    .toList();

            if (provisionTypeMergeable != null && provisionTypeMergeable.size() > 0) {
                Boolean isPriority = getPriorityFromAssoServiceProvisionTypesList(assoServiceProvisionTypes);
                Provision mergeableProvision = generateProvisionFromProvisionType(
                        provisionTypeMergeable.get(0).getProvisionType(),
                        service, provisionTypeMergeable.get(0), isPriority);

                newProvisions.add(mergeableProvision);
            }

            List<AssoServiceProvisionType> provisionTypeNonMergeable = assoServiceProvisionTypes.stream()
                    .filter(s -> (s.getProvisionType().getIsMergeable() == null
                            || !s.getProvisionType().getIsMergeable())
                            && s.getProvisionType().getId().equals(provisionTypePossible.getId())
                            && !s.getProvisionType().getProvisionScreenType().getCode()
                                    .equals(ProvisionScreenType.ANNOUNCEMENT))
                    .toList();

            for (AssoServiceProvisionType assoServiceProvisionType : provisionTypeNonMergeable) {
                if (provisionTypeNonMergeable != null && provisionTypeNonMergeable.size() > 0) {
                    Provision nonMergeableProvision = generateProvisionFromProvisionType(
                            assoServiceProvisionType.getProvisionType(),
                            service, assoServiceProvisionType, assoServiceProvisionType.getIsPriority());
                    newProvisions.add(nonMergeableProvision);
                }
            }

        }

        // AL merge

        List<AssoServiceProvisionType> provisionTypeMergeable = assoServiceProvisionTypes.stream()
                .filter(s -> Boolean.TRUE.equals(s.getProvisionType().getIsMergeable())
                        && s.getProvisionType().getProvisionScreenType().getCode()
                                .equals(ProvisionScreenType.ANNOUNCEMENT))
                .toList();

        if (provisionTypeMergeable != null && provisionTypeMergeable.size() > 0) {
            Boolean isPriority = getPriorityFromAssoServiceProvisionTypesList(assoServiceProvisionTypes);
            Provision mergeableProvision = generateProvisionFromProvisionType(
                    provisionTypeMergeable.get(0).getProvisionType(),
                    service, provisionTypeMergeable.get(0), isPriority);

            if (provisionTypeMergeable.size() > 1) {
                mergeableProvision = generateProvisionFromProvisionType(
                        this.constantService.getProvisionTypeCharacterAnnouncement(), service,
                        assoServiceProvisionTypes.get(0), assoServiceProvisionTypes.get(0).getIsPriority());
            }

            mergeableProvision = completeNoticesFromAnnouncementProvision(mergeableProvision,
                    provisionTypeMergeable, affaire);
            mergeableProvision.setIsRedactedByJss(true);

            newProvisions.add(mergeableProvision);
        }

        List<AssoServiceProvisionType> provisionTypeNonMergeable = assoServiceProvisionTypes.stream()
                .filter(s -> (s.getProvisionType().getIsMergeable() == null
                        || !s.getProvisionType().getIsMergeable())
                        && s.getProvisionType().getProvisionScreenType().getCode()
                                .equals(ProvisionScreenType.ANNOUNCEMENT))
                .toList();

        for (AssoServiceProvisionType assoServiceProvisionType : provisionTypeNonMergeable) {
            if (provisionTypeNonMergeable != null && provisionTypeNonMergeable.size() > 0) {
                Provision nonMergeableProvision = generateProvisionFromProvisionType(
                        assoServiceProvisionType.getProvisionType(),
                        service, assoServiceProvisionType, assoServiceProvisionType.getIsPriority());

                completeNoticesFromAnnouncementProvision(nonMergeableProvision, provisionTypeNonMergeable,
                        affaire);
                nonMergeableProvision.setIsRedactedByJss(true);

                newProvisions.add(nonMergeableProvision);
            }
        }

        return newProvisions;
    }

    private boolean getPriorityFromAssoServiceProvisionTypesList(
            List<AssoServiceProvisionType> assoServiceProvisionTypes) {
        if (assoServiceProvisionTypes != null) {
            assoServiceProvisionTypes.sort(new Comparator<AssoServiceProvisionType>() {
                @Override
                public int compare(AssoServiceProvisionType arg0, AssoServiceProvisionType arg1) {
                    return arg1.getComplexity().compareTo(arg0.getComplexity());
                }
            });

            for (AssoServiceProvisionType asso : assoServiceProvisionTypes) {
                if (asso.getIsPriority()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Provision completeNoticesFromAnnouncementProvision(Provision provision,
            List<AssoServiceProvisionType> assoAnnouncementMergeable, Affaire affaire) throws OsirisException {
        List<NoticeType> noticeTypes = new ArrayList<>();
        List<NoticeTypeFamily> noticeTypeFamilies = new ArrayList<>();
        String noticeTemplate = "";
        List<Integer> noticeTypeIds = new ArrayList<>();
        List<Integer> noticeTypeFamilyIds = new ArrayList<>();

        Announcement announcement = new Announcement();
        announcement.setIsHeader(false);
        announcement.setIsHeaderFree(false);
        announcement.setIsProofReadingDocument(false);
        announcement.setAnnouncementStatus(
                announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_NEW));

        if (provision.getProvisionType().getProvisionScreenType().getId()
                .equals(constantService.getProvisionScreenTypeAnnouncement().getId())) {
            if (provision.getAnnouncement() == null)
                provision.setAnnouncement(announcement);

            for (AssoServiceProvisionType asso : assoAnnouncementMergeable) {
                if (asso.getNoticeTypeFamily() != null
                        && !noticeTypeFamilyIds.contains(asso.getNoticeTypeFamily().getId())) {
                    noticeTypeFamilies.add(asso.getNoticeTypeFamily());
                    noticeTypeFamilyIds.add(asso.getNoticeTypeFamily().getId());
                }
                if (asso.getNoticeType() != null
                        && !noticeTypeIds.contains(asso.getNoticeType().getId())) {
                    noticeTypes.add(asso.getNoticeType());
                    noticeTypeIds.add(asso.getNoticeType().getId());
                }
            }
            if (noticeTypeFamilies.size() == 1) {
                provision.getAnnouncement()
                        .setNoticeTypeFamily(assoAnnouncementMergeable.get(0).getNoticeTypeFamily());
                provision.getAnnouncement().setNoticeTypes(noticeTypes);
            }
            provision.getAnnouncement().setNotice(noticeTemplate);
            provision.getAnnouncement().setPublicationDate(LocalDate.now());
            if (affaire != null && affaire.getCity() != null)
                provision.getAnnouncement().setDepartment(affaire.getCity().getDepartment());
        }
        return provision;
    }

    private Provision generateProvisionFromProvisionType(ProvisionType provisionType, Service service,
            AssoServiceProvisionType assoServiceProvisionType, Boolean isPriority)
            throws OsirisException {
        Provision provision = new Provision();
        if (provisionType != null) {
            provision.setProvisionFamilyType(provisionType.getProvisionFamilyType());
            provision.setProvisionType(provisionType);
            provision.setService(service);

            provision.setComplexity(assoServiceProvisionType.getComplexity());
            if (provision.getComplexity() == null)
                provision.setComplexity(4);

            provision.setIsPriority(isPriority);
            if (provision.getIsPriority() == null)
                provision.setIsPriority(false);
            provision.setIsLogo(false);
            provision.setIsRedactedByJss(false);
            provision.setIsBaloPackage(false);
            provision.setIsPublicationPaper(false);
            provision.setIsPublicationReceipt(false);
            provision.setIsPublicationFlag(false);
            provision.setIsNantissementDeposit(false);
            provision.setIsSocialShareNantissementRedaction(false);
            provision.setIsBusinnessNantissementRedaction(false);
            provision.setIsSellerPrivilegeRedaction(false);
            provision.setIsTreatmentMultipleModiciation(false);
            provision.setIsVacationMultipleModification(false);
            provision.setIsRegisterPurchase(false);
            provision.setIsRegisterInitials(false);
            provision.setIsRegisterShippingCosts(false);
            provision.setIsDisbursement(false);
            provision.setIsFeasibilityStudy(false);
            provision.setIsChronopostFees(false);
            provision.setIsApplicationFees(false);
            provision.setIsBankCheque(false);
            provision.setIsComplexeFile(false);
            provision.setIsBilan(false);
            provision.setIsDocumentScanning(false);
            provision.setIsEmergency(false);
            provision.setIsRneUpdate(false);
            provision.setIsVacationUpdateBeneficialOwners(false);
            provision.setIsFormalityAdditionalDeclaration(false);
            provision.setIsCorrespondenceFees(false);
            provision.setIsSupplyFullBeCopy(false);
            provision.setIsNotifyBalo(Boolean.TRUE.equals(assoServiceProvisionType.getIsNotifyBalo()));
            provision.setIsNotifyBodacc(Boolean.TRUE.equals(assoServiceProvisionType.getIsNotifyBodacc()));
            provision.setIsNotifyJo(Boolean.TRUE.equals(assoServiceProvisionType.getIsNotifyJo()));
        }
        return provision;
    }

    private AssoServiceDocument getAssoServiceDocumentFromAssoServiceTypeDocument(
            AssoServiceTypeDocument assoServiceTypeDocument, Service service) {
        AssoServiceDocument assoServiceDocument = new AssoServiceDocument();
        assoServiceDocument.setIsMandatory(assoServiceTypeDocument.getIsMandatory());
        assoServiceDocument.setService(service);
        assoServiceDocument.setTypeDocument(assoServiceTypeDocument.getTypeDocument());
        return assoServiceDocument;
    }

    private AssoServiceFieldType getAssoServiceFieldTypeFromAssoServiceTypeFieldType(
            AssoServiceTypeFieldType assoServiceTypeFieldType, Service service) {
        AssoServiceFieldType assoServiceFieldType = new AssoServiceFieldType();
        assoServiceFieldType.setIsMandatory(assoServiceTypeFieldType.getIsMandatory());
        assoServiceFieldType.setService(service);
        assoServiceFieldType.setServiceFieldType(assoServiceTypeFieldType.getServiceFieldType());
        return assoServiceFieldType;
    }

    private List<Provision> getProvisionsFromServiceType(ServiceType serviceType, Affaire affaire, Service service)
            throws OsirisException {
        ArrayList<Provision> provisions = new ArrayList<Provision>();
        if (serviceType.getAssoServiceProvisionTypes() != null)
            for (AssoServiceProvisionType assoServiceProvisionType : serviceType.getAssoServiceProvisionTypes()) {
                boolean shouldAdd = true;

                if (affaire.getEmployeeNumber() != null && affaire.getEmployeeNumber() > 0) {
                    if (assoServiceProvisionType.getMaxEmployee() != null
                            && affaire.getEmployeeNumber() > assoServiceProvisionType.getMaxEmployee())
                        shouldAdd = false;
                    if (assoServiceProvisionType.getMinEmployee() != null
                            && affaire.getEmployeeNumber() < assoServiceProvisionType.getMinEmployee())
                        shouldAdd = false;
                }

                if (affaire.getLegalForm() != null && assoServiceProvisionType.getFormeJuridiques() != null
                        && assoServiceProvisionType.getFormeJuridiques().size() > 0) {
                    boolean found = false;
                    for (FormeJuridique formeJuridique : assoServiceProvisionType.getFormeJuridiques()) {
                        if (formeJuridique.getCode().equals(affaire.getLegalForm().getCode())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        shouldAdd = false;
                }

                if (affaire.getApeCodes() != null && affaire.getApeCodes().length() > 0
                        && assoServiceProvisionType.getApeCodes() != null
                        && assoServiceProvisionType.getApeCodes().length() > 0) {
                    String[] apeCodesAffaire = affaire.getApeCodes().split(";");
                    String[] apeCodesService = assoServiceProvisionType.getApeCodes().split(";");
                    boolean found = false;
                    for (String apeCodeAffaire : apeCodesAffaire)
                        for (String apeCodeService : apeCodesService) {
                            if (apeCodeAffaire.trim().toUpperCase().equals(apeCodeService.trim().toUpperCase())) {
                                found = true;
                                break;
                            }
                        }
                    if (!found)
                        shouldAdd = false;
                }

                if (shouldAdd) {
                    provisions.add(
                            generateProvisionFromProvisionType(assoServiceProvisionType.getProvisionType(), service,
                                    assoServiceProvisionType, assoServiceProvisionType.getIsPriority()));
                }
            }
        return provisions;
    }

    @Transactional(rollbackFor = Exception.class)
    public Service modifyServiceType(List<ServiceType> serviceTypes, Service service) throws OsirisException {
        service = getService(service.getId());
        ArrayList<AssoServiceFieldType> assoToDelete = new ArrayList<AssoServiceFieldType>();

        for (ServiceType serviceType : serviceTypes) {
            serviceType = serviceTypeService.getServiceType(serviceType.getId());
            service.setServiceTypes(serviceTypes);
            service.setCustomLabel(null);
            service.setServiceLabelToDisplay(null);

            for (AssoServiceTypeFieldType serviceTypeFieldType : serviceType.getAssoServiceTypeFieldTypes()) {
                boolean found = false;
                for (AssoServiceFieldType serviceFieldType : service.getAssoServiceFieldTypes()) {
                    if (serviceFieldType.getServiceFieldType().getId().equals(serviceTypeFieldType.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    service.getAssoServiceFieldTypes()
                            .add(getAssoServiceFieldTypeFromAssoServiceTypeFieldType(serviceTypeFieldType, service));
                }
            }

            for (AssoServiceFieldType serviceFieldType : service.getAssoServiceFieldTypes()) {
                boolean found = false;
                for (AssoServiceTypeFieldType serviceTypeFieldType : serviceType.getAssoServiceTypeFieldTypes()) {
                    if (serviceFieldType.getServiceFieldType().getId().equals(serviceTypeFieldType.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    assoToDelete.add(serviceFieldType);
            }

            if (assoToDelete.size() > 0)
                service.getAssoServiceFieldTypes().removeAll(assoToDelete);

            // remove empty service association
            ArrayList<AssoServiceDocument> finalAssos = new ArrayList<AssoServiceDocument>();
            if (service.getAssoServiceDocuments() != null && service.getAssoServiceDocuments().size() > 0) {
                for (AssoServiceDocument asso : service.getAssoServiceDocuments()) {
                    if (asso.getAttachments() != null && asso.getAttachments().size() > 0)
                        finalAssos.add(asso);
                }
            }
            // Add new ones
            ArrayList<AssoServiceDocument> newAssos = new ArrayList<AssoServiceDocument>();
            if (serviceType.getAssoServiceTypeDocuments() != null
                    && serviceType.getAssoServiceTypeDocuments().size() > 0) {
                for (AssoServiceTypeDocument assoType : serviceType.getAssoServiceTypeDocuments()) {
                    Boolean found = false;
                    for (AssoServiceDocument assoService : finalAssos) {
                        if (assoService.getTypeDocument().getCode().equals(assoType.getTypeDocument().getCode())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        newAssos.add(getAssoServiceDocumentFromAssoServiceTypeDocument(assoType, service));
                    }
                }
            }
            if (newAssos.size() > 0)
                finalAssos.addAll(newAssos);

            service.getAssoServiceDocuments().clear();
            if (finalAssos.size() > 0)
                for (AssoServiceDocument asso : finalAssos)
                    service.getAssoServiceDocuments().add(asso);

            List<Provision> newProvisions = getProvisionsFromServiceType(serviceType,
                    service.getAssoAffaireOrder().getAffaire(), service);

            for (Provision provision : newProvisions)
                provisionService.addOrUpdateProvision(provision);
            service.getProvisions().addAll(newProvisions);
        }

        return addOrUpdateService(service);
    }

    private Service computeServiceLabel(Service service) {
        if (service != null && service.getServiceTypes() != null) {
            if (service.getCustomLabel() == null || service.getCustomLabel().length() == 0)
                service.setServiceLabelToDisplay(String.join(" / ", service.getServiceTypes().stream()
                        .map(s -> s.getLabel()).collect(Collectors.toList())));
            else
                service.setServiceLabelToDisplay(service.getCustomLabel());

            if (service.getServiceLabelToDisplay() != null && service.getServiceLabelToDisplay().length() > 2000)
                service.setServiceLabelToDisplay(service.getServiceLabelToDisplay().substring(0, 1999));
        }
        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> getAttachmentsForProvisionOfService(Service service) throws OsirisException {
        service = getService(service.getId());
        List<Integer> attachmentTypeIdsDone = new ArrayList<Integer>();
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (service.getProvisions() != null)
            for (Provision provision : service.getProvisions())
                if (provision.getAttachments() != null)
                    for (Attachment attachment : attachmentService.sortAttachmentByDateDesc(provision.getAttachments()))
                        if ((attachment.getIsDisabled() == null || attachment.getIsDisabled() == false)
                                && !attachmentTypeIdsDone.contains(attachment.getAttachmentType().getId())
                                && (attachment.getAttachmentType().getIsToSentOnFinalizationMail() != null
                                        && attachment.getAttachmentType().getIsToSentOnFinalizationMail()
                                        || attachment.getAttachmentType().getIsToSentOnUpload() != null
                                                && attachment.getAttachmentType().getIsToSentOnUpload())) {
                            attachments.add(attachment);
                        }
        if (service.getAssoAffaireOrder() != null && service.getAssoAffaireOrder().getQuotation() != null)
            if (!service.getAssoAffaireOrder().getQuotation().getAttachments().isEmpty())
                for (Attachment attachment : service.getAssoAffaireOrder().getQuotation().getAttachments())
                    if (attachment.getAttachmentType().getId()
                            .equals(constantService.getAttachmentTypeQuotation().getId()))
                        attachments.add(attachment);

        return attachments;
    }

    @Override
    public List<Service> populateTransientField(List<Service> services) throws OsirisException {
        if (services != null)
            for (Service service : services) {
                // TODO bouger dans ServiceServiceImpl ce bout là
                service.setHasMissingInformations(false);
                if (isServiceHasMissingInformations(service)) {
                    service.setHasMissingInformations(true);

                    service.getMissingAttachmentQueries().sort(new Comparator<MissingAttachmentQuery>() {

                        @Override
                        public int compare(MissingAttachmentQuery o1, MissingAttachmentQuery o2) {
                            if (o1 != null && o2 == null)
                                return 1;
                            if (o1 == null && o2 != null)
                                return -1;
                            if (o1 == null && o2 == null)
                                return 0;
                            if (o1 != null && o2 != null)
                                return o2.getCreatedDateTime().compareTo(o1.getCreatedDateTime());
                            return 0;
                        }
                    });
                    service.setLastMissingAttachmentQueryDateTime(
                            service.getMissingAttachmentQueries().get(0).getCreatedDateTime()); // TODO : quand
                                                                                                // les alertes
                                                                                                // de mails
                                                                                                // affaire + rib
                                                                                                // affaire
                                                                                                // seront
                                                                                                // intégrées,
                                                                                                // mettre cette
                                                                                                // date à la
                                                                                                // date de
                                                                                                // création de
                                                                                                // la commande
                }
                service.setServiceStatus(getServiceStatusLabel(service));
                setServicePrice(service, true, true);
                if (service.getServiceTotalPrice().compareTo(new BigDecimal(0)) <= 0f)
                    service.setServiceTotalPrice(null);
                removeDisabledAttachments(service);
                removeUnusedAssoServiceDocument(service);

                if (service.getProvisions() != null)
                    for (Provision provision : service.getProvisions()) {
                        if (provision.getAnnouncement() != null
                                && provision.getAnnouncement().getConfrere() != null)
                            service.setConfrereLabel(provision.getAnnouncement().getConfrere().getLabel());

                        if (provision.getSimpleProvision() != null
                                && provision.getSimpleProvision().getSimpleProvisionStatus() != null
                                && provision.getSimpleProvision().getSimpleProvisionStatus().getCode()
                                        .equals(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY)
                                && provision.getSimpleProvision().getWaitedCompetentAuthority() != null)
                            service.setWaitingAcLabel(
                                    provision.getSimpleProvision().getWaitedCompetentAuthority().getLabel());

                        if (provision.getFormalite() != null && provision.getFormalite().getFormaliteStatus() != null
                                && provision.getFormalite().getFormaliteStatus().getCode()
                                        .equals(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY)
                                && provision.getFormalite().getWaitedCompetentAuthority() != null)
                            service.setWaitingAcLabel(
                                    provision.getFormalite().getWaitedCompetentAuthority().getLabel());
                    }
            }
        return services;
    }

    private void removeDisabledAttachments(Service service) {
        if (service != null && service.getAssoServiceDocuments() != null)
            for (AssoServiceDocument asso : service.getAssoServiceDocuments()) {
                List<Attachment> attachmentsToRemove = new ArrayList<Attachment>();
                if (asso.getAttachments() != null) {
                    for (Attachment attachment : asso.getAttachments()) {
                        if (attachment.getIsDisabled() != null && attachment.getIsDisabled())
                            attachmentsToRemove.add(attachment);
                    }
                    if (attachmentsToRemove.size() > 0) {
                        asso.getAttachments().removeAll(attachmentsToRemove);
                    }
                }
            }
    }

    private void removeUnusedAssoServiceDocument(Service service) throws OsirisException {
        // When published in the same service, do not ask for publication flag, JSS will
        // provide it
        if (service != null && service.getProvisions() != null) {
            boolean asAnnouncement = false;
            for (Provision provision : service.getProvisions()) {
                if (provision.getAnnouncement() != null)
                    asAnnouncement = true;
                break;
            }

            if (asAnnouncement && service.getAssoServiceDocuments() != null) {
                for (AssoServiceDocument assoServiceDocument : service.getAssoServiceDocuments()) {
                    if (assoServiceDocument.getTypeDocument().getAttachmentType() != null
                            && assoServiceDocument.getTypeDocument().getAttachmentType().getId()
                                    .equals(constantService.getAttachmentTypePublicationFlag().getId())) {
                        assoServiceDocument.setIsMandatory(false);
                    }
                }
            }

            // Remove "more informations" field
            for (AssoServiceFieldType assoServiceFieldType : service.getAssoServiceFieldTypes()) {
                if (assoServiceFieldType.getServiceFieldType().getId()
                        .equals(constantService.getFurtherInformationServiceFieldType().getId()))
                    assoServiceFieldType.setIsMandatory(false);
            }
        }
    }

    @Override
    public boolean isServiceHasMissingInformations(Service service) {
        if (service.getMissingAttachmentQueries() != null
                && service.getMissingAttachmentQueries().size() > 0) {
            if (service.getProvisions() != null)
                for (Provision provision : service.getProvisions()) {
                    if (provision.getSimpleProvision() != null
                            && provision.getSimpleProvision().getSimpleProvisionStatus().getCode()
                                    .equals(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT)
                            ||
                            provision.getFormalite() != null
                                    && provision.getFormalite().getFormaliteStatus().getCode()
                                            .equals(FormaliteStatus.FORMALITE_WAITING_DOCUMENT)) {
                        return true;
                    }
                }
        }
        return false;
    }

    private String getServiceStatusLabel(Service service) {
        Integer currentPriority = -1;
        String currentStatus = "";
        if (service.getProvisions() != null && service.getId() != null)
            for (Provision provision : service.getProvisions()) {
                if (provision.getAnnouncement() != null && provision.getAnnouncement().getAnnouncementStatus() != null
                        && provision.getAnnouncement().getAnnouncementStatus().getServicePriority() != null
                        && provision.getAnnouncement().getAnnouncementStatus()
                                .getServicePriority() > currentPriority) {
                    currentPriority = provision.getAnnouncement().getAnnouncementStatus().getServicePriority();
                    currentStatus = provision.getAnnouncement().getAnnouncementStatus().getLabel();
                } else if (provision.getSimpleProvision() != null && provision.getSimpleProvision()
                        .getSimpleProvisionStatus() != null && provision.getSimpleProvision()
                                .getSimpleProvisionStatus().getServicePriority() > currentPriority) {
                    currentPriority = provision.getSimpleProvision().getSimpleProvisionStatus()
                            .getServicePriority();
                    currentStatus = provision.getSimpleProvision().getSimpleProvisionStatus().getLabel();
                } else if (provision.getFormalite() != null && provision.getFormalite().getFormaliteStatus() != null
                        && provision.getFormalite().getFormaliteStatus()
                                .getServicePriority() > currentPriority) {
                    currentPriority = provision.getFormalite().getFormaliteStatus().getServicePriority();
                    currentStatus = provision.getFormalite().getFormaliteStatus().getLabel();
                    if (provision.getFormalite().getFormaliteStatus().getCode()
                            .equals(FormaliteStatus.FORMALITE_AUTHORITY_REJECTED))
                        currentStatus = formaliteStatusService
                                .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY)
                                .getLabel();
                } else if (provision.getDomiciliation() != null
                        && provision.getDomiciliation().getDomiciliationStatus() != null
                        && provision.getDomiciliation().getDomiciliationStatus()
                                .getServicePriority() > currentPriority) {
                    currentPriority = provision.getDomiciliation().getDomiciliationStatus().getServicePriority();
                    currentStatus = provision.getDomiciliation().getDomiciliationStatus().getLabel();
                }
            }
        return currentStatus;
    }

    private void setServicePrice(Service service, boolean withDiscount, boolean withVat) {
        BigDecimal totalPrice = new BigDecimal(0);
        BigDecimal preTaxPrice = new BigDecimal(0);
        BigDecimal vatPrice = new BigDecimal(0);
        BigDecimal discountAmount = new BigDecimal(0);
        if (service.getProvisions() != null)
            for (Provision provision : service.getProvisions()) {
                if (provision.getInvoiceItems() != null) {
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                        if (invoiceItem.getPreTaxPriceReinvoiced() != null) {
                            totalPrice = totalPrice.add(invoiceItem.getPreTaxPriceReinvoiced());
                            preTaxPrice = preTaxPrice.add(invoiceItem.getPreTaxPriceReinvoiced());
                        } else if (invoiceItem.getPreTaxPrice() != null) {
                            totalPrice = totalPrice.add(invoiceItem.getPreTaxPrice());
                            preTaxPrice = preTaxPrice.add(invoiceItem.getPreTaxPrice());
                        }

                        if (withDiscount && invoiceItem.getDiscountAmount() != null) {
                            totalPrice = totalPrice.subtract(invoiceItem.getDiscountAmount());
                            discountAmount = discountAmount.add(invoiceItem.getDiscountAmount());
                        }
                        if (withVat && invoiceItem.getVatPrice() != null) {
                            totalPrice = totalPrice.add(invoiceItem.getVatPrice());
                            vatPrice = vatPrice.add(invoiceItem.getVatPrice());
                        }
                    }
                }
            }

        service.setServiceTotalPrice(totalPrice);
        service.setServicePreTaxPrice(preTaxPrice);
        service.setServiceVatPrice(vatPrice);
        service.setServiceDiscountAmount(discountAmount);
    }
}
