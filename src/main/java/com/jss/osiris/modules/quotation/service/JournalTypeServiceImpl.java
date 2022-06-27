package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.repository.JournalTypeRepository;

@Service
public class JournalTypeServiceImpl implements JournalTypeService {

    @Autowired
    JournalTypeRepository journalTypeRepository;

    @Override
    public List<JournalType> getJournalTypes() {
        return IterableUtils.toList(journalTypeRepository.findAll());
    }

    @Override
    public JournalType getJournalType(Integer id) {
        Optional<JournalType> journalType = journalTypeRepository.findById(id);
        if (!journalType.isEmpty())
            return journalType.get();
        return null;
    }

    @Override
    public JournalType addOrUpdateJournalType(JournalType journalType) {
        return journalTypeRepository.save(journalType);
    }
}
