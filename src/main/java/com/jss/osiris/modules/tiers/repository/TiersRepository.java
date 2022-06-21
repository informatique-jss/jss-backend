package com.jss.osiris.modules.tiers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.tiers.model.Tiers;

public interface TiersRepository extends CrudRepository<Tiers, Integer> {

    @Query(value = "select a from Tiers a where id = :idTiers and isIndividual = true")
    List<Tiers> findByIsIndividualAndIdTiers(@Param("idTiers") Integer idTiers);

}