package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Region;
import com.jss.osiris.modules.miscellaneous.repository.RegionRepository;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    RegionRepository regionRepository;

    @Override
    public List<Region> getRegions() {
        return IterableUtils.toList(regionRepository.findAll());
    }

    @Override
    public Region getRegion(Integer id) {
        Optional<Region> region = regionRepository.findById(id);
        if (!region.isEmpty())
            return region.get();
        return null;
    }
}
