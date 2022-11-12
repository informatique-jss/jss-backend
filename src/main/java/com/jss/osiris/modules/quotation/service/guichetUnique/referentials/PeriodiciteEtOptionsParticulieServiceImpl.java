package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PeriodiciteEtOptionsParticulie;
import com.jss.osiris.modules.quotation.repository.guichetUnique.PeriodiciteEtOptionsParticulieRepository;

@Service
public class PeriodiciteEtOptionsParticulieServiceImpl implements PeriodiciteEtOptionsParticulieService {

    @Autowired
    PeriodiciteEtOptionsParticulieRepository PeriodiciteEtOptionsParticulieRepository;

    @Override
    @Cacheable(value = "periodiciteEtOptionsParticulieList", key = "#root.methodName")
    public List<PeriodiciteEtOptionsParticulie> getPeriodiciteEtOptionsParticulie() {
        return IterableUtils.toList(PeriodiciteEtOptionsParticulieRepository.findAll());
    }
}
