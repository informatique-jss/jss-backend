package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.TransfertFundsType;

public interface TransfertFundsTypeService {
    public List<TransfertFundsType> getTransfertFundsTypes();

    public TransfertFundsType getTransfertFundsType(Integer id);

    public TransfertFundsType addOrUpdateTransfertFundsType(TransfertFundsType transfertFundsType);
}
