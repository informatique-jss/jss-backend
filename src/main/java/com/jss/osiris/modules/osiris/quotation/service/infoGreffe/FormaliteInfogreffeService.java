package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.EvenementInfogreffe;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.FormaliteInfogreffe;

public interface FormaliteInfogreffeService {

    public void refreshAllFormaliteInfogreffe(Boolean isRefreshOnlyToday) throws OsirisException;

    public void refreshFormaliteInfogreffeDetail(FormaliteInfogreffe formaliteInfogreffe) throws OsirisException;

    public FormaliteInfogreffe addOrUpdateFormaliteInfogreffe(FormaliteInfogreffe formaliteInfogreffe);

    public FormaliteInfogreffe getFormaliteInfogreffe(Integer formaliteNumero);

    public List<FormaliteInfogreffe> getFormaliteInfogreffeByReference(String value);

    public EvenementInfogreffe getLastEvenementInfogreffe(FormaliteInfogreffe formaliteInfogreffe,
            boolean onlyNonNullStatus);

}