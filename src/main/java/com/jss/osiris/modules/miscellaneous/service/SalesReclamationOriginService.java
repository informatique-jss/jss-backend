package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamationOrigin;

public interface SalesReclamationOriginService {

        public List<SalesReclamationOrigin> getReclamationOrigins();

        public SalesReclamationOrigin addOrUpdateReclamationOrigin(SalesReclamationOrigin salesReclamationOrigin);
}
