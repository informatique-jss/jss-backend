package com.jss.jssbackend.modules.tiers.repository;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Tiers;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TiersRepository extends CrudRepository<Tiers, Integer> {

    @Query(value = "select a from Tiers a where id = :idTiers and isIndividual = true")
    List<Tiers> findByIsIndividualAndIdTiers(@Param("idTiers") Integer idTiers);

}