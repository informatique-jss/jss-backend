package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;

public interface DomiciliationStatusService {
    public List<DomiciliationStatus> getDomiciliationStatus();

    public DomiciliationStatus getDomiciliationStatus(Integer id);

    public DomiciliationStatus addOrUpdateDomiciliationStatus(DomiciliationStatus domiciliationStatus);

    public DomiciliationStatus getDomiciliationStatusByCode(String code);

    public void updateStatusReferential() throws OsirisException;
}
