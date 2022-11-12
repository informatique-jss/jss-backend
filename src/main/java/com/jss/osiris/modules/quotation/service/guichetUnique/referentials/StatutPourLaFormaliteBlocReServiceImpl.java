package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourLaFormaliteBlocRe;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutPourLaFormaliteBlocReRepository;

@Service
public class StatutPourLaFormaliteBlocReServiceImpl implements StatutPourLaFormaliteBlocReService {

    @Autowired
    StatutPourLaFormaliteBlocReRepository StatutPourLaFormaliteBlocReRepository;

    @Override
    @Cacheable(value = "statutPourLaFormaliteBlocReList", key = "#root.methodName")
    public List<StatutPourLaFormaliteBlocRe> getStatutPourLaFormaliteBlocRe() {
        return IterableUtils.toList(StatutPourLaFormaliteBlocReRepository.findAll());
    }
}
