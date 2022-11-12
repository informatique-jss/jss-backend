package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutPourLaFormaliteRepository;

@Service
public class StatutPourLaFormaliteServiceImpl implements StatutPourLaFormaliteService {

    @Autowired
    StatutPourLaFormaliteRepository StatutPourLaFormaliteRepository;

    @Override
    @Cacheable(value = "statutPourLaFormaliteList", key = "#root.methodName")
    public List<StatutPourLaFormalite> getStatutPourLaFormalite() {
        return IterableUtils.toList(StatutPourLaFormaliteRepository.findAll());
    }
}
