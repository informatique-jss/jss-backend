package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeVoie;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeVoieRepository;

@Service
public class TypeVoieServiceImpl implements TypeVoieService {

    @Autowired
    TypeVoieRepository TypeVoieRepository;

    @Override
    @Cacheable(value = "typeVoieList", key = "#root.methodName")
    public List<TypeVoie> getTypeVoie() {
        return IterableUtils.toList(TypeVoieRepository.findAll());
    }
}
