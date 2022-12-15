package com.jss.osiris.modules.tiers.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.tiers.model.Responsable;

public interface ResponsableRepository extends CrudRepository<Responsable, Integer> {

    Responsable findByLoginWeb(String loginWeb);
}