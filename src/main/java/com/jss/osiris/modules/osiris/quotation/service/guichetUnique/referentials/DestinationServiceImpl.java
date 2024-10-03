package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.Destination;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.DestinationRepository;

@Service
public class DestinationServiceImpl implements DestinationService {

    @Autowired
    DestinationRepository DestinationRepository;

    @Override
    public List<Destination> getDestination() {
        return IterableUtils.toList(DestinationRepository.findAll());
    }
}
