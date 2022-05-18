package com.jss.jssbackend.modules.clients.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.clients.model.TiersType;
import com.jss.jssbackend.modules.clients.repository.TiersTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersTypesServiceImpl implements TiersTypesService {

    @Autowired
    TiersTypeRepository tiersTypeRepository;

    @Override
    public List<TiersType> getTiersTypes() {
        return IterableUtils.toList(tiersTypeRepository.findAll());
    }

    @Override
    public TiersType getTiersType(Integer id) {
        Optional<TiersType> tiersType = tiersTypeRepository.findById(id);
        if (!tiersType.isEmpty())
            return tiersType.get();
        return null;
    }
}
