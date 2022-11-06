package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonneBlocPreneurBail;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypePersonneBlocPreneurBailRepository;

@Service
public class TypePersonneBlocPreneurBailServiceImpl implements TypePersonneBlocPreneurBailService {

    @Autowired
    TypePersonneBlocPreneurBailRepository TypePersonneBlocPreneurBailRepository;

    @Override
    @Cacheable(value = "typePersonneBlocPreneurBailList", key = "#root.methodName")
    public List<TypePersonneBlocPreneurBail> getTypePersonneBlocPreneurBail() {
        return IterableUtils.toList(TypePersonneBlocPreneurBailRepository.findAll());
    }
}
