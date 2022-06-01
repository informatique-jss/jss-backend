package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Responsable;

public interface ResponsableService {
    public List<Responsable> getResponsables();

    public Responsable getResponsable(Integer id);

    public List<Responsable> getResponsableByKeyword(String searchedValue);
}
