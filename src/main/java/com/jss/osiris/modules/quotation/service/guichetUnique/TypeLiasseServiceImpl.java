package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeLiasse;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeLiasseRepository;

@Service
public class TypeLiasseServiceImpl implements TypeLiasseService {

    @Autowired
    TypeLiasseRepository TypeLiasseRepository;

    @Override
    @Cacheable(value = "typeLiasseList", key = "#root.methodName")
    public List<TypeLiasse> getTypeLiasse() {
        return IterableUtils.toList(TypeLiasseRepository.findAll());
    }
}
