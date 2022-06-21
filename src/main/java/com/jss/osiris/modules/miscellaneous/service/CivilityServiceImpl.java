package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Civility2;
import com.jss.osiris.modules.miscellaneous.repository.CivilityRepository;

@Service
public class CivilityServiceImpl implements CivilityService {

    @Autowired
    CivilityRepository civilityRepository;

    @Override
    public List<Civility2> getCivilities() {
        return IterableUtils.toList(civilityRepository.findAll());
    }

    @Override
    public Civility2 getCivility(Integer id) {
        Optional<Civility2> civility = civilityRepository.findById(id);
        if (!civility.isEmpty())
            return civility.get();
        return null;
    }
}
