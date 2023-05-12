package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.BodaccStatus;

public interface BodaccStatusService {
    public List<BodaccStatus> getBodaccStatus();

    public BodaccStatus getBodaccStatus(Integer id);

    public BodaccStatus addOrUpdateBodaccStatus(BodaccStatus bodaccStatus);

    public BodaccStatus getBodaccStatusByCode(String code);

    public void updateBodaccStatusReferential() throws OsirisException;
}
