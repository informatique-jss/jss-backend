package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatusRepository;

@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    StatusRepository StatusRepository;

    @Override
    public List<Status> getStatus() {
        return IterableUtils.toList(StatusRepository.findAll());
    }

    @Override
    public Status getStatus(String code) {
        Optional<Status> status = StatusRepository.findById(code);
        if (status.isPresent())
            return status.get();
        return null;
    }
}
