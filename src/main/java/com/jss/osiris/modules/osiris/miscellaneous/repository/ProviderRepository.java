package com.jss.osiris.modules.osiris.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;

public interface ProviderRepository extends QueryCacheCrudRepository<Provider, Integer> {

    @Query(nativeQuery = true, value = "select * from provider where upper(label) like concat('%',trim(upper(:value)),'%')")
    List<Provider> findByValue(@Param("value") String value);

}