package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;

public interface MissingAttachmentQueryService {
    public MissingAttachmentQuery getMissingAttachmentQuery(Integer id);

    public MissingAttachmentQuery addOrUpdateMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery);

    public void sendMissingAttachmentQueryToCustomer(MissingAttachmentQuery query)
            throws OsirisException, OsirisClientMessageException;

}
