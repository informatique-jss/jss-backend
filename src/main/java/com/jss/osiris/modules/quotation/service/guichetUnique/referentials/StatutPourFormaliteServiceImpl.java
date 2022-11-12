package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourFormalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutPourFormaliteRepository;

@Service
public class StatutPourFormaliteServiceImpl implements StatutPourFormaliteService {

    @Autowired
    StatutPourFormaliteRepository StatutPourFormaliteRepository;

    @Override
    @Cacheable(value = "statutPourFormaliteList", key = "#root.methodName")
    public List<StatutPourFormalite> getStatutPourFormalite() {
        return IterableUtils.toList(StatutPourFormaliteRepository.findAll());
    }
}
