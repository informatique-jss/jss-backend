package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.repository.CivilityRepository;

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
        if (civility.isPresent())
            return civility.get();
        return null;
    }

    @Override
    public Civility addOrUpdateCivility(
            Civility civility) {
        return civilityRepository.save(civility);
    }
}
