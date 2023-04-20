package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;

public interface StatusService {
    public List<Status> getStatus();

    public Status getStatus(String code);
}
