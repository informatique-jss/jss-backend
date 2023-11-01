package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.DebourDel;
import com.jss.osiris.modules.quotation.model.Provision;

public interface DebourDelRepository extends QueryCacheCrudRepository<DebourDel, Integer> {

    List<DebourDel> findByProvision(Provision provision);
}