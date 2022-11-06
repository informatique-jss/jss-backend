package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormeJuridiqueRepository;

@Service
public class FormeJuridiqueServiceImpl implements FormeJuridiqueService {

    @Autowired
    FormeJuridiqueRepository FormeJuridiqueRepository;

    @Override
    @Cacheable(value = "formeJuridiqueList", key = "#root.methodName")
    public List<FormeJuridique> getFormeJuridique() {
        return IterableUtils.toList(FormeJuridiqueRepository.findAll());
    }
}
