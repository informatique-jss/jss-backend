package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifRejetMsa;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifRejetMsaRepository;

@Service
public class MotifRejetMsaServiceImpl implements MotifRejetMsaService {

    @Autowired
    MotifRejetMsaRepository MotifRejetMsaRepository;

    @Override
    @Cacheable(value = "motifRejetMsaList", key = "#root.methodName")
    public List<MotifRejetMsa> getMotifRejetMsa() {
        return IterableUtils.toList(MotifRejetMsaRepository.findAll());
    }
}
