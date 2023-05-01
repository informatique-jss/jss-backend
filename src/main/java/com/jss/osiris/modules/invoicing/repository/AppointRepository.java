package com.jss.osiris.modules.invoicing.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.invoicing.model.Appoint;

public interface AppointRepository extends QueryCacheCrudRepository<Appoint, Integer> {

    List<Appoint> findByLabelContainingIgnoreCase(String searchLabel);
}