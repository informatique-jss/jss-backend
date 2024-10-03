package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import java.io.InputStream;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.DocumentAssocieInfogreffe;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.FormaliteInfogreffe;

public interface InfogreffeDelegateService {
    public List<FormaliteInfogreffe> getAllInfogreffeFormalities() throws OsirisException;

    public FormaliteInfogreffe getInfogreffeFormalite(FormaliteInfogreffe formaliteInfogreffe) throws OsirisException;

    public InputStream getAttachmentFileFromEvenementInfogreffe(
            DocumentAssocieInfogreffe documentAssocieInfogreffe)
            throws OsirisException;
}
