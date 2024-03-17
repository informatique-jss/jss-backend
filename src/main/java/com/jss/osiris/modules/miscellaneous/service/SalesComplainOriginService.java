package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.SalesComplainOrigin;

public interface SalesComplainOriginService {

        public List<SalesComplainOrigin> getComplainOrigins();

        public SalesComplainOrigin addOrUpdateComplainOrigin(SalesComplainOrigin salesComplainOrigin);
}
