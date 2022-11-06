package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ModalitesDeControle;
import com.jss.osiris.modules.quotation.repository.guichetUnique.ModalitesDeControleRepository;

@Service
public class ModalitesDeControleServiceImpl implements ModalitesDeControleService {

    @Autowired
    ModalitesDeControleRepository ModalitesDeControleRepository;

    @Override
    @Cacheable(value = "modalitesDeControleList", key = "#root.methodName")
    public List<ModalitesDeControle> getModalitesDeControle() {
        return IterableUtils.toList(ModalitesDeControleRepository.findAll());
    }
}
