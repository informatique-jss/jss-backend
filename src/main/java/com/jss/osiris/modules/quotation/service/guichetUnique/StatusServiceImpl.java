package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatusRepository;

@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    StatusRepository StatusRepository;

    @Override
    @Cacheable(value = "statusList", key = "#root.methodName")
    public List<Status> getStatus() {
        return IterableUtils.toList(StatusRepository.findAll());
    }
}
