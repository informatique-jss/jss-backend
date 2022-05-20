package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Region;

public interface RegionService {
    public List<Region> getRegions();

    public Region getRegion(Integer id);
}
