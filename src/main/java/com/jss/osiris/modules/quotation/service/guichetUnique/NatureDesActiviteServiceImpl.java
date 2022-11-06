package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureDesActivite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.NatureDesActiviteRepository;

@Service
public class NatureDesActiviteServiceImpl implements NatureDesActiviteService {

    @Autowired
    NatureDesActiviteRepository NatureDesActiviteRepository;

    @Override
    @Cacheable(value = "natureDesActiviteList", key = "#root.methodName")
    public List<NatureDesActivite> getNatureDesActivite() {
        return IterableUtils.toList(NatureDesActiviteRepository.findAll());
    }
}
