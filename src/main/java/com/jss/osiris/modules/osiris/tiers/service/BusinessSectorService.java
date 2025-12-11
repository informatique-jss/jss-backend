package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.BusinessSector;

public interface BusinessSectorService {
    public List<BusinessSector> getBusinessSectors();

    public BusinessSector getBusinessSector(Integer id);

    public BusinessSector addOrUpdateBusinessSector(BusinessSector businessSector);
}
