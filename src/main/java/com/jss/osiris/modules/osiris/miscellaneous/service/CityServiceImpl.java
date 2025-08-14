package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.repository.CityRepository;

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
        if (city.isPresent())
            return city.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public City addOrUpdateCity(
            City city) {
        return cityRepository.save(city);
    }

    @Override
    public List<City> getCitiesByPostalCode(String postalCode) {
        return IterableUtils.toList(cityRepository.findByPostalCodeContaining(postalCode));
    }

    @Override
    public List<City> getCitiesByLabel(String label) {
        return IterableUtils.toList(cityRepository.findByLabelContainingIgnoreCase(label));
    }

    @Override
    public List<City> getCitiesByCountry(Integer countryId, String city, String postalCode) {
        if (postalCode != null) {
            List<City> cities = getCitiesByPostalCode(postalCode);
            if (cities != null && cities.size() > 0)
                return cities;
        }
        if (city == null || city.length() <= 2)
            return null;

        if (countryId != null) {
            Country country = countryService.getCountry(countryId);
            if (country != null)
                return IterableUtils.toList(cityRepository.findCitiesByCountryAndCity(city, country));
        }
        return cityRepository.findByLabelContainingIgnoreCase(city);
    }

    @Override
    public List<City> getCitiesByCountryAndPostalCode(Integer countryId, String postalCode) {
        Country country = countryService.getCountry(countryId);
        return cityRepository.findByCountryAndPostalCode(country, postalCode);
    }

    @Override
    public Page<City> getCitiesByLabelAndCountryAndPostalCode(String label, Integer countryId, String postalCode,
            Pageable pageable) {
        Country country = countryService.getCountry(countryId);
        Page<City> cities = cityRepository.findByLabelContainingIgnoreCaseAndCountryAndPostalCodeContainingIgnoreCase(
                label, country,
                postalCode, pageable);

        if (cities.getNumberOfElements() > 0 || !label.contains("-"))
            return cities;

        return cityRepository.findByLabelContainingIgnoreCaseAndCountryAndPostalCodeContainingIgnoreCase(
                label.replaceAll("-", " "), country,
                postalCode, pageable);
    }

    @Override
    public List<City> getCitiesByCountry(Integer countryId) {
        Country country = countryService.getCountry(countryId);
        return cityRepository.findByCountry(country);
    }
}
