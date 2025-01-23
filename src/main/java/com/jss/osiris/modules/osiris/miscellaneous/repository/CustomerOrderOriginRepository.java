package com.jss.osiris.modules.osiris.miscellaneous.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;

public interface CustomerOrderOriginRepository extends QueryCacheCrudRepository<CustomerOrderOrigin, Integer> {

    List<CustomerOrderOrigin> findByUsername(String username);
}