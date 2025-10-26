package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;

public interface BaloDelegateService {
    public List<BaloNotice> getBaloAfterDate(LocalDate createdAfter);
}
