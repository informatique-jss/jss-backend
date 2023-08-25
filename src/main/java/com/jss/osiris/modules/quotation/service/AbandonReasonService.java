package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.AbandonReason;

public interface AbandonReasonService {

    public List<AbandonReason> getAbandonReasons();

    public AbandonReason getAbandonReason(Integer id);

    public AbandonReason addOrUpdateAbandonReason(AbandonReason abandonReason);

}
