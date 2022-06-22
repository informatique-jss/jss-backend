package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.FundType;

public interface FundTypeService {
    public List<FundType> getFundTypes();

    public FundType getFundType(Integer id);
}
