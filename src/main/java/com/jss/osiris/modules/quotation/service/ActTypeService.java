package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.ActType;

public interface ActTypeService {
    public List<ActType> getActTypes();

    public ActType getActType(Integer id);
}
