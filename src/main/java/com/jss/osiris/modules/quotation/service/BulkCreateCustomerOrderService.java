package com.jss.osiris.modules.quotation.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.service.ResponsableService;

@Service
public class BulkCreateCustomerOrderService {

    @Autowired
    ConstantService constantService;

    @Autowired
    CityService cityService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ProvisionFamilyTypeService provisionFamilyTypeService;

    @Autowired
    ProvisionTypeService provisionTypeService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    AffaireService affaireService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    DocumentService documentService;

    // @Scheduled(initialDelay = 100, fixedDelay = 10000000)
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings({ "all" })
    public void bulkCreateCustomerOrder()
            throws OsirisException, IOException, OsirisValidationException, OsirisClientMessageException {
        File file = new File(
                "C:\\TEMP\\affaires.csv");
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(file));
        String availalbe;
        int i = 0;
        CustomerOrder customerOrder = null;
        Affaire affaire = null;
        Responsable responsable = responsableService.getResponsable(91614);
        ArrayList<CustomerOrder> customerOrders = new ArrayList<CustomerOrder>();

        if (br != null) {
            br.readLine();
            while ((availalbe = br.readLine()) != null) {
                String[] fields = availalbe.split(";");
                if (fields.length < 8)
                    throw new OsirisException(null, "length");
                affaire = new Affaire();
                affaire.setIsIndividual(false);
                affaire.setIsUnregistered(true);
                affaire.setDenomination("SNCF OPTIM'SERVICES");
                affaire.setAddress(fields[1] + " " + fields[2] + " " + fields[3] + " " + fields[0]);
                affaire.setCountry(constantService.getCountryFrance());
                affaire.setPostalCode(StringUtils.leftPad(fields[5], 5, "0"));

                if (fields.length > 9 && fields[9] != null && !fields[9].equals("")) {
                    affaire.setCity(cityService.getCity(Integer.parseInt(fields[9])));
                } else {
                    List<City> cities = cityService.getCitiesByPostalCode(affaire.getPostalCode());
                    if (cities == null || cities.size() == 0 || cities.size() > 1) {
                        System.out.println("city id with name " + affaire.getPostalCode() + " " + fields[6] + " ?");
                    } else {
                        affaire.setCity(cities.get(0));
                    }
                }
                if (affaire.getCity() == null) {
                    throw new OsirisException(null, "city");
                }
                affaireService.addOrUpdateAffaire(affaire);

                if (i % 20 == 0) {
                    customerOrder = new CustomerOrder();
                    customerOrders.add(customerOrder);
                    customerOrder.setIsQuotation(false);
                    customerOrder.setOverrideSpecialOffer(false);
                    customerOrder.setResponsable(responsable);
                    customerOrder.setAssoAffaireOrders(new ArrayList<AssoAffaireOrder>());
                    customerOrder.setCustomerOrderStatus(
                            customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN));

                    customerOrder.setDocuments(new ArrayList<Document>());
                    customerOrder.getDocuments()
                            .add(documentService.cloneDocument(documentService.getDocumentByDocumentType(
                                    responsable.getDocuments(), constantService.getDocumentTypeDigital())));
                    customerOrder.getDocuments()
                            .add(documentService.cloneDocument(documentService.getDocumentByDocumentType(
                                    responsable.getDocuments(), constantService.getDocumentTypePaper())));
                    Document billingDocument = documentService.cloneDocument(documentService.getDocumentByDocumentType(
                            responsable.getDocuments(), constantService.getDocumentTypeBilling()));
                    billingDocument.setBillingLabelType(constantService.getBillingLabelTypeOther());
                    billingDocument.setBillingLabel("GIE SNCF Optim' services");
                    billingDocument.setBillingAddress("1 place aux étoiles");
                    billingDocument.setBillingPostalCode("93120");
                    billingDocument.setBillingLabelCity(cityService.getCity(111806));
                    billingDocument.setBillingLabelCountry(constantService.getCountryFrance());
                    customerOrder.getDocuments().add(billingDocument);
                }

                if (customerOrder != null) {
                    AssoAffaireOrder asso = new AssoAffaireOrder();
                    asso.setAffaire(affaire);
                    asso.setProvisions(new ArrayList<Provision>());
                    customerOrder.getAssoAffaireOrders().add(asso);

                    Provision provision = new Provision();
                    provision.setProvisionFamilyType(provisionFamilyTypeService.getProvisionFamilyType(112732));
                    provision.setProvisionType(provisionTypeService.getProvisionType(114667));
                    asso.getProvisions().add(provision);

                    Formalite formalite = new Formalite();
                    provision.setFormalite(formalite);
                    provision.setIsApplicationFees(false);
                    provision.setIsBaloNormalization(false);
                    provision.setIsBaloPackage(false);
                    provision.setIsBaloPublicationFlag(false);
                    provision.setIsBankCheque(false);
                    provision.setIsBilan(false);
                    provision.setIsBodaccFollowup(false);
                    provision.setIsBodaccFollowupAndRedaction(false);
                    provision.setIsBusinnessNantissementRedaction(false);
                    provision.setIsChronopostFees(false);
                    provision.setIsComplexeFile(false);
                    provision.setIsCorrespondenceFees(false);
                    provision.setIsDisbursement(false);
                    provision.setIsDocumentScanning(false);
                    provision.setIsEmergency(false);
                    provision.setIsFeasibilityStudy(false);
                    provision.setIsFormalityAdditionalDeclaration(false);
                    provision.setIsLogo(false);
                    provision.setIsNantissementDeposit(false);
                    provision.setIsPublicationFlag(false);
                    provision.setIsPublicationPaper(false);
                    provision.setIsPublicationReceipt(false);
                    provision.setIsRedactedByJss(false);
                    provision.setIsRegisterInitials(false);
                    provision.setIsRegisterPurchase(false);
                    provision.setIsRegisterShippingCosts(false);
                    provision.setIsSellerPrivilegeRedaction(false);
                    provision.setIsSocialShareNantissementRedaction(false);
                    provision.setIsTreatmentMultipleModiciation(false);
                    provision.setIsVacationMultipleModification(false);
                    provision.setIsVacationUpdateBeneficialOwners(false);
                }
                i++;
            }
        }
        br.close();

        if (customerOrders != null) {
            for (CustomerOrder co : customerOrders) {
                quotationValidationHelper.validateQuotationAndCustomerOrder(co, null);
                customerOrderService.addOrUpdateCustomerOrder(co, true, false);
                System.out.println(customerOrders.indexOf(co) + " done");
            }
        }
        System.out.println("end");
    }
}
