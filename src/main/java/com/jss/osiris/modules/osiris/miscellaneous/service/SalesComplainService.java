package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.SalesComplain;

public interface SalesComplainService {

        public List<SalesComplain> getComplains();

        public List<SalesComplain> getComplainsByTiersId(Integer idTiers);

        public SalesComplain addOrUpdateComplain(SalesComplain salesComplain);
}
