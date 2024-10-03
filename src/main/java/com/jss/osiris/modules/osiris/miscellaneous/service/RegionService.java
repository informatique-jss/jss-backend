package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Region;

public interface RegionService {
    public List<Region> getRegions();

    public Region getRegion(Integer id);

    public Region addOrUpdateRegion(Region region);
}
