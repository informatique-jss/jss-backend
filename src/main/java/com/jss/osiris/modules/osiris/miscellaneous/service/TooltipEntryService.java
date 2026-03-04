package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.TooltipEntry;

public interface TooltipEntryService {
    public List<TooltipEntry> getTooltipEntries();

    public TooltipEntry getTooltipEntry(Integer id);

    public TooltipEntry addOrUpdateTooltipEntry(TooltipEntry tooltipEntry);
}
