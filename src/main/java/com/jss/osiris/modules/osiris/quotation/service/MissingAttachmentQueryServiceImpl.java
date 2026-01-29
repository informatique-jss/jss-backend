package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.repository.MissingAttachmentQueryRepository;

@org.springframework.stereotype.Service
public class MissingAttachmentQueryServiceImpl implements MissingAttachmentQueryService {

    @Autowired
    MissingAttachmentQueryRepository missingAttachmentQueryRepository;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    AssoServiceDocumentService assoServiceDocumentService;

    @Autowired
    BatchService batchService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    AssoServiceFieldTypeService assoServiceFieldTypeService;

    @Autowired
    ConstantService constantService;

    @Autowired
    SimpleProvisionStatusService simpleProvisionStatusService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    EmployeeService employeeService;

    @Override
    public MissingAttachmentQuery getMissingAttachmentQuery(Integer id) {
        Optional<MissingAttachmentQuery> missingAttachmentQuery = missingAttachmentQueryRepository.findById(id);
        if (missingAttachmentQuery.isPresent())
            return missingAttachmentQuery.get();
        return null;
    }

    @Override
    public List<MissingAttachmentQuery> getMissingAttachmentQueriesByIdService(Integer idService) {
        return missingAttachmentQueryRepository.findByService(serviceService.getService(idService));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MissingAttachmentQuery addOrUpdateMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery)
            throws OsirisException {
        missingAttachmentQueryRepository.save(missingAttachmentQuery);

        if (missingAttachmentQuery.getService().getAssoAffaireOrder().getCustomerOrder() != null) {
            batchService.declareNewBatch(Batch.REINDEX_ASSO_AFFAIRE_ORDER,
                    missingAttachmentQuery.getService().getAssoAffaireOrder().getId());
            batchService.declareNewBatch(Batch.REINDEX_CUSTOMER_ORDER,
                    missingAttachmentQuery.getService().getAssoAffaireOrder().getCustomerOrder().getId());
        }
        return missingAttachmentQuery;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MissingAttachmentQuery sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query,
            boolean isForcedReminder, Boolean isWaitingForAttachmentToUpload)
            throws OsirisException, OsirisClientMessageException {
        Service service = new Service();

        if (query.getAssoServiceDocument() != null && query.getAssoServiceDocument().size() > 0) {
            service = assoServiceDocumentService
                    .getAssoServiceDocument(query.getAssoServiceDocument().get(0).getId()).getService();
            for (AssoServiceDocument assoServiceDocument : query.getAssoServiceDocument()) {
                String newComment = assoServiceDocument.getFormalisteComment();
                assoServiceDocument = assoServiceDocumentService.getAssoServiceDocument(assoServiceDocument.getId());
                assoServiceDocument.setFormalisteComment(newComment);
                assoServiceDocument.setService(service);
                assoServiceDocument.setIsMandatory(true);
                assoServiceDocument = assoServiceDocumentService.addOrUpdateAssoServiceDocument(assoServiceDocument);
            }
        }

        if (query.getAssoServiceFieldType() != null && query.getAssoServiceFieldType().size() > 0) {
            service = assoServiceFieldTypeService
                    .getAssoServiceFieldType(query.getAssoServiceFieldType().get(0).getId()).getService();
            for (AssoServiceFieldType assoServiceFieldType : query.getAssoServiceFieldType()) {
                String newComment = assoServiceFieldType.getFormalisteComment();
                assoServiceFieldType = assoServiceFieldTypeService
                        .getAssoServiceFieldType(assoServiceFieldType.getId());
                assoServiceFieldType.setFormalisteComment(newComment);
                assoServiceFieldType.setService(service);
                assoServiceFieldType.setIsMandatory(true);
                assoServiceFieldType = assoServiceFieldTypeService.addOrUpdateServiceFieldType(assoServiceFieldType);
            }
        }

        boolean isLastReminder = false;
        boolean toSend = false;
        if (query.getId() == null) {
            query.setFirstCustomerReminderDateTime(null);
            query.setSecondCustomerReminderDateTime(null);
            query.setThirdCustomerReminderDateTime(null);
            query.setCreatedDateTime(LocalDateTime.now());
            query.setEmployeeSentBy(employeeService.getCurrentEmployee());
            toSend = true;
        } else {
            if (query.getFirstCustomerReminderDateTime() == null) {
                if (isForcedReminder || query.getCreatedDateTime().isBefore(LocalDateTime.now().minusDays(10))) {
                    query.setFirstCustomerReminderDateTime(LocalDateTime.now());
                    toSend = true;
                }
            } else if (query.getSecondCustomerReminderDateTime() == null) {
                if (isForcedReminder
                        || query.getFirstCustomerReminderDateTime().isBefore(LocalDateTime.now().minusDays(10))) {
                    query.setSecondCustomerReminderDateTime(LocalDateTime.now());
                    toSend = true;
                }
            } else if (query.getThirdCustomerReminderDateTime() == null) {
                if (isForcedReminder
                        || query.getSecondCustomerReminderDateTime().isBefore(LocalDateTime.now().minusDays(10))) {
                    isLastReminder = true;
                    query.setThirdCustomerReminderDateTime(LocalDateTime.now());
                    toSend = true;
                }
            }
        }
        query.setService(service);
        addOrUpdateMissingAttachmentQuery(query);
        if (toSend && !isWaitingForAttachmentToUpload)
            mailHelper.sendMissingAttachmentQueryToCustomer(query, isLastReminder);

        return query;
    }

    @Override
    public MissingAttachmentQuery sendMissingAttachmentQueryWithUploadedFiles(MissingAttachmentQuery query)
            throws OsirisClientMessageException, OsirisException {
        mailHelper.sendMissingAttachmentQueryToCustomer(query, false);
        return query;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendRemindersToCustomerForMissingAttachmentQuery() throws OsirisException {
        List<MissingAttachmentQuery> missingAttachmentQueries = missingAttachmentQueryRepository
                .getMissingAttachmentQueriesForCustomerReminder(simpleProvisionStatusService
                        .getSimpleProvisionStatusByCode(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT)
                        .getId(),
                        formaliteStatusService.getFormaliteStatusByCode(FormaliteStatus.FORMALITE_WAITING_DOCUMENT)
                                .getId(),
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId());

        if (missingAttachmentQueries != null && missingAttachmentQueries.size() > 0) {
            for (MissingAttachmentQuery missingAttachmentQuery : missingAttachmentQueries) {
                batchService.declareNewBatch(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_MISSING_ATTACHMENT_QUERIES,
                        missingAttachmentQuery.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendReminderToCustomerForMissingAttachmentQuery(MissingAttachmentQuery query)
            throws OsirisException, OsirisClientMessageException {
        sendMissingAttachmentQueryToCustomer(query, false, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MissingAttachmentQuery sendMissingAttachmentQueryImmediatly(MissingAttachmentQuery query)
            throws OsirisException, OsirisClientMessageException {
        query = getMissingAttachmentQuery(query.getId());
        sendMissingAttachmentQueryToCustomer(query, true, false);
        return query;
    }

    @Override
    public void checkCompleteAttachmentListAndComment(AssoServiceDocument assoServiceDocument, Attachment attachment)
            throws OsirisException {
        if (assoServiceDocument != null && assoServiceDocument.getService().getMissingAttachmentQueries() != null
                && assoServiceDocument.getService().getMissingAttachmentQueries().size() > 0) {

            List<MissingAttachmentQuery> missingAttchmentQueries = assoServiceDocument.getService()
                    .getMissingAttachmentQueries();
            missingAttchmentQueries.sort(new Comparator<MissingAttachmentQuery>() {
                @Override
                public int compare(MissingAttachmentQuery o1, MissingAttachmentQuery o2) {
                    if (o1 == null && o2 != null)
                        return 1;
                    if (o1 != null && o2 == null)
                        return -1;
                    return o2.getCreatedDateTime().compareTo(o1.getCreatedDateTime());
                }
            });

            for (MissingAttachmentQuery missingAttachmentQuery : assoServiceDocument.getService()
                    .getMissingAttachmentQueries()) {
                if (missingAttachmentQuery.getSendToMe() == false) {
                    for (AssoServiceDocument assoServiceDocumentService : assoServiceDocument.getService()
                            .getAssoServiceDocuments())
                        for (AssoServiceDocument assoServiceDocumentMissingQuery : missingAttachmentQuery
                                .getAssoServiceDocument()) {
                            if (assoServiceDocumentService.getTypeDocument().getCode()
                                    .equals(assoServiceDocumentMissingQuery.getTypeDocument().getCode())) {
                                if (assoServiceDocumentMissingQuery.getAttachments() != null
                                        && assoServiceDocumentMissingQuery.getAttachments().size() > 0) {
                                    boolean isAtLeastOneAttachmentAvailable = false;
                                    for (Attachment attachmentDocument : assoServiceDocumentMissingQuery
                                            .getAttachments()) {
                                        if (!attachmentDocument.getIsDisabled()) {
                                            isAtLeastOneAttachmentAvailable = true;
                                            break;
                                        }
                                    }
                                    if (!isAtLeastOneAttachmentAvailable && !attachment.getTypeDocument().getCode()
                                            .equals(assoServiceDocumentMissingQuery.getTypeDocument().getCode()))
                                        return;
                                } else if (!attachment.getTypeDocument().getCode()
                                        .equals(assoServiceDocumentMissingQuery.getTypeDocument().getCode()))
                                    return;
                            }
                        }

                    // After 'break' add comment
                    CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                            assoServiceDocument.getService().getAssoAffaireOrder().getCustomerOrder(),
                            "La demande de pièces manquantes du " + missingAttachmentQuery.getCreatedDateTime()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a été complétée",
                            false, false, false);

                    customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                            constantService.getActiveDirectoryGroupFormalites());
                    return;
                }
            }
        }
    }

}
