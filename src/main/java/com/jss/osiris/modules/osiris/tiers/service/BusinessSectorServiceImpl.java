package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.tiers.model.BusinessSector;
import com.jss.osiris.modules.osiris.tiers.repository.BusinessSectorRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessSectorServiceImpl implements BusinessSectorService {

    @Autowired
    BusinessSectorRepository businessSectorRepository;

    @Override
    public List<BusinessSector> getBusinessSectors() {
        return IterableUtils.toList(businessSectorRepository.findAll());
    }

    @Override
    public BusinessSector getBusinessSector(Integer id) {
        Optional<BusinessSector> businessSector = businessSectorRepository.findById(id);
        if (businessSector.isPresent())
            return businessSector.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessSector addOrUpdateBusinessSector(
            BusinessSector businessSector) {
        return businessSectorRepository.save(businessSector);
    }
}
