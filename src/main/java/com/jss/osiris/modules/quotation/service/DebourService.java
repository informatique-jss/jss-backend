package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.DebourSearch;
import com.jss.osiris.modules.invoicing.model.DebourSearchResult;
import com.jss.osiris.modules.quotation.model.Debour;

public interface DebourService {
    public Debour getDebour(Integer id);

    public Debour addOrUpdateDebour(Debour debour);

    public List<DebourSearchResult> searchDebours(DebourSearch debourSearch) throws OsirisException;

    public void reindexDebours();

    public void setDebourAsAssociated(Debour debour);
}
