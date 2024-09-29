package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.IPaperSetResult;
import com.jss.osiris.modules.quotation.model.PaperSet;

public interface PaperSetService {
    public List<PaperSet> getPaperSets();

    public PaperSet getPaperSet(Integer id);

    public PaperSet addOrUpdatePaperSet(PaperSet paperSet);

    public List<IPaperSetResult> searchPaperSets(String textSearch, Boolean isDisplayValidated,
            Boolean isDisplayCancelled);

    public PaperSet cancelPaperSet(PaperSet paperSet) throws OsirisException;

    public PaperSet validatePaperSet(PaperSet paperSet) throws OsirisException;
}
