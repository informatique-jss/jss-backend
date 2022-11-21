package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.DomiciliationStatus;

public interface DomiciliationStatusService {
    public List<DomiciliationStatus> getDomiciliationStatus();

    public DomiciliationStatus getDomiciliationStatus(Integer id);

    public DomiciliationStatus addOrUpdateDomiciliationStatus(DomiciliationStatus domiciliationStatus);

    public DomiciliationStatus getDomiciliationStatusByCode(String code);

    public void updateStatusReferential();
}
