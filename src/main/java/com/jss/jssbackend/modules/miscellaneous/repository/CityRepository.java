package com.jss.jssbackend.modules.miscellaneous.repository;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Country;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CityRepository extends CrudRepository<City, Integer> {

    List<City> findByPostalCodeContaining(String postalCode);

    @Query("Select c from City c where c.country=:country and lower(c.label) like lower(concat('%', :city,'%')) ")
    List<City> findCitiesByCountryAndCity(@Param("city") String city, @Param("country") Country country);

    List<City> findByLabelContainingIgnoreCase(String label);
}