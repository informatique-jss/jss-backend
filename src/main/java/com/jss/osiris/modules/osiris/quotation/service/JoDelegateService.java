package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.JoNotice;

public interface JoDelegateService {
    public List<JoNotice> getJoAfterDate(LocalDate createdAfter);
}
