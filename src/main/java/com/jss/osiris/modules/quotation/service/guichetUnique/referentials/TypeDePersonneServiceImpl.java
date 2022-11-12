package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDePersonne;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeDePersonneRepository;

@Service
public class TypeDePersonneServiceImpl implements TypeDePersonneService {

    @Autowired
    TypeDePersonneRepository TypeDePersonneRepository;

    @Override
    @Cacheable(value = "typeDePersonneList", key = "#root.methodName")
    public List<TypeDePersonne> getTypeDePersonne() {
        return IterableUtils.toList(TypeDePersonneRepository.findAll());
    }
}
