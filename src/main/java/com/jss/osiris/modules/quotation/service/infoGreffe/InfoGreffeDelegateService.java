package com.jss.osiris.modules.quotation.service.infoGreffe;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.IdentifiantFormalite;

public interface InfoGreffeDelegateService {
    public List<FormaliteInfogreffe> getAllInfogreffeFormalities(String competentAuthority) throws OsirisException;

    public FormaliteInfogreffe getInfogreffeFormalite(IdentifiantFormalite identifiantFormalite) throws OsirisException;
}
