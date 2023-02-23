package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Affaire;

public interface AffaireRepository extends CrudRepository<Affaire, Integer> {

    Affaire findBySiret(String siret);

    List<Affaire> findBySiren(String siret);
}