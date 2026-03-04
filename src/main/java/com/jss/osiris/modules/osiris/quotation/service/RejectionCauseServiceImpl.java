package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.quotation.model.RejectionCause;
import com.jss.osiris.modules.osiris.quotation.repository.RejectionCauseRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejectionCauseServiceImpl implements RejectionCauseService {

    @Autowired
    RejectionCauseRepository rejectionCauseRepository;

    @Override
    public List<RejectionCause> getRejectionCauses() {
        return IterableUtils.toList(rejectionCauseRepository.findAll());
    }

    @Override
    public RejectionCause getRejectionCause(Integer id) {
        Optional<RejectionCause> rejectionCause = rejectionCauseRepository.findById(id);
        if (rejectionCause.isPresent())
            return rejectionCause.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RejectionCause addOrUpdateRejectionCause(
            RejectionCause rejectionCause) {
        return rejectionCauseRepository.save(rejectionCause);
    }
}
