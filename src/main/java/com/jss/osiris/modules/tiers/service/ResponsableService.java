package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.Responsable;

public interface ResponsableService {
    public List<Responsable> getResponsables();

    public Responsable getResponsable(Integer id);

    public List<Responsable> getResponsableByKeyword(String searchedValue);

    public void reindexResponsable();
}
