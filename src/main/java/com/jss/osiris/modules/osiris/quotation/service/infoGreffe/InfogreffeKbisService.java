package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;

public interface InfogreffeKbisService {

    void orderKbis(Integer entityId) throws OsirisException;

    KbisRequest getRequest(Integer id);

    KbisRequest addOrUpdateKbisRequest(KbisRequest kbisRequest);

    Attachment getUpToDateKbisForSiret(String siren) throws OsirisException;

    KbisRequest orderNewKbisForSiret(String siren, Provision provision) throws OsirisException;

}
