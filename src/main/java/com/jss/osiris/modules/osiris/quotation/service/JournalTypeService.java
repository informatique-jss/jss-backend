package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.JournalType;

public interface JournalTypeService {
    public List<JournalType> getJournalTypes();

    public JournalType getJournalType(Integer id);

    public JournalType addOrUpdateJournalType(JournalType journalType);
}
