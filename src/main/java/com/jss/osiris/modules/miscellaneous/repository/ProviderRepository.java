package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.Provider;

public interface ProviderRepository extends CrudRepository<Provider, Integer> {
}