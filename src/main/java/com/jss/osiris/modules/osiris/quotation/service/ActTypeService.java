package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.ActType;

public interface ActTypeService {
    public List<ActType> getActTypes();

    public ActType getActType(Integer id);

    public ActType addOrUpdateActType(ActType actType);
}
