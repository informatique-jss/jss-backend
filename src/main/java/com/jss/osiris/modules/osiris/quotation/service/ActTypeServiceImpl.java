package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.ActType;
import com.jss.osiris.modules.osiris.quotation.repository.ActTypeRepository;

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
    @Transactional(rollbackFor = Exception.class)
    public ActType addOrUpdateActType(ActType actType) {
        return actTypeRepository.save(actType);
    }
}
