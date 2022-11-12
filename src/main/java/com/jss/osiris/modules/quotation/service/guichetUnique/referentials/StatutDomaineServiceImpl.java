package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutDomaine;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutDomaineRepository;

@Service
public class StatutDomaineServiceImpl implements StatutDomaineService {

    @Autowired
    StatutDomaineRepository StatutDomaineRepository;

    @Override
    @Cacheable(value = "statutDomaineList", key = "#root.methodName")
    public List<StatutDomaine> getStatutDomaine() {
        return IterableUtils.toList(StatutDomaineRepository.findAll());
    }
}
