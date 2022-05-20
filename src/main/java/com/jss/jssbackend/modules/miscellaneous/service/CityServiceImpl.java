package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.repository.CityRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    CityRepository cityRepository;

    @Autowired
    CountryService countryService;

    @Override
    public List<City> getCities() {
        return IterableUtils.toList(cityRepository.findAll());
    }

    @Override
    public City getCity(Integer id) {
        Optional<City> city = cityRepository.findById(id);
        if (!city.isEmpty())
            return city.get();
        return null;
    }

    @Override
    public List<City> getCitiesByPostalCode(String postalCode) {
        return IterableUtils.toList(cityRepository.findByPostalCodeContaining(postalCode));
    }

    @Override
    public List<City> getCitiesByCountry(Integer countryId, String city) {
        if (countryId != null) {
            Country country = countryService.getCountry(countryId);
            if (country != null)
                return IterableUtils.toList(cityRepository.findCitiesByCountryAndCity(city, country));
        }
        return cityRepository.findByLabelContainingIgnoreCase(city);
    }

}
