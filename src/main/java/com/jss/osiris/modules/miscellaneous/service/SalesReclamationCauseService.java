package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamationCause;

public interface SalesReclamationCauseService {

        public List<SalesReclamationCause> getReclamationCauses();

        public SalesReclamationCause addOrUpdateReclamationCause(SalesReclamationCause salesReclamationCause);
}
