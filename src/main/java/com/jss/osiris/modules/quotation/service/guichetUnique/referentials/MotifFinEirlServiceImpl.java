package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifFinEirl;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifFinEirlRepository;

@Service
public class MotifFinEirlServiceImpl implements MotifFinEirlService {

    @Autowired
    MotifFinEirlRepository MotifFinEirlRepository;

    @Override
    public List<MotifFinEirl> getMotifFinEirl() {
        return IterableUtils.toList(MotifFinEirlRepository.findAll());
    }
}
