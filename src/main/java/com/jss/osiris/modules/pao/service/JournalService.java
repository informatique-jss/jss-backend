package com.jss.osiris.modules.pao.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.pao.model.Journal;

public interface JournalService {
    public List<Journal> getJournals();

    public Journal getJournal(Integer id);

    public Journal addOrUpdateJournal(Journal journal) throws OsirisException, OsirisClientMessageException;
}
