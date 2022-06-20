package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.TransfertFundsType;

public interface TransfertFundsTypeService {
    public List<TransfertFundsType> getTransfertFundsTypes();

    public TransfertFundsType getTransfertFundsType(Integer id);
}
