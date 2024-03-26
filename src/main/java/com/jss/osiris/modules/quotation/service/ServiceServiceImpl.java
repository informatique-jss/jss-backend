package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.AssoServiceProvisionType;
import com.jss.osiris.modules.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.ServiceType;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServiceTypeService serviceTypeService;

    @Autowired
    ConstantService constantService;

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
        if (service.getCustomLabel() != null && service.getCustomLabel().trim().length() == 0)
            service.setCustomLabel(null);
        return serviceRepository.save(service);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Service getServiceForServiceTypeAndAffaire(ServiceType serviceType, Affaire affaire) {
        Service service = new Service();
        service.setServiceType(serviceType);

        // Documents
        ArrayList<AssoServiceDocument> assoServiceDocuments = new ArrayList<AssoServiceDocument>();
        if (serviceType.getAssoServiceTypeDocuments() != null)
            for (AssoServiceTypeDocument assoServiceTypeDocument : serviceType.getAssoServiceTypeDocuments()) {
                assoServiceDocuments
                        .add(getAssoServiceDocumentFromAssoServiceTypeDocument(assoServiceTypeDocument, service));
            }
        service.setAssoServiceDocuments(assoServiceDocuments);

        // Provision
        service.setProvisions(getProvisionsFromServiceType(serviceType, affaire, service));
        return service;
    }

    private AssoServiceDocument getAssoServiceDocumentFromAssoServiceTypeDocument(
            AssoServiceTypeDocument assoServiceTypeDocument, Service service) {
        AssoServiceDocument assoServiceDocument = new AssoServiceDocument();
        assoServiceDocument.setIsMandatory(assoServiceTypeDocument.getIsMandatory());
        assoServiceDocument.setService(service);
        assoServiceDocument.setTypeDocument(assoServiceTypeDocument.getTypeDocument());
        return assoServiceDocument;
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

                if (affaire.getLegalForm() != null && assoServiceProvisionType.getFormeJuridiques() != null) {
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
                    provisions.add(provision);
                }
            }
        return provisions;
    }

    @Transactional(rollbackFor = Exception.class)
    public Service modifyServiceType(ServiceType serviceType, Service service) {
        serviceType = serviceTypeService.getServiceType(serviceType.getId());
        service = getService(service.getId());

        service.setServiceType(serviceType);

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
        if (serviceType.getAssoServiceTypeDocuments() != null && serviceType.getAssoServiceTypeDocuments().size() > 0) {
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

        service.getProvisions()
                .addAll(getProvisionsFromServiceType(serviceType, service.getAssoAffaireOrder().getAffaire(), service));

        return addOrUpdateService(service);
    }

    @Override
    public String getServiceLabel(Service service) throws OsirisException {
        if (service != null) {
            if (service.getCustomLabel() == null || service.getCustomLabel().length() == 0)
                return service.getServiceType().getLabel();
            if (service.getServiceType().getId().equals(constantService.getServiceTypeOther().getId()))
                return service.getCustomLabel();
            return service.getServiceType().getLabel();
        }
        return "";
    }

}
