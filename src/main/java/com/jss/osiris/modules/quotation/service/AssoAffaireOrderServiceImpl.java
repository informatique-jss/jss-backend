package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.AffaireSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.quotation.model.BodaccStatus;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.AssoAffaireOrderRepository;

@Service
public class AssoAffaireOrderServiceImpl implements AssoAffaireOrderService {

    @Autowired
    AssoAffaireOrderRepository assoAffaireOrderRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    BodaccStatusService bodaccStatusService;

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    DomiciliationStatusService domiciliationStatusService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Override
    public List<AssoAffaireOrder> getAssoAffaireOrders() {
        return IterableUtils.toList(assoAffaireOrderRepository.findAll());
    }

    @Override
    public AssoAffaireOrder getAssoAffaireOrder(Integer id) {
        Optional<AssoAffaireOrder> assoAffaireOrder = assoAffaireOrderRepository.findById(id);
        if (assoAffaireOrder.isPresent())
            return assoAffaireOrder.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssoAffaireOrder addOrUpdateAssoAffaireOrder(
            AssoAffaireOrder assoAffaireOrder)
            throws OsirisException {
        for (Provision provision : assoAffaireOrder.getProvisions()) {
            provision.setAssoAffaireOrder(assoAffaireOrder);
        }
        assoAffaireOrder = completeAssoAffaireOrder(assoAffaireOrder, assoAffaireOrder.getCustomerOrder());
        assoAffaireOrder.setCustomerOrder(assoAffaireOrder.getCustomerOrder());
        AssoAffaireOrder affaireSaved = assoAffaireOrderRepository.save(assoAffaireOrder);
        indexEntityService.indexEntity(affaireSaved, affaireSaved.getId());
        customerOrderService.checkAllProvisionEnded(assoAffaireOrder.getCustomerOrder());
        return affaireSaved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForAsso(AssoAffaireOrder asso, Employee employee) {
        asso.setAssignedTo(employee);
        assoAffaireOrderRepository.save(asso);
    }

    @Override
    public void reindexAffaires() {
        List<AssoAffaireOrder> affaires = getAssoAffaireOrders();
        if (affaires != null)
            for (AssoAffaireOrder affaire : affaires)
                indexEntityService.indexEntity(affaire, affaire.getId());
    }

    @Override
    public AssoAffaireOrder completeAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder, IQuotation customerOrder) {
        // Complete domiciliation end date
        int nbrAssignation = 0;
        Employee currentEmployee = null;

        for (Provision provision : assoAffaireOrder.getProvisions()) {
            provision.setAssoAffaireOrder(assoAffaireOrder);
            if (provision.getDomiciliation() != null) {
                Domiciliation domiciliation = provision.getDomiciliation();
                if (customerOrder.getId() == null || domiciliation.getDomiciliationStatus() == null)
                    domiciliation.setDomiciliationStatus(domiciliationStatusService
                            .getDomiciliationStatusByCode(DomiciliationStatus.DOMICILIATION_NEW));

                if (domiciliation.getEndDate() == null && domiciliation.getStartDate() != null) {
                    domiciliation.setEndDate(domiciliation.getStartDate().plusYears(1));

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getMails() != null
                            && domiciliation.getMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getActivityMails() != null
                            && domiciliation.getActivityMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getActivityMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null
                            && domiciliation.getLegalGardianMails() != null
                            && domiciliation.getLegalGardianMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getLegalGardianMails());

                    if (domiciliation != null
                            && domiciliation.getLegalGardianPhones() != null
                            && domiciliation.getLegalGardianPhones().size() > 0)
                        phoneService.populateMPhoneIds(domiciliation.getLegalGardianPhones());

                }
            }

            if (provision.getFormalite() != null) {
                if (customerOrder.getId() == null || provision.getFormalite().getFormaliteStatus() == null)
                    provision.getFormalite().setFormaliteStatus(
                            formaliteStatusService.getFormaliteStatusByCode(FormaliteStatus.FORMALITE_NEW));

                if (provision.getFormalite().getReferenceMandataire() == null)
                    // Play with fire ...
                    provision.getFormalite().setReferenceMandataire(provision.getFormalite().getId() + "");
                if (provision.getFormalite().getNomDossier() == null)
                    provision.getFormalite()
                            .setNomDossier(assoAffaireOrder.getAffaire().getDenomination() != null
                                    ? assoAffaireOrder.getAffaire().getDenomination()
                                    : (assoAffaireOrder.getAffaire().getFirstname() + " "
                                            + assoAffaireOrder.getAffaire().getLastname()));
                if (provision.getFormalite().getSignedPlace() == null)
                    provision.getFormalite().setSignedPlace("8 Rue Saint-Augustin, 75002 Paris");
            }

            if (provision.getBodacc() != null) {
                if (customerOrder.getId() == null || provision.getBodacc().getBodaccStatus() == null)
                    provision.getBodacc()
                            .setBodaccStatus(bodaccStatusService.getBodaccStatusByCode(BodaccStatus.BODACC_NEW));
            }

            if (provision.getAnnouncement() != null) {
                if (customerOrder.getId() == null || provision.getAnnouncement().getAnnouncementStatus() == null)
                    provision.getAnnouncement().setAnnouncementStatus(announcementStatusService
                            .getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_NEW));
            }

            // Set proper assignation regarding provision item configuration
            if (provision.getAssignedTo() == null && customerOrder instanceof CustomerOrder) {
                Employee employee = provision.getProvisionType().getDefaultEmployee();

                if (provision.getProvisionType().getAssignationType() != null) {
                    if (provision.getProvisionType().getAssignationType().getCode()
                            .equals(AssignationType.FORMALISTE)) {
                        if (customerOrder.getConfrere() != null
                                && customerOrder.getConfrere().getFormalisteEmployee() != null)
                            employee = customerOrder.getConfrere().getFormalisteEmployee();
                        if (customerOrder.getResponsable() != null) {
                            if (customerOrder.getResponsable().getFormalisteEmployee() != null)
                                employee = customerOrder.getResponsable().getFormalisteEmployee();
                            else if (customerOrder.getResponsable().getTiers().getFormalisteEmployee() != null)
                                employee = customerOrder.getResponsable().getTiers().getFormalisteEmployee();
                        } else if (customerOrder.getTiers() != null
                                && customerOrder.getTiers().getFormalisteEmployee() != null)
                            employee = customerOrder.getTiers().getFormalisteEmployee();
                    }
                    if (provision.getProvisionType().getAssignationType().getCode()
                            .equals(AssignationType.PUBLICISTE)) {
                        if (customerOrder.getConfrere() != null
                                && customerOrder.getConfrere().getInsertionEmployee() != null)
                            employee = customerOrder.getConfrere().getInsertionEmployee();
                        if (customerOrder.getResponsable() != null) {
                            if (customerOrder.getResponsable().getInsertionEmployee() != null)
                                employee = customerOrder.getResponsable().getInsertionEmployee();
                            else if (customerOrder.getResponsable().getTiers().getInsertionEmployee() != null)
                                employee = customerOrder.getResponsable().getTiers().getInsertionEmployee();
                        } else if (customerOrder.getTiers() != null
                                && customerOrder.getTiers().getInsertionEmployee() != null)
                            employee = customerOrder.getTiers().getInsertionEmployee();
                    }
                }
                provision.setAssignedTo(employee);
                if (currentEmployee == null || !currentEmployee.getId().equals(employee.getId())) {
                    currentEmployee = employee;
                    nbrAssignation++;
                }
            }
        }
        if (nbrAssignation == 1)
            assoAffaireOrder.setAssignedTo(currentEmployee);

        return assoAffaireOrder;
    }

    @Override
    public ArrayList<AssoAffaireOrderSearchResult> searchForAsso(AffaireSearch affaireSearch) {
        ArrayList<Integer> statusId = null;
        ArrayList<Integer> assignedId = null;
        ArrayList<Integer> responsibleId = null;

        if (affaireSearch.getStatus() != null) {
            statusId = new ArrayList<Integer>();
            for (IWorkflowElement status : affaireSearch.getStatus())
                statusId.add(status.getId());
        }

        if (affaireSearch.getAssignedTo() != null) {
            assignedId = new ArrayList<Integer>();
            assignedId.add(affaireSearch.getAssignedTo().getId());
        }

        if (affaireSearch.getResponsible() != null) {
            responsibleId = new ArrayList<Integer>();
            responsibleId.add(affaireSearch.getResponsible().getId());
        }

        if (affaireSearch.getLabel() == null)
            affaireSearch.setLabel("");

        return assoAffaireOrderRepository.findAsso(new ArrayList<Integer>(),
                new ArrayList<Integer>(), affaireSearch.getLabel(),
                statusId);
    }
}
