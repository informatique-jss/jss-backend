package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;

public interface SimpleProvisionStatusService {
    public List<SimpleProvisionStatus> getSimpleProvisionStatus();

    public SimpleProvisionStatus getSimpleProvisonStatus(Integer id);

    public SimpleProvisionStatus addOrUpdateSimpleProvisonStatus(SimpleProvisionStatus simpleProvisonStatus);

    public void updateStatusReferential() throws OsirisException;

    public SimpleProvisionStatus getSimpleProvisionStatusByCode(String code);
}
