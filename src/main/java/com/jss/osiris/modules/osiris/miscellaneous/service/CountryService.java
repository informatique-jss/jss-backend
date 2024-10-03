package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Country;

public interface CountryService {
    public List<Country> getCountries();

    public Country getCountry(Integer id);

    public Country addOrUpdateCountry(Country country);
}
