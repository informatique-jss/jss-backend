package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.JeuneAgriculteur;
import com.jss.osiris.modules.quotation.repository.guichetUnique.JeuneAgriculteurRepository;

@Service
public class JeuneAgriculteurServiceImpl implements JeuneAgriculteurService {

    @Autowired
    JeuneAgriculteurRepository JeuneAgriculteurRepository;

    @Override
    @Cacheable(value = "jeuneAgriculteurList", key = "#root.methodName")
    public List<JeuneAgriculteur> getJeuneAgriculteur() {
        return IterableUtils.toList(JeuneAgriculteurRepository.findAll());
    }
}
