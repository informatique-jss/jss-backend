package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.SalesComplainOrigin;

public interface SalesComplainOriginService {

        public List<SalesComplainOrigin> getComplainOrigins();

        public SalesComplainOrigin addOrUpdateComplainOrigin(SalesComplainOrigin salesComplainOrigin);
}
