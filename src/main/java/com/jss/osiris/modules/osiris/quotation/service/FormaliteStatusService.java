package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;

public interface FormaliteStatusService {
    public List<FormaliteStatus> getFormaliteStatus();

    public FormaliteStatus getFormaliteStatus(Integer id);

    public FormaliteStatus addOrUpdateFormaliteStatus(FormaliteStatus formaliteStatus);

    public FormaliteStatus getFormaliteStatusByCode(String code);

    public void updateStatusReferential() throws OsirisException;
}
