package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;

public interface MissingAttachmentQueryService {
        public MissingAttachmentQuery getMissingAttachmentQuery(Integer id);

        public List<MissingAttachmentQuery> getMissingAttachmentQueriesByIdService(Integer idService);

        public MissingAttachmentQuery addOrUpdateMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery)
                        throws OsirisException;

        public MissingAttachmentQuery sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query,
                        boolean isForcedReminder, Boolean isWaitingForAttachmentToUpload)
                        throws OsirisException, OsirisClientMessageException;

        public MissingAttachmentQuery sendMissingAttachmentQueryWithUploadedFiles(MissingAttachmentQuery query)
                        throws OsirisClientMessageException, OsirisException;

        public void sendRemindersToCustomerForMissingAttachmentQuery() throws OsirisException;

        public void sendReminderToCustomerForMissingAttachmentQuery(MissingAttachmentQuery query)
                        throws OsirisException, OsirisClientMessageException;

        public MissingAttachmentQuery sendMissingAttachmentQueryImmediatly(MissingAttachmentQuery query)
                        throws OsirisException, OsirisClientMessageException;

        public void checkCompleteAttachmentListAndComment(AssoServiceDocument assoServiceDocument,
                        Attachment attachment)
                        throws OsirisException;
}
