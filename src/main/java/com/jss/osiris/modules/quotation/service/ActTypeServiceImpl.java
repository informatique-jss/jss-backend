package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.repository.ActTypeRepository;

@Service
public class ActTypeServiceImpl implements ActTypeService {

    @Autowired
    ActTypeRepository actTypeRepository;

    @Override
    public List<ActType> getActTypes() {
        return IterableUtils.toList(actTypeRepository.findAll());
    }

    @Override
    public ActType getActType(Integer id) {
        Optional<ActType> actType = actTypeRepository.findById(id);
        if (actType.isPresent())
            return actType.get();
        return null;
    }

    @Override
    public ActType addOrUpdateActType(ActType actType) {
        return actTypeRepository.save(actType);
    }
}
