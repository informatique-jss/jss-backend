package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Country;

public interface CountryService {
    public List<Country> getCountries();

    public Country getCountry(Integer id);
}
