package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.quotation.repository.MissingAttachmentQueryRepository;

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
    SimpleProvisionStatusService simpleProvisionStatusService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Override
    public MissingAttachmentQuery getMissingAttachmentQuery(Integer id) {
        Optional<MissingAttachmentQuery> missingAttachmentQuery = missingAttachmentQueryRepository.findById(id);
        if (missingAttachmentQuery.isPresent())
            return missingAttachmentQuery.get();
        return null;
    }

    @Override
    public List<MissingAttachmentQuery> getMissingAttachmentQueriesByIdService(Integer idService) {
        List<MissingAttachmentQuery> missingAttachmentQueries = missingAttachmentQueryRepository
                .findMissingAttachmentQueriesByIdService(idService);
        if (missingAttachmentQueries != null && missingAttachmentQueries.size() > 0)
            return missingAttachmentQueries;
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MissingAttachmentQuery addOrUpdateMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery) {
        return missingAttachmentQueryRepository.save(missingAttachmentQuery);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MissingAttachmentQuery sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query,
            boolean isForcedReminder)
            throws OsirisException, OsirisClientMessageException {
        Service service = assoServiceDocumentService
                .getAssoServiceDocument(query.getAssoServiceDocument().get(0).getId()).getService();

        for (AssoServiceDocument assoServiceDocument : query.getAssoServiceDocument()) {
            String newComment = assoServiceDocument.getFormalisteComment();
            assoServiceDocument = assoServiceDocumentService.getAssoServiceDocument(assoServiceDocument.getId());
            assoServiceDocument.setFormalisteComment(newComment);
            assoServiceDocument.setService(service);
            assoServiceDocument = assoServiceDocumentService.addOrUpdateAssoServiceDocument(assoServiceDocument);
        }

        boolean isLastReminder = false;
        boolean toSend = false;
        if (query.getId() == null) {
            query.setFirstCustomerReminderDateTime(null);
            query.setSecondCustomerReminderDateTime(null);
            query.setThirdCustomerReminderDateTime(null);
            query.setCreatedDateTime(LocalDateTime.now());
            toSend = true;
        } else {
            if (query.getFirstCustomerReminderDateTime() == null
                    && (isForcedReminder || query.getCreatedDateTime().isBefore(LocalDateTime.now().minusDays(10)))) {
                query.setFirstCustomerReminderDateTime(LocalDateTime.now());
                toSend = true;
            } else if (query.getSecondCustomerReminderDateTime() == null && (isForcedReminder
                    || query.getFirstCustomerReminderDateTime().isBefore(LocalDateTime.now().minusDays(10)))) {
                query.setSecondCustomerReminderDateTime(LocalDateTime.now());
                toSend = true;
            } else if (query.getThirdCustomerReminderDateTime() == null && (isForcedReminder
                    || query.getSecondCustomerReminderDateTime().isBefore(LocalDateTime.now().minusDays(10)))) {
                isLastReminder = true;
                query.setThirdCustomerReminderDateTime(LocalDateTime.now());
                toSend = true;
            }
        }
        query.setService(service);
        addOrUpdateMissingAttachmentQuery(query);

        if (toSend)
            mailHelper.sendMissingAttachmentQueryToCustomer(query, isLastReminder);

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
                                .getId());

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
        sendMissingAttachmentQueryToCustomer(query, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MissingAttachmentQuery sendMissingAttachmentQueryImmediatly(MissingAttachmentQuery query)
            throws OsirisException, OsirisClientMessageException {
        query = getMissingAttachmentQuery(query.getId());
        sendMissingAttachmentQueryToCustomer(query, true);
        return query;
    }

}
