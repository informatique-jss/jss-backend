package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.TransfertFundsType;

public interface TransfertFundsTypeService {
    public List<TransfertFundsType> getTransfertFundsTypes();

    public TransfertFundsType getTransfertFundsType(Integer id);
}
