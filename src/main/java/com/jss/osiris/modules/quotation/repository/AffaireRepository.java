package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.Affaire;

public interface AffaireRepository extends QueryCacheCrudRepository<Affaire, Integer> {

    Affaire findBySiret(String siret);

    List<Affaire> findBySiren(String siret);
}