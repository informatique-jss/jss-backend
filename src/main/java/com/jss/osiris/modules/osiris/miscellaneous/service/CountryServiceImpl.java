package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.repository.CountryRepository;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    CountryRepository countryRepository;

    @Override
    public List<Country> getCountries() {
        return IterableUtils.toList(countryRepository.findAll());
    }

    @Override
    public Country getCountry(Integer id) {
        Optional<Country> country = countryRepository.findById(id);
        if (country.isPresent())
            return country.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Country addOrUpdateCountry(
            Country country) {
        return countryRepository.save(country);
    }
}
