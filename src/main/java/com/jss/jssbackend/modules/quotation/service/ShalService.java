package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.Shal;

public interface ShalService {
    public List<Shal> getShals();

    public Shal getShal(Integer id);
}
