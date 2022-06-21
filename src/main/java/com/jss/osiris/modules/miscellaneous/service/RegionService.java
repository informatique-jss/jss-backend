package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Region;

public interface RegionService {
    public List<Region> getRegions();

    public Region getRegion(Integer id);
}
