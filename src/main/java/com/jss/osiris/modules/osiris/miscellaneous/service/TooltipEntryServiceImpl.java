package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.miscellaneous.model.TooltipEntry;
import com.jss.osiris.modules.osiris.miscellaneous.repository.TooltipEntryRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TooltipEntryServiceImpl implements TooltipEntryService {

    @Autowired
    TooltipEntryRepository tooltipEntryRepository;

    @Override
    public List<TooltipEntry> getTooltipEntries() {
        return IterableUtils.toList(tooltipEntryRepository.findAll());
    }

    @Override
    public TooltipEntry getTooltipEntry(Integer id) {
        Optional<TooltipEntry> tooltipEntry = tooltipEntryRepository.findById(id);
        if (tooltipEntry.isPresent())
            return tooltipEntry.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TooltipEntry addOrUpdateTooltipEntry(
            TooltipEntry tooltipEntry) {
        return tooltipEntryRepository.save(tooltipEntry);
    }
}
