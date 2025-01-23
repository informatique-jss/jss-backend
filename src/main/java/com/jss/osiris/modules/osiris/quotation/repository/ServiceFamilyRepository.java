package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;

public interface ServiceFamilyRepository extends QueryCacheCrudRepository<ServiceFamily, Integer> {

    List<ServiceFamily> findByServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup);
}