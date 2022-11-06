package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeFormaliteRepository;

@Service
public class TypeFormaliteServiceImpl implements TypeFormaliteService {

    @Autowired
    TypeFormaliteRepository TypeFormaliteRepository;

    @Override
    @Cacheable(value = "typeFormaliteList", key = "#root.methodName")
    public List<TypeFormalite> getTypeFormalite() {
        return IterableUtils.toList(TypeFormaliteRepository.findAll());
    }
}
