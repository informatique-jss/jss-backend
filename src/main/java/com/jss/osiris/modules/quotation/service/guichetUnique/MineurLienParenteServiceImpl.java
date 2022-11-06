package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MineurLienParente;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MineurLienParenteRepository;

@Service
public class MineurLienParenteServiceImpl implements MineurLienParenteService {

    @Autowired
    MineurLienParenteRepository MineurLienParenteRepository;

    @Override
    @Cacheable(value = "mineurLienParenteList", key = "#root.methodName")
    public List<MineurLienParente> getMineurLienParente() {
        return IterableUtils.toList(MineurLienParenteRepository.findAll());
    }
}
