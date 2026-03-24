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

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceItemService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.IWorkflowElement;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionSearch;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementStatusRepository;
import com.jss.osiris.modules.osiris.quotation.repository.ProvisionRepository;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.InfogreffeKbisService;

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

    @Autowired
    BatchService batchService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    CustomerMailService customerMailService;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    DocumentService documentService;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Autowired
    InfogreffeKbisService infogreffeKbisService;

    @Override
    public Provision getProvision(Integer id) {
        Optional<Provision> provision = provisionRepository.findById(id);
        if (provision.isPresent())
            return provision.get();
        return null;
    }

    @Override
    public Provision addOrUpdateProvision(Provision provision) throws OsirisException {
        provisionRepository.save(provision);

        if (provision.getService().getAssoAffaireOrder().getCustomerOrder() != null) {
            batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER,
                    provision.getService().getAssoAffaireOrder().getId());
            batchService.declareNewBatch(Batch.REINDEX_CUSTOMER_ORDER,
                    provision.getService().getAssoAffaireOrder().getCustomerOrder().getId());
        } else if (provision.getService().getAssoAffaireOrder().getQuotation() != null)
            batchService.declareNewBatch(Batch.REINDEX_QUOTATION,
                    provision.getService().getAssoAffaireOrder().getQuotation().getId());

        return provision;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForProvision(Provision provision, Employee employee) {
        provision.setAssignedTo(employee);
        provisionRepository.save(provision);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProvision(Provision provision) throws OsirisException {
        deleteProvisionAndDependencies(provision, false);
        return true;
    }

    @Override
    public void deleteProvisionAndDependencies(Provision provision, boolean permanentlyDeleteAttachments)
            throws OsirisException {
        List<CustomerMail> mails = provision.getCustomerMails();
        if (mails != null && mails.size() > 0)
            mails.forEach(mail -> mail.setProvision(null));

        List<Notification> notifications = provision.getNotifications();
        if (notifications != null && notifications.size() > 0)
            notificationRepository.deleteAll(notifications);

        List<CustomerOrderComment> customerOrders = customerOrderCommentService
                .getCustomerOrderCommentForProvision(provision);
        if (customerOrders != null && customerOrders.size() > 0)
            customerOrders.forEach(customerOrder -> customerOrder.setProvision(null));

        List<KbisRequest> kbisRequests = provision.getKbisRequests();
        if (kbisRequests != null && kbisRequests.size() > 0) {
            kbisRequests.forEach(kbisRequest -> {
                kbisRequest.setAttachment(null);
                kbisRequest.setProvision(null);
            });
        }
        if (provision.getAttachments() != null && provision.getAttachments().size() > 0) {
            for (Attachment attachment : provision.getAttachments()) {
                if (permanentlyDeleteAttachments)
                    attachmentService.definitivelyDeleteAttachment(attachment);
                else
                    attachmentService.cleanAttachmentForDelete(attachment);
            }
            provision.getAttachments().forEach(t -> t.setProvision(null));
        }
        provisionRepository.delete(provision);
        if (provision.getService().getAssoAffaireOrder().getCustomerOrder() != null
                && provision.getService().getAssoAffaireOrder().getCustomerOrder().getQuotations() != null
                && provision.getService().getAssoAffaireOrder().getCustomerOrder().getQuotations().size() > 0)
            notificationService
                    .notifyQuotationModified(provision.getService().getAssoAffaireOrder().getCustomerOrder());
    }

    @Override
    public List<ProvisionBoardResult> getDashboardEmployee(List<Employee> employees) {
        List<Integer> employeeIds = new ArrayList<Integer>();
        for (Employee employee : employees)
            employeeIds.add(employee.getId());

        return provisionRepository.getDashboardEmployee(employeeIds,
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT).getId(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT).getId());
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

        ArrayList<CustomerOrderStatus> excludedCustomerOrderStatus = new ArrayList<CustomerOrderStatus>();
        excludedCustomerOrderStatus
                .add(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT));
        excludedCustomerOrderStatus
                .add(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT));
        excludedCustomerOrderStatus
                .add(customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED));

        return provisionRepository.searchProvision(formalistesIds, Arrays.asList(0), Arrays.asList(0),
                status.stream().filter(s -> s instanceof AnnouncementStatus).toList(),
                status.stream().filter(s -> s instanceof SimpleProvisionStatus).toList(),
                status.stream().filter(s -> s instanceof FormaliteStatus).toList(),
                status.stream().filter(s -> s instanceof DomiciliationStatus).toList(), excludedCustomerOrderStatus);
    }

    @Override
    public List<Provision> searchForProvisions(ProvisionSearch provisionSearch) {

        Integer commercialId = (provisionSearch.getSalesEmployee() != null)
                ? provisionSearch.getSalesEmployee().getId()
                : 0;

        Integer formalisteId = (provisionSearch.getFormalisteEmployee() != null)
                ? provisionSearch.getFormalisteEmployee().getId()
                : 0;

        Integer responsableId = provisionSearch.getResponsable() != null
                ? provisionSearch.getResponsable()
                : 0;

        Integer provisionStatus = provisionSearch.getProvisionStatus() != null
                ? provisionSearch.getProvisionStatus().getId()
                : 0;

        return provisionRepository.searchForProvision(Arrays.asList(formalisteId), Arrays.asList(commercialId),
                Arrays.asList(responsableId), Arrays.asList(provisionStatus));
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
