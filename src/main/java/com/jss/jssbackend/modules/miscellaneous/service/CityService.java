package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.City;

public interface CityService {
    public List<City> getCities();

    public City getCity(Integer id);

    public List<City> getCitiesByPostalCode(String postalCode);

    public List<City> getCitiesByCountry(Integer countryId, String city);
}
