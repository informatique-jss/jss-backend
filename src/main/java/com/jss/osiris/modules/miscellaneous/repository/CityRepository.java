package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;

import jakarta.persistence.QueryHint;

public interface CityRepository extends QueryCacheCrudRepository<City, Integer> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<City> findByPostalCodeContaining(String postalCode);

    @Query("Select c from City c where c.country=:country and lower(c.label) like lower(concat('%', :city,'%')) ")
    List<City> findCitiesByCountryAndCity(@Param("city") String city, @Param("country") Country country);

    List<City> findByLabelContainingIgnoreCase(String label);
}