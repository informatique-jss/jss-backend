package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamation;

public interface SalesReclamationService {

        public List<SalesReclamation> getReclamations();

        public List<SalesReclamation> getReclamationsByTiersId(Integer idTiers);

        public SalesReclamation addOrUpdateReclamation(SalesReclamation salesReclamation);
}
