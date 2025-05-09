package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementStatusRepository;
import com.jss.osiris.modules.osiris.quotation.repository.ProvisionRepository;

@Service
public class ProvisionServiceImpl implements ProvisionService {

    @Autowired
    ProvisionRepository provisionRepository;

    @Autowired
    AnnouncementStatusRepository announcementStatusRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    AssoAffaireOrderService assoAffaireOrderService;

    @Override
    public Provision getProvision(Integer id) {
        Optional<Provision> provision = provisionRepository.findById(id);
        if (provision.isPresent())
            return provision.get();
        return null;
    }

    @Override
    public Provision addOrUpdateProvision(Provision provision) {
        return provisionRepository.save(provision);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForProvision(Provision provision, Employee employee) {
        provision.setAssignedTo(employee);
        provisionRepository.save(provision);
    }

    @Override
    @Transactional
    public Boolean deleteProvision(Provision provision) {
        if (provision.getAttachments() != null && provision.getAttachments().size() > 0) {
            for (Attachment attachment : provision.getAttachments()) {
                attachmentService.cleanAttachmentForDelete(attachment);
            }
        }
        provisionRepository.delete(provision);
        return true;
    }

    @Override
    public List<ProvisionBoardResult> getDashboardEmployee(List<Employee> employees) {
        List<Integer> employeeIds = new ArrayList<Integer>();
        for (Employee employee : employees)
            employeeIds.add(employee.getId());

        return provisionRepository.getDashboardEmployee(employeeIds,
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT).getId(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN).getId());
    }

    @Override
    public File getRegistrationActPdf(Provision provision) throws OsirisException {
        return generatePdfDelegate.generateRegistrationActPdf(provision);
    }

    @Override
    public File getTrackingSheetPdf(Provision provision) throws OsirisException {
        return generatePdfDelegate.generateTrackingSheetPdf(provision);
    }

    @Override
    public List<Provision> searchProvisions(List<Employee> commercials,
            List<IWorkflowElement> status) {

        List<Integer> formalistesIds = (commercials != null && commercials.size() > 0)
                ? commercials.stream().map(Employee::getId).collect(Collectors.toList())
                : Arrays.asList(0);

        return provisionRepository.searchProvision(formalistesIds,
                status.stream().filter(s -> s instanceof AnnouncementStatus).toList(),
                status.stream().filter(s -> s instanceof SimpleProvisionStatus).toList(),
                status.stream().filter(s -> s instanceof FormaliteStatus).toList(),
                status.stream().filter(s -> s instanceof DomiciliationStatus).toList(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProvisionStatus(Provision provision, IWorkflowElement status)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        provision = getProvision(provision.getId());
        if (status instanceof AnnouncementStatus && provision.getAnnouncement() != null)
            provision.getAnnouncement().setAnnouncementStatus((AnnouncementStatus) status);
        if (status instanceof SimpleProvisionStatus && provision.getSimpleProvision() != null)
            provision.getSimpleProvision().setSimpleProvisionStatus((SimpleProvisionStatus) status);
        if (status instanceof FormaliteStatus && provision.getFormalite() != null)
            provision.getFormalite().setFormaliteStatus((FormaliteStatus) status);
        if (status instanceof DomiciliationStatus && provision.getDomiciliation() != null)
            provision.getDomiciliation().setDomiciliationStatus((DomiciliationStatus) status);
        assoAffaireOrderService.addOrUpdateAssoAffaireOrder(provision.getService().getAssoAffaireOrder());
    }
}
