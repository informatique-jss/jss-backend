package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.TiersDocumentType;
import com.jss.jssbackend.modules.tiers.repository.TiersDocumentTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersDocumentTypeServiceImpl implements TiersDocumentTypeService {

    @Autowired
    TiersDocumentTypeRepository tiersDocumentTypeRepository;

    @Override
    public List<TiersDocumentType> getTiersDocumentTypes() {
        return IterableUtils.toList(tiersDocumentTypeRepository.findAll());
    }

    @Override
    public TiersDocumentType getTiersDocumentType(Integer id) {
        Optional<TiersDocumentType> tiersDocumentType = tiersDocumentTypeRepository.findById(id);
        if (!tiersDocumentType.isEmpty())
            return tiersDocumentType.get();
        return null;
    }
}
