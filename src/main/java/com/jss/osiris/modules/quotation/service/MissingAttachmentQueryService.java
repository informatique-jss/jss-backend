package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.quotation.model.AssoServiceDocument;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;

public interface MissingAttachmentQueryService {
        public MissingAttachmentQuery getMissingAttachmentQuery(Integer id);

        public List<MissingAttachmentQuery> getMissingAttachmentQueriesByIdService(Integer idService);

        public MissingAttachmentQuery addOrUpdateMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery);

        public MissingAttachmentQuery sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query,
                        boolean isForcedReminder)
                        throws OsirisException, OsirisClientMessageException;

        public void sendRemindersToCustomerForMissingAttachmentQuery() throws OsirisException;

        public void sendReminderToCustomerForMissingAttachmentQuery(MissingAttachmentQuery query)
                        throws OsirisException, OsirisClientMessageException;

        public MissingAttachmentQuery sendMissingAttachmentQueryImmediatly(MissingAttachmentQuery query)
                        throws OsirisException, OsirisClientMessageException;

        public void checkCompleteAttachmentListAndComment(AssoServiceDocument assoServiceDocument,
                        Attachment attachment)
                        throws OsirisException;
}
