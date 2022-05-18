package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.repository.CivilityRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CivilityServiceImpl implements CivilityService {

    @Autowired
    CivilityRepository civilityRepository;

    @Override
    public List<Civility> getCivilities() {
        return IterableUtils.toList(civilityRepository.findAll());
    }

    @Override
    public Civility getCivility(Integer id) {
        Optional<Civility> civility = civilityRepository.findById(id);
        if (!civility.isEmpty())
            return civility.get();
        return null;
    }
}
