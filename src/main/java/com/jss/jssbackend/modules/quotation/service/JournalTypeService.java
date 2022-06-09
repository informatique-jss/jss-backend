package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.JournalType;

public interface JournalTypeService {
    public List<JournalType> getJournalTypes();

    public JournalType getJournalType(Integer id);
}
