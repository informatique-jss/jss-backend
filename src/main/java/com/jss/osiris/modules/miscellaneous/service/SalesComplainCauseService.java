package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.SalesComplainCause;

public interface SalesComplainCauseService {

        public List<SalesComplainCause> getComplainCauses();

        public SalesComplainCause addOrUpdateComplainCause(SalesComplainCause salesComplainCause);
}
