package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.DebourSearch;
import com.jss.osiris.modules.invoicing.model.DebourSearchResult;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;

public interface DebourService {
    public Debour getDebour(Integer id);

    public List<Debour> getDeboursForProvision(Provision provision);

    public void deleteDebour(Debour debour);

    public Debour addOrUpdateDebour(Debour debour);

    public List<DebourSearchResult> searchDebours(DebourSearch debourSearch) throws OsirisException;

    public void reindexDebours();

    public void setDebourAsAssociated(Debour debour);

    public List<Debour> findNonAssociatedDeboursForDateAndAmount(LocalDate date, Float amount) throws OsirisException;

    public void unassociateDebourFromInvoice(Debour debour) throws OsirisException;
}