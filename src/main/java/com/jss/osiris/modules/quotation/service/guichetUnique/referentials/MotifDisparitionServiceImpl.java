package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifDisparition;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifDisparitionRepository;

@Service
public class MotifDisparitionServiceImpl implements MotifDisparitionService {

    @Autowired
    MotifDisparitionRepository MotifDisparitionRepository;

    @Override
    public List<MotifDisparition> getMotifDisparition() {
        return IterableUtils.toList(MotifDisparitionRepository.findAll());
    }
}
