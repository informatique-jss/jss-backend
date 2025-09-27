package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.modules.osiris.miscellaneous.model.City;

public interface CityService {
    public List<City> getCities();

    public City getCity(Integer id);

    public City addOrUpdateCity(City city);

    public List<City> getCitiesByPostalCode(String postalCode);

    public List<City> getCitiesByLabel(String label);

    public List<City> getCitiesByCountry(Integer countryId, String city, String postalCode);

    public List<City> getCitiesByCountryAndPostalCode(Integer countryId, String postalCode);

    public Page<City> getCitiesByLabelAndCountryAndPostalCode(String label, Integer countryId, String postalCode,
            Pageable pageable);

    public List<City> getCitiesByCountry(Integer countryId);

    public City getCityByInpiLabel(String inpiLabel);
}
