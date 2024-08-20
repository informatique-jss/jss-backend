package com.jss.osiris.modules.quotation.service.infoGreffe;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;

public interface FormaliteInfogreffeService {

    public void refreshAllFormaliteInfogreffe(Boolean isRefreshOnlyToday) throws OsirisException;

    public void refreshFormaliteInfogreffeDetail(FormaliteInfogreffe formaliteInfogreffe) throws OsirisException;

    public FormaliteInfogreffe addOrUpdFormaliteInfogreffe(FormaliteInfogreffe formaliteInfogreffe);

    public FormaliteInfogreffe getFormaliteInfogreffe(Integer formaliteNumero);
}