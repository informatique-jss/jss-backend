package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.quotation.model.Service;
import com.jss.osiris.modules.quotation.repository.MissingAttachmentQueryRepository;

@org.springframework.stereotype.Service
public class MissingAttachmentQueryServiceImpl implements MissingAttachmentQueryService {

    @Autowired
    MissingAttachmentQueryRepository missingAttachmentQueryRepository;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    AssoServiceDocumentService assoServiceDocumentService;

    @Override
    public MissingAttachmentQuery getMissingAttachmentQuery(Integer id) {
        Optional<MissingAttachmentQuery> missingAttachmentQuery = missingAttachmentQueryRepository.findById(id);
        if (missingAttachmentQuery.isPresent())
            return missingAttachmentQuery.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MissingAttachmentQuery addOrUpdateMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery) {
        return missingAttachmentQueryRepository.save(missingAttachmentQuery);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query)
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

        query.setCreatedDateTime(LocalDateTime.now());
        query.setService(service);
        addOrUpdateMissingAttachmentQuery(query);

        mailHelper.sendMissingAttachmentQueryToCustomer(query);
    }

}
