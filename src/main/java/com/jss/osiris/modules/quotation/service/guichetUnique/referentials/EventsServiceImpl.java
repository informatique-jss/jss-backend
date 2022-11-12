package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Events;
import com.jss.osiris.modules.quotation.repository.guichetUnique.EventsRepository;

@Service
public class EventsServiceImpl implements EventsService {

    @Autowired
    EventsRepository EventsRepository;

    @Override
    @Cacheable(value = "eventsList", key = "#root.methodName")
    public List<Events> getEvents() {
        return IterableUtils.toList(EventsRepository.findAll());
    }
}
