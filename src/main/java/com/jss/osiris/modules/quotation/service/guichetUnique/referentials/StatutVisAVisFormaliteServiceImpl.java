package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutVisAVisFormalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutVisAVisFormaliteRepository;

@Service
public class StatutVisAVisFormaliteServiceImpl implements StatutVisAVisFormaliteService {

    @Autowired
    StatutVisAVisFormaliteRepository StatutVisAVisFormaliteRepository;

    @Override
    @Cacheable(value = "statutVisAVisFormaliteList", key = "#root.methodName")
    public List<StatutVisAVisFormalite> getStatutVisAVisFormalite() {
        return IterableUtils.toList(StatutVisAVisFormaliteRepository.findAll());
    }
}
