package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.FormaliteStatus;

public interface FormaliteStatusService {
    public List<FormaliteStatus> getFormaliteStatus();

    public FormaliteStatus getFormaliteStatus(Integer id);

    public FormaliteStatus addOrUpdateFormaliteStatus(FormaliteStatus formaliteStatus);

    public FormaliteStatus getFormaliteStatusByCode(String code);
}
