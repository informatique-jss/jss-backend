package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;

public interface BodaccDelegateService {
    public List<BodaccNotice> getBodaccAfterDate(LocalDate createdAfter);
}
