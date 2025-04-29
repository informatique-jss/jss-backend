package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeFieldType;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
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
    ProvisionService provisionService;

    @Override
    public Service getService(Integer id) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent())
            return service.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Service addOrUpdateService(
            Service service) {
        computeServiceLabel(service);
        if (service.getCustomLabel() != null && service.getCustomLabel().trim().length() == 0)
            service.setCustomLabel(null);
        return serviceRepository.save(service);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteService(Service service) {
        if (service.getProvisions() != null && service.getProvisions().size() > 0) {
            for (Provision provision : service.getProvisions()) {
                if (provision.getAttachments() != null && provision.getAttachments().size() > 0) {
                    for (Attachment attachment : provision.getAttachments()) {
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
    public List<Service> generateServiceInstanceFromMultiServiceTypes(List<ServiceType> serviceTypes, Affaire affaire,
            String customLabel) throws OsirisException {

        List<Service> services = new ArrayList<Service>();
        List<ServiceType> serviceTypeNonMergeables = new ArrayList<ServiceType>();

        List<ServiceType> mergeableTypes = serviceTypes.stream().filter(s -> s.getIsMergeable()).toList();
        if (!mergeableTypes.isEmpty()) {
            services.add(getServiceForMultiServiceTypesAndAffaire(mergeableTypes, affaire, customLabel));
        }

        serviceTypeNonMergeables = serviceTypes.stream().filter(s -> !s.getIsMergeable())
                .toList();
        for (ServiceType serviceType : serviceTypeNonMergeables)
            services.add(
                    getServiceForMultiServiceTypesAndAffaire(Arrays.asList(serviceType), affaire, customLabel));

        return services;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Service getServiceForMultiServiceTypesAndAffaire(List<ServiceType> serviceTypes, Affaire affaire,
            String customLabel)
            throws OsirisException {

        ArrayList<Integer> newAssoServiceServiceTypeIds = new ArrayList<Integer>();
        ArrayList<AssoServiceDocument> assoServiceDocuments = new ArrayList<AssoServiceDocument>();
        ArrayList<String> typeDocumentCodes = new ArrayList<String>();
        ArrayList<AssoServiceFieldType> assoServiceFieldTypes = new ArrayList<AssoServiceFieldType>();
        ArrayList<Integer> serviceFieldTypeIds = new ArrayList<Integer>();

        ArrayList<AssoServiceProvisionType> assoServiceProvisionTypes = new ArrayList<AssoServiceProvisionType>();
        ArrayList<Integer> provisionTypesIds = new ArrayList<Integer>();

        Service service = new Service();

        for (ServiceType serviceType : serviceTypes) {
            // fill all provisions type in list
            if (!serviceType.getAssoServiceProvisionTypes().isEmpty())
                for (AssoServiceProvisionType assoServiceProvision : serviceType.getAssoServiceProvisionTypes())
                    if (!provisionTypesIds.contains(assoServiceProvision.getId())) {
                        assoServiceProvisionTypes.add(assoServiceProvision);
                        provisionTypesIds.add(assoServiceProvision.getId());
                    }

            // Asso service - serviceType
            if (!newAssoServiceServiceTypeIds.contains(serviceType.getId())) {
                newAssoServiceServiceTypeIds.add(serviceType.getId());
                if (service.getServiceTypes() != null && service.getServiceTypes().size() > 0)
                    service.getServiceTypes().add(serviceType);
                else
                    service.setServiceTypes(new ArrayList<>(Arrays.asList(serviceType)));

            }

            if (serviceType.getAssoServiceTypeDocuments() != null)
                for (AssoServiceTypeDocument assoServiceTypeDocument : serviceType.getAssoServiceTypeDocuments()) {
                    AssoServiceDocument newAssoServiceDocument = getAssoServiceDocumentFromAssoServiceTypeDocument(
                            assoServiceTypeDocument, service);
                    if (newAssoServiceDocument.getTypeDocument() != null
                            && !typeDocumentCodes.contains(assoServiceTypeDocument.getTypeDocument().getCode())) {
                        typeDocumentCodes.add(assoServiceTypeDocument.getTypeDocument().getCode());
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
                    }
                }
        }
        // Merge provisions Type before create instances
        if (service.getProvisions() != null && service.getProvisions().size() > 0)
            service.getProvisions().addAll(mergeProvisionTypes(assoServiceProvisionTypes, service));
        else
            service.setProvisions(mergeProvisionTypes(assoServiceProvisionTypes, service));

        // Always add further information field
        AssoServiceFieldType newAssoServiceFieldType = new AssoServiceFieldType();
        newAssoServiceFieldType.setIsMandatory(false);
        newAssoServiceFieldType.setService(service);
        newAssoServiceFieldType.setServiceFieldType(constantService.getFurtherInformationServiceFieldType());
        assoServiceFieldTypes.add(newAssoServiceFieldType);

        service.setAssoServiceFieldTypes(assoServiceFieldTypes);
        service.setAssoServiceDocuments(assoServiceDocuments);
        service = computeServiceLabel(service);
        return service;
    }

    private List<Provision> mergeProvisionTypes(ArrayList<AssoServiceProvisionType> assoServiceProvisionTypes,
            Service service) throws OsirisException {
        List<Provision> newProvisions = new ArrayList<Provision>();
        ArrayList<AssoServiceProvisionType> provisionTypeFormalitesMergeable = new ArrayList<AssoServiceProvisionType>();
        ArrayList<AssoServiceProvisionType> provisionTypeFormalitesNotMergeable = new ArrayList<AssoServiceProvisionType>();
        ArrayList<AssoServiceProvisionType> provisionTypeSimpleProvisionMergeable = new ArrayList<AssoServiceProvisionType>();
        ArrayList<AssoServiceProvisionType> provisionTypeSimpleProvisionNotMergeable = new ArrayList<AssoServiceProvisionType>();
        ArrayList<AssoServiceProvisionType> provisionTypeAnnouncementCharacter = new ArrayList<AssoServiceProvisionType>();

        if (assoServiceProvisionTypes != null && assoServiceProvisionTypes.size() > 0) {
            provisionTypeFormalitesMergeable.addAll(assoServiceProvisionTypes.stream()
                    .filter(s -> s.getProvisionType().getIsMergeable()
                            && s.getProvisionType().getProvisionScreenType().getCode()
                                    .equals(ProvisionScreenType.FORMALITE))
                    .toList());

            provisionTypeFormalitesNotMergeable.addAll(assoServiceProvisionTypes.stream()
                    .filter(s -> !s.getProvisionType().getIsMergeable()
                            && s.getProvisionType().getProvisionScreenType().getCode()
                                    .equals(ProvisionScreenType.FORMALITE))
                    .toList());

            provisionTypeAnnouncementCharacter.addAll(assoServiceProvisionTypes.stream()
                    .filter(s -> s.getProvisionType().getProvisionScreenType().getCode()
                            .equals(ProvisionScreenType.ANNOUNCEMENT))
                    .toList());

            provisionTypeSimpleProvisionMergeable.addAll(assoServiceProvisionTypes.stream()
                    .filter(s -> s.getProvisionType().getIsMergeable()
                            && s.getProvisionType().getProvisionScreenType().getCode()
                                    .equals(ProvisionScreenType.STANDARD))
                    .toList());
            provisionTypeSimpleProvisionNotMergeable.addAll(assoServiceProvisionTypes.stream()
                    .filter(s -> !s.getProvisionType().getIsMergeable()
                            && s.getProvisionType().getProvisionScreenType().getCode()
                                    .equals(ProvisionScreenType.STANDARD))
                    .toList());
        }

        if (provisionTypeFormalitesMergeable.size() > 0)
            newProvisions
                    .add(generateProvisionFromProvisionType(provisionTypeFormalitesMergeable.get(0).getProvisionType(),
                            service));
        if (provisionTypeFormalitesNotMergeable.size() > 0)
            for (AssoServiceProvisionType asso : provisionTypeFormalitesNotMergeable)
                newProvisions.add(generateProvisionFromProvisionType(asso.getProvisionType(), service));

        if (provisionTypeSimpleProvisionMergeable.size() > 0)
            newProvisions
                    .add(generateProvisionFromProvisionType(
                            provisionTypeSimpleProvisionMergeable.get(0).getProvisionType(),
                            service));

        if (provisionTypeSimpleProvisionNotMergeable.size() > 0)
            for (AssoServiceProvisionType asso : provisionTypeSimpleProvisionNotMergeable)
                newProvisions.add(generateProvisionFromProvisionType(asso.getProvisionType(), service));

        if (!provisionTypeAnnouncementCharacter.isEmpty() && provisionTypeAnnouncementCharacter.size() > 1)
            newProvisions.add(generateProvisionFromProvisionType(
                    this.constantService.getProvisionTypeCharacterAnnouncement(), service));
        else if (!provisionTypeAnnouncementCharacter.isEmpty() && provisionTypeAnnouncementCharacter.size() == 1)
            newProvisions.add(generateProvisionFromProvisionType(
                    provisionTypeAnnouncementCharacter.get(0).getProvisionType(), service));

        return newProvisions;
    }

    private Provision generateProvisionFromProvisionType(ProvisionType provisionType,
            Service service) {
        Provision provision = new Provision();
        if (provisionType != null) {
            provision.setProvisionFamilyType(provisionType.getProvisionFamilyType());
            provision.setProvisionType(provisionType);
            provision.setService(service);
            provision.setIsLogo(false);
            provision.setIsRedactedByJss(false);
            provision.setIsBaloPackage(false);
            provision.setIsPublicationPaper(false);
            provision.setIsPublicationReceipt(false);
            provision.setIsPublicationFlag(false);
            provision.setIsBodaccFollowup(false);
            provision.setIsBodaccFollowupAndRedaction(false);
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

    private List<Provision> getProvisionsFromServiceType(ServiceType serviceType, Affaire affaire, Service service) {
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
                    Provision provision = new Provision();
                    provision.setProvisionFamilyType(
                            assoServiceProvisionType.getProvisionType().getProvisionFamilyType());
                    provision.setProvisionType(assoServiceProvisionType.getProvisionType());
                    provision.setService(service);

                    provision.setIsLogo(false);
                    provision.setIsRedactedByJss(false);
                    provision.setIsBaloPackage(false);
                    provision.setIsPublicationPaper(false);
                    provision.setIsPublicationReceipt(false);
                    provision.setIsPublicationFlag(false);
                    provision.setIsBodaccFollowup(false);
                    provision.setIsBodaccFollowupAndRedaction(false);
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
                    provisions.add(provision);
                }
            }
        return provisions;
    }

    @Transactional(rollbackFor = Exception.class)
    public Service modifyServiceType(List<ServiceType> serviceTypes, Service service) {
        service = getService(service.getId());

        ArrayList<AssoServiceFieldType> assoToDelete = new ArrayList<AssoServiceFieldType>();
        ArrayList<Integer> serviceTypeIds = new ArrayList<Integer>();

        for (ServiceType serviceType : serviceTypes) {
            serviceType = serviceTypeService.getServiceType(serviceType.getId());
            if (!serviceTypeIds.contains(serviceType.getId())) {
                serviceTypeIds.add(serviceType.getId());
                service.getServiceTypes().add(serviceType);
            }

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
            newProvisions.forEach(provision -> provisionService.addOrUpdateProvision(provision));
            service.getProvisions().addAll(newProvisions);
        }

        return addOrUpdateService(service);
    }

    private Service computeServiceLabel(Service service) {
        if (service != null) {
            if (service.getCustomLabel() == null || service.getCustomLabel().length() == 0)
                service.setServiceLabelToDisplay(String.join(" / ", service.getServiceTypes().stream()
                        .map(s -> s.getCustomLabel()).collect(Collectors.toList())));
            else
                service.setServiceLabelToDisplay(service.getCustomLabel());
        }
        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> getAttachmentsForProvisionOfService(Service service) {
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
        return attachments;
    }
}
