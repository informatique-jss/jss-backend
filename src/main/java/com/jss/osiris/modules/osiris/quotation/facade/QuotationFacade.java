package com.jss.osiris.modules.osiris.quotation.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.InfogreffeKbisService;

@Service
public class QuotationFacade {

    @Autowired
    InfogreffeKbisService infogreffeKbisService;

    @Autowired
    ProvisionService provisionService;

    @Transactional(rollbackFor = Exception.class)
    public KbisRequest orderNewKbisForSiret(String siret, Integer provisionId) throws OsirisException {
        Provision provision = provisionService.getProvision(provisionId);
        return infogreffeKbisService.orderNewKbisForSiret(siret, provision);
    }

    @Transactional(rollbackFor = Exception.class)
    public Attachment getUpToDateKbisForSiret(String siret) throws OsirisException {
        return infogreffeKbisService.getUpToDateKbisForSiret(siret);
    }

}
