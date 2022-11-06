package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DestinationLocationGeranceMand;
import com.jss.osiris.modules.quotation.repository.guichetUnique.DestinationLocationGeranceMandRepository;

@Service
public class DestinationLocationGeranceMandServiceImpl implements DestinationLocationGeranceMandService {

    @Autowired
    DestinationLocationGeranceMandRepository DestinationLocationGeranceMandRepository;

    @Override
    @Cacheable(value = "destinationLocationGeranceMandList", key = "#root.methodName")
    public List<DestinationLocationGeranceMand> getDestinationLocationGeranceMand() {
        return IterableUtils.toList(DestinationLocationGeranceMandRepository.findAll());
    }
}
