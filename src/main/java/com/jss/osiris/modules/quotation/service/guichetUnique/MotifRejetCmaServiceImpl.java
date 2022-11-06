package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifRejetCma;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifRejetCmaRepository;

@Service
public class MotifRejetCmaServiceImpl implements MotifRejetCmaService {

    @Autowired
    MotifRejetCmaRepository MotifRejetCmaRepository;

    @Override
    @Cacheable(value = "motifRejetCmaList", key = "#root.methodName")
    public List<MotifRejetCma> getMotifRejetCma() {
        return IterableUtils.toList(MotifRejetCmaRepository.findAll());
    }
}
