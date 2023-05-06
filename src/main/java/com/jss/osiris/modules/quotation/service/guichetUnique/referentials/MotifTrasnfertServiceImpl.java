package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifTrasnfert;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifTrasnfertRepository;

@Service
public class MotifTrasnfertServiceImpl implements MotifTrasnfertService {

    @Autowired
    MotifTrasnfertRepository MotifTrasnfertRepository;

    @Override
    public List<MotifTrasnfert> getMotifTrasnfert() {
        return IterableUtils.toList(MotifTrasnfertRepository.findAll());
    }
}
