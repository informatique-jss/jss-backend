package com.jss.osiris.modules.osiris.crm.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;

public interface KpiCrmCategoryRepository extends QueryCacheCrudRepository<KpiCrmCategory, Integer> {

    KpiCrmCategory findByCode(String code);

    List<KpiCrmCategory> findByOrderByLabel();
}