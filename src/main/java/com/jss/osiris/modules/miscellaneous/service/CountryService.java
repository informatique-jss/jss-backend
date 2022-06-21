package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Country;

public interface CountryService {
    public List<Country> getCountries();

    public Country getCountry(Integer id);
}
