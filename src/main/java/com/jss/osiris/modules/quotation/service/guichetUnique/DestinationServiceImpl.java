package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Destination;
import com.jss.osiris.modules.quotation.repository.guichetUnique.DestinationRepository;

@Service
public class DestinationServiceImpl implements DestinationService {

    @Autowired
    DestinationRepository DestinationRepository;

    @Override
    @Cacheable(value = "destinationList", key = "#root.methodName")
    public List<Destination> getDestination() {
        return IterableUtils.toList(DestinationRepository.findAll());
    }
}
