package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegistreEirl;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RegistreEirlRepository;

@Service
public class RegistreEirlServiceImpl implements RegistreEirlService {

    @Autowired
    RegistreEirlRepository RegistreEirlRepository;

    @Override
    @Cacheable(value = "registreEirlList", key = "#root.methodName")
    public List<RegistreEirl> getRegistreEirl() {
        return IterableUtils.toList(RegistreEirlRepository.findAll());
    }
}
