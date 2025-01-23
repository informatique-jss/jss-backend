package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.FundType;

public interface FundTypeService {
    public List<FundType> getFundTypes();

    public FundType getFundType(Integer id);

    public FundType addOrUpdateFundType(FundType fundType);
}
