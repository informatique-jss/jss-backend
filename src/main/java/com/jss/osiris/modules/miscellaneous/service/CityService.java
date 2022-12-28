package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.City;

public interface CityService {
    public List<City> getCities();

    public City getCity(Integer id);

    public City addOrUpdateCity(City city);

    public List<City> getCitiesByPostalCode(String postalCode);

    public List<City> getCitiesByCountry(Integer countryId, String city, String postalCode);
}
